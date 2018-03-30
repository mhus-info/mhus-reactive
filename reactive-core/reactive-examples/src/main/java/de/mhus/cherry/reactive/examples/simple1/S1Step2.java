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

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.old.RServiceTaskInternal;

@ActivityDescription(
		outputs = @Output(activity=S1TheEnd.class), 
		lane = S1Lane1.class
		)
public class S1Step2 extends RServiceTaskInternal<S1Pool> {

	@Override
	public Class<? extends AActivity<S1Pool>> doExecuteInternal() {
		return S1TheEnd.class;
	}

//	@Override
//	public HumanForm createForm() {
//		return new HumanForm().add(
//			new FmText(S1Pool::getText1, "Text1", ""),
//			new FmText(S1Pool::getText1, "Text1", "",new FmColumns(2))
//		).add(
//			new FmText(S1Pool::getText1, "Text1", ""),
//			new FmText(S1Pool::getText1, "Text1", ""),
//			new FmText(S1Pool::getText1, "Text1", "")
//		);
//	}

}
