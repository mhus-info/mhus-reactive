package de.mhus.cherry.reactive.model.migrate;

import java.util.List;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public interface MigrationContext {

	ProcessContext<?> getFromContext();
	ProcessContext<?> getToContext();
	
	PCase getCase();
	List<PNode> getNodes();
	
}
