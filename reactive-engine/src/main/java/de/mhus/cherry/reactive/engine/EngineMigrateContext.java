package de.mhus.cherry.reactive.engine;

import java.util.LinkedList;
import java.util.List;

import de.mhus.cherry.reactive.model.engine.PCase;
import de.mhus.cherry.reactive.model.engine.PNode;
import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.migrate.MigrationContext;

public class EngineMigrateContext implements MigrationContext {

	private EngineContext fromContext;
	private EngineContext toContext;
	private PCase caze;
	private LinkedList<PNode> nodes;

	public EngineMigrateContext(EngineContext fromContext, EngineContext toContext, PCase caze,
	        LinkedList<PNode> nodes) {
		this.fromContext = fromContext;
		this.toContext = toContext;
		this.caze = caze;
		this.nodes = nodes;
	}

	@Override
	public ProcessContext<?> getFromContext() {
		return fromContext;
	}

	@Override
	public ProcessContext<?> getToContext() {
		return toContext;
	}

	@Override
	public List<PNode> getNodes() {
		return nodes;
	}

	@Override
	public PCase getCase() {
		return caze;
	}

}
