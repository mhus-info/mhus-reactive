package de.mhus.cherry.reactive.examples.simple1;

import java.util.Map;

import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.util.activity.RPool;
import de.mhus.lib.annotations.adb.DbPersistent;

@PoolDescription()
public class S1Pool extends RPool<S1Pool> {

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
		
	
}
