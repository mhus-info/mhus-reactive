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
package de.mhus.cherry.reactive.model.engine;

import java.util.List;
import java.util.Set;

import de.mhus.cherry.reactive.model.activity.AElement;
import de.mhus.cherry.reactive.model.activity.APool;
import de.mhus.cherry.reactive.model.annotations.PoolDescription;

public interface EPool {

	List<EElement> getStartPoints();

	Class<? extends APool<?>> getPoolClass();

	EElement getElement(String name);

	Set<String> getElementNames();

	List<EElement> getElements(Class<? extends AElement<?>> ifc);

	String getCanonicalName();

	List<EElement> getOutputElements(EElement element);

	String getName();

	boolean isElementOfPool(EElement element);

	PoolDescription getPoolDescription();
}
