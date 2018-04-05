/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
