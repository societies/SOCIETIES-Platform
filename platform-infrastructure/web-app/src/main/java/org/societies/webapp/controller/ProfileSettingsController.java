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
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.*;
import org.societies.webapp.service.UserService;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean(name = "profileSettings") // JSF
@SessionScoped // JSF
public class ProfileSettingsController extends BasePageController {

    private class PreferenceDetailsComparator implements Comparator<PreferenceDetails> {
        @Override
        public int compare(PreferenceDetails o1, PreferenceDetails o2) {
            if (o1 == null && o2 == null)
                return 0;

            if (o1 == null)
                return -1;

            if (o2 == null)
                return 1;

            if (o1.getPreferenceName() == null && o2.getPreferenceName() == null)
                return 0;

            if (o1.getPreferenceName() == null)
                return -1;

            if (o2.getPreferenceName() == null)
                return 1;

            return o1.getPreferenceName().compareTo(o2.getPreferenceName());
        }
    }

    public static final String OUTCOME_NODE = "outcome";
    public static final String CONDITION_NODE = "condition";
    public static final String PREFERENCE_NODE = "preference";

    @ManagedProperty(value = "#{userService}")
    private UserService userService;

    @ManagedProperty(value = "#{userPreferenceConditionMonitor}")
    private IUserPreferenceConditionMonitor userPreferenceConditionMonitor;

    @ManagedProperty(value = "#{internalCtxBroker}")
    private ICtxBroker internalCtxBroker;

    @ManagedProperty(value = "#{serviceDiscovery}")
    private IServiceDiscovery serviceDiscovery;

    // this is not a JSF managed property - it's contained within userPreferenceConditionMonitor
    private IUserPreferenceManagement userPreferenceManagement;

    private boolean treeChangesMade = false;
    private TreeNode preferencesRootNode;
    private TreeNode selectedTreeNode;
    private PreferenceDetails selectedPreference;
    private IPreferenceCondition selectedCondition;
    private IPreferenceOutcome selectedOutcome;
    private ContextPreferenceCondition conditionToAdd = new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "", "");
    private PreferenceOutcome outcomeToAdd = new PreferenceOutcome(null, "", "", "");
    private String addConditionMode;
    private String newPreferenceName;

    /* The following are used for the edit/delete methods, to map a condition/outcome to the parent preference */
    private final Map<IPreferenceCondition, PreferenceDetails> conditionToPDMap = new HashMap<IPreferenceCondition, PreferenceDetails>();
    private final Map<IPreferenceOutcome, PreferenceDetails> outcomeToPDMap = new HashMap<IPreferenceOutcome, PreferenceDetails>();
    private final Map<IPreferenceCondition, IPreference> conditionToPreferenceMap = new HashMap<IPreferenceCondition, IPreference>();
    private final Map<IPreferenceOutcome, IPreference> outcomeToPreferenceMap = new HashMap<IPreferenceOutcome, IPreference>();
    private final Map<PreferenceDetails, IPreference> pdToPreferenceMap = new HashMap<PreferenceDetails, IPreference>();
    private final Map<PreferenceDetails, IPreferenceTreeModel> pdToPreferenceTreeModelMap = new HashMap<PreferenceDetails, IPreferenceTreeModel>();
    private Service newPreferenceService;
    private List<Service> availableServices;


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
        if (userPreferenceConditionMonitor == null)
            log.error("setUserPreferenceConditionMonitor() = null");
        else
            log.trace("setUserPreferenceConditionMonitor() = " + userPreferenceConditionMonitor.toString());

        this.userPreferenceConditionMonitor = userPreferenceConditionMonitor;

        loadPreferenceTreeData();
    }

    @SuppressWarnings("UnusedDeclaration")
    public IUserPreferenceManagement getUserPreferenceManagement() {
        return userPreferenceManagement;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setUserPreferenceManagement(IUserPreferenceManagement userPreferenceManagement) {
        this.userPreferenceManagement = userPreferenceManagement;
    }

    @SuppressWarnings("UnusedDeclaration")
    public ICtxBroker getInternalCtxBroker() {
        return internalCtxBroker;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {
        this.internalCtxBroker = internalCtxBroker;
    }

    @SuppressWarnings("UnusedDeclaration")
    public IServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /* Web app Getters and Setters */
    public TreeNode getPreferencesRootNode() {
//        log.trace("getPreferencesRootNode()");
        if (preferencesRootNode == null)
            populatePreferencesRootNode();

        return preferencesRootNode;
    }

    public void setSelectedTreeNode(TreeNode selectedTreeNode) {
//        log.trace("setSelectedTreeNode() = " + selectedTreeNode);
        this.selectedTreeNode = selectedTreeNode;
    }

    public TreeNode getSelectedTreeNode() {
//        log.trace("getSelectedTreeNode() = " + selectedTreeNode);
        return selectedTreeNode;
    }

    @SuppressWarnings("UnusedDeclaration")
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

    public String[] getAvailableCtxAttributeTypes() {
        return CtxAttributeTypes.ALL_TYPES;
    }

    private String[] availablePreferenceNames;

    public String[] getAvailablePreferenceNames() {
        if (availablePreferenceNames == null) {
            List<PersonalisablePreferenceIdentifier> knownPersonalisablePreferences = userPreferenceManagement.getKnownPersonalisablePreferences();
            List<String> names = new ArrayList<String>();

            for (PersonalisablePreferenceIdentifier pref : knownPersonalisablePreferences) {
                names.add(pref.getPreferenceName());
            }

            availablePreferenceNames = names.toArray(new String[names.size()]);
            log.trace("Found " + availablePreferenceNames.length + " preference names: " + Arrays.toString(availablePreferenceNames));
        }

        return availablePreferenceNames;
    }


    public void setNewPreferenceName(String newPreferenceName) {
        this.newPreferenceName = newPreferenceName;
    }

    public String getNewPreferenceName() {
        return newPreferenceName;
    }

    public void setNewPreferenceService(Service newPreferenceService) {
        this.newPreferenceService = newPreferenceService;
    }

    public Service getNewPreferenceService() {
        return newPreferenceService;
    }

    // this method may be called a few times in the same request
    public synchronized List<Service> getAvailableServices() {
        if (availableServices == null) {
            try {
                availableServices = serviceDiscovery.getServices(userService.getIdentity()).get();

            } catch (Exception e) {
                addGlobalMessage("Error loading services list", e.getMessage(), FacesMessage.SEVERITY_ERROR);
                log.error("Error loading services list", e);

                // ensure we don't get the error again
                availableServices = new ArrayList<Service>();
            }
        }

        return availableServices;
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

    public boolean isTreeChangesMade() {
        return treeChangesMade;
    }

    /* Public methods */
    public void savePreferenceState() {
        log.trace("savePreferenceState()");
        treeChangesMade = true;

        TreeNode node = getSelectedTreeNode();
        if (node == null) {

            addGlobalMessage("Cannot edit", "No node selected", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() == null) {

            addGlobalMessage("Cannot edit", "No data in selected node", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() instanceof IPreferenceCondition) {

            IPreferenceCondition condition = (IPreferenceCondition) node.getData();
            PreferenceDetails preferenceDetails = conditionToPDMap.get(condition);
//            IPreference preference = conditionToPreferenceMap.get(condition);

            if (condition.getoperator() == null) {
                addGlobalMessage("Condition NOT updated for " + preferenceDetails.getPreferenceName(),
                        "Operator was null - something has gone wrong",
                        FacesMessage.SEVERITY_ERROR);
//                log.trace("Operator was null - Condition NOT updated for " + preferenceDetails.getPreferenceName());
            } else {

//                userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

                String fmt = "%s %s %s";
                addGlobalMessage("Condition updated for " + preferenceDetails.getPreferenceName(),
                        String.format(fmt, condition.getname(), condition.getoperator(), condition.getvalue()),
                        FacesMessage.SEVERITY_INFO);
//                log.trace("Condition updated for " + preferenceDetails.getPreferenceName());
            }

        } else if (node.getData() instanceof IPreferenceOutcome) {

            IPreferenceOutcome outcome = (IPreferenceOutcome) node.getData();
            PreferenceDetails preferenceDetails = outcomeToPDMap.get(outcome);
            IPreference preference = outcomeToPreferenceMap.get(outcome);

            if (preferenceDetails == null) {
                addGlobalMessage("Cannot edit", "preferenceDetails was null", FacesMessage.SEVERITY_ERROR);
            } else if (preference == null) {
                addGlobalMessage("Cannot edit", "preference was null", FacesMessage.SEVERITY_ERROR);
            } else {

//                userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

                String fmt = "%s = %s (q=%s, p=%s)";
                addGlobalMessage("Outcome updated for " + preferenceDetails.getPreferenceName(),
                        String.format(fmt, outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
                        FacesMessage.SEVERITY_INFO);
//                log.trace("Outcome updated for " + preferenceDetails.getPreferenceName());
            }
        } else {

            addGlobalMessage("Cannot edit", "The node you selected cannot be edited", FacesMessage.SEVERITY_WARN);

        }

        // clears all stored data and forces it to be reloaded from the UserPrefManagement service
//        clearData();
        preferencesRootNode = null; // force redraw of the tree
    }

    public void deleteSelectedNode() {
        log.trace("deleteSelectedNode()");
        treeChangesMade = true;

        TreeNode node = getSelectedTreeNode();
        if (node == null) {

            addGlobalMessage("Cannot delete", "No node selected", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() == null) {

            addGlobalMessage("Cannot delete", "No data in selected node", FacesMessage.SEVERITY_WARN);

        } else if (node.getData() instanceof PreferenceDetails) {

            PreferenceDetails preferenceDetails = (PreferenceDetails) node.getData();

            userPreferenceManagement.deletePreference(userService.getIdentity(), preferenceDetails);

            pdToPreferenceTreeModelMap.remove(preferenceDetails);
            pdToPreferenceMap.remove(preferenceDetails);

            addGlobalMessage("Preference " + preferenceDetails.getPreferenceName() + " removed",
                    "The preference, including all conditions and outcomes, has been removed",
                    FacesMessage.SEVERITY_INFO);

        } else if (node.getData() instanceof IPreferenceCondition) {

            IPreferenceCondition condition = (IPreferenceCondition) node.getData();
            PreferenceDetails preferenceDetails = conditionToPDMap.get(condition);
            IPreference preference = conditionToPreferenceMap.get(condition);

            log.debug("Deleting condition...");
            IPreference parent = (IPreference) preference.getParent();
            // remove the preference from its parent
            parent.remove(preference);

//            userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

            String fmt = "%s %s %s";
            addGlobalMessage("Condition removed for " + preferenceDetails.getPreferenceName(),
                    String.format(fmt, condition.getname(), condition.getoperator(), condition.getvalue()),
                    FacesMessage.SEVERITY_INFO);
//            log.trace("Condition removed for " + preferenceDetails.getPreferenceName());

        } else if (node.getData() instanceof IPreferenceOutcome) {

            IPreferenceOutcome outcome = (IPreferenceOutcome) node.getData();
            PreferenceDetails preferenceDetails = outcomeToPDMap.get(outcome);
            IPreference preference = outcomeToPreferenceMap.get(outcome);

            log.trace("Deleting outcome...");
            IPreference parent = (IPreference) preference.getParent();
            // remove the preference from its parent
            parent.remove(preference);

//            userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, preference);

            String fmt = "%s = %s (q=%s, p=%s)";
            addGlobalMessage("Outcome removed for " + preferenceDetails.getPreferenceName(),
                    String.format(fmt, outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
                    FacesMessage.SEVERITY_INFO);
//            log.trace("Outcome removed for " + preferenceDetails.getPreferenceName());

        } else {

            addGlobalMessage("Cannot delete", "The node you selected cannot be deleted", FacesMessage.SEVERITY_WARN);

        }

        // clears all stored data and forces it to be reloaded from the UserPrefManagement service
//        clearData();
        preferencesRootNode = null; // force redraw of the tree
    }

    public void addPreference() {
        log.trace("addPreference()");
        treeChangesMade = true;

        String prefName = getNewPreferenceName(); // set from the GUI

        if (prefName == null || "".equals(prefName)) {
            addGlobalMessage("Preference NOT added", "The preference name cannot be empty", FacesMessage.SEVERITY_WARN);
            return;
        }

        PreferenceOutcome outcome = new PreferenceOutcome(null, "", prefName, "default");

        PreferenceDetails preferenceDetails = new PreferenceDetails();
        preferenceDetails.setPreferenceName(prefName);

        if (getNewPreferenceService() != null) {
            preferenceDetails.setServiceID(getNewPreferenceService().getServiceIdentifier());
//            preferenceDetails.setServiceType(getNewPreferenceService().getServiceType());
        }

        PreferenceTreeNode outcomeNode = new PreferenceTreeNode();
        outcomeNode.setUserObject(outcome);

        IPreference preferenceNode = new PreferenceTreeNode();
        preferenceNode.setUserObject(preferenceDetails);
        PreferenceTreeModel model = new PreferenceTreeModel(preferenceNode);

        pdToPreferenceTreeModelMap.put(preferenceDetails, model);
        pdToPreferenceMap.put(preferenceDetails, preferenceNode);
//        populatePreferenceNode(preferenceDetails, model);

        setNewPreferenceName("");
        setNewPreferenceService(null);

        userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, outcomeNode);

        addGlobalMessage("Preference added", "The preference " + prefName + " was added", FacesMessage.SEVERITY_INFO);
//        log.trace("The preference " + prefName + " was added");

        // clears all stored data and forces it to be reloaded from the UserPrefManagement service
//        clearData();
        preferencesRootNode = null; // force redraw of the tree
    }

    public void addConditionOnly() {
        log.trace("addConditionOnly()");
        treeChangesMade = true;

        ContextPreferenceCondition newCondition = conditionToAdd;
        conditionToAdd = new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "", "");

        PreferenceDetails preferenceDetails;
        IPreference parentObject;
        IPreference selectedObject;

        if (selectedOutcome != null) {
            preferenceDetails = outcomeToPDMap.get(selectedOutcome);
            selectedObject = outcomeToPreferenceMap.get(selectedOutcome);
            parentObject = (IPreference) selectedObject.getParent();
            log.trace("Adding condition before outcome");
        } else if (selectedCondition != null) {
            preferenceDetails = conditionToPDMap.get(selectedCondition);
            selectedObject = conditionToPreferenceMap.get(selectedCondition);
            parentObject = (IPreference) selectedObject.getParent();
            log.trace("Adding condition before condition");
        } else {
            addGlobalMessage("Add outcome failed",
                    "PreferenceDetails was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("PreferenceDetails was null, cannot save new condition");
            return;
        }

        // validate
        if (newCondition.getname() == null || newCondition.getname().equals("")
                || newCondition.getoperator() == null
                || newCondition.getvalue() == null || newCondition.getvalue().equals("")) {
            addGlobalMessage("Add condition after " + parentObject.getUserObject().toString(),
                    "Value(s) null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("Value(s) null - something has gone wrong");
            return;
        }


        if (parentObject == null) {
            addGlobalMessage("Add outcome for " + preferenceDetails.getPreferenceName(),
                    "conditionPref was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("conditionPref was null, cannot save new outcome");
            return;
        }

//        log.debug("preferenceDetails=" + preferenceDetails.toString());
//        log.debug("selectedObject=" + selectedObject.toString());
//        log.debug("parentObject=" + parentObject.toString());

        log.trace("Creating objects...");
        IPreference newConditionPreference = new PreferenceTreeNode();
        newConditionPreference.setUserObject(newCondition);


        // Retrieve a context attribute with a string value
        // TODO: need to pick CtxAttributeType from a list
        String ctxAttributeType = newCondition.getname();

        try {
            Future<List<CtxIdentifier>> idsFuture = this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, ctxAttributeType);
            List<CtxIdentifier> ids = idsFuture.get();

            if (ids.size() == 0) {
                log.error("no identifiers found for '" + ctxAttributeType + "'");
            } else {
                log.debug("Identifiers for " + ctxAttributeType + ":...");
                for (CtxIdentifier id : ids) {
                    log.debug(id.toString());
                }

                newCondition.setCtxIdentifier((CtxAttributeIdentifier) ids.get(0));
            }

        } catch (Exception e) {
            log.error("ExecutionException", e);

            addGlobalMessage("Error saving condition",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            return;
        }


        if ("before".equals(addConditionMode)) {
            log.trace("Adding BEFORE");
            // This condition goes BEFORE the selected object, and AFTER the selected object's parent
            parentObject.remove(selectedObject);
            parentObject.add(newConditionPreference);
            newConditionPreference.add(selectedObject);

        } else if ("after".equals(addConditionMode)) {
            log.trace("Adding AFTER");
            // this condition goes AFTER the selected object
//            Enumeration<IPreference> selectedNodeChildren = newConditionPreference.postorderEnumeration();
//            while (selectedNodeChildren.hasMoreElements()) {
//                IPreference child = selectedNodeChildren.nextElement();
//
//                newConditionPreference.add(child);
//                selectedPreferenceObject.remove(child);
//            }

            selectedObject.add(newConditionPreference);
        }


//        try {
//            log.debug("parentPreferenceObject=" + parentObject.toString());
//            log.debug("selectedPreferenceObject=" + selectedObject.toString());
//            log.debug("newConditionPreference=" + newConditionPreference.toString());
//        } catch (Exception ex) {
//            log.warn("Error toStringing()", ex);
//        }

        log.trace("Storing...");
        conditionToPDMap.put(newCondition, preferenceDetails);
        conditionToPreferenceMap.put(newCondition, newConditionPreference);
//        userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, parentObject);


        String fmt = "%s %s %s";
        addGlobalMessage("Outcome added for " + preferenceDetails.getPreferenceName(),
                String.format(fmt, newCondition.getname(), newCondition.getoperator(), newCondition.getvalue()),
                FacesMessage.SEVERITY_INFO);
//        log.trace("Outcome added for " + preferenceDetails.getPreferenceName());

        // clears all stored data and forces it to be reloaded from the UserPrefManagement service
//        clearData();
        preferencesRootNode = null; // force redraw of the tree
    }

    public void addConditionAndOutcome() {
        log.trace("addConditionAndOutcome()");
        treeChangesMade = true;

        PreferenceDetails preferenceDetails;
        IPreference selectedPreferenceObject;
        ContextPreferenceCondition newCondition = conditionToAdd;
        PreferenceOutcome newOutcome = outcomeToAdd;
        conditionToAdd = new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "", "");
        outcomeToAdd = new PreferenceOutcome(null, "", "", "");


        if (selectedCondition != null) {
            preferenceDetails = conditionToPDMap.get(selectedCondition);
            selectedPreferenceObject = conditionToPreferenceMap.get(selectedCondition);
            log.trace("Adding condition and outcome after condition");
        } else if (selectedPreference != null) {
            preferenceDetails = (selectedPreference);
            selectedPreferenceObject = pdToPreferenceMap.get(selectedPreference);
            log.trace("Adding condition and outcome after preference");
        } else {
            addGlobalMessage("Add condition and outcome failed",
                    "PreferenceDetails was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("PreferenceDetails was null, cannot save new condition");
            return;
        }

        if (selectedPreferenceObject == null) {
            addGlobalMessage("Add outcome for " + preferenceDetails.getPreferenceName(),
                    "conditionPref was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("conditionPref was null, cannot save new outcome");
            return;
        }

        // validate
        if (newCondition.getname() == null || newCondition.getname().equals("")
                || newCondition.getoperator() == null
                || newCondition.getvalue() == null || newCondition.getvalue().equals("")) {

            addGlobalMessage("Add condition after " + selectedPreferenceObject.getUserObject().toString(),
                    "Value(s) null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("Value(s) null - something has gone wrong");
            return;
        }


        log.trace("Creating objects...");
        IPreference newConditionPreference = new PreferenceTreeNode();
        newConditionPreference.setUserObject(newCondition);


        // Retrieve a context attribute with a string value
        // TODO: need to pick CtxAttributeType from a list
        String ctxAttributeType = CtxAttributeTypes.LOCATION_SYMBOLIC;

        try {
            Future<List<CtxIdentifier>> idsFuture = this.internalCtxBroker.lookup(CtxModelType.ATTRIBUTE, ctxAttributeType);
            List<CtxIdentifier> ids = idsFuture.get();

            log.debug("Identifiers for " + ctxAttributeType + ":...");
            for (CtxIdentifier id : ids) {
                log.debug(id.toString());
            }

            newCondition.setCtxIdentifier((CtxAttributeIdentifier) ids.get(0));


        } catch (Exception e) {
            log.error("ExecutionException", e);

            addGlobalMessage("Error saving condition",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            return;
        }


        log.trace("Adding...");
        // and the outcome comes after the new preference
        IPreference newOutcomePreference = new PreferenceTreeNode();
        newOutcomePreference.setUserObject(newOutcome);
        newConditionPreference.add(newOutcomePreference);


        // this condition goes AFTER the selected object
        selectedPreferenceObject.add(newConditionPreference);

        log.trace("Storing...");
        conditionToPDMap.put(newCondition, preferenceDetails);
        conditionToPreferenceMap.put(newCondition, newConditionPreference);
//        userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, selectedPreferenceObject);


        String fmt = "%s %s %s => %s";
        addGlobalMessage("Outcome and condition added for " + preferenceDetails.getPreferenceName(),
                String.format(fmt, newCondition.getname(), newCondition.getoperator(), newCondition.getvalue(), newOutcome.getvalue()),
                FacesMessage.SEVERITY_INFO);
//        log.trace("Outcome and condition added for " + preferenceDetails.getPreferenceName());

        // clears all stored data and forces it to be reloaded from the UserPrefManagement service
//        clearData();
        preferencesRootNode = null; // force redraw of the tree
    }

    public void addOutcomeOnly() {
        log.trace("addOutcomeOnly()");
        treeChangesMade = true;

        PreferenceDetails preferenceDetails;
        IPreference parentPreferenceObject;
        PreferenceOutcome newOutcome = outcomeToAdd;
        outcomeToAdd = new PreferenceOutcome(null, "", "", "");


        if (selectedCondition != null) {
            preferenceDetails = conditionToPDMap.get(selectedCondition);
            parentPreferenceObject = conditionToPreferenceMap.get(selectedCondition);
            log.trace("Adding outcome to condition");
        } else if (selectedPreference != null) {
            preferenceDetails = selectedPreference;
            parentPreferenceObject = pdToPreferenceMap.get(selectedPreference);
            log.trace("Adding outcome to preference");
        } else {
            addGlobalMessage("Add outcome failed",
                    "PreferenceDetails was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("PreferenceDetails was null, cannot save new outcome");
            return;
        }

        if (preferenceDetails == null) {
            addGlobalMessage("Add outcome failed",
                    "PreferenceDetails was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("PreferenceDetails was null, cannot save new outcome");
            return;
        }

        // validate
        if (newOutcome.getvalue() == null || newOutcome.getvalue().equals("")) {
            addGlobalMessage("Add outcome for " + preferenceDetails.getPreferenceName(),
                    "Operator was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("Operator was null, cannot save new outcome");
            return;
        }

        if (parentPreferenceObject == null) {
            addGlobalMessage("Add outcome for " + preferenceDetails.getPreferenceName(),
                    "conditionPref was null - something has gone wrong",
                    FacesMessage.SEVERITY_ERROR);
            log.error("conditionPref was null, cannot save new outcome");
            return;
        }

        IPreference newOutcomePreference = new PreferenceTreeNode();
        newOutcomePreference.setUserObject(newOutcome);
        parentPreferenceObject.add(newOutcomePreference);

        outcomeToPDMap.put(newOutcome, preferenceDetails);
        outcomeToPreferenceMap.put(newOutcome, newOutcomePreference);

//        userPreferenceManagement.storePreference(userService.getIdentity(), preferenceDetails, parentPreferenceObject);

        String fmt = "%s = %s (q=%s, p=%s)";
        String msg = String.format(fmt, newOutcome.getparameterName(), newOutcome.getvalue(), newOutcome.getQualityofPreference(), newOutcome.getConfidenceLevel());
        addGlobalMessage("Outcome added for " + preferenceDetails.getPreferenceName(),
                msg,
                FacesMessage.SEVERITY_INFO);
//        if (log.isTraceEnabled())
//            log.trace("Outcome added for " + preferenceDetails.getPreferenceName() + ": " + msg);

        // clears all stored data and forces it to be reloaded from the UserPrefManagement service
//        clearData();
        preferencesRootNode = null; // force redraw of the tree
    }


    public void saveTreeChanges() {
        log.trace("saveTreeChanges()");
        Set<PreferenceDetails> preferenceDetailsSet = pdToPreferenceMap.keySet();

        for (PreferenceDetails pd : preferenceDetailsSet) {
            log.debug("Storing preference: " + pd.getPreferenceName());

            IPreferenceTreeModel model = pdToPreferenceTreeModelMap.get(pd);
            log.trace(model.toString());

            userPreferenceManagement.storePreference(userService.getIdentity(), pd, pdToPreferenceMap.get(pd));
        }

        loadPreferenceTreeData();
    }

    public void revertChanges() {
        log.trace("saveTreeChanges()");
        loadPreferenceTreeData();
    }

    public void updateTreeSelection() {
        this.selectedCondition = null;
        this.selectedOutcome = null;
        this.selectedPreference = null;

        if (getSelectedTreeNode() == null)
            return;

        if (PREFERENCE_NODE.equals(getSelectedTreeNode().getType())) {
            this.selectedPreference = (PreferenceDetails) getSelectedTreeNode().getData();
        } else if (CONDITION_NODE.equals(getSelectedTreeNode().getType())) {
            this.selectedCondition = (IPreferenceCondition) getSelectedTreeNode().getData();
        } else if (OUTCOME_NODE.equals(getSelectedTreeNode().getType())) {
            this.selectedOutcome = (IPreferenceOutcome) getSelectedTreeNode().getData();
        }
    }

    public void selectAddConditionBefore() {
        this.addConditionMode = "before";
    }

    public void selectAddConditionAfter() {
        this.addConditionMode = "after";
    }


    /* Private helper methods */
    private void clearData() {
        log.trace("clearData()");
        preferencesRootNode = null;
        selectedTreeNode = null;
        selectedPreference = null;
        selectedCondition = null;
        selectedOutcome = null;
        conditionToAdd = new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "", "");
        outcomeToAdd = new PreferenceOutcome(null, "", "", "");
        newPreferenceName = "";

        conditionToPDMap.clear();
        outcomeToPDMap.clear();
        conditionToPreferenceMap.clear();
        outcomeToPreferenceMap.clear();
        pdToPreferenceMap.clear();
        pdToPreferenceTreeModelMap.clear();

        treeChangesMade = false;
    }

    private void loadPreferenceTreeData() {
        log.trace("loadPreferenceTreeData()");
        clearData();

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
        Collections.sort(detailsList, new PreferenceDetailsComparator());

        log.trace("Loaded " + detailsList.size() + " preferences");
        for (PreferenceDetails preferenceDetails : detailsList) {
            try {
                IPreferenceTreeModel preferenceTreeModel =
                        userPreferenceManagement.getModel(userService.getIdentity(), preferenceDetails);

                log.trace(" - Loaded preference " + preferenceDetails.getPreferenceName());

                pdToPreferenceTreeModelMap.put(preferenceDetails, preferenceTreeModel);
            } catch (Exception ex) {
                log.error("Error loading preference tree model for " + preferenceDetails.getPreferenceName(), ex);
            }
        }
    }

    private void populatePreferencesRootNode() {
        preferencesRootNode = new DefaultTreeNode("Preferences", null);
        preferencesRootNode.setExpanded(true);

        Set<PreferenceDetails> allPreferenceDetails = pdToPreferenceTreeModelMap.keySet();

        for (PreferenceDetails preferenceDetails : allPreferenceDetails) {
            IPreferenceTreeModel preferenceTreeModel = pdToPreferenceTreeModelMap.get(preferenceDetails);

            populatePreferenceNode(preferenceDetails, preferenceTreeModel);
        }


    }

    private void populatePreferenceNode(PreferenceDetails preferenceDetails, IPreferenceTreeModel preferenceTreeModel) {
        if (preferenceTreeModel == null) {
            log.error("preferenceTreeModel was null in populatePreferenceNode for " + preferenceDetails.getPreferenceName());
            return;
        }

        TreeNode preferenceNode = new DefaultTreeNode(PREFERENCE_NODE,
                preferenceDetails,
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

        TreeNode conditionNode = new DefaultTreeNode(CONDITION_NODE,
                condition,
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

        TreeNode outcomeNode = new DefaultTreeNode(OUTCOME_NODE,
                outcome,
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
