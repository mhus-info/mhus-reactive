package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.engine.PNode.TYPE_NODE;
import de.mhus.cherry.reactive.model.util.ActivityUtil;

public abstract class RMessageEvent<P extends RPool<?>> extends REvent<P>{

	@Override
	public void initializeActivity() throws Exception {
		getContext().getPNode().setState(STATE_NODE.WAITING);
		getContext().getPNode().setType(TYPE_NODE.MESSAGE);
		getContext().getPNode().setEvent(ActivityUtil.getEvent(this));
	}

}
