package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.annotations.ActorDescription;
import de.mhus.cherry.reactive.model.engine.AaaProvider;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class RActor implements AActor, ContextRecipient {

	private ProcessContext<?> context;

	protected ProcessContext<?> getContext() {
		return context;
	}
	
	@Override
	public void setContext(ProcessContext<?> context) {
		this.context = (ProcessContext<?>) context;
	}

	@Override
	public boolean hasAccess(String user) {
		AaaProvider aaa = context.getAaaProvider();
		if (user == null || !aaa.isUserActive(user)) return false;
		if (aaa.hasAdminAccess(user)) return true;
		ActorDescription desc = this.getClass().getAnnotation(ActorDescription.class);
		if (desc != null) {
			for (String name : desc.users()) {
				if (user.equals(name)) return true;
			}
			for (String name : desc.groups()) {
				if (aaa.hasGroupAccess(user,name)) return true;
			}
		}
		return false;
	}

}
