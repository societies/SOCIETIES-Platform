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
package org.societies.personalisation.PersonalisationGUI.impl.preferences.personalisation;


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
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.osgi.event.EMSException;
import org.societies.personalisation.PersonalisationGUI.impl.preferences.GUI;
import org.societies.personalisation.PersonalisationGUI.impl.preferences.common.ConditionCreatorDialog;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
/**
 * @author  EPapadopoulou@users.sourceforge.net
 * @created April 27, 2010
 */
public class UserPreferenceGUI extends JFrame implements ActionListener,  WindowListener, TreeSelectionListener
{
	static UserPreferenceGUI thepreferenceGUI;

	JPanel pnPanel0;

	JPanel pnPanel3;

	JPanel pnPanel4;
	JButton btAddCondition;
	JButton btAddOutcome;
	JButton btRemoveCondition;
	JButton btRemoveOutcome;
	JButton btClear;

	JPanel pnPanel8;
	JLabel lbLblServiceID;
	JTextField tfTxtServiceID;
	JLabel lbLblServiceType;
	JTextField tfTxtServiceType;

	JPanel pnPanel9;
	JTree trDisplayTree;

	JPanel pnPanel10;
	JButton btSavePreference;
	private final IIdentity userIdentity;
	
	private final GUI masterGUI;
	
	private IPreference selectedNode;
	/*JPopupMenu popupMenu;

	private JMenuItem editMenuItem;

	private JMenuItem deleteMenuItem;

	private JMenuItem addConditionMenuItem;

	private JMenuItem addOutcomeMenuItem;
*/
	private boolean isClosed = false;

	private final PreferenceDetails details;

	private IPreferenceTreeModel preferenceTreeModel;

	private IUserPreferenceManagement prefMgr;
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
	public UserPreferenceGUI(GUI masterGUI, IIdentity identity, PreferenceDetails d) 
	{
		super();
		System.out.println("Creating user preferenceGUI for: \n"+d.toString());
		this.details = d;
		this.userIdentity = identity;
		this.addWindowListener(this);
		this.masterGUI = masterGUI;
		prefMgr = this.masterGUI.getPrefMgr();
		preferenceTreeModel = prefMgr.getModel(identity, d);
		if (this.preferenceTreeModel==null){
			this.preferenceTreeModel = new PreferenceTreeModel(new PreferenceTreeNode());
			this.preferenceTreeModel.setServiceType(d.getServiceType());
			if (d.getServiceID()!=null){
				this.preferenceTreeModel.setServiceID(d.getServiceID());
			}
			this.preferenceTreeModel.setPreferenceName(d.getPreferenceName());
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
		pnPanel8.setBorder( BorderFactory.createTitledBorder( "User Preferences GUI - mailto:EPapadopoulou@users.sourceforge.net" ) );
		GridBagLayout gbPanel8 = new GridBagLayout();
		GridBagConstraints gbcPanel8 = new GridBagConstraints();
		pnPanel8.setLayout( gbPanel8 );

		lbLblServiceID = new JLabel( "Service ID:"  );
		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( lbLblServiceID, gbcPanel8 );
		pnPanel8.add( lbLblServiceID );

		tfTxtServiceID = new JTextField( );
		if (this.details.getServiceID()!=null){
			tfTxtServiceID.setText(this.details.getServiceID().getServiceInstanceIdentifier());
		}else{
			tfTxtServiceID.setText("Generic");
		}
		tfTxtServiceID.setEditable(false);
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( tfTxtServiceID, gbcPanel8 );
		pnPanel8.add( tfTxtServiceID );

		lbLblServiceType = new JLabel( "Service Type:"  );

		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 1;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( lbLblServiceType, gbcPanel8 );
		pnPanel8.add( lbLblServiceType );

		tfTxtServiceType = new JTextField( );
		tfTxtServiceType.setText(this.details.getServiceType());
		tfTxtServiceType.setEditable(false);
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 1;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( tfTxtServiceType, gbcPanel8 );
		pnPanel8.add( tfTxtServiceType );
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
		this.selectedNode = (IPreference) this.trDisplayTree.getModel().getRoot();
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btAddCondition)){
			this.addCondition();
		}else if (e.getSource().equals(this.btAddOutcome)){
			this.addOutcome();
		}else if (e.getSource().equals(this.btClear)){
			IPreferenceTreeModel model = new PreferenceTreeModel(new PreferenceTreeNode());
			this.trDisplayTree.setModel(model);
			

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
		
		
		try{
			IPreferenceTreeModel model = (IPreferenceTreeModel) this.trDisplayTree.getModel();
			boolean stored = prefMgr.storePreference(this.userIdentity, details, model.getRootPreference());
			if (stored){
				//need to inform pcm
			JOptionPane.showMessageDialog(this, "Successfully stored preference", "Preference saved", JOptionPane.INFORMATION_MESSAGE);
			this.dispose();
			}else{
				JOptionPane.showMessageDialog(this, "An error occurred while storing preference. Please report this to the SOCIETIES administrators if the problem persists.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "An error occurred while storing preference. Please report this to the SOCIETIES administrators if the problem persists.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		
		
	}

	private boolean checkPreference(){
		IPreferenceTreeModel model  =  (IPreferenceTreeModel) this.trDisplayTree.getModel();
		Enumeration<IPreference> nodeEnum = model.getRootPreference().breadthFirstEnumeration();
		int index = 1;
		while (nodeEnum.hasMoreElements()){
			System.out.println("Node no"+index);
			index +=1;
			IPreference p = nodeEnum.nextElement();
			if (p.isLeaf()){
				Enumeration<IPreference> children = p.children();
				if (children.hasMoreElements()){
					JOptionPane.showMessageDialog(this, "An outcome cannot have conditions below it", "Error in preference", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			if (p.isBranch()){
				Enumeration<IPreference> children = p.children();
				if (!children.hasMoreElements()){
					JOptionPane.showMessageDialog(this, "Preference is incomplete. A Preference has to have an outcome at the end. ", "Error in preference", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		
		return true;
		
	}

	private void addCondition(){
		ConditionCreatorDialog dialog = new  ConditionCreatorDialog(this,true,this.masterGUI.getCtxBroker(), this.userIdentity);
		if (dialog.getResult()){
			IPreferenceCondition condition = dialog.getConditionObject();
			this.calculateSizeOfObject("Condition object is of size: ", condition);
			IPreference node = new PreferenceTreeNode(condition);
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
		OutcomeCreatorDialog dialog = new OutcomeCreatorDialog(this, details);
		if (dialog.getResponse()){
			IPreferenceOutcome outcome = dialog.getOutcome();
			IPreference node = new PreferenceTreeNode(outcome);
			this.calculateSizeOfObject("Adding outcome of size:", node);
			//this.addToSelectedNode(node);
			this.selectedNode.add(node);
			
			((DefaultTreeModel) this.trDisplayTree.getModel()).nodeStructureChanged(this.selectedNode);
		}
		else{
			System.out.println("Result of OutcomeCreator is false");
		}
	}
	
	private void printModel (){
		IPreferenceTreeModel model = (IPreferenceTreeModel) trDisplayTree.getModel();
		IPreference p = model.getRootPreference();
		Enumeration<IPreference> e = p.postorderEnumeration();
		while (e.hasMoreElements()){
			System.out.println(e.nextElement().toString());
		}
	}
	
/*	private void addToSelectedNode(IPreference node){
		
		this.calculateSizeOfObject("Model size before addition:",this.trDisplayTree.getModel());
		TreePath path = trDisplayTree.getSelectionPath();
		IPreference selectedNode = (IPreference) path.getLastPathComponent();
		selectedNode.add(node);
		this.calculateSizeOfObject("Model size after addition:",this.trDisplayTree.getModel());
		
	}*/
	
	
	private IPreferenceTreeModel loadPreferences(){
/*		IPreferenceHandler prefmgr = this.masterGUI.getPrefMgr();
		System.out.println("Requesting preference for: ");
		System.out.println("DPI: "+this.dpi.toUriString());
		System.out.println("ServiceType: "+this.serviceType);
		System.out.println("ServiceID: "+this.serviceID.toUriString());
		System.out.println("Preference: "+this.prefName);
		return prefmgr.getModel(this.dpi, this.serviceType, this.serviceID, this.prefName);*/
		
		
		return null;
	}


	
/*	private void sendEvent(){
		PreferenceChangedEvent event = new PreferenceChangedEvent(userIdentity.toUriString(), details.getServiceID(), details.getServiceType(), details.getPreferenceName());
		PSSEvent pssEvent  = new PSSEvent(PSSEventTypes.PREFERENCES_CHANGED, "", this.getClass().getName(), XMLConverter.objectToXml(event));
		try {
			this.masterGUI.getEventMgr().postEventToPSS(pssEvent);
		} catch (EMSException e) {
			e.printStackTrace();
		}
	}*/

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent e) {
		this.isClosed=true;
		System.out.println(this.getClass().getEnclosingMethod());
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		this.isClosed = true;
		System.out.println(this.getClass().getEnclosingMethod());
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent e) {}

	

	public boolean getIsClosed() {
		return this.isClosed;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		this.selectedNode = (IPreference) this.trDisplayTree.getLastSelectedPathComponent();
		if (selectedNode==null){
			System.out.println("selected node is null");
			this.btRemoveCondition.setEnabled(false);
			this.btRemoveOutcome.setEnabled(false);
			this.btAddCondition.setEnabled(true);
			this.btAddOutcome.setEnabled(true);
		}else {
			System.out.println("Selected Node is: "+selectedNode.toString());
			if (selectedNode.getUserObject() instanceof IPreferenceCondition){
				this.btRemoveCondition.setEnabled(true);
				this.btRemoveOutcome.setEnabled(false);
				this.btAddCondition.setEnabled(true);
				this.btAddOutcome.setEnabled(true);
			}else if (selectedNode.getUserObject() instanceof IOutcome){
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
		this.trDisplayTree.requestFocusInWindow();
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
