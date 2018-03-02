package de.mhus.cherry.reactive.examples.simple1;

import java.util.Map;

import de.mhus.cherry.reactive.model.activity.StartPoint;
import de.mhus.cherry.reactive.util.ReactivePool;
import de.mhus.lib.annotations.adb.DbPersistent;

public class S1ExamplePool extends ReactivePool<S1ExamplePool> {

	@DbPersistent
	private String text1 = "Moin";

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	@Override
	public Class<? extends StartPoint<S1ExamplePool>> getStartPoint() {
		return S1Start1.class;
	}

	@Override
	protected void checkInputParameters(Map<String, Object> parameters) throws Exception {
		
	}
		
	
}
