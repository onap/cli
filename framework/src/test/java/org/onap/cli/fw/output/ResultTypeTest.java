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

package org.onap.cli.fw.output;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResultTypeTest {
    @Test
    public void resultTypeGetTest() {
        assertTrue(ResultType.TABLE.equals(ResultType.get("table")) && ResultType.CSV.equals(ResultType.get("csv"))
                && ResultType.JSON.equals(ResultType.get("json")) && ResultType.YAML.equals(ResultType.get("yaml"))
                && ResultType.TEXT.equals(ResultType.get("text")));

    }

    @Test
    public void isTabularFormTest() {
        assertTrue(ResultType.isTabularForm("table"));
    }

    @Test
    public void isTabularFormNotTest() {
        assertFalse(ResultType.isTabularForm("text"));
    }
}
