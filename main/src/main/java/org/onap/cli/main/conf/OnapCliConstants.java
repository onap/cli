/*
 * Copyright 2017-18 Huawei Technologies Co., Ltd.
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

package org.onap.cli.main.conf;

public final class OnapCliConstants {

    public static final String PARAM_HELP_SHORT = "h";
    public static final String PARAM_HELP_LOGN = "help";

    public static final String PARAM_VERSION_SHORT = "v";
    public static final String PARAM_VERSION_LONG = "version";

    public static final String PARAM_PROFILE_SHORT = "c";
    public static final String PARAM_PROFILE_LONG = "profile";

    public static final String PARAM_PARAM_FILE_SHORT = "p";
    public static final String PARAM_PARAM_FILE_LONG = "param-file";

    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_FAILURE = 1;

    public static final String PARAM_INTERACTIVE_PROMPT = "oclip";
    public static final String PARAM_INTERACTIVE_EXIT = "exit";
    public static final String PARAM_INTERACTIVE_EXIT_MSG = "To exit from the session.";
    public static final String PARAM_INTERACTIVE_CLEAR = "clear";
    public static final String PARAM_INTERACTIVE_CLEAR_MSG = "To clear the screen";
    public static final String PARAM_INTERACTIVE_USE = "use";
    public static final String PARAM_INTERACTIVE_USE_MSG = "To set the current product version, more details please check version";
    public static final String PARAM_INTERACTIVE_HELP = "help";
    public static final String PARAM_INTERACTIVE_HELP_MSG = "To get the help details of supported commands";
    public static final String PARAM_INTERACTIVE_VERSION = PARAM_VERSION_LONG;
    public static final String PARAM_INTERACTIVE_VERSION_MSG = "To see the version details";
    public static final String PARAM_INTERACTIVE_SET = "set";
    public static final String PARAM_INTERACTIVE_SET_MSG = "To set the parameter values. Once its set, will be available for all commands in current session.";
    public static final String PARAM_INTERACTIVE_UNSET = "unset";
    public static final String PARAM_INTERACTIVE_UNSET_MSG = "To unset the parameter value in current session.";
    public static final String PARAM_INTERACTIVE_PROFILE = PARAM_PROFILE_LONG;
    public static final String PARAM_INTERACTIVE_PROFILE_MSG = "Start profiling current settings made of use, set.";

    public static final String PARAM_INTERACTIVE_ARG_SPLIT_PATTERN = "\\s+";

    private OnapCliConstants(){}
}
