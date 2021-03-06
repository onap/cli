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

package org.onap.cli.fw.http.auth;

import org.onap.cli.fw.http.conf.OnapCommandHttpConstants;

/**
 * Oclip Service as reported in api catalog.
 */
public class OnapCommandHttpService {
    /*
     * Oclip Service name like aai.
     */
    private String serviceName;

    /*
     * Oclip Service API version like v1, v2, etc
     */
    private String serviceVersion;

    private String basePath;

    /**
     * Mode of service consideration. By default, it goes with
     * 'catalog' mode, where basePath will be discovered by
     * the framework using serviceName and serviceVersion
     * Another mode is 'direct', in which bastPath will be
     * same as OnapCredentails.ServiceUrl.
     */
    private String mode = OnapCommandHttpConstants.MODE_DIRECT;

    private String authType =  OnapCommandHttpConstants.AUTH_NONE;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isModeDirect() {
        return this.getMode().equals(OnapCommandHttpConstants.MODE_DIRECT);
    }

    public String getAuthType() {
        return this.authType;
    }

    public void setAuthType(String auth) {
        this.authType = auth;
    }

    public boolean isNoAuth() {
        return this.authType.equalsIgnoreCase(OnapCommandHttpConstants.AUTH_NONE);
    }

    public String getName() {
        return serviceName;
    }

    public void setName(String name) {
        this.serviceName = name;
    }

    public String getVersion() {
        return serviceVersion;
    }

    public void setVersion(String version) {
        this.serviceVersion = version;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getVersion();
    }

}