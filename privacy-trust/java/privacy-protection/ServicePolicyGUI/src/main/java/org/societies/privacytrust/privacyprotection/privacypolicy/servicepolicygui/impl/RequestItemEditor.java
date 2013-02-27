/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.privacytrust.privacyprotection.privacypolicy.servicepolicygui.impl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifierScheme;

public class RequestItemEditor extends JFrame implements ItemListener
{
	private JPanel pnPanel0;
	private JPanel resourcePanel;
	private JLabel resourceLabel;
	private JComboBox resourceTypeList;
	private JPanel actionsPanel;
	private ActionsTableModel actionsModel;
	private JTable actionsTable;
	private JButton addActionBtn;
	private JButton removeActionBtn;
	private JPanel conditionsPanel;
	private JTable conditionsTable;
	private JButton addConditionBtn;
	private JButton removeConditionBtn;
	private JButton btnSave;
	private JButton btnDiscard;
	private ConditionsTableModel conditionsModel;
	private JLabel schemeLabel;
	private JComboBox schemeList;
	private List<String> contextTypes = new ArrayList<String>();
	private List<String> cisTypes = new ArrayList<String>();
	private List<String> activityTypes = new ArrayList<String>();
	private List<String> cssTypes = new ArrayList<String>();
	private List<String> deviceTypes = new ArrayList<String>();
	
	public static void main(String[] args)
	{
		try
		{
			      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException localClassNotFoundException)
		{
		}
		catch (InstantiationException localInstantiationException)
		{
		}
		catch (IllegalAccessException localIllegalAccessException)
		{
		}
		catch (UnsupportedLookAndFeelException localUnsupportedLookAndFeelException)
		{
		}
		   RequestItemEditor theRequestItemEditor = new RequestItemEditor(null);
		 Object[] list = theRequestItemEditor.getCtxAttributeTypesList();
		 for (Object obj : list){
			 System.out.println(obj.toString());
		 }
	}

	public RequestItemEditor(ActionListener listener)
	{
		     super("Requested Items Editor");
		     this.setupDataTypes();
		     this.pnPanel0 = new JPanel();
		     this.pnPanel0.setBorder(BorderFactory.createTitledBorder("Resource Editor"));
		     GridBagLayout gbPanel0 = new GridBagLayout();
		     GridBagConstraints gbcPanel0 = new GridBagConstraints();
		     this.pnPanel0.setLayout(gbPanel0);

		     this.resourcePanel = new JPanel();
		     GridBagLayout gbPanel2 = new GridBagLayout();
		     GridBagConstraints gbcPanel2 = new GridBagConstraints();
		     this.resourcePanel.setLayout(gbPanel2);

		     this.schemeLabel = new JLabel("Scheme");
		     gbcPanel2.gridx = 0;
		     gbcPanel2.gridy = 0;
		     gbcPanel2.gridwidth = 1;
		     gbcPanel2.gridheight = 1;
		     gbcPanel2.fill = 1;
		     gbcPanel2.weightx = 1.0D;
		     gbcPanel2.weighty = 1.0D;
		     gbcPanel2.anchor = 11;
		     gbPanel2.setConstraints(this.schemeLabel, gbcPanel2);
		     this.resourcePanel.add(this.schemeLabel);

		     this.schemeList = new JComboBox(this.getSchemeList());
		     this.schemeList.setSelectedIndex(0);
		     this.schemeList.addItemListener(this);
		     gbcPanel2.gridx = 1;
		     gbcPanel2.gridy = 0;
		     gbcPanel2.gridwidth = 1;
		     gbcPanel2.gridheight = 1;
		     gbcPanel2.fill = 1;
		     gbcPanel2.weightx = 1.0D;
		     gbcPanel2.weighty = 0.0D;
		     gbcPanel2.anchor = 11;
		     gbPanel2.setConstraints(this.schemeList, gbcPanel2);
		     this.resourcePanel.add(this.schemeList);
		     
		     this.resourceLabel = new JLabel("ResourceType");
		     gbcPanel2.gridx = 0;
		     gbcPanel2.gridy = 1;
		     gbcPanel2.gridwidth = 1;
		     gbcPanel2.gridheight = 1;
		     gbcPanel2.fill = 1;
		     gbcPanel2.weightx = 1.0D;
		     gbcPanel2.weighty = 1.0D;
		     gbcPanel2.anchor = 11;
		     gbPanel2.setConstraints(this.resourceLabel, gbcPanel2);
		     this.resourcePanel.add(this.resourceLabel);

		     this.resourceTypeList = new JComboBox(getCtxAttributeTypesList());
		     this.resourceTypeList.setEditable(true);
		     gbcPanel2.gridx = 1;
		     gbcPanel2.gridy = 1;
		     gbcPanel2.gridwidth = 1;
		     gbcPanel2.gridheight = 1;
		     gbcPanel2.fill = 1;
		     gbcPanel2.weightx = 1.0D;
		     gbcPanel2.weighty = 0.0D;
		     gbcPanel2.anchor = 11;
		     gbPanel2.setConstraints(this.resourceTypeList, gbcPanel2);
		     this.resourcePanel.add(this.resourceTypeList);
		     
		     gbcPanel0.gridx = 0;
		     gbcPanel0.gridy = 0;
		     gbcPanel0.gridwidth = 2;
		     gbcPanel0.gridheight = 1;
		     gbcPanel0.fill = 2;
		     gbcPanel0.weightx = 1.0D;
		     gbcPanel0.weighty = 1.0D;
		     gbcPanel0.anchor = 11;
		     gbcPanel0.insets = new Insets(3, 3, 3, 3);
		     gbPanel0.setConstraints(this.resourcePanel, gbcPanel0);
		     this.pnPanel0.add(this.resourcePanel);

		     
		     
		     this.actionsPanel = new JPanel();
		     this.actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
		     GridBagLayout gbActionsPanel = new GridBagLayout();
		     GridBagConstraints gbcActionsPanel = new GridBagConstraints();
		     this.actionsPanel.setLayout(gbActionsPanel);

		     List actions = new ArrayList();

		     this.actionsModel = new ActionsTableModel();
		     this.actionsTable = new JTable(this.actionsModel);
		     JScrollPane scpList0 = new JScrollPane(this.actionsTable);
		     gbcActionsPanel.gridx = 0;
		     gbcActionsPanel.gridy = 0;
		     gbcActionsPanel.gridwidth = 2;
		     gbcActionsPanel.gridheight = 1;
		     gbcActionsPanel.fill = 1;
		     gbcActionsPanel.weightx = 1.0D;
		     gbcActionsPanel.weighty = 1.0D;
		     gbcActionsPanel.anchor = 11;
		     gbActionsPanel.setConstraints(scpList0, gbcActionsPanel);
		     this.actionsPanel.add(scpList0);

		     this.addActionBtn = new JButton("Add Action");
		     this.addActionBtn.setActionCommand("addAction");
		     this.addActionBtn.addActionListener(listener);
		     gbcActionsPanel.gridx = 0;
		     gbcActionsPanel.gridy = 1;
		     gbcActionsPanel.gridwidth = 1;
		     gbcActionsPanel.gridheight = 1;
		     gbcActionsPanel.fill = 1;
		     gbcActionsPanel.weightx = 1.0D;
		     gbcActionsPanel.weighty = 0.0D;
		     gbcActionsPanel.anchor = 11;
		     gbActionsPanel.setConstraints(this.addActionBtn, gbcActionsPanel);
		     this.actionsPanel.add(this.addActionBtn);

		     this.removeActionBtn = new JButton("Remove Action");
		     this.removeActionBtn.addActionListener(listener);
		     this.removeActionBtn.setActionCommand("removeAction");
		     gbcActionsPanel.gridx = 1;
		     gbcActionsPanel.gridy = 1;
		     gbcActionsPanel.gridwidth = 1;
		     gbcActionsPanel.gridheight = 1;
		     gbcActionsPanel.fill = 1;
		     gbcActionsPanel.weightx = 1.0D;
		     gbcActionsPanel.weighty = 0.0D;
		     gbcActionsPanel.anchor = 11;
		     gbActionsPanel.setConstraints(this.removeActionBtn, gbcActionsPanel);
		     this.actionsPanel.add(this.removeActionBtn);
		     gbcPanel0.gridx = 0;
		     gbcPanel0.gridy = 1;
		     gbcPanel0.gridwidth = 1;
		     gbcPanel0.gridheight = 1;
		     gbcPanel0.fill = 1;
		     gbcPanel0.weightx = 1.0D;
		     gbcPanel0.weighty = 0.0D;
		     gbcPanel0.anchor = 11;
		     gbPanel0.setConstraints(this.actionsPanel, gbcPanel0);
		     this.pnPanel0.add(this.actionsPanel);

		     this.conditionsPanel = new JPanel();
		     this.conditionsPanel.setBorder(BorderFactory.createTitledBorder("Conditions"));
		     GridBagLayout gbConditionsPanel = new GridBagLayout();
		     GridBagConstraints gbcConditionsPanel = new GridBagConstraints();
		     this.conditionsPanel.setLayout(gbConditionsPanel);

		     this.conditionsModel = new ConditionsTableModel();

		     this.conditionsTable = new JTable(this.conditionsModel);
		     JScrollPane jsp = new JScrollPane(this.conditionsTable);
		     gbcConditionsPanel.gridx = 0;
		     gbcConditionsPanel.gridy = 0;
		     gbcConditionsPanel.gridwidth = 2;
		     gbcConditionsPanel.gridheight = 1;
		     gbcConditionsPanel.fill = 1;
		     gbcConditionsPanel.weightx = 1.0D;
		     gbcConditionsPanel.weighty = 1.0D;
		     gbcConditionsPanel.anchor = 11;
		     gbConditionsPanel.setConstraints(jsp, gbcConditionsPanel);

		     this.conditionsPanel.add(jsp);

		     this.addConditionBtn = new JButton("Add Condition");
		     this.addConditionBtn.addActionListener(listener);
		     this.addConditionBtn.setActionCommand("addCondition");
		     gbcConditionsPanel.gridx = 0;
		     gbcConditionsPanel.gridy = 1;
		     gbcConditionsPanel.gridwidth = 1;
		     gbcConditionsPanel.gridheight = 1;
		     gbcConditionsPanel.fill = 1;
		     gbcConditionsPanel.weightx = 1.0D;
		     gbcConditionsPanel.weighty = 0.0D;
		     gbcConditionsPanel.anchor = 11;
		     gbConditionsPanel.setConstraints(this.addConditionBtn, gbcConditionsPanel);
		     this.conditionsPanel.add(this.addConditionBtn);

		     this.removeConditionBtn = new JButton("Remove Condition");
		     this.removeConditionBtn.addActionListener(listener);
		     this.removeConditionBtn.setActionCommand("removeCondition");
		     gbcConditionsPanel.gridx = 1;
		     gbcConditionsPanel.gridy = 1;
		     gbcConditionsPanel.gridwidth = 1;
		     gbcConditionsPanel.gridheight = 1;
		     gbcConditionsPanel.fill = 1;
		     gbcConditionsPanel.weightx = 1.0D;
		     gbcConditionsPanel.weighty = 0.0D;
		     gbcConditionsPanel.anchor = 11;
		     gbConditionsPanel.setConstraints(this.removeConditionBtn, gbcConditionsPanel);
		     this.conditionsPanel.add(this.removeConditionBtn);
		     gbcPanel0.gridx = 1;
		     gbcPanel0.gridy = 1;
		     gbcPanel0.gridwidth = 1;
		     gbcPanel0.gridheight = 1;
		     gbcPanel0.fill = 1;
		     gbcPanel0.weightx = 1.0D;
		     gbcPanel0.weighty = 0.0D;
		     gbcPanel0.anchor = 11;
		     gbPanel0.setConstraints(this.conditionsPanel, gbcPanel0);
		     this.pnPanel0.add(this.conditionsPanel);

		     this.btnSave = new JButton("Save");
		     this.btnSave.addActionListener(listener);
		     this.btnSave.setActionCommand("saveResource");
		     gbcPanel0.gridx = 1;
		     gbcPanel0.gridy = 2;
		     gbcPanel0.gridwidth = 1;
		     gbcPanel0.gridheight = 1;
		     gbcPanel0.fill = 1;
		     gbcPanel0.weightx = 1.0D;
		     gbcPanel0.weighty = 0.0D;
		     gbcPanel0.anchor = 11;
		     gbPanel0.setConstraints(this.btnSave, gbcPanel0);
		     this.pnPanel0.add(this.btnSave);

		     this.btnDiscard = new JButton("Discard");
		     this.btnDiscard.setActionCommand("discard");
		     this.btnDiscard.addActionListener(listener);
		     gbcPanel0.gridx = 0;
		     gbcPanel0.gridy = 2;
		     gbcPanel0.gridwidth = 1;
		     gbcPanel0.gridheight = 1;
		     gbcPanel0.fill = 1;
		     gbcPanel0.weightx = 1.0D;
		     gbcPanel0.weighty = 0.0D;
		     gbcPanel0.anchor = 11;
		     gbPanel0.setConstraints(this.btnDiscard, gbcPanel0);
		     this.pnPanel0.add(this.btnDiscard);

		     setDefaultCloseOperation(0);

		     setContentPane(this.pnPanel0);
		     pack();
		     setVisible(true);
	}


	private void setupDataTypes() {
		this.cisTypes = new ArrayList<String>();
		this.cisTypes.add("cis-member-list");
		this.cisTypes.add("cis-list");
		
		this.deviceTypes = new ArrayList<String>();
		this.deviceTypes.add("meta-data");
		
		this.activityTypes = new ArrayList<String>();
		this.activityTypes.add("activityfeed");
		
		
	}

	private String[] getSchemeList() {
		DataIdentifierScheme[] fields = DataIdentifierScheme.values();
		
		ArrayList<String> tempNames = new ArrayList<String>();
		for (int i=0; i<fields.length; i++){
			if (!fields[i].name().equalsIgnoreCase("CSS"))
				tempNames.add(fields[i].name());
		}
		String[] names = new String[tempNames.size()];
		for (int i=0; i<tempNames.size(); i++){
			names[i] = tempNames.get(i);
		}
		
		return names;
		
	}

	private String[] getCtxAttributeTypesList() {
		Field[] fields = CtxAttributeTypes.class.getDeclaredFields();
		
		String[] names = new String[fields.length];
		
		for (int i=0; i<names.length; i++){
			names[i] = fields[i].getName();
			
			
		}
		this.contextTypes = Arrays.asList(names);
		return names;
	}
	
	public void addAction(ActionConstants action, Boolean optional) {
		     Vector row = new Vector();
		     row.add(action);
		     row.add(optional);
		     this.actionsModel.addRow(row);
		     this.actionsTable.setModel(this.actionsModel);
	}

	public void removeSelectedAction()
	{
		     int index = this.actionsTable.getSelectedRow();
		     if (index >= 0)
			       this.actionsModel.removeRow(index);
		else
			       JOptionPane.showMessageDialog(this, "Select an Action to remove");
	}

	public void addCondition(ConditionConstants condition, String value, Boolean b)
	{
		     Vector row = new Vector();
		     row.add(condition);
		     row.add(value);
		     row.add(b);
		     this.conditionsModel.addRow(row);
		     this.conditionsTable.setModel(this.conditionsModel);
	}

	public void removeSelectedCondition() {
		     int index = this.conditionsTable.getSelectedRow();
		     if (index >= 0)
			       this.conditionsModel.removeRow(index);
		else
			       JOptionPane.showMessageDialog(this, "Select a Condition to remove");
	}

	public RequestItem getRequestItem()
	{
		     String resourceType = this.resourceTypeList.getSelectedItem().toString().trim();
		     if (resourceType == null) {
			       JOptionPane.showMessageDialog(this, "Please enter a resource type");
			       return null;
		}
		     if (resourceType.equals("")) {
			       JOptionPane.showMessageDialog(this, "Please enter a resource type");
			       return null;
		}

		     ArrayList actions = new ArrayList();
		     for (int i = 0; i < this.actionsModel.getRowCount(); i++) {
			       ActionConstants ac = (ActionConstants)this.actionsModel.getValueAt(i, 0);
			       Boolean optional = (Boolean)this.actionsModel.getValueAt(i, 1);
			       Action a = new Action(ac, optional.booleanValue());
			       actions.add(a);
		}

		     Resource resource = new Resource(DataIdentifierScheme.valueOf(this.schemeList.getSelectedItem().toString()), resourceType);

		     ArrayList conditions = new ArrayList();
		     for (int i = 0; i < this.conditionsModel.getRowCount(); i++) {
			       ConditionConstants cc = (ConditionConstants)this.conditionsModel.getValueAt(i, 0);
			       String value = (String)this.conditionsModel.getValueAt(i, 1);
			       Boolean optional = (Boolean)this.conditionsModel.getValueAt(i, 2);
			       Condition condition = new Condition(cc, value, optional.booleanValue());
			       conditions.add(condition);
		}
		     RequestItem item = new RequestItem(resource, actions, conditions);
		     return item;
	}

	public void itemStateChanged(ItemEvent e) {
		System.out.println("pre - ResourceTypeList.isEditable()="+resourceTypeList.isEditable());
		if (e.getStateChange()==ItemEvent.SELECTED){
			String scheme = (String) e.getItem();
			System.out.println("selected scheme: "+scheme);
			this.resourceTypeList.removeAllItems();
			if (DataIdentifierScheme.CIS.name().equals(scheme)){
				for (String cisType : this.cisTypes){
					this.resourceTypeList.addItem(cisType);
				}
				this.resourceTypeList.setEditable(false);
			}else if (DataIdentifierScheme.ACTIVITY.name().equals(scheme)){
				for (String activityType : this.activityTypes){
					this.resourceTypeList.addItem(activityType);
				}
				this.resourceTypeList.setEditable(false);
			}else if (DataIdentifierScheme.CONTEXT.name().equals(scheme)){
				for (String contextType : this.contextTypes){
					this.resourceTypeList.addItem(contextType);
				}
				this.resourceTypeList.setEditable(true);
			}else if (DataIdentifierScheme.CSS.name().equals(scheme)){
				for (String cssType : this.cssTypes){
					this.resourceTypeList.addItem(cssType);
				}
				this.resourceTypeList.setEditable(false);
			}else if(DataIdentifierScheme.DEVICE.name().equals(scheme)){
				for (String deviceType : this.deviceTypes){
					this.resourceTypeList.addItem(deviceType);
				}
				this.resourceTypeList.setEditable(false);
				
			}
		}
		System.out.println("post - ResourceTypeList.isEditable()="+resourceTypeList.isEditable());
		
	}
	
	
}
