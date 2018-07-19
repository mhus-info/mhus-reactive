package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.ASubProcess;
import de.mhus.cherry.reactive.model.annotations.SubDescription;
import de.mhus.cherry.reactive.model.engine.EElement;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;
import de.mhus.cherry.reactive.model.errors.EngineException;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.util.bpmn2.RPool;

/**
 * Execute another StartPoint of the same pool and wait until it ends. If you use
 * InactiveStartPoint the will also be executed. You can realize sub sequences using
 * this Element.
 * 
 * The SubStart will run in the same case but within a separate Runtime.
 * 
 * Define the start point using the SubDescription
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public class RSubStart<P extends RPool<?>> extends RActivity<P> implements ASubProcess<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		
		// get and check data
		SubDescription desc = getClass().getAnnotation(SubDescription.class);
		if (desc == null) 
			throw new EngineException("sub start without SubDescription definition");
		String myStartPointName = desc.start().getCanonicalName();
		EElement eMyStartPoint = getContext().getEPool().getElement(myStartPointName);
		if (eMyStartPoint == null)
			throw new EngineException("sub start point '"+myStartPointName+"' not found in pool " + getPool().getClass());
		
		// start sub node and set close activity
		InternalEngine iEngine = (InternalEngine)getContext().getEEngine();
		RuntimeNode runtime = iEngine.doExecuteStartPoint(getContext(), eMyStartPoint);
		prepareNewRuntime(runtime);
		runtime.setCloseActivity(getContext().getPNode().getId());
		runtime.save();
		
		// set this node to wait
		getContext().getPNode().setState(STATE_NODE.WAITING);
		
	}

	/**
	 * Prepare new runtime object before save if needed
	 * 
	 * @param runtime
	 */
	protected void prepareNewRuntime(RuntimeNode runtime) {
		
	}

	@Override
	public void doClose(ProcessContext<?> closingContext) throws Exception {
		// execute next activity
		String nextName = doExecuteAfterSub(closingContext);
		if (nextName == null) 
			nextName = DEFAULT_OUTPUT;
		if (!nextName.equals(RETRY)) {
			Class<? extends AActivity<?>> next = ActivityUtil.getOutputByName(this, nextName);
			if (next == null)
				throw new EngineException("Output Activity not found: " + nextName + " in " + getClass().getCanonicalName());
			getContext().createActivity(next);
			getContext().getPNode().setState(STATE_NODE.CLOSED);
		}
	}

	/**
	 * Overwrite to do something after the sub is finished and before the next task is started.
	 * @return The next task or null for default
	 */
	protected String doExecuteAfterSub(ProcessContext<?> closingContext) {
		return null;
	}

}
