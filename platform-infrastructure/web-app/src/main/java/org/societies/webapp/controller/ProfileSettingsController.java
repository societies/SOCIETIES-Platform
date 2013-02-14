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

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.Enumeration;
import java.util.List;

//@Controller // Spring
@ManagedBean(name = "profileSettings") // JSF
@SessionScoped // JSF
//@Scope("Request") // Spring
public class ProfileSettingsController extends BasePageController {

    public static final String OUTCOME_NODE = "outcome";
    public static final String CONDITION_NODE = "condition";
    public static final String PREFERENCE_NODE = "preference";

    @Autowired
    @ManagedProperty(value = "#{userService}")
    private UserService userService; // Spring dependency

    @Autowired
    @ManagedProperty(value = "#{userPreferenceConditionMonitor}")
    private IUserPreferenceConditionMonitor userPreferenceConditionMonitor; // Spring dependency

    private IUserPreferenceManagement userPreferenceManagement;

    private TreeNode preferencesRootNode;
    private TreeNode selectedTreeNode;
    private IPreferenceCondition conditionToEdit;
    private IPreferenceOutcome outcomeToEdit;
    private PreferenceDetails preferenceToEdit;

    public ProfileSettingsController() {
        log.info("ProfileSettingsController ctor");
    }

    /* Spring/JSF dependency Getters and Setters */
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

    /* Web app Getters and Setters */
    public TreeNode getPreferencesRootNode() {
//        log.trace("getPreferencesRootNode()");
        if (preferencesRootNode == null)
            populatePreferencesRootNode();

        return preferencesRootNode;
    }

    public void setSelectedTreeNode(TreeNode selectedTreeNode) {
        log.trace("setSelectedTreeNode() = " + selectedTreeNode);
        this.selectedTreeNode = selectedTreeNode;
    }

    public TreeNode getSelectedTreeNode() {
//        log.trace("getSelectedTreeNode() = " + selectedTreeNode);
        return selectedTreeNode;
    }

    public PreferenceDetails getPreferenceToEdit() {
//        log.trace("getPreferenceToEdit()");
        return preferenceToEdit;
    }

    public IPreferenceCondition getConditionToEdit() {
//        log.trace("getConditionToEdit() = " + conditionToEdit);
        return conditionToEdit;
    }

    public IPreferenceOutcome getOutcomeToEdit() {
//        log.trace("getOutcomeToEdit()");
        return outcomeToEdit;
    }

    /* Public methods */
    public void editSelectedNode() {
        log.trace("editSelectedNode()");
        this.preferenceToEdit = null;
        this.conditionToEdit = null;
        this.outcomeToEdit = null;

        if (getSelectedTreeNode() == null) {
            super.addGlobalMessage("No node selected", "No node selected", FacesMessage.SEVERITY_WARN);

        } else if (PREFERENCE_NODE.equals(getSelectedTreeNode().getType())) {
            PreferenceDetails preferenceDetails = (PreferenceDetails) getSelectedTreeNode().getData();
            log.debug("setting preference to edit: " + preferenceDetails);
            this.preferenceToEdit = preferenceDetails;

//            super.addGlobalMessage(preferenceDetails.getPreferenceName(), "Selected edit on preference", FacesMessage.SEVERITY_INFO);
        } else if (CONDITION_NODE.equals(getSelectedTreeNode().getType())) {
            IPreferenceCondition condition = (IPreferenceCondition) getSelectedTreeNode().getData();
            log.debug("setting condition to edit: " + condition);
            this.conditionToEdit = condition;

//            super.addGlobalMessage(condition.getname(), "Selected edit on condition", FacesMessage.SEVERITY_INFO);
        } else if (OUTCOME_NODE.equals(getSelectedTreeNode().getType())) {
            IPreferenceOutcome outcome = (IPreferenceOutcome) getSelectedTreeNode().getData();
            log.debug("setting outcome   to edit: " + outcome);
            this.outcomeToEdit = outcome;

//            super.addGlobalMessage(outcome.getparameterName(), "Selected edit on outcome", FacesMessage.SEVERITY_INFO);
        } else {
            super.addGlobalMessage("No node selected", "You've probably tried to edit the root node. Well done. You win a gold star", FacesMessage.SEVERITY_WARN);
        }

    }

    public void deleteSelectedNode() {
        log.trace("deleteSelectedNode()");
    }

    /* Private helper methods */
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

            populatePreferenceNode(preferenceDetails, preferenceTreeModel);
        }
    }

    private void populatePreferenceNode(PreferenceDetails preferenceDetails, IPreferenceTreeModel preferenceTreeModel) {

        TreeNode preferenceNode = new DefaultTreeNode(PREFERENCE_NODE,
                preferenceDetails,
//                preferenceTreeModel.getPreferenceName(),
                preferencesRootNode);
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

//        String fmt = "%s %s %s";
        TreeNode conditionNode = new DefaultTreeNode(CONDITION_NODE,
                condition,
//                String.format(fmt, condition.getname() + " " + condition.getoperator() + " " + condition.getvalue()),
                preferenceNode);
        conditionNode.setExpanded(true);

        processSubnodes(preference, conditionNode);
    }

    private void populateOutcomeNode(IPreference preference, TreeNode conditionNode) {
        IPreferenceOutcome outcome = preference.getOutcome();

        if (outcome == null) {
            log.error("leaf node " + preference.toString() + " contains null outcome");
            return;
        }

//        String fmt = "%s = %s (q=%s, p=%s)";
        TreeNode outcomeNode = new DefaultTreeNode(OUTCOME_NODE,
                outcome,
//                String.format(fmt, outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
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

            if (ele.isBranch()) {
                // this is a CONDITION
                populateConditionNode(ele, node);
                conditionsFound = true;
            } else if (ele.isLeaf()) {
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

                if (ele.isBranch()) {
                    // this is a CONDITION
//                    populateConditionNode(ele, node);
                } else if (ele.isLeaf()) {
                    // this is an OUTCOME
                    populateOutcomeNode(ele, node);
                }
            }
        }
    }


}
