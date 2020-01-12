package de.mhus.cherry.reactive.model.util;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.CaseLockProvider;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.base.service.LockManager;
import de.mhus.lib.core.cfg.CfgLong;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.TimeoutException;

public class LocalCaseLockProvider implements CaseLockProvider {

    private static CfgLong CFG_TIMEOUT = new CfgLong(LocalCaseLockProvider.class, "timeout", MPeriod.MINUTE_IN_MILLISECOUNDS * 5);
    
    @Override
    public boolean isCaseLocked(UUID caseId) {
        return M.l(LockManager.class).getLock(getClass().getCanonicalName() + "_" + caseId).isLocked();
    }

    @Override
    public Lock lock(UUID caseId) throws TimeoutException {
        return M.l(LockManager.class).getLock(getClass().getCanonicalName() + "_" + caseId).lockWithException(CFG_TIMEOUT.value());
    }

    @Override
    public boolean acquireCleanupMaster(long until) {
        return true;
    }

    @Override
    public boolean acquirePrepareMaster(long until) {
        return true;
    }

}
