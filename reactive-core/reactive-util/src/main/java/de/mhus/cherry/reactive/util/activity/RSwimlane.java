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
package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.AActor;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.activity.ASwimlane;
import de.mhus.cherry.reactive.model.engine.ContextRecipient;
import de.mhus.cherry.reactive.model.engine.ProcessContext;

public class RSwimlane<P extends APool<?>> implements ASwimlane<P>, ContextRecipient {

	private Class<? extends AActor> actor;

	@Override
	public void setContext(ProcessContext<?> context) {
		actor = (Class<? extends AActor>) context.getEPool().getPoolDescription().actorDefault();
	}

	@Override
	public Class<? extends AActor> getActor() {
		return actor;
	}

}
