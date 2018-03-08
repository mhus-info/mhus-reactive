package de.mhus.cherry.reactive.engine;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.InactiveStartPoint;
import de.mhus.cherry.reactive.model.activity.RElement;
import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.activity.StartPoint;
import de.mhus.cherry.reactive.model.activity.Swimlane;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.engine.EngineElement;
import de.mhus.cherry.reactive.model.engine.EnginePool;
import de.mhus.cherry.reactive.model.engine.EngineProcess;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.cherry.reactive.model.engine.ProcessProvider;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MSystem;
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
	
	public class ProcessContainer implements EngineProcess {

		private ProcessLoader loader;
		private String canonicalName;
		private Class<? extends Process> processClass;
		private ProcessDescription processDescription;
		private String processName;
		private HashMap<String, EnginePool> pools = new HashMap<>();
		private HashMap<String, EngineElement> elements = new HashMap<>();

		@SuppressWarnings("unchecked")
		public ProcessContainer(ProcessLoader loader) throws MException {
			this.loader = loader;
			// iterate all elements
			for (Class<? extends RElement<?>> element : loader.getElements()) {
				
				// find the process description
				if (!element.isInterface() && de.mhus.cherry.reactive.model.activity.RProcess.class.isAssignableFrom(element)) {
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
				if (!element.isInterface() && RElement.class.isAssignableFrom(element)) {
					try {
						ElementContainer act = new ElementContainer((Class<? extends Activity<?>>) element);
						if (elements.containsKey(act.getCanonicalName()))
							throw new MException("Multiple activity with the same name",act.getCanonicalName() ); // should not happen
						elements.put(act.getCanonicalName(), act);
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
			
			// init pools
			for (EnginePool pool : pools.values())
				((PoolContainer)pool).collectElements(elements);
			
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
		public List<Class<? extends RElement<?>>> getElements() {
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
		public Set<String> getPoolNames() {
			return pools.keySet();
		}
		
		@Override
		public EngineElement getElement(String name) {
			return elements.get(name);
		}
		
		@Override
		public Set<String> getElementNames() {
			return elements.keySet();
		}
				
	}
	
	public class PoolContainer  implements EnginePool {

		private Class<? extends Pool<?>> pool;
		private HashMap<String, EngineElement> poolElements = new HashMap<>();
		private PoolDescription poolDescription;
		private String name;

		public PoolContainer(Class<? extends Pool<?>> pool) throws MException {
			this.pool = pool;
			poolDescription = pool.getAnnotation(PoolDescription.class);
			if (poolDescription == null)
				throw new MException("Pool without description annotation found",pool);
			name = poolDescription.name().length() == 0 ? pool.getName() : poolDescription.name();
		}

		public void collectElements(HashMap<String, EngineElement> elements) {
			for (EngineElement element : elements.values()) {
				Class<? extends RElement<?>> clazz = element.getElementClass();
				String elementPool = MSystem.getTemplateCanonicalName(clazz, 0);
				if (pool.getCanonicalName().equals(elementPool))
						poolElements.put(element.getCanonicalName(),element);
			}
		}

		@Override
		public String getCanonicalName() {
			return name;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public List<EngineElement> getStartPoints() {
			LinkedList<EngineElement> out = new LinkedList<>();
			for (EngineElement element : poolElements.values()) {
				Class<? extends RElement<?>> clazz = element.getElementClass();
				if (element.is(StartPoint.class) && !InactiveStartPoint.class.isAssignableFrom(clazz))
					out.add(element);
			}
			return out;
		}
		
		@Override
		public Class<? extends Pool<?>> getPoolClass() {
			return pool;
		}
				
		@Override
		public EngineElement getElement(String name) {
			return poolElements.get(name);
		}
		
		@Override
		public Set<String> getElementNames() {
			return poolElements.keySet();
		}

		@Override
		public List<EngineElement> getElements(Class<? extends RElement<?>> ifc) {
			LinkedList<EngineElement> out = new LinkedList<>();
			for (EngineElement element : poolElements.values())
				if (ifc.isAssignableFrom(element.getElementClass()))
					out.add(element);
			return out;
		}
		
		@Override
		public List<EngineElement> getOutputElements(EngineElement element) {
			LinkedList<EngineElement> out = new LinkedList<>();
			for (Class<? extends Activity<?>> output : element.getOutputs()) {
				EngineElement o = getElement(output.getCanonicalName());
				if (o != null)
					out.add(o);
			}
			return out;
		}


	}
	
	public class ElementContainer implements EngineElement {

		private Class<? extends RElement<?>> element;
		private String name;
		private ActivityDescription actDescription;
		
		public ElementContainer(Class<? extends RElement<?>> element) throws MException {
			this.element = element;
			if (Activity.class.isAssignableFrom(element)) {
				actDescription = element.getAnnotation(ActivityDescription.class);
				if (actDescription == null) throw new MException("Activity without description annotation",element);
			}
			name = element.getCanonicalName();
		}

		@Override
		public String getCanonicalName() {
			return name;
		}

		@Override
		public Class<? extends RElement<?>> getElementClass() {
			return element;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean is(Class<? extends RElement> ifc) {
			if (ifc == null) return false;
			return ifc.isAssignableFrom(element);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Activity<?>>[] getOutputs() {
			if (actDescription == null) return new Class[0];
			return actDescription.outputs();
		}

		@Override
		public Trigger[] getTriggers() {
			if (	actDescription == null ||
					actDescription.triggers().length == 0 || 
					actDescription.triggers().length == 1 && actDescription.triggers()[0].type() == TYPE.NOOP)
				return new Trigger[0];
			return actDescription.triggers();
		}

		@Override
		public Class<? extends Swimlane<?>> getSwiminglane() {
			if (actDescription == null) return null;
			return actDescription.lane();
		}
		
	}

	public Set<String> getProcessNames() {
		return processes.keySet();
	}
}
