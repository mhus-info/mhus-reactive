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
package de.mhus.cherry.reactive.examples.simple1.events;

import de.mhus.cherry.reactive.examples.simple1.S1Lane1;
import de.mhus.cherry.reactive.examples.simple1.S1Pool;
import de.mhus.cherry.reactive.examples.simple1.S1TheEnd;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.bpmn2.RReceiveSignalEvent;

@ActivityDescription(
        event = "signal",
        outputs = @Output(activity = S1TheEnd.class),
        lane = S1Lane1.class)
public class S1EventSignal extends RReceiveSignalEvent<S1Pool> {

    @Override
    public void doExecute() throws Exception {
        log().i(getContext().getPNode().getMessage());
    }
}
