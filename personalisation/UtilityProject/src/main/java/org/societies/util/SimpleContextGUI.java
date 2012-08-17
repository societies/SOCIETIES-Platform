package org.societies.util;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * @author  Administrator
 * @created July 17, 2012
 */
public class SimpleContextGUI extends JFrame  implements ActionListener
{
	static SimpleContextGUI theSimpleContextGUI;
	private static Logger LOG = LoggerFactory.getLogger(SimpleContextGUI.class);

	JPanel pnPanel0;

	JPanel pnPanel3;
	JLabel lbLabel2;
	JTextField txtSymLoc;
	JButton btnSymLocUpdate;

	JPanel pnPanel4;
	JComboBox cmbCtxAttr;
	JTextField txtCtxAttributeValue;
	JButton btUpdateCtxAttribute;

	JPanel pnPanel5;
	JTextField txtOtherCtxAttributeType;
	JTextField txtOtherCtxAttributeValue;
	JButton btUpdateOther;
	private ICtxBroker ctxBroker;
	private Set<CtxAttribute> symlocAttributes; 
	private ICommManager commManager;
	private IIdentityManager idMgr;
	private IIdentity userIdentity;

	private IndividualCtxEntity entityPerson;
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
		theSimpleContextGUI = new SimpleContextGUI();
	} 

	/**
	 */
	public SimpleContextGUI() 
	{
		super( "TITLE" );
		this.symlocAttributes = new HashSet<CtxAttribute>();
		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel3 = new JPanel();
		pnPanel3.setBorder( BorderFactory.createTitledBorder( "Update SymLoc" ) );
		GridBagLayout gbPanel3 = new GridBagLayout();
		GridBagConstraints gbcPanel3 = new GridBagConstraints();
		pnPanel3.setLayout( gbPanel3 );

		lbLabel2 = new JLabel( "Symbolic Location:"  );
		gbcPanel3.gridx = 0;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 1;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbcPanel3.insets = new Insets( 20,20,20,20 );
		gbPanel3.setConstraints( lbLabel2, gbcPanel3 );
		pnPanel3.add( lbLabel2 );

		txtSymLoc = new JTextField( );
		gbcPanel3.gridx = 1;
		gbcPanel3.gridy = 0;
		gbcPanel3.gridwidth = 1;
		gbcPanel3.gridheight = 1;
		gbcPanel3.fill = GridBagConstraints.BOTH;
		gbcPanel3.weightx = 1;
		gbcPanel3.weighty = 0;
		gbcPanel3.anchor = GridBagConstraints.NORTH;
		gbcPanel3.insets = new Insets( 20,0,20,20 );
		gbPanel3.setConstraints( txtSymLoc, gbcPanel3 );
		pnPanel3.add( txtSymLoc );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 10,10,10,10 );
		gbPanel0.setConstraints( pnPanel3, gbcPanel0 );
		pnPanel0.add( pnPanel3 );

		btnSymLocUpdate = new JButton( "Update"  );
		btnSymLocUpdate.addActionListener(this);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 1;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.VERTICAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets( 0,0,20,0 );
		gbPanel0.setConstraints( btnSymLocUpdate, gbcPanel0 );
		pnPanel0.add( btnSymLocUpdate );

		pnPanel4 = new JPanel();
		pnPanel4.setBorder( BorderFactory.createTitledBorder( "Update CtxAttributeTypes" ) );
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );

		
		Object[] ctxAttributetypes = this.getCtxAttributeTypesList();
		cmbCtxAttr = new JComboBox( ctxAttributetypes );
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 1;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 20,20,20,20 );
		gbPanel4.setConstraints( cmbCtxAttr, gbcPanel4 );
		pnPanel4.add( cmbCtxAttr );

		txtCtxAttributeValue = new JTextField( );
		gbcPanel4.gridx = 1;
		gbcPanel4.gridy = 0;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 0;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 20,0,20,20 );
		gbPanel4.setConstraints( txtCtxAttributeValue, gbcPanel4 );
		pnPanel4.add( txtCtxAttributeValue );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 2;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 10,10,10,10 );
		gbPanel0.setConstraints( pnPanel4, gbcPanel0 );
		pnPanel0.add( pnPanel4 );

		btUpdateCtxAttribute = new JButton( "Update"  );
		btUpdateCtxAttribute.addActionListener(this);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 3;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.VERTICAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets( 0,0,20,0 );
		gbPanel0.setConstraints( btUpdateCtxAttribute, gbcPanel0 );
		pnPanel0.add( btUpdateCtxAttribute );

		pnPanel5 = new JPanel();
		pnPanel5.setBorder( BorderFactory.createTitledBorder( "Update other" ) );
		GridBagLayout gbPanel5 = new GridBagLayout();
		GridBagConstraints gbcPanel5 = new GridBagConstraints();
		pnPanel5.setLayout( gbPanel5 );

		txtOtherCtxAttributeType = new JTextField( );
		gbcPanel5.gridx = 1;
		gbcPanel5.gridy = 0;
		gbcPanel5.gridwidth = 1;
		gbcPanel5.gridheight = 1;
		gbcPanel5.fill = GridBagConstraints.BOTH;
		gbcPanel5.weightx = 1;
		gbcPanel5.weighty = 0;
		gbcPanel5.anchor = GridBagConstraints.NORTH;
		gbcPanel5.insets = new Insets( 20,0,20,20 );
		gbPanel5.setConstraints( txtOtherCtxAttributeType, gbcPanel5 );
		pnPanel5.add( txtOtherCtxAttributeType );

		txtOtherCtxAttributeValue = new JTextField( );
		gbcPanel5.gridx = 0;
		gbcPanel5.gridy = 0;
		gbcPanel5.gridwidth = 1;
		gbcPanel5.gridheight = 1;
		gbcPanel5.fill = GridBagConstraints.BOTH;
		gbcPanel5.weightx = 1;
		gbcPanel5.weighty = 0;
		gbcPanel5.anchor = GridBagConstraints.NORTH;
		gbcPanel5.insets = new Insets( 20,20,20,20 );
		gbPanel5.setConstraints( txtOtherCtxAttributeValue, gbcPanel5 );
		pnPanel5.add( txtOtherCtxAttributeValue );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 5;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 10,10,10,10 );
		gbPanel0.setConstraints( pnPanel5, gbcPanel0 );
		pnPanel0.add( pnPanel5 );

		btUpdateOther = new JButton( "Update"  );
		btUpdateOther.addActionListener(this);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 7;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.VERTICAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets( 0,0,20,0 );
		gbPanel0.setConstraints( btUpdateOther, gbcPanel0 );
		pnPanel0.add( btUpdateOther );

		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );
	}

	private Object[] getCtxAttributeTypesList() {
		Field[] fields = CtxAttributeTypes.class.getDeclaredFields();
		
		String[] names = new String[fields.length];
		
		for (int i=0; i<names.length; i++){
			names[i] = fields[i].getName();
		}
		
		return fields;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		if (this.entityPerson==null){
			this.LOG.debug("Retrieving Peson entity");
			Future<IndividualCtxEntity> retrieveIndividualEntity;
			try {
				retrieveIndividualEntity = this.getCtxBroker().retrieveIndividualEntity(this.userIdentity);
				entityPerson = retrieveIndividualEntity.get();
				this.LOG.debug("Retrieved Person entity");
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
		if (event.getSource().equals(this.btnSymLocUpdate)){
			this.LOG.debug("BtnSymlocUpdate clicked");
			String value = this.txtSymLoc.getText();
			
			if (this.symlocAttributes.isEmpty()){
				this.LOG.debug("Retrieving symloc attributes");
				try {
					Future<IndividualCtxEntity> retrieveIndividualEntity = this.getCtxBroker().retrieveIndividualEntity(this.userIdentity);
					entityPerson = retrieveIndividualEntity.get();
					
					this.symlocAttributes = entityPerson.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
					this.LOG.debug("Retrieved "+this.symlocAttributes.size()+" symloc attributes");
					
				} catch (CtxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			this.LOG.debug("Updating symLoc attributes to: "+value);
				for (CtxAttribute ctxAttribute : this.symlocAttributes){
					ctxAttribute.setStringValue(value);
					try {
						ctxAttribute = (CtxAttribute) ctxBroker.update(ctxAttribute).get();
					} catch (CtxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
		}else if (event.getSource().equals(this.btUpdateCtxAttribute)){
			Field field = (Field) this.cmbCtxAttr.getSelectedItem();
			try {
				String ctxAttributeType = (String) field.get(null);
				
				Set<CtxAttribute> attributes = this.entityPerson.getAttributes(ctxAttributeType);
	
				if (attributes.isEmpty()){
					this.entityPerson = (IndividualCtxEntity) this.ctxBroker.retrieveIndividualEntity(userIdentity);
				}
				for (CtxAttribute attr : attributes){
					attr.setStringValue(this.txtCtxAttributeValue.getText());
					ctxBroker.update(attr);
				}
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (event.getSource().equals(this.btUpdateOther)){
			JOptionPane.showMessageDialog(this, "Not implemented yet", "Missing Implementation", JOptionPane.INFORMATION_MESSAGE, null);
		}
		
	}

	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	/**
	 * @return the commManager
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
		this.idMgr = this.commManager.getIdManager();
		this.userIdentity = this.idMgr.getThisNetworkNode();
	} 
	

} 
