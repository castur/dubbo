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

import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.common.status.StatusChecker;
import org.apache.dubbo.common.threadpool.ThreadPool;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.support.Parameter;
import org.apache.dubbo.remoting.Codec;
import org.apache.dubbo.remoting.Constants;
import org.apache.dubbo.remoting.Dispatcher;
import org.apache.dubbo.remoting.Transporter;
import org.apache.dubbo.remoting.exchange.Exchanger;
import org.apache.dubbo.remoting.telnet.TelnetHandler;
import org.apache.dubbo.rpc.Protocol;

import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.THREADPOOL_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.DUBBO_PROTOCOL;
import static org.apache.dubbo.common.constants.CommonConstants.HOST_KEY;
import static org.apache.dubbo.config.Constants.PROTOCOLS_SUFFIX;
import static org.apache.dubbo.remoting.Constants.TELNET;
import static org.apache.dubbo.remoting.Constants.DUBBO_VERSION_KEY;

/**
 * 服务提供者协议配置
 *
 * ProtocolConfig
 *
 * @export
 */
public class ProtocolConfig extends AbstractConfig {

    private static final long serialVersionUID = 6913423882496634749L;

    /**
     * 协议名字
     * Protocol name
     */
    private String name;

    /**
     *
     * Service ip address (when there are multiple network cards available)
     */
    private String host;

    /**
     * 端口
     * Service port
     */
    private Integer port;

    /**
     *
     * Context path
     */
    private String contextpath;

    /**
     * 线程池类型，可选：fixed/cached
     * Thread pool
     */
    private String threadpool;

    /**
     * 线程池类型
     * Thread pool core thread size
     */
    private Integer corethreads;

    /**
     * 服务线程池大小
     * Thread pool size (fixed size)
     */
    private Integer threads;

    /**
     * io线程池大小
     * IO thread pool size (fixed size)
     */
    private Integer iothreads;

    /**
     * 线程池队列大小，当线程池满时，排队等待执行的队列大小，建议不要设置，当线程池满时应立即失败，重试其它服务提供机器，而不是排队，除非有特殊需求。
     * Thread pool's queue length
     */
    private Integer queues;

    /**
     * 最大连接数
     * Max acceptable connections
     */
    private Integer accepts;

    /**
     * 协议编码
     * Protocol codec
     */
    private String codec;

    /**
     * 序列化
     * 协议序列化方式，当协议支持多种序列化方式时使用，比如：dubbo协议的dubbo,hessian2,java,compactedjava，以及http协议的json等
     * Serialization
     */
    private String serialization;

    /**
     * 字符集
     * Charset
     */
    private String charset;

    /**
     * 有效载荷最大长度
     * 请求及响应数据包大小限制，单位：字节
     * Payload max length
     */
    private Integer payload;

    /**
     * 字节长度
     * Buffer size
     */
    private Integer buffer;

    /**
     * 心跳间隔，对于长连接，当物理层断开时，比如拔网线，TCP的FIN消息来不及发送，对方收不到断开事件，此时需要心跳来帮助检查连接是否已断开
     * 心跳间隔
     * Heartbeat interval
     */
    private Integer heartbeat;

    /**
     * 设为true，将向logger中输出访问日志，也可填写访问日志文件路径，直接把访问日志输出到指定文件
     * Access log
     */
    private String accesslog;

    /**
     * 协议的服务端和客户端实现类型，比如：dubbo协议的mina,netty等，可以分拆为server和client配置
     * dubbo协议缺省为netty
     * Transfort
     */
    private String transporter;

    /**
     * 信息如何交换
     * How information is exchanged
     */
    private String exchanger;

    /**
     * 协议的消息派发方式，用于指定线程模型，比如：dubbo协议的all, direct, message, execution, connection等
     * dubbo协议缺省为all
     * Thread dispatch mode
     */
    private String dispatcher;

    /**
     * Networker
     */
    private String networker;

    /**
     * 协议的服务器端实现类型，比如：dubbo协议的mina,netty等，http协议的jetty,servlet等
     * dubbo协议缺省为netty，http协议缺省为servlet
     * Sever impl
     */
    private String server;

    /**
     * 协议的客户端实现类型，比如：dubbo协议的mina,netty等
     * dubbo协议缺省为netty
     * Client impl
     */
    private String client;

    /**
     * Supported telnet commands, separated with comma.
     */
    private String telnet;

    /**
     * Command line prompt
     */
    private String prompt;

    /**
     * Status check
     */
    private String status;

    /**
     * Whether to register
     */
    private Boolean register;

    /**
     * 是否都是持久连接
     * whether it is a persistent connection
     */
    //TODO add this to provider config
    private Boolean keepAlive;

    // TODO add this to provider config
    private String optimizer;

    /**
     * The extension
     */
    private String extension;

    /**
     * The customized parameters
     */
    private Map<String, String> parameters;

    /**
     * If it's default
     */
    private Boolean isDefault;

    public ProtocolConfig() {
    }

    public ProtocolConfig(String name) {
        setName(name);
    }

    public ProtocolConfig(String name, int port) {
        setName(name);
        setPort(port);
    }

    @Parameter(excluded = true)
    public String getName() {
        return name;
    }

    public final void setName(String name) {
        checkName("name", name);
        this.name = name;
        this.updateIdIfAbsent(name);
    }

    @Parameter(excluded = true)
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        checkName(HOST_KEY, host);
        this.host = host;
    }

    @Parameter(excluded = true)
    public Integer getPort() {
        return port;
    }

    public final void setPort(Integer port) {
        this.port = port;
    }

    @Deprecated
    @Parameter(excluded = true)
    public String getPath() {
        return getContextpath();
    }

    @Deprecated
    public void setPath(String path) {
        setContextpath(path);
    }

    @Parameter(excluded = true)
    public String getContextpath() {
        return contextpath;
    }

    public void setContextpath(String contextpath) {
        checkPathName("contextpath", contextpath);
        this.contextpath = contextpath;
    }

    public String getThreadpool() {
        return threadpool;
    }

    public void setThreadpool(String threadpool) {
        checkExtension(ThreadPool.class, THREADPOOL_KEY, threadpool);
        this.threadpool = threadpool;
    }

    public Integer getCorethreads() {
        return corethreads;
    }

    public void setCorethreads(Integer corethreads) {
        this.corethreads = corethreads;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Integer getIothreads() {
        return iothreads;
    }

    public void setIothreads(Integer iothreads) {
        this.iothreads = iothreads;
    }

    public Integer getQueues() {
        return queues;
    }

    public void setQueues(Integer queues) {
        this.queues = queues;
    }

    public Integer getAccepts() {
        return accepts;
    }

    public void setAccepts(Integer accepts) {
        this.accepts = accepts;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        if (DUBBO_PROTOCOL.equals(name)) {
            checkMultiExtension(Codec.class, Constants.CODEC_KEY, codec);
        }
        this.codec = codec;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        if (DUBBO_PROTOCOL.equals(name)) {
            checkMultiExtension(Serialization.class, Constants.SERIALIZATION_KEY, serialization);
        }
        this.serialization = serialization;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }

    public Integer getBuffer() {
        return buffer;
    }

    public void setBuffer(Integer buffer) {
        this.buffer = buffer;
    }

    public Integer getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(Integer heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        if (DUBBO_PROTOCOL.equals(name)) {
            checkMultiExtension(Transporter.class, Constants.SERVER_KEY, server);
        }
        this.server = server;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        if (DUBBO_PROTOCOL.equals(name)) {
            checkMultiExtension(Transporter.class, Constants.CLIENT_KEY, client);
        }
        this.client = client;
    }

    public String getAccesslog() {
        return accesslog;
    }

    public void setAccesslog(String accesslog) {
        this.accesslog = accesslog;
    }

    public String getTelnet() {
        return telnet;
    }

    public void setTelnet(String telnet) {
        checkMultiExtension(TelnetHandler.class, TELNET, telnet);
        this.telnet = telnet;
    }

    @Parameter(escaped = true)
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        checkMultiExtension(StatusChecker.class, "status", status);
        this.status = status;
    }

    public Boolean isRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        checkExtension(Transporter.class, Constants.TRANSPORTER_KEY, transporter);
        this.transporter = transporter;
    }

    public String getExchanger() {
        return exchanger;
    }

    public void setExchanger(String exchanger) {
        checkExtension(Exchanger.class, Constants.EXCHANGER_KEY, exchanger);
        this.exchanger = exchanger;
    }

    /**
     * typo, switch to use {@link #getDispatcher()}
     *
     * @deprecated {@link #getDispatcher()}
     */
    @Deprecated
    @Parameter(excluded = true)
    public String getDispather() {
        return getDispatcher();
    }

    /**
     * typo, switch to use {@link #getDispatcher()}
     *
     * @deprecated {@link #setDispatcher(String)}
     */
    @Deprecated
    public void setDispather(String dispather) {
        setDispatcher(dispather);
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        checkExtension(Dispatcher.class, Constants.DISPACTHER_KEY, dispatcher);
        this.dispatcher = dispatcher;
    }

    public String getNetworker() {
        return networker;
    }

    public void setNetworker(String networker) {
        this.networker = networker;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void destroy() {
        if (name != null) {
            ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(name).destroy();
        }
    }

    @Override
    public void refresh() {
        if (StringUtils.isEmpty(this.getName())) {
            this.setName(DUBBO_VERSION_KEY);
        }
        super.refresh();
        if (StringUtils.isNotEmpty(this.getId())) {
            this.setPrefix(PROTOCOLS_SUFFIX);
            super.refresh();
        }
    }

    @Override
    @Parameter(excluded = true)
    public boolean isValid() {
        return StringUtils.isNotEmpty(name);
    }
}
