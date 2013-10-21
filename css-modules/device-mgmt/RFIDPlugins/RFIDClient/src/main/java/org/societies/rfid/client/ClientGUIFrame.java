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
package org.societies.rfid.client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.rfid.server.api.remote.IRfidServer;


/**
 * @author  Administrator
 * @created January 27, 2011
 */
public class ClientGUIFrame extends JFrame
{
	
	static ClientGUIFrame theClientGUIFrame;

	public final String RFIDCtxType = "rfidTagNo";
	SymLocMonitorTable symlocTableModel;
	JPanel pnPanel0;

	JPanel pnPanel1;

	JPanel pnPanel2;
	JLabel lbTagNumber;
	JTextField tfTagNumber;

	JPanel pnPanel3;
	JTable tbData;
	JPanel pnPanel4;
	JButton btPush;


	private ICtxBroker ctxBroker;

	private IIdentity userIdentity;

	private ServiceResourceIdentifier serviceID;
	
	private String rfidTagNumber = "";

	private CtxAttribute attr;
	
	private final IIdentity serverIdentity;
	
	private IRfidServer rfidServer;
	private Requestor me;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	/**
	 */
	public ClientGUIFrame(
			IRfidServer server,
			ICtxBroker broker,
			IIdentity userIdentity,  
			IIdentity serverIdentity,
			ServiceResourceIdentifier serviceID) 
	{
		super( "My RFID location monitoring application" );
		if(logging.isDebugEnabled()) logging.debug("Starting clientGUI");
		this.rfidServer = server;
		this.userIdentity = userIdentity;
		this.serverIdentity = serverIdentity;
		this.ctxBroker = broker;
		this.serviceID = serviceID;
		this.setUp();
		
		
		pnPanel0 = new JPanel();
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

		lbTagNumber = new JLabel( "Tag Number"  );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( lbTagNumber, gbcPanel2 );
		pnPanel2.add( lbTagNumber );

		tfTagNumber = new JTextField( );
		tfTagNumber.setText(rfidTagNumber);
		gbcPanel2.gridx = 1;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( tfTagNumber, gbcPanel2 );
		pnPanel2.add( tfTagNumber );
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 19;
		gbcPanel1.gridheight = 3;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( pnPanel2, gbcPanel1 );
		pnPanel1.add( pnPanel2 );

		pnPanel3 = new JPanel();
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		symlocTableModel = new SymLocMonitorTable();
		tbData = new JTable( symlocTableModel );
		JScrollPane scpData = new JScrollPane( tbData );
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 1;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbPanel3.setConstraints( scpData, gbcPanel3 );
		pnPanel3.add( scpData );
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 5;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( pnPanel3, gbcPanel1 );
		pnPanel1.add( pnPanel3 );

		pnPanel4 = new JPanel();
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );


		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 20;
		gbcPanel0.gridheight = 20;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
		if(logging.isDebugEnabled()) logging.debug("Started client GUI");
	} 
	
	private void setUp(){
		me = new RequestorService(this.serverIdentity, this.serviceID);
		
		/*
		 * first we need to retrieve the tag number if it exists in the db. 
		 * otherwise, we will ask the user to enter the tag number and the password
		 */
		
		try {
			Future<CtxEntityIdentifier> futureOperatorEntityId = ctxBroker.retrieveIndividualEntityId(me, userIdentity);
			CtxEntityIdentifier operatorId = futureOperatorEntityId.get();
					//broker.retrievebroker.retrieveOperator(this.myPublicDPI, userIdentity);
			
			Future<List<CtxIdentifier>> futureAttributeIds= this.ctxBroker.lookup(me, operatorId, CtxModelType.ATTRIBUTE, RFIDCtxType);
			List<CtxIdentifier> attributes = futureAttributeIds.get();
			if (attributes.size()==0){
				
				
			
				 this.rfidTagNumber = "";
				while (this.rfidTagNumber==null || this.rfidTagNumber.equalsIgnoreCase("")){
				this.rfidTagNumber = (String)JOptionPane.showInputDialog(
				                    this,
				                    "Enter the RFID tag number below",
				                    "RFID Tag needed",
				                    JOptionPane.PLAIN_MESSAGE,
				                    null,
				                    null,
				                    "");
				}
				
				String password = "";
				while (password==null || password.equalsIgnoreCase("")){
					password = (String)JOptionPane.showInputDialog(
					                    this,
					                    "Enter the password given to you by the RFID Controller administrator for "+this.rfidTagNumber+" below",
					                    "Password needed",
					                    JOptionPane.PLAIN_MESSAGE,
					                    null,
					                    null,
					                    "");
					}
				
					attr = ctxBroker.createAttribute(me, operatorId, RFIDCtxType).get();
				
				
					//rfidAPI.registerRFIDTag(this.rfidTagNumber, userIdentity.toUriString(), password);
					this.sendRegisterMessage(password);
					
					System.out.println("Sent Request to Register tag: "+this.rfidTagNumber);
					
				
			}else{
				attr = (CtxAttribute) this.ctxBroker.retrieve(me, attributes.get(0)).get();
				this.rfidTagNumber = attr.getStringValue();
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
	
	private void sendRegisterMessage(String password) {
		
		if (serverIdentity==null){
			JOptionPane.showMessageDialog(this, "serverIdentityIsnull");
		}
		if (rfidTagNumber==null){
			JOptionPane.showMessageDialog(this, "rfid tag number is null");
		}
		
		if (this.userIdentity==null){
			JOptionPane.showMessageDialog(this, "userid is null");
			
		}
		
		if (this.serviceID==null){
			JOptionPane.showMessageDialog(this, "serviceID is null");
		}else{
			if (this.serviceID.getIdentifier()==null){
				JOptionPane.showMessageDialog(this, "serviceID.getid is null");
			}
		}
		
		if (password==null){
			JOptionPane.showMessageDialog(this, "password is null");
		}
		
		if (this.rfidServer==null){
			JOptionPane.showMessageDialog(this, "rfidServer is null");
		}
		this.rfidServer.registerRFIDTag(serverIdentity.getJid(), rfidTagNumber, this.userIdentity.getJid(), "", password);


	}
	public void sendSymLocUpdate(String tagNumber, String symLoc){
		Vector<String> row = new Vector<String>();
		row.add(symLoc);
		row.add(new Date().toString());
		this.symlocTableModel.addRow(row);
		this.tbData.setModel(symlocTableModel);

		
	}
	public void acknowledgeRegistration(int rStatus) {
		
		switch (rStatus){
		case 0 : 
			this.attr.setStringValue(this.rfidTagNumber);
			try {
				this.ctxBroker.update(me, attr);
				
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(logging.isDebugEnabled()) logging.debug("Error updating "+attr.getType()+" attribute ");	
			}
			this.tfTagNumber.setText(rfidTagNumber);
			JOptionPane.showMessageDialog(this, "Successfully registered tag: "+this.rfidTagNumber);
			break;
		case 1 :
			//JOptionPane.showMessageDialog(this, "Error registering tag. Password incorrect");
			String newPasswd = (String) JOptionPane.showInputDialog(this, "The password for registering your RFID tag number was incorrect. Please enter your password again.","RFID Tag Registration unsuccessful", JOptionPane.ERROR_MESSAGE, null, null, new String());
			this.sendRegisterMessage(newPasswd);
			break;
		case 2 :
			JOptionPane.showMessageDialog(this, "Error registering tag. Tag number not recognised");
			this.rfidTagNumber = (String) JOptionPane.showInputDialog(this, "The rfid tag number was not recognised. Please enter a valid rfid tag number below. ", "RFID Tag Registration unsuccessful", JOptionPane.ERROR_MESSAGE, null, null, new String());
			
			String password = (String) JOptionPane.showInputDialog(this, "Please enter the password", "RFID Tag registration password needed", JOptionPane.ERROR_MESSAGE, null, null, new String());
			this.sendRegisterMessage(password);
			break;
		default: JOptionPane.showMessageDialog(this, "Error registering tag. Unknown error!");
			break;
		
		}
	}


	/**
	 * @return the rfidServer
	 */
	public IRfidServer getRfidServer() {
		return rfidServer;
	}

	/**
	 * @param rfidServer the rfidServer to set
	 */
	public void setRfidServer(IRfidServer rfidServer) {
		this.rfidServer = rfidServer;
	}
} 
