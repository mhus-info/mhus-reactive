/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.mhus.cherry.reactive.engine.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.activity.AStartPoint;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;
import de.mhus.cherry.reactive.model.annotations.Trigger;
import de.mhus.cherry.reactive.model.annotations.Trigger.TYPE;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.EPool;
import de.mhus.cherry.reactive.model.engine.EProcess;
import de.mhus.cherry.reactive.model.engine.ProcessLoader;
import de.mhus.cherry.reactive.model.engine.ProcessProvider;
import de.mhus.cherry.reactive.model.util.DefaultSwimlane;
import de.mhus.cherry.reactive.model.util.InactiveStartPoint;
import de.mhus.cherry.reactive.model.util.NobodyActor;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.errors.MException;

public class DefaultProcessProvider extends MLog implements ProcessProvider {

	protected HashMap<String, ProcessContainer> processes = new HashMap<>();
	protected LinkedList<String> warnings = new LinkedList<>();
	
	public String addProcess(ProcessLoader loader) throws MException {
		ProcessContainer container = new ProcessContainer(loader);
		String name = container.getCanonicalName() + ":" + container.getVersion();
		if (processes.containsKey(name))
			log().w("Process already defined, overwrite",container.getProcessName());
		processes.put(name, container);
		return name;
	}
	
	public void removeProcess(String name) {
		processes.remove(name);
	}
	
	@Override
	public EProcess getProcess(String name, String version) {
		return processes.get(name + ":" + version);
	}
	
	@Override
	public EProcess getProcess(String nameVerion) {
		return processes.get(nameVerion);
	}
	
	public class ProcessContainer implements EProcess {

		private ProcessLoader loader;
		private String canonicalName;
		private Class<? extends AProcess> processClass;
		private ProcessDescription processDescription;
		private String processName;
		private HashMap<String, EPool> pools = new HashMap<>();
		private HashMap<String, EElement> elements = new HashMap<>();
		private String name;

		@SuppressWarnings("unchecked")
		public ProcessContainer(ProcessLoader loader) throws MException {
			this.loader = loader;
			// iterate all elements
			for (Class<? extends AElement<?>> element : loader.getElements()) {
				
				// find the process description
				if (!element.isInterface() && de.mhus.cherry.reactive.model.activity.AProcess.class.isAssignableFrom(element)) {
					if (processClass != null)
						throw new MException("Multipe process definition classes found",processClass,element);
					processClass = (Class<? extends AProcess>) element;
				}
				
				// find the pool descriptions
				if (!element.isInterface() && APool.class.isAssignableFrom(element)) {
					try {
						PoolContainer pool = new PoolContainer((Class<? extends APool<?>>) element);
						if (pools.containsKey(pool.getCanonicalName()))
							throw new MException("Multiple pools with the same name",pool.getCanonicalName() );
						pool.setProcess(this);
						pools.put(pool.getCanonicalName(), pool);
					} catch (Throwable t) {
						log().w(element,t);
						warnings.add("Pool " + element.getCanonicalName() + ": " + t.getMessage());
					}
				}
				
				// find all activities
				if (!element.isInterface() && AElement.class.isAssignableFrom(element)) {
					try {
						ElementContainer act = new ElementContainer((Class<? extends AActivity<?>>) element);
						if (elements.containsKey(act.getCanonicalName()))
							throw new MException("Multiple activities with the same name",act.getCanonicalName() ); // should not happen
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
			processName = processClass.getCanonicalName();
			name = MString.isEmpty(processDescription.name()) ? processClass.getSimpleName() : processDescription.name();
			canonicalName = processClass.getCanonicalName() + ":" + processDescription.version();

			// init pools
			for (EPool pool : pools.values())
				((PoolContainer)pool).collectElements();
			
		}

		@Override
		public String getProcessName() {
			return canonicalName;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getCanonicalName() {
			return processName;
		}
		
		@Override
		public String getVersion() {
			return processDescription.version();
		}
		
		@Override
		public List<Class<? extends AElement<?>>> getElements() {
			return loader.getElements();
		}
		
		@Override
		public String toString() {
			return canonicalName;
		}
		
		@Override
		public EPool getPool(String name) {
			return pools.get(name);
		}
		
		@Override
		public Set<String> getPoolNames() {
			return pools.keySet();
		}
		
		@Override
		public EElement getElement(String name) {
			return elements.get(name);
		}
		
		@Override
		public Set<String> getElementNames() {
			return elements.keySet();
		}

		@Override
		public ProcessDescription getProcessDescription() {
			return processDescription;
		}

		@Override
		public Class<? extends AProcess> getProcessClass() {
			return processClass;
		}
						
	}
	
	public class PoolContainer  implements EPool {

		private Class<? extends APool<?>> pool;
		private HashMap<String, EElement> poolElements = new HashMap<>();
		private PoolDescription poolDescription;
		private String name;
		private ProcessContainer process;

		public PoolContainer(Class<? extends APool<?>> pool) throws MException {
			this.pool = pool;
			poolDescription = pool.getAnnotation(PoolDescription.class);
			if (poolDescription == null)
				throw new MException("Pool without description annotation found",pool);
			name = MString.isEmpty(poolDescription.name()) ? pool.getSimpleName() : poolDescription.name();
		}

		public void setProcess(ProcessContainer process) {
			this.process = process;
		}
		
		public EProcess getProcess() {
			return process;
		}

		public void collectElements() {
			for (EElement element : process.elements.values())
				if (isElementOfPool(element))
					poolElements.put(element.getCanonicalName(),element);
		}
		
		@Override
		public boolean isElementOfPool(EElement element) {
			Class<? extends AElement<?>> clazz = element.getElementClass();
			String elementPool = MSystem.getTemplateCanonicalName(clazz, 0);
			// for direct check
			// if (pool.getCanonicalName().equals(elementPool))
			// with this check also pool subclasses are possible
			EElement poolContainer = process.elements.get(elementPool);
			if (poolContainer == null) return false;
			Class<? extends AElement<?>> poolClass = poolContainer.getElementClass();
			return (pool.isAssignableFrom(poolClass));
		}

		@Override
		public String getCanonicalName() {
			return pool.getCanonicalName();
		}
		
		@Override
		public List<EElement> getStartPoints() {
			LinkedList<EElement> out = new LinkedList<>();
			for (EElement element : poolElements.values()) {
				Class<? extends AElement<?>> clazz = element.getElementClass();
				if (element.is(AStartPoint.class) && !InactiveStartPoint.class.isAssignableFrom(clazz))
					out.add(element);
			}
			return out;
		}
		
		@Override
		public Class<? extends APool<?>> getPoolClass() {
			return pool;
		}
				
		@Override
		public EElement getElement(String name) {
			return poolElements.get(name);
		}
		
		@Override
		public Set<String> getElementNames() {
			return poolElements.keySet();
		}

		@Override
		public List<EElement> getElements(Class<? extends AElement<?>> ifc) {
			LinkedList<EElement> out = new LinkedList<>();
			for (EElement element : poolElements.values())
				if (ifc.isAssignableFrom(element.getElementClass()))
					out.add(element);
			return out;
		}
		
		@Override
		public List<EElement> getOutputElements(EElement element) {
			LinkedList<EElement> out = new LinkedList<>();
			for (Output output : element.getOutputs()) {
				EElement o = getElement(output.activity().getCanonicalName());
				if (o != null)
					out.add(o);
			}
			return out;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public PoolDescription getPoolDescription() {
			return poolDescription;
		}
		
		@Override
		public String toString() {
			return getCanonicalName();
		}

	}
	
	public class ElementContainer implements EElement {

		private Class<? extends AElement<?>> element;
		private String name;
		private ActivityDescription actDescription;
		
		public ElementContainer(Class<? extends AElement<?>> element) throws MException {
			this.element = element;
			if (AActivity.class.isAssignableFrom(element)) {
				actDescription = element.getAnnotation(ActivityDescription.class);
				if (actDescription == null) throw new MException("Activity without description annotation",element);
			}
			name = actDescription == null || MString.isEmpty(actDescription.name()) ? element.getSimpleName() : actDescription.name();
		}

		@Override
		public String getCanonicalName() {
			return element.getCanonicalName();
		}

		@Override
		public Class<? extends AElement<?>> getElementClass() {
			return element;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean is(Class<? extends AElement> ifc) {
			if (ifc == null) return false;
			return ifc.isAssignableFrom(element);
		}
		
		@Override
		public Output[] getOutputs() {
			if (actDescription == null) return new Output[0];
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

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends ASwimlane<?>> getSwimlane() {
			if (actDescription == null) return null;
			@SuppressWarnings("rawtypes")
			Class<? extends ASwimlane> lane = actDescription.lane();
			return (Class<? extends ASwimlane<?>>) lane;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isInterface(Class<?> ifc) {
			if (ifc == null) return false;
			return ifc.isAssignableFrom(element);
		}

		@Override
		public ActivityDescription getActivityDescription() {
			return actDescription;
		}
		
		@Override
		public String toString() {
			return getCanonicalName();
		}

		@Override
		public HashMap<String, Long> getSchedulerList() {
			Trigger[] triggers = getTriggers();
			if (triggers.length == 0) return null;
			HashMap<String, Long> out = new HashMap<>();
			int cnt = 0;
			for (Trigger trigger : triggers) {
				if (trigger.type() == TYPE.TIMER) {
					long time = EngineUtil.getNextScheduledTime(trigger.event());
					out.put(trigger.name().length() == 0 ? "trigger." + cnt : trigger.name(), time);
				}
				cnt++;
			}
			return out;
		}

		@Override
		public HashMap<String, String> getSignalList() {
			Trigger[] triggers = getTriggers();
			if (triggers.length == 0) return null;
			HashMap<String, String> out = new HashMap<>();
			for (Trigger trigger : triggers) {
				if (trigger.type() == TYPE.SIGNAL) {
					out.put(trigger.name(), trigger.event());
				}
			}
			return out;
		}

		@Override
		public HashMap<String, String> getMessageList() {
			Trigger[] triggers = getTriggers();
			if (triggers.length == 0) return null;
			HashMap<String, String> out = new HashMap<>();
			for (Trigger trigger : triggers) {
				if (trigger.type() == TYPE.MESSAGE) {
					out.put(trigger.name(), trigger.event());
				}
			}
			return out;
		}

		@Override
		public Class<? extends AActor> getAssignedActor(EPool pool) {
			ActorAssign actorAssign = getElementClass().getAnnotation(ActorAssign.class);
			if (actorAssign != null) return actorAssign.value();
			Class<? extends ASwimlane<?>> lane = getSwimlane();
			if (lane == null || lane == DefaultSwimlane.class) {
				
			}
			actorAssign = lane.getAnnotation(ActorAssign.class);
			if (actorAssign != null) return actorAssign.value();
			if (pool != null)
				return pool.getPoolDescription().actorDefault();
			return NobodyActor.class;
		}
	}

	public Set<String> getProcessNames() {
		return processes.keySet();
	}
}
