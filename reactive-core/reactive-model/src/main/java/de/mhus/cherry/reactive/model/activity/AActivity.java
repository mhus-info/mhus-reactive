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
package de.mhus.cherry.reactive.model.activity;

import java.util.Map;

import de.mhus.cherry.reactive.model.engine.ProcessContext;

public interface AActivity<P extends APool<?>> extends AElement<P> {

	String DEFAULT_OUTPUT = "";
	
	ProcessContext<P> getContext();
	
	default P getPool() {
		return getContext().getPool();
	}

	void initializeActivity() throws Exception;
	
	void doExecuteActivity() throws Exception;

	Map<String, Object> exportParamters();
	
	void importParameters(Map<String, Object> parameters);

}
