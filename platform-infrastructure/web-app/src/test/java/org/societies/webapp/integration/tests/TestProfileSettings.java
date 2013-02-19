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
package org.societies.webapp.integration.tests;


import org.junit.Before;
import org.junit.Test;
import org.societies.webapp.integration.selenium.SeleniumTest;
import org.societies.webapp.integration.selenium.components.*;
import org.societies.webapp.integration.selenium.pages.IndexPage;
import org.societies.webapp.integration.selenium.pages.ProfileSettingsPage;

public class TestProfileSettings extends SeleniumTest {
    private static final String USERNAME = "paddy";
    private static final String PASSWORD = "paddy";

    private ProfileSettingsPage profileSettingsPage;

    private ProfileSettingsPage.TreeNode preferenceTreeRoot;
    private ProfileSettingsPage.TreeNode bgColourNode;
    private ProfileSettingsPage.TreeNode bgColourHomeConditionNode;
    private ProfileSettingsPage.TreeNode bgColourWorkConditionNode;
    private ProfileSettingsPage.TreeNode bgColourHomeOutcomeNode;
    private ProfileSettingsPage.TreeNode bgColourWorkOutcomeNode;
    private ProfileSettingsPage.TreeNode volumeNode;
    private ProfileSettingsPage.TreeNode volumeHomeConditionNode;
    private ProfileSettingsPage.TreeNode volumeWorkConditionNode;
    private ProfileSettingsPage.TreeNode volumeHomeOutcomeNode;
    private ProfileSettingsPage.TreeNode volumeWorkOutcomeNode;

    @Before
    public void setupTest() {
        IndexPage indexPage = new IndexPage(getDriver());

        indexPage.doLogin(USERNAME, PASSWORD);

        profileSettingsPage = indexPage.navigateToProfileSettings();

        buildDefaultPreferenceTree();


    }

    private void buildDefaultPreferenceTree() {
        preferenceTreeRoot = new ProfileSettingsPage.TreeNode("Preferences", ProfileSettingsPage.TreeNodeType.ROOT);

        bgColourNode = new ProfileSettingsPage.TreeNode("bgColour", ProfileSettingsPage.TreeNodeType.PREFERENCE, preferenceTreeRoot);
        bgColourHomeConditionNode = new ProfileSettingsPage.TreeNode("locationSymbolic EQUALS home", ProfileSettingsPage.TreeNodeType.CONDITION, bgColourNode);
        bgColourWorkConditionNode = new ProfileSettingsPage.TreeNode("locationSymbolic EQUALS work", ProfileSettingsPage.TreeNodeType.CONDITION, bgColourNode);
        bgColourHomeOutcomeNode = new ProfileSettingsPage.TreeNode("bgColour red", ProfileSettingsPage.TreeNodeType.OUTCOME, bgColourHomeConditionNode);
        bgColourWorkOutcomeNode = new ProfileSettingsPage.TreeNode("bgColour black", ProfileSettingsPage.TreeNodeType.OUTCOME, bgColourWorkConditionNode);

        volumeNode = new ProfileSettingsPage.TreeNode("volume", ProfileSettingsPage.TreeNodeType.PREFERENCE, preferenceTreeRoot);
        volumeHomeConditionNode = new ProfileSettingsPage.TreeNode("locationSymbolic EQUALS home", ProfileSettingsPage.TreeNodeType.CONDITION, volumeNode);
        volumeWorkConditionNode = new ProfileSettingsPage.TreeNode("locationSymbolic EQUALS work", ProfileSettingsPage.TreeNodeType.CONDITION, volumeNode);
        volumeHomeOutcomeNode = new ProfileSettingsPage.TreeNode("volume 10", ProfileSettingsPage.TreeNodeType.OUTCOME, volumeHomeConditionNode);
        volumeWorkOutcomeNode = new ProfileSettingsPage.TreeNode("volume 50", ProfileSettingsPage.TreeNodeType.OUTCOME, volumeWorkConditionNode);
    }

    @Test
    public void userDetailsCorrect() {
        profileSettingsPage.verifyUsernameInTitle(USERNAME);
        profileSettingsPage.verifyUserDetails("paddy", "societies.local.macs.hw.ac.uk", "CSS_RICH",
                "paddy.societies.local.macs.hw.ac.uk", "paddy.societies.local.macs.hw.ac.uk");
    }

    @Test
    public void verifyTreeDisplayedCorrectly() {
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);
    }

    @Test
    public void openPopups_hitCancelButton_noChangesMade() {
        ProfileSettingsTreeContextMenu ctxMenu;

        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // ADD... TO PREFERENCE DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnPreferenceNode(
                new int[]{1},
                "volume");
        ProfileSettingsAddConditionAndOutcomeDialog addConditionAndOutcomeDialog = ctxMenu.clickAdd();
        addConditionAndOutcomeDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // ADD CONDITION BEFORE CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnConditionNode(
                new int[]{1, 0},
                "locationSymbolic EQUALS home");
        addConditionAndOutcomeDialog = ctxMenu.clickAddBefore();
        addConditionAndOutcomeDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // ADD CONDITION AND OUTCOME TO CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnConditionNode(
                new int[]{1, 0},
                "locationSymbolic EQUALS home");
        addConditionAndOutcomeDialog = ctxMenu.clickAddConditionAndOutcome();
        addConditionAndOutcomeDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // EDIT CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnConditionNode(
                new int[]{0, 0},
                "locationSymbolic EQUALS home");
        ProfileSettingsEditConditionDialog editConditionDialog = ctxMenu.clickEditCondition();
        // change values to ensure it's not accidentally persisted
        editConditionDialog.setName("newName");
        editConditionDialog.setOperator("greater than");
        editConditionDialog.setValue("newValue");
        editConditionDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // DELETE CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnConditionNode(
                new int[]{1, 0},
                "locationSymbolic EQUALS home");
        ProfileSettingsDeleteDialog deleteDialog = ctxMenu.clickDelete();
        deleteDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // ADD CONDITION BEFORE OUTCOME DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnOutcomeNode(
                new int[]{1, 0, 0},
                "volume 10");
        addConditionAndOutcomeDialog = ctxMenu.clickAddBefore();
        addConditionAndOutcomeDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // EDIT OUTCOME DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnOutcomeNode(
                new int[]{0, 1, 0},
                "bgColour black");
        ProfileSettingsEditOutcomeDialog editOutcomeDialog = ctxMenu.clickEditOutcome();
        // change values to ensure it's not accidentally persisted
        editOutcomeDialog.setValue("newValue2");
        editOutcomeDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // DELETE OUTCOME DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnOutcomeNode(
                new int[]{1, 0, 0},
                "volume 10");
        deleteDialog = ctxMenu.clickDelete();
        deleteDialog.clickCancel();
        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);



        // refresh page, verify again
        getDriver().get(getDriver().getCurrentUrl());
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);
    }

    @Test
    public void addConditionAndOutcomeDialog_correctFieldsShown() {
        ProfileSettingsTreeContextMenu ctxMenu;

        // just to make sure the test is set up correctly
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);


        // ADD... TO PREFERENCE DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnPreferenceNode(
                new int[]{1},
                "volume");
        ProfileSettingsAddConditionAndOutcomeDialog addConditionAndOutcomeDialog = ctxMenu.clickAdd();
        addConditionAndOutcomeDialog.verifyConditionFieldsVisible();
        addConditionAndOutcomeDialog.verifyOutcomeFieldsVisible();
        addConditionAndOutcomeDialog.clickCancel();


        // ADD CONDITION BEFORE CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnConditionNode(
                new int[]{1, 0},
                "locationSymbolic EQUALS home");
        addConditionAndOutcomeDialog = ctxMenu.clickAddBefore();
        addConditionAndOutcomeDialog.verifyConditionFieldsVisible();
        addConditionAndOutcomeDialog.verifyOutcomeFieldsNotVisible();
        addConditionAndOutcomeDialog.clickCancel();


        // ADD CONDITION AND OUTCOME TO CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnConditionNode(
                new int[]{1, 0},
                "locationSymbolic EQUALS home");
        addConditionAndOutcomeDialog = ctxMenu.clickAddConditionAndOutcome();
        addConditionAndOutcomeDialog.verifyConditionFieldsVisible();
        addConditionAndOutcomeDialog.verifyOutcomeFieldsVisible();
        addConditionAndOutcomeDialog.clickCancel();


        // ADD CONDITION BEFORE OUTCOME DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnOutcomeNode(
                new int[]{1, 0, 0},
                "volume 10");
        addConditionAndOutcomeDialog = ctxMenu.clickAddBefore();
        addConditionAndOutcomeDialog.verifyConditionFieldsVisible();
        addConditionAndOutcomeDialog.verifyOutcomeFieldsNotVisible();
        addConditionAndOutcomeDialog.clickCancel();

    }

    @Test
    public void editCondition_update3values_correctValuesUpdated() {
        ProfileSettingsTreeContextMenu ctxMenu;

        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);

        // EDIT CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnConditionNode(
                new int[]{0, 0},
                "locationSymbolic EQUALS home");
        ProfileSettingsEditConditionDialog editConditionDialog = ctxMenu.clickEditCondition();

        // change values to ensure it's not accidentally persisted
        editConditionDialog.setName("newName");
        editConditionDialog.setOperator("greater than");
        editConditionDialog.setValue("newValue");
        editConditionDialog.clickSave();

        // this is what the node should now display
        bgColourWorkConditionNode.text = "newName GREATER THAN newValue";

        // verify tree
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);
        // refresh page, verify again
        String url = getDriver().getCurrentUrl();
        getDriver().get(BASE_URL);
        getDriver().get(url);
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);
    }

    @Test
    public void editOutcome_updateValue_correctValuesUpdated() {
        ProfileSettingsTreeContextMenu ctxMenu;

        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);

        // EDIT CONDITION DIALOG
        ctxMenu = profileSettingsPage.openContextMenuOnOutcomeNode(
                new int[]{0, 1, 0},
                "bgColour black");
        ProfileSettingsEditOutcomeDialog dialog = ctxMenu.clickEditOutcome();

        // change values to ensure it's not accidentally persisted
        dialog.setValue("blue");
        dialog.clickSave();

        // this is what the node should now display
        bgColourWorkOutcomeNode.text = "bgColour blue";

        // verify tree
//        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);
        // refresh page, verify again
        String url = getDriver().getCurrentUrl();
        getDriver().get(BASE_URL);
        getDriver().get(url);
        profileSettingsPage.verifyPreferenceTreeState(preferenceTreeRoot);
    }
}
