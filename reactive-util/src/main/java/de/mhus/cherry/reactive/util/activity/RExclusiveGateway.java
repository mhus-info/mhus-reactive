package de.mhus.cherry.reactive.util.activity;

import de.mhus.cherry.reactive.model.activity.ACondition;
import de.mhus.cherry.reactive.model.activity.AExclusiveGateway;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.util.ActivityUtil;
import de.mhus.cherry.reactive.model.util.NoCondition;
import de.mhus.lib.errors.MException;

/**
 * Decide between outputs using the Output Condition
 * Define one Output without Condition to set it as default Output
 * Define ACondition(s) to the Output(s) for the condition.
 * RExclusiveGateway will follow the Output with the highest result. Or the first with the same result
 * For binary decisions use the constants TRUE and FALSE
 * 
 * @author mikehummel
 *
 * @param <P>
 */
public class RExclusiveGateway<P extends RPool<?>> extends RGateway<P> implements AExclusiveGateway<P> {

	@SuppressWarnings("unchecked")
	@Override
	public Output[] doExecute() throws Exception {
		Output current = null;
		Output defaultOutput = null;
		int currentRes = Integer.MIN_VALUE;
		for (Output output : ActivityUtil.getOutputs(this)) {
			if (output.condition() == NoCondition.class)
				defaultOutput = output;
			else {
				Class<? extends ACondition<P>> condition = (Class<? extends ACondition<P>>) output.condition();
				int res = condition.newInstance().check(getContext());
				if (res > currentRes) {
					currentRes = res;
					current = output;
				}
			}
		}
		if (current == null) current = defaultOutput;
		if (current == null) throw new MException("condition not found",getClass().getCanonicalName());
		return new Output[] {current};
	}

}
