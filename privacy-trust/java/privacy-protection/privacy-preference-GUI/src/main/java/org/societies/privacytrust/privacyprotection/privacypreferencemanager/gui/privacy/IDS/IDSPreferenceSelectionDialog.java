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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.privacy.IDS;

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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IDSPreferenceDetails;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.GUI;
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
	JComboBox cmbProviderIdentities;
	JComboBox cmbServiceIDs;

	JPanel pnPanel8;
	JButton btOK;
	JButton btCancel;

	private  GUI masterGUI;

	private IDSPreferenceDetails details;

	private boolean userCancelled = false;

	private List<Requestor> providerList ;

	private ArrayList<IIdentity> userIdentities;

	private IIdentityManager identityMgr;
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
	public IDSPreferenceSelectionDialog(JFrame frame, GUI masterGUI, boolean isModal) {
		super(frame, "Select IDS Preference details", isModal);
		//super( OWNER, "TITLE", MODAL );
		this.masterGUI = masterGUI;
		this.providerList = new ArrayList<Requestor>();
		this.userIdentities = new  ArrayList<IIdentity>();
		this.identityMgr = this.masterGUI.getIdMgr();
		this.loadConsumerDPIs();
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

		cmbUserDPIs = new JComboBox( this.userIdentities.toArray() );
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

		cmbProviderIdentities = new JComboBox( this.getProviderDPIsForInitialComboBox().toArray() );

		this.cmbProviderIdentities.setSelectedIndex(0);
		gbcPanel6.gridx = 0;
		gbcPanel6.gridy = 2;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 0;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbcPanel6.insets = new Insets( 0,0,10,0 );
		gbPanel6.setConstraints( cmbProviderIdentities, gbcPanel6 );
		pnPanel6.add( cmbProviderIdentities );


		cmbServiceIDs = new JComboBox( this.getCisOrServiceIDsForInitialComboBox(cmbProviderIdentities.getSelectedItem().toString()).toArray() );
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

		this.cmbProviderIdentities.addActionListener(this);

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btOK)){
			IIdentity selectedUserIdentity = this.userIdentities.get(this.cmbUserDPIs.getSelectedIndex());
			this.details = new IDSPreferenceDetails(selectedUserIdentity);
			if (this.cmbProviderIdentities.getSelectedIndex()>0){
				this.details.setRequestor(this.providerList.get(cmbProviderIdentities.getSelectedIndex()));
			}
		}else if (e.getSource().equals(this.btCancel)){
			this.userCancelled = true;
			this.dispose();
		}else if (e.getSource().equals(this.cmbProviderIdentities)){
			String dpiStr = (String) this.cmbProviderIdentities.getSelectedItem();
			if (dpiStr.equalsIgnoreCase("Generic")){
				this.cmbServiceIDs.removeAllItems();
			}else{
				this.cmbServiceIDs.removeAllItems();
				List<String> serviceIDStrings = this.getCisOrServiceIDsForInitialComboBox(dpiStr);
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

		List<String> identityStrings = new ArrayList<String>();
		identityStrings.add("Generic");

		for (Requestor providerid : this.providerList){
			identityStrings.add(providerid.getRequestorId().getJid());
		}
		return identityStrings;
	}


	private List<String> getCisOrServiceIDsForInitialComboBox(String dpiStr){
		if (dpiStr.equalsIgnoreCase("Generic")){
			return new ArrayList<String>();
		}
		
		List<String> cisIdorServiceId = new ArrayList<String>();
		boolean genericAdded = false;
		for (Requestor requestor : this.providerList){
			if (requestor.getRequestorId().getJid().equalsIgnoreCase(dpiStr)){
				if (requestor instanceof RequestorCis){
					cisIdorServiceId.add(((RequestorCis) requestor).getCisRequestorId().getJid());
				}else if (requestor instanceof RequestorService){
					cisIdorServiceId.add(((RequestorService) requestor).getRequestorServiceId().getServiceInstanceIdentifier());
				}else{
					if (genericAdded == false){
						cisIdorServiceId.add("Generic");
						genericAdded = true;
					}
				}
				
			}
		}
		
		return cisIdorServiceId;
	}	
/*	private void loadExternalPssData(){
		try{
			if (this.masterGUI==null){
				JOptionPane.showMessageDialog(this, "MASTERGUI is NULL");
			}
			IPssManager pssMgr = this.masterGUI.getPssMgr();
			Collection<PSSInfo> pssInfoList = pssMgr.listPSSs();
			List<IIdentity> dpis = new ArrayList<IIdentity>();

			if (pssInfoList!=null){
				for(PSSInfo info : pssInfoList){
					try {
						IIdentity dpi = DigitalPersonalIdentifier.fromString(info.getPublicDPI());
						dpis.add(dpi);
						this.providerList.put(dpi, new ArrayList<IServiceIdentifier>());
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
						IIdentity dpi = DigitalPersonalIdentifier.fromString(service.getServiceId().getOperatorId());
						if (this.providerList.containsKey(dpi)){
							this.providerList.get(dpi).add(service.getServiceId());
						}
					} catch (MalformedDigitialPersonalIdentifierException e) {
						JOptionPane.showMessageDialog(this, "Could not parse DPI from peerID: "+service.getPeerId(), "Error getting PeerID from PssService", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
			}
					for (IIdentity dpi: dpis){
			Collection<PssService> services =this.masterGUI.getServiceDiscovery().findSharedForeignPSSServices(dpi);
			ArrayList<IServiceIdentifier> serviceIDs = new ArrayList<IServiceIdentifier>();
			for (PssService service : services){
				serviceIDs.add(service.getServiceId());
			}
			this.providerServiceTable.put(dpi, serviceIDs);
		}
		}catch (PssManagerException e){

		} 

	}
*/

	private void loadConsumerDPIs(){
		
		userIdentities = new ArrayList<IIdentity>(); 
		Set<IIdentity> ids = this.identityMgr.getPublicIdentities();
		
		Iterator<IIdentity> it = ids.iterator();
		
		while (it.hasNext()){
			userIdentities.add(it.next());
		}

	}
} 
