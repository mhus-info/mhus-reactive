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
package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AServiceTask;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.util.bpmn2.RPool;
import de.mhus.lib.core.MPeriod;

/**
 * Use this task type to check something in intervals. The default interval is fifteen minutes. You
 * can change the interval using the ActivityDescription.event() parameter.
 *
 * @author mikehummel
 * @param <P>
 */
public abstract class RRetryTask<P extends RPool<?>> extends RTask<P> implements AServiceTask<P> {

    /** Overwrite to follow another branch then default. */
    protected String output = null;
    /** Overwrite to change the default interval time. */
    protected long interval = 0;

    public RRetryTask() {
        interval = getDefaultInterval();
    }

    /**
     * Returns the default interval set by definition or 15 minutes as fall back.
     *
     * @return Default configured interval
     */
    public long getDefaultInterval() {
        String intervalStr = getClass().getAnnotation(ActivityDescription.class).event();
        return MPeriod.toMilliseconds(intervalStr, 60000 * 15);
    }

    @Override
    public String doExecute() throws Exception {

        boolean done = doRetry();

        if (!done) {
            long newSchedule = System.currentTimeMillis() + interval;
            getContext().getPNode().setScheduled(newSchedule);
            getContext().getPNode().setTryCount(3);
            getContext().getPNode().setState(STATE_NODE.SCHEDULED);
            return RETRY;
        } else {
            getContext().getPNode().setState(STATE_NODE.CLOSED);
        }

        return output;
    }

    protected abstract boolean doRetry() throws Exception;
}
