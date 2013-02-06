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
package org.societies.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.webapp.models.ProfileSettingsForm;
import org.societies.webapp.service.UserPreferenceManagementService;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Paddy S
 */
@Controller
@Scope("request")
public class ProfileSettingsController {
    private static Logger log = LoggerFactory.getLogger(ProfileSettingsController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private UserPreferenceManagementService userPreferenceManagementService;

    public ProfileSettingsController() {
        log.info("ProfileSettingsController constructor");
    }

    @RequestMapping(value = "/profilesettings.html", method = RequestMethod.GET)
    public ModelAndView initProfileSettings() {

        if (!userService.isUserLoggedIn()) {
            // redirect to login page
            // TODO: popup login box
            log.warn("User not logged in - redirecting to home");
            return new ModelAndView(new RedirectView("index.html", true));
        }

        Map<String, Object> model = new HashMap<String, Object>();
        ProfileSettingsForm form = new ProfileSettingsForm();

        userService.loadUserDetailsFromCommMgr();
        userService.loadUserDetailsIntoModel(model);

        model.put("loggedIn", true); // can only be true by this point
        model.put("form", form);

        IIdentity ident = (IIdentity) model.get("identity");
        populateUserDetails(form, ident);
        populateProfileForm(form, ident);

        return new ModelAndView("profilesettings", model);
    }

    private void populateUserDetails(ProfileSettingsForm form, IIdentity ident) {
        form.setFullName(userService.getUsername());

    }

    private void populateProfileForm(ProfileSettingsForm form, IIdentity ident) {

        List<PreferenceDetails> detailsList = userPreferenceManagementService.getPreferenceDetailsForAllPreferences();
        form.setPreferenceDetailsList(detailsList);

        if (detailsList == null) return;

        for (PreferenceDetails preferenceDetails : detailsList) {
            IPreferenceTreeModel preferenceTreeModel = userPreferenceManagementService.getModel(ident, preferenceDetails);
            form.getPreferenceDetailTreeModelMap().put(preferenceDetails, preferenceTreeModel);

            log.debug("----------------------");
            log.debug(preferenceDetails.getPreferenceName() + "/" + preferenceTreeModel.getPreferenceName());
            log.debug(preferenceTreeModel.getServiceType());
            log.debug(preferenceTreeModel.getLastModifiedDate().toString());
            log.debug(preferenceTreeModel.getServiceID().getServiceInstanceIdentifier() + " / " + preferenceTreeModel.getServiceID().getIdentifier().toString());

            IPreference preference = preferenceTreeModel.getRootPreference();
            blah(preference);

        }

    }

    private void blah(IPreference preference) {
        log.debug("+++");
        IPreferenceCondition condition = preference.getCondition();
        IPreferenceOutcome outcome = preference.getOutcome();

        IPreference root = preference.getRoot();
        Object userObject = preference.getUserObject();

        log.debug(preference.toTreeString());

        if (condition == null)
            log.debug("no condition");
        else
            log.debug(condition.getname() + "/" + condition.getType() + "/" + condition.getvalue() + "/" + condition.getoperator());

        if (outcome == null)
            log.debug("no outcome");
        else
            log.debug(outcome.getparameterName() + "/" + outcome.getServiceType() + "/" + outcome.getvalue() + "/" + outcome.getQualityofPreference() + "/" + outcome.getConfidenceLevel());

        if (root == null)
            log.debug("no root");
        else
            log.debug(root.toString());

        if (userObject == null)
            log.debug("no userObject");
        else
            log.debug(userObject.getClass() + ": " + userObject.toString());

        Enumeration<IPreference> e = preference.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            IPreference ele = e.nextElement();
            if (ele == preference) continue;

            blah(ele);
        }
    }

}
