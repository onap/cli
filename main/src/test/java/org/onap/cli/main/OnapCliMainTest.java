/*
 * Copyright 2017 Huawei Technologies Co., Ltd.
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

package org.onap.cli.main;

import static org.junit.Assert.fail;

import java.io.IOException;


import jline.console.ConsoleReader;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Test;
import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.error.OnapCommandHelpFailed;
import org.onap.cli.fw.registrar.OnapCommandRegistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import org.onap.cli.fw.error.OnapCommandInvalidSchema;
import org.onap.cli.fw.utils.OnapCommandDiscoveryUtils;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class OnapCliMainTest {

    OnapCli cli = null;

    private void handle(String[] args) {
        cli = new OnapCli(args);
        cli.handle();
    }

    @Test
    public void testHelp() {
        assertDoesNotThrow(() -> this.handle(new String[] { "--help" }));
    }

    @Test
    public void testHelpShort() {
        assertDoesNotThrow(() -> this.handle(new String[] { "-h" }));
    }

    @Test
    public void testVersion() {
        assertDoesNotThrow(() -> this.handle(new String[] { "--version" }));
    }

    @Test
    public void testVersionShort() {
        assertDoesNotThrow(() -> this.handle(new String[] { "-v" }));
    }

    @Test
    public void testHelpSampleCommand() {
        assertDoesNotThrow(() -> this.handle(new String[] { "sample-test", "--help" }));
    }

    @Test
    public void testHelpSampleCommandShort() {
        assertDoesNotThrow(() -> this.handle(new String[] { "sample-test", "-h" }));
    }

    @Test
    public void testVersionSampleCommandShort() {
        assertDoesNotThrow(() -> this.handle(new String[] { "sample-test", "-v" }));
    }

    @Test
    public void testHandleSampleCommandSet() throws OnapCommandException {
        OnapCommandRegistrar.getRegistrar().addParamCache("sample:string-param", "paramValue");
        OnapCommandRegistrar.getRegistrar().addParamCache("host-username", "paramValue");
        OnapCommandRegistrar.getRegistrar().addParamCache("host-password", "paramValue");
        OnapCommandRegistrar.getRegistrar().addParamCache("host-url", "paramValue");
        assertDoesNotThrow(() -> this.handle(new String[] { "sample-test", "--string-param", "test"}));
    }

    @Test
    public void testHandleSampleCommandFailure() throws OnapCommandException {
        assertDoesNotThrow(() -> this.handle(new String[] { "sample-test", "--string-param"}));
    }

    @Test
    public void interactiveTest() {
        cli = new OnapCli(new String[] {});

        mockConsole("exit");
        cli.handleInteractive();

        mockConsole("clear");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("sample-test -h");

        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("use open-cli");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("set a=b");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("set");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("set a=");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("unset a");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("profile test");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("profile");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("version");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsole("help");
        try {
            cli.handleInteractive();
        } catch (Exception e) {
        }

        cli = new OnapCli(new String[] {});
        mockConsoleReader();
        assertDoesNotThrow(() -> cli.handleInteractive());

    }

    private static void mockConsoleReader() {
        new MockUp<OnapCli>() {
            @Mock
            public ConsoleReader createConsoleReader() throws IOException {
                throw new IOException("Exception mock");
            }
        };
    }

    private static void mockConsole(String input) {
        new MockUp<ConsoleReader>() {
            boolean isMock = true;

            @Mock
            public String readLine(Invocation inv) throws IOException {
                if (isMock) {
                    isMock = false;
                    return input;
                } else {
                    return inv.proceed(input);
                }
            }
        };
    }

    @Test
    public void testDirectiveHelp() {
        try {
            OnapCli.getDirectiveHelp();
        } catch (OnapCommandHelpFailed e) {
            fail("Directive help failed to run");
        }
    }
    @Test
    public void testLoadYamlForYamlReader() throws OnapCommandInvalidSchema {
        Map<String,?> map = OnapCommandDiscoveryUtils.loadYaml("src/test/resources/open-cli-schema/sample-test-schema.yaml");
        assertFalse(map.isEmpty());
    }
    @Test
    public void testverifyCommand() throws OnapCommandException {
        cli = new OnapCli(new String[] {"schema-validate","--verify" });
      new MockUp<OnapCommandRegistrar>(){
            @Mock
            public List<Map<String, Object>> getTestSuite(String cmd, String product) throws OnapCommandException {
                List<Map<String, Object>> list=new ArrayList<>();
                Map<String,Object>map=new HashMap<>();
                map.put("output","output");
                map.put("input", Arrays.asList(new String[]{"--verify"}));
                map.put("sampleid","sample1");
                map.put("samplefileid","schema-validate-sample.yaml");
                map.put("moco","schema-validate-moco.json");
                assertDoesNotThrow(() -> list.add(map));
                return list;
            }
        };
        cli.handleCommand();
    }
}
