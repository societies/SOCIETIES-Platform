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
package org.societies.css.devicemgmt.rfiddriver.impl;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * @author  Administrator
 * @created February 2, 2011
 */
public class SocketServer extends JFrame implements ActionListener
{
	static SocketServer theSocketServer;

	JPanel pnPanel0;

	JPanel pnPanel1;

	JPanel pnPanel2;
	JLabel lbLabel0;
	JComboBox cmbTagCombo;
	JComboBox cmbWUnitCombo;
	JLabel lbLabel1;

	JPanel pnPanel3;
	JButton btBut0;

	private ServerSocket server;

	private Socket client;

	private BufferedReader in;

	private PrintWriter out;

	private String line;
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
		theSocketServer = new SocketServer();
		String tag = "0071";
		String wUnit = "0000";
		String input = "******"+tag+"*"+wUnit;
		System.out.println(input.substring(6,10));
		System.out.println(input.substring(11,15));
	} 

	/**
	 */
	public SocketServer() 
	{
		super( "Mock RFID Reader" );

		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		pnPanel1.setBorder( BorderFactory.createTitledBorder( "SocketServer" ) );
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		pnPanel2 = new JPanel();
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		pnPanel2.setLayout( gbPanel2 );

		lbLabel0 = new JLabel( "Tag:"  );
		gbcPanel2.gridx = 1;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 6;
		gbcPanel2.gridheight = 4;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets( 0,5,0,5 );
		gbPanel2.setConstraints( lbLabel0, gbcPanel2 );
		pnPanel2.add( lbLabel0 );

		String []dataTagCombo = { "0071", "0072", "0073" };
		cmbTagCombo = new JComboBox( dataTagCombo );
		gbcPanel2.gridx = 7;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 13;
		gbcPanel2.gridheight = 4;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets( 0,5,0,0 );
		gbPanel2.setConstraints( cmbTagCombo, gbcPanel2 );
		pnPanel2.add( cmbTagCombo );

		String []dataWUnitCombo = { "0000", "0010" };
		cmbWUnitCombo = new JComboBox( dataWUnitCombo );
		gbcPanel2.gridx = 13;
		gbcPanel2.gridy = 4;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets( 10,5,0,0 );
		gbPanel2.setConstraints( cmbWUnitCombo, gbcPanel2 );
		pnPanel2.add( cmbWUnitCombo );

		lbLabel1 = new JLabel( "Wakup Unit:"  );
		gbcPanel2.gridx = 2;
		gbcPanel2.gridy = 4;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets( 10,5,0,5 );
		gbPanel2.setConstraints( lbLabel1, gbcPanel2 );
		pnPanel2.add( lbLabel1 );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 20;
		gbcPanel1.gridheight = 5;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 5,3,0,3 );
		gbPanel1.setConstraints( pnPanel2, gbcPanel1 );
		pnPanel1.add( pnPanel2 );

		pnPanel3 = new JPanel();
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		btBut0 = new JButton( "SendUpdate"  );
		btBut0.addActionListener(this);
		gbcPanel3.gridx = 12;
		gbcPanel3.gridy = 3;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbcPanel3.insets = new Insets( 10,30,10,30 );
		gbPanel3.setConstraints( btBut0, gbcPanel3 );
		pnPanel3.add( btBut0 );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 5;
		gbcPanel1.gridwidth = 20;
		gbcPanel1.gridheight = 6;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( pnPanel3, gbcPanel1 );
		pnPanel1.add( pnPanel3 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 20;
		gbcPanel0.gridheight = 18;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		setDefaultCloseOperation( EXIT_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
		while (true){
			listenSocket();
		}
	}

	  public void listenSocket(){

		if (server!=null){
			if (server.isBound()){
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		    try{
		      server = new ServerSocket(10001); 
		    } catch (IOException e) {
		      System.out.println("Could not listen on port 10001");
		      
		      return;
		      //System.exit(-1);
		     
		    }

		    try{
		      client = server.accept();
		    } catch (IOException e) {
		      System.out.println("Accept failed: 10001");
		      try {
				server.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("Unable to close socket!");
				return;
			}
		    }

		    try{
		      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		      out = new PrintWriter(client.getOutputStream(), true);
		    } catch (IOException e) {
		      System.out.println("Accept failed: 10001");
		      //System.exit(-1);
		      try {
					server.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("Unable to close socket!");
					return;
				}
		    }
		 
		    boolean error = false;
			while(!error){
		      try{
		        line = in.readLine();
		        System.out.println("Somebody said: "+line);
		//Send data back to client
		        //out.println(line);
		       
		      } catch (IOException e) {
		        System.out.println("Read failed");
		        error = true;
		        try {
					server.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println("Unable to close socket!");
					return;
				}
		        //System.exit(-1);
		        //listenSocket();
		      }
		    }
		  }
		  

	  
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btBut0)){
			String tag = this.cmbTagCombo.getSelectedItem().toString();
			String wUnit = this.cmbWUnitCombo.getSelectedItem().toString();
			out.println("******"+tag+"*"+wUnit);
			System.out.println("Sending new line to client: "+"******"+tag+"*"+wUnit);
		}
		
	} 
	
	
} 
