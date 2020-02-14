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

    private static CfgLong CFG_TIMEOUT =
            new CfgLong(
                    LocalCaseLockProvider.class, "timeout", MPeriod.MINUTE_IN_MILLISECOUNDS * 5);

    @Override
    public boolean isCaseLocked(UUID caseId) {
        return M.l(LockManager.class)
                .getLock(getClass().getCanonicalName() + "_" + caseId)
                .isLocked();
    }

    @Override
    public Lock lock(UUID caseId) throws TimeoutException {
        return M.l(LockManager.class)
                .getLock(getClass().getCanonicalName() + "_" + caseId)
                .lockWithException(CFG_TIMEOUT.value());
    }

    @Override
    public Lock acquireCleanupMaster() {
        return new DummyLock();
    }

    @Override
    public Lock acquirePrepareMaster() {
        return new DummyLock();
    }

    @Override
    public Lock lockOrNull(UUID caseId) {
        try {
            return M.l(LockManager.class)
                    .getLock(getClass().getCanonicalName() + "_" + caseId)
                    .lockWithException(10);
        } catch (TimeoutException e) {
        }
        return null;
    }

    @Override
    public Lock acquireEngineMaster() {
        return M.l(LockManager.class).getLock(getClass().getCanonicalName() + ":engine").lock();
    }

//    @Override
//    public void releaseEngineMaster() {
//        M.l(LockManager.class).getLock(getClass().getCanonicalName() + ":engine").unlockHard();
//    }

    @Override
    public boolean isReady() {
        return true;
    }
    
    private class DummyLock implements Lock {

        @Override
        public Lock lock() {
            return this;
        }

        @Override
        public boolean lock(long timeout) {
            return true;
        }

        @Override
        public boolean unlock() {
            return true;
        }

        @Override
        public void unlockHard() {
            
        }

        @Override
        public boolean isLocked() {
            return true;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getOwner() {
            return null;
        }

        @Override
        public long getLockTime() {
            return 0;
        }

        @Override
        public boolean refresh() {
            return true;
        }

        @Override
        public long getCnt() {
            return 0;
        }

        @Override
        public String getStartStackTrace() {
            return null;
        }
        
    }
}
