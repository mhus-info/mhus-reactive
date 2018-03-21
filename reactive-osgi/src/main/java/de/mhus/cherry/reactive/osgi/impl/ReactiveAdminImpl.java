package de.mhus.cherry.reactive.osgi.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import de.mhus.cherry.reactive.engine.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineConfiguration;
import de.mhus.cherry.reactive.engine.EngineListenerUtil;
import de.mhus.cherry.reactive.engine.EngineUtil;
import de.mhus.cherry.reactive.engine.PoolValidator;
import de.mhus.cherry.reactive.engine.PoolValidator.Finding;
import de.mhus.cherry.reactive.engine.PoolValidator.LEVEL;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.engine.AaaProvider;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.PEngine;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;
import de.mhus.cherry.reactive.util.engine.MemoryStorage;
import de.mhus.cherry.reactive.util.engine.SqlDbStorage;
import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.cfg.CfgString;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.MRuntimeException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.sql.DataSourceProvider;
import de.mhus.lib.sql.DefaultDbPool;

//TODO renew datasource reference from time to time ... 

@Component(immediate=true)
public class ReactiveAdminImpl extends MLog implements ReactiveAdmin {

	public ReactiveAdminImpl instance;
	private EngineConfiguration config;
	private Engine engine;
	private BundleContext context;
	private CfgString storageDsName = new CfgString(ReactiveAdmin.class, "storageDsName", "reactive-storage");
	private DataSource storageDataSource;
	private DataSourceProvider storageDsProvider;
	private DefaultDbPool storagePool;
	private CfgString archiveDsName = new CfgString(ReactiveAdmin.class, "archiveDsName", "reactive-archive");
	private DataSource archiveDataSource;
	private DataSourceProvider archiveDsProvider;
	private DefaultDbPool archivePool;
	
	private AaaProvider aaaProvider;
	protected boolean aaaAdminMode = false;
	protected boolean aaaDefaultAccess = true;
	private ServiceTracker<AProcess,AProcess> processTracker;
	private TreeMap<String, ProcessInfo> availableProcesses = new TreeMap<>();
	private boolean autoDeploy = true;
	private Thread executor;
	private long nextCleanup;
	private boolean executionSuspended = false;
	private boolean stopExecutor = false;
	
	// --- Process list handling
	
	@Override
	public void removeProcess(String canonicalName) {
		if (canonicalName == null) return;
		log().d("remove process",canonicalName);
		synchronized (availableProcesses) {
			availableProcesses.remove(canonicalName);
		}
	}

	@Override
	public String addProcess(String[] fileNames, boolean remember) throws FileNotFoundException {
		StringBuffer names = new StringBuffer();
		File[] files = new File[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			files[i] = new File(fileNames[i]);
			if (!files[i].exists()) throw new FileNotFoundException(fileNames[i]);
			if (names.length() != 0) names.append(',');
			names.append(fileNames[i]);
		}
		DefaultProcessLoader loader = new DefaultProcessLoader(files);
		addProcess(names.toString(),loader);
		if (remember)
			config.persistent.getParameters().put("osgi.process.path:" + loader.getProcessCanonicalName(), names.toString());
		return loader.getProcessCanonicalName();
	}
	
	public void forgetProcess(String name) {
		config.persistent.getParameters().remove("osgi.process.path:" + name);
		
	}
	
	@Override
	public boolean addProcess(String info, ProcessLoader loader) {
		if (info == null) {
			info = "";
		}
		String canonicalName = loader.getProcessCanonicalName();
		log().d("add process",info);
		synchronized (availableProcesses) {
			if (availableProcesses.put(canonicalName, new ProcessInfo(info, canonicalName, loader)) != null)
				log().w("Process was already present",canonicalName);
		}
		if (autoDeploy && isProcessActivated(canonicalName) )
			try {
				deploy(canonicalName, false, false);
				return true;
			} catch (MException e) {
				log().e(canonicalName,e);
			}
		return false;
	}

	private boolean isProcessActivated(String name) {
		startEngine();
		if (engine == null) return false;
		return config.persistent.isProcessEnabled(name);
	}
	
	@Override
	public Collection<String> getAvailableProcesses() {
		synchronized (availableProcesses) {
			return Collections.unmodifiableCollection(availableProcesses.keySet());
		}
	}
	
	@Override
	public List<Finding> deploy(String name, boolean addVersion, boolean activate) throws MException {
		startEngine();
		// get process
		ProcessInfo info = null;
		synchronized (availableProcesses) {
			info = availableProcesses.get(name);
		}
		if (info == null) 
			throw new MException("Process not found",name);
		if (info.deployedName != null) {
			log().w("Process already deployed, redeploy",name);
			undeploy(name);
		}
		
		info.deployedName = ((DefaultProcessProvider)config.processProvider).addProcess(info.loader);
		
		EProcess process = config.processProvider.getProcess(info.deployedName);
		boolean foundError = false;
		PoolValidator validator = null;
		for (String poolName : process.getPoolNames()) {
			log().i(">>> Pool", poolName);
			EPool pool = process.getPool(poolName);
			validator = new PoolValidator(pool);
			validator.validate();
			for (PoolValidator.Finding finding : validator.getFindings()) {
				if (finding.getLevel() == LEVEL.ERROR || finding.getLevel() == LEVEL.FATAL ) {
					log().e("***",finding);
					foundError = true;
				}
			}
		}
		if (foundError) {
			log().w("Found errors, undeploy process");
			((DefaultProcessProvider)config.processProvider).removeProcess(info.deployedName);
			return validator.getFindings();
		}
		
		// add version
		if (addVersion)
			config.persistent.enableProcessVersion(info.deployedName);

		if (activate)
			config.persistent.activateProcessVersion(info.deployedName);

		if (addVersion || activate)
			engine.saveEnginePersistence();
		
		return null;
	}
	
	@Override
	public String getProcessDeployName(String name) {
		synchronized (availableProcesses) {
			ProcessInfo info = availableProcesses.get(name);
			if (info == null) return null;
			return info.deployedName;
		}
	}
	
	@Override
	public String getProcessInfo(String name) {
		synchronized (availableProcesses) {
			ProcessInfo info = availableProcesses.get(name);
			if (info == null) return null;
			return info.info;
		}
	}
	
	@Override
	public void undeploy(String name) throws MException {
		startEngine();
		//TODO stop cases before ?
		ProcessInfo info = null;
		synchronized (availableProcesses) {
			info = availableProcesses.get(name);
		}
		if (info == null) 
			throw new MException("Process not found",name);
		if (info.deployedName == null)
			throw new MException("Process is not deployed",name);
		((DefaultProcessProvider)config.processProvider).removeProcess(info.deployedName);
	}
	
	private class ProcessInfo {
		ProcessLoader loader;
		String info;
		String deployedName;
		String canonicalName;
		
		public ProcessInfo(String info, String canonicalName, ProcessLoader loader) {
			this.info = info;
			this.canonicalName = canonicalName;
			this.loader = loader;
		}
		
		@Override
		public String toString() {
			return canonicalName;
		}
		
	}
	
	// --- Init
	
	@Activate
	public void doActivate(ComponentContext ctx) {
		instance = this;
		context = ctx.getBundleContext();
		processTracker = new ServiceTracker<>(context, AProcess.class, new ServiceTrackerCustomizer<AProcess, AProcess>() {

			@Override
			public AProcess addingService(ServiceReference<AProcess> reference) {
				AProcess process = context.getService(reference);
				addProcess(reference.getBundle().getSymbolicName() + ":" + process.getClass().getSimpleName(), new OsgiProcessLoader(process));
				return null;
			}

			@Override
			public void modifiedService(ServiceReference<AProcess> reference, AProcess service) {
				removeProcess(EngineUtil.getProcessCanonicalName(service));
				addProcess(reference.getBundle().getSymbolicName() + ":" + service.getClass().getSimpleName(), new OsgiProcessLoader(service));
			}

			@Override
			public void removedService(ServiceReference<AProcess> reference, AProcess service) {
				removeProcess(EngineUtil.getProcessCanonicalName(service));
			}
		});
		processTracker.open(true);
		
		executor = new Thread(new Runnable() {

			@Override
			public void run() {
				stopExecutor = false;
				log().i("Engine executor started");
				while (true) {
					if (stopExecutor) return;
					try {
						if (doExecuteStep() == 0)
							Thread.sleep(3000);
						else
							Thread.sleep(1000);
					} catch (Throwable t) {
						log().e(t);
						MThread.sleep(1000);
					}
				}
			}
			
		},"reactive-engine-executor");
		executor.setDaemon(true);
		executor.start();
	}
	
	protected int doExecuteStep() throws NotFoundException, IOException {
		if (executionSuspended ) return 0;
		Engine e = engine;
		if (e == null) return 0;
		int cnt = e.execute();
		e = engine;
		if (e == null) return 0;
		if (System.currentTimeMillis() > nextCleanup) {
			nextCleanup = System.currentTimeMillis() + MTimeInterval.MINUTE_IN_MILLISECOUNDS / 2;
			e.cleanup();
		}
		return cnt;
	}

	@Deactivate
	public void doDeactivate(ComponentContext ctx) {
		stopExecutor  = true;
		int cnt = 60;
		log().i("Wait for engine to stop");
		while (executor.isAlive() && cnt > 0) {
			MThread.sleep(1000);
			cnt--;
		}
		if (cnt == 0)
			log().w("Engine not stopped");
		
		instance = null;
		stopEngine();
		processTracker.close();
		processTracker = null;
	}
	
//	@Reference(unbind="unbindAaaProvider",optional=true)
	public void setAaaProvider(AaaProvider provider) {
		aaaProvider = provider;
	}
	
	public void unbindAaaProvider(AaaProvider provider) {
		aaaProvider = null;
	}
	
	@Override
	public synchronized void startEngine() {
		if (engine != null) return;
		try {
	 		// start engine
			config = new EngineConfiguration();
			// storage
			if (storageDsName.value().equals("*")) {
				log().w("Engine: Using memory storage");
				config.storage = new MemoryStorage();
				PEngine e = new PEngine();
				IConfig cfg = MApi.getCfg(ReactiveAdmin.class);
				if (cfg != null) {
					IConfig cfgEngine = cfg.getNode("engine");
					if (cfgEngine != null) {
						for (IConfig cfgPa : cfgEngine.getNodes("parameter")) {
							e.getParameters().put(cfgPa.getString("name"),cfgPa.getString("value"));
						}
					}
				}
				config.storage.saveEngine(e);
			} else {
				storageDsProvider = new DataSourceProvider();
				updateStorageDataSource();
				storagePool = new DefaultDbPool(storageDsProvider);
				config.storage = new SqlDbStorage(storagePool,"storage");
			}
			// archive
			if (archiveDsName.value().equals("*")) {
				log().w("Engine: Using memory archive");
				config.archive = new MemoryStorage();
			} else {
				archiveDsProvider = new DataSourceProvider();
				updateArchiveDataSource();
				archivePool = new DefaultDbPool(archiveDsProvider);
				config.archive = new SqlDbStorage(archivePool,"archive");
			}
			// aaa
			config.aaa = new AaaProvider() {
				
				@Override
				public boolean hasGroupAccess(String user, String group) {
					if (aaaAdminMode) return true;
					if (aaaProvider != null) 
						return aaaProvider.hasGroupAccess(user, group);
					return aaaDefaultAccess;
				}
				
				@Override
				public boolean hasAdminAccess(String user) {
					if (aaaAdminMode) return true;
					if (aaaProvider != null)
						return aaaProvider.hasAdminAccess(user);
					return aaaDefaultAccess;
				}
				
				@Override
				public String getCurrentUserId() {
					if (aaaProvider != null)
						return aaaProvider.getCurrentUserId();
					return "osgi";
				}
			};
			// parameters
			config.parameters = new HashMap<>();
			// TODO default config?
			
			// process provider
			config.processProvider = new DefaultProcessProvider();
			
			// listener
			config.listener = EngineListenerUtil.createLogInfoListener();
			
			engine = new Engine(config);
			
			// auto add process
			for (String key : config.persistent.getParameters().keySet()) {
				if (key.startsWith("osgi.process.path:")) {
					String name = key.substring(18);
					String[] fileNames = String.valueOf(config.persistent.getParameters().get(key)).split(",");
					File[] files = new File[fileNames.length];
					for (int i = 0; i < fileNames.length; i++) {
						files[i] = new File(fileNames[i]);
						if (!files[i].exists()) {
							files = null;
							break;
						}
					}
					if (files != null)
						addProcess(name, new DefaultProcessLoader(files));
				}
			}
			
			// auto deploy
			synchronized (availableProcesses) {
				for (ProcessInfo info : availableProcesses.values()) {
					if (autoDeploy && isProcessActivated(info.canonicalName) )
						try {
							deploy(info.canonicalName, false, false);
						} catch (MException e) {
							log().e(info.canonicalName,e);
						}
				}
			}
		} catch (Throwable t) {
			engine = null;
			config = null;
			throw new MRuntimeException(t);
		}
	}
	
	@Override
	public void stopEngine() {
		//TODO wait 
		config = null;
		engine = null;
		synchronized (availableProcesses) {
			availableProcesses.values().forEach(v -> v.deployedName = null);
		}
	}

	@Override
	public STATE_ENGINE getEngineState() {
		if (engine == null) return STATE_ENGINE.STOPPED;
		if (executionSuspended) return STATE_ENGINE.SUSPENDED;
		return STATE_ENGINE.RUNNING;
	}

	protected void updateStorageDataSource() throws InvalidSyntaxException, MException {
		Collection<ServiceReference<DataSource>> refs = context.getServiceReferences(DataSource.class, "(osgi.jndi.service.name="+storageDsName+")");
		if (refs.size() == 0) throw new MException("datasource not found",storageDsName);
		storageDataSource = context.getService(refs.iterator().next());
		storageDsProvider.setDataSource(storageDataSource);
	}

	protected void updateArchiveDataSource() throws InvalidSyntaxException, MException {
		Collection<ServiceReference<DataSource>> refs = context.getServiceReferences(DataSource.class, "(osgi.jndi.service.name="+archiveDsName+")");
		if (refs.size() == 0) throw new MException("datasource not found",archiveDsName);
		archiveDataSource = context.getService(refs.iterator().next());
		archiveDsProvider.setDataSource(archiveDataSource);
	}

	@Override
	public Engine getEngine() {
		startEngine();
		return engine;
	}

	@Override
	public PEngine getEnginePersistence() {
		return config.persistent;
	}
	
	@Override
	public void setExecutionSuspended(boolean suspend) {
		executionSuspended = suspend;
	}
	
	
}
