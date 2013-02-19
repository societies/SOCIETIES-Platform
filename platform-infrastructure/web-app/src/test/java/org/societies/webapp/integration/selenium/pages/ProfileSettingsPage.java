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
    private static final String CONDITION_NODE_XPATH = "//*[starts-with(@id,'mainForm:preferenceTree:') and contains(@id,':lblNode_condition')]";
    private static final String OUTCOME_NODE_XPATH = "//*[starts-with(@id,'mainForm:preferenceTree:') and contains(@id,':lblNode_outcome')]";

    private static final String PREF_NODE_BY_INDEX = "//*[@id='mainForm:preferenceTree:%s:lblNode_preference']";
    private static final String CONDITION_NODE_BY_INDEX = "//*[@id='mainForm:preferenceTree:%s:lblNode_condition']";
    private static final String OUTCOME_NODE_BY_INDEX = "//*[@id='mainForm:preferenceTree:%s:lblNode_outcome']";
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
        // always wait until the root node is visible - it ensures that the page is loaded
        waitUntilVisible(By.xpath(ROOT_NODE_XPATH));

        List<WebElement> nodes;
        String xpath;

        // now that we know the root node is visible, we can just do a simple verify
        // this will stop time being wasted if the test is going to fail - it all adds up if this method is repeated often
        switch (rootNode.type) {
            case ROOT:
                xpath = ROOT_NODE_XPATH;
                break;
            case PREFERENCE:
                xpath = PREF_NODE_XPATH;
                break;
            case CONDITION:
                xpath = CONDITION_NODE_XPATH;
                break;
            case OUTCOME:
                xpath = OUTCOME_NODE_XPATH;
                break;

            default:
                Assert.fail("invalid node type" + rootNode.type.toString());
                return;
        }

        nodes = verifyElementsVisible(By.xpath(xpath));

        boolean found = false;
        for (WebElement node : nodes) {
            if (rootNode.text.equals(node.getText())) {
                found = true;
                break;
            }
        }
        if (!found) {
            Assert.fail("Node with text " + rootNode.text + " not found by xpath " + xpath);
        }

        for (TreeNode subNode : rootNode.subNodes) {
            verifyPreferenceTreeState(subNode);
        }
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
}
