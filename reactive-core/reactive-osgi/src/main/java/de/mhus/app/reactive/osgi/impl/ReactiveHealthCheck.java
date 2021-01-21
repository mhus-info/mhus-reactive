/**
 * Copyright (C) 2018 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.app.reactive.osgi.impl;

import org.apache.felix.hc.annotation.HealthCheckService;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.felix.hc.api.HealthCheck;
import org.apache.felix.hc.api.Result;
import org.osgi.service.component.annotations.Component;

import de.mhus.app.reactive.engine.Engine;
import de.mhus.app.reactive.osgi.ReactiveAdmin;

// https://github.com/apache/felix/tree/trunk/healthcheck

@HealthCheckService(
        name = "ReactiveHealthCheck",
        tags = {"systemalive"})
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
                if (admin.getEngineStatus() != ReactiveAdmin.STATE_ENGINE.RUNNING)
                    log.warn("Engine is not Running, Status {}", admin.getEngineStatus());
                else
                    log.debug(
                            "Status {} Rounds {}",
                            admin.getEngineStatus(),
                            engine.getStatisticRounds());
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
