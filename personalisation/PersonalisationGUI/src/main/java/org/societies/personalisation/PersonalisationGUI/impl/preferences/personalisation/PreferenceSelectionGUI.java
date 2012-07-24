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


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.personalisation.PersonalisationGUI.impl.preferences.GUI;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;

/**
 * @author  Administrator
 * @created July 1, 2010
 */
public class PreferenceSelectionGUI extends JFrame implements ActionListener, WindowListener
{
	static PreferenceSelectionGUI thePreferenceSelectionGUI;

	JPanel pnPanel0;

	JPanel pnPanel1;

	JPanel pnPanel3;
	JButton btEditPreference;
	JButton btCreatePreference;

	JPanel pnPanel4;
	JTable tbTable1;

	private GUI masterGUI;

	private IIdentity userIdentity;

	
	private PreferenceDetailsTableModel model = new PreferenceDetailsTableModel();

	private boolean isClosed = false;

	private JButton btDeletePreference;

	private JPanel pnPanel5;

	private JButton btRefresh;

	private IUserPreferenceManagement prefMgr;
	
	private IServiceDiscovery serviceDiscovery;
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
		thePreferenceSelectionGUI = new PreferenceSelectionGUI();
	} 

	/**
	 */
	public PreferenceSelectionGUI(){
		super( "Preferences" );
		this.addWindowListener(this);
		
		this.showGUI();
		
	} 
	public PreferenceSelectionGUI(GUI masterGUI, IIdentity identity){
		super( "Preferences for DPI: "+identity.toString() );
		this.setLocation();
		this.addWindowListener(this);
		this.masterGUI = masterGUI;
		this.userIdentity = identity;
		this.serviceDiscovery = this.masterGUI.getServiceDiscovery();
		this.retrievePreferenceDetails();
		this.showGUI();
		
	} 

	private void setLocation(){
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int maxX = screenSize.width ;
	    int maxY = screenSize.height ;
	    this.setLocation((maxX/3), (maxY/3));
		//this.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
	}
	
/*	private void setLocationAndSize(){
			    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			    int maxX = screenSize.width ;
			    int maxY = screenSize.height ;
			    this.setLocation((maxX/3), (maxY/3));
				this.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
			    this.getContentPane().setSize(400, 300);
			}*/
	public void showGUI()
	{
		

		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		pnPanel3 = new JPanel();
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		btEditPreference = new JButton( "Edit Selected"  );
		btEditPreference.addActionListener(this);
		gbcPanel3.gridx = 2;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbcPanel3.insets = new Insets( 1,1,1,1 );
		gbPanel3.setConstraints( btEditPreference, gbcPanel3 );
		pnPanel3.add( btEditPreference );

		btCreatePreference = new JButton( "Create New"  );
		btCreatePreference.addActionListener(this);
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbcPanel3.insets = new Insets( 1,1,1,1 );
		gbPanel3.setConstraints( btCreatePreference, gbcPanel3 );
		pnPanel3.add( btCreatePreference );

		btDeletePreference = new JButton( "Delete Selected"  );
		btDeletePreference.addActionListener(this);
		gbcPanel3.gridx = 1;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbPanel3.setConstraints( btDeletePreference, gbcPanel3 );
		pnPanel3.add( btDeletePreference );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.SOUTH;
		gbPanel1.setConstraints( pnPanel3, gbcPanel1 );
		pnPanel1.add( pnPanel3 );

		pnPanel4 = new JPanel();
		pnPanel4.setBorder( BorderFactory.createTitledBorder( "Existing preferences" ) );
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );


		tbTable1 = new JTable(model);
		tbTable1.setPreferredScrollableViewportSize(new Dimension (400,150));
		tbTable1.setFillsViewportHeight(true);
		//tbTable1.setAutoCreateRowSorter( true );
		JScrollPane scpTable1 = new JScrollPane( tbTable1 );
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 1;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 5,0,20,0 );
		gbPanel4.setConstraints( scpTable1, gbcPanel4 );
		pnPanel4.add( scpTable1 );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( pnPanel4, gbcPanel1 );
		pnPanel1.add( pnPanel4 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		pnPanel5 = new JPanel();
		GridBagLayout gbPanel5 = new GridBagLayout();
		GridBagConstraints gbcPanel5 = new GridBagConstraints();
		pnPanel5.setLayout( gbPanel5 );

		btRefresh = new JButton( "Refresh Table"  );
		btRefresh.addActionListener(this);
		gbcPanel5.gridx = 0;
		gbcPanel5.gridy = 0;
		gbcPanel5.gridwidth = 1;
		gbcPanel5.gridheight = 1;
		gbcPanel5.fill = GridBagConstraints.BOTH;
		gbcPanel5.weightx = 1;
		gbcPanel5.weighty = 0;
		gbcPanel5.anchor = GridBagConstraints.NORTH;
		gbcPanel5.insets = new Insets( 10,2,5,2 );
		gbPanel5.setConstraints( btRefresh, gbcPanel5 );
		pnPanel5.add( btRefresh );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel5, gbcPanel0 );
		pnPanel0.add( pnPanel5 );


		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		this.setLocation();
		setVisible( true );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PreferenceDetails detail;
		if (e.getSource().equals(this.btCreatePreference)){
			System.out.println("Create new preference");
			PreferenceSelectionDialog dialog = new PreferenceSelectionDialog(this.masterGUI);
			
			detail = dialog.getPreferenceDetail();
			if (detail==null){
				JOptionPane.showMessageDialog(this, "detail is null");
				return;
			}
			System.out.println("Going to create new preference for \n"+detail.toString());
			UserPreferenceGUI gui = new UserPreferenceGUI(this.masterGUI, this.userIdentity, detail);
		
			this.refreshTable();
		}else if (e.getSource().equals(this.btEditPreference)){
			if (this.tbTable1.getSelectedRow()!=-1)
			{
				int row  = this.tbTable1.getSelectedRow();
				detail = ((PreferenceDetailsTableModel)this.tbTable1.getModel()).getRow(row);
				System.out.println("Edit existing preference");
				UserPreferenceGUI gui = new UserPreferenceGUI(this.masterGUI, this.userIdentity, detail);
			}else{
				JOptionPane.showMessageDialog(this, "Please select a preference to edit", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}else if (e.getSource().equals(this.btRefresh)){
			System.out.println("Refreshing table data");
			this.refreshTable();
			System.out.println("Refreshed table data ");
		}else if (e.getSource().equals(this.btDeletePreference)){
			if (this.tbTable1.getSelectedRow()!=-1)
			{
				int row  = this.tbTable1.getSelectedRow();
				detail = ((PreferenceDetailsTableModel)this.tbTable1.getModel()).getRow(row);
				System.out.println("Edit existing preference");
				
				prefMgr.deletePreference(userIdentity, detail);
				this.refreshTable();
			}else{
				JOptionPane.showMessageDialog(this, "Please select a preference to delete", "Error", JOptionPane.ERROR_MESSAGE);
			}			
		}
		
		
		
	} 
	
	
	
	private void retrievePreferenceDetails(){
		prefMgr = this.masterGUI.getPrefMgr();
		
		List<PreferenceDetails> details = prefMgr.getPreferenceDetailsForAllPreferences();
		this.model = new PreferenceDetailsTableModel(details);
		
	}
	
	
	private void refreshTable(){
		if (SwingUtilities.isEventDispatchThread()){
			System.out.println("is EventDispatchThread");
		}else{
			System.out.println("NOT in EventDispatchThread");
		}
		
		List<PreferenceDetails> details = prefMgr.getPreferenceDetailsForAllPreferences();
		if (details.isEmpty()){
			System.out.println("Refreshed data empty");
		}else{
			System.out.println("Refreshed data not empty");
		}
		PreferenceDetailsTableModel dModel = new PreferenceDetailsTableModel(details);
		this.tbTable1.setModel(dModel);
		
	}
	

	public boolean getIsClosed(){
		return this.isClosed;
	}
	/*
	 * WindowListener methods
	 */
	
	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {
		this.isClosed =true;
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
} 
