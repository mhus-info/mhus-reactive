package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.util.activity.RSwimlane;

@ActorAssign(S1ActorWorker.class)
public class S1Lane1 extends RSwimlane<S1Pool> {

}
