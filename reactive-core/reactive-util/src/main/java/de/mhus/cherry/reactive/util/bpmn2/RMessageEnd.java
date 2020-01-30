/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.util.bpmn2;

import de.mhus.cherry.reactive.model.activity.AEndPoint;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.util.activity.RActivity;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;

/**
 * End point and send in the same time a message to the system.
 *
 * @author mikehummel
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
        ((InternalEngine) getContext().getEEngine()).execute(uri, parameters);
    }

    /**
     * Prepare the parameters and return the name of the message to send.
     *
     * @param parameters
     * @return the name (not uri but the path of the uri without bpmm://) or null will not send and
     *     go ahead
     */
    protected abstract String prepareMessage(MProperties parameters);
}
