package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AExclusiveGateway;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.errors.EngineException;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.util.bpmn2.RPool;

public abstract class RConditionGateway<P extends RPool<?>> extends RActivity<P> implements AExclusiveGateway<P> {

    @Override
    public void doExecuteActivity() throws Exception {
        String nextName = doExecute();
        if (nextName == null) nextName = DEFAULT_OUTPUT;
        if (!nextName.equals(RETRY)) {
            Class<? extends AActivity<?>> next = ActivityUtil.getOutputByName(this, nextName);
            if (next == null)
                throw new EngineException(
                        "Output Activity not found: "
                                + nextName
                                + " in "
                                + getClass().getCanonicalName());
            getContext().createActivity(next);
            getContext().getPNode().setState(STATE_NODE.CLOSED);
        }

    }

    public abstract String doExecute() throws Exception;

}
