/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.config;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.common.utils.UrlUtils;
import org.apache.dubbo.config.support.Parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.dubbo.common.constants.CommonConstants.ANYHOST_VALUE;
import static org.apache.dubbo.common.constants.CommonConstants.PATH_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.PROTOCOL_KEY;
import static org.apache.dubbo.config.Constants.CONFIG_CONFIGFILE_KEY;
import static org.apache.dubbo.config.Constants.CONFIG_ENABLE_KEY;
import static org.apache.dubbo.config.Constants.CONFIG_TIMEOUT_KEY;
import static org.apache.dubbo.config.Constants.ZOOKEEPER_PROTOCOL;
import static org.apache.dubbo.configcenter.Constants.CONFIG_CHECK_KEY;
import static org.apache.dubbo.configcenter.Constants.CONFIG_CLUSTER_KEY;
import static org.apache.dubbo.configcenter.Constants.CONFIG_GROUP_KEY;
import static org.apache.dubbo.configcenter.Constants.CONFIG_NAMESPACE_KEY;

/**
 * 配置中心
 * ConfigCenterConfig
 */
public class ConfigCenterConfig extends AbstractConfig {
    private AtomicBoolean inited = new AtomicBoolean(false);
    /**
     * 使用哪个配置中心：apollo、zookeeper、nacos等
     * 以zookeeper为例
     1. 指定protocol，则address可以简化为127.0.0.1:2181；
     2. 不指定protocol，则address取值为zookeeper://127.0.0.1:2181
     */
    private String protocol;
    private String address;

    /* The config center cluster, it's real meaning may very on different Config Center products. */
    /**
     * 含义视所选定的配置中心而不同。
     如Apollo中用来区分不同的配置集群
     */
    private String cluster;

    /* The namespace of the config center, generally it's used for multi-tenant,
    but it's real meaning depends on the actual Config Center you use.
    */
    /**
     * 通常用于多租户隔离，实际含义视具体配置中心而不同。
     * zookeeper - 环境隔离，默认值dubbo；
     apollo - 区分不同领域的配置集合，默认使用dubbo和application
     */
    private String namespace = CommonConstants.DUBBO;
    /* The group of the config center, generally it's used to identify an isolated space for a batch of config items,
    but it's real meaning depends on the actual Config Center you use.
    */
    /**
     * 含义视所选定的配置中心而不同。
     nacos - 隔离不同配置集
     zookeeper - 隔离不同配置集
     */
    private String group = CommonConstants.DUBBO;
    private String username;
    private String password;
    private Long timeout = 3000L;

    // If the Config Center is given the highest priority, it will override all the other configurations
    /**
     * 来自配置中心的配置项具有最高优先级，即会覆盖本地配置项。
     */
    private Boolean highestPriority = true;

    // Decide the behaviour when initial connection try fails, 'true' means interrupt the whole process once fail.
    /**
     * 当配置中心连接失败时，是否终止应用启动。
     */
    private Boolean check = true;

    /* Used to specify the key that your properties file mapping to, most of the time you do not need to change this parameter.
    Notice that for Apollo, this parameter is meaningless, set the 'namespace' is enough.
    */
    /**
     * 全局级配置文件所映射到的key
     zookeeper - 默认路径/dubbo/config/dubbo/dubbo.properties
     apollo - dubbo namespace中的dubbo.properties键
     */
    private String configFile = CommonConstants.DEFAULT_DUBBO_PROPERTIES;

    /* the .properties file under 'configFile' is global shared while .properties under this one is limited only to this application
    */
    /**
     *
     */
    private String appConfigFile;

    /* If the Config Center product you use have some special parameters that is not covered by this class, you can add it to here.
    For example, with XML:
      <dubbo:config-center>
           <dubbo:parameter key="config.{your key}" value="{your value}" />
      </dubbo:config-center>
     */
    private Map<String, String> parameters;

    public ConfigCenterConfig() {
    }

    public URL toUrl() {
        Map<String, String> map = new HashMap<>();
        appendParameters(map, this);
        if (StringUtils.isEmpty(address)) {
            address = ANYHOST_VALUE;
        }
        map.put(PATH_KEY, ConfigCenterConfig.class.getSimpleName());
        // use 'zookeeper' as the default configcenter.
        if (StringUtils.isEmpty(map.get(PROTOCOL_KEY))) {
            map.put(PROTOCOL_KEY, ZOOKEEPER_PROTOCOL);
        }
        return UrlUtils.parseURL(address, map);
    }

    public boolean checkOrUpdateInited() {
        return inited.compareAndSet(false, true);
    }

    public void setExternalConfig(Map<String, String> externalConfiguration) {
        Environment.getInstance().setExternalConfigMap(externalConfiguration);
    }

    public void setAppExternalConfig(Map<String, String> appExternalConfiguration) {
        Environment.getInstance().setAppExternalConfigMap(appExternalConfiguration);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Parameter(excluded = true)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Parameter(key = CONFIG_CLUSTER_KEY)
    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Parameter(key = CONFIG_NAMESPACE_KEY)
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Parameter(key = CONFIG_GROUP_KEY)
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Parameter(key = CONFIG_CHECK_KEY)
    public Boolean isCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    @Parameter(key = CONFIG_ENABLE_KEY)
    public Boolean isHighestPriority() {
        return highestPriority;
    }

    public void setHighestPriority(Boolean highestPriority) {
        this.highestPriority = highestPriority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Parameter(key = CONFIG_TIMEOUT_KEY)
    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Parameter(key = CONFIG_CONFIGFILE_KEY)
    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    @Parameter(excluded = true)
    public String getAppConfigFile() {
        return appConfigFile;
    }

    public void setAppConfigFile(String appConfigFile) {
        this.appConfigFile = appConfigFile;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        checkParameterName(parameters);
        this.parameters = parameters;
    }

    @Override
    @Parameter(excluded = true)
    public boolean isValid() {
        if (StringUtils.isEmpty(address)) {
            return false;
        }

        return address.contains("://") || StringUtils.isNotEmpty(protocol);
    }
}
