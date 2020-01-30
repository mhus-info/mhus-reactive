package de.mhus.cherry.reactive.util.activity;

import java.util.Map;

import de.mhus.cherry.reactive.model.engine.EngineConst;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.model.util.ValidateParametersBeforeExecute;
import de.mhus.cherry.reactive.util.bpmn2.RPool;
import de.mhus.lib.errors.ValidationException;

public class RAquireLock<P extends RPool<?>> extends REvent<P> implements ValidateParametersBeforeExecute {

    
    @Override
    public void initializeActivity() throws Exception {
        
        String resource = getResourceName();
        
        if (getContext().getEEngine().aquireEngineLock(resource, getContext())) {
            getContext().getPNode().setState(STATE_NODE.RUNNING);
        } else {
            getContext().getPNode().setState(STATE_NODE.WAITING);
            getContext().getPNode().setType(TYPE_NODE.MESSAGE);
            getContext().getPNode().setMessageEvent(EngineConst.LOCK_PREFIX + resource);
        }
    }
    
    protected String getResourceName() {
        return ActivityUtil.getEvent(this);
    }

    @Override
    public void doExecute() throws Exception {

    }

    @Override
    public void validateParameters(Map<String, Object> parameters) throws ValidationException {
        String resource = getResourceName();
        if (!getContext().getEEngine().aquireEngineLock(resource, getContext())) {
            throw new ValidationException("can't aquire lock",resource);
        }
    }


}
