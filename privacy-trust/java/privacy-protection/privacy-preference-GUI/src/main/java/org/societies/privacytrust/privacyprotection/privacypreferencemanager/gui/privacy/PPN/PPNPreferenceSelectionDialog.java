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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PPNPreferenceDetails;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.GUI;
/**
 * @author  Administrator
 * @created July 3, 2010
 */
public class PPNPreferenceSelectionDialog extends JDialog implements ActionListener
{
	static PPNPreferenceSelectionDialog thePPNPreferenceSelectionDialog;

	JPanel pnPanel0;

	JPanel pnPanel1;

	JPanel pnPanel2;
	JLabel lbLabel0;
	JLabel lbLabel3;
	JLabel lbLabel4;
	JLabel lbLabel5;

	JPanel pnPanel6;
	JComboBox cmbContextTypes;
	JComboBox cmbContextIDs;
	JComboBox cmbProviderDPIs;
	JComboBox cmbServiceIDs;

	JPanel pnPanel8;
	JButton btOK;
	JButton btCancel;

	private GUI masterGUI;
	
	
	private List<Requestor> providers;

	private Hashtable<String, List<CtxAttributeIdentifier>> contextTable;

	private boolean userCancelled = false;


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
		thePPNPreferenceSelectionDialog = new PPNPreferenceSelectionDialog();
	} 


	public PPNPreferenceSelectionDialog(){
		super();
		//this.loadExternalPssData();
		this.retrieveCtxIDs();
		this.showGUI();
	}
	/**
	 */
	public PPNPreferenceSelectionDialog(JFrame frame, GUI masterGUI) 
	{
		super(frame, "Preference Details", true);
		this.masterGUI = masterGUI;
		//super( OWNER, "TITLE", MODAL );
		this.providers = new ArrayList<Requestor>();
		this.contextTable = new Hashtable<String, List<CtxAttributeIdentifier>>();
		//this.loadExternalPssData();
		this.retrieveCtxIDs();
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

		lbLabel0 = new JLabel( "Context Type:"  );
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

		lbLabel3 = new JLabel( "Context ID:"  );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 1;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.WEST;
		gbcPanel2.insets = new Insets( 0,0,10,0 );
		gbPanel2.setConstraints( lbLabel3, gbcPanel2 );
		pnPanel2.add( lbLabel3 );

		lbLabel4 = new JLabel( "Provider DPI:"  );
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


		cmbContextTypes = new JComboBox( this.getContextTypes().toArray() );
		cmbContextTypes.setSelectedIndex(0);
		gbcPanel6.gridx = 0;
		gbcPanel6.gridy = 0;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 0;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbcPanel6.insets = new Insets( 0,0,10,0 );
		gbPanel6.setConstraints( cmbContextTypes, gbcPanel6 );
		pnPanel6.add( cmbContextTypes );


		cmbContextIDs = new JComboBox( this.getCtxIDsForInitialComboBox(cmbContextTypes.getSelectedItem().toString()).toArray() );
		gbcPanel6.gridx = 0;
		gbcPanel6.gridy = 1;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 0;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbcPanel6.insets = new Insets( 0,0,10,0 );
		gbPanel6.setConstraints( cmbContextIDs, gbcPanel6 );
		pnPanel6.add( cmbContextIDs );


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


		cmbServiceIDs = new JComboBox( this.getCisOrServiceIDsForInitialComboBox(this.cmbProviderDPIs.getSelectedItem().toString()).toArray() );
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


		this.cmbContextTypes.addActionListener(this);
		this.cmbProviderDPIs.addActionListener(this);
		this.btCancel.addActionListener(this);
		this.btOK.addActionListener(this);


		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
	}


	public PPNPreferenceDetails getNewPPNPreferenceDetails(){
		
		String contextType = (String) this.cmbContextTypes.getSelectedItem();
		PPNPreferenceDetails details = new PPNPreferenceDetails(contextType);
		
		String ctxIDStr = (String) this.cmbContextIDs.getSelectedItem();
		if (!ctxIDStr.equalsIgnoreCase("Generic")){
			List<CtxAttributeIdentifier> attrIDList = this.contextTable.get(contextType);
			
			for (CtxAttributeIdentifier temp : attrIDList){
				if (temp.getUri().equalsIgnoreCase(ctxIDStr)){
					details.setAffectedDataId(temp);
					break;
				}
			}
		}

		String dpiStr = (String) this.cmbProviderDPIs.getSelectedItem();
		if (!dpiStr.equalsIgnoreCase("Generic")){
			
				int index = this.cmbProviderDPIs.getSelectedIndex();
				
				details.setRequestor(this.providers.get(index));
		}


		return details;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btCancel)){
			this.userCancelled = true;
			this.dispose();
		}else
			if (e.getSource().equals(this.btOK)){
				this.userCancelled = false;
				this.dispose();
			}
		if (e.getSource().equals(this.cmbContextTypes)){
			String type = (String) cmbContextTypes.getSelectedItem();
			List<String> ctxIDStrings = this.getCtxIDsForInitialComboBox(type);
			this.cmbContextIDs.removeAllItems();
			for (String idStr : ctxIDStrings){
				this.cmbContextIDs.addItem(idStr);
			}
		}else
			if (e.getSource().equals(this.cmbProviderDPIs)){
				String dpiStr = (String) this.cmbProviderDPIs.getSelectedItem();
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


	private void retrieveCtxIDs(){


		ICtxBroker broker = this.masterGUI.getCtxBroker();
		try {
			CtxEntity operator = broker.retrieveIndividualEntity(this.masterGUI.getIdMgr().getThisNetworkNode()).get();
			Set<CtxAttribute> list = operator.getAttributes();

			Iterator<CtxAttribute> it = list.iterator();

			while(it.hasNext()){
				CtxAttribute attr = it.next();
				if (this.contextTable.containsKey(attr.getType())){
					this.contextTable.get(attr.getType()).add(attr.getId());
				}else{
					List<CtxAttributeIdentifier> idList = new ArrayList<CtxAttributeIdentifier>();
					idList.add(attr.getId());
					this.contextTable.put(attr.getType(), idList);
				}
			}

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private List<String> getContextTypes(){
		List<String> contextTypes = new ArrayList<String>();
		Enumeration<String> types = this.contextTable.keys();
		while (types.hasMoreElements()){
			contextTypes.add(types.nextElement());
		}
		System.out.println(contextTypes.size()+" context types found");
		return contextTypes;
	}

	private List<String> getCtxIDsForInitialComboBox(String type){
		List<CtxAttributeIdentifier> ctxIDs = this.contextTable.get(type);
		System.out.println(ctxIDs.size()+" context IDs found");
		List<String> ctxIDStrings = new ArrayList<String>();
		ctxIDStrings.add("Generic");
		for (CtxIdentifier ctxID : ctxIDs){
			ctxIDStrings.add(ctxID.toUriString());
		}

		return ctxIDStrings;
	}

	private List<String> getProviderDPIsForInitialComboBox(){

		List<String> identityStrings = new ArrayList<String>();
		identityStrings.add("Generic");

		for (Requestor providerid : this.providers){
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
		for (Requestor requestor : this.providers){
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
			List<IDigitalPersonalIdentifier> dpis = new ArrayList<IDigitalPersonalIdentifier>();

			if (pssInfoList!=null){
				for(PSSInfo info : pssInfoList){
					try {

						IDigitalPersonalIdentifier dpi = DigitalPersonalIdentifier.fromString(info.getPublicDPI());
						dpis.add(dpi);
						this.providers.put(dpi, new ArrayList<IServiceIdentifier>());
					} catch (MalformedDigitialPersonalIdentifierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			Collection<PssService> allServices;
			
				allServices = this.masterGUI.getServiceDiscovery().findAllExternalServices();
				

				if (allServices!=null){
					for (PssService service : allServices){
						try {
							IDigitalPersonalIdentifier dpi = DigitalPersonalIdentifier.fromString(service.getServiceId().getOperatorId());
							if (this.providers.containsKey(dpi)){
								this.providers.get(dpi).add(service.getServiceId());
							}
						} catch (MalformedDigitialPersonalIdentifierException e) {
							JOptionPane.showMessageDialog(this, "Could not parse DPI from peerID: "+service.getPeerId(), "Error getting PeerID from PssService", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}

				for (IDigitalPersonalIdentifier dpi: dpis){

					Collection<PssService> services =this.masterGUI.getServiceDiscovery().findSharedForeignPSSServices(dpi);
					if (services!=null){
						ArrayList<IServiceIdentifier> serviceIDs = new ArrayList<IServiceIdentifier>();
						for (PssService service : services){
							serviceIDs.add(service.getServiceId());
						}
						this.providerServiceTable.put(dpi, serviceIDs);
					}else{
						System.out.println("Didn't find any services for DPI: "+dpi.toUriString());
						this.providerServiceTable.put(dpi, new ArrayList<IServiceIdentifier>());
					}
				}

			
		}catch (PssManagerException e){

		}
	}*/

	public boolean isUserCancelled(){
		return this.userCancelled;
	}
} 
