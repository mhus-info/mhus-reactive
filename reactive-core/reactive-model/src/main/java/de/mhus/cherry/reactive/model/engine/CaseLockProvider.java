package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.lib.core.concurrent.Lock;

public interface CaseLockProvider {

    boolean isCaseLocked(UUID caseId);

    Lock lock(UUID caseId);

}
