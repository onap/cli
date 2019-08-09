/*
 * Copyright 2019 Huawei Technologies Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.cli.fw.cmd.execution;

import org.junit.Test;
import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.store.OnapCommandExecutionStoreTest;

import static org.junit.Assert.*;

public class OnapCommandExceutionShowDebugCommandTest {
    public static void setUp() throws Exception {
        OnapCommandExecutionStoreTest executionStoreTest= new OnapCommandExecutionStoreTest();
        executionStoreTest.setUp();
        executionStoreTest.listExecutionsTest();
        executionStoreTest.storeExectutionDebugTest();
        executionStoreTest.storeExectutionEndTest();
        executionStoreTest.storeExectutionOutputTest();
        executionStoreTest.storeExectutionProgressTest();
        executionStoreTest.storeExectutionStartTest();
    }
    @Test
    public void runTest() throws OnapCommandException {
        OnapCommandExceutionShowDebugCommand cmd=new OnapCommandExceutionShowDebugCommand();
        cmd.initializeSchema("execution-show-debug.yaml");
        cmd.getParametersMap().get("execution-id").setValue("requestId");
        cmd.execute();
        assertNotNull(cmd.getResult().getOutput());

    }

}