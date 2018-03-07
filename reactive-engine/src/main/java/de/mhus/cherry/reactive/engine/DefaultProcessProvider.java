package de.mhus.cherry.reactive.engine;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.Element;
import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.activity.StartPoint;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;
import de.mhus.cherry.reactive.model.engine.EngineActivity;
import de.mhus.cherry.reactive.model.engine.EnginePool;
import de.mhus.cherry.reactive.model.engine.EngineProcess;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.cherry.reactive.model.engine.ProcessProvider;
import de.mhus.lib.core.MLog;
import de.mhus.lib.errors.MException;

public class DefaultProcessProvider extends MLog implements ProcessProvider {

	protected HashMap<String, ProcessContainer> processes = new HashMap<>();
	protected LinkedList<String> warnings = new LinkedList<>();
	
	public void addProcess(ProcessLoader loader) throws MException {
		ProcessContainer container = new ProcessContainer(loader);
		if (processes.containsKey(container.getCanonicalName()))
			throw new MException("Process already defined",container.getCanonicalName());
		processes.put(container.getCanonicalName(), container);

	}
	
	public EngineProcess getProcess(String name) {
		return processes.get(name);
	}
	
	private class ProcessContainer implements EngineProcess {

		private ProcessLoader loader;
		private String canonicalName;
		private Class<? extends Process> processClass;
		private ProcessDescription processDescription;
		private String processName;
		private HashMap<String, EnginePool> pools = new HashMap<>();
		private HashMap<String, EngineActivity> activities = new HashMap<>();

		@SuppressWarnings("unchecked")
		public ProcessContainer(ProcessLoader loader) throws MException {
			this.loader = loader;
			// iterate all elements
			for (Class<? extends Element> element : loader.getElements()) {
				
				// find the process description
				if (!element.isInterface() && Process.class.isAssignableFrom(element)) {
					if (processClass != null)
						throw new MException("Multipe process definition classes found",processClass,element);
					processClass = (Class<? extends Process>) element;
				}
				
				// find the pool descriptions
				if (!element.isInterface() && Pool.class.isAssignableFrom(element)) {
					try {
						PoolContainer pool = new PoolContainer((Class<? extends Pool<?>>) element);
						if (pools.containsKey(pool.getCanonicalName()))
							throw new MException("Multiple pools with the same name",pool.getCanonicalName() );
						pools.put(pool.getCanonicalName(), pool);
					} catch (Throwable t) {
						log().w(element,t);
						warnings.add("Pool " + element.getCanonicalName() + ": " + t.getMessage());
					}
				}
				
				// find all activities
				if (!element.isInterface() && Activity.class.isAssignableFrom(element)) {
					try {
						ActivityContainer act = new ActivityContainer((Class<? extends Activity<?>>) element);
						if (activities.containsKey(act.getCanonicalName()))
							throw new MException("Multiple activity with the same name",act.getCanonicalName() );
						activities.put(act.getCanonicalName(), act);
					} catch (Throwable t) {
						log().w(element,t);
						warnings.add("Pool " + element.getCanonicalName() + ": " + t.getMessage());
					}
				}
				
			}
			if (processClass == null)
				throw new MException("process definition class not found");
			processDescription = processClass.getAnnotation(ProcessDescription.class);
			if (processDescription == null)
				throw new MException("process definition annotation not found");
			processName = processDescription.name().length() == 0 ? processClass.getName() : processDescription.name();
			canonicalName = processName + ":" + processDescription.version();
		}

		public String getCanonicalName() {
			return canonicalName;
		}
		
		@Override
		public String getProcessName() {
			return processName;
		}
		
		@Override
		public String getVersion() {
			return processDescription.version();
		}
		
		@Override
		public List<Class<? extends Element>> getElements() {
			return loader.getElements();
		}
		
		@Override
		public String toString() {
			return canonicalName;
		}
		
		@Override
		public EnginePool getPool(String name) {
			return pools.get(name);
		}
		
		@Override
		public EngineActivity getActivity(String name) {
			return activities.get(name);
		}

		@Override
		public Set<String> getPoolNames() {
			return pools.keySet();
		}
		
		@Override
		public Set<String> getActivityNames() {
			return activities.keySet();
		}
		
		public void getElement(EnginePool pool, Class<? extends Element> type ) {
			LinkedList<> out = new LinkedList<>();
			String poolType = pool.getElementClass().getCanonicalName();
			for (Class<? extends Element> element : loader.getElements()) {
				Type mySuperclass = element.getGenericSuperclass();
				Type tType = ((ParameterizedType)mySuperclass).getActualTypeArguments()[0];
				String templName = tType.getTypeName();
				if (poolType.equals(templName) && type.isAssignableFrom(element)) {
					xxx
				}
//				String className = tType.toString().split(" ")[1];
//				Class clazz = Class.forName(className);
			}
		}
		
	}
	
	private class PoolContainer  implements EnginePool {

		private Class<? extends Pool<?>> pool;
		private PoolDescription poolDescription;
		private String name;

		public PoolContainer(Class<? extends Pool<?>> pool) throws MException {
			this.pool = pool;
			poolDescription = pool.getAnnotation(PoolDescription.class);
			if (poolDescription == null)
				throw new MException("Pool without description annotation found",pool);
			name = poolDescription.name().length() == 0 ? pool.getName() : poolDescription.name();
		}

		public String getCanonicalName() {
			return name;
		}
		
		@Override
		public Class<? extends StartPoint<?>>[] getStartPoints() {
			return poolDescription.startPoints();
		}
		
		@Override
		public Class<? extends Pool<?>> getElementClass() {
			return pool;
		}
				
	}
	
	private class ActivityContainer implements EngineActivity {

		private Class<? extends Activity<?>> act;
		private String name;

		public ActivityContainer(Class<? extends Activity<?>> act) throws MException {
			this.act = act;
			ActivityDescription actDescription = act.getAnnotation(ActivityDescription.class);
			if (actDescription == null) throw new MException("Activity without description annotation",act);
			name = act.getCanonicalName();
		}

		public String getCanonicalName() {
			return name;
		}
		
	}
}
