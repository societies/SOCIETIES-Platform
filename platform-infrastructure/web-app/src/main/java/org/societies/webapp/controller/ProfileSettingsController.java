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
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import java.util.Enumeration;
import java.util.List;

@Controller // Spring
@ManagedBean(name = "profileSettings") // JSF
@RequestScoped // JSF
@Scope("Request") // Spring
public class ProfileSettingsController extends BasePageController {

    @Autowired
    private UserService userService; // Spring dependency

    @Autowired
    private IUserPreferenceConditionMonitor userPreferenceConditionMonitor; // Spring dependency

    private IUserPreferenceManagement userPreferenceManagement;

    @Inject
    private LoginController loginController; // Java EE CDI dependency

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

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    //    @RequestMapping(value = "/profilesettings.html", method = RequestMethod.GET)
//    public ModelAndView initProfileSettings() {
//
//        if (!userService.isUserLoggedIn()) {
//            // redirect to login page
//            // TODO: popup login box
//            log.warn("User not logged in - redirecting to home");
//            return new ModelAndView(new RedirectView("index.html", true));
//        }
//
//        Map<String, Object> model = new HashMap<String, Object>();
//        ProfileSettingsForm form = new ProfileSettingsForm();
//
//        userService.loadUserDetailsFromCommMgr();
//        userService.loadUserDetailsIntoModel(model);
//
//        model.put("loggedIn", true); // can only be true by this point
//        model.put("form", form);
//
//        IIdentity ident = (IIdentity) model.get("identity");
//        populateUserDetails(form, ident);
//        StringBuilder sb = new StringBuilder();
//
//        populateProfileForm(form, ident, sb);
//
//        form.setPreferenceHtml(sb.toString());
//
//        return new ModelAndView("profilesettings", model);
//    }
//
//    private void populateUserDetails(ProfileSettingsForm form, IIdentity ident) {
//        form.setFullName(userService.getUsername());
//
//    }
//
//    private void populateProfileForm(ProfileSettingsForm form, IIdentity ident, StringBuilder sb) {
//
//        List<PreferenceDetails> detailsList = userPreferenceManagementService.getPreferenceDetailsForAllPreferences();
//        form.setPreferenceDetailsList(detailsList);
//
//        if (detailsList == null) return;
//
//
//        for (PreferenceDetails preferenceDetails : detailsList) {
//            IPreferenceTreeModel preferenceTreeModel = userPreferenceManagementService.getModel(ident, preferenceDetails);
//            form.getPreferenceDetailTreeModelMap().put(preferenceDetails, preferenceTreeModel);
//
////            log.debug("----------------------");
////            log.debug(preferenceDetails.getPreferenceName() + " / " + preferenceTreeModel.getPreferenceName());
////            log.debug(preferenceTreeModel.getServiceType());
////            log.debug(preferenceTreeModel.getLastModifiedDate().toString());
////            log.debug(preferenceTreeModel.getServiceID().getServiceInstanceIdentifier() + " / " + preferenceTreeModel.getServiceID().getIdentifier().toString());
//
//            sb.append("<div style=\"border: 1px solid #000; padding: 0 3px 3px 3px; \">");
//            sb.append("<div style=\"border: 1px solid #000; margin: 0px; padding 2px; width: 100%; background-color: #009; color: #fff; font-weight: bold; text-align: center; font-variant: small-caps; font-size: large;\">");
//            sb.append(preferenceDetails.getPreferenceName());
//            sb.append("</div>");
//            sb.append("<br>");
//            sb.append("Last modified: ");
//            sb.append(preferenceTreeModel.getLastModifiedDate().toString());
//            sb.append("\n");
//
//            IPreference preference = preferenceTreeModel.getRootPreference();
//            blah(preference, 0, sb);
//
//            sb.append("</div>\n");
//
//        }
//
//    }
//
//    private void blah(IPreference preference, int indent, StringBuilder sb) {
//
//        if (preference.getLevel() != indent) return;
//
//
//        String s_indent = "";
//        for (int i = 0; i < indent; i++) s_indent += "\t";
//
//        sb.append("\n");
//        sb.append(s_indent);
//        sb.append("<ul>\n");
//        sb.append(s_indent);
//        sb.append("<li>");
//
////        log.debug("+++" + Integer.toHexString(preference.hashCode()) + " d=" + preference.getDepth() + " l=" + preference.getLevel());
//
//        IPreferenceCondition condition = preference.getCondition();
//        if (condition != null) {
//            sb.append("[CONDITION] " + condition.getType() + ": " + condition.getname() + " " + condition.getoperator() + " " + condition.getvalue() + "<br/>");
////            log.debug(s_indent + "[CONDITION] " + condition.getType() + ": " + condition.getname() + " " + condition.getoperator() + " " + condition.getvalue());
//        }
//
//        IPreferenceOutcome outcome = preference.getOutcome();
//        if (outcome != null) {
//            sb.append("[OUTCOME] " + outcome.getparameterName() + "/" + outcome.getServiceType() + "/" + outcome.getvalue() + "/" + outcome.getQualityofPreference() + "/" + outcome.getConfidenceLevel() + "<br/>");
////            log.debug(s_indent + "[OUTCOME] " + outcome.getparameterName() + "/" + outcome.getServiceType() + "/" + outcome.getvalue() + "/" + outcome.getQualityofPreference() + "/" + outcome.getConfidenceLevel());
//        }
//
//        IPreference root = preference.getRoot();
//        if (root != null && condition == null && outcome == null) {
//            sb.append(s_indent + "[ROOT] " + root.toString() + "<br/>");
////            log.debug(s_indent + "[ROOT] " + root.toString());
//        }
//
////        Object userObject = preference.getUserObject();
////        if (userObject != null) {
////            sb.append(s_indent + "[USER_OBJECT] " + userObject.getClass() + ": " + userObject.toString() + "<br/>");
//////            log.debug(s_indent + "[USER_OBJECT] " + userObject.getClass() + ": " + userObject.toString());
////        }
//
//        Enumeration<IPreference> e = preference.postorderEnumeration();
//        while (e.hasMoreElements()) {
//            IPreference ele = e.nextElement();
//            if (ele == preference) continue;
//
//            blah(ele, indent + 1, sb);
//        }
//
//        sb.append(s_indent);
//        sb.append("</li>\n");
//        sb.append(s_indent);
//        sb.append("</ul>\n");
//
//    }

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
                    userPreferenceManagement.getModel(loginController.getIdentity(), preferenceDetails);

            IPreference preference = preferenceTreeModel.getRootPreference();

            populatePreferenceNode(preferenceDetails, preference);
        }
    }

    private void populatePreferenceNode(PreferenceDetails preferenceDetails, IPreference preference) {
        TreeNode preferenceNode = new DefaultTreeNode(preferenceDetails.getPreferenceName(), preferencesRootNode);
        preferenceNode.setExpanded(true);

        if (preference.isBranch()) {
            // this is a CONDITION
            populateConditionNode(preference, preferenceNode);
        } else if (preference.isLeaf()) {
            // this is an OUTCOME
            populateOutcomeNode(preference, preferenceNode);
        }

    }

    private void populateConditionNode(IPreference preference, TreeNode preferenceNode) {
        IPreferenceCondition condition = preference.getCondition();

        TreeNode conditionNode = new DefaultTreeNode(condition.getname() + " " + condition.getoperator() + " " + condition.getvalue(), preferenceNode);
        conditionNode.setExpanded(true);

        Enumeration<IPreference> e = preference.postorderEnumeration();
        while (e.hasMoreElements()) {
            IPreference ele = e.nextElement();
            if (ele == preference) continue;

            populateConditionNode(ele, conditionNode);
        }

    }

    private void populateOutcomeNode(IPreference preference, TreeNode conditionNode) {
        IPreferenceOutcome outcome = preference.getOutcome();

        String fmt = "$s = $s (q=$s, p=$s)";
        TreeNode outcomeNode = new DefaultTreeNode(
                String.format(fmt,
                        outcome.getparameterName(), outcome.getvalue(), outcome.getQualityofPreference(), outcome.getConfidenceLevel()),
                conditionNode);
        outcomeNode.setExpanded(true);

    }
}
