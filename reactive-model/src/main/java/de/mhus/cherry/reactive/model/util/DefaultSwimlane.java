package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class DefaultSwimlane implements ASwimlane<APool<?>>, ContextRecipient {

	private Class<? extends AActor<APool<?>>> actor;

	@SuppressWarnings("unchecked")
	@Override
	public void setContext(ProcessContext<?> context) {
		actor = (Class<? extends AActor<APool<?>>>) context.getEPool().getPoolDescription().actorDefault();
	}

	@Override
	public Class<? extends AActor<APool<?>>> getActor() {
		return actor;
	}


}
