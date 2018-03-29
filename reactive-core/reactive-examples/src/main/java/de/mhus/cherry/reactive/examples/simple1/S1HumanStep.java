package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.annotations.Output;
import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.cherry.reactive.util.activity.RHumanTask;
import de.mhus.lib.annotations.generic.Public;
import de.mhus.lib.core.M;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.definition.FmReadOnly;
import de.mhus.lib.form.definition.FmText;

@ActivityDescription(
		outputs = @Output(activity=S1TheEnd.class), 
		lane = S1Lane1.class
		)
public class S1HumanStep extends RHumanTask<S1Pool> {

	@PropertyDescription
	private String text3 = "text3";
	
	@SuppressWarnings("unchecked")
	@Override
	public HumanForm createForm() {
		return new HumanForm().add(
			new FmText(M.n(S1Pool::getText1), "Text1", "", new FmReadOnly()),
			new FmText(M.n(S1Pool::getText2), "Text2", ""),
			new FmText(M.n(S1HumanStep::getText3), "Text3", "")
		);
	}

	@Override
	public String[] createIndexValues(boolean init) {
		return null;
	}

	@Override
	protected void doSubmit() throws MException {
		
	}

	public String getText3() {
		return text3;
	}

}
