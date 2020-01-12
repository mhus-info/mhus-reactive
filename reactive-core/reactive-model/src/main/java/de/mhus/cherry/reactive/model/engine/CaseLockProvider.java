package de.mhus.cherry.reactive.model.engine;

import java.util.UUID;

import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.TimeoutException;

public interface CaseLockProvider {

    boolean isCaseLocked(UUID caseId);

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

}
