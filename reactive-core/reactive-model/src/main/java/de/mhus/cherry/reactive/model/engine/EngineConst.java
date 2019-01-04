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
package de.mhus.cherry.reactive.model.engine;

import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.cfg.CfgLong;

public class EngineConst {

	public static final String SCHEME_REACTIVE = "bpm";
	public static final String OPTION_CUSTOM_ID = "customId";
	public static final String OPTION_CUSTOMER_ID = "customerId";
	public static CfgLong DEFAULT_ACTIVITY_TIMEOUT = new CfgLong(EEngine.class, "defaultActivityTimeout", MPeriod.MINUTE_IN_MILLISECOUNDS * 5);
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
	public static final String ENGINE_PROGRESS_TIMEOUT = "engine.progress.timeout";

	public static final String MILESTONE_START = "NEW";
	public static final String MILESTONE_PROGRESS = "PROGRESS";
	public static final String MILESTONE_FINISHED = "FINISHED";
	/**
	 * Set this parameter to wait for milestone PROGRESS after start of case.
	 */
	public static final String PARAM_PROGRESS = "progress";
	public static final String OPTION_UUID = "uuid";
	public static final String OPTION_CLOSE_ACTIVITY = "closeActivity"; 

}
