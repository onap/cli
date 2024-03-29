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

package org.onap.cli.fw.http.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.error.OnapCommandInvalidParameterValue;
import org.onap.cli.fw.error.OnapCommandParameterNotFound;
import org.onap.cli.fw.error.OnapCommandResultEmpty;
import org.onap.cli.fw.error.OnapCommandResultMapProcessingFailed;
import org.onap.cli.fw.http.conf.OnapCommandHttpConstants;
import org.onap.cli.fw.http.connect.HttpInput;
import org.onap.cli.fw.http.connect.HttpResult;
import org.onap.cli.fw.http.error.OnapCommandHttpHeaderNotFound;
import org.onap.cli.fw.http.error.OnapCommandHttpInvalidRequestBody;
import org.onap.cli.fw.http.error.OnapCommandHttpInvalidResponseBody;
import org.onap.cli.fw.input.OnapCommandParameter;
import org.onap.cli.fw.input.OnapCommandParameterType;
import org.onap.cli.fw.utils.OnapCommandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import net.minidev.json.JSONArray;

public class OnapCommandHttpUtils {

    static Logger log = LoggerFactory.getLogger(OnapCommandHttpUtils.class);
    private static Gson gson = new GsonBuilder().serializeNulls().create();
    private OnapCommandHttpUtils() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Set argument to param value.
     *
     * @param params
     *            map
     * @param input
     *            HttpInput
     * @return HttpInput
     * @throws OnapCommandParameterNotFound
     *             exception
     * @throws OnapCommandInvalidParameterValue
     *             exception
     */
    public static HttpInput populateParameters(Map<String, OnapCommandParameter> params, HttpInput input)
            throws OnapCommandException {

        HttpInput inp = new HttpInput();

        for (OnapCommandParameter param : params.values()) {
            if (OnapCommandParameterType.BINARY.equals(param.getParameterType())) {
                inp.setBinaryData(true);
                break;
            }
        }

        inp.setUri(OnapCommandUtils.replaceLineForSpecialValues(input.getUri()));
        inp.setUri(OnapCommandUtils.replaceLineFromInputParameters(input.getUri(), params));

        inp.setMethod(input.getMethod().toLowerCase());

        boolean isRemoveEmptyNodes = Boolean.parseBoolean(input.getContext().getOrDefault(OnapCommandHttpConstants.CONTEXT_REMOVE_EMPTY_JSON_NODES, "false"));

        //Process for md5
        Map <String, String> values = new HashMap<>();
        for (Map.Entry<String, OnapCommandParameter> param: params.entrySet()) {
            values.put(param.getKey(), param.getValue().getValue().toString());
        }

        if (!input.getMultiparts().isEmpty()) {
            for (HttpInput.Part part: input.getMultiparts()) {
                part.setContent(OnapCommandUtils.replaceLineForSpecialValues(part.getContent(), values));
                part.setContent(OnapCommandUtils.replaceLineFromInputParameters(part.getContent(), params));
                if (isRemoveEmptyNodes) {
                    part.setContent(OnapCommandHttpUtils.normalizeJson(part.getContent()));
                }
            }

            inp.setMultiparts(input.getMultiparts());
        } else {
            inp.setMultipartEntityName(input.getMultipartEntityName());
            inp.setBody(OnapCommandUtils.replaceLineForSpecialValues(input.getBody(), values));
            inp.setBody(OnapCommandUtils.replaceLineFromInputParameters(inp.getBody(), params));
            if (isRemoveEmptyNodes) {
                inp.setBody(OnapCommandHttpUtils.normalizeJson(inp.getBody()));
            }
        }

        //consider __body__ spl entry
        values.put(OnapCommandHttpConstants.__BODY__, inp.getBody());

        for (String h : input.getReqHeaders().keySet()) {
            String value = input.getReqHeaders().get(h);
            value = OnapCommandUtils.replaceLineForSpecialValues(value, values);
            inp.getReqHeaders().put(h, OnapCommandUtils.replaceLineFromInputParameters(value, params));
        }

        for (String h : input.getReqQueries().keySet()) {
            String value = input.getReqQueries().get(h);
            value = OnapCommandUtils.replaceLineForSpecialValues(value, values);
            inp.getReqQueries().put(h, OnapCommandUtils.replaceLineFromInputParameters(value, params));
        }

        return inp;
    }

    /**
     * Populate result.
     *
     * @param resultMap
     *            map
     * @param resultHttp
     *            HttpResult
     * @return map
     * @throws OnapCommandHttpHeaderNotFound
     *             header not found exception
     * @throws OnapCommandHttpInvalidResponseBody
     *             invalid response body exception
     * @throws OnapCommandResultMapProcessingFailed
     *             map processing failed exception
     */
    public static Map<String, List<String>> populateOutputs(Map<String, String> resultMap, HttpResult resultHttp)
            throws OnapCommandException {
        Map<String, List<String>> resultsProcessed = new HashMap<>();

        for (Entry<String, String> entry : resultMap.entrySet()) {
            String key = entry.getKey();
            try {
                resultsProcessed.put(key, OnapCommandHttpUtils.replaceLineFromOutputResults(resultMap.get(key), resultHttp));
            } catch(OnapCommandResultEmpty e) {  // NOSONAR
                // pass
            }
        }

        return resultsProcessed;
    }

    public static List<String> replaceLineFromOutputResults(String line, HttpResult resultHttp)
            throws OnapCommandHttpHeaderNotFound, OnapCommandHttpInvalidResponseBody,
            OnapCommandResultMapProcessingFailed, OnapCommandResultEmpty {
        StringBuilder headerProcessedLine = new StringBuilder();

        ArrayList<String> result = new ArrayList<>();
        if (!line.contains("$b{") && !line.contains("$h{")) {
            result.add(line);
            return result;
        }

        /**
         * In case of empty response body [] or {}
         **/
        if (resultHttp.getBody() != null && resultHttp.getBody().length() <= 2) {
            return result;
        }

        /**
         * Process headers macros : line: $h{abc}-$b{$.[*].xyz} , After processing line will be [abc's
         * value]-$b{$.[*].xyz}
         **/
        int currentIdx = 0;
        while (currentIdx < line.length()) {
            int idxS = line.indexOf("$h{", currentIdx);
            if (idxS == -1) {
                headerProcessedLine.append(line.substring(currentIdx));
                break;
            }
            int idxE = line.indexOf('}', idxS);
            String headerName = line.substring(idxS + 3, idxE);
            headerName = headerName.trim();
            if (!resultHttp.getRespHeaders().containsKey(headerName)) {
                throw new OnapCommandHttpHeaderNotFound(headerName);
            }
            String value = resultHttp.getRespHeaders().get(headerName);

            headerProcessedLine.append(line.substring(currentIdx, idxS) + value);
            currentIdx = idxE + 1;
        }

        // Process body jsonpath macros
        List<Object> values = new ArrayList<>();
        StringBuilder bodyProcessedPattern = new StringBuilder();
        currentIdx = 0;
        int maxRows = 1; // in normal case, only one row will be there
        while (currentIdx < headerProcessedLine.length()) {
            int idxS = headerProcessedLine.indexOf("$b{", currentIdx);
            if (idxS == -1) {
                bodyProcessedPattern.append(headerProcessedLine.substring(currentIdx));
                break;
            }
            int idxE = headerProcessedLine.indexOf("}", idxS);
            String jsonPath = headerProcessedLine.substring(idxS + 3, idxE);
            jsonPath = jsonPath.trim();
            Object value = new Object();
            try {
                // JSONArray or String
                value = JsonPath.read(resultHttp.getBody(), jsonPath);
            } catch (PathNotFoundException e1) { // NOSONAR
                //set to blank for those entries which are missing from the output json
                value = "";
            } catch (Exception e) {
                throw new OnapCommandHttpInvalidResponseBody(jsonPath, e);
            }

            if (value instanceof JSONArray) {
                JSONArray arr = (JSONArray) value;
                if (arr.size() > maxRows) {
                    maxRows = arr.size();
                }
            }
            bodyProcessedPattern.append(headerProcessedLine.substring(currentIdx, idxS) + "%s");
            values.add(value);
            currentIdx = idxE + 1;
        }

        if (bodyProcessedPattern.toString().isEmpty()) {
            result.add(headerProcessedLine.toString());
            return result;
        } else {
            for (int i = 0; i < maxRows; i++) {
                currentIdx = 0;
                StringBuilder bodyProcessedLine = new StringBuilder();
                int positionalIdx = 0; // %s positional idx
                while (currentIdx < bodyProcessedPattern.length()) {
                    int idxS = bodyProcessedPattern.indexOf("%s", currentIdx);
                    if (idxS == -1) {
                        bodyProcessedLine.append(bodyProcessedPattern.substring(currentIdx));
                        break;
                    }
                    int idxE = idxS + 2; // %s
                    try {
                        Object value = values.get(positionalIdx);
                        String valueS = String.valueOf(value);
                        if (value instanceof JSONArray) {
                            JSONArray arr = (JSONArray) value;
                            if (!arr.isEmpty()) {
                                valueS = arr.get(i).toString();
                            } else {
                                throw new OnapCommandResultEmpty();
                            }
                        }

                        bodyProcessedLine.append(bodyProcessedPattern.substring(currentIdx, idxS) + valueS);
                        currentIdx = idxE;
                        positionalIdx++;
                    } catch (OnapCommandResultEmpty e) {
                        throw e;
                    } catch (Exception e) {
                        throw new OnapCommandResultMapProcessingFailed(line, e);
                    }
                }
                result.add(bodyProcessedLine.toString());
            }

            return result;
        }
    }

    public static void normalizeJson(JsonElement node) {
        Iterator<Entry<String, JsonElement>> it = node.getAsJsonObject().entrySet().iterator();

        while (it.hasNext()) {
            JsonElement child = it.next().getValue();
            if (child.isJsonPrimitive() && child.getAsJsonPrimitive().isString() && child.getAsJsonPrimitive().getAsString().equals(""))
                it.remove();
            else  if (child.isJsonNull())
                it.remove();
            else if (child.isJsonObject())
                normalizeJson(child);
            else if (child.isJsonArray()) {
                for (JsonElement ele:child.getAsJsonArray()) {
                    if (ele.isJsonObject())
                        normalizeJson(ele);
                }
            }
        }
    }

    public static String normalizeJson(String json) throws OnapCommandHttpInvalidRequestBody {
        JsonElement node;
        try {
            node = JsonParser.parseString(json);
            normalizeJson(node);
            return gson.toJson(node);
        } catch (Exception e) {  //NOSONAR
            throw new OnapCommandHttpInvalidRequestBody(e);
        }

    }
}

