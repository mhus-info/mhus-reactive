package de.mhus.cherry.reactive.model.engine;

/**
 * Enhanced, not default engine features.
 * 
 * @author mikehummel
 *
 */
public interface InternalEngine {

	RuntimeNode doExecuteStartPoint(ProcessContext<?> context, EElement eMyStartPoint) throws Exception;

}
