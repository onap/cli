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

import org.onap.cli.fw.cmd.OnapCommand;
import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.schema.OnapCommandSchema;
import org.onap.cli.fw.store.OnapCommandExecutionStore;

@OnapCommandSchema(schema = "execution-show-out.yaml")
public class OnapCommandExceutionShowOutCommand extends OnapCommand {

    @Override
    protected void run() throws OnapCommandException {
        String executionId = getParametersMap().get("execution-id").getValue().toString();
        this.getResult().setOutput(OnapCommandExecutionStore.getStore().showExecutionOut(executionId));
    }
}
