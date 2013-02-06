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
package org.societies.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("Session")
public class UserPreferenceManagementService {
    private static Logger log = LoggerFactory.getLogger(UserPreferenceManagementService.class);

    //    @Autowired
    private IUserPreferenceManagement userPreferenceManagement;

    @Autowired
    private IUserPreferenceConditionMonitor userPreferenceConditionMonitor;

    public UserPreferenceManagementService() {
        if (log.isTraceEnabled())
            log.trace("UserPreferenceManagementService ctor()");
    }

    public List<PreferenceDetails> getPreferenceDetailsForAllPreferences() {
        if (log.isTraceEnabled())
            log.trace("getPreferenceDetailsForAllPreferences()");

        if (userPreferenceManagement == null) {
            if (userPreferenceConditionMonitor == null) {
                log.error("userPreferenceConditionMonitor is null");
                return null;
            }
            this.userPreferenceManagement = userPreferenceConditionMonitor.getPreferenceManager();
        }
        if (userPreferenceManagement == null) {
            log.error("userPreferenceManagement is null");
            return null;
        }
        List<PreferenceDetails> list = userPreferenceManagement.getPreferenceDetailsForAllPreferences();


        if (log.isDebugEnabled())
            log.debug("getPreferenceDetailsForAllPreferences() size = " + list.size());

        return list;
//        return null;
    }

    public IPreferenceTreeModel getModel(IIdentity identity, PreferenceDetails preferenceDetails) {
        if (log.isTraceEnabled())
            log.trace("getModel()");

        if (userPreferenceManagement == null) {
            if (userPreferenceConditionMonitor == null) {
                log.error("userPreferenceConditionMonitor is null");
                return null;
            }
            this.userPreferenceManagement = userPreferenceConditionMonitor.getPreferenceManager();
        }
        if (userPreferenceManagement == null) {
            log.error("userPreferenceManagement is null");
            return null;
        }

        IPreferenceTreeModel model = userPreferenceManagement.getModel(identity, preferenceDetails);
        return model;
//        return null;
    }

}
