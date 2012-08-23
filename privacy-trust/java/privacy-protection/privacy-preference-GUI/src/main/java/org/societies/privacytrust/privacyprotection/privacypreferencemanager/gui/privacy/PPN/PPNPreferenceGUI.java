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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.GUI;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.common.ConditionCreatorDialog;
/**
 * @author  EPapadopoulou@users.sourceforge.net
 * @created April 27, 2010
 */
public class PPNPreferenceGUI extends JFrame implements ActionListener,  WindowListener, TreeSelectionListener
{
	static PPNPreferenceGUI thepreferenceGUI;

	JPanel pnPanel0;

	JPanel pnPanel3;

	JPanel pnPanel4;
	JButton btAddCondition;
	JButton btAddOutcome;
	JButton btRemoveCondition;
	JButton btRemoveOutcome;
	JButton btClear;

	JPanel pnPanel8;
	JLabel lbProviderDPI;
	JTextField txtProviderDPI;
	JLabel lbServiceID;
	JTextField txtServiceID;

	JPanel pnPanel9;
	JTree trDisplayTree;

	JPanel pnPanel10;
	JButton btSavePreference;
	
	private final GUI masterGUI;
	
	private IPrivacyPreference selectedNode;
	/*JPopupMenu popupMenu;

	private JMenuItem editMenuItem;

	private JMenuItem deleteMenuItem;

	private JMenuItem addConditionMenuItem;

	private JMenuItem addOutcomeMenuItem;
*/
	private boolean isClosed = false;

	private final PPNPreferenceDetails details;

	private PPNPrivacyPreferenceTreeModel preferenceTreeModel;

	private JLabel lbContextType;

	private JTextField txtContextType;

	private JLabel lbContextID;

	private JTextField txtContextID;
	/**
	 */
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
		//thepreferenceGUI = new UserPreferenceGUI(null);
		JFrame frame = new JFrame();
		frame.add(thepreferenceGUI);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	} 

	/**
	 */
	public PPNPreferenceGUI(GUI masterGUI, PPNPreferenceDetails d) 
	{
		super();
		System.out.println("Creating PPN preferenceGUI for: \n"+d.toString());
		this.details = d;
		
		this.addWindowListener(this);
		this.masterGUI = masterGUI;
		IPrivacyPreferenceManager privPrefMgr = (IPrivacyPreferenceManager) this.masterGUI.getPrivPrefMgr();
		preferenceTreeModel = (PPNPrivacyPreferenceTreeModel) privPrefMgr.getPPNPreference(d);
		if (this.preferenceTreeModel==null){
			this.preferenceTreeModel = new PPNPrivacyPreferenceTreeModel(d.getDataType(),new PrivacyPreference());
			if (d.getAffectedDataId()!=null){
				this.preferenceTreeModel.setAffectedDataId(d.getAffectedDataId());
			}
			
			if (d.getRequestor()!=null){
				this.preferenceTreeModel.setRequestor(d.getRequestor());
			}
		}
		this.showGUI();
	}
	private void setLocation(){
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int maxX = screenSize.width ;
	    int maxY = screenSize.height ;
	    this.setLocation((maxX/3), (maxY/3));
		//this.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
	    //this.getContentPane().setSize(400, 300);
	}
/*	private void setupPopupMenus() {
		this.editMenuItem = new JMenuItem("Edit");
		this.editMenuItem.addActionListener(this);
		this.deleteMenuItem = new JMenuItem("Delete");
		this.deleteMenuItem.addActionListener(this);
		this.addConditionMenuItem = new JMenuItem("Add Condition");
		this.addConditionMenuItem.addActionListener(this);
		this.addOutcomeMenuItem = new JMenuItem("Add Outcome");
		this.addOutcomeMenuItem.addActionListener(this);
		
		this.popupMenu = new JPopupMenu();
		this.popupMenu.add(this.editMenuItem);
		this.popupMenu.add(this.deleteMenuItem);
		this.popupMenu.add(this.addConditionMenuItem);
		this.popupMenu.add(this.addOutcomeMenuItem);
		
		
		
	}*/

	private void showGUI(){
		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel3 = new JPanel();
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		pnPanel4 = new JPanel();
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );

		btAddCondition = new JButton( "Add a Condition"  );
		btAddCondition.addActionListener(this);
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 0,20,0,10 );
		gbPanel4.setConstraints( btAddCondition, gbcPanel4 );
		pnPanel4.add( btAddCondition );

		btAddOutcome = new JButton( "Add an Outcome"  );
		btAddOutcome.addActionListener(this);
		gbcPanel4.gridx = 1;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 0,10,0,20 );
		gbPanel4.setConstraints( btAddOutcome, gbcPanel4 );
		pnPanel4.add( btAddOutcome );

		btRemoveCondition = new JButton( "Remove Condition"  );
		btRemoveCondition.addActionListener(this);
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 1;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 20,20,20,10 );
		gbPanel4.setConstraints( btRemoveCondition, gbcPanel4 );
		pnPanel4.add( btRemoveCondition );

		btRemoveOutcome = new JButton( "Remove Outcome"  );
		btRemoveOutcome.addActionListener(this);
		gbcPanel4.gridx = 1;
		gbcPanel4.gridy = 1;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 20,10,20,20 );
		gbPanel4.setConstraints( btRemoveOutcome, gbcPanel4 );
		pnPanel4.add( btRemoveOutcome );

		btClear = new JButton( "Clear all"  );
		btClear.addActionListener(this);
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 2;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 0,20,0,10 );
		gbPanel4.setConstraints( btClear, gbcPanel4 );
		pnPanel4.add( btClear );
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 1;
		gbcPanel3.gridwidth = 2;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbPanel3.setConstraints( pnPanel4, gbcPanel3 );
		pnPanel3.add( pnPanel4 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 2;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel3, gbcPanel0 );
		pnPanel0.add( pnPanel3 );

		pnPanel8 = new JPanel();
		pnPanel8.setBorder( BorderFactory.createTitledBorder( "PPN Preferences GUI - mailto:EPapadopoulou@users.sourceforge.net" ) );
		GridBagLayout gbPanel8 = new GridBagLayout();
		GridBagConstraints gbcPanel8 = new GridBagConstraints();
		pnPanel8.setLayout( gbPanel8 );

		
		lbContextType = new JLabel("Context Type:");
		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( lbContextType, gbcPanel8 );
		pnPanel8.add( lbContextType );

		txtContextType = new JTextField();
		txtContextType.setText(details.getDataType());
		txtContextType.setEditable(false);
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( txtContextType, gbcPanel8 );
		pnPanel8.add( txtContextType );
	
		
		lbContextID = new JLabel("Context ID:");
		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 1;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( lbContextID, gbcPanel8 );
		pnPanel8.add( lbContextID );

		txtContextID = new JTextField();
		if (this.details.getAffectedDataId()!=null){
			txtContextID.setText(details.getAffectedDataId().getUri());
		}else{
			txtContextID.setText("Generic");
		}
		txtContextID.setEditable(false);
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 1;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( txtContextID, gbcPanel8 );
		pnPanel8.add( txtContextID );
		
		
		lbProviderDPI = new JLabel( "Provider DPI:"  );
		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 2;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( lbProviderDPI, gbcPanel8 );
		pnPanel8.add( lbProviderDPI );

		txtProviderDPI = new JTextField( );
		if (this.details.getRequestor().getRequestorId()!=null){
			txtProviderDPI.setText(this.details.getRequestor().getRequestorId().getJid());
		}else{
			txtProviderDPI.setText("Generic");
		}
		txtProviderDPI.setEditable(false);
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 2;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( txtProviderDPI, gbcPanel8 );
		pnPanel8.add( txtProviderDPI );

		lbServiceID = new JLabel( "CIS ID or Service ID:"  );

		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 3;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( lbServiceID, gbcPanel8 );
		pnPanel8.add( lbServiceID );

		txtServiceID = new JTextField( );
		Requestor r = this.details.getRequestor();
		if (r instanceof RequestorService){
			ServiceResourceIdentifier serviceID = ((RequestorService) r).getRequestorServiceId();
			if (serviceID!=null){
				this.txtServiceID.setText(serviceID.getServiceInstanceIdentifier());
			}
		}else if (r instanceof RequestorCis){
			IIdentity cisId = ((RequestorCis) r).getCisRequestorId();
			if (cisId!=null){
				this.txtServiceID.setText(cisId.getJid());
			}
		}else{
			txtServiceID.setText("Generic");
		}
		txtServiceID.setEditable(false);
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 3;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( txtServiceID, gbcPanel8 );
		pnPanel8.add( txtServiceID );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel8, gbcPanel0 );
		pnPanel0.add( pnPanel8 );

		pnPanel9 = new JPanel();
		pnPanel9.setBorder( BorderFactory.createTitledBorder( "" ) );
		GridBagLayout gbPanel9 = new GridBagLayout();
		GridBagConstraints gbcPanel9 = new GridBagConstraints();
		pnPanel9.setLayout( gbPanel9 );

		
		

		trDisplayTree = new JTree(this.preferenceTreeModel);
		trDisplayTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		trDisplayTree.addTreeSelectionListener(this);
		this.selectedNode = (IPrivacyPreference) this.trDisplayTree.getModel().getRoot();
		//trDisplayTree.setAlignmentX( 0.0 );
		//trDisplayTree.setAlignmentY( 0.0 );
		trDisplayTree.setAutoscrolls( true );
		trDisplayTree.setEditable( false );
		trDisplayTree.setForeground( new Color( 0,0,0 ) );
		trDisplayTree.setInheritsPopupMenu( true );
		trDisplayTree.setLargeModel( true );
		trDisplayTree.setRootVisible( true );
		//trDisplayTree.addMouseListener(this);
		JScrollPane scpDisplayTree = new JScrollPane( trDisplayTree );
		
		
		gbcPanel9.gridx = 0;
		gbcPanel9.gridy = 0;
		gbcPanel9.gridwidth = 1;
		gbcPanel9.gridheight = 1;
		gbcPanel9.fill = GridBagConstraints.BOTH;
		gbcPanel9.weightx = 1;
		gbcPanel9.weighty = 1;
		gbcPanel9.anchor = GridBagConstraints.NORTH;
		gbPanel9.setConstraints( scpDisplayTree, gbcPanel9 );
		pnPanel9.add( scpDisplayTree );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel9, gbcPanel0 );
		pnPanel0.add( pnPanel9 );

		pnPanel10 = new JPanel();
		GridBagLayout gbPanel10 = new GridBagLayout();
		GridBagConstraints gbcPanel10 = new GridBagConstraints();
		pnPanel10.setLayout( gbPanel10 );

		btSavePreference = new JButton( "Save Preference"  );

		btSavePreference.addActionListener(this);
		gbcPanel10.gridx = 0;
		gbcPanel10.gridy = 0;
		gbcPanel10.gridwidth = 1;
		gbcPanel10.gridheight = 1;
		gbcPanel10.fill = GridBagConstraints.BOTH;
		gbcPanel10.weightx = 1;
		gbcPanel10.weighty = 0;
		gbcPanel10.anchor = GridBagConstraints.EAST;
		gbcPanel10.insets = new Insets( 20,10,20,20 );
		gbPanel10.setConstraints( btSavePreference, gbcPanel10 );
		pnPanel10.add( btSavePreference );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel10, gbcPanel0 );
		pnPanel0.add( pnPanel10 );

		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		setContentPane( this.pnPanel0 );
		this.setLocation();
		pack();
		setVisible( true );
	}

	private void clearPreferenceTreeModel(){
		this.preferenceTreeModel = new PPNPrivacyPreferenceTreeModel(this.details.getDataType(),new PrivacyPreference());
		if (this.details.getAffectedDataId()!=null){
			this.preferenceTreeModel.setAffectedDataId(this.details.getAffectedDataId());
		}
		
		if (this.details.getRequestor()!=null){
			this.preferenceTreeModel.setRequestor(this.details.getRequestor());
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btAddCondition)){
			this.addCondition();
		}else if (e.getSource().equals(this.btAddOutcome)){
			this.addOutcome();
		}else if (e.getSource().equals(this.btClear)){

			this.clearPreferenceTreeModel();
			this.trDisplayTree.setModel(this.preferenceTreeModel);
			

		}else if (e.getSource().equals(this.btRemoveCondition)){
			int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this condition?", "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n==JOptionPane.YES_OPTION){
				//this.deleteNode();
				this.selectedNode.removeFromParent();
				
				((DefaultTreeModel) this.trDisplayTree.getModel()).nodeStructureChanged(this.selectedNode);
				
			}
		}else if (e.getSource().equals(this.btRemoveOutcome)){
			int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this outcome?", "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n==JOptionPane.YES_OPTION){
				
				//this.deleteNode();
				this.selectedNode.removeFromParent();
				((DefaultTreeModel) this.trDisplayTree.getModel()).nodeStructureChanged(this.selectedNode);
			}
			
		}else if (e.getSource().equals(this.btSavePreference)){
			if (this.checkPreference()){
				this.storePreference();
			}
		}/*else if (e.getSource().equals(addConditionMenuItem)){
			this.addCondition();
		}else if (e.getSource().equals(addOutcomeMenuItem)){
			this.addOutcome();
		}*/
		//this.trDisplayTree.repaint();
		//this.trDisplayTree.updateUI();
		this.printModel();
	} 

	private void calculateSizeOfObject(String message, Object p){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(p);
			oos.flush(); 
			oos.close(); 
			bos.close();
			System.out.println(message+" "+bos.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	private void calculateSizeOfObjectAndPopup(String message, Object p){
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(p);
			oos.flush(); 
			oos.close(); 
			bos.close();
			JOptionPane.showMessageDialog(this,message+" "+bos.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	private void storePreference() {
		this.calculateSizeOfObject("Retrieved model from JTree:",this.trDisplayTree.getModel());
		
		IPrivacyPreferenceManager privPrefMgr = this.masterGUI.getPrivPrefMgr();
		try{
			IPrivacyPreferenceTreeModel model = (IPrivacyPreferenceTreeModel) this.trDisplayTree.getModel();
			privPrefMgr.storePPNPreference(this.details, model.getRootPreference());
			JOptionPane.showMessageDialog(this, "Successfully stored preference", "Preference saved", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Unable to store preference",e.toString(),JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		
	}

	private boolean checkPreference(){
		IPrivacyPreferenceTreeModel model  =  (IPrivacyPreferenceTreeModel) this.trDisplayTree.getModel();
		Enumeration<IPrivacyPreference> nodeEnum = model.getRootPreference().breadthFirstEnumeration();
		int index = 1;
		while (nodeEnum.hasMoreElements()){
			System.out.println("Node no"+index);
			index +=1;
			IPrivacyPreference p = nodeEnum.nextElement();
			if (p.isLeaf()){
				Enumeration<IPrivacyPreference> children = p.children();
				if (children.hasMoreElements()){
					JOptionPane.showMessageDialog(this, "An outcome cannot have conditions as subtrees", "Error in preference", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			if (p.isBranch()){
				Enumeration<IPrivacyPreference> children = p.children();
				if (!children.hasMoreElements()){
					JOptionPane.showMessageDialog(this, "All leaves of the tree must be Outcomes", "Error in preference", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		
		return true;
		
	}

	private void addCondition(){
		ConditionCreatorDialog dialog = new  ConditionCreatorDialog(this,true,this.masterGUI.getCtxBroker(), this.masterGUI.getUserIdentity());
		if (dialog.getResult()){
			IPrivacyPreferenceCondition condition = dialog.getConditionForPrivacy();
			this.calculateSizeOfObject("Condition object is of size: ", condition);
			IPrivacyPreference node = new PrivacyPreference(condition);
			this.calculateSizeOfObject("Adding condition of size:", node);
			//this.addToSelectedNode(node);
			this.selectedNode.add(node);
			((DefaultTreeModel) this.trDisplayTree.getModel()).nodeStructureChanged(this.selectedNode);
			
			//this.trDisplayTree.expandPath(this.trDisplayTree.getSelectionPath());
			
		}
	}
	
/*	private void deleteNode(){
		TreePath path = trDisplayTree.getSelectionPath();
		IPreference rmNode = (IPreference) path.getLastPathComponent();
		System.out.println("Attempting to remove: "+rmNode.toString());
		rmNode.removeFromParent();
		
		
		trDisplayTree.repaint();
		//IPreference parent = (IPreference) selectedNode.getParent();
		//parent.remove(selectedNode);
		
		
	}*/
	

	private void addOutcome(){
		PPNPOutcomeGUI gui = new PPNPOutcomeGUI(this, this.masterGUI, this.details, true);
	

		if (!gui.isUserCancelled()){
			IPrivacyPreference outcomePref = gui.constructPrivacyPreference();
			if (outcomePref!=null){
				this.selectedNode.add(outcomePref);
				((DefaultTreeModel) this.trDisplayTree.getModel()).nodeStructureChanged(this.selectedNode);				
			}
		}
		
	}
	
	private void printModel (){
		IPrivacyPreferenceTreeModel model = (IPrivacyPreferenceTreeModel) trDisplayTree.getModel();
		IPrivacyPreference p = model.getRootPreference();
		Enumeration<IPrivacyPreference> e = p.postorderEnumeration();
		while (e.hasMoreElements()){
			System.out.println(e.nextElement().toString());
		}
	}
	


	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {
		this.isClosed=true;
		System.out.println(this.getClass().getEnclosingMethod());
	}

	@Override
	public void windowClosing(WindowEvent e) {
		this.isClosed = true;
		System.out.println(this.getClass().getEnclosingMethod());
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

	

	public boolean getIsClosed() {
		return this.isClosed;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		this.selectedNode = (IPrivacyPreference) this.trDisplayTree.getLastSelectedPathComponent();
		if (selectedNode==null){
			System.out.println("selected node is null");
			this.btRemoveCondition.setEnabled(false);
			this.btRemoveOutcome.setEnabled(false);
			this.btAddCondition.setEnabled(true);
			this.btAddOutcome.setEnabled(true);
		}else {
			System.out.println("Selected Node is: "+selectedNode.toString());
			if (selectedNode.getUserObject() instanceof IPrivacyPreferenceCondition){
				this.btRemoveCondition.setEnabled(true);
				this.btRemoveOutcome.setEnabled(false);
				this.btAddCondition.setEnabled(true);
				this.btAddOutcome.setEnabled(true);
			}else if (selectedNode.getUserObject() instanceof PPNPOutcome){
				this.btRemoveCondition.setEnabled(false);
				this.btRemoveOutcome.setEnabled(true);
				this.btAddCondition.setEnabled(false);
				this.btAddOutcome.setEnabled(false);
			}else{
				this.btRemoveCondition.setEnabled(false);
				this.btRemoveOutcome.setEnabled(false);
				this.btAddCondition.setEnabled(true);
				this.btAddOutcome.setEnabled(true);
			}
		}
		
	}
	
	/*
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()){
			//System.out.println("Pressed: Is popup Trigger");

			//System.out.println("click" + e.getSource());
			if (e.getSource().equals(this.trDisplayTree)){

				TreePath path = trDisplayTree.getSelectionPath();
				IPreference selectedNode =  (IPreference) path.getLastPathComponent();
				
				if (selectedNode.isLeaf()){
					trDisplayTree.add(this.popupMenu);
					this.popupMenu.remove(this.addConditionMenuItem);
					this.popupMenu.remove(this.addOutcomeMenuItem);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
					this.btAddCondition.setEnabled(false);
					this.btAddOutcome.setEnabled(false);
					this.btRemoveCondition.setEnabled(false);
					this.btRemoveOutcome.setEnabled(true);
					
					
				}else{
					trDisplayTree.add(this.popupMenu);
					this.popupMenu.add(this.addConditionMenuItem);
					if (!selectedNode.children().hasMoreElements()){
						this.popupMenu.add(this.addOutcomeMenuItem);
					}
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
					this.btAddCondition.setEnabled(true);
					this.btAddOutcome.setEnabled(true);
					this.btRemoveCondition.setEnabled(true);
					this.btRemoveOutcome.setEnabled(false);
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()){
			//System.out.println("Pressed: Is popup Trigger");

			//System.out.println("click" + e.getSource());
			if (e.getSource().equals(this.trDisplayTree)){

				TreePath path = trDisplayTree.getSelectionPath();
				IPreference selectedNode =  (IPreference) path.getLastPathComponent();
				
				if (selectedNode.isLeaf()){
					trDisplayTree.add(this.popupMenu);
					this.popupMenu.remove(this.addConditionMenuItem);
					this.popupMenu.remove(this.addOutcomeMenuItem);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
					this.btAddCondition.setEnabled(false);
					this.btAddOutcome.setEnabled(false);
					this.btRemoveCondition.setEnabled(false);
					this.btRemoveOutcome.setEnabled(true);
					
					
				}else{
					trDisplayTree.add(this.popupMenu);
					this.popupMenu.add(this.addConditionMenuItem);
					if (!selectedNode.children().hasMoreElements()){
						this.popupMenu.add(this.addOutcomeMenuItem);
					}
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
					this.btAddCondition.setEnabled(true);
					this.btAddOutcome.setEnabled(true);
					this.btRemoveCondition.setEnabled(true);
					this.btRemoveOutcome.setEnabled(false);
				}
			}
		}
	}
	
	*/
	
} 
