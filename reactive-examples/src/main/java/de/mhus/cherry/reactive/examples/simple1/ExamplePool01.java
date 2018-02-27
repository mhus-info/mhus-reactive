package de.mhus.cherry.reactive.examples.simple1;

import de.mhus.cherry.reactive.model.activity.Pool;
import de.mhus.cherry.reactive.model.activity.StartPoint;
import de.mhus.lib.annotations.adb.DbPersistent;

public class ExamplePool01 extends Pool {

	@DbPersistent
	private String text1 = "Moin";

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}
	
	public Class<? extends StartPoint> getStart() {
		return Start01.class;
	}
	
	
}
