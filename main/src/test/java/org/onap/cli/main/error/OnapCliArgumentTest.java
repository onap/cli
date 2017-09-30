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

package org.onap.cli.main.error;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OnapCliArgumentTest {

    @Test
    public void onapCliArgumentValueMissingTest() {
        OnapCliArgumentValueMissing failed = new OnapCliArgumentValueMissing("Argument value missing");
        assertEquals("0x7001::Value for argument Argument value missing is missing", failed.getMessage());
    }

    @Test
    public void onapCliInvalidArgumentTest() {
        OnapCliInvalidArgument failed = new OnapCliInvalidArgument("Invalid Argument");
        assertEquals("0x7000::Invalid argument Invalid Argument", failed.getMessage());
        failed = new OnapCliInvalidArgument("Invalid Argument", new Exception(""));
        assertEquals("0x7000::Invalid argument Invalid Argument , ", failed.getMessage());
    }

}
