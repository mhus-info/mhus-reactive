package de.mhus.cherry.reactive.engine.util;

import de.mhus.cherry.reactive.model.activity.AProcess;
import de.mhus.cherry.reactive.model.annotations.ProcessDescription;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MTimeInterval;
import de.mhus.lib.core.schedule.CronJob;

public class EngineUtil {

	/**
	 * Calculate the next execution time for this trigger
	 * @param timer
	 * @return time or -1
	 */
	public static long getNextScheduledTime(String timer) {
		
		if (MString.isEmptyTrim(timer))
			return -1;

		long time = MTimeInterval.toTime(timer, -1);
		if (time < 0) {
			CronJob.Definition def = new CronJob.Definition(timer);
			if(def.isDisabled()) {
				return -1;
			}
			return def.calculateNext(System.currentTimeMillis());
		} else {
			return System.currentTimeMillis() + time;
		}
	}

	public static String getProcessCanonicalName(AProcess process) {
		ProcessDescription desc = process.getClass().getAnnotation(ProcessDescription.class);
		if (desc == null) return null;
		String name = desc.name();
		if (MString.isEmpty(name)) name = process.getClass().getCanonicalName();
		return name + ":" + desc.version();
	}

}
