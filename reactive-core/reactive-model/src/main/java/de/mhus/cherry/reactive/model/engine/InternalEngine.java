package de.mhus.cherry.reactive.model.engine;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.util.MUri;

/**
 * Enhanced, not default engine features.
 * 
 * @author mikehummel
 *
 */
public interface InternalEngine {

	RuntimeNode doExecuteStartPoint(ProcessContext<?> context, EElement eMyStartPoint) throws Exception;

	Object execute(MUri uri, IProperties parameters) throws Exception;

	void doNodeErrorHandling(PNode closeNode, String error) throws Exception;

}
