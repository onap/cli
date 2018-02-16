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

package org.onap.cli.fw.http.cmd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.Moco;
import com.github.dreamhead.moco.ResponseHandler;
import com.github.dreamhead.moco.Runner;
import org.onap.cli.fw.cmd.OnapCommand;
import org.onap.cli.fw.cmd.OnapCommandType;
import org.onap.cli.fw.conf.OnapCommandConfig;
import org.onap.cli.fw.conf.OnapCommandConstants;
import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.error.OnapCommandExecutionFailed;
import org.onap.cli.fw.error.OnapCommandInvalidSchema;
import org.onap.cli.fw.http.auth.OnapCommandHttpAuthClient;
import org.onap.cli.fw.http.auth.OnapCommandHttpService;
import org.onap.cli.fw.http.conf.OnapCommandHttpConstants;
import org.onap.cli.fw.http.connect.HttpInput;
import org.onap.cli.fw.http.connect.HttpResult;
import org.onap.cli.fw.http.error.OnapCommandFailedMocoGenerate;
import org.onap.cli.fw.http.schema.OnapCommandSchemaHttpLoader;
import org.onap.cli.fw.http.utils.OnapCommandHttpUtils;
import org.onap.cli.fw.input.OnapCommandParameter;
import org.onap.cli.fw.output.OnapCommandResultAttribute;
import org.onap.cli.fw.registrar.OnapCommandRegistrar;
import org.onap.cli.fw.schema.OnapCommandSchema;
import org.onap.cli.fw.schema.OnapCommandSchemaInfo;
import org.onap.cli.fw.utils.OnapCommandDiscoveryUtils;
import org.onap.cli.fw.utils.OnapCommandUtils;
import org.onap.cli.http.mock.MockJsonGenerator;
import org.onap.cli.http.mock.MockRequest;
import org.onap.cli.http.mock.MockResponse;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.json;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonHttpServer;
import static com.github.dreamhead.moco.Runner.runner;
import static com.github.dreamhead.moco.Runner.running;

/**
 * Oclip http Command.
 *
 */
@OnapCommandSchema(type = OnapCommandHttpConstants.HTTP_SCHEMA_PROFILE)
public class OnapHttpCommand extends OnapCommand {

    private HttpInput input = new HttpInput();

    private List<Integer> successStatusCodes = new ArrayList<>();

    private Map<String, String> resultMap = new HashMap<>();

    protected OnapCommandHttpAuthClient authClient;

    private OnapCommandHttpService oclipService = new OnapCommandHttpService();

    // used to run the moco server for verify
    private  Runner runner;

    boolean shouldVerify = false;

    public OnapHttpCommand() {
        super.addDefaultSchemas(OnapCommandHttpConstants.DEFAULT_PARAMETER_HTTP_FILE_NAME);
    }

    public void setInput(HttpInput input) {
        this.input = input;
    }

    @Override
    public String getSchemaVersion() {
        return OnapCommandConstants.OPEN_CLI_SCHEMA_VERSION_VALUE_1_0;
    }

    public void setSuccessStatusCodes(List<Integer> successStatusCodes) {
        this.successStatusCodes = successStatusCodes;
    }

    public void setResultMap(Map<String, String> resultMap) {
        this.resultMap = resultMap;
    }

    public HttpInput getInput() {
        return input;
    }

    public List<Integer> getSuccessStatusCodes() {
        return successStatusCodes;
    }

    public Map<String, String> getResultMap() {
        return resultMap;
    }

    /*
     * Oclip service, this command uses to execute it.
     */
    public OnapCommandHttpService getService() {
        return this.oclipService;
    }

    public void setService(OnapCommandHttpService service) {
        this.oclipService = service;
    }

    @Override
    protected List<String> initializeProfileSchema(Map<String, ?> schemaMap, boolean validate) throws OnapCommandException {
        return OnapCommandSchemaHttpLoader.parseHttpSchema(this, schemaMap, validate);
    }

    @Override
    protected void validate() throws OnapCommandException {
        if (! this.isAuthRequired()) {
            if (this.getParametersMap().containsKey(OnapCommandHttpConstants.DEAFULT_PARAMETER_USERNAME)) {
                this.getParametersMap().get(OnapCommandHttpConstants.DEAFULT_PARAMETER_USERNAME).setOptional(true);
            }
            if (this.getParametersMap().containsKey(OnapCommandHttpConstants.DEAFULT_PARAMETER_PASSWORD)) {
                this.getParametersMap().get(OnapCommandHttpConstants.DEAFULT_PARAMETER_PASSWORD).setOptional(true);
            }
        }

        super.validate();
    }

    private boolean isAuthRequired() {
        return !this.getService().isNoAuth()
                && "false".equals(this.getParametersMap().get(OnapCommandHttpConstants.DEFAULT_PARAMETER_NO_AUTH).getValue())
                && (this.getInfo().getCommandType().equals(OnapCommandType.CMD) ||
                        this.getInfo().getCommandType().equals(OnapCommandType.CATALOG));
    }

    private Optional<OnapCommandParameter> findParameterByName(String parameterName) {
        return this.getParameters().stream()
                .filter(e -> e.getName().equals(parameterName))
                .findFirst();
    }

    @Override
    protected void preRun() throws OnapCommandException {
        Optional<OnapCommandParameter> verifyOpt = this.getParameters().stream()
                .filter(e -> e.getName().equals("verify"))
                .findFirst();
        if(verifyOpt.isPresent()) {
            shouldVerify = verifyOpt.get().getValue().equals("true");
        }

        if (shouldVerify) {
            Optional<OnapCommandParameter> hostUrlParamOpt = findParameterByName("host-url");
            Optional<OnapCommandParameter> noAuthParamOpt = findParameterByName("no-auth");

            if (hostUrlParamOpt.isPresent()) {
                OnapCommandParameter onapCommandParameter = hostUrlParamOpt.get();
                onapCommandParameter.setValue(
                        OnapCommandConfig.getPropertyValue(OnapCommandConstants.VERIFY_MOCO_HOST)
                                + ":" + OnapCommandConfig.getPropertyValue(OnapCommandConstants.VERIFY_MOCO_PORT));
            }

            if (noAuthParamOpt.isPresent()) {
                OnapCommandParameter onapCommandParameter = noAuthParamOpt.get();
                onapCommandParameter.setValue(true);
            }


            // configture moco server

            Map<String, ?> mocoServerConfigs = null;
            try {
                mocoServerConfigs = getMocoServerConfigs(this);
            } catch (IOException e) {
                throw new OnapCommandException("Mocking file is not available in path. ", e);
            }

            HttpServer server = Moco.httpServer(Integer.parseInt(
                    OnapCommandConfig.getPropertyValue(OnapCommandConstants.VERIFY_MOCO_PORT)));

            List<ResponseHandler> responseHandlers = new ArrayList<>();

            if (mocoServerConfigs.containsKey(OnapCommandConstants.VERIFY_RESPONSE_JSON)) {
                responseHandlers.add(Moco.with(mocoServerConfigs.get(OnapCommandConstants.VERIFY_RESPONSE_JSON).toString()));
            }
            responseHandlers.add(Moco.status((Integer) mocoServerConfigs.get(OnapCommandConstants.VERIFY_RESPONSE_STATUS)));

            server.request(Moco.by(Moco.uri((String) mocoServerConfigs.get(OnapCommandConstants.VERIFY_REQUEST_URI))))
                    .response(Moco.header(OnapCommandConstants.VERIFY_CONTENT_TYPE, OnapCommandConstants.VERIFY_CONTENT_TYPE_VALUE),
                            responseHandlers.toArray(new ResponseHandler[responseHandlers.size()]));

            runner = runner(server);
            runner.start();
        }
    }

    private Map<String, ?> getMocoServerConfigs(OnapCommand cmd) throws OnapCommandException, IOException {

        String mockedFile = null;
        HashMap<String, Object> serverConfig = new HashMap();

        Optional<OnapCommandParameter> contextOpt = cmd.getParameters().stream()
                .filter(e -> e.getName().equals("context"))
                .findFirst();

        if (contextOpt.isPresent()) {
            Map<String, String> context = new ObjectMapper().readValue((String) contextOpt.get().getValue(), Map.class);
            mockedFile = context.get("moco");
        }

        Resource resource = OnapCommandDiscoveryUtils.findResource(mockedFile,
                OnapCommandConstants.VERIFY_SAMPLES_DIRECTORY + OnapCommandConstants.JSON_PATTERN);


        List<Map<String, ?>> stringMap = (List<Map<String, ?>>) new Yaml().load(resource.getInputStream());

        if(!stringMap.isEmpty()) {
            Map<String, ?> jsonConfigs = stringMap.get(0);
            Map<String, String> request = (Map<String, String>) jsonConfigs.get(OnapCommandConstants.VERIFY_REQUEST);
            serverConfig.put(OnapCommandConstants.VERIFY_REQUEST_URI, request.get(OnapCommandConstants.VERIFY_REQUEST_URI));

            Map<String, String> response = (Map<String, String>) jsonConfigs.get(OnapCommandConstants.VERIFY_RESPONSE);
            serverConfig.put(OnapCommandConstants.VERIFY_RESPONSE_STATUS, response.get(OnapCommandConstants.VERIFY_RESPONSE_STATUS));

            if(response.get(OnapCommandConstants.VERIFY_RESPONSE_JSON) != null) {
                serverConfig.put(OnapCommandConstants.VERIFY_RESPONSE_JSON,
                        new ObjectMapper().writeValueAsString(response.get(OnapCommandConstants.VERIFY_RESPONSE_JSON)));
            }
        }
        return serverConfig;
    }

    @Override
    protected void postRun() throws OnapCommandException {
        if (shouldVerify) {
            runner.stop();
        }
    }

    @Override
    protected void run() throws OnapCommandException {
        try {
            // For auth/catalog type commands, login and logout logic is not required
            boolean isAuthRequired = this.isAuthRequired();

            this.authClient = new OnapCommandHttpAuthClient(
                    this,
                    this.getResult().isDebug());

            if (isAuthRequired) {
                this.authClient.login();
            }

            this.processRequest();

            if (isAuthRequired) {
                this.authClient.logout();
            }

            if (this.getResult().isDebug() && authClient != null) {
                this.getResult().setDebugInfo(this.authClient.getDebugInfo());
            }
        } catch (OnapCommandException e) {
            if (this.getResult().isDebug() && authClient != null) {
                this.getResult().setDebugInfo(this.authClient.getDebugInfo());
            }
            throw e;
        }
    }

    protected void processRequest() throws OnapCommandException {

        HttpInput httpInput = OnapCommandHttpUtils.populateParameters(this.getParametersMap(), this.getInput());
        httpInput.setUri(this.authClient.getServiceUrl() + httpInput.getUri());

        HttpResult output = this.authClient.run(httpInput);

        this.getResult().setOutput(output);
        if (!this.getSuccessStatusCodes().contains(output.getStatus())) {
            throw new OnapCommandExecutionFailed(this.getName(), output.getBody(), output.getStatus());
        }

        //pre-process result map for spl entries and input parameters
        for (Entry<String, String> resultMap : this.getResultMap().entrySet()) {
            String value = OnapCommandUtils.replaceLineForSpecialValues(resultMap.getValue());
            value = OnapCommandUtils.replaceLineFromInputParameters(value, this.getParametersMap());
            this.resultMap.put(resultMap.getKey(), value);
        }

        Map<String, ArrayList<String>> results = OnapCommandHttpUtils.populateOutputs(this.getResultMap(), output);
        results = OnapCommandUtils.populateOutputsFromInputParameters(results, this.getParametersMap());

        for (OnapCommandResultAttribute attr : this.getResult().getRecords()) {
            attr.setValues(results.get(attr.getName()));
        }
        generateJsonMock(httpInput, output, this.getSchemaName());
    }

    private void generateJsonMock(HttpInput httpInput, HttpResult httpResult, String schemaName)
            throws OnapCommandFailedMocoGenerate {

        if (Boolean.parseBoolean(OnapCommandConfig.getPropertyValue(OnapCommandConstants.SAMPLE_GEN_ENABLED))) {
            try {
                MockRequest mockRequest = new MockRequest();
                mockRequest.setMethod(httpInput.getMethod());
                mockRequest.setUri(httpInput.getUri());
                mockRequest.setHeaders(httpInput.getReqHeaders());
                mockRequest.setJson(httpInput.getBody());

                MockResponse mockResponse = new MockResponse();
                mockResponse.setStatus(httpResult.getStatus());
                mockResponse.setJson(httpResult.getBody());

                MockJsonGenerator.generateMocking(mockRequest, mockResponse,
                        OnapCommandConfig.getPropertyValue(OnapCommandConstants.SAMPLE_GEN_TARGET_FOLDER)
                        + "/" + schemaName.replace(".yaml", "") + "-moco.json");
            } catch (IOException error) {
                throw new OnapCommandFailedMocoGenerate(schemaName, error);
            }
        }
    }
}
