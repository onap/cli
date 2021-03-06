/*
 * Copyright 2018 Huawei Technologies Co., Ltd.
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

/**
 * OnapCommandCmdConstants.
 *
 */
public class OnapCommandCmdConstants {
    public static final String CONF = "open-cli-cmd.properties";

    //cmd
    public static final String CMD = "cmd";
    public static final String COMMAND = "command";
    public static final String ENVIRONMENT = "environment";
    public static final String WD = "working_directory";
    public static final String RESULT_MAP = "result_map";
    public static final String OUTPUT = "output";
    public static final String ERROR = "error";
    public static final String SUCCESS_EXIT_CODE = "success_codes";
    public static final String PASS_CODE = "pass_codes";

    public static final String CMD_MANDATORY_SECTIONS = "cli.schema.cmd.sections.mandatory";
    public static final String CMD_SECTIONS = "cli.schema.cmd.sections";

    public static final String DEFAULT_PARAMETER_CMD_FILE_NAME = "default_input_parameters_cmd.yaml";
    public static final String TIMEOUT = "timeout";
    private OnapCommandCmdConstants() {
        //as per coding standard !
    }
}


