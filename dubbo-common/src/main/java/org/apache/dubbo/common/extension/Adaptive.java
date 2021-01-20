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
package org.apache.dubbo.common.extension;

import org.apache.dubbo.common.URL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provide helpful information for {@link ExtensionLoader} to inject dependency extension instance.
 * @Adaptive 注解，可添加类或方法上，分别代表类两种不同的使用方式
 * 一个扩展接口，有且仅有一个Adaptive扩展实现类
 * 第一种，标记在类上，代表手动实现它是一个扩展接口的Adaptive扩展实现类。
 *  目前只有ExtensionFactory扩展的实现类AdaptiveExtensionFactory
 * 第二种，标记在拓展接口的方法上，代表自动生成代码实现该接口的 Adaptive 拓展实现类。
 *  value ，从 Dubbo URL 获取参数中，使用键名( Key )，获取键值。该值为真正的拓展名。
 *  自适应拓展实现类，会获取拓展名对应的真正的拓展对象。通过该对象，执行真正的逻辑。
 *  可以设置多个键名( Key )，顺序获取直到有值。若最终获取不到，使用默认拓展名。
 *
 * @see ExtensionLoader
 * @see URL
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Adaptive {
    /**
     * Decide which target extension to be injected. The name of the target extension is decided by the parameter passed
     * in the URL, and the parameter names are given by this method.
     * <p>
     * If the specified parameters are not found from {@link URL}, then the default extension will be used for
     * dependency injection (specified in its interface's {@link SPI}).
     * <p>
     * For example, given <code>String[] {"key1", "key2"}</code>:
     * <ol>
     * <li>find parameter 'key1' in URL, use its value as the extension's name</li>
     * <li>try 'key2' for extension's name if 'key1' is not found (or its value is empty) in URL</li>
     * <li>use default extension if 'key2' doesn't exist either</li>
     * <li>otherwise, throw {@link IllegalStateException}</li>
     * </ol>
     * If the parameter names are empty, then a default parameter name is generated from interface's
     * class name with the rule: divide classname from capital char into several parts, and separate the parts with
     * dot '.', for example, for {@code org.apache.dubbo.xxx.YyyInvokerWrapper}, the generated name is
     * <code>String[] {"yyy.invoker.wrapper"}</code>.
     *
     * @return parameter names in URL
     */
    /**
     * 从{@link URL}的key名，对应的value作为Adapt成的Extension名
     * 如果{@link URL}这些key都没有value，作为缺省的扩展(在接口的{@link SPI}中设置的值)。
     * @return
     */
    String[] value() default {};

}