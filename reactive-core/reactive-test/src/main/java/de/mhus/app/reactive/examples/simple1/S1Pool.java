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
package de.mhus.app.reactive.examples.simple1;

import java.util.Date;
import java.util.Map;

import de.mhus.app.reactive.model.annotations.PoolDescription;
import de.mhus.app.reactive.model.annotations.PropertyDescription;
import de.mhus.app.reactive.util.bpmn2.RPool;
import de.mhus.lib.basics.consts.GenerateConst;

@PoolDescription(
        displayName = "Example Pool",
        description = "This pool is used to test the current development",
        indexDisplayNames = {"Text 1", "Text 2", "Created"},
        actorRead = S1ActorWorker.class,
        actorInitiator = S1ActorManager.class)
@GenerateConst
public class S1Pool extends RPool<S1Pool> {

    public enum TEST_ENUM { ON, OFF };
    
    @PropertyDescription(displayName = "Switch Text", writable = false, initial = true)
    private String text1 = "Moin";

    @PropertyDescription(initial = true)
    private String text2 = "";

    @PropertyDescription(initial = true)
    private Date testDate = null;

    @PropertyDescription(initial = true)
    private TEST_ENUM testEnum = null;

    @PropertyDescription(initial = true)
    private int testInt = 0;

    @PropertyDescription(initial = true)
    private Integer testInteger = null;
    
    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    @Override
    protected void checkInputParameters(Map<String, Object> parameters) throws Exception {}

    public String getText2() {
        return text2;
    }

    public void setText2(String in) {
        text2 = in;
    }

    @Override
    public String[] createIndexValues(boolean init) {
        if (init) return new String[] {text1, text2, new Date().toString()};
        return null;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public TEST_ENUM getTestEnum() {
        return testEnum;
    }

    public void setTestEnum(TEST_ENUM testEnum) {
        this.testEnum = testEnum;
    }

    public int getTestInt() {
        return testInt;
    }

    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }

    public Integer getTestInteger() {
        return testInteger;
    }

    public void setTestInteger(Integer testInteger) {
        this.testInteger = testInteger;
    }
}
