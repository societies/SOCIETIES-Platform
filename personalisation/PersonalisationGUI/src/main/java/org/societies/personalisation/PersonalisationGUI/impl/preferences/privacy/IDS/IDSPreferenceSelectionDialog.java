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
package org.societies.personalisation.PersonalisationGUI.impl.preferences.privacy.IDS;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JButton;

import org.personalsmartspace.pm.prefGUI.impl.initiatePrefGUI;
import org.personalsmartspace.pss_psm_pssmanager.api.platform.IPssManager;
import org.personalsmartspace.pss_psm_pssmanager.api.platform.PSSInfo;
import org.personalsmartspace.pss_psm_pssmanager.api.pss3p.PssManagerException;
import org.personalsmartspace.pss_sm_api.impl.PssService;
import org.personalsmartspace.pss_sm_api.impl.ServiceMgmtException;
import org.personalsmartspace.spm.identity.api.platform.DigitalPersonalIdentifier;
import org.personalsmartspace.spm.identity.api.platform.IIdentityManagement;
import org.personalsmartspace.spm.identity.api.platform.MalformedDigitialPersonalIdentifierException;
import org.personalsmartspace.spm.preference.api.platform.IDSPreferenceDetails;
import org.personalsmartspace.sre.api.pss3p.IDigitalPersonalIdentifier;
import org.personalsmartspace.sre.api.pss3p.IServiceIdentifier;
import org.personalsmartspace.sre.api.pss3p.PssServiceIdentifier;
/**
 * @author  Administrator
 * @created July 4, 2010
 */
public class IDSPreferenceSelectionDialog extends JDialog  implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static IDSPreferenceSelectionDialog theIDSPreferenceSelectionDialog;

	JPanel pnPanel0;

	JPanel pnPanel1;

	JPanel pnPanel2;
	JLabel lbLabel0;
	JLabel lbLabel4;
	JLabel lbLabel5;

	JPanel pnPanel6;
	JComboBox cmbUserDPIs;
	JComboBox cmbProviderDPIs;
	JComboBox cmbServiceIDs;

	JPanel pnPanel8;
	JButton btOK;
	JButton btCancel;

	private  initiatePrefGUI masterGUI;
	
	private IDSPreferenceDetails details;

	private boolean userCancelled = false;

	private Hashtable<IDigitalPersonalIdentifier, List<IServiceIdentifier>> providerServiceTable ;

	private ArrayList<IDigitalPersonalIdentifier> myConsumerDPIs;
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
		theIDSPreferenceSelectionDialog = new IDSPreferenceSelectionDialog(new JFrame(), null, false);
	} 

	/**
	 */
	public IDSPreferenceSelectionDialog(JFrame frame, initiatePrefGUI masterGUI, boolean isModal) {
		super(frame, "Select IDS Preference details", isModal);
		//super( OWNER, "TITLE", MODAL );
		this.masterGUI = masterGUI;
		this.providerServiceTable = new  Hashtable<IDigitalPersonalIdentifier, List<IServiceIdentifier>>();
		this.myConsumerDPIs = new  ArrayList<IDigitalPersonalIdentifier>();
		this.loadConsumerDPIs();
		this.loadExternalPssData();
		this.showGUI();
	}
	
	public void showGUI(){
		pnPanel0 = new JPanel();
		pnPanel0.setBorder( BorderFactory.createTitledBorder( "Details" ) );
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		pnPanel2 = new JPanel();
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		pnPanel2.setLayout( gbPanel2 );

		lbLabel0 = new JLabel( "My Affected DPI:"  );
		lbLabel0.setIcon( new ImageIcon( "" ) );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.WEST;
		gbcPanel2.insets = new Insets( 0,0,10,0 );
		gbPanel2.setConstraints( lbLabel0, gbcPanel2 );
		pnPanel2.add( lbLabel0 );

		lbLabel4 = new JLabel( "Provider DPI:"  );
		lbLabel4.setIcon( new ImageIcon( "" ) );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 2;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.WEST;
		gbcPanel2.insets = new Insets( 0,0,10,0 );
		gbPanel2.setConstraints( lbLabel4, gbcPanel2 );
		pnPanel2.add( lbLabel4 );

		lbLabel5 = new JLabel( "Service ID:"  );
		lbLabel5.setIcon( new ImageIcon( "" ) );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 3;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.WEST;
		gbcPanel2.insets = new Insets( 0,0,10,0 );
		gbPanel2.setConstraints( lbLabel5, gbcPanel2 );
		pnPanel2.add( lbLabel5 );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( pnPanel2, gbcPanel1 );
		pnPanel1.add( pnPanel2 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.WEST;
		gbcPanel0.insets = new Insets( 10,10,10,10 );
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		pnPanel6 = new JPanel();
		GridBagLayout gbPanel6 = new GridBagLayout();
		GridBagConstraints gbcPanel6 = new GridBagConstraints();
		pnPanel6.setLayout( gbPanel6 );

		cmbUserDPIs = new JComboBox( this.myConsumerDPIs.toArray() );
		gbcPanel6.gridx = 0;
		gbcPanel6.gridy = 0;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 0;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbcPanel6.insets = new Insets( 0,0,10,0 );
		gbPanel6.setConstraints( cmbUserDPIs, gbcPanel6 );
		pnPanel6.add( cmbUserDPIs );

		cmbProviderDPIs = new JComboBox( this.getProviderDPIsForInitialComboBox().toArray() );
		
		this.cmbProviderDPIs.setSelectedIndex(0);
		gbcPanel6.gridx = 0;
		gbcPanel6.gridy = 2;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 0;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbcPanel6.insets = new Insets( 0,0,10,0 );
		gbPanel6.setConstraints( cmbProviderDPIs, gbcPanel6 );
		pnPanel6.add( cmbProviderDPIs );

		
		cmbServiceIDs = new JComboBox( this.getServiceIDsForInitialComboBox(cmbProviderDPIs.getSelectedItem().toString()).toArray() );
		gbcPanel6.gridx = 0;
		gbcPanel6.gridy = 3;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 0;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbPanel6.setConstraints( cmbServiceIDs, gbcPanel6 );
		pnPanel6.add( cmbServiceIDs );
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 10,10,10,10 );
		gbPanel0.setConstraints( pnPanel6, gbcPanel0 );
		pnPanel0.add( pnPanel6 );

		pnPanel8 = new JPanel();
		GridBagLayout gbPanel8 = new GridBagLayout();
		GridBagConstraints gbcPanel8 = new GridBagConstraints();
		pnPanel8.setLayout( gbPanel8 );

		btOK = new JButton( "OK"  );
		this.btOK.addActionListener(this);
		btOK.setIcon( new ImageIcon( "" ) );
		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( btOK, gbcPanel8 );
		pnPanel8.add( btOK );

		btCancel = new JButton( "Cancel"  );
		this.btCancel.addActionListener(this);
		btCancel.setIcon( new ImageIcon( "" ) );
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( btCancel, gbcPanel8 );
		pnPanel8.add( btCancel );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 30,0,0,0 );
		gbPanel0.setConstraints( pnPanel8, gbcPanel0 );
		pnPanel0.add( pnPanel8 );
		
		this.cmbProviderDPIs.addActionListener(this);

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btOK)){
			try{
			IDigitalPersonalIdentifier selectedUserDPI = DigitalPersonalIdentifier.fromString(this.cmbUserDPIs.getSelectedItem().toString());
			this.details = new IDSPreferenceDetails(selectedUserDPI);
			if (this.cmbProviderDPIs.getSelectedIndex()>0){
				try{
				IDigitalPersonalIdentifier selectedProviderDPI = DigitalPersonalIdentifier.fromString(this.cmbProviderDPIs.getSelectedItem().toString());
				this.details.setProviderDPI(selectedProviderDPI);
				if (this.cmbServiceIDs.getSelectedIndex()>0){
					try{
						IServiceIdentifier serviceID = new PssServiceIdentifier(this.cmbServiceIDs.getSelectedItem().toString());
						this.details.setServiceID(serviceID);
					}catch (IllegalArgumentException ex){
						
					}
				}
				}catch(MalformedDigitialPersonalIdentifierException ex){
					JOptionPane.showMessageDialog(this, "Could not parse affected consumer DPI from combobox", "Error getting provider DPI", JOptionPane.ERROR_MESSAGE);
				}
			}
			}catch(MalformedDigitialPersonalIdentifierException ex){
				JOptionPane.showMessageDialog(this, "Could not parse affected consumer DPI from combobox", "Error getting consumer DPI", JOptionPane.ERROR_MESSAGE);
			}
			this.userCancelled = false;
			this.dispose();
		}else if (e.getSource().equals(this.btCancel)){
			this.userCancelled = true;
		}else if (e.getSource().equals(this.cmbProviderDPIs)){
			String dpiStr = (String) this.cmbProviderDPIs.getSelectedItem();
			if (dpiStr.equalsIgnoreCase("Generic")){
				this.cmbServiceIDs.removeAllItems();
			}else{
				this.cmbServiceIDs.removeAllItems();
				List<String> serviceIDStrings = this.getServiceIDsForInitialComboBox(dpiStr);
				for (String serviceIDStr : serviceIDStrings){
					this.cmbServiceIDs.addItem(serviceIDStr);
				}
			}
		}
	} 
	
	
	
	public IDSPreferenceDetails getPreferenceDetails(){
		return this.details;
	}
	
	public boolean isUserCancelled(){
		return this.userCancelled;
		
	}
	private List<String> getProviderDPIsForInitialComboBox(){
		Enumeration<IDigitalPersonalIdentifier> dpis = this.providerServiceTable.keys();
		List<String> dpiStrings = new ArrayList<String>();
		dpiStrings.add("Generic");
		while (dpis.hasMoreElements()){
			dpiStrings.add(dpis.nextElement().toUriString());
		}
		
		return dpiStrings;
	}
	
	private List<String> getServiceIDsForInitialComboBox(String dpiStr){
		if (dpiStr.equalsIgnoreCase("Generic")){
			return new ArrayList<String>();
		}
		Enumeration<IDigitalPersonalIdentifier> keys = this.providerServiceTable.keys();
		IDigitalPersonalIdentifier dpi = null;
		while (keys.hasMoreElements()){
			IDigitalPersonalIdentifier tDPI = keys.nextElement();
			if (dpiStr.equalsIgnoreCase(tDPI.toUriString())){
				dpi = tDPI;
			}
		}
		if (dpi==null){
			return new ArrayList<String>();
		}
		List<IServiceIdentifier> serviceIDs = this.providerServiceTable.get(dpi);
		List<String> serviceIDStrings = new ArrayList<String>();
		serviceIDStrings.add("Generic");
		for (IServiceIdentifier id : serviceIDs){
			serviceIDStrings.add(id.toUriString());
		}
		
		return serviceIDStrings;
	}	
	private void loadExternalPssData(){
		try{
			if (this.masterGUI==null){
				JOptionPane.showMessageDialog(this, "MASTERGUI is NULL");
			}
		IPssManager pssMgr = this.masterGUI.getPssMgr();
		Collection<PSSInfo> pssInfoList = pssMgr.listPSSs();
		List<IDigitalPersonalIdentifier> dpis = new ArrayList<IDigitalPersonalIdentifier>();

		if (pssInfoList!=null){
			for(PSSInfo info : pssInfoList){
				try {
					IDigitalPersonalIdentifier dpi = DigitalPersonalIdentifier.fromString(info.getPublicDPI());
					dpis.add(dpi);
					this.providerServiceTable.put(dpi, new ArrayList<IServiceIdentifier>());
				} catch (MalformedDigitialPersonalIdentifierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		Collection<PssService> allServices = this.masterGUI.getServiceDiscovery().findAllExternalServices();

		if (allServices!=null){
			for (PssService service : allServices){
				try {
					IDigitalPersonalIdentifier dpi = DigitalPersonalIdentifier.fromString(service.getServiceId().getOperatorId());
					if (this.providerServiceTable.containsKey(dpi)){
						this.providerServiceTable.get(dpi).add(service.getServiceId());
					}
				} catch (MalformedDigitialPersonalIdentifierException e) {
					JOptionPane.showMessageDialog(this, "Could not parse DPI from peerID: "+service.getPeerId(), "Error getting PeerID from PssService", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		}
/*		for (IDigitalPersonalIdentifier dpi: dpis){
			Collection<PssService> services =this.masterGUI.getServiceDiscovery().findSharedForeignPSSServices(dpi);
			ArrayList<IServiceIdentifier> serviceIDs = new ArrayList<IServiceIdentifier>();
			for (PssService service : services){
				serviceIDs.add(service.getServiceId());
			}
			this.providerServiceTable.put(dpi, serviceIDs);
		}*/
		}catch (PssManagerException e){
			
		} 
		
	}
	
	
	private void loadConsumerDPIs(){
		IIdentityManagement IDM = this.masterGUI.getIDM();
		myConsumerDPIs = new ArrayList<IDigitalPersonalIdentifier>(); 
		Collections.addAll(myConsumerDPIs, IDM.getAllDigitalPersonalIdentifiers());
		
		ArrayList<IDigitalPersonalIdentifier> standardDPIs = new ArrayList<IDigitalPersonalIdentifier>();
		standardDPIs.add(IDM.getPublicDigitalPersonalIdentifier());
		standardDPIs.add(IDM.getPrivateDigitalPersonalIdentifier());
		standardDPIs.add(IDM.getLocalDigitalPersonalIdentifier());
		
		myConsumerDPIs.removeAll(standardDPIs);
	}
} 
