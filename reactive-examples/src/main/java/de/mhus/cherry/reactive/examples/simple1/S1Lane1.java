package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActorAssign;
import de.mhus.cherry.reactive.util.ReactiveSwimlane;

@ActorAssign(S1ActorWorker.class)
public class S1Lane1 extends ReactiveSwimlane<S1ExamplePool> {

}
