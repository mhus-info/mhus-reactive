/**
 * This file is part of cherry-reactive.
 *
 *     cherry-reactive is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     cherry-reactive is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with cherry-reactive.  If not, see <http://www.gnu.org/licenses/>.
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
