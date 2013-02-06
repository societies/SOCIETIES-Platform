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

package org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.privacy.PPN;



import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.security.auth.Subject;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RuleTarget;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.GUI;

/**
 * @author  Administrator
 * @created March 4, 2010
 */
public class PPNPOutcomeGUI extends JDialog implements ActionListener 
{
	static PPNPOutcomeGUI thePPNPOutcomeGUI;

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
	JButton btOK;

	private JTable	conditionsTable;
	
	private ConditionsTableModel	conditionsModel;
	
	private String contextType;
	
	private IPrivacyPreference pref = null;
	private boolean userCancelled = false;
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	private ICtxBroker broker;
	private IPrivacyPreferenceManager privPrefMgr;

	private  GUI masterGUI;

	private final PPNPreferenceDetails details;
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
		thePPNPOutcomeGUI = new PPNPOutcomeGUI("SYMBOLIC_LOCATION");
	} 

	public PPNPOutcomeGUI(JFrame frame, GUI masterGUI, PPNPreferenceDetails details, boolean isModal){
		super(frame, "PPNP Outcome Creator Dialog", isModal);
		this.masterGUI = masterGUI;
		this.details = details;
		this.privPrefMgr=this.masterGUI.getPrivPrefMgr();
		this.broker = this.masterGUI.getCtxBroker();
		
		this.contextType = details.getDataType();
		
		this.showGUI();
	}
	
	PPNPOutcomeGUI(String contextType){
		
		super(new JFrame(), "Privacy Policy Negotiation Preference Outcome GUI", false );
		this.details = new PPNPreferenceDetails(contextType);
		this.contextType = contextType;
		this.showGUI();
	}
	
	public void showGUI(){

		
		pnBackPanel = new JPanel();
		pnBackPanel.setBorder( BorderFactory.createTitledBorder( "" ) );
		GridBagLayout gbBackPanel = new GridBagLayout();
		GridBagConstraints gbcBackPanel = new GridBagConstraints();
		pnBackPanel.setLayout( gbBackPanel );

		DetailsPanel = new JPanel();
		DetailsPanel.setBorder( BorderFactory.createTitledBorder( "Resource and Effect" ) );
		GridBagLayout gbDetails = new GridBagLayout();
		GridBagConstraints gbcDetails = new GridBagConstraints();
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
		resourceTxt.setText(contextType);
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

		String []effects = { "PERMIT", "DENY" };
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
		GridBagLayout gbActionsPanel = new GridBagLayout();
		GridBagConstraints gbcActionsPanel = new GridBagConstraints();
		pnActionsPanel.setLayout( gbActionsPanel );



		chkboxREAD = new JCheckBox( "READ"  );
		
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
		GridBagLayout gbConditionsPanel = new GridBagLayout();
		GridBagConstraints gbcConditionsPanel = new GridBagConstraints();
		pnConditionsPanel.setLayout( gbConditionsPanel );



		this.conditionsModel = new ConditionsTableModel();
		
		
		conditionsTable = new JTable(this.conditionsModel);
		
		this.conditionsTable.getColumnModel().getColumn(0).setPreferredWidth(175);

		JScrollPane jsp = new JScrollPane(conditionsTable);
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
		GridBagLayout gbButtonsPanel = new GridBagLayout();
		GridBagConstraints gbcButtonsPanel = new GridBagConstraints();
		pnButtonsPanel.setLayout( gbButtonsPanel );

		btOK = new JButton( "OK"  );
		btOK.addActionListener(this);
		gbcButtonsPanel.gridx = 1;
		gbcButtonsPanel.gridy = 0;
		gbcButtonsPanel.gridwidth = 1;
		gbcButtonsPanel.gridheight = 1;
		gbcButtonsPanel.fill = GridBagConstraints.BOTH;
		gbcButtonsPanel.weightx = 1;
		gbcButtonsPanel.weighty = 0;
		gbcButtonsPanel.anchor = GridBagConstraints.NORTH;
		gbButtonsPanel.setConstraints( btOK, gbcButtonsPanel );
		pnButtonsPanel.add( btOK );
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

		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setContentPane( pnBackPanel );
		pack();
		setVisible( true );
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource().equals(this.btOK)){
			this.pref = this.constructPrivacyPreference();
			this.userCancelled = false;
			this.dispose();
		}else{
			this.userCancelled = true;
			this.dispose();
		}
	}
	


	private CtxAttributeIdentifier getCtxIdentifier(){
		CtxAttributeIdentifier id = null;
		try {
			List<CtxIdentifier> list = broker.lookup(CtxModelType.ATTRIBUTE, this.contextType).get();
			
			if (list.size()==1){
				return (CtxAttributeIdentifier) list.get(0);
			}else if(list.size()==0){
				return null;
			}else{
				CtxAttributeIdentifier selectedCtxID = (CtxAttributeIdentifier) JOptionPane.showInputDialog(this, "Multiple attributes of type: "+this.contextType+" found. Please select one", "Configuration", JOptionPane.QUESTION_MESSAGE, null, list.toArray(), list.get(0));
				return selectedCtxID;
			}
		} catch (CtxException e) {
			logging.debug("Error while trying to locate CtxIdentifier for resource:"+this.contextType);
			e.printStackTrace();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public IPrivacyPreference constructPrivacyPreference(){
		PPNPOutcome outcome;
		
		try {
			outcome = new PPNPOutcome((PrivacyOutcomeConstants) this.effectComboBox.getSelectedItem(), this.constructRuleTarget(), this.gatherConditions());
			return new PrivacyPreference(outcome);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
		
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
		
		List<Requestor> requestors = new ArrayList<Requestor>();
		requestors.add(this.details.getRequestor());
		
		//TODO: add a scheme field
		Resource r = new Resource(DataIdentifierScheme.CONTEXT, details.getDataType());
		if (this.details.getAffectedDataId()!=null){
			r = new Resource(this.getCtxIdentifier());
		}
		return new RuleTarget(requestors, r, this.gatherActions());
		
	}
		


	public boolean isUserCancelled() {
		return userCancelled;
	}
	
	
} 
