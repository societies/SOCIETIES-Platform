/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
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
package org.societies.integration.tests.bit.webapp;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.integration.test.IntegrationTestCase;
import org.springframework.osgi.context.BundleContextAware;

@SuppressWarnings({"MethodMayBeStatic", "UnusedDeclaration"})
public class WebappCISTestsInit extends IntegrationTestCase implements BundleContextAware {

    private static final Logger log = LoggerFactory.getLogger(WebappCISTestsInit.class);

    private static ICommManager commManager;
    private static ICtxBroker ctxBroker;
    private static IPrivacyPolicyManager privacyPolicyManager;
    private static IPrivacyPolicyNegotiationManager privacyPolicyNegotiationManager;
    private static IEventMgr eventManager;
    private static BundleContext bundleContext;

    public WebappCISTestsInit() {
        super(2116,
                TestWebappCisJoin.class
        );
        log.debug("Starting WebappCISTestsInit");
    }

    public static ICommManager getCommManager() {
        return commManager;
    }

    public void setCommManager(ICommManager commManager) {
        WebappCISTestsInit.commManager = commManager;
    }

    public static ICtxBroker getCtxBroker() {
        return ctxBroker;
    }

    public void setCtxBroker(ICtxBroker ctxBroker) {
        WebappCISTestsInit.ctxBroker = ctxBroker;
    }

    public static IPrivacyPolicyManager getPrivacyPolicyManager() {
        return privacyPolicyManager;
    }

    public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
        WebappCISTestsInit.privacyPolicyManager = privacyPolicyManager;
    }

    public static IPrivacyPolicyNegotiationManager getPrivacyPolicyNegotiationManager() {
        return privacyPolicyNegotiationManager;
    }

    public void setPrivacyPolicyNegotiationManager(IPrivacyPolicyNegotiationManager privacyPolicyNegotiationManager) {
        WebappCISTestsInit.privacyPolicyNegotiationManager = privacyPolicyNegotiationManager;
    }

    public static IEventMgr getEventManager() {
        return eventManager;
    }

    public void setEventManager(IEventMgr eventManager) {
        WebappCISTestsInit.eventManager = eventManager;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        WebappCISTestsInit.bundleContext = bundleContext;
    }

    public static BundleContext getBundleContext() {
        return bundleContext;
    }
}
