package de.mhus.cherry.reactive.osgi.impl;

import org.apache.felix.hc.annotation.HealthCheckService;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.felix.hc.api.HealthCheck;
import org.apache.felix.hc.api.Result;
import org.osgi.service.component.annotations.Component;

import de.mhus.cherry.reactive.engine.Engine;
import de.mhus.cherry.reactive.osgi.ReactiveAdmin;

// https://github.com/apache/felix/tree/trunk/healthcheck

@HealthCheckService(name = "ReactiveHealthCheck", tags = {"systemalive"})
@Component(immediate = true)
public class ReactiveHealthCheck implements HealthCheck {

    @Override
    public Result execute() {
        FormattingResultLog log = new FormattingResultLog();

        ReactiveAdminImpl admin = ReactiveAdminImpl.instance;
        if (admin == null) {
            log.critical("admin not found");
        } else {
            Engine engine = admin.getEngine();
            if (engine == null) {
                log.critical("engine not found");
            } else {
                if (admin.getEngineStatus() !=  ReactiveAdmin.STATE_ENGINE.RUNNING)
                    log.warn("Engine is not Running, Status {}", admin.getEngineStatus());
                else
                    log.debug("Status {} Rounds {}", admin.getEngineStatus(), engine.getStatisticRounds() );
            }
        }
          
//        log.info("Checking my context {}", myContextObject);
//        if(myContextObject.retrieveStatus() != ...expected value...) {
//            log.warn("Problem with ...");
//        }
//        if(myContextObject.retrieveOtherStatus() != ...expected value...) {
//            log.critical("Cricital Problem with ...");
//        }

        return new Result(log);
    }
}
