/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.cherry.reactive.util.bpmn2;

import de.mhus.cherry.reactive.model.activity.ASender;
import de.mhus.cherry.reactive.model.engine.InternalEngine;
import de.mhus.cherry.reactive.util.activity.RTask;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.util.MUri;

/**
 * Send a message and step to the next activity.
 *
 * @author mikehummel
 * @param <P>
 */
public abstract class RSendMessageEvent<P extends RPool<?>> extends RTask<P> implements ASender<P> {

    @Override
    public String doExecute() throws Exception {
        MProperties parameters = new MProperties();
        String msg = prepareMessage(parameters);
        if (msg == null) return null; // ignore and go ahead if msg name is null

        // send
        MUri uri = MUri.toUri("bpmm://" + msg);
        ((InternalEngine) getContext().getEEngine()).execute(uri, parameters);

        return null;
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
