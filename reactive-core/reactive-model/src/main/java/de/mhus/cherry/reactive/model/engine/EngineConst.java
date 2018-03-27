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

}
