package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.cherry.reactive.util.activity.RHumanTask;
import de.mhus.lib.core.M;
import de.mhus.lib.form.definition.FmColumns;
import de.mhus.lib.form.definition.FmText;

@ActivityDescription(
		outputs = @Output(activity=S1TheEnd.class), 
		lane = S1Lane1.class
		)
public class S1HumanStep extends RHumanTask<S1Pool> {

	@Override
	public String doExecute() {
		return "";
	}

	@SuppressWarnings("unchecked")
	@Override
	public HumanForm createForm() {
		return new HumanForm().add(
			new FmText(M.n(S1Pool::getText1), "Text1", ""),
			new FmText(M.n(S1Pool::getText1), "Text1", "",new FmColumns(2))
		).add(
			new FmText(M.n(S1Pool::getText1), "Text1", ""),
			new FmText(M.n(S1Pool::getText1), "Text1", ""),
			new FmText(M.n(S1Pool::getText1), "Text1", "")
		);
	}

}
