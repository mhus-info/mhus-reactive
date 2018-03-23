package de.mhus.cherry.reactive.model.ui;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class ICase {

	private String uri;
	private String caseName;
	private String caseCanonicalName;
	private ActivityDescription caseDescription;

	public ICase(ProcessContext<?> context, PCase caze) {
		this.uri = caze.getUri();
		this.caseName = caze.getName();
		this.caseCanonicalName = caze.getCanonicalName();
		this.caseDescription = context.getEPool().getElement(caze.getCanonicalName()).getActivityDescription();
	}

	public String getUri() {
		return uri;
	}

	public String getCaseName() {
		return caseName;
	}

	public String getCaseCanonicalName() {
		return caseCanonicalName;
	}

	public String getCaseDisplayName() {
		return caseDescription.displayName();
	}
	
	public String getCaseDescription() {
		return caseDescription.description();
	}

}
