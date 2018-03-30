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

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.activity.AEvent;
import de.mhus.cherry.reactive.model.engine.PNode.STATE_NODE;
import de.mhus.cherry.reactive.model.errors.EngineException;
import de.mhus.cherry.reactive.model.util.ActivityUtil;

public abstract class RAbstractEvent<P extends RPool<?>> extends RActivity<P> implements AEvent<P> {

	protected String outputName = DEFAULT_OUTPUT;

	@Override
	public void doExecuteActivity() throws Exception {
		doExecute();
		if (outputName == null) outputName = DEFAULT_OUTPUT;
		if (!outputName.equals(RETRY)) {
			Class<? extends AActivity<?>> next = ActivityUtil.getOutputByName(this, outputName);
			if (next == null)
				throw new EngineException("Output Activity not found: " + outputName + " in " + getClass().getCanonicalName());
			getContext().createActivity(next);
			getContext().getPNode().setState(STATE_NODE.CLOSED);
		}
	}

	public abstract void doExecute()  throws Exception;

}
