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
    public boolean acquireCleanupMaster(long until) {
        return M.l(ClusterApi.class).isMaster("reactive_cleanup_" + name);
    }

    @Override
    public boolean acquirePrepareMaster(long until) {
        return M.l(ClusterApi.class).isMaster("reactive_prepare_" + name);
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
}
