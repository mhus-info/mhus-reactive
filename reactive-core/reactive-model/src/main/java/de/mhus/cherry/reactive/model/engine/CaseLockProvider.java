package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.TimeoutException;

public interface CaseLockProvider {

    boolean isCaseLocked(UUID caseId);

    /**
     * Return the lock if already locked. This must be an atomic operation.
     *
     * @param caseId
     * @return The acquired lock.
     * @throws TimeoutException Thrown if it was not possible to acquire the lock.
     */
    Lock lock(UUID caseId) throws TimeoutException;

    /**
     * return true if the clean up master was acquired until the date in ms.
     *
     * @return The lock or null
     */
    Lock acquireCleanupMaster();

    /**
     * return true if the prepare master was acquired until the date in ms.
     *
     * @return The lock or null
     */
    Lock acquirePrepareMaster();

    /**
     * acquired the engine lock
     *
     * @return The lock or null
     */
    Lock acquireEngineMaster();

    /**
     * Return the lock or null if already locked. This must be an atomic operation.
     *
     * @param caseId
     * @return The acquired lock.
     */
    Lock lockOrNull(UUID caseId);
    
    
    /**
     * Return true if the lock engine is ready to lock. If not the engine will wait until it's ready.
     * 
     * @return true if locking is possible
     */
    boolean isReady();
}
