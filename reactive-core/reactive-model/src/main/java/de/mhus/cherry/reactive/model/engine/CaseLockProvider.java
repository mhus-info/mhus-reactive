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
     * @param until system time in ms
     * @return true if acquired, false if not
     */
    boolean acquireCleanupMaster(long until);

    /**
     * return true if the prepare master was acquired until the date in ms.
     *
     * @param until system time in ms
     * @return true if acquired, false if not
     */
    boolean acquirePrepareMaster(long until);

    /**
     * acquired the engine lock
     *
     * @param until system time in ms
     */
    void acquireEngineMaster();
    
    /**
     * release a aquired lock.
     */
    void releaseEngineMaster();
    
    /**
     * Return the lock or null if already locked. This must be an atomic operation.
     *
     * @param caseId
     * @return The acquired lock.
     */
    Lock lockOrNull(UUID caseId);
}
