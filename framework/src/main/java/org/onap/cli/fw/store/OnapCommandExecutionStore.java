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

package org.onap.cli.fw.store;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.onap.cli.fw.conf.OnapCommandConfig;
import org.onap.cli.fw.conf.OnapCommandConstants;
import org.onap.cli.fw.error.OnapCommandExecutionFailed;
import org.onap.cli.fw.error.OnapCommandExecutionNotFound;
import org.onap.cli.fw.utils.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnapCommandExecutionStore {
    private static Logger log = LoggerFactory.getLogger(OnapCommandExecutionStore.class);

    private static boolean storeReady = false; // NOSONAR
    private static final String REQUEST_ID = "requestId";
    private static final String EXECUTION_ID = "executionId";
    private static final String INPUT = "input";
    private static final String STDOUT = "stdout";
    private static final String STDERR = "stderr";
    private static final String DEBUG = "debug";
    private static final String IN_PROGRESS = "in-progress";
    private static final String OUTPUT = "output";
    private static final String ERROR = "error";
    private static final String COMPLETED = "completed";
    private static final String FAILED = "failed";
    private static final String EXECUTIONID = "execution-id";
    private static final String REQUESTID = "request-id";
    private static final String OS_NAME = "os.name";
    private static final String WINDOWS = "windows";

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

    private static final String SEPARATOR = "__";

    private enum SearchMode {
        FIND,
        FILE //for developer mode


    }

    private static SearchMode searchMode = SearchMode.FILE;
    static {
        String mode = OnapCommandConfig.getPropertyValue(OnapCommandConstants.OPEN_CLI_EXECUTION_SEARCH_MODE);
        if (mode.equalsIgnoreCase(SearchMode.FIND.name()))
            searchMode = SearchMode.FIND;
    }

    public static class ExecutionStoreContext {
        private String requestId;
        private String executionId;
        private String profile;
        private String storePath;
        public String getExecutionId() {
            return executionId;
        }
        public ExecutionStoreContext setExecutionId(String executionId) {
            this.executionId = executionId;
            return this;
        }
        public String getStorePath() {
            return storePath;
        }
        public ExecutionStoreContext setStorePath(String storePath) {
            this.storePath = storePath;
            return this;
        }
        public String getRequestId() {
            return requestId;
        }
        public ExecutionStoreContext setRequestId(String requestId) {
            this.requestId = requestId;
             return this;
        }
        public String getProfile() {
            return profile;
        }
        public void setProfile(String profile) {
            this.profile = profile;
        }
    }

    public static class Execution {
        private String id;
        private String requestId;
        private String status;
        private String startTime;
        private String endTime;
        private String input;
        private String output;
        private String profile;
        private String command;
        private String product;
        private String service;

        public String getInput() {
            return input;
        }
        public void setInput(String input) {
            this.input = input;
        }
        public String getOutput() {
            return output;
        }
        public void setOutput(String output) {
            this.output = output;
        }
        public String getProfile() {
            return profile;
        }
        public void setProfile(String profile) {
            this.profile = profile;
        }
        public String getCommand() {
            return command;
        }
        public void setCommand(String command) {
            this.command = command;
        }
        public String getProduct() {
            return product;
        }
        public void setProduct(String product) {
            this.product = product;
        }
        public String getService() {
            return service;
        }
        public void setService(String service) {
            this.service = service;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getEndTime() {
            return endTime;
        }
        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        public void setStartTime(String timeOfExecution) {
            this.startTime = timeOfExecution;
        }
        public String getStartTime() {
            return startTime;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getRequestId() {
            return requestId;
        }
        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }
    }

    static {
        try {
            FileUtils.forceMkdir(new File(getBasePath()));
            storeReady = true;
        } catch (IOException e) {
            log.error("Failed to create the data store results");
        }
    }

    private static OnapCommandExecutionStore store = null;

    private OnapCommandExecutionStore() {
        this.dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static OnapCommandExecutionStore getStore() {
        if (store == null) {
            store = new OnapCommandExecutionStore();
        }

        return store;
    }

    private static String getBasePath() {
        return OnapCommandConfig.getPropertyValue(OnapCommandConstants.OPEN_CLI_DATA_DIR) +
                File.separator + "executions";
    }

    public ExecutionStoreContext storeExectutionStart(
            String requestId, String product, String service, String cmd, String profile, String input) {

        ExecutionStoreContext context = new ExecutionStoreContext();
        context.setRequestId(requestId);

        String executionId = requestId + "-" + System.currentTimeMillis();
        context.setExecutionId(executionId);

        String storePath = getBasePath() + File.separator + executionId + SEPARATOR + product +
                SEPARATOR + service +
                SEPARATOR + cmd +
                SEPARATOR + (profile != null ? profile : "" );

        try {
            File dir = new File(storePath);
            FileUtils.forceMkdir(dir);
            context.setStorePath(dir.getAbsolutePath());

            if (product != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + OnapCommandConstants.INFO_PRODUCT), product, (Charset) null);
            if (service != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + OnapCommandConstants.INFO_SERVICE), service, (Charset) null);
            if (cmd != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + OnapCommandConstants.RPC_CMD), cmd, (Charset) null);

            FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + REQUEST_ID), requestId, (Charset) null);

            FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + EXECUTION_ID), executionId, (Charset) null);

            if (input != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + INPUT), input, (Charset) null);
            if (profile != null) {
                context.setProfile(profile);
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + OnapCommandConstants.RPC_PROFILE), profile, (Charset) null);
            }

            FileUtils.touch(new File(context.getStorePath() + File.separator + STDOUT));
            FileUtils.touch(new File(context.getStorePath() + File.separator + STDERR));
            FileUtils.touch(new File(context.getStorePath() + File.separator + DEBUG));

            FileUtils.touch(new File(context.getStorePath() + File.separator + IN_PROGRESS));
        } catch (IOException e) {
            log.error("Failed to store the execution start details {}", storePath);
        }

        return context;
    }

    public void storeExectutionEnd(
            ExecutionStoreContext context,
            String output, String error, String debug, boolean passed) {

        try {
            if (output != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + OUTPUT), output, (Charset) null);
            if (error != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + ERROR), error, (Charset) null);
            if (debug != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + DEBUG), debug, (Charset) null);
            if (passed)
                FileUtils.touch(new File(context.getStorePath() + File.separator + COMPLETED));
            else
                FileUtils.touch(new File(context.getStorePath() + File.separator + FAILED));
            Path path= Paths.get(context.getStorePath() + File.separator + IN_PROGRESS);
            deleteFile(context, path);
        } catch (IOException e) {
            log.error("Failed to store the execution end details {}", context.storePath);
        }
    }

    private void deleteFile(ExecutionStoreContext context, Path path){
        try {
            Files.delete(path);
        } catch (IOException e) {
            String contextPath = context.getStorePath() + File.separator + IN_PROGRESS;
            log.error("Failed to delete {}", contextPath);
        }
    }

    public void storeExectutionProgress(
            ExecutionStoreContext context,
            String output, String error, String debug) {

        try {
            if (output != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + OUTPUT), output, (Charset) null);
            if (error != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + ERROR), error, (Charset) null);
            if (debug != null)
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + DEBUG), debug, (Charset) null);
        } catch (IOException e) {
            log.error("Failed to store the execution end details {}", context.storePath);
        }
    }

    public void storeExectutionDebug(
            ExecutionStoreContext context,
            String debug) {

        try {
            if (debug != null) {
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + DEBUG), debug, (Charset) null);
            }
        } catch (IOException e) {
            log.error("Failed to store the execution debug details {}", context.storePath);
        }
    }

    public void storeExectutionOutput(
            ExecutionStoreContext context,
            String output) {

        try {
            if (output != null) {
                FileUtils.writeStringToFile(new File(context.getStorePath() + File.separator + OUTPUT), output, (Charset) null);
            }
        } catch (IOException e) {
            log.error("Failed to store the execution output details {}", context.storePath);
        }
    }

    public List <String> listExecutionsWindows(Map<String, String> search, List <String> dirs){
        for (File f: new File(getBasePath()).listFiles()) {
            if(search.containsKey(EXECUTIONID)) {
                if (f.getName().startsWith(search.get(EXECUTIONID)))
                    dirs.add(f.getAbsolutePath());

                continue;
            }

            if(search.containsKey(REQUESTID)) {
                if (f.getName().startsWith(search.get(REQUESTID)))
                    dirs.add(f.getAbsolutePath());

            }

            else
                dirs.add(f.getAbsolutePath());
        }
        return dirs;
    }

    public List <String> searchAndListExecutions(Map<String, String> search, List <String> dirs) throws OnapCommandExecutionFailed, IOException, InterruptedException {
        StringBuilder searchString = new StringBuilder("find " + new File(getBasePath()).getAbsolutePath() + " -type d ");

        String startTime = search.get("startTime");
        if (startTime != null) {
            searchString.append(" -newermt " + startTime);
        }

        String endTime = search.get("endTime");
        if (endTime != null) {
            searchString.append(" ! -newermt " + endTime);
        }

        searchString.append(" -name \"");

        if(search.containsKey(EXECUTIONID)) {
            searchString.append(search.get(EXECUTIONID));
        } else if(search.containsKey(REQUESTID)) {
            searchString.append(search.get(REQUESTID) + "*");
        } else {
            searchString.append("*");
        }

        for (String term: Arrays.asList("product", "service", "command", "profile")) {
            searchString.append("__");
            if (search.get(term) != null && !search.get(term).isEmpty()) {
                searchString.append(search.get(term));
            } else {
                searchString.append("*");
            }
        }
        if (!searchString.toString().endsWith("*"))
            searchString.append("*");

        searchString.append("\"");

        ProcessRunner pr = new ProcessRunner(new String [] {searchString.toString()}, null, ".");
        pr.setTimeout(10000);
        pr.overrideToUnix();
        pr.run();
        if (pr.getExitCode() != 0) {
            throw new OnapCommandExecutionFailed("System failed to search the executions with error " + pr.getError());
        }

        if (!pr.getOutput().trim().isEmpty())
            dirs = Arrays.asList(pr.getOutput().split("\\r?\\n"));

        return dirs;
    }

    public List<OnapCommandExecutionStore.Execution> listExecutions(Map<String, String> search) throws OnapCommandExecutionFailed {
        List <OnapCommandExecutionStore.Execution> list = new ArrayList<>();

        try {
            List <String> dirs = new ArrayList<>();
            if (System.getProperty(OS_NAME).toLowerCase().startsWith(WINDOWS) || searchMode.equals(SearchMode.FILE)) {
                dirs = listExecutionsWindows(search, dirs);
            } else {
                //find results -type d -newermt '2019-02-11 10:00:00' ! -newermt '2019-02-11 15:10:00' -name "*__*__profile-list*"
                //find 'results' -type d -newermt '2019-02-11T10:00:00.000' ! -newermt '2019-02-11T15:10:00.000' -name "*__*__profile*"
                dirs = searchAndListExecutions(search, dirs);
            }

            for (String dir: dirs) {
                list.add(this.makeExecution(dir));
            }
        } catch (Exception e) {// NOSONAR
            throw new OnapCommandExecutionFailed(e, "Failed to search the executions");
        }

        return list;
    }

    private Execution makeExecution(String executionStorePath) throws IOException {
        OnapCommandExecutionStore.Execution exectuion = new OnapCommandExecutionStore.Execution();
        if (new File(executionStorePath + File.separator + REQUEST_ID).exists())
            exectuion.setRequestId(FileUtils.readFileToString(new File(executionStorePath + File.separator + REQUEST_ID), (Charset) null));
        if (new File(executionStorePath + File.separator + EXECUTION_ID).exists())
            exectuion.setId(FileUtils.readFileToString(new File(executionStorePath + File.separator + EXECUTION_ID), (Charset) null));
        exectuion.setProduct(FileUtils.readFileToString(new File(executionStorePath + File.separator + OnapCommandConstants.INFO_PRODUCT), (Charset) null));
        exectuion.setService(FileUtils.readFileToString(new File(executionStorePath + File.separator + OnapCommandConstants.INFO_SERVICE), (Charset) null));
        exectuion.setCommand(FileUtils.readFileToString(new File(executionStorePath + File.separator + OnapCommandConstants.RPC_CMD), (Charset) null));
        if (new File(executionStorePath + File.separator + OnapCommandConstants.RPC_PROFILE).exists())
            exectuion.setProfile(FileUtils.readFileToString(new File(executionStorePath + File.separator + OnapCommandConstants.RPC_PROFILE), (Charset) null));

        exectuion.setInput(FileUtils.readFileToString(new File(executionStorePath + File.separator + INPUT), (Charset) null));
        exectuion.setStartTime(dateFormatter.format(new File(executionStorePath + File.separator + INPUT).lastModified()));

        if (new File(executionStorePath + File.separator + IN_PROGRESS).exists()) {
            exectuion.setStatus(IN_PROGRESS);
        } else if (new File(executionStorePath + File.separator + COMPLETED).exists()) {
            exectuion.setStatus(COMPLETED);
            if (new File(executionStorePath + File.separator + OUTPUT).exists()) {
                exectuion.setOutput(FileUtils.readFileToString(new File(executionStorePath + File.separator + OUTPUT), (Charset) null));
                exectuion.setEndTime(dateFormatter.format(new File(executionStorePath + File.separator + OUTPUT).lastModified()));
            }
        } else if (new File(executionStorePath + File.separator + FAILED).exists()) {
            exectuion.setStatus(FAILED);
            if (new File(executionStorePath + File.separator + ERROR).exists()) {
                exectuion.setOutput(FileUtils.readFileToString(new File(executionStorePath + File.separator + ERROR), (Charset) null));
                exectuion.setEndTime(dateFormatter.format(new File(executionStorePath + File.separator + ERROR).lastModified()));
            }
        }

        return exectuion;
    }

    private File getExecutionDir(String executionId) throws OnapCommandExecutionNotFound {
        File []f =  new File(getBasePath()).listFiles((dir, name) -> name.startsWith(executionId));

        if (f.length == 0) {
            throw new OnapCommandExecutionNotFound(executionId);
        }

        return f[0];
    }

    public String showExecutionOut(String executionId) throws OnapCommandExecutionNotFound {
        try {
            return FileUtils.readFileToString(new File (this.getExecutionDir(executionId).getAbsolutePath() + File.separator + STDOUT), (Charset) null);
        } catch (IOException e) {
            return "";
        }
    }

    public String showExecutionErr(String executionId) throws OnapCommandExecutionNotFound {
        try {
            return FileUtils.readFileToString(new File (this.getExecutionDir(executionId).getAbsolutePath() + File.separator + STDERR), (Charset) null);
        } catch (IOException e) {
            return "";
        }
    }

    public String showExecutionDebug(String executionId) throws OnapCommandExecutionNotFound {
        try {
            return FileUtils.readFileToString(new File (this.getExecutionDir(executionId).getAbsolutePath() + File.separator + DEBUG), (Charset) null);
        } catch (IOException e) {
            return "";
        }
    }
    public Execution getExecution(String executionId) throws OnapCommandExecutionNotFound, OnapCommandExecutionFailed {
        try {
            return this.makeExecution(this.getExecutionDir(executionId).getAbsolutePath());
        } catch (IOException e) {
            throw new OnapCommandExecutionFailed(e, "Failed to retrieve the execution");
        }
    }
}