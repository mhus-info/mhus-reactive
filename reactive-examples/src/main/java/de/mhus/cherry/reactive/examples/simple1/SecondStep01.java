package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Activity;
import de.mhus.cherry.reactive.model.activity.HumanForm;
import de.mhus.cherry.reactive.model.activity.HumanTask;
import de.mhus.cherry.reactive.model.activity.ServiceActivity;
import de.mhus.cherry.reactive.model.annotations.ActivityDefinition;
import de.mhus.lib.form.definition.FmColumns;
import de.mhus.lib.form.definition.FmText;

@ActivityDefinition(
		outputs = TheEnd01.class, 
		lane = MyLane01.class
		)
public class SecondStep01 extends HumanTask {

	@Override
	public Class<? extends Activity> doExecute() {
		return TheEnd01.class;
	}

	@Override
	public HumanForm createForm() {
		return new HumanForm().add(
			new FmText(ExamplePool01::getText1, "Text1", ""),
			new FmText(ExamplePool01::getText1, "Text1", "",new FmColumns(2))
		).add(
			new FmText(ExamplePool01::getText1, "Text1", ""),
			new FmText(ExamplePool01::getText1, "Text1", ""),
			new FmText(ExamplePool01::getText1, "Text1", "")
		);
	}

}
