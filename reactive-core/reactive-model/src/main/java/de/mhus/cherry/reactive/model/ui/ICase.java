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
package de.mhus.cherry.reactive.model.ui;

import java.util.Map;
import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.PCase.STATE_CASE;

public interface ICase {

	String getUri();

	String getCanonicalName();

	String getCustomId();

	String getCustomerId();
	
	STATE_CASE getState();

	UUID getId();

	Map<String,String> getProperties();

	long getCreated();

	long getModified();

	int getPriority();

	int getScore();

}
