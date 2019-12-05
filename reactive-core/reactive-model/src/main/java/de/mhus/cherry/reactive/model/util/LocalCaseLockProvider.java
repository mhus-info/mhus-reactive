package de.mhus.cherry.reactive.model.util;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.CaseLockProvider;
import de.mhus.lib.core.M;
import de.mhus.lib.core.base.service.LockManager;
import de.mhus.lib.core.concurrent.Lock;

public class LocalCaseLockProvider implements CaseLockProvider {

    @Override
    public boolean isCaseLocked(UUID caseId) {
        return M.l(LockManager.class).getLock(getClass().getCanonicalName() + "_" + caseId).isLocked();
    }

    @Override
    public Lock lock(UUID caseId) {
        return M.l(LockManager.class).getLock(getClass().getCanonicalName() + "_" + caseId);
    }

}
