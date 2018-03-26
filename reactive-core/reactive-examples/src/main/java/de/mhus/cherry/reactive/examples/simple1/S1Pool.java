package de.mhus.cherry.reactive.examples.simple1;

import java.util.Date;
import java.util.Map;

import de.mhus.cherry.reactive.model.annotations.PoolDescription;
import de.mhus.cherry.reactive.util.activity.RPool;
import de.mhus.lib.annotations.adb.DbPersistent;

@PoolDescription(
		displayName="Example Pool",
		description="This pool is used to test the current development",
		indexDisplayNames = {"Text 1","Text 2","Created"},
		actorRead=S1ActorManager.class
		)
public class S1Pool extends RPool<S1Pool> {

	@DbPersistent
	private String text1 = "Moin";
	@DbPersistent
	private String text2 = "";

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
	}

	@Override
	protected void checkInputParameters(Map<String, Object> parameters) throws Exception {
		
	}

	public Object getText2() {
		return text2;
	}

	@Override
	public String[] createIndexValues(boolean init) {
		if (init)
			return new String[] {text1,text2,new Date().toString()};
		return null;
	}
		
	
}
