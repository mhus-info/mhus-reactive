package de.mhus.cherry.reactive.util.bpmn2;

import de.mhus.cherry.reactive.model.activity.AEndPoint;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.util.activity.RActivity;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;

/**
 * End point and send in the same time a message to the system.
 * @author mikehummel
 *
 * @param <P>
 */
public abstract class RMessageEnd<P extends RPool<?>> extends RActivity<P> implements AEndPoint<P> {

	@Override
	public void doExecuteActivity() throws Exception {
		MProperties parameters = new MProperties();
		String msg = prepareMessage(parameters);
		if (msg == null) return; // ignore and go ahead if msg name is null
		
		// send
		MUri uri = MUri.toUri("bpmm://" + msg);
		((InternalEngine)getContext().getEEngine()).execute(uri, parameters);
		
	}

	/**
	 * Prepare the parameters and return the name of the message to send.
	 * 
	 * @param parameters
	 * @return the name (not uri but the path of the uri without bpmm://) or null will not send and go ahead
	 */
	protected abstract String prepareMessage(MProperties parameters);

}
