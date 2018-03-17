package de.mhus.cherry.reactive.model.migrate;

import java.util.List;

import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public interface Migrator {

	void doMigrate(ProcessContext<?> context, List<PNode> nodes);

}
