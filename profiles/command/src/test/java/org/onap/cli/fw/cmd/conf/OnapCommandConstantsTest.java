/*
 * Copyright 2022 Samsung Electronics
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

package org.onap.cli.fw.cmd.conf;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OnapCommandConstantsTest {
    @Test
    public void test() {
        assertTrue("cmd" == OnapCommandCmdConstants.CMD && "command" == OnapCommandCmdConstants.COMMAND
                && "environment".equals(OnapCommandCmdConstants.ENVIRONMENT)
                && "working_directory".equals(OnapCommandCmdConstants.WD)
                && "result_map".equals(OnapCommandCmdConstants.RESULT_MAP)
                && "output".equals(OnapCommandCmdConstants.OUTPUT)
                && "error".equals(OnapCommandCmdConstants.ERROR)
                && "success_codes".equals(OnapCommandCmdConstants.SUCCESS_EXIT_CODE)
                && "pass_codes".equals(OnapCommandCmdConstants.PASS_CODE)
                && "timeout".equals(OnapCommandCmdConstants.TIMEOUT)
        );
    }
}
