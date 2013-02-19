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
import java.util.*;

@ManagedBean(name = "profileSettings") // JSF
@SessionScoped // JSF
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
    private PreferenceDetails selectedPreference;
    private IPreferenceCondition selectedCondition;
    private IPreferenceOutcome selectedOutcome;
    private IPreferenceCondition conditionToAdd = new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "", "");
    private IPreferenceOutcome outcomeToAdd = new PreferenceOutcome(null, "", "", "");
    private String addConditionMode;

    /* The following are used for the edit/delete methods, to map a condition/outcome to the parent preference */
    private final Map<IPreferenceCondition, PreferenceDetails> conditionToPDMap = new HashMap<IPreferenceCondition, PreferenceDetails>();
    private final Map<IPreferenceOutcome, PreferenceDetails> outcomeToPDMap = new HashMap<IPreferenceOutcome, PreferenceDetails>();
    private final Map<IPreferenceCondition, IPreference> conditionToPreferenceMap = new HashMap<IPreferenceCondition, IPreference>();
    private final Map<IPreferenceOutcome, IPreference> outcomeToPreferenceMap = new HashMap<IPreferenceOutcome, IPreference>();
    private final Map<PreferenceDetails, IPreference> pdToPreferenceMap = new HashMap<PreferenceDetails, IPreference>();


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

    public PreferenceDetails getSelectedPreference() {
        return selectedPreference;
    }

    public IPreferenceCondition getSelectedCondition() {
        return selectedCondition;
    }

    public IPreferenceOutcome getSelectedOutcome() {
        return selectedOutcome;
    }

    public IPreferenceCondition getConditionToAdd() {
        return conditionToAdd;
    }

    public IPreferenceOutcome getOutcomeToAdd() {
        return outcomeToAdd;
    }

    public OperatorConstants[] getConditionOperators() {
        return OperatorConstants.values().clone();
    }


    /* Public methods */
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
            addGlobalMessage("Condition updated for " + preferenceDetails.getPreferenceName(),
                    String.format(fmt, condition.getname(), condition.getoperator(), condition.getvalue()),
                    FacesMessage.SEVERITY_INFO);

        } else if (node.getData() instanceof IPreferenceOutcome) {

            IPreferenceOutcome outcome = (IPreferenceOutcome) node.getData();
            PreferenceDetails preferenceDetails = outcomeToPDMap.get(outcome);
            IPreference preference = outcomeToPreferenceMap.get(outcome);
            userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

            String fmt = "%s = %s (q=%s, p=%s)";
            addGlobalMessage("Outcome updated for " + preferenceDetails.getPreferenceName(),
                    String.format(fmt, outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
                    FacesMessage.SEVERITY_INFO);

        } else {

            addGlobalMessage("Cannot edit", "The node you selected cannot be edited", FacesMessage.SEVERITY_WARN);

        }


    }

    public void deleteSelectedNode() {
        log.trace("deleteSelectedNode()");

        TreeNode node = getSelectedTreeNode();
        if (node == null) {

            addGlobalMessage("Cannot delete", "No node selected", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() == null) {

            addGlobalMessage("Cannot delete", "No data in selected node", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() instanceof IPreferenceCondition) {

            IPreferenceCondition condition = (IPreferenceCondition) node.getData();
            PreferenceDetails preferenceDetails = conditionToPDMap.get(condition);
            IPreference preference = conditionToPreferenceMap.get(condition);

            log.debug("Deleting condition...");
            IPreference parent = (IPreference) preference.getParent();
            // remove the preference from its parent
            parent.remove(preference);

            // TODO: remove child nodes?

            userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

            // clear down the preference tree, which will cause it to be rebuilt from the UPM
            preferencesRootNode = null;

            String fmt = "%s %s %s";
            addGlobalMessage("Condition removed for " + preferenceDetails.getPreferenceName(),
                    String.format(fmt, condition.getname(), condition.getoperator(), condition.getvalue()),
                    FacesMessage.SEVERITY_INFO);

        } else if (node.getData() instanceof IPreferenceOutcome) {

            IPreferenceOutcome outcome = (IPreferenceOutcome) node.getData();
            PreferenceDetails preferenceDetails = outcomeToPDMap.get(outcome);
            IPreference preference = outcomeToPreferenceMap.get(outcome);

            log.debug("Deleting outcome...");
            IPreference parent = (IPreference) preference.getParent();
            // remove the preference from its parent
            parent.remove(preference);

            // TODO: remove child nodes?

            userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

            // clear down the preference tree, which will cause it to be rebuilt from the UPM
            preferencesRootNode = null;

            String fmt = "%s = %s (q=%s, p=%s)";
            addGlobalMessage("Outcome removed for " + preferenceDetails.getPreferenceName(),
                    String.format(fmt, outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
                    FacesMessage.SEVERITY_INFO);

        } else {

            addGlobalMessage("Cannot delete", "The node you selected cannot be deleted", FacesMessage.SEVERITY_WARN);

        }
    }

    public boolean isShowAddCondition() {
        // ONLY time not to show "add condition" is when the root preference is selected, and it has no children

        if (selectedPreference != null) {
            IPreference preference = pdToPreferenceMap.get(selectedPreference);

            // only enable this if the preference has current children
            return hasConditionAsDirectChild(preference) || hasOutcomeAsDirectChild(preference);
        }

        return true;
    }

    public boolean isShowAddOutcome() {
        // DON'T show add outcome if:
        // - Adding a condition before anything

        return !"before".equals(addConditionMode);
    }

    public void addConditionAndOutcome() {

    }

    public void addConditionBefore() {

    }

    public void addConditionAfter() {

    }

    public void addOutcomeOnly() {

    }

    public void updateTreeSelection() {
        this.selectedCondition = null;
        this.selectedOutcome = null;
        this.selectedPreference = null;

        if (getSelectedTreeNode() == null) {
//            super.addGlobalMessage("No node selected", "No node selected", FacesMessage.SEVERITY_WARN);

        } else if (PREFERENCE_NODE.equals(getSelectedTreeNode().getType())) {
            PreferenceDetails preferenceDetails = (PreferenceDetails) getSelectedTreeNode().getData();
            log.debug("setting preference to edit: " + preferenceDetails);
            this.selectedPreference = preferenceDetails;

//            super.addGlobalMessage(preferenceDetails.getPreferenceName(), "Selected edit on preference", FacesMessage.SEVERITY_INFO);
        } else if (CONDITION_NODE.equals(getSelectedTreeNode().getType())) {
            IPreferenceCondition condition = (IPreferenceCondition) getSelectedTreeNode().getData();
            log.debug("setting condition to edit: " + condition);
            this.selectedCondition = condition;

//            super.addGlobalMessage(condition.getname(), "Selected edit on condition", FacesMessage.SEVERITY_INFO);
        } else if (OUTCOME_NODE.equals(getSelectedTreeNode().getType())) {
            IPreferenceOutcome outcome = (IPreferenceOutcome) getSelectedTreeNode().getData();
            log.debug("setting outcome to edit: " + outcome);
            this.selectedOutcome = outcome;

//            super.addGlobalMessage(outcome.getparameterName(), "Selected edit on outcome", FacesMessage.SEVERITY_INFO);
        } else {
//            super.addGlobalMessage("No node selected", "You've probably tried to edit the root node. Well done. You win a gold star", FacesMessage.SEVERITY_WARN);
        }
    }

    public void selectAddConditionBefore() {
        this.addConditionMode = "before";
    }

    public void selectAddConditionAfter() {
        this.addConditionMode = "after";
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

        // sort the preferences by name
        Collections.sort(detailsList, new Comparator<PreferenceDetails>() {
            @Override
            public int compare(PreferenceDetails o1, PreferenceDetails o2) {
                return o1.getPreferenceName().compareTo(o2.getPreferenceName());
            }
        });

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

        pdToPreferenceMap.put(preferenceDetails, preference);

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

        List<IPreference> conditions = new ArrayList<IPreference>();
        List<IPreference> outcomes = new ArrayList<IPreference>();

        while (e.hasMoreElements()) {
            IPreference ele = e.nextElement();
            if (ele == preference) continue;
            if (ele.isBranch()) {
                // this is a CONDITION
                conditions.add(ele);
//                populateConditionNode(ele, node, preferenceDetails);
            } else if (ele.isLeaf()) {
                // this is an OUTCOME
                outcomes.add(ele);
//                populateOutcomeNode(ele, node, preferenceDetails);
            }
        }

        // process conditions
        if (conditions.size() > 0) {
            Collections.sort(conditions, new Comparator<IPreference>() {
                @Override
                public int compare(IPreference o1, IPreference o2) {
                    int c;
                    IPreferenceCondition c1 = o1.getCondition();
                    IPreferenceCondition c2 = o2.getCondition();

                    // compare by name
                    c = c1.getname().compareTo(c2.getname());
                    if (c != 0)
                        return c;

                    // if same, compare by operator
                    c = c1.getoperator().getDescription().compareTo(c2.getoperator().getDescription());
                    if (c != 0)
                        return c;

                    // if same, compare by value
                    c = c1.getvalue().compareTo(c2.getvalue());
                    return c;
                }
            });

            for (IPreference ele : conditions) {
                populateConditionNode(ele, node, preferenceDetails);
            }
        }
        // process outcomes only if no conditions found
        else if (outcomes.size() > 0) {
            Collections.sort(outcomes, new Comparator<IPreference>() {
                @Override
                public int compare(IPreference o1, IPreference o2) {
                    int c;
                    IPreferenceOutcome c1 = o1.getOutcome();
                    IPreferenceOutcome c2 = o2.getOutcome();

                    // compare by value
                    c = c1.getvalue().compareTo(c2.getvalue());
                    return c;
                }
            });

            for (IPreference ele : outcomes) {
                populateOutcomeNode(ele, node, preferenceDetails);
            }
        }
    }

    private boolean hasConditionAsDirectChild(IPreference parent) {
        Enumeration<IPreference> e = parent.breadthFirstEnumeration();

        while (e.hasMoreElements()) {
            IPreference ele = e.nextElement();
            if (ele == parent) continue;

            if (ele.isBranch()) {
                return true;
            }

        }

        return false;
    }

    private boolean hasOutcomeAsDirectChild(IPreference parent) {
        Enumeration<IPreference> e = parent.breadthFirstEnumeration();

        boolean foundOutcome = false;
        while (e.hasMoreElements()) {
            IPreference ele = e.nextElement();
            if (ele == parent) continue;

            if (ele.isBranch()) {
                return false;
            }

            if (ele.isLeaf()) {
                foundOutcome = true;
            }
        }

        return foundOutcome;
    }

}
