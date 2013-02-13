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

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.Enumeration;
import java.util.List;

@Controller // Spring
@ManagedBean(name = "profileSettings") // JSF
@RequestScoped // JSF
@Scope("Request") // Spring
public class ProfileSettingsController extends BasePageController {

    @Autowired
    @ManagedProperty(value = "#{userService}")
    private UserService userService; // Spring dependency

    @Autowired
    @ManagedProperty(value = "#{userPreferenceConditionMonitor}")
    private IUserPreferenceConditionMonitor userPreferenceConditionMonitor; // Spring dependency

    private IUserPreferenceManagement userPreferenceManagement;

    private TreeNode preferencesRootNode;

    public ProfileSettingsController() {
        log.info("ProfileSettingsController ctor");
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public IUserPreferenceConditionMonitor getUserPreferenceConditionMonitor() {
        return userPreferenceConditionMonitor;
    }

    public void setUserPreferenceConditionMonitor(IUserPreferenceConditionMonitor userPreferenceConditionMonitor) {
        this.userPreferenceConditionMonitor = userPreferenceConditionMonitor;
    }

    public IUserPreferenceManagement getUserPreferenceManagement() {
        return userPreferenceManagement;
    }

    public void setUserPreferenceManagement(IUserPreferenceManagement userPreferenceManagement) {
        this.userPreferenceManagement = userPreferenceManagement;
    }

    public TreeNode getPreferencesRootNode() {
        if (preferencesRootNode == null)
            populatePreferencesRootNode();

        return preferencesRootNode;
    }

    private void populatePreferencesRootNode() {
        preferencesRootNode = new DefaultTreeNode("Preferences", null);
        preferencesRootNode.setExpanded(true);

        if (userPreferenceManagement == null) {
            if (userPreferenceConditionMonitor == null) {
                log.error("userPreferenceConditionMonitor is null - cannot populate preference root node");
                return;
            }
            this.userPreferenceManagement = userPreferenceConditionMonitor.getPreferenceManager();
        }

        if (userPreferenceManagement == null) {
            log.error("userPreferenceManagementService is null - cannot populate preference root node");
            return;
        }

        List<PreferenceDetails> detailsList = userPreferenceManagement.getPreferenceDetailsForAllPreferences();

        if (detailsList == null) {
            log.warn("userPreferenceManagement returned null preference details list");
            return;
        }


        for (PreferenceDetails preferenceDetails : detailsList) {
            IPreferenceTreeModel preferenceTreeModel =
                    userPreferenceManagement.getModel(userService.getIdentity(), preferenceDetails);

            populatePreferenceNode(preferenceTreeModel);
        }
    }

    private void populatePreferenceNode(IPreferenceTreeModel preferenceTreeModel) {

        TreeNode preferenceNode = new DefaultTreeNode(preferenceTreeModel.getPreferenceName(), preferencesRootNode);
        preferenceNode.setExpanded(true);

        IPreference preference = preferenceTreeModel.getRootPreference();

        processSubnodes(preference, preferenceNode);

    }

    private void populateConditionNode(IPreference preference, TreeNode preferenceNode) {
        IPreferenceCondition condition = preference.getCondition();

        if (condition == null) {
            log.error("branch node " + preference.toString() + " contains null condition");
            return;
        }
        TreeNode conditionNode = new DefaultTreeNode(condition.getname() + " " + condition.getoperator() + " " + condition.getvalue(), preferenceNode);
        conditionNode.setExpanded(true);

        processSubnodes(preference, conditionNode);

    }

    private void populateOutcomeNode(IPreference preference, TreeNode conditionNode) {
        IPreferenceOutcome outcome = preference.getOutcome();

        if (outcome == null) {
            log.error("leaf node " + preference.toString() + " contains null outcome");
            return;
        }

        String fmt = "%s = %s (q=%s, p=%s)";
        TreeNode outcomeNode = new DefaultTreeNode(
                String.format(fmt,
                        outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
                conditionNode);
        outcomeNode.setExpanded(true);

    }

    private void processSubnodes(IPreference preference, TreeNode node) {
        Enumeration<IPreference> e = preference.postorderEnumeration();
        boolean conditionsFound = false;
        // process conditions ONLY
        while (e.hasMoreElements()) {
            IPreference ele = e.nextElement();
            if (ele == preference) continue;

            if (ele.getCondition() != null) {
                // this is a CONDITION
                populateConditionNode(ele, node);
                conditionsFound = true;
            } else if (ele.getOutcome() != null) {
                // this is an OUTCOME
//                populateOutcomeNode(ele, node);
            }
        }
        // process outcomes ONLY
        if (!conditionsFound) {
            e = preference.postorderEnumeration();
            while (e.hasMoreElements()) {
                IPreference ele = e.nextElement();
                if (ele == preference) continue;

                if (ele.getCondition() != null) {
                    // this is a CONDITION
                    populateConditionNode(ele, node);
                } else if (ele.getOutcome() != null) {
                    // this is an OUTCOME
                    populateOutcomeNode(ele, node);
                }
            }
        }
    }
}
