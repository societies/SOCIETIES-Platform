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
package org.societies.webapp.integration.selenium.pages;

import junit.framework.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.societies.webapp.integration.selenium.components.ProfileSettingsTreeContextMenu;

import java.util.ArrayList;
import java.util.List;

public class ProfileSettingsPage extends BaseSocietiesPage {
    private static final String TITLE = "//h4[@class='form_title']";
    private static final String IDENTIFIER = "//li[contains(text(), 'Identifier:')]";
    private static final String DOMAIN = "//li[contains(text(), 'Domain:')]";
    private static final String TYPE = "//li[contains(text(), 'Type:')]";
    private static final String JID = "//li[contains(text(), 'JID:')]";
    private static final String BARE_JID = "//li[contains(text(), 'Bare JID:')]";

    private static final String ROOT_NODE_XPATH = "//*[@id='mainForm:preferenceTree:lblNode_default']";
    private static final String PREF_NODE_XPATH = "//*[starts-with(@id,'mainForm:preferenceTree:') and contains(@id,':lblNode_preference')]";
//    private static final String CONDITION_NODE_XPATH = "//*[starts-with(@id,'mainForm:preferenceTree:') and contains(@id,':lblNode_condition')]";
//    private static final String OUTCOME_NODE_XPATH = "//*[starts-with(@id,'mainForm:preferenceTree:') and contains(@id,':lblNode_outcome')]";

    private static final String PREF_NODE_BY_INDEX = "//*[@id='mainForm:preferenceTree:%s:lblNode_preference']";
    private static final String CONDITION_NODE_BY_INDEX = "//*[@id='mainForm:preferenceTree:%s:lblNode_condition']";
    private static final String OUTCOME_NODE_BY_INDEX = "//*[@id='mainForm:preferenceTree:%s:lblNode_outcome']";

    private static final String PREF_NODE_BY_PARTIAL_INDEX = "//*[starts-with(@id,'mainForm:preferenceTree:%s') and contains(@id, ':lblNode_preference')]";
    private static final String CONDITION_NODE_BY_PARTIAL_INDEX = "//*[starts-with(@id,'mainForm:preferenceTree:%s') and contains(@id, ':lblNode_condition')]";
    private static final String OUTCOME_NODE_BY_PARTIAL_INDEX = "//*[starts-with(@id,'mainForm:preferenceTree:%s') and contains(@id, ':lblNode_outcome')]";

    private static final String SAVE_TREE_BUTTON_XPATH = "//button[@id='mainForm:saveButton']";

    private static final String NODE_ID_SEPARATOR = "_";

    public enum TreeNodeType {PREFERENCE, CONDITION, ROOT, OUTCOME}

    public static class TreeNode {

        public String text;
        public TreeNodeType type;
        public final List<TreeNode> subNodes = new ArrayList<TreeNode>();

        public TreeNode(String text, TreeNodeType type) {
            this.text = text;
            this.type = type;
        }

        public TreeNode(String text, TreeNodeType type, TreeNode parentNode) {
            this(text, type);
            parentNode.subNodes.add(this);
        }
    }

    public ProfileSettingsPage(WebDriver driver) {
        super(driver);
    }

    public void verifyUsernameInTitle(String username) {
        WebElement title = waitUntilVisible(By.xpath(TITLE));

        Assert.assertTrue("Username not found in title: \n" + title.getText(),
                title.getText().contains(username));
    }

    public void verifyUserDetails(String identifier, String domain, String type, String jid, String bareJid) {
        WebElement identifier_element = waitUntilVisible(By.xpath(IDENTIFIER));
        Assert.assertTrue("Identifier not found in element: \n" + identifier_element.getText(), identifier_element.getText().contains(identifier));

        WebElement domain_element = waitUntilVisible(By.xpath(DOMAIN));
        Assert.assertTrue("Domain not found in element: \n" + domain_element.getText(), domain_element.getText().contains(domain));

        WebElement type_element = waitUntilVisible(By.xpath(TYPE));
        Assert.assertTrue("Type not found in element: \n" + type_element.getText(), type_element.getText().contains(type));

        WebElement jid_element = waitUntilVisible(By.xpath(JID));
        Assert.assertTrue("JID not found in element: \n" + jid_element.getText(), jid_element.getText().contains(jid));

        WebElement barejid_element = waitUntilVisible(By.xpath(BARE_JID));
        Assert.assertTrue("Bare JID not found in element: \n" + barejid_element.getText(), barejid_element.getText().contains(bareJid));

    }


    public void verifyPreferenceTreeState(TreeNode rootNode) {
        verifyPreferenceTreeState(rootNode, new int[]{}, 1);
    }

    private void verifyPreferenceTreeState(TreeNode rootNode, int[] parentIDs, int expectedSiblings) {
        // always wait until the root node is visible - it ensures that the page is loaded
        waitUntilVisible(By.xpath(ROOT_NODE_XPATH));


        String root_id = "";
        for (int i = 0; i < parentIDs.length; i++) {
            root_id += parentIDs[i];

            if (i < parentIDs.length - 1)
                root_id += NODE_ID_SEPARATOR;
        }

        // now that we know the root node is visible, we can just do a simple verify
        // this will stop time being wasted if the test is going to fail - it all adds up if this method is repeated often
        String xpath;
        switch (rootNode.type) {
            case ROOT:
                xpath = ROOT_NODE_XPATH;
                break;
            case PREFERENCE:
                xpath = String.format(PREF_NODE_BY_PARTIAL_INDEX, root_id);
                break;
            case CONDITION:
                xpath = String.format(CONDITION_NODE_BY_PARTIAL_INDEX, root_id);
                break;
            case OUTCOME:
                xpath = String.format(OUTCOME_NODE_BY_PARTIAL_INDEX, root_id);
                break;

            default:
                Assert.fail("invalid node type" + rootNode.type.toString());
                return;
        }

        List<WebElement> nodes = verifyElementsVisible(By.xpath(xpath));

        Assert.assertEquals("Wrong number of nodes found by xpath " + xpath,
                expectedSiblings, nodes.size());

        WebElement node = null;
        for (WebElement ele : nodes) {
            if (rootNode.text.equals(ele.getText())) {
                node = ele;
                break;
            }
        }
        if (node == null) {
            Assert.fail("Node with text " + rootNode.text + " not found by xpath " + xpath);
        }

        // find this node's ID
        int[] newParentIDs;

        if (rootNode.type != TreeNodeType.ROOT) {
            String idString = node.getAttribute("id");
            idString = idString.split(":")[2];

            String[] ids = idString.split(NODE_ID_SEPARATOR);
            idString = ids[ids.length - 1];

            newParentIDs = new int[parentIDs.length + 1];
            for (int i = 0; i < parentIDs.length; i++) {
                newParentIDs[i] = parentIDs[i];
            }
            newParentIDs[newParentIDs.length - 1] = Integer.valueOf(idString);

        } else {
            newParentIDs = new int[0];
        }

        if (rootNode.subNodes.size() == 0) {
            // verify no children
            verifyNodeHasNoChildren(newParentIDs);
        } else {
            for (TreeNode subNode : rootNode.subNodes) {
                verifyPreferenceTreeState(subNode, newParentIDs, rootNode.subNodes.size());
            }
        }

    }

    private void verifyNodeHasNoChildren(int[] nodeIDpath) {

        String root_id = "";
        for (int aNodeIDpath : nodeIDpath) {
            root_id += aNodeIDpath + NODE_ID_SEPARATOR;
        }

        String xpath;

        xpath = String.format(PREF_NODE_BY_PARTIAL_INDEX, root_id);
        Assert.assertEquals("Expected no child nodes for xpath " + xpath, 0, waitUntilElementsFound(By.xpath(xpath)).size());

        xpath = String.format(CONDITION_NODE_BY_PARTIAL_INDEX, root_id);
        Assert.assertEquals("Expected no child nodes for xpath " + xpath, 0, waitUntilElementsFound(By.xpath(xpath)).size());

        xpath = String.format(OUTCOME_NODE_BY_PARTIAL_INDEX, root_id);
        Assert.assertEquals("Expected no child nodes for xpath " + xpath, 0, waitUntilElementsFound(By.xpath(xpath)).size());

    }

    public ProfileSettingsTreeContextMenu openContextMenuOnRootNode() {
        return openContextMenuOnNode(new int[]{}, ROOT_NODE_XPATH, "Preferences");
    }

    public ProfileSettingsTreeContextMenu openContextMenuOnPreferenceNode(int[] indicies, String expectedText) {
        return openContextMenuOnNode(indicies, PREF_NODE_BY_INDEX, expectedText);
    }

    public ProfileSettingsTreeContextMenu openContextMenuOnConditionNode(int[] indicies, String expectedText) {
        return openContextMenuOnNode(indicies, CONDITION_NODE_BY_INDEX, expectedText);
    }

    public ProfileSettingsTreeContextMenu openContextMenuOnOutcomeNode(int[] indicies, String expectedText) {
        return openContextMenuOnNode(indicies, OUTCOME_NODE_BY_INDEX, expectedText);
    }

    private ProfileSettingsTreeContextMenu openContextMenuOnNode(int[] indicies, String xpath, String expectedText) {
        // if this is the 3rd level of the tree, the ID string will look something like 1_0_2
        // so we need to build the "1_0_" to append before the individual nodes on this level
        String root_id = "";
        for (int i = 0; i < indicies.length; i++) {
            root_id += indicies[i];

            if (i < indicies.length - 1)
                root_id += NODE_ID_SEPARATOR;
        }

        WebElement node = waitUntilVisible(By.xpath(String.format(xpath, root_id)));

        Assert.assertEquals(expectedText, node.getText());

        super.openContextMenuOnElement(node);

        return new ProfileSettingsTreeContextMenu(getDriver());
    }

    public void removeAllPreferences() {

        try {
            List<WebElement> preferenceNodes = waitUntilElementsFound(By.xpath(PREF_NODE_XPATH));
            int lastCount = preferenceNodes.size();

            while (preferenceNodes.size() > 0) {
                String nodeText = preferenceNodes.get(0).getText();
                openContextMenuOnPreferenceNode(new int[]{0}, nodeText)
                        .clickDelete()
                        .clickOk();

                preferenceNodes = waitUntilElementsFound(By.xpath(PREF_NODE_XPATH));

                if (preferenceNodes.size() >= lastCount) {
                    Assert.fail("Deleting node " + nodeText + " didn't work");
                }

                lastCount = preferenceNodes.size();
            }
        } catch (NoSuchElementException ex) {
            // do nothing - none are left
        }

    }

    public void clickSaveTreeButton() {
        clickButton(By.xpath(SAVE_TREE_BUTTON_XPATH));
    }

}
