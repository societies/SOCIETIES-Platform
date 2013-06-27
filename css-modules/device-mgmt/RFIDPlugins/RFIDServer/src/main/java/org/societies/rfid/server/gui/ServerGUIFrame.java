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
package org.societies.rfid.server.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.rfid.server.RfidServer;



public class ServerGUIFrame extends JFrame implements ActionListener
{
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	static ServerGUIFrame theServerGUIFrame;

	DataMonitorTable activeDataTable;
	JPanel pnPanel0;

	JPanel pnPanel1;

	JPanel pnPanel3;
	JLabel lbLabel0;

	JPanel pnPanel4;
	JTable jtableActivityMonitor;
	
	JPanel pnPanel5;
	JTextField tfTagNumber;
	JButton btGenPWD;

	private final RfidServer rfidServer;

	private JTable jtableRegisteredTags;

	private TagRegTable tagRegTable;
	/**
	 */
	public static void main( String args[] ) 
	{
/*		try 
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
		}*/
		theServerGUIFrame = new ServerGUIFrame(null);
	} 

	/**
	 */
	public ServerGUIFrame(RfidServer parent) 
	{
		super( "RFID Monitoring GUI" );
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//UIManager.put("ClassLoader", getClass().getClassLoader());
		this.logging.debug("Starting server GUI frame");
		rfidServer = parent;

		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		/*
		 * panel 3 section start
		 */
		pnPanel3 = new JPanel();
		pnPanel3.setBorder( BorderFactory.createTitledBorder( "Registered tags" ) );
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		this.tagRegTable = new TagRegTable();
		jtableRegisteredTags = new JTable( this.tagRegTable );
		jtableRegisteredTags.setPreferredScrollableViewportSize(new Dimension(600,200));
		JScrollPane scpTable1 = new JScrollPane(jtableRegisteredTags);
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 1;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbcPanel3.insets = new Insets( 5,0,25,0 );
		gbPanel3.setConstraints( scpTable1, gbcPanel3 );
		pnPanel3.add( scpTable1 );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 5,0,10,0 );
		gbPanel1.setConstraints( pnPanel3, gbcPanel1 );
		pnPanel1.add( pnPanel3 );

		
		/*
		 * panel 3 section end
		 */
		pnPanel4 = new JPanel();
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );


		activeDataTable = new DataMonitorTable();
		jtableActivityMonitor = new JTable( activeDataTable );
		jtableActivityMonitor.setPreferredScrollableViewportSize(new Dimension(600,200));
		jtableActivityMonitor.getColumnModel().getColumn(0).setPreferredWidth(175);
		jtableActivityMonitor.getColumnModel().getColumn(1).setPreferredWidth(20);
		jtableActivityMonitor.setFillsViewportHeight(true);
		JScrollPane scpTable = new JScrollPane( jtableActivityMonitor );
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 1;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbPanel4.setConstraints( scpTable, gbcPanel4 );
		pnPanel4.add( scpTable );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( pnPanel4, gbcPanel1 );
		pnPanel1.add( pnPanel4 );
		
		
		/*
		 * panel5 start
		 */
		
		   pnPanel5 = new JPanel();
		   pnPanel5.setBorder( BorderFactory.createTitledBorder( "Register new tag" ) );
		   GridBagLayout gbPanel5 = new GridBagLayout();
		   GridBagConstraints gbcPanel5 = new GridBagConstraints();
		   pnPanel5.setLayout( gbPanel5 );

		   tfTagNumber = new JTextField( );
		   gbcPanel5.gridx = 0;
		   gbcPanel5.gridy = 0;
		   gbcPanel5.gridwidth = 1;
		   gbcPanel5.gridheight = 1;
		   gbcPanel5.fill = GridBagConstraints.BOTH;
		   gbcPanel5.weightx = 1;
		   gbcPanel5.weighty = 0;
		   gbcPanel5.anchor = GridBagConstraints.NORTH;
		   gbcPanel5.insets = new Insets( 5,5,5,10 );
		   gbPanel5.setConstraints( tfTagNumber, gbcPanel5 );
		   pnPanel5.add( tfTagNumber );

		   btGenPWD = new JButton( "Generate Password"  );
		   btGenPWD.addActionListener(this);
		   gbcPanel5.gridx = 1;
		   gbcPanel5.gridy = 0;
		   gbcPanel5.gridwidth = 1;
		   gbcPanel5.gridheight = 1;
		   gbcPanel5.fill = GridBagConstraints.BOTH;
		   gbcPanel5.weightx = 1;
		   gbcPanel5.weighty = 0;
		   gbcPanel5.anchor = GridBagConstraints.NORTH;
		   gbcPanel5.insets = new Insets( 5,10,5,5 );
		   gbPanel5.setConstraints( btGenPWD, gbcPanel5 );
		   pnPanel5.add( btGenPWD );
		   gbcPanel1.gridx = 0;
		   gbcPanel1.gridy = 2;
		   gbcPanel1.gridwidth = 1;
		   gbcPanel1.gridheight = 1;
		   gbcPanel1.fill = GridBagConstraints.BOTH;
		   gbcPanel1.weightx = 1;
		   gbcPanel1.weighty = 0;
		   gbcPanel1.anchor = GridBagConstraints.NORTH;
		   gbPanel1.setConstraints( pnPanel5, gbcPanel1 );
		   pnPanel1.add( pnPanel5 );
		   
		   /*
		    * panel5 stop
		    */
		
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 19;
		gbcPanel0.gridheight = 18;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
		this.logging.debug("initialised ServerGUIFrame");
	} 
	
	
	public void addRow(String dpiAsString, String tagNumber, String wUnit, String symLoc){
		Vector<String> row = new Vector<String>();
		row.add(dpiAsString);
		row.add(tagNumber);
		row.add(wUnit);
		row.add(symLoc);
		this.activeDataTable.addRow(row);
		
		if (!this.tagRegTable.containsTag(tagNumber)){
			Vector<String> row2 = new Vector<String>();
			row2.add(tagNumber);
			row2.add(dpiAsString);
			row2.add("");
			this.tagRegTable.addRow(row2);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btGenPWD)){
			String tag = this.tfTagNumber.getText();
			String password = this.rfidServer.getPassword();
			JOptionPane jPane = new JOptionPane();
			
			int n = jPane.showConfirmDialog(this, "This action will de-register any DPI already registered for tag: "+tag+".\nContinue?");
			if (n==JOptionPane.YES_OPTION){
				jPane.showMessageDialog(this, "Password for tag "+tag+" is "+password);
				this.rfidServer.storePassword(tag, password);
				this.tagRegTable.changePassword(tag, password);
				this.tagRegTable.changeDPI(tag, "");
			}
		}
		
	}
	
	public void setNewDPIRegistered(String tagNumber, String dpiAsString){
		this.tagRegTable.changeDPI(tagNumber, dpiAsString);
	}
	
	
} 

