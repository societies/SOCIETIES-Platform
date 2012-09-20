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
package org.societies.css.devicemgmt.controller.gui;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.societies.css.devicemgmt.controller.model.PressureMat;
/**
 * @author  Administrator
 * @created September 19, 2012
 */
public class PressureMatMonitorGUI extends JFrame 
{
	static PressureMatMonitorGUI thePressureMatMonitorGUI;

	JPanel pnPanel0;

	JPanel pnPanel1;
	JLabel lbLabel0;
	JTextField tfText0;

	JPanel pnPanel2;
	JLabel lbLabel1;
	JTextField tfText1;

	JPanel pnPanel3;
	JLabel lbLabel2;
	JTextField tfText2;

	JPanel pnPanel4;
	JLabel lbLabel3;
	JTextField tfText3;

	JPanel pnPanel5;
	JLabel lbLabel4;
	JTextField tfText4;

	JPanel pnPanel6;
	JLabel lbLabel5;
	JTextField tfText5;

	JPanel pnPanel7;
	JLabel lbLabel6;
	JTextField tfText6;

	JPanel pnPanel8;
	JLabel lbLabel7;
	JTextField tfText7;

	private String controllerID;
	
	private Hashtable<JLabel, JTextField> labels;

	private List<PressureMat> mats;
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
		thePressureMatMonitorGUI = new PressureMatMonitorGUI("", new ArrayList<PressureMat>());
	} 

	/**
	 */
	public PressureMatMonitorGUI(String controllerID, List<PressureMat> mats) 
	{
		super( "Pressure Mat Monitoring Window Controller ID : " +controllerID);
		this.controllerID = controllerID;
		this.mats = mats;
		this.labels = new Hashtable<JLabel, JTextField>();
		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		tfText0 = new JTextField( );
		if (mats.size()>0){
			lbLabel0 = new JLabel(mats.get(0).getPressureMatId());
			this.labels.put(lbLabel0, tfText0);
		}else{
			lbLabel0 = new JLabel( ""  );
		}
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbLabel0, gbcPanel1 );
		pnPanel1.add( lbLabel0 );

		tfText0 = new JTextField( );
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( tfText0, gbcPanel1 );
		pnPanel1.add( tfText0 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 3;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		
		pnPanel2 = new JPanel();
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		pnPanel2.setLayout( gbPanel2 );
		
		tfText1 = new JTextField( );
		if (mats.size()>1){
			lbLabel1 = new JLabel(mats.get(1).getPressureMatId());
			this.labels.put(lbLabel1, tfText1);
		}else{
			lbLabel1 = new JLabel( ""  );
			
		}
		
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 1;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( lbLabel1, gbcPanel2 );
		pnPanel2.add( lbLabel1 );

		
		gbcPanel2.gridx = 1;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( tfText1, gbcPanel2 );
		pnPanel2.add( tfText1 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel2, gbcPanel0 );
		pnPanel0.add( pnPanel2 );

		pnPanel3 = new JPanel();
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		
		tfText2 = new JTextField( );
		if (mats.size()>2){
			lbLabel2 = new JLabel(mats.get(2).getPressureMatId());
			this.labels.put(lbLabel2, tfText2);
		}
		else{
		lbLabel2 = new JLabel( ""  );
		}
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 1;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbPanel3.setConstraints( lbLabel2, gbcPanel3 );
		pnPanel3.add( lbLabel2 );

		
		gbcPanel3.gridx = 1;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbPanel3.setConstraints( tfText2, gbcPanel3 );
		pnPanel3.add( tfText2 );
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

		pnPanel4 = new JPanel();
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );

		tfText3 = new JTextField( );
		if (mats.size()>3){
			lbLabel3 = new JLabel(mats.get(3).getPressureMatId());
			this.labels.put(lbLabel3, tfText3);
		}else{
			lbLabel3 = new JLabel("");
		}
		
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 1;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbPanel4.setConstraints( lbLabel3, gbcPanel4 );
		pnPanel4.add( lbLabel3 );

		gbcPanel4.gridx = 1;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbPanel4.setConstraints( tfText3, gbcPanel4 );
		pnPanel4.add( tfText3 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 3;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel4, gbcPanel0 );
		pnPanel0.add( pnPanel4 );

		pnPanel5 = new JPanel();
		GridBagLayout gbPanel5 = new GridBagLayout();
		GridBagConstraints gbcPanel5 = new GridBagConstraints();
		pnPanel5.setLayout( gbPanel5 );

		tfText4 = new JTextField( );
		if (mats.size()>4){
			lbLabel4 = new JLabel(mats.get(4).getPressureMatId());
			this.labels.put(lbLabel4, tfText4);
		}else {
		lbLabel4 = new JLabel( ""  );
		}
		gbcPanel5.gridx = 0;
		gbcPanel5.gridy = 0;
		gbcPanel5.gridwidth = 1;
		gbcPanel5.gridheight = 1;
		gbcPanel5.fill = GridBagConstraints.BOTH;
		gbcPanel5.weightx = 1;
		gbcPanel5.weighty = 1;
		gbcPanel5.anchor = GridBagConstraints.NORTH;
		gbPanel5.setConstraints( lbLabel4, gbcPanel5 );
		pnPanel5.add( lbLabel4 );

		gbcPanel5.gridx = 1;
		gbcPanel5.gridy = 0;
		gbcPanel5.gridwidth = 1;
		gbcPanel5.gridheight = 1;
		gbcPanel5.fill = GridBagConstraints.BOTH;
		gbcPanel5.weightx = 1;
		gbcPanel5.weighty = 0;
		gbcPanel5.anchor = GridBagConstraints.NORTH;
		gbPanel5.setConstraints( tfText4, gbcPanel5 );
		pnPanel5.add( tfText4 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel5, gbcPanel0 );
		pnPanel0.add( pnPanel5 );

		pnPanel6 = new JPanel();
		GridBagLayout gbPanel6 = new GridBagLayout();
		GridBagConstraints gbcPanel6 = new GridBagConstraints();
		pnPanel6.setLayout( gbPanel6 );

		tfText5 = new JTextField( );
		if (mats.size()>5){
			lbLabel5 = new JLabel(mats.get(5).getPressureMatId());
			this.labels.put(lbLabel5, tfText5);
		}else{
			lbLabel5 = new JLabel( ""  );
		}
		
		gbcPanel6.gridx = 0;
		gbcPanel6.gridy = 0;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 1;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbPanel6.setConstraints( lbLabel5, gbcPanel6 );
		pnPanel6.add( lbLabel5 );

		gbcPanel6.gridx = 1;
		gbcPanel6.gridy = 0;
		gbcPanel6.gridwidth = 1;
		gbcPanel6.gridheight = 1;
		gbcPanel6.fill = GridBagConstraints.BOTH;
		gbcPanel6.weightx = 1;
		gbcPanel6.weighty = 0;
		gbcPanel6.anchor = GridBagConstraints.NORTH;
		gbPanel6.setConstraints( tfText5, gbcPanel6 );
		pnPanel6.add( tfText5 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 5;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 2;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel6, gbcPanel0 );
		pnPanel0.add( pnPanel6 );

		pnPanel7 = new JPanel();
		GridBagLayout gbPanel7 = new GridBagLayout();
		GridBagConstraints gbcPanel7 = new GridBagConstraints();
		pnPanel7.setLayout( gbPanel7 );

		tfText6 = new JTextField( );
		if (mats.size()>6){
			lbLabel6 = new JLabel(mats.get(6).getPressureMatId());
			this.labels.put(lbLabel6, tfText6);
		}else{
			lbLabel6 = new JLabel( ""  );
		}
	
		gbcPanel7.gridx = 0;
		gbcPanel7.gridy = 0;
		gbcPanel7.gridwidth = 1;
		gbcPanel7.gridheight = 1;
		gbcPanel7.fill = GridBagConstraints.BOTH;
		gbcPanel7.weightx = 1;
		gbcPanel7.weighty = 1;
		gbcPanel7.anchor = GridBagConstraints.NORTH;
		gbPanel7.setConstraints( lbLabel6, gbcPanel7 );
		pnPanel7.add( lbLabel6 );

		gbcPanel7.gridx = 1;
		gbcPanel7.gridy = 0;
		gbcPanel7.gridwidth = 1;
		gbcPanel7.gridheight = 1;
		gbcPanel7.fill = GridBagConstraints.BOTH;
		gbcPanel7.weightx = 1;
		gbcPanel7.weighty = 0;
		gbcPanel7.anchor = GridBagConstraints.NORTH;
		gbPanel7.setConstraints( tfText6, gbcPanel7 );
		pnPanel7.add( tfText6 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 7;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel7, gbcPanel0 );
		pnPanel0.add( pnPanel7 );

		pnPanel8 = new JPanel();
		GridBagLayout gbPanel8 = new GridBagLayout();
		GridBagConstraints gbcPanel8 = new GridBagConstraints();
		pnPanel8.setLayout( gbPanel8 );

		tfText7 = new JTextField( );
		if (mats.size()>7){
			lbLabel7 = new JLabel(mats.get(7).getPressureMatId());
			this.labels.put(lbLabel7, tfText7);
		}else{
			lbLabel7 = new JLabel( ""  );
		}
		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( lbLabel7, gbcPanel8 );
		pnPanel8.add( lbLabel7 );

		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbPanel8.setConstraints( tfText7, gbcPanel8 );
		pnPanel8.add( tfText7 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 8;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel8, gbcPanel0 );
		pnPanel0.add( pnPanel8 );

		setDefaultCloseOperation( EXIT_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
	} 
	
	
	public void updatePressureMatInfo(PressureMat mat){
		Enumeration<JLabel> keys = this.labels.keys();
		while(keys.hasMoreElements()){
			JLabel lbl = keys.nextElement();
			if (lbl.getText().trim().equalsIgnoreCase(mat.getPressureMatId().trim())){
				this.labels.get(lbl).setText(Boolean.toString(mat.isEnabled()));
			}
		}
	}
	
	
	public void updatePressureMatInfo(String resourceId, String value){
		Enumeration<JLabel> keys = this.labels.keys();
		while(keys.hasMoreElements()){
			JLabel lbl = keys.nextElement();
			if (lbl.getText().trim().equalsIgnoreCase(resourceId.trim())){
				this.labels.get(lbl).setText(value);
			}
		}
	}

	/**
	 * @return the controllerID
	 */
	public String getControllerID() {
		return controllerID;
	}

} 
