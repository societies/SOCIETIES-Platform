/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.orchestration.cpa.test;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bootstraps Spring-managed beans into an application. How to use:
 * <ul>
 * <li>Create application context XML configuration files and put them where
 * they can be loaded as class path resources. The configuration must include
 * the {@code <context:annotation-config/>} element to enable annotation-based
 * configuration, or the {@code <context:component-scan base-package="..."/>}
 * element to also detect bean definitions from annotated classes.
 * <li>Create a "main" class that will receive references to Spring-managed
 * beans. Add the {@code @Autowired} annotation to any properties you want to be
 * injected with beans from the application context.
 * <li>In your application {@code main} method, create an
 * {@link ApplicationContextLoader} instance, and call the {@link #load} method
 * with the "main" object and the configuration file locations as parameters.
 * </ul>
 */
public class ApplicationContextLoader {

    protected ConfigurableApplicationContext applicationContext;

    public ApplicationContextLoader() {
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Loads application context. Override this method to change how the
     * application context is loaded.
     * 
     * @param configLocations
     *            configuration file locations
     */
    protected void loadApplicationContext(String... configLocations) {
        applicationContext = new ClassPathXmlApplicationContext(
                configLocations);
        applicationContext.registerShutdownHook();
    }

    /**
     * Injects dependencies into the object. Override this method if you need
     * full control over how dependencies are injected.
     * 
     * @param main
     *            object to inject dependencies into
     */
    protected void injectDependencies(Object main) {
        getApplicationContext().getBeanFactory().autowireBeanProperties(
                main, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
    }

    /**
     * Loads application context, then injects dependencies into the object.
     * 
     * @param main
     *            object to inject dependencies into
     * @param configLocations
     *            configuration file locations
     */
    public void load(Object main, String... configLocations) {
        loadApplicationContext(configLocations);
        injectDependencies(main);
    }
}
