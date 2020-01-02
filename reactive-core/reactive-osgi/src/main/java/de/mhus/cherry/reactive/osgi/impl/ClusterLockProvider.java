package de.mhus.cherry.reactive.osgi.impl;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.CaseLockProvider;
import de.mhus.lib.core.M;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MPeriod;
import de.mhus.lib.core.cfg.CfgLong;
import de.mhus.lib.core.concurrent.Lock;
import de.mhus.lib.errors.TimeoutException;
import de.mhus.osgi.sop.api.cluster.ClusterApi;

public class ClusterLockProvider extends MLog implements CaseLockProvider {

    private static CfgLong CFG_TIMEOUT = new CfgLong(ClusterLockProvider.class,"timeout",MPeriod.MINUTE_IN_MILLISECOUNDS * 5);
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
        return M.l(ClusterApi.class).getLock("reactive_case_" + name + "_" + caseId).lockWithException(CFG_TIMEOUT.value());
    }

}
