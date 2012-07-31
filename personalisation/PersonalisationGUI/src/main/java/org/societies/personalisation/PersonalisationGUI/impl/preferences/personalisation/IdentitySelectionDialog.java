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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import org.societies.api.identity.IIdentity;
import org.societies.personalisation.PersonalisationGUI.impl.preferences.GUI;
/**
 * @author  Administrator
 * @created April 28, 2010
 */
public class IdentitySelectionDialog extends JDialog implements ActionListener, WindowListener
{
	static IdentitySelectionDialog thePreferenceSelectionDialog;

	JPanel pnPanel0;
	JLabel lbService;
	JComboBox cmbDPIs;
	JButton btContinue;
	private List<IIdentity> dpis;
	private boolean cancelled = false;
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
		
		thePreferenceSelectionDialog = new IdentitySelectionDialog( new JFrame(), null);
		JOptionPane.showMessageDialog(new JFrame(), thePreferenceSelectionDialog.getIdentity().toString());

	} 
	

	public IdentitySelectionDialog(JFrame frame, GUI masterGUI){
		super(frame, true);
		this.addWindowListener(this);
		this.dpis = new ArrayList<IIdentity>();
		try{
			Set<IIdentity> identitySet = masterGUI.getIdMgr().getPublicIdentities();
			
			for (IIdentity dpi: identitySet){
				this.dpis.add(dpi);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
			this.showGUI();
		
	}
	
	private void setLocationAndSize(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int maxX = screenSize.width ;
		int maxY = screenSize.height ;
		this.setLocation((maxX/3), (maxY/3));
		//this.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
		//this.getContentPane().setSize(400, 300);
	}
	
	public void showGUI () 
	{
	

		pnPanel0 = new JPanel();
		pnPanel0.setBorder( BorderFactory.createTitledBorder( "Choose Preference" ) );
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		lbService = new JLabel( "Select Service:"  );
		gbcPanel0.gridx = 2;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 1;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( lbService, gbcPanel0 );
		pnPanel0.add( lbService );

		

		cmbDPIs = new JComboBox();
		gbcPanel0.gridx = 5;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( cmbDPIs, gbcPanel0 );
		pnPanel0.add( cmbDPIs );

		
		btContinue = new JButton( "Continue"  );
		gbcPanel0.gridx = 5;
		gbcPanel0.gridy = 8;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( btContinue, gbcPanel0 );
		pnPanel0.add( btContinue );

		
		this.setData();
		this.btContinue.addActionListener(this);
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		this.setLocationAndSize();
		setVisible( true );
	} 
	
	public IIdentity getIdentity(){
		return (IIdentity) this.cmbDPIs.getSelectedItem();
	}
	
	public void setData(){
		this.cmbDPIs.setEditable(false);
		
		for (IIdentity dpi : dpis){
			this.cmbDPIs.addItem(dpi);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
		
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		this.cancelled = true;
	}


	@Override
	public void windowClosing(WindowEvent e) {
		this.cancelled = true;
		
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}





	public boolean isCancelled() {
		return cancelled;
	}
	
	
} 
