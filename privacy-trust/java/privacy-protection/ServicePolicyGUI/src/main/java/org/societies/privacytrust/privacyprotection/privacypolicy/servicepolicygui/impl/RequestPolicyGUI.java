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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;

public class RequestPolicyGUI extends JPanel
implements ActionListener, WindowListener
{
	RequestPolicy policy;
	ArrayList<RequestItem> requestItems;
	ResourcesTableModel model;
	JButton saveBtn;
	JPanel resourcePanel;
	JTable resourceTable;
	JPanel resourceButtonsPanel;
	JButton addResourceBtn;
	JButton removeResourceBtn;
	JPanel subjectPanel;
	JLabel serviceIDLabel;
	JLabel dpiLabel;
	JTextField serviceIDTxtField;
	JTextField dpiTxtField;
	RequestItemEditor reqEditor;
	
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
		
		RequestPolicyGUI gui = new RequestPolicyGUI();
		
		
	}

	public RequestPolicyGUI()
	{
		this.requestItems = new ArrayList<RequestItem>();
		setBorder(BorderFactory.createTitledBorder("RequestPolicy editor for 3p service developers"));
		GridBagLayout gbBackPanel = new GridBagLayout();
		GridBagConstraints gbcBackPanel = new GridBagConstraints();
		setLayout(gbBackPanel);

		this.saveBtn = new JButton("Save");
		this.saveBtn.setActionCommand("savePolicy");
		this.saveBtn.addActionListener(this);
		gbcBackPanel.gridx = 2;
		gbcBackPanel.gridy = 3;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = 1;
		gbcBackPanel.weightx = 1.0D;
		gbcBackPanel.weighty = 0.0D;
		gbcBackPanel.anchor = 11;
		gbBackPanel.setConstraints(this.saveBtn, gbcBackPanel);
		add(this.saveBtn);

		this.resourcePanel = new JPanel();
		GridBagLayout gbResourcePanel = new GridBagLayout();
		GridBagConstraints gbcResourcePanel = new GridBagConstraints();
		this.resourcePanel.setLayout(gbResourcePanel);

		this.model = new ResourcesTableModel();

		this.resourceTable = new JTable(this.model);
		JScrollPane scpResourceTable = new JScrollPane(this.resourceTable);
		
		gbcResourcePanel.gridx = 0;
		gbcResourcePanel.gridy = 0;
		gbcResourcePanel.gridwidth = 1;
		gbcResourcePanel.gridheight = 1;
		gbcResourcePanel.fill = 1;
		gbcResourcePanel.weightx = 1.0D;
		gbcResourcePanel.weighty = 1.0D;
		gbcResourcePanel.anchor = 11;
		gbResourcePanel.setConstraints(scpResourceTable, gbcResourcePanel);
		this.resourcePanel.add(scpResourceTable);

		JScrollPane scpResourcePanel = new JScrollPane(this.resourcePanel);
		gbcBackPanel.gridx = 0;
		gbcBackPanel.gridy = 1;
		gbcBackPanel.gridwidth = 3;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = 1;
		gbcBackPanel.weightx = 1.0D;
		gbcBackPanel.weighty = 0.0D;
		gbcBackPanel.anchor = 11;
		gbBackPanel.setConstraints(scpResourcePanel, gbcBackPanel);
		add(scpResourcePanel);

		this.resourceButtonsPanel = new JPanel();
		GridBagLayout gbResourceButtonsPanel = new GridBagLayout();
		GridBagConstraints gbcResourceButtonsPanel = new GridBagConstraints();
		this.resourceButtonsPanel.setLayout(gbResourceButtonsPanel);

		this.addResourceBtn = new JButton("Add New Resource");
		this.addResourceBtn.setActionCommand("addResource");
		this.addResourceBtn.addActionListener(this);
		gbcResourceButtonsPanel.gridx = 0;
		gbcResourceButtonsPanel.gridy = 0;
		gbcResourceButtonsPanel.gridwidth = 1;
		gbcResourceButtonsPanel.gridheight = 1;
		gbcResourceButtonsPanel.fill = 1;
		gbcResourceButtonsPanel.weightx = 1.0D;
		gbcResourceButtonsPanel.weighty = 0.0D;
		gbcResourceButtonsPanel.anchor = 11;
		gbResourceButtonsPanel.setConstraints(this.addResourceBtn, gbcResourceButtonsPanel);
		this.resourceButtonsPanel.add(this.addResourceBtn);

		this.removeResourceBtn = new JButton("Remove Resource");
		this.removeResourceBtn.setActionCommand("removeResource");
		this.removeResourceBtn.addActionListener(this);

		gbcResourceButtonsPanel.gridx = 1;
		gbcResourceButtonsPanel.gridy = 0;
		gbcResourceButtonsPanel.gridwidth = 1;
		gbcResourceButtonsPanel.gridheight = 1;
		gbcResourceButtonsPanel.fill = 1;
		gbcResourceButtonsPanel.weightx = 1.0D;
		gbcResourceButtonsPanel.weighty = 0.0D;
		gbcResourceButtonsPanel.anchor = 11;
		gbResourceButtonsPanel.setConstraints(this.removeResourceBtn, gbcResourceButtonsPanel);
		this.resourceButtonsPanel.add(this.removeResourceBtn);
		gbcBackPanel.gridx = 1;
		gbcBackPanel.gridy = 2;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = 1;
		gbcBackPanel.weightx = 1.0D;
		gbcBackPanel.weighty = 0.0D;
		gbcBackPanel.anchor = 11;
		gbBackPanel.setConstraints(this.resourceButtonsPanel, gbcBackPanel);
		add(this.resourceButtonsPanel);

		this.subjectPanel = new JPanel();
		this.subjectPanel.setBorder(BorderFactory.createTitledBorder(""));
		GridBagLayout gbSubjectPanel = new GridBagLayout();
		GridBagConstraints gbcSubjectPanel = new GridBagConstraints();
		this.subjectPanel.setLayout(gbSubjectPanel);

/*		this.serviceIDLabel = new JLabel("Service Identifier or CIS Identity:");
		gbcSubjectPanel.gridx = 0;
		gbcSubjectPanel.gridy = 0;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = 1;
		gbcSubjectPanel.weightx = 1.0D;
		gbcSubjectPanel.weighty = 1.0D;
		gbcSubjectPanel.anchor = 11;
		gbSubjectPanel.setConstraints(this.serviceIDLabel, gbcSubjectPanel);
		this.subjectPanel.add(this.serviceIDLabel);

		this.dpiLabel = new JLabel("Your Identity:");
		gbcSubjectPanel.gridx = 0;
		gbcSubjectPanel.gridy = 1;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = 1;
		gbcSubjectPanel.weightx = 1.0D;
		gbcSubjectPanel.weighty = 1.0D;
		gbcSubjectPanel.anchor = 11;
		gbSubjectPanel.setConstraints(this.dpiLabel, gbcSubjectPanel);
		this.subjectPanel.add(this.dpiLabel);

		this.serviceIDTxtField = new JTextField();
		gbcSubjectPanel.gridx = 1;
		gbcSubjectPanel.gridy = 0;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = 1;
		gbcSubjectPanel.weightx = 1.0D;
		gbcSubjectPanel.weighty = 1.0D;
		gbcSubjectPanel.anchor = 11;
		gbSubjectPanel.setConstraints(this.serviceIDTxtField, gbcSubjectPanel);
		this.subjectPanel.add(this.serviceIDTxtField);

		this.dpiTxtField = new JTextField();
		gbcSubjectPanel.gridx = 1;
		gbcSubjectPanel.gridy = 1;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = 1;
		gbcSubjectPanel.weightx = 1.0D;
		gbcSubjectPanel.weighty = 1.0D;
		gbcSubjectPanel.anchor = 11;
		gbSubjectPanel.setConstraints(this.dpiTxtField, gbcSubjectPanel);
		this.subjectPanel.add(this.dpiTxtField);
		gbcBackPanel.gridx = 0;
		gbcBackPanel.gridy = 0;
		gbcBackPanel.gridwidth = 3;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = 1;
		gbcBackPanel.weightx = 1.0D;
		gbcBackPanel.weighty = 0.0D;
		gbcBackPanel.anchor = 18;
		gbBackPanel.setConstraints(this.subjectPanel, gbcBackPanel);
		add(this.subjectPanel);*/

		JScrollPane scpBackPanel = new JScrollPane(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(this.saveBtn)) {
			if (this.reqEditor != null) {
				JOptionPane.showMessageDialog(this, "Please finish editing the resource in the Resources editor window");
				this.reqEditor.toFront();
			}
			else if (this.requestItems.size() == 0) {
				JOptionPane.showMessageDialog(this, "Add at least one resource ");
			} else {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setSelectedFile(new File("Privacy-policy.xml"));
				fileChooser.setDialogTitle("Save your privacy folder inside the src/main/resources folder of your 3p service");
				
				fileChooser.setFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						return "XML files - .xml";
					}
					
					@Override
					public boolean accept(File f) {
						return f.getName().endsWith(".xml");
					}
				});
				int returnVal = fileChooser.showSaveDialog(this);
				if (returnVal == 0) {
					createPolicy(fileChooser.getSelectedFile());
				}
			}

		}
		else if (e.getActionCommand().equalsIgnoreCase("addResource"))
		{
			if (this.reqEditor == null) {
				this.reqEditor = new RequestItemEditor(this);
				this.reqEditor.addWindowListener(this);
				System.out.println("Created RequestItemEditor");
			} else {
				this.reqEditor.toFront();
			}

		}
		else if (e.getActionCommand().equalsIgnoreCase("removeResource")) {
			int index = this.resourceTable.getSelectedRow();
			if (index >= 0) {
				RequestItem item = (RequestItem)this.requestItems.get(index);
				System.out.println("Removing item: \n" + item.toString());
				this.requestItems.remove(index);
				this.model.removeRow(index);
			} else {
				JOptionPane.showMessageDialog(this, "Select a resource to delete");
			}
		}
		else if (e.getActionCommand().equalsIgnoreCase("addAction")) {
			if (this.reqEditor != null) {
				String message = "Select an action from the list";
				String title = "New Action";

				ActionConstants action = (ActionConstants)JOptionPane.showInputDialog(this.reqEditor, message, title, 3, null, ActionConstants.values(), ActionConstants.READ);
				if (action != null)
				{
					this.reqEditor.addAction(action, new Boolean(false));
				}
			}

		}
		else if (e.getActionCommand().equalsIgnoreCase("removeAction")) {
			this.reqEditor.removeSelectedAction();
		}
		else if (e.getActionCommand().equalsIgnoreCase("addCondition")) {
			if (this.reqEditor != null) {
				String message = "Select a condition from the list";
				String title = "New Condition";
				ConditionConstants condition = (ConditionConstants)JOptionPane.showInputDialog(this.reqEditor, message, title, 3, null, ConditionConstants.values(), ConditionConstants.DATA_RETENTION_IN_HOURS);
				if (condition != null) {
					String[] values = PrivacyConditionsConstantValues.getValues(condition);
					System.out.println("Value: " + condition);
					message = "Enter a value for " + condition.toString();
					String value = (String)JOptionPane.showInputDialog(this.reqEditor, message, title, 3, null, values, "");
					if (value != null) {
						this.reqEditor.addCondition(condition, value, new Boolean(true));
					}
				}
			}

		}
		else if (e.getActionCommand().equalsIgnoreCase("removeCondition")) {
			this.reqEditor.removeSelectedCondition();
		}
		else if (e.getActionCommand().equalsIgnoreCase("saveResource")) {
			RequestItem requestItem = this.reqEditor.getRequestItem();
			if (requestItem == null) {
				System.out.println("Problem retrieving RequestItem");
			}
			else
			{
				this.reqEditor.dispose();
				this.reqEditor = null;
				this.requestItems.add(requestItem);
				addResource(requestItem);
			}

		}
		else if (e.getActionCommand().equalsIgnoreCase("discard"))
		{
			System.out.println("enabled: " + isEnabled());
			if (this.reqEditor == null) {
				System.out.println("reqEditor is null");
			}
			this.reqEditor.dispose();
			this.reqEditor = null;
		}
	}

	private void createPolicy(File selectedFile)
	{
		try
		{
			this.policy = new RequestPolicy(this.requestItems);
			try
			{
				FileWriter fWriter = new FileWriter(selectedFile);
				BufferedWriter bWriter = new BufferedWriter(fWriter);
				BufferedWriter out = new BufferedWriter(bWriter);
				out.write(this.policy.toXMLString());
				out.close();
				JOptionPane.showMessageDialog(this, "ServicePolicy saved as: " + selectedFile.getCanonicalPath());
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(this, "Error saving servicePolicy to file");
				ioe.printStackTrace();
			}

		}
		catch (IllegalArgumentException ie)
		{
			JOptionPane.showMessageDialog(this, "Invalid serviceID");
		}
	}

	private void addResource(RequestItem item) {
		Vector row = new Vector();
		row.add(item.getResource().getDataType());
		String actions = "";
		for (int i = 0; i < item.getActions().size(); i++) {
			Action a = (Action)item.getActions().get(i);
			if (i > 0) {
				actions = actions.concat("+");
				actions = actions.concat(a.getActionType().toString());
			}
			else {
				actions = a.getActionType().toString();
			}
		}
		row.add(actions);
		String conditions = "";
		for (int i = 0; i < item.getConditions().size(); i++) {
			Condition con = (Condition)item.getConditions().get(i);
			if (i > 0) {
				conditions = conditions.concat(",");
				conditions = conditions.concat(con.getConditionName().toString());
			} else {
				conditions = conditions.concat(con.getConditionName().toString());
			}
		}
		row.add(conditions);
		Boolean b = Boolean.valueOf("a");
		System.out.println(b);
		if (b.booleanValue())
			System.out.println("true");
		else {
			System.out.println("false");
		}
		row.add(b);
		this.model.addRow(row);

		System.out.println(this.model.getColumnClass(this.model.getColumnCount()));
		this.resourceTable.setModel(this.model);
		System.out.println(this.resourceTable.getColumnClass(this.resourceTable.getColumnCount() - 1));
	}

	public void windowClosing(WindowEvent e)
	{
		System.out.println("window closing");
		JOptionPane.showMessageDialog(this.reqEditor, "Sorry, Use the Save or Discard Buttons to exit");
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}
}

