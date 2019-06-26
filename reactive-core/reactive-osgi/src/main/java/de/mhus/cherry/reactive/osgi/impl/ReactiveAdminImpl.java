/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.engine.EngineConfiguration;
import de.mhus.cherry.reactive.engine.util.DefaultProcessLoader;
import de.mhus.cherry.reactive.engine.util.DefaultProcessProvider;
import de.mhus.cherry.reactive.engine.util.EngineListenerUtil;
import de.mhus.cherry.reactive.engine.util.PoolValidator;
import de.mhus.cherry.reactive.engine.util.PoolValidator.Finding;
import de.mhus.cherry.reactive.engine.util.PoolValidator.LEVEL;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;
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
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.MThread;
import de.mhus.lib.core.cfg.CfgString;
import de.mhus.lib.core.config.IConfig;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.MRuntimeException;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.sql.DataSourceProvider;
import de.mhus.lib.sql.DefaultDbPool;
import de.mhus.lib.sql.Dialect;

//TODO renew datasource reference from time to time ... 

@Component(immediate=true)
public class ReactiveAdminImpl extends MLog implements ReactiveAdmin {

	public ReactiveAdminImpl instance;
	private EngineConfiguration config;
	private Engine engine;
	private BundleContext context;
	private static CfgString storageDsName = new CfgString(ReactiveAdmin.class, "storageDsName", "reactive-storage");
	private DataSource storageDataSource;
	private DataSourceProvider storageDsProvider;
	private DefaultDbPool storagePool;
	private static CfgString archiveDsName = new CfgString(ReactiveAdmin.class, "archiveDsName", "reactive-archive");
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
	private static CfgString engineLogLevel = new CfgString(ReactiveAdmin.class, "logLevel", "DEBUG");
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
		StringBuilder names = new StringBuilder();
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
		// find process
		if (autoDeploy) {
			ProcessDescription desc = findProcessDescription(loader, canonicalName);
			if (isProcessActivated(canonicalName))
				try {
					deploy(canonicalName, false, false);
					return true;
				} catch (MException e) {
					log().e(canonicalName,e);
				}
			else
			if (desc != null && desc.autoDeploy())
				try {
					deploy(canonicalName, false, true);
					return true;
				} catch (MException e) {
					log().e(canonicalName,e);
				}
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
	
	private ProcessDescription findProcessDescription(ProcessLoader loader, String name) {
		ProcessInfo info = null;
		synchronized (availableProcesses) {
			info = availableProcesses.get(name);
		}
		if (info == null) return null;
		for (Class<? extends AElement<?>> elem : loader.getElements())
			if (AProcess.class.isAssignableFrom(elem)) {
				return elem.getAnnotation(ProcessDescription.class);
			}
		return null;
	}
	
	@Override
	public List<Finding> deploy(String name, boolean addVersion, boolean activate) throws MException {
		startEngine();
		log().i("deploy",name);
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
		info.time = System.currentTimeMillis();
		
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
	public long getProcessDeployTime(String name) {
		synchronized (availableProcesses) {
			ProcessInfo info = availableProcesses.get(name);
			if (info == null) return 0;
			return info.time;
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
		info.deployedName = null;
	}
	
	private class ProcessInfo {
		ProcessLoader loader;
		String info;
		String deployedName;
		String canonicalName;
		long time = 0;
		
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
		// try to start engine
		new MThread(new Runnable() {
		    @Override
            public void run() {
		        while (true) {
    		        try {
    		            startEngine();
    		            activate();
    		            return;
    		        } catch (Throwable t) {
    		            log().i("Can't start engine",t.toString());
    		        }
    		        MThread.sleep(10000);
		        }
		    }
		}).start();
	}
	
	private void activate() {
		processTracker = new ServiceTracker<>(context, AProcess.class, new ServiceTrackerCustomizer<AProcess, AProcess>() {

			@Override
			public AProcess addingService(ServiceReference<AProcess> reference) {
			    try {
    				AProcess process = context.getService(reference);
    				OsgiProcessLoader loader = new OsgiProcessLoader(process);
    				addProcess(reference.getBundle().getSymbolicName() + ":" + process.getClass().getSimpleName(), loader);
			    } catch (Throwable t) {
			        log().e(reference, t);
			    }
				return null;
			}

			@Override
			public void modifiedService(ServiceReference<AProcess> reference, AProcess service) {
			    try {
    				OsgiProcessLoader loader = new OsgiProcessLoader(service);
    				removeProcess(loader.getProcessCanonicalName());
    				addProcess(reference.getBundle().getSymbolicName() + ":" + service.getClass().getSimpleName(), loader);
                } catch (Throwable t) {
                    log().e(reference, t);
                }
			}

			@Override
			public void removedService(ServiceReference<AProcess> reference, AProcess service) {
			    try {
			        OsgiProcessLoader loader = new OsgiProcessLoader(service);
			        removeProcess(loader.getProcessCanonicalName());
                } catch (Throwable t) {
                    log().e(reference, t);
                }
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
		int cnt = e.processNodes();
		e = engine;
		if (e == null) return 0;
		if (System.currentTimeMillis() > nextCleanup) {
			nextCleanup = System.currentTimeMillis() + MPeriod.MINUTE_IN_MILLISECOUNDS / 2;
			e.cleanupCases();
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

				@Override
				public boolean validatePassword(String user, String pass) {
					if (aaaAdminMode) return true;
					if (aaaProvider != null) 
						return aaaProvider.validatePassword(user, pass);
					return aaaDefaultAccess;
				}

				@Override
				public boolean isUserActive(String user) {
					if (aaaAdminMode) return true;
					if (aaaProvider != null) 
						return aaaProvider.isUserActive(user);
					return aaaDefaultAccess;
				}

				@Override
				public boolean hasUserGeneralActorAccess(String uri, String canonicalName, String user) {
					//TODO check customized access
					if (aaaProvider != null) 
						return aaaProvider.hasUserGeneralActorAccess(uri, canonicalName, user);
					return false;
				}
			};
			// parameters
			config.parameters = new HashMap<>();
			// TODO default config?
			
			// process provider
			config.processProvider = new DefaultProcessProvider();
			
			// listener
			if (engineLogLevel.value().equals("INFO"))
			    config.listener.add(EngineListenerUtil.createLogInfoListener());
            if (engineLogLevel.value().equals("DEBUG"))
                config.listener.add(EngineListenerUtil.createLogDebugListener());
			
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
		try {
			String driver = storageDataSource.getConnection().getMetaData().getDriverName();
			Dialect dialect = Dialect.findDialect(driver);
			storageDsProvider.setDialect(dialect);
		} catch (Exception e) {
			log().e(e);
		}
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

	@Override
	public ProcessLoader getProcessLoader(String name) throws NotFoundException {
		synchronized (availableProcesses) {
			ProcessInfo info = availableProcesses.get(name);
			if (info == null) throw new NotFoundException("process unknown",name);
			return info.loader;
		}
	}

}
