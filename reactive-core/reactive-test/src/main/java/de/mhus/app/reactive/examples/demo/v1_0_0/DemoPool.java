package de.mhus.app.reactive.examples.demo.v1_0_0;

import java.util.Map;

import de.mhus.app.reactive.examples.simple1.S1ActorManager;
import de.mhus.app.reactive.examples.simple1.S1ActorWorker;
import de.mhus.app.reactive.model.annotations.PoolDescription;
import de.mhus.app.reactive.util.bpmn2.RPool;

@PoolDescription(
        displayName = "Demo Pool",
        description = "This pool is a demo",
        actorRead = S1ActorWorker.class,
        actorInitiator = S1ActorManager.class)
public class DemoPool extends RPool<DemoPool> {

    @Override
    public String[] createIndexValues(boolean init) {
        return null;
    }

    @Override
    protected void checkInputParameters(Map<String, Object> parameters) throws Exception {
        
    }

}
