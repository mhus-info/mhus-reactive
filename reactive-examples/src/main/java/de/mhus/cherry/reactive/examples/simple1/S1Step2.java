package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.AActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.util.activity.RServiceTaskInternal;

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
