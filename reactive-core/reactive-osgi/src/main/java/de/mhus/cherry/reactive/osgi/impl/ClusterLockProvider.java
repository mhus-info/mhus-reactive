package de.mhus.cherry.reactive.osgi.impl;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.CaseLockProvider;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.cfg.CfgLong;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.TimeoutException;
import de.mhus.osgi.sop.api.cluster.ClusterApi;

public class ClusterLockProvider extends MLog implements CaseLockProvider {

    private static CfgLong CFG_TIMEOUT = new CfgLong(ClusterLockProvider.class, "timeout", 10000);
    private String name;

    public ClusterLockProvider(String name) {
        this.name = name;
    }

    @Override
    public boolean isCaseLocked(UUID caseId) {
        return M.l(ClusterApi.class).getLock("reactive_case_" + name + "_" + caseId).isLocked();
    }

    @Override
    public Lock lock(UUID caseId) throws TimeoutException {
        return M.l(ClusterApi.class)
                .getLock("reactive_case_" + name + "_" + caseId)
                .lockWithException(CFG_TIMEOUT.value());
    }

    @Override
    public Lock acquireCleanupMaster() {
        if (M.l(ClusterApi.class).isMaster("reactive_cleanup_" + name))
            return new MasterLock("reactive_cleanup_" + name);
        return null;
    }

    @Override
    public Lock acquirePrepareMaster() {
        if (M.l(ClusterApi.class).isMaster("reactive_prepare_" + name))
            return new MasterLock("reactive_prepare_" + name);
        return null;
    }

    @Override
    public Lock lockOrNull(UUID caseId) {
        try {
            return M.l(ClusterApi.class)
                    .getLock("reactive_case_" + name + "_" + caseId)
                    .lockWithException(10);
        } catch (TimeoutException e) {
        }
        return null;
    }

    @Override
    public Lock acquireEngineMaster() {
        return M.l(ClusterApi.class).getLock("reactive_engine_" + name).lock();
    }

//    @Override
//    public void releaseEngineMaster() {
//        M.l(ClusterApi.class).getLock("reactive_engine_" + name).unlockHard();
//    }

    @Override
    public boolean isReady() {
        return M.l(ClusterApi.class).isReady();
    }
    
    private class MasterLock implements Lock {

        private String name;

        public MasterLock(String name) {
            this.name = name;
        }

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
            return name;
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
