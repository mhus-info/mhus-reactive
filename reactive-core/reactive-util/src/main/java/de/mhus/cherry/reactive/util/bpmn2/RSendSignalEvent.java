package de.mhus.cherry.reactive.util.bpmn2;

import de.mhus.cherry.reactive.model.activity.ASender;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.util.activity.RTask;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;

/**
 * Send a signal and step to the next activity.
 * @author mikehummel
 *
 * @param <P>
 */
public abstract class RSendSignalEvent<P extends RPool<?>> extends RTask<P> implements ASender<P> {

	@Override
	public String doExecute() throws Exception {
		MProperties parameters = new MProperties();
		String msg = prepareSignal(parameters);
		if (msg == null) return null; // ignore and go ahead if msg name is null
		
		// send
		MUri uri = MUri.toUri("bpms://" + msg);
		((InternalEngine)getContext().getEEngine()).execute(uri, parameters);
		
		return null;
	}

	/**
	 * Prepare the parameters and return the name of the signal to send.
	 * 
	 * @param parameters
	 * @return the name (not uri but the path of the uri without bpms://) or null will not send and go ahead
	 */
	protected abstract String prepareSignal(MProperties parameters);

}
