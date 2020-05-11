package de.mhus.cherry.reactive.osgi.impl;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.CaseLockProvider;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.cfg.CfgLong;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.core.service.ClusterApi;
import de.mhus.lib.errors.TimeoutException;

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
        return M.l(ClusterApi.class).getLock("reactive_cleanup_" + name).lock();
    }

    @Override
    public Lock acquirePrepareMaster() {
        return M.l(ClusterApi.class).getLock("reactive_prepare_" + name).lock();
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

}
