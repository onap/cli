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

package org.onap.cli.fw.cmd;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.onap.cli.fw.cmd.schema.OnapSchemaRefreshCommand;
import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.output.OnapCommandResultAttribute;

public class OnapSchemaRefreshCommandTest {

    @Test
    public void validateSchemaCommandTest1() throws OnapCommandException {
        OnapSchemaRefreshCommand cmd = new OnapSchemaRefreshCommand();
        cmd.initializeSchema("schema-refresh.yaml");
        cmd.execute();

        List<OnapCommandResultAttribute> oclipCommandResultAttributes = cmd.getResult()
                .getRecords();


        assertTrue(oclipCommandResultAttributes.size() > 1);

    }
}
