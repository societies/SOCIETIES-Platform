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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
/**
 * @author  Administrator
 * @created May 3, 2010
 */
public class OutcomeCreatorDialog extends JDialog implements ActionListener
{
	static OutcomeCreatorDialog theOutcomeCreatorDialog;

	JPanel pnPanel0;

	JPanel pnPanel1;
	JTextField tfActionValue;

	JPanel pnPanel2;
	JButton btOK;
	JButton btCancel;
	JLabel lbLabel0;
	JLabel lbLabel1;
	JTextField tfAction;

	private boolean result = false;
	private PreferenceDetails detail;
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
		PreferenceDetails detail = new PreferenceDetails();
		detail.setPreferenceName("volume");
		theOutcomeCreatorDialog = new OutcomeCreatorDialog(new JFrame(), detail);
	} 
	public OutcomeCreatorDialog(JFrame parent, PreferenceDetails detail) {
		super( parent, "Outcome Creator", true );
		this.detail = detail;
		this.showGUI();

	}

	
	private void setLocationAndSize(){
			    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			    int maxX = screenSize.width ;
			    int maxY = screenSize.height ;
			    this.setLocation((maxX/3), (maxY/3));
				//this.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
			    this.setSize(300, 200);
			}
	
	/**
	 */
	public void showGUI()
	{

		
		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		pnPanel1.setBorder( BorderFactory.createTitledBorder( "Add Outcome" ) );
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		tfActionValue = new JTextField( );
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( tfActionValue, gbcPanel1 );
		pnPanel1.add( tfActionValue );

		pnPanel2 = new JPanel();
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		pnPanel2.setLayout( gbPanel2 );

		btCancel = new JButton( "Cancel"  );
		btCancel.addActionListener(this);
		btCancel.setPreferredSize(new Dimension(40, 25));
		btCancel.setIcon( new ImageIcon( "" ) );
		gbcPanel2.gridx = 1;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets(40,2,1,30);
		
		gbPanel2.setConstraints( btCancel, gbcPanel2 );
		pnPanel2.add( btCancel );

		btOK = new JButton( "OK"  );
		btOK.addActionListener(this);
		btOK.setIcon( new ImageIcon( "" ) );
		btOK.setPreferredSize(new Dimension(40, 25));
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbcPanel2.insets = new Insets(40,30,1,2);
		gbPanel2.setConstraints( btOK, gbcPanel2 );
		pnPanel2.add( btOK );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 4;
		gbcPanel1.gridwidth = 2;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		
		gbPanel1.setConstraints( pnPanel2, gbcPanel1 );
		pnPanel1.add( pnPanel2 );

		lbLabel0 = new JLabel( "Action:"  );
		lbLabel0.setIcon( new ImageIcon( "" ) );
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

		lbLabel1 = new JLabel( "Value:"  );
		lbLabel1.setIcon( new ImageIcon( "" ) );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 2;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( lbLabel1, gbcPanel1 );
		pnPanel1.add( lbLabel1 );

		tfAction = new JTextField( );
		tfAction.setText(detail.getPreferenceName());
		tfAction.setEditable(false);
		gbcPanel1.gridx = 1;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbPanel1.setConstraints( tfAction, gbcPanel1 );
		pnPanel1.add( tfAction );
		gbcPanel0.gridx = 3;
		gbcPanel0.gridy = 6;
		gbcPanel0.gridwidth = 14;
		gbcPanel0.gridheight = 8;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 2,2,2,2 );
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		this.setLocationAndSize();
		setVisible( true );
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btOK)){
			
			if (this.tfAction.getText().isEmpty()){
				JOptionPane.showMessageDialog(this, "Please enter an action", "Missing Action", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (this.tfActionValue.getText().isEmpty()){
				JOptionPane.showMessageDialog(this, "Please enter a value", "Missing Value", JOptionPane.ERROR_MESSAGE);
				return;
			}
			this.result = true;
			this.dispose();
		}else{
			this.result = false;
			this.dispose();
		}
		
	} 
	
	
	public String getAction(){
		return this.tfAction.getText();
	}
	
	public String getActionValue(){
		return this.tfActionValue.getText();
	}
	
	public IPreferenceOutcome getOutcome(){
		IPreferenceOutcome outcome = new PreferenceOutcome(this.detail.getServiceID(), this.detail.getServiceType(), this.getAction(),this.getActionValue());
		return outcome;
	}
	public boolean getResponse(){
		return this.result;
	}
} 
