package de.mhus.cherry.reactive.util.bpmn2;

import java.util.UUID;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.ASubProcess;
import de.mhus.cherry.reactive.model.annotations.SubDescription;
import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.errors.EngineException;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.util.activity.RActivity;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.util.MUri;
import de.mhus.lib.core.util.MutableUri;

public abstract class RSubProcess<P extends RPool<?>> extends RActivity<P> implements ASubProcess<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		
		// get and check data
		SubDescription desc = getClass().getAnnotation(SubDescription.class);
		if (desc == null) 
			throw new EngineException("sub process without SubDescription definition");
		
		String uri = desc.uri();
		if (MString.isEmpty(uri)) 
			throw new EngineException("sub process without uri");

		InternalEngine iEngine = (InternalEngine)getContext().getEEngine();
		IProperties parameters = mapParametersForNewPool();
		MUri mUri = MUri.toUri(uri);
		((MutableUri)mUri).setParams(new String[] { EngineConst.OPTION_CLOSE_ACTIVITY + "=" + getContext().getPNode().getId() });
		UUID id = (UUID) iEngine.execute(mUri, parameters);
		if (id == null)
			throw new EngineException("Can't execute sub process");
		
		// set this node to wait
		getContext().getPNode().setState(STATE_NODE.WAITING);

	}
	
	/**
	 * Return parameter set for new pool.
	 * 
	 * @return parameters or null if not necessary
	 */
	protected abstract IProperties mapParametersForNewPool();

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
	 * Map parameters back from executed case.
	 * 
	 * @return The next task or null for default
	 */
	protected abstract String doExecuteAfterSub(ProcessContext<?> closingContext);

}
