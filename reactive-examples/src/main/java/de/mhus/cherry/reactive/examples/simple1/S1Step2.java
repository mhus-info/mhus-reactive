package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.annotations.ActivityDescription;
import de.mhus.cherry.reactive.model.util.HumanForm;
import de.mhus.cherry.reactive.util.ReactiveHumanTask;
import de.mhus.lib.form.definition.FmColumns;
import de.mhus.lib.form.definition.FmText;

@ActivityDescription(
		outputs = S1TheEnd.class, 
		lane = S1Lane1.class
		)
public class S1Step2 extends ReactiveHumanTask<S1ExamplePool> {

	@Override
	public Class<? extends Activity<S1ExamplePool>> doExecute() {
		return S1TheEnd.class;
	}

	@Override
	public HumanForm createForm() {
		return new HumanForm().add(
			new FmText(S1ExamplePool::getText1, "Text1", ""),
			new FmText(S1ExamplePool::getText1, "Text1", "",new FmColumns(2))
		).add(
			new FmText(S1ExamplePool::getText1, "Text1", ""),
			new FmText(S1ExamplePool::getText1, "Text1", ""),
			new FmText(S1ExamplePool::getText1, "Text1", "")
		);
	}

}
