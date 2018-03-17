package de.mhus.cherry.reactive.model.util;

import de.mhus.cherry.reactive.model.engine.ProcessContext;
import de.mhus.cherry.reactive.model.engine.RuntimeNode;

public interface CloseActivity {

	void doClose(ProcessContext<?> context, RuntimeNode runtimeNode);

}
