package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.util.bpmn2.RPool;

public class RReleaseLock<P extends RPool<?>> extends RAbstractTask<P> {

    @Override
    public String doExecute() throws Exception {
        String resource = getResourceName();
        getContext().getEEngine().releaseEngineLock(resource, getContext());
        return null;
    }

    protected String getResourceName() {
        return ActivityUtil.getEvent(this);
    }

}
