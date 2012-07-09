package org.personalsmartspace.spm.servicepolicygui.impl;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.personalsmartspace.spm.identity.api.platform.DigitalPersonalIdentifier;
import org.personalsmartspace.spm.identity.api.platform.IIdentityManagement;
import org.personalsmartspace.spm.identity.api.platform.MalformedDigitialPersonalIdentifierException;
import org.personalsmartspace.spm.negotiation.api.platform.RequestItem;
import org.personalsmartspace.spm.negotiation.api.platform.RequestPolicy;
import org.personalsmartspace.spm.policy.api.platform.IPolicyManager;
import org.personalsmartspace.spm.preference.api.platform.Action;
import org.personalsmartspace.spm.preference.api.platform.Condition;
import org.personalsmartspace.spm.preference.api.platform.Subject;
import org.personalsmartspace.spm.preference.api.platform.constants.ActionConstants;
import org.personalsmartspace.spm.preference.api.platform.constants.ConditionConstants;
import org.personalsmartspace.sre.api.pss3p.IDigitalPersonalIdentifier;
import org.personalsmartspace.sre.api.pss3p.IServiceIdentifier;
import org.personalsmartspace.sre.api.pss3p.PssServiceIdentifier;
/**
 * @author  Administrator
 * @created January 14, 2010
 */
public class RequestPolicyGUI extends JPanel implements ActionListener, WindowListener
{	
	IIdentityManagement IDM;
	IPolicyManager policyMgr;
	RequestPolicy policy;
	ArrayList<RequestItem> requestItems;
	ResourcesTableModel model ;

	JButton saveBtn;

	JPanel resourcePanel;
	JTable resourceTable;

	JPanel resourceButtonsPanel;
	JButton addResourceBtn;
	JButton removeResourceBtn;

	JPanel subjectPanel;
	JLabel serviceIDLabel;
	JLabel dpiLabel;
	JTextField serviceIDTxtField;
	JTextField dpiTxtField;
	RequestItemEditor reqEditor;
	BundleContext context;

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
		//RequestPolicyGUI theRequestPolicyGUI = new RequestPolicyGUI();
	} 

	/**
	 */
	public RequestPolicyGUI(BundleContext bc) 
	{


		super();

		this.context = bc;
		this.requestItems = new ArrayList<RequestItem>();

		this.setBorder( BorderFactory.createTitledBorder( "mailto:EPapadopoulou@users.sourceforge.net" ) );
		GridBagLayout gbBackPanel = new GridBagLayout();
		GridBagConstraints gbcBackPanel = new GridBagConstraints();
		this.setLayout( gbBackPanel );

		saveBtn = new JButton( "Save"  );
		saveBtn.setActionCommand("savePolicy");
		saveBtn.addActionListener(this);
		gbcBackPanel.gridx = 2;
		gbcBackPanel.gridy = 3;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbBackPanel.setConstraints( saveBtn, gbcBackPanel );
		this.add( saveBtn );

		resourcePanel = new JPanel();
		GridBagLayout gbResourcePanel = new GridBagLayout();
		GridBagConstraints gbcResourcePanel = new GridBagConstraints();
		resourcePanel.setLayout( gbResourcePanel );

		this.model = new ResourcesTableModel();
		
		resourceTable = new JTable( model );
		JScrollPane scpResourceTable = new JScrollPane( resourceTable );
		gbcResourcePanel.gridx = 0;
		gbcResourcePanel.gridy = 0;
		gbcResourcePanel.gridwidth = 1;
		gbcResourcePanel.gridheight = 1;
		gbcResourcePanel.fill = GridBagConstraints.BOTH;
		gbcResourcePanel.weightx = 1;
		gbcResourcePanel.weighty = 1;
		gbcResourcePanel.anchor = GridBagConstraints.NORTH;
		gbResourcePanel.setConstraints( scpResourceTable, gbcResourcePanel );
		resourcePanel.add( scpResourceTable );


		JScrollPane scpResourcePanel = new JScrollPane( resourcePanel );
		gbcBackPanel.gridx = 0;
		gbcBackPanel.gridy = 1;
		gbcBackPanel.gridwidth = 3;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbBackPanel.setConstraints( scpResourcePanel, gbcBackPanel );
		this.add( scpResourcePanel );

		resourceButtonsPanel = new JPanel();
		GridBagLayout gbResourceButtonsPanel = new GridBagLayout();
		GridBagConstraints gbcResourceButtonsPanel = new GridBagConstraints();
		resourceButtonsPanel.setLayout( gbResourceButtonsPanel );

		addResourceBtn = new JButton( "Add New Resource"  );
		addResourceBtn.setActionCommand("addResource");
		addResourceBtn.addActionListener(this);
		gbcResourceButtonsPanel.gridx = 0;
		gbcResourceButtonsPanel.gridy = 0;
		gbcResourceButtonsPanel.gridwidth = 1;
		gbcResourceButtonsPanel.gridheight = 1;
		gbcResourceButtonsPanel.fill = GridBagConstraints.BOTH;
		gbcResourceButtonsPanel.weightx = 1;
		gbcResourceButtonsPanel.weighty = 0;
		gbcResourceButtonsPanel.anchor = GridBagConstraints.NORTH;
		gbResourceButtonsPanel.setConstraints( addResourceBtn, gbcResourceButtonsPanel );
		resourceButtonsPanel.add( addResourceBtn );

		removeResourceBtn = new JButton( "Remove Resource"  );
		removeResourceBtn.setActionCommand("removeResource");
		removeResourceBtn.addActionListener(this);

		gbcResourceButtonsPanel.gridx = 1;
		gbcResourceButtonsPanel.gridy = 0;
		gbcResourceButtonsPanel.gridwidth = 1;
		gbcResourceButtonsPanel.gridheight = 1;
		gbcResourceButtonsPanel.fill = GridBagConstraints.BOTH;
		gbcResourceButtonsPanel.weightx = 1;
		gbcResourceButtonsPanel.weighty = 0;
		gbcResourceButtonsPanel.anchor = GridBagConstraints.NORTH;
		gbResourceButtonsPanel.setConstraints( removeResourceBtn, gbcResourceButtonsPanel );
		resourceButtonsPanel.add( removeResourceBtn );
		gbcBackPanel.gridx = 1;
		gbcBackPanel.gridy = 2;
		gbcBackPanel.gridwidth = 1;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTH;
		gbBackPanel.setConstraints( resourceButtonsPanel, gbcBackPanel );
		this.add( resourceButtonsPanel );

		subjectPanel = new JPanel();
		subjectPanel.setBorder( BorderFactory.createTitledBorder( "" ) );
		GridBagLayout gbSubjectPanel = new GridBagLayout();
		GridBagConstraints gbcSubjectPanel = new GridBagConstraints();
		subjectPanel.setLayout( gbSubjectPanel );

		serviceIDLabel = new JLabel( "Service Identifier:"  );
		gbcSubjectPanel.gridx = 0;
		gbcSubjectPanel.gridy = 0;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = GridBagConstraints.BOTH;
		gbcSubjectPanel.weightx = 1;
		gbcSubjectPanel.weighty = 1;
		gbcSubjectPanel.anchor = GridBagConstraints.NORTH;
		gbSubjectPanel.setConstraints( serviceIDLabel, gbcSubjectPanel );
		subjectPanel.add( serviceIDLabel );

		dpiLabel = new JLabel( "Your Public DPI:"  );
		gbcSubjectPanel.gridx = 0;
		gbcSubjectPanel.gridy = 1;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = GridBagConstraints.BOTH;
		gbcSubjectPanel.weightx = 1;
		gbcSubjectPanel.weighty = 1;
		gbcSubjectPanel.anchor = GridBagConstraints.NORTH;
		gbSubjectPanel.setConstraints( dpiLabel, gbcSubjectPanel );
		subjectPanel.add( dpiLabel );

		serviceIDTxtField = new JTextField( );
		gbcSubjectPanel.gridx = 1;
		gbcSubjectPanel.gridy = 0;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = GridBagConstraints.BOTH;
		gbcSubjectPanel.weightx = 1;
		gbcSubjectPanel.weighty = 1;
		gbcSubjectPanel.anchor = GridBagConstraints.NORTH;
		gbSubjectPanel.setConstraints( serviceIDTxtField, gbcSubjectPanel );
		subjectPanel.add( serviceIDTxtField );

		dpiTxtField = new JTextField( );
		gbcSubjectPanel.gridx = 1;
		gbcSubjectPanel.gridy = 1;
		gbcSubjectPanel.gridwidth = 1;
		gbcSubjectPanel.gridheight = 1;
		gbcSubjectPanel.fill = GridBagConstraints.BOTH;
		gbcSubjectPanel.weightx = 1;
		gbcSubjectPanel.weighty = 1;
		gbcSubjectPanel.anchor = GridBagConstraints.NORTH;
		gbSubjectPanel.setConstraints( dpiTxtField, gbcSubjectPanel );
		subjectPanel.add( dpiTxtField );
		gbcBackPanel.gridx = 0;
		gbcBackPanel.gridy = 0;
		gbcBackPanel.gridwidth = 3;
		gbcBackPanel.gridheight = 1;
		gbcBackPanel.fill = GridBagConstraints.BOTH;
		gbcBackPanel.weightx = 1;
		gbcBackPanel.weighty = 0;
		gbcBackPanel.anchor = GridBagConstraints.NORTHWEST;
		gbBackPanel.setConstraints( subjectPanel, gbcBackPanel );
		this.add( subjectPanel );



		JScrollPane scpBackPanel = new JScrollPane( this );
		//setContentPane( scpBackPanel );
		//pack();
		//setVisible( true );
	}


	private IPolicyManager getPolicyManager(){
		ServiceTracker servTracker = new ServiceTracker(this.context, IPolicyManager.class.getName(), null);
		servTracker.open();
		Object[] services = servTracker.getServices();
		if (null!=services){
			if (services.length >0){
				return (IPolicyManager) services[0];
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	private IIdentityManagement getIDM(){
		ServiceTracker servTracker = new ServiceTracker(this.context, IIdentityManagement.class.getName(), null);
		servTracker.open();
		Object[] services = servTracker.getServices();
		if (null!=services){
			if (services.length >0){
				return (IIdentityManagement) services[0];
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("savePolicy")){
			if (this.reqEditor!=null){
				JOptionPane.showMessageDialog(this, "Please finish editing the resource in the other editor");
				this.reqEditor.toFront();
			}else
				if (this.serviceIDTxtField.getText().compareTo("")==0){
					JOptionPane.showMessageDialog(this, "Enter the serviceID");
				}
				else if (this.dpiTxtField.getText().compareTo("")==0){
					JOptionPane.showMessageDialog(this,"Enter your public DPI ");
				}
				else if (this.requestItems.size()==0){
					JOptionPane.showMessageDialog(this, "Add at least one resource ");
				}else{
					this.createPolicy();
				}

		}
		else if (e.getActionCommand().equalsIgnoreCase("addResource")){
			//this.setVisible(false);

			//this.getParent().setEnabled(false);
			//System.out.println("enabled: "+this.getParent().isEnabled());
			if (this.reqEditor==null){
				reqEditor = new RequestItemEditor(this);
				reqEditor.addWindowListener(this);	
				System.out.println("Created RequestItemEditor");
			}else{
				reqEditor.toFront();
			}



		}
		else if (e.getActionCommand().equalsIgnoreCase("removeResource")){
			int index = this.resourceTable.getSelectedRow();
			if (index>=0){
				RequestItem item = this.requestItems.get(index);
				System.out.println("Removing item: \n"+item.toString());
				this.requestItems.remove(index);
				this.model.removeRow(index);
			}else{
				JOptionPane.showMessageDialog(this,"Select a resource to delete");
			}
		}
		else if (e.getActionCommand().equalsIgnoreCase("addAction")){
			if (this.reqEditor!=null){
				String message = "Select an action from the list";
				String title = "New Action";

				ActionConstants action  = (ActionConstants) JOptionPane.showInputDialog(this.reqEditor, message, title, JOptionPane.QUESTION_MESSAGE, null, ActionConstants.values(), ActionConstants.READ);
				if (action!=null){

					this.reqEditor.addAction(action, new Boolean(false));
				}
			}

		}
		else if(e.getActionCommand().equalsIgnoreCase("removeAction")){
			this.reqEditor.removeSelectedAction();


		}
		else if(e.getActionCommand().equalsIgnoreCase("addCondition")){
			if (this.reqEditor!=null){
				String message = "Select a condition from the list";
				String title = "New Condition";
				ConditionConstants condition = (ConditionConstants) JOptionPane.showInputDialog(this.reqEditor, message, title, JOptionPane.QUESTION_MESSAGE, null, ConditionConstants.values(), ConditionConstants.DATA_RETENTION_IN_HOURS);
				if (condition!=null){
					System.out.println("Value: "+condition);
					message = "Enter a value for "+condition.toString();
					String value = (String) JOptionPane.showInputDialog(this.reqEditor, message, title, JOptionPane.QUESTION_MESSAGE, null,null,"");
					if (value!=null){
						this.reqEditor.addCondition(condition, value,new Boolean(true));
					}
				}
			}

		}
		else if(e.getActionCommand().equalsIgnoreCase("removeCondition")){
			this.reqEditor.removeSelectedCondition();
		}

		else if(e.getActionCommand().equalsIgnoreCase("saveResource")){
			RequestItem requestItem = this.reqEditor.getRequestItem();
			if (requestItem==null){
				System.out.println("Problem retrieving RequestItem");
			}else{
				//this.getParent().setEnabled(true);
				//this.setEnabled(true);
				//System.out.println("enabled: "+this.isEnabled());
				this.reqEditor.dispose();
				this.reqEditor = null;
				this.requestItems.add(requestItem);
				this.addResource(requestItem);
			}

		}
		else if (e.getActionCommand().equalsIgnoreCase("discard")){

			//this.setEnabled(true);
			System.out.println("enabled: "+this.isEnabled());
			if (this.reqEditor==null){
				System.out.println("reqEditor is null");
			}
			this.reqEditor.dispose();
			this.reqEditor = null;

		}
	}

	private void createPolicy(){
		IDigitalPersonalIdentifier dpi;
		try {
			this.policy = null;
			dpi = DigitalPersonalIdentifier.fromString(this.dpiTxtField.getText());
		} catch (MalformedDigitialPersonalIdentifierException e) {
			//dpi = new DigitalPersonalIdentifier(this.dpiTxtField.getText());

			dpi = this.getIDM().getPublicDigitalPersonalIdentifier();
			JOptionPane.showMessageDialog(this, "DPI not valid. Using public DPI from IDM: "+dpi.toUriString());
			e.printStackTrace();
		}
		IServiceIdentifier serviceID;
		try{
			String localserviceID = this.serviceIDTxtField.getText();
			if ( (localserviceID.contains("@")) || (localserviceID.startsWith("pss://")) ){
				JOptionPane.showMessageDialog(this, "Please enter the local serviceID without the DPI and pss:// prefix");
			}
			else{
					serviceID = new PssServiceIdentifier(localserviceID,dpi);
				

				Subject subject = new Subject(dpi,serviceID);
				this.policy = new RequestPolicy(subject, requestItems);
				try {
					File file =  new File("./servicePrivacyPolicies/"+serviceID.getLocalServiceId()+".xml");
					FileWriter fWriter = new FileWriter(file);
					BufferedWriter bWriter = new BufferedWriter(fWriter);
					BufferedWriter out = new BufferedWriter(bWriter);
					out.write(this.policy.toXMLString());
					out.close();
					JOptionPane.showMessageDialog(this, "ServicePolicy saved as: "+file.getCanonicalPath());
				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(this, "Error saving servicePolicy to file");
					ioe.printStackTrace();
				}
				this.policyMgr = this.getPolicyManager();
				if (this.policyMgr==null){
					System.out.println("PolicyMgr is null");
				}else{
					this.policyMgr.addPrivacyPolicyForService(serviceID, policy);
					JOptionPane.showMessageDialog(this, "Service Policy sent to Policy Manager for storing in context DB");
				}
			}
		}
		catch (IllegalArgumentException ie){
			//serviceID = new PssServiceIdentifier("pss://NullSoft@Winamp");
			JOptionPane.showMessageDialog(this, "Invalid serviceID");
		}
	}

	private void addResource(RequestItem item){
		Vector row = new Vector();
		row.add(item.getResource().getContextType());
		String actions = "";
		for (int i=0; i<item.getActions().size(); i++){
			Action a = item.getActions().get(i);
			if (i>0){
				actions = actions.concat("+");
				actions = actions.concat(a.getActionType().toString());
				
			}else{
				actions = a.getActionType().toString();
			}
		}
		row.add(actions);
		String conditions = "";
		for (int i=0; i<item.getConditions().size();i++){
			Condition con = item.getConditions().get(i);
			if (i>0){
				conditions = conditions.concat(",");
				conditions = conditions.concat(con.getConditionName().toString());
			}else{
				conditions = conditions.concat(con.getConditionName().toString());
			}
		}
		row.add(conditions);
		Boolean b = Boolean.valueOf("a");
		System.out.println(b);
		if (b){
			System.out.println("true");
		}else{
			System.out.println("false");
		}
		row.add(b);
		this.model.addRow(row);
	
		System.out.println(this.model.getColumnClass(this.model.getColumnCount()));
		this.resourceTable.setModel(model);
		System.out.println(this.resourceTable.getColumnClass(this.resourceTable.getColumnCount()-1));


	}


	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("window closing");
		JOptionPane.showMessageDialog(reqEditor, "Sorry, Use the Save or Discard Buttons to exit");

	}

	public void windowDeactivated(WindowEvent e) {}	
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {} 
	public void windowClosed(WindowEvent e) {}	
	public void windowActivated(WindowEvent e) {}

	

} 
