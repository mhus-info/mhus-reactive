/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
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
package de.mhus.app.reactive.osgi;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.mhus.app.reactive.engine.Engine;
import de.mhus.app.reactive.engine.util.PoolValidator.Finding;
import de.mhus.app.reactive.model.engine.PEngine;
import de.mhus.app.reactive.model.engine.ProcessLoader;
import de.mhus.lib.errors.MException;
import de.mhus.lib.errors.NotFoundException;

public interface ReactiveAdmin {

    enum STATE_ENGINE {
        STOPPED,
        SUSPENDED,
        WAITING,
        RUNNING
    }

    void startEngine();

    void stopEngine();

    Collection<String> getAvailableProcesses();

    boolean addProcess(String name, ProcessLoader loader);

    void removeProcess(String name);

    List<Finding> deploy(String name, boolean addVersion, boolean activate) throws MException;

    void undeploy(String name) throws MException;

    String getProcessDeployName(String name);

    Engine getEngine();

    PEngine getEnginePersistence();

    void setExecutionSuspended(boolean suspend);

    STATE_ENGINE getEngineStatus();

    String getProcessInfo(String name);

    String addProcess(String[] fileNames, boolean remember) throws FileNotFoundException;

    ProcessLoader getProcessLoader(String name) throws NotFoundException;

    long getProcessDeployTime(String name);

    Date getStartDate();
}
