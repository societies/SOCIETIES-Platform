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
package org.societies.personalisation.PersonalisationGUI.impl.preferences;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;

import org.osgi.framework.BundleContext;
import org.personalsmartspace.pm.prefGUI.impl.personalisation.DPISelectionDialog;
import org.personalsmartspace.pm.prefGUI.impl.personalisation.PreferenceSelectionGUI;
import org.personalsmartspace.pm.prefGUI.impl.personalisation.UserPreferenceGUI;
import org.personalsmartspace.sre.api.pss3p.IDigitalPersonalIdentifier;
/**
 * @author  Administrator
 * @created April 29, 2010
 */
public class GuiSelection extends JFrame implements ActionListener
{
	static GuiSelection theGuiSelection;

	JPanel pnPanel0;
	JPanel pnPanel1;
	JButton btPersonalisation;
	JButton btPrivacy;
	PreferenceSelectionGUI prefSelectionGUI = null;
	Hashtable<IDigitalPersonalIdentifier, PreferenceSelectionGUI> guis;

	private final initiatePrefGUI masterGUI;
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
		theGuiSelection = new GuiSelection(null);
	} 

	/**
	 */
	public GuiSelection(initiatePrefGUI masterGUI) 
	{
		super( "Select a GUI" );
		this.masterGUI = masterGUI;
		this.guis = new Hashtable<IDigitalPersonalIdentifier, PreferenceSelectionGUI>();
		
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int maxX = screenSize.width ;
        int maxY = screenSize.height ;
        this.setLocation((maxX/3), (maxY/3));
		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel1 = new JPanel();
		pnPanel1.setBorder( BorderFactory.createTitledBorder( "Select GUI" ) );
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		btPersonalisation = new JButton( "Start Personalisation GUI"  );
		btPersonalisation.addActionListener(this);
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 50,50,0,50 );
		gbPanel1.setConstraints( btPersonalisation, gbcPanel1 );
		pnPanel1.add( btPersonalisation );

		btPrivacy = new JButton( "Start Privacy GUI"  );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 50,50,50,50 );
		gbPanel1.setConstraints( btPrivacy, gbcPanel1 );
		pnPanel1.add( btPrivacy );
		gbcPanel0.gridx = 5;
		gbcPanel0.gridy = 5;
		gbcPanel0.gridwidth = 11;
		gbcPanel0.gridheight = 9;
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
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btPersonalisation)){
			IdentitySelectionDialog dpiDialog = new IdentitySelectionDialog(this, masterGUI);
			if (dpiDialog.isCancelled()){
				System.out.println("dpiDialog cancelled");
				return;
			}else{	
				IDigitalPersonalIdentifier dpi = dpiDialog.getIdentity();
				if (this.guis.containsKey(dpi)){
					if (this.guis.get(dpi).getIsClosed()){
						this.guis.get(dpi).setVisible(true);
					}
				}else{
					PreferenceSelectionGUI gui = new PreferenceSelectionGUI(this.masterGUI, dpi);
					this.guis.put(dpi, gui);
				}
			}
			
		}
		
	} 
} 
