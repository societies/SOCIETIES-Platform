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
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.source.CtxSourceNames;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;

/**
 * @author  Administrator
 * @created July 17, 2012
 */
public class SimpleContextGUI2 extends JFrame  implements ActionListener
{
	static SimpleContextGUI2 theSimpleContextGUI;
	private static Logger LOG = LoggerFactory.getLogger(SimpleContextGUI2.class);

	JPanel pnPanel0;

	JPanel pnPanel21;
	JLabel lbLabel21;

	JPanel pnPanel3;
	JLabel lbLabel2;
	JTextField txtSymLoc;



	JPanel pnPanel4;
	JComboBox cmbCtxAttr;
	JComboBox cmbCtxAttrSourceType;

	JComboBox cmbCtxEnt;
	JTextField txtCtxAttributeValue;
	JTextField txtCtxEntityValue;

	JButton btUpdate;
	JButton btnRetrieve;

	JPanel pnPanel5;
	JTextField txtOtherCtxAttributeType;
	JTextField txtOtherCtxAttributeValue;
	//JButton btUpdateOther;
	private ICtxBroker ctxBroker;

	private ICommManager commManager;
	private IIdentityManager idMgr;
	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;


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
		theSimpleContextGUI = new SimpleContextGUI2();
	} 

	/**
	 */
	public SimpleContextGUI2() 
	{
		super( "TITLE" );

		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnPanel4 = new JPanel();
		pnPanel4.setBorder( BorderFactory.createTitledBorder( "Select CtxAttributeType and CtxSourceType" ) );
		GridBagLayout gbPanel4 = new GridBagLayout();
		GridBagConstraints gbcPanel4 = new GridBagConstraints();
		pnPanel4.setLayout( gbPanel4 );

		//Object[] ctxAttributetypes = this.getCtxAttributeTypesList();
		Object[] ctxAttributetypes = CtxAttributeTypes.class.getDeclaredFields();
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

		Object[] ctxAttributeSourcetypes = CtxSourceNames.class.getDeclaredFields();

		cmbCtxAttrSourceType = new JComboBox( ctxAttributeSourcetypes );
		gbcPanel4.gridx = 0;
		gbcPanel4.gridy = 1;
		gbcPanel4.gridwidth = 1;
		gbcPanel4.gridheight = 1;
		gbcPanel4.fill = GridBagConstraints.BOTH;
		gbcPanel4.weightx = 1;
		gbcPanel4.weighty = 1;
		gbcPanel4.anchor = GridBagConstraints.NORTH;
		gbcPanel4.insets = new Insets( 20,20,20,20 );
		gbPanel4.setConstraints( cmbCtxAttrSourceType, gbcPanel4 );
		pnPanel4.add( cmbCtxAttrSourceType );


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

		btUpdate = new JButton( "Create/Update"  );
		btUpdate.addActionListener(this);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 3;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.VERTICAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets( 0,0,20,0 );
		gbPanel0.setConstraints( btUpdate, gbcPanel0 );
		pnPanel0.add( btUpdate );


		// Select entity type
		pnPanel21 =  new JPanel();
		pnPanel21.setBorder( BorderFactory.createTitledBorder( "Select CtxEntity" ) );
		GridBagLayout gbPanel21 = new GridBagLayout();


		GridBagConstraints gbcPanel21 = new GridBagConstraints();

		pnPanel21.setLayout( gbPanel21 );
		lbLabel21 = new JLabel( "CtxEntity type"  );

		Object[] ctxEntitytypes = CtxEntityTypes.class.getDeclaredFields();

		cmbCtxEnt = new JComboBox( ctxEntitytypes );

		gbcPanel21.gridx = 0;
		gbcPanel21.gridy = 0;
		gbcPanel21.gridwidth = 1;
		gbcPanel21.gridheight = 1;
		gbcPanel21.fill = GridBagConstraints.BOTH;
		gbcPanel21.weightx = 1;
		gbcPanel21.weighty = 1;
		gbcPanel21.anchor = GridBagConstraints.NORTH;
		gbcPanel21.insets = new Insets( 20,20,20,20 );
		gbPanel21.setConstraints( cmbCtxEnt, gbcPanel21 );
		pnPanel21.add( cmbCtxEnt );

		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 2;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbcPanel0.insets = new Insets( 10,10,10,10 );
		gbPanel0.setConstraints( pnPanel21, gbcPanel0 );
		pnPanel0.add( pnPanel21 );


		btnRetrieve = new JButton( "Retrieve"  );
		btnRetrieve.addActionListener(this);
		gbcPanel0.gridx = 1;
		gbcPanel0.gridy = 4;
		gbcPanel0.gridwidth = 1;
		gbcPanel0.gridheight = 1;
		gbcPanel0.fill = GridBagConstraints.VERTICAL;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.CENTER;
		gbcPanel0.insets = new Insets( 0,0,20,0 );
		gbPanel0.setConstraints( btnRetrieve, gbcPanel0 );
		pnPanel0.add( btnRetrieve );



		// end of select entity type
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );

	}

	/*
	private Object[] getCtxAttributeTypesList() {

		Field[] fields = CtxAttributeTypes.class.getDeclaredFields();

		String[] names = new String[fields.length];

		for (int i=0; i<names.length; i++){
			names[i] = fields[i].getName();
			System.out.println("fields "+fields[i]);
		}

		return fields;
	}


	private Object[] getCtxEntityTypesList() {
		Field[] fields = CtxEntityTypes.class.getDeclaredFields();

		String[] names = new String[fields.length];

		for (int i=0; i<names.length; i++){
			names[i] = fields[i].getName();
		}

		return fields;
	}


	private Object[] getCtxAttributeSourceTypeList() {
		Field[] fields = CtxSourceNames.class.getDeclaredFields();

		String[] names = new String[fields.length];

		for (int i=0; i<names.length; i++){
			names[i] = fields[i].getName();
		}

		return fields;
	}
	 */

	@Override
	public void actionPerformed(ActionEvent event) {


		Field field1 = (Field) this.cmbCtxEnt.getSelectedItem();
		Field field2 = (Field) this.cmbCtxAttr.getSelectedItem();
		Field field3 = (Field) this.cmbCtxAttrSourceType.getSelectedItem();

		try {
			String ctxEntityType = (String) field1.get(null);
			String ctxAttributeType = (String) field2.get(null);
			String ctxAttributeSourceType = (String) field3.get(null);


			if (event.getSource().equals(this.btUpdate)){

				if(ctxEntityType.equals(CtxEntityTypes.CSS_NODE) ){

					this.updateLocationCSSNode(this.txtCtxAttributeValue.getText(), ctxAttributeSourceType, 1d/60);	
				}


				List<CtxIdentifier> listCtxEntID =  this.ctxBroker.lookup(CtxModelType.ENTITY,ctxEntityType).get();
				CtxEntity ctxEntity = null;
				if(listCtxEntID.size()>0) {
					ctxEntity = (CtxEntity) this.ctxBroker.retrieve(listCtxEntID.get(0)).get();
				} else if(listCtxEntID.size() == 0){
					ctxEntity = (CtxEntity) this.ctxBroker.createEntity(ctxEntityType).get();

				}

				List<CtxIdentifier> listCtxAttrID =  this.ctxBroker.lookup(ctxEntity.getId(), CtxModelType.ATTRIBUTE,ctxAttributeType).get();


				if(listCtxAttrID.size()>0){
					CtxAttribute attr = (CtxAttribute) this.ctxBroker.retrieve(listCtxAttrID.get(0)).get();
					attr.setStringValue(this.txtCtxAttributeValue.getText());
					ctxBroker.update(attr);
					attr = (CtxAttribute) ctxBroker.retrieve(attr.getId()).get();

					JOptionPane.showMessageDialog(this,  "Updated "+ attr.getId()+ "with value:"+attr.getStringValue(), "??", JOptionPane.INFORMATION_MESSAGE, null);
				} else if (listCtxAttrID.size() == 0){
					CtxAttribute attr = this.ctxBroker.createAttribute(ctxEntity.getId(),ctxAttributeType ).get();
					attr.setStringValue(this.txtCtxAttributeValue.getText());
					ctxBroker.update(attr);
					attr = (CtxAttribute) ctxBroker.retrieve(attr.getId()).get();
					JOptionPane.showMessageDialog(this,  "Created "+ attr.getId()+ "with value:"+attr.getStringValue(), "??", JOptionPane.INFORMATION_MESSAGE, null);
				}

			} else if(event.getSource().equals(this.btnRetrieve)){

				List<CtxIdentifier> listCtxEntID =  this.ctxBroker.lookup(CtxModelType.ENTITY,ctxEntityType).get();

				CtxEntity ctxEntity = null;

				if(listCtxEntID.size()>0) {
					ctxEntity = (CtxEntity) this.ctxBroker.retrieve(listCtxEntID.get(0)).get();
					List<CtxIdentifier> listCtxAttrID  = this.ctxBroker.lookup(ctxEntity.getId(),CtxModelType.ATTRIBUTE, ctxAttributeType).get();

					if(listCtxAttrID.size() > 0){
						 CtxAttribute attr = (CtxAttribute) this.ctxBroker.retrieve(listCtxAttrID.get(0)).get();
				
						 JOptionPane.showMessageDialog(this,  "Retrieved "+ attr.getId()+ "with value:"+attr.getStringValue(), "Ctx message", JOptionPane.INFORMATION_MESSAGE, null);
					}
				}


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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}



	private CtxAttribute updateLocationCSSNode(String locationValue, String sourceId, Double updateFreq){

		LOG.info("*** updateLocationCSSNode : updates an existing  Location attribute in CSS node");

		CtxEntity cssNodeEntity = null ;
		CtxAttribute locationCssNodeAttr_pz = null;
		CtxAttribute locationCssNodeAttr_rfid = null;
		CtxAttribute locationCssNodeAttrNull = null;

		try {
			this.cssNodeId = this.commManager.getIdManager().getThisNetworkNode();
			final String cssOwnerStr = this.cssNodeId.getBareJid();
			this.cssOwnerId = this.commManager.getIdManager().fromJid(cssOwnerStr);
			LOG.info("*** cssOwnerId = " + this.cssOwnerId);

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 



		try {

			cssNodeEntity = this.ctxBroker.retrieveCssNode(this.cssNodeId).get();
			Set<CtxAttribute> attrLocNodeSet = cssNodeEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC);
			List<CtxAttribute> attrLocList = new ArrayList<CtxAttribute>(attrLocNodeSet);

			if(attrLocList.size() <2){
				createCSSNodeLocationAttributes();	
			}



			if(attrLocList.size() > 0){

				for(CtxAttribute locationCssNodeAttr : attrLocList){
					LOG.info("update  location attribute "+locationCssNodeAttr.getId()  +" with source id "+locationCssNodeAttr.getSourceId()+" for source "+sourceId );

					if(locationCssNodeAttr.getSourceId().contains(CtxSourceNames.PZ) && sourceId.contains(CtxSourceNames.PZ)){
						//LOG.info("update PZ location attribute with value "+locationValue);
						locationCssNodeAttr_pz = locationCssNodeAttr;
						locationCssNodeAttr_pz.setStringValue(locationValue);
						locationCssNodeAttr_pz.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr_pz = (CtxAttribute) this.ctxBroker.update(locationCssNodeAttr_pz).get();
						return locationCssNodeAttr_pz;
					}

					if(locationCssNodeAttr.getSourceId().contains(CtxSourceNames.RFID) && sourceId.contains(CtxSourceNames.RFID)){
						LOG.info("update rfid location attribute with value"+locationValue);
						locationCssNodeAttr_rfid = locationCssNodeAttr;
						locationCssNodeAttr_rfid.setStringValue(locationValue);
						locationCssNodeAttr_rfid.getQuality().setUpdateFrequency(updateFreq);
						locationCssNodeAttr_rfid = (CtxAttribute) this.ctxBroker.update(locationCssNodeAttr_rfid).get();
						return locationCssNodeAttr_rfid;					
					}
				}
			}



		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOG.info("something went wrong when updating value "+locationValue+ " sourceId"+sourceId+" updateFreq"+updateFreq);

		return locationCssNodeAttrNull;
	}


	private void createCSSNodeLocationAttributes(){

		boolean createLocAttrRFID = true;
		boolean createLocAttrPZ = true;

		try {
			this.cssNodeId = this.commManager.getIdManager().getThisNetworkNode();

			//	final String cssOwnerStr = this.cssNodeId.getBareJid();
			//	this.cssOwnerId = this.commManager.getIdManager().fromJid(cssOwnerStr);
			//	LOG.info("*** cssOwnerId = " + this.cssOwnerId);

			CtxEntity cssNodeEntity = this.ctxBroker.retrieveCssNode(this.cssNodeId).get();

			Set<CtxAttribute> attrLocSet = cssNodeEntity.getAttributes(CtxAttributeTypes.LOCATION_SYMBOLIC); 
			List<CtxAttribute> attrLocList = new ArrayList<CtxAttribute>(attrLocSet);

			for(CtxAttribute attrLoc : attrLocList){
				if(attrLoc.getSourceId().contains(CtxSourceNames.RFID)) createLocAttrRFID = false;

			}

			for(CtxAttribute attrLoc : attrLocList){

				if(attrLoc.getSourceId().contains(CtxSourceNames.PZ)) createLocAttrPZ = false;

			}

			if(createLocAttrRFID == true) {
				LOG.info("create RFID location attribute");
				CtxAttribute loc_rfid = this.ctxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
				loc_rfid.setSourceId(CtxSourceNames.RFID);
				this.ctxBroker.update(loc_rfid);
			}

			if(createLocAttrPZ == true) {
				LOG.info("create PZ location attribute");
				CtxAttribute loc_pz = this.ctxBroker.createAttribute(cssNodeEntity.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
				loc_pz.setSourceId(CtxSourceNames.PZ);
				this.ctxBroker.update(loc_pz);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		//this.userIdentity = this.idMgr.getThisNetworkNode();
	} 


} 
