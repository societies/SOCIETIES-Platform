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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.PersonalisationGUI.impl.preferences.GUI;
/**
 * @author  Administrator
 * @created July 1, 2010
 */
public class PreferenceSelectionDialog extends JDialog implements ActionListener
{
	static PreferenceSelectionDialog thePreferenceSelectionDialog;

	JPanel pnPanel0;

	JPanel pnPanel1;

	JPanel pnPanel2;

	JPanel pnPanel4;
	JLabel lbLabel0;
	JTextField serviceTypeTextField;

	JPanel pnPanel5;
	JButton btGenericContinue;

	JPanel pnPanel3;

	JPanel pnPanel7;
	JButton btSpecificContinue;

	JPanel pnPanel8;
	JLabel lbLabel1;
	JComboBox serviceIDcombo;
	private PreferenceDetails detail;

	private  GUI masterGUI;

	private IUserPreferenceManagement prefMgr;
	
	private IServiceDiscovery serviceDiscovery;
	
	private Hashtable<String,ServiceResourceIdentifier> serviceIDsForCombo = new Hashtable<String,ServiceResourceIdentifier>();


	private List<Service> localServices;
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
		//thePreferenceSelectionDialog = new PreferenceSelectionDialog();
	} 


	public PreferenceSelectionDialog(GUI masterGUI){
		super();
		this.localServices = new ArrayList<Service>();
		serviceIDsForCombo = new Hashtable<String, ServiceResourceIdentifier>();
		this.setModal(true);
		this.setTitle("Preference name input");
		this.masterGUI = masterGUI;
		this.serviceDiscovery = this.masterGUI.getServiceDiscovery();
		this.prefMgr = masterGUI.getPrefMgr();
		this.showGUI();
	}

	private void setLocationAndSize(){
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    int maxX = screenSize.width ;
	    int maxY = screenSize.height ;
	    this.setLocation((maxX/3), (maxY/3));
		//this.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
		this.setSize(400, 300);
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
		GridBagLayout gbPanel1 = new GridBagLayout();
		GridBagConstraints gbcPanel1 = new GridBagConstraints();
		pnPanel1.setLayout( gbPanel1 );

		pnPanel2 = new JPanel();
		pnPanel2.setBorder( BorderFactory.createTitledBorder( "Generic Preference" ) );
		GridBagLayout gbPanel2 = new GridBagLayout();
		GridBagConstraints gbcPanel2 = new GridBagConstraints();
		pnPanel2.setLayout( gbPanel2 );

		pnPanel4 = new JPanel();
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );

		lbLabel0 = new JLabel( "Service Type:"  );
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 1;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 2,2,2,2 );
		gbPanel4.setConstraints( lbLabel0, gbcPanel4 );
		pnPanel4.add( lbLabel0 );

		serviceTypeTextField = new JTextField( );
		gbcPanel4.gridx = 1;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 2,4,2,2 );
		gbPanel4.setConstraints( serviceTypeTextField, gbcPanel4 );
		pnPanel4.add( serviceTypeTextField );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 0;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( pnPanel4, gbcPanel2 );
		pnPanel2.add( pnPanel4 );

		pnPanel5 = new JPanel();
		GridBagLayout gbPanel5 = new GridBagLayout();
		GridBagConstraints gbcPanel5 = new GridBagConstraints();
		pnPanel5.setLayout( gbPanel5 );

		btGenericContinue = new JButton( "Continue"  );
		btGenericContinue.addActionListener(this);
		gbcPanel5.gridx = 0;
		gbcPanel5.gridy = 0;
		gbcPanel5.gridwidth = 1;
		gbcPanel5.gridheight = 1;
		gbcPanel5.fill = GridBagConstraints.VERTICAL;
		gbcPanel5.weightx = 1;
		gbcPanel5.weighty = 0;
		gbcPanel5.anchor = GridBagConstraints.NORTHEAST;
		gbPanel5.setConstraints( btGenericContinue, gbcPanel5 );
		pnPanel5.add( btGenericContinue );
		gbcPanel2.gridx = 0;
		gbcPanel2.gridy = 1;
		gbcPanel2.gridwidth = 1;
		gbcPanel2.gridheight = 1;
		gbcPanel2.fill = GridBagConstraints.BOTH;
		gbcPanel2.weightx = 1;
		gbcPanel2.weighty = 0;
		gbcPanel2.anchor = GridBagConstraints.NORTH;
		gbPanel2.setConstraints( pnPanel5, gbcPanel2 );
		pnPanel2.add( pnPanel5 );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 0;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.HORIZONTAL;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 1;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 0,4,20,4 );
		gbPanel1.setConstraints( pnPanel2, gbcPanel1 );
		pnPanel1.add( pnPanel2 );

		pnPanel3 = new JPanel();
		pnPanel3.setBorder( BorderFactory.createTitledBorder( "Installed services" ) );
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		pnPanel7 = new JPanel();
		GridBagLayout gbPanel7 = new GridBagLayout();
		GridBagConstraints gbcPanel7 = new GridBagConstraints();
		pnPanel7.setLayout( gbPanel7 );

		btSpecificContinue = new JButton( "Continue"  );
		btSpecificContinue.addActionListener(this);
		gbcPanel7.gridx = 0;
		gbcPanel7.gridy = 0;
		gbcPanel7.gridwidth = 1;
		gbcPanel7.gridheight = 1;
		gbcPanel7.fill = GridBagConstraints.VERTICAL;
		gbcPanel7.weightx = 1;
		gbcPanel7.weighty = 0;
		gbcPanel7.anchor = GridBagConstraints.NORTHEAST;
		gbPanel7.setConstraints( btSpecificContinue, gbcPanel7 );
		pnPanel7.add( btSpecificContinue );
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 1;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbPanel3.setConstraints( pnPanel7, gbcPanel3 );
		pnPanel3.add( pnPanel7 );

		pnPanel8 = new JPanel();
		GridBagLayout gbPanel8 = new GridBagLayout();
		GridBagConstraints gbcPanel8 = new GridBagConstraints();
		pnPanel8.setLayout( gbPanel8 );

		lbLabel1 = new JLabel( "Select a service:"  );
		gbcPanel8.gridx = 0;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 1;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbcPanel8.insets = new Insets( 2,2,2,2 );
		gbPanel8.setConstraints( lbLabel1, gbcPanel8 );
		pnPanel8.add( lbLabel1 );


		serviceIDcombo = new JComboBox();
		this.addAllServices();
		gbcPanel8.gridx = 1;
		gbcPanel8.gridy = 0;
		gbcPanel8.gridwidth = 1;
		gbcPanel8.gridheight = 1;
		gbcPanel8.fill = GridBagConstraints.BOTH;
		gbcPanel8.weightx = 1;
		gbcPanel8.weighty = 0;
		gbcPanel8.anchor = GridBagConstraints.NORTH;
		gbcPanel8.insets = new Insets( 2,4,2,2 );
		gbPanel8.setConstraints( serviceIDcombo, gbcPanel8 );
		pnPanel8.add( serviceIDcombo );
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbPanel3.setConstraints( pnPanel8, gbcPanel3 );
		pnPanel3.add( pnPanel8 );
		gbcPanel1.gridx = 0;
		gbcPanel1.gridy = 1;
		gbcPanel1.gridwidth = 1;
		gbcPanel1.gridheight = 1;
		gbcPanel1.fill = GridBagConstraints.BOTH;
		gbcPanel1.weightx = 1;
		gbcPanel1.weighty = 0;
		gbcPanel1.anchor = GridBagConstraints.NORTH;
		gbcPanel1.insets = new Insets( 0,4,10,4 );
		gbPanel1.setConstraints( pnPanel3, gbcPanel1 );
		pnPanel1.add( pnPanel3 );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( pnPanel1, gbcPanel0 );
		pnPanel0.add( pnPanel1 );

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );

		
		pack();
		this.setLocationAndSize();
		setVisible( true );
	} 



	public PreferenceDetails getPreferenceDetail(){
		return this.detail;
	}

	private void addAllServices(){
		
		try {
			Future<List<Service>> flocalServices = this.serviceDiscovery.getLocalServices();
			localServices = flocalServices.get();
			
			for (Service service : localServices){
				this.serviceIDsForCombo.put(service.getServiceName(), service.getServiceIdentifier());
				this.serviceIDcombo.addItem(service.getServiceName());
			}
			if (localServices.size()==0){
				this.btSpecificContinue.setEnabled(false);
				this.btGenericContinue.setEnabled(true);
			}
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
/*		try {
			services = discovery.findAllServices();
			if (services==null){
				JOptionPane.showMessageDialog(this, "Service Discovery returned 0 services");
			}else{
				//JOptionPane.showMessageDialog(null, "ServiceDiscovery found :"+services.size()+" services ");
				for (PssService service : services){
					//if (!service.isFrameworkService()){
						this.discoveredServices.add(service);
						String decorativeName = "NoNameProvided";
						if (service.getServiceName()!=null){
							decorativeName = service.getServiceName();
						}
						decorativeName = decorativeName.concat(service.getServiceId().toUriString());
						this.serviceIDsForCombo.put(decorativeName, service.getServiceId());
						this.serviceIDcombo.addItem(decorativeName);
					//}
				}	
				if (this.discoveredServices.size()==0){
					this.btSpecificContinue.setEnabled(false);
				}
			}
		} catch (ServiceMgmtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/
	}

	private String getServiceName(ServiceResourceIdentifier serviceID) {
		for (Service service: localServices){
			if (service.getServiceIdentifier().equals(serviceID)){
				return service.getServiceName();
			}
		}
		
		return "Service has no name";
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btGenericContinue)){
			
			if (this.serviceTypeTextField.getText().equals("")){
				JOptionPane.showMessageDialog(this, "Please enter a service type first");
			}else{
				String s = "";
				boolean initialQ = true;
				while (s==null || s.equals("")){
					if (!initialQ){
						JOptionPane.showMessageDialog(this, "Please enter a preference name");
					}
					s = (String)JOptionPane.showInputDialog(
							this,
							"Preference",
							"Enter a Preference Name",
							JOptionPane.QUESTION_MESSAGE,
							null,
							null,
					"");
					initialQ=false;

					if ((s==null)||(s.equals(""))){
						System.out.println("preference name is empty");
					}
				}
				this.detail = new PreferenceDetails(s);
				this.detail.setServiceType(this.serviceTypeTextField.getText());
				this.detail.setPreferenceName(s);
				this.dispose();
			}
		}else if (e.getSource().equals(this.btSpecificContinue)){
			String s = "";
			boolean initialQ = true;
			while (s==""){
				if (!initialQ){
					JOptionPane.showMessageDialog(this, "Please enter a preference name");
				}
				s = (String)JOptionPane.showInputDialog(
						this,
						"Preference",
						"Type a Preference Name",
						JOptionPane.QUESTION_MESSAGE,
						null,
						null,
						"");
				initialQ=false;
			}
			String decorativeName = this.serviceIDcombo.getSelectedItem().toString();
			
			for (Service service: localServices){
				if (service.getServiceName().equalsIgnoreCase(decorativeName)){
					this.detail = new PreferenceDetails(this.serviceTypeTextField.getText(), service.getServiceIdentifier(), s);
				}
			}
			
			
			this.dispose();
		}
	}

	public PreferenceDetails getDetails(){
		return this.detail;
	}
} 
