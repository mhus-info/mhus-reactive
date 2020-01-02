package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.TimeoutException;

public interface CaseLockProvider {

    boolean isCaseLocked(UUID caseId);

    Lock lock(UUID caseId) throws TimeoutException;

}
