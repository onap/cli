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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.onap.cli.fw.error.OnapCommandException;
import org.onap.cli.fw.input.OnapCommandParameterType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OnapCommandResultTest {

    @Test
    @Ignore
    public void commandResultObjTest() throws OnapCommandException { //NOSONAR
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.LANDSCAPE);
        res.setRecords(new ArrayList<OnapCommandResultAttribute>());
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.TABLE);
        res.setDebug(true);

        assertTrue("debugInfo".equals(res.getDebugInfo()) && res.isIncludeSeparator()
                && "Output".equals(res.getOutput())
                && OnapCommandPrintDirection.LANDSCAPE.equals(res.getPrintDirection())
                && res.getRecords().isEmpty() && OnapCommandResultAttributeScope.LONG.equals(res.getScope())
                && OnapCommandResultType.TABLE.equals(res.getType()));

        String help = res.print();
    }

    @Test
    @Ignore
    public void commandResultPrintLandscapeTableTest() throws OnapCommandException { //NOSONAR
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.LANDSCAPE);

        OnapCommandResultAttribute att = new OnapCommandResultAttribute();
        att.setName("param");
        att.setDescription("description");
        att.setType(OnapCommandParameterType.STRING);
        att.setValues(new ArrayList<String>(Arrays.asList(new String[] { "value" })));
        List<OnapCommandResultAttribute> list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        res.setRecords(list);
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.TABLE);
        res.getRecordsMap();
        String expRes = "+--------+\n|param   |\n+--------+\n|value   |\n+--------+\n";
        String result = res.print();

        assertEquals(expRes, result);

    }

    @Test
    @Ignore
    public void commandResultPrintLandscapeJsonTest() throws OnapCommandException { //NOSONAR
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.LANDSCAPE);

        OnapCommandResultAttribute att = new OnapCommandResultAttribute();
        att.setName("param");
        att.setDescription("description");
        att.setType(OnapCommandParameterType.JSON);
        att.setValues(
                new ArrayList<String>(Arrays.asList(new String[] { "{\"id\": \"0001\",\"value\": \"result\"}" })));
        List<OnapCommandResultAttribute> list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        res.setRecords(list);
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.JSON);

        // Will be handled after the json print is implemented
        String result = res.print();
        assertEquals("[{\"param\":{\"id\":\"0001\",\"value\":\"result\"}}]",result);
        // String expRes = "+--------+\n|param |\n+--------+\n|value
        // |\n+--------+\n";
        // assertEquals(expRes,result);

    }

    @Test
    @Ignore
    public void commandResultPrintLandscapeCsvTest() throws OnapCommandException { //NOSONAR
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.LANDSCAPE);

        OnapCommandResultAttribute att = new OnapCommandResultAttribute();
        att.setName("param");
        att.setDescription("description");
        att.setType(OnapCommandParameterType.STRING);
        att.setValues(new ArrayList<String>(Arrays.asList(new String[] { "value" })));
        List<OnapCommandResultAttribute> list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        OnapCommandResultAttribute a1 = new OnapCommandResultAttribute();
        a1.setName("param1");
        a1.setDescription("description1");
        a1.setType(OnapCommandParameterType.STRING);
        a1.setValues(new ArrayList<String>(Arrays.asList(new String[] { "value1" })));

        list.add(a1);
        res.setRecords(list);
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.CSV);

        String expRes = "param,param1\r\n";
        String result = res.print();
        assertEquals(expRes, result);

    }

    @Test
    @Ignore
    public void commandResultPrintPortraitCsvTest() throws OnapCommandException { //NOSONAR
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.PORTRAIT);

        OnapCommandResultAttribute att = new OnapCommandResultAttribute();
        att.setName("param");
        att.setDescription("description");
        att.setType(OnapCommandParameterType.STRING);
        att.setValues(new ArrayList<String>(Arrays.asList(new String[] { "value" })));
        List<OnapCommandResultAttribute> list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        OnapCommandResultAttribute a1 = new OnapCommandResultAttribute();
        a1.setName("param1");
        a1.setDescription("description1");
        a1.setType(OnapCommandParameterType.STRING);
        a1.setValues(new ArrayList<String>(Arrays.asList(new String[] { "value1" })));

        list.add(a1);
        res.setRecords(list);
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.CSV);
        String expRes = "property,value\r\nparam,value\r\n";
        String result = res.print();
        assertEquals(expRes, result);
    }

    @Test
    @Ignore
    public void commandResultPrintPortraitTableTest() throws OnapCommandException { //NOSONAR
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.PORTRAIT);

        OnapCommandResultAttribute att = new OnapCommandResultAttribute();
        att.setName("param");
        att.setDescription("description");
        att.setType(OnapCommandParameterType.STRING);
        att.setValues(new ArrayList<String>(Arrays.asList(new String[] { "value" })));

        List<OnapCommandResultAttribute> list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        res.setRecords(list);
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.TABLE);
        String expRes = "+----------+--------+\n|property  |value   |\n+----------+--------+"
                + "\n|param     |value   |\n+----------+--------+\n";

        String result = res.print();

        assertEquals(expRes, result);

    }
    @Test
    public void commandResultPrintLandscapeJsonTestForGson() throws OnapCommandException {
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.LANDSCAPE);

        OnapCommandResultAttribute att = new OnapCommandResultAttribute();
        att.setName("param");
        att.setDescription("description");
        att.setType(OnapCommandParameterType.JSON);
        att.setValues(
                new ArrayList<String>(Arrays.asList(new String[] { "{\"id\": \"0001\",\"value\": \"result\"}" })));
        List<OnapCommandResultAttribute> list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        res.setRecords(list);
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.JSON);
        String result = res.print();
        String expRes="[{\"param\":{\"id\":\"0001\",\"value\":\"result\"}}]";
        assertEquals(expRes,result);

    }

    @Test
    public void printYamlTest() throws OnapCommandException {
        OnapCommandResult res = new OnapCommandResult();
        res.setDebugInfo("debugInfo");
        res.setIncludeSeparator(true);
        res.setIncludeTitle(true);
        res.setOutput("Output");
        res.setPrintDirection(OnapCommandPrintDirection.LANDSCAPE);

        OnapCommandResultAttribute att = new OnapCommandResultAttribute();
        att.setName("param");
        att.setDescription("description");
        att.setType(OnapCommandParameterType.YAML);
        att.setValues(
                new ArrayList<String>(Arrays.asList(new String[] {  "{\"id\": \"0001\",\"value\": \"result\"}"  })));
        List<OnapCommandResultAttribute> list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        res.setRecords(list);
        res.setScope(OnapCommandResultAttributeScope.LONG);
        res.setType(OnapCommandResultType.YAML);
        String result = res.print();
        String expRes="---\n- param:\n    id: \"0001\"\n    value: \"result\"\n";
        assertEquals(expRes,result);

        att.setValues(
                new ArrayList<String>(Arrays.asList(new String[] {  "{\"id\": \"0001\": \"value\": }"  })));
        list = new ArrayList<OnapCommandResultAttribute>();
        list.add(att);
        res.setRecords(list);
        result = res.print();
        expRes="---\n- param: null\n";
        assertEquals(expRes,result);

    }
}
