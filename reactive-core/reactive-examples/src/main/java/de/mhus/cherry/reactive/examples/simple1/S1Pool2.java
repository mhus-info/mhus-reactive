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

package de.mhus.cherry.reactive.examples.simple1;

import java.util.Map;

import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.util.activity.RPool;
import de.mhus.lib.annotations.adb.DbPersistent;

@PoolDescription()
public class S1Pool2 extends RPool<S1Pool2> {

	@DbPersistent
	private String text1 = "Moin";

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	@Override
	protected void checkInputParameters(Map<String, Object> parameters) throws Exception {
		
	}

	@Override
	public String[] createIndexValues(boolean init) {
		// TODO Auto-generated method stub
		return null;
	}
		
	
}
