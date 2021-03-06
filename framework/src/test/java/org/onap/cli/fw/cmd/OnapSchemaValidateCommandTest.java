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

import org.junit.Ignore;
import org.junit.Test;
import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.registrar.OnapCommandRegistrar;
import org.onap.cli.fw.schema.ValidateSchemaTest;

import org.onap.cli.fw.output.OnapCommandResult;
import static org.junit.Assert.assertNotNull;

public class OnapSchemaValidateCommandTest {

    @Ignore
    @Test
    public void validateSchemaCommandTest1() throws OnapCommandException { //NOSONAR
        OnapCommand cmd = OnapCommandRegistrar.getRegistrar().get("schema-validate");
        cmd.getParametersMap().get("schema-location").setValue("schema-validate-pass.yaml");
        cmd.getParametersMap().get("internal-schema").setValue("true");
        cmd.execute();
        OnapCommandResult onapCommandResult = cmd.execute();
        assertNotNull(onapCommandResult.getOutput());
    }

    @Ignore
    @Test
    public void validateSchemaCommandTest2() throws OnapCommandException { //NOSONAR
        OnapCommand cmd = OnapCommandRegistrar.getRegistrar().get("schema-validate");
        cmd.getParametersMap().get("schema-location").setValue(
                ValidateSchemaTest.class.getClassLoader().getResource("schema-validate-pass.yaml").getFile());
        cmd.getParametersMap().get("internal-schema").setValue("true");
        cmd.execute();
        OnapCommandResult onapCommandResult = cmd.execute();
        assertNotNull(onapCommandResult);
    }
}
