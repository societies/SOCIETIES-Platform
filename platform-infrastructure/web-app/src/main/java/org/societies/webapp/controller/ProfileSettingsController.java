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
import org.societies.personalisation.preference.api.model.*;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IPreferenceCondition conditionToDelete;
    private IPreferenceOutcome outcomeToDelete;
    private PreferenceDetails preferenceToDelete;

    /* The following are used for the edit/delete methods, to map a condition/outcome to the parent preference */
    private final Map<IPreferenceCondition, PreferenceDetails> conditionToPDMap = new HashMap<IPreferenceCondition, PreferenceDetails>();
    private final Map<IPreferenceOutcome, PreferenceDetails> outcomeToPDMap = new HashMap<IPreferenceOutcome, PreferenceDetails>();
    private final Map<IPreferenceCondition, IPreference> conditionToPreferenceMap = new HashMap<IPreferenceCondition, IPreference>();
    private final Map<IPreferenceOutcome, IPreference> outcomeToPreferenceMap = new HashMap<IPreferenceOutcome, IPreference>();

    public ProfileSettingsController() {
        log.info("ProfileSettingsController ctor");
    }

    /* Spring/JSF dependency Getters and Setters */
    @SuppressWarnings("UnusedDeclaration")
    public UserService getUserService() {
        return userService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IUserPreferenceConditionMonitor getUserPreferenceConditionMonitor() {
        return userPreferenceConditionMonitor;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserPreferenceConditionMonitor(IUserPreferenceConditionMonitor userPreferenceConditionMonitor) {
        this.userPreferenceConditionMonitor = userPreferenceConditionMonitor;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IUserPreferenceManagement getUserPreferenceManagement() {
        return userPreferenceManagement;
    }

    @SuppressWarnings("UnusedDeclaration")
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

    public IPreferenceCondition getConditionToDelete() {
        return conditionToDelete;
    }

    public IPreferenceOutcome getOutcomeToDelete() {
        return outcomeToDelete;
    }

    public PreferenceDetails getPreferenceToDelete() {
        return preferenceToDelete;
    }

    /* Public methods */
    public void editSelectedNode() {
        log.trace("editSelectedNode()");
        this.preferenceToEdit = null;
        this.conditionToEdit = null;
        this.outcomeToEdit = null;
        this.preferenceToDelete = null;
        this.conditionToDelete = null;
        this.outcomeToDelete = null;

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
            log.debug("setting outcome to edit: " + outcome);
            this.outcomeToEdit = outcome;

//            super.addGlobalMessage(outcome.getparameterName(), "Selected edit on outcome", FacesMessage.SEVERITY_INFO);
        } else {
            super.addGlobalMessage("No node selected", "You've probably tried to edit the root node. Well done. You win a gold star", FacesMessage.SEVERITY_WARN);
        }

    }

//    public void saveConditionListener() {
//        log.trace("saveConditionListener()");
//
//        PreferenceDetails preferenceDetails = conditionToPDMap.get(conditionToEdit);
//        IPreference preference = conditionToPreferenceMap.get(conditionToEdit);
//
//        userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);
//    }
//
//    public void saveOutcomeListener() {
//        log.trace("saveOutcomeListener()");
//
//        PreferenceDetails preferenceDetails = outcomeToPDMap.get(outcomeToEdit);
//        IPreference preference = outcomeToPreferenceMap.get(outcomeToEdit);
//
//        userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);
//    }

    public void savePreferenceState() {
        log.trace("savePreferenceState()");

        TreeNode node = getSelectedTreeNode();
        if (node == null) {

            addGlobalMessage("Cannot edit", "No node selected", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() == null) {

            addGlobalMessage("Cannot edit", "No data in selected node", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() instanceof IPreferenceCondition) {

            IPreferenceCondition condition = (IPreferenceCondition) node.getData();
            PreferenceDetails preferenceDetails = conditionToPDMap.get(condition);
            IPreference preference = conditionToPreferenceMap.get(condition);
            userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

            String fmt = "%s %s %s";
            addGlobalMessage("Condition updated",
                    String.format(fmt, condition.getname() + " " + condition.getoperator() + " " + condition.getvalue()),
                    FacesMessage.SEVERITY_INFO);

        } else if (node.getData() instanceof IPreferenceOutcome) {

            IPreferenceOutcome outcome = (IPreferenceOutcome) node.getData();
            PreferenceDetails preferenceDetails = outcomeToPDMap.get(outcome);
            IPreference preference = outcomeToPreferenceMap.get(outcome);
            userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

            String fmt = "%s = %s (q=%s, p=%s)";
            addGlobalMessage("Outcome updated",
                    String.format(fmt, outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
                    FacesMessage.SEVERITY_INFO);

        } else {

            addGlobalMessage("Cannot edit", "The node you selected cannot be edited", FacesMessage.SEVERITY_WARN);

        }


    }

    public void deleteSelectedNode() {
        log.trace("deleteSelectedNode()");
        this.preferenceToEdit = null;
        this.conditionToEdit = null;
        this.outcomeToEdit = null;
        this.preferenceToDelete = null;
        this.conditionToDelete = null;
        this.outcomeToDelete = null;


        if (getSelectedTreeNode() == null) {
            super.addGlobalMessage("No node selected", "No node selected", FacesMessage.SEVERITY_WARN);

        } else if (PREFERENCE_NODE.equals(getSelectedTreeNode().getType())) {
            PreferenceDetails preferenceDetails = (PreferenceDetails) getSelectedTreeNode().getData();
            log.debug("setting preference to delete: " + preferenceDetails);
            this.preferenceToDelete = preferenceDetails;

//            super.addGlobalMessage(preferenceDetails.getPreferenceName(), "Selected delete on preference", FacesMessage.SEVERITY_INFO);
        } else if (CONDITION_NODE.equals(getSelectedTreeNode().getType())) {
            IPreferenceCondition condition = (IPreferenceCondition) getSelectedTreeNode().getData();
            log.debug("setting condition to delete: " + condition);
            this.conditionToDelete = condition;

//            super.addGlobalMessage(condition.getname(), "Selected delete on condition", FacesMessage.SEVERITY_INFO);
        } else if (OUTCOME_NODE.equals(getSelectedTreeNode().getType())) {
            IPreferenceOutcome outcome = (IPreferenceOutcome) getSelectedTreeNode().getData();
            log.debug("setting outcome to delete: " + outcome);
            this.outcomeToDelete = outcome;

//            super.addGlobalMessage(outcome.getparameterName(), "Selected delete on outcome", FacesMessage.SEVERITY_INFO);
        } else {
            super.addGlobalMessage("No node selected", "You've probably tried to delete the root node. Well done. You win a gold star", FacesMessage.SEVERITY_WARN);
        }

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
        log.trace("Creating preference node: " + preferenceDetails.getPreferenceName());

        IPreference preference = preferenceTreeModel.getRootPreference();

        processSubnodes(preference, preferenceNode, preferenceDetails);

    }

    private void populateConditionNode(IPreference preference, TreeNode preferenceNode, PreferenceDetails preferenceDetails) {
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
        log.trace("Creating condition node: " + condition.getname());

        conditionToPDMap.put(condition, preferenceDetails);
        conditionToPreferenceMap.put(condition, preference);

        processSubnodes(preference, conditionNode, preferenceDetails);
    }

    private void populateOutcomeNode(IPreference preference, TreeNode conditionNode, PreferenceDetails preferenceDetails) {
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
        log.trace("Creating outcome node: " + outcome.getparameterName());

        outcomeToPDMap.put(outcome, preferenceDetails);
        outcomeToPreferenceMap.put(outcome, preference);

    }

    private void processSubnodes(IPreference preference, TreeNode node, PreferenceDetails preferenceDetails) {
        Enumeration<IPreference> e = preference.postorderEnumeration();
        boolean conditionsFound = false;
        // process conditions ONLY
        while (e.hasMoreElements()) {
            IPreference ele = e.nextElement();
            if (ele == preference) continue;

            if (ele.isBranch()) {
                // this is a CONDITION
                populateConditionNode(ele, node, preferenceDetails);
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
                    populateOutcomeNode(ele, node, preferenceDetails);
                }
            }
        }
    }


    public OperatorConstants[] getConditionOperators() {
        return OperatorConstants.values();
    }
}
