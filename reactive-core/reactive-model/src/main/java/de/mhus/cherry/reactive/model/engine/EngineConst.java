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

import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.cfg.CfgLong;

public class EngineConst {

	public static final String SCHEME_REACTIVE = "bpm";
	public static final String OPTION_CUSTOM_ID = "customId";
	public static final String OPTION_CUSTOMER_ID = "customerId";
	public static CfgLong DEFAULT_ACTIVITY_TIMEOUT = new CfgLong(EEngine.class, "defaultActivityTimeout", MTimeInterval.MINUTE_IN_MILLISECOUNDS * 5);
	public static final int TRY_COUNT = 3;
	public static final String ENGINE_EXECUTE_PARALLEL = "engine.execute.parallel";
	public static final String ENGINE_EXECUTE_MAX_THREADS = "engine.execute.max.threads";
	public static final String ENGINE_SLEEP_BETWEEN_PROGRESS = "engine.sleep.between.progress";
	public static final int DEFAULT_TRY_COUNT = 1;
	public static final long END_OF_DAYS = Long.MAX_VALUE;
	public static final int MAX_INDEX_VALUES = 10;
	public static final String UI_PNODE_PREFIX = "pnode.";
	public static final String UI_CASE_PREFIX = "case.";
	public static final String UI_NODE_PREFIX = "node.";
	public static final int MAX_CREATE_ACTIVITY = 1000;
	public static final int ERROR_CODE_MAX_CREATE_ACTIVITY = -1000;

}
