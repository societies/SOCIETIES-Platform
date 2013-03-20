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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

/*import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.ClientResponsePolicyGenerator;*/



/**
 * @author  Administrator
 * @created March 4, 2010
 */
public class PPNPOutcomeDialog /*extends JDialog implements ActionListener */ 
{
	/*static PPNPOutcomeDialog thePPNPOutcomeGUI;

	JPanel pnBackPanel;

	JPanel DetailsPanel;
	JLabel resourceLbl;
	JTextField resourceTxt;
	JLabel effectLbl;
	JComboBox effectComboBox;

	JPanel pnActionsPanel;
	
	JCheckBox chkboxREAD;
	JCheckBox chkboxWRITE;
	JCheckBox chkboxCREATE;
	JCheckBox chkboxDELETE;

	JPanel pnConditionsPanel;

	JPanel pnButtonsPanel;
	JButton saveBtn;

	private JTable	conditionsTable;	
	private ConditionsTableModel	conditionsModel;
	
	private String dataType;
	private Requestor requestor;
	
	private IPrivacyPreference pref = null;
	GridBagLayout gbBackPanel;
	GridBagConstraints gbcBackPanel;
	

	
	GridBagLayout gbDetails;
	GridBagConstraints gbcDetails;
	GridBagLayout gbButtonsPanel;
	GridBagConstraints gbcButtonsPanel;
	GridBagLayout gbActionsPanel ;
	GridBagConstraints gbcActionsPanel;
	GridBagLayout gbConditionsPanel;
	GridBagConstraints gbcConditionsPanel;
	
	String sourceClassName = ""; 
	boolean accepted = false;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IPrivacyPreferenceManager privPrefMgr;
	private RequestItem requestItem;

	private ICtxBroker broker;
	public static void main( String args[] ) 
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch ( ClassNotFoundException e ) 
		{
		}
		catch ( InstantiationException e ) 
		{
		}
		catch ( IllegalAccessException e ) 
		{
		}
		catch ( UnsupportedLookAndFeelException e ) 
		{
		}
		thePPNPOutcomeGUI = new PPNPOutcomeDialog("SYMBOLIC_LOCATION");
		System.out.println("Outcome:");
		System.out.println(thePPNPOutcomeGUI.getOutcome().toString());
	} 

	public PPNPOutcomeDialog(Requestor requestor, String sourceClassName, RequestItem item, IPrivacyPreferenceManager pMgr){
		//super( new JFrame(),"Privacy Policy Negotiation Preference Outcome GUI" );
		super(new JFrame(), "Privacy Policy Negotiation - Privacy Preference GUI", true);
		this.requestItem = item;
		this.dataType = item.getResource().getDataType();
		this.requestor = requestor;
		this.sourceClassName = sourceClassName;
		this.privPrefMgr = pMgr;
		this.showGUI();
	}
	
	PPNPOutcomeDialog(String contextType){
		//super( new JFrame(), "Privacy Policy Negotiation Preference Outcome GUI" );
		super(new JFrame(), "Privacy Policy Negotiation - Privacy Preference GUI", true);
		this.dataType = contextType;
		this.sourceClassName = ClientResponsePolicyGenerator.class.getName();
		this.showGUI();
		
	}
	
	public PPNPOutcome getOutcome(){
		return (PPNPOutcome) this.pref.getUserObject();
	}
	
	private void showGUI(){

		
		pnBackPanel = new JPanel();
		pnBackPanel.setBorder( BorderFactory.createTitledBorder( "" ) );
		this.gbBackPanel = new GridBagLayout();
		this.gbcBackPanel = new GridBagConstraints();
		pnBackPanel.setLayout( gbBackPanel );

		DetailsPanel = new JPanel();
		DetailsPanel.setBorder( BorderFactory.createTitledBorder( "Resource and Effect" ) );
		this.gbDetails = new GridBagLayout();
		this.gbcDetails = new GridBagConstraints();
		DetailsPanel.setLayout( gbDetails );

		resourceLbl = new JLabel( "ResourceType:"  );
		gbcDetails.gridx = 0;
		gbcDetails.gridy = 0;
		gbcDetails.gridwidth = 1;
		gbcDetails.gridheight = 1;
		gbcDetails.fill = GridBagConstraints.HORIZONTAL;
		gbcDetails.weightx = 1;
		gbcDetails.weighty = 1;
		gbcDetails.anchor = GridBagConstraints.CENTER;
		gbDetails.setConstraints( resourceLbl, gbcDetails );
		DetailsPanel.add( resourceLbl );

		resourceTxt = new JTextField( );
		resourceTxt.setText(dataType);
		resourceTxt.setEditable(false);
		gbcDetails.gridx = 1;
		gbcDetails.gridy = 0;
		gbcDetails.gridwidth = 1;
		gbcDetails.gridheight = 1;
		gbcDetails.fill = GridBagConstraints.HORIZONTAL;
		gbcDetails.weightx = 1;
		gbcDetails.weighty = 0;
		gbcDetails.anchor = GridBagConstraints.CENTER;
		gbDetails.setConstraints( resourceTxt, gbcDetails );
		DetailsPanel.add( resourceTxt );

		effectLbl = new JLabel( "Effect:"  );
		gbcDetails.gridx = 0;
		gbcDetails.gridy = 2;
		gbcDetails.gridwidth = 1;
		gbcDetails.gridheight = 1;
		gbcDetails.fill = GridBagConstraints.HORIZONTAL;
		gbcDetails.weightx = 1;
		gbcDetails.weighty = 1;
		gbcDetails.anchor = GridBagConstraints.CENTER;
		gbDetails.setConstraints( effectLbl, gbcDetails );
		DetailsPanel.add( effectLbl );


		effectComboBox = new JComboBox( PrivacyOutcomeConstants.values());
		gbcDetails.gridx = 1;
		gbcDetails.gridy = 2;
		gbcDetails.gridwidth = 1;
		gbcDetails.gridheight = 1;
		gbcDetails.fill = GridBagConstraints.HORIZONTAL;
		gbcDetails.weightx = 1;
		gbcDetails.weighty = 0;
		gbcDetails.anchor = GridBagConstraints.CENTER;
		gbDetails.setConstraints( effectComboBox, gbcDetails );
		DetailsPanel.add( effectComboBox );
		gbcBackPanel.gridx = 1;
		gbcBackPanel.gridy = 0;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbcBackPanel.insets = new Insets( 5,5,5,5 );
		gbBackPanel.setConstraints( DetailsPanel, gbcBackPanel );
		pnBackPanel.add( DetailsPanel );

		pnActionsPanel = new JPanel();
		pnActionsPanel.setBorder( BorderFactory.createTitledBorder( "Actions" ) );
		this.gbActionsPanel = new GridBagLayout();
		this.gbcActionsPanel = new GridBagConstraints();
		pnActionsPanel.setLayout( gbActionsPanel );



		chkboxREAD = new JCheckBox( "READ (mandatory)"  );
		this.chkboxREAD.setSelected(true);
		this.chkboxREAD.setEnabled(false);
		gbcActionsPanel.gridx = 1;
		gbcActionsPanel.gridy = 0;
		gbcActionsPanel.gridwidth = 1;
		gbcActionsPanel.gridheight = 1;
		gbcActionsPanel.fill = GridBagConstraints.BOTH;
		gbcActionsPanel.weightx = 1;
		gbcActionsPanel.weighty = 0;
		gbcActionsPanel.anchor = GridBagConstraints.NORTH;
		gbActionsPanel.setConstraints( chkboxREAD, gbcActionsPanel );
		pnActionsPanel.add( chkboxREAD );

		chkboxWRITE = new JCheckBox( "WRITE"  );
		gbcActionsPanel.gridx = 1;
		gbcActionsPanel.gridy = 1;
		gbcActionsPanel.gridwidth = 1;
		gbcActionsPanel.gridheight = 1;
		gbcActionsPanel.fill = GridBagConstraints.BOTH;
		gbcActionsPanel.weightx = 1;
		gbcActionsPanel.weighty = 0;
		gbcActionsPanel.anchor = GridBagConstraints.NORTH;
		gbActionsPanel.setConstraints( chkboxWRITE, gbcActionsPanel );
		pnActionsPanel.add( chkboxWRITE );

		chkboxCREATE = new JCheckBox( "CREATE"  );
		gbcActionsPanel.gridx = 1;
		gbcActionsPanel.gridy = 2;
		gbcActionsPanel.gridwidth = 1;
		gbcActionsPanel.gridheight = 1;
		gbcActionsPanel.fill = GridBagConstraints.BOTH;
		gbcActionsPanel.weightx = 1;
		gbcActionsPanel.weighty = 0;
		gbcActionsPanel.anchor = GridBagConstraints.NORTH;
		gbActionsPanel.setConstraints( chkboxCREATE, gbcActionsPanel );
		pnActionsPanel.add( chkboxCREATE );

		chkboxDELETE = new JCheckBox( "DELETE"  );
		gbcActionsPanel.gridx = 1;
		gbcActionsPanel.gridy = 3;
		gbcActionsPanel.gridwidth = 1;
		gbcActionsPanel.gridheight = 1;
		gbcActionsPanel.fill = GridBagConstraints.BOTH;
		gbcActionsPanel.weightx = 1;
		gbcActionsPanel.weighty = 0;
		gbcActionsPanel.anchor = GridBagConstraints.NORTH;
		gbActionsPanel.setConstraints( chkboxDELETE, gbcActionsPanel );
		pnActionsPanel.add( chkboxDELETE );
		gbcBackPanel.gridx = 1;
		gbcBackPanel.gridy = 1;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbcBackPanel.insets = new Insets( 5,5,5,5 );
		gbBackPanel.setConstraints( pnActionsPanel, gbcBackPanel );
		pnBackPanel.add( pnActionsPanel );
		
		
		
		pnConditionsPanel = new JPanel();
		pnConditionsPanel.setBorder( BorderFactory.createTitledBorder( "Conditions" ) );
		this.gbConditionsPanel = new GridBagLayout();
		this.gbcConditionsPanel = new GridBagConstraints();
		pnConditionsPanel.setLayout( gbConditionsPanel );



		this.conditionsModel = new ConditionsTableModel();
		
		
		conditionsTable = new JTable(this.conditionsModel);
		this.conditionsTable.setPreferredScrollableViewportSize(new Dimension (400,150));		
		this.conditionsTable.getColumnModel().getColumn(0).setPreferredWidth(175);
		
		this.conditionsTable.setFillsViewportHeight(true);

		JScrollPane jsp = new JScrollPane(conditionsTable);
		
		Dimension d = jsp.getSize();
		double width = d.getWidth();
		double height = d.getHeight() - 250.0;
		Dimension newdim = new Dimension();
		newdim.setSize(width, height);
		conditionsTable.setSize(newdim);
		gbcConditionsPanel.gridx = 2;
		gbcConditionsPanel.gridy = 0;
		gbcConditionsPanel.gridwidth = 1;
		gbcConditionsPanel.gridheight = 1;
		gbcConditionsPanel.fill = GridBagConstraints.BOTH;
		gbcConditionsPanel.weightx = 1;
		gbcConditionsPanel.weighty = 1;
		gbcConditionsPanel.anchor = GridBagConstraints.NORTH;
		gbcConditionsPanel.insets = new Insets( 1,1,1,1 );
		gbConditionsPanel.setConstraints( jsp, gbcConditionsPanel );
		pnConditionsPanel.add( jsp );
		gbcBackPanel.gridx = 1;
		gbcBackPanel.gridy = 2;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 1;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbcBackPanel.insets = new Insets( 5,5,5,5 );
		gbBackPanel.setConstraints( pnConditionsPanel, gbcBackPanel );
		pnBackPanel.add( pnConditionsPanel );

		pnButtonsPanel = new JPanel();
		this.gbButtonsPanel = new GridBagLayout();
		this.gbcButtonsPanel = new GridBagConstraints();
		pnButtonsPanel.setLayout( gbButtonsPanel );

		if (this.sourceClassName.equals(ClientResponsePolicyGenerator.class.getName())){
			this.AddButtonsAcceptDeny();
		}else{
			this.AddButtonSave();
		}

		this.setRequestedActionsAndConditions();
		setDefaultCloseOperation( javax.swing.JFrame.DO_NOTHING_ON_CLOSE );

		setContentPane( pnBackPanel );
		pack();
		setVisible( true );

	}
	
	private void AddButtonSave(){
		saveBtn = new JButton( "Save"  );
		saveBtn.addActionListener(this);
		gbcButtonsPanel.gridx = 1;
		gbcButtonsPanel.gridy = 0;
		gbcButtonsPanel.gridwidth = 1;
		gbcButtonsPanel.gridheight = 1;
		gbcButtonsPanel.fill = GridBagConstraints.BOTH;
		gbcButtonsPanel.weightx = 1;
		gbcButtonsPanel.weighty = 0;
		gbcButtonsPanel.anchor = GridBagConstraints.NORTH;
		gbButtonsPanel.setConstraints( saveBtn, gbcButtonsPanel );
		pnButtonsPanel.add( saveBtn );
		gbcBackPanel.gridx = 1;
		gbcBackPanel.gridy = 4;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbBackPanel.setConstraints( pnButtonsPanel, gbcBackPanel );
		pnBackPanel.add( pnButtonsPanel );	
	}

	private void AddButtonsAcceptDeny(){
		JButton acceptBtn = new JButton("Accept");
		acceptBtn.addActionListener(this);
		acceptBtn.setActionCommand("Accept");
		gbcButtonsPanel.gridx = 1;
		gbcButtonsPanel.gridy = 0;
		gbcButtonsPanel.gridwidth = 1;
		gbcButtonsPanel.gridheight = 1;
		gbcButtonsPanel.fill = GridBagConstraints.BOTH;
		gbcButtonsPanel.weightx = 1;
		gbcButtonsPanel.weighty = 0;
		gbcButtonsPanel.anchor = GridBagConstraints.NORTH;
		gbButtonsPanel.setConstraints( acceptBtn, gbcButtonsPanel );
		pnButtonsPanel.add( acceptBtn );
		
		JButton denyBtn = new JButton("Deny");
		denyBtn.addActionListener(this);
		denyBtn.setActionCommand("Deny");
		gbcButtonsPanel.gridx = 2;
		gbcButtonsPanel.gridy = 0;
		gbcButtonsPanel.gridwidth = 1;
		gbcButtonsPanel.gridheight = 1;
		gbcButtonsPanel.fill = GridBagConstraints.BOTH;
		gbcButtonsPanel.weightx = 1;
		gbcButtonsPanel.weighty = 0;
		gbcButtonsPanel.anchor = GridBagConstraints.NORTH;
		gbButtonsPanel.setConstraints(denyBtn, gbcButtonsPanel);
		pnButtonsPanel.add(denyBtn);
		
		gbcBackPanel.gridx = 1;
		gbcBackPanel.gridy = 4;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbBackPanel.setConstraints( pnButtonsPanel, gbcBackPanel );
		pnBackPanel.add( pnButtonsPanel );	
		
	}
	 (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 
	@Override
	public void actionPerformed(ActionEvent event) {
		//TODO: send actions to UIM 
		if (event.getActionCommand().equalsIgnoreCase("Accept")){
			this.accepted = true;
			this.pref = this.constructPrivacyPreference();
			int answer = JOptionPane.showConfirmDialog(this, "Do you want to save this as a preference for this type of resource and provider DPI?", 
					"Save permanently", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer==JOptionPane.YES_OPTION){
				log("Will try to save this preference in the PolicyManager");
				try{
					PPNPreferenceDetails details = new PPNPreferenceDetails(dataType);
					details.setRequestor(requestor);
					this.privPrefMgr.storePPNPreference(details, pref);
					
					
				}catch (Exception e){
					log("Exception caught while trying to save privacy preference through the policy manager");
					e.printStackTrace();
				}
			}
		}else if (event.getActionCommand().equalsIgnoreCase("Deny")){
			this.accepted = false;
		}else{
			this.pref = this.constructPrivacyPreference();
			//log(((PPNPOutcome) pref.getUserObject()).toString());
			int answer = JOptionPane.showConfirmDialog(this, "Do you want to save this as a preference for this type of resource and provider DPI?", 
					"Save permanently", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer==JOptionPane.YES_OPTION){
				log("Will try to save this preference in the PolicyManager");
				try{
					PPNPreferenceDetails details = new PPNPreferenceDetails(dataType);
					details.setRequestor(requestor);
					this.privPrefMgr.storePPNPreference(details,pref);

					
				}catch (Exception e){
					log("Exception caught while trying to save privacy preference through the policy manager");
					e.printStackTrace();
				}
			}
		}
		this.close();
	}
	
	
	private CtxIdentifier getCtxIdentifier(){
		CtxIdentifier id = null;		
		try {
			List<CtxIdentifier> list = broker.lookup(CtxModelType.ATTRIBUTE, this.dataType).get();
			if (list.size()>0){
				return CtxIdentifierFactory.getInstance().fromString(list.get(0).toString());
				//return broker.parseIdentifier(list.get(0).toUriString());
			}else{
				return null;
			}
		} catch (CtxException e) {
			e.printStackTrace();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log("Error while trying to locate CtxIdentifier for resource:"+this.dataType);
		return null;
	}
	
	private IPrivacyPreference constructPrivacyPreference(){
		PPNPOutcome outcome;
		
		try {
			outcome = new PPNPOutcome((PrivacyOutcomeConstants) this.effectComboBox.getSelectedItem(), this.constructRuleTarget(), this.gatherConditions());
			return new PrivacyPreference(outcome);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void setRequestor(Requestor subject){
		this.requestor = subject;
	}
	
	
	private List<Condition> gatherConditions(){
		ArrayList<Condition> conditions = new ArrayList<Condition>();
		if (this.effectComboBox.getSelectedItem().equals(PrivacyOutcomeConstants.BLOCK)){
			return conditions;
		}
		for (int i=0; i<this.conditionsModel.getRowCount(); i++){
			Boolean selected = (Boolean) this.conditionsModel.getValueAt(i,2);
			if (selected.booleanValue()){
				ConditionConstants cc = (ConditionConstants) this.conditionsModel.getValueAt(i, 0);
				String value = (String) this.conditionsModel.getValueAt(i,1);
			
				Condition condition = new Condition(cc,value);
				conditions.add(condition);
			}
		}
		return conditions;
	}
	
	private List<Action> gatherActions(){
		List<Action> actions = new ArrayList<Action>();
		if (this.effectComboBox.getSelectedItem().equals(PrivacyOutcomeConstants.BLOCK)){
			return actions;
		}	
		
		actions.add( new Action(ActionConstants.READ));
		
		if (this.chkboxWRITE.isSelected()){
			actions.add(new Action(ActionConstants.WRITE));
		}
		
		if (this.chkboxCREATE.isSelected()){
			actions.add(new Action(ActionConstants.CREATE));
		}
		
		if (this.chkboxDELETE.isSelected()){
			actions.add(new Action(ActionConstants.DELETE));
		}
		
		return actions;
		
	}
	private RuleTarget constructRuleTarget(){
		
		List<Requestor> subjects = new ArrayList<Requestor>();
		if (this.requestor!=null){
			subjects.add(this.requestor);
		}
		Resource r = new Resource(this.requestItem.getResource().getScheme(),this.dataType);
		
		return new RuleTarget(subjects, r, this.gatherActions());
		
	}
		
	public void close(){
		this.dispose();
	}
	
	private void setRequestedActionsAndConditions(){
		List<Action> actions = this.requestItem.getActions();
		if (actions.contains(ActionConstants.READ)){
			this.chkboxREAD.setSelected(true);
			this.chkboxREAD.setEnabled(false);
		}
		
		for (int i=0; i<10; i++){
			System.out.println("\n");
		}
		

		for (Action a : actions){
			if (a.getActionType().equals(ActionConstants.WRITE)){
				this.chkboxWRITE.setSelected(true);
				this.chkboxWRITE.setEnabled(false);
				System.out.println("Located RequestedAction: "+a.getActionType());
			}else if (a.getActionType().equals(ActionConstants.DELETE)){
				this.chkboxDELETE.setSelected(true);
				this.chkboxDELETE.setEnabled(false);
				System.out.println("Located RequestedAction: "+a.getActionType());
			}else if (a.getActionType().equals(ActionConstants.CREATE)){
				this.chkboxCREATE.setSelected(true);
				this.chkboxCREATE.setEnabled(false);
				System.out.println("Located RequestedAction: "+a.getActionType());
			}else{
				System.out.println("DID NOT LOCATE RequestedAction: "+a.getActionType());

			}
		}
		
		for (int i=0; i<10; i++){
			System.out.println("\n");
		}
		if (actions.contains(ActionConstants.WRITE)){
			this.chkboxWRITE.setSelected(true);
			this.chkboxWRITE.setEnabled(false);
		}
		
		if (actions.contains(ActionConstants.CREATE)){
			this.chkboxCREATE.setSelected(true);
			this.chkboxCREATE.setEnabled(false);
		}
		
		if (actions.contains(ActionConstants.DELETE)){
			this.chkboxDELETE.setSelected(true);
			this.chkboxDELETE.setEnabled(false);
		}
		
		
		List<Condition> conditions = this.requestItem.getConditions();
		
		for (Condition c : conditions){
			for (int i=0; i<this.conditionsModel.getRowCount(); i++){
				ConditionConstants conditionConstant = (ConditionConstants) this.conditionsModel.getValueAt(i,0);
				if (c.getConditionName().equals(conditionConstant)){
					this.conditionsModel.setValueAt(c.getValueAsString(), i, 1);
					this.conditionsModel.setValueAt(true, i, 2);
				}
			}
		}
		
		this.effectComboBox.setSelectedItem(PrivacyOutcomeConstants.ALLOW);
		//this.effectComboBox.setEditable(false);
		//this.effectComboBox.setEnabled(false);
	}
	
	
	public boolean wasAccepted(){
		return this.accepted;
	}
	
	
	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}


*/} 
