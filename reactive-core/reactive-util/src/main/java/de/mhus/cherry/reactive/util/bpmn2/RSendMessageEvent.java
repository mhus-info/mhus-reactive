package de.mhus.cherry.reactive.util.bpmn2;

import de.mhus.cherry.reactive.model.activity.ASender;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.util.activity.RTask;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;

public abstract class RSendMessageEvent<P extends RPool<?>> extends RTask<P> implements ASender<P> {

	@Override
	public String doExecute() throws Exception {
		MProperties parameters = new MProperties();
		String msg = prepareMessage(parameters);
		if (msg == null) return null; // ignore and go ahead if msg name is null
		
		// send
		MUri uri = MUri.toUri("bpmm://" + msg);
		((InternalEngine)getContext().getEEngine()).execute(uri, parameters);
		
		return null;
	}

	/**
	 * Prepare the parameters and return the name of the message to send.
	 * 
	 * @param parameters
	 * @return the name (not uri but the path of the uri without bpmm://) or null will not send and go ahead
	 */
	protected abstract String prepareMessage(MProperties parameters);

}
