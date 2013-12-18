package org.societies.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.joda.time.DateTime;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.personalisation.CACI.api.CACIDiscovery.ICACIDiscovery;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
// org.societies.personalisation.CAUI.api.model.UserIntentModelData;
import org.societies.personalisation.CAUI.api.model.*;

public class CauiGUI2  extends JFrame  implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ICtxBroker ctxBroker;
	private ICommManager commManager;
	private IIdentityManager idMgr;
	private IIdentity userIdentity;
	public  ICAUIPrediction cauiPrediction;
	private ICAUIDiscovery cauiDiscovery;

	public ICACIDiscovery caciDiscovery;
	
	public static IUserActionMonitor uam;


	private final static String newline = "\n";

	private IndividualCtxEntity entityPerson;

	String history = "";

	JTextArea textAreaHistory;
	JTextArea textAreaUIModel;

	public CauiGUI2() {
		
		
		//JFrame f = new JFrame();

		ctxBroker = this.getCtxBroker();
		cauiPrediction = this.getCauiPrediction();
		caciDiscovery = this.getCaciDiscovery();
		
		System.out.println("services:cauiPrediction: " + cauiPrediction);
		System.out.println("services:caciDiscovery: " + caciDiscovery);
		System.out.println("services:cauiDiscovery: " + cauiDiscovery);
		System.out.println("services:ctxBroker: " + ctxBroker);
		System.out.println("services:uam "+ uam);
		
		

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	
		} catch (ClassNotFoundException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		} catch (InstantiationException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		} catch (IllegalAccessException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		} catch (UnsupportedLookAndFeelException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
		
		getContentPane().setLayout(null);
		//history
		JPanel panelHistory = new JPanel();
		panelHistory.setBounds(10, 11, 560, 152);

		panelHistory.setLayout(null);

		textAreaHistory = new JTextArea();
		textAreaHistory.setEditable(false); 

		JScrollPane scrolltextAreaHistory = new JScrollPane(textAreaHistory);
		scrolltextAreaHistory.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrolltextAreaHistory.setBounds(10, 11, 446, 141);
		panelHistory.add(scrolltextAreaHistory);

		//button
		JButton btnRetrieveHistory = new JButton("history");
		btnRetrieveHistory.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.out.println("retrieve history pressed");
				Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = retrieveHistoryTupleData();
				String data = printHistory(results);
				textAreaHistory.setText("");
				textAreaHistory.append(data+newline);

			}
		});
		btnRetrieveHistory.setBounds(485, 33, 65, 23);
		panelHistory.add(btnRetrieveHistory);
		getContentPane().add(panelHistory);



		// ui model		
		JPanel panelModel = new JPanel();
		panelModel.setBounds(10, 164, 560, 167);
		getContentPane().add(panelModel);
		panelModel.setLayout(null);

		textAreaUIModel = new JTextArea();
		//textAreaUIModel.setBounds(10, 11, 434, 145);
		//panelModel.add(textAreaUIModel);

		JScrollPane scrolltextAreaUIModel = new JScrollPane(textAreaUIModel);
		scrolltextAreaUIModel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrolltextAreaUIModel.setBounds(10, 11, 434, 145);
		panelModel.add(scrolltextAreaUIModel);




		JButton btnNewButton = new JButton("Display model");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Prediction model button clicked");

				List<CtxIdentifier> ls;

				try {
					ls = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.CAUI_MODEL).get();
					if(ls.size()>0){
						CtxAttributeIdentifier attrID = (CtxAttributeIdentifier) ls.get(0);
						CtxAttribute uiModelAttr =  ctxBroker.retrieveAttribute(attrID,false).get();
						UserIntentModelData newUIModelData = (UserIntentModelData) SerialisationHelper.deserialise(uiModelAttr.getBinaryValue(), this.getClass().getClassLoader());
						textAreaUIModel.setText("");
						textAreaUIModel.append(printModel(newUIModelData.getActionModel()));
					}

				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CtxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (ClassNotFoundException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

			}
		});
		btnNewButton.setBounds(454, 34, 106, 23);
		panelModel.add(btnNewButton);

		JButton btnLearnModel = new JButton("Learn model");
		btnLearnModel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				cauiDiscovery.generateNewUserModel();

			}
		});
		btnLearnModel.setBounds(454, 68, 106, 23);
		panelModel.add(btnLearnModel);

		JButton btnLearnCaci = new JButton("Learn CACI");
		btnLearnCaci.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				List<CtxIdentifier> listISMemberOf;
				try {
					listISMemberOf = ctxBroker.lookup(CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();

					IIdentity cisId = null;
					if(!listISMemberOf.isEmpty() ){
						CtxAssociation assoc = (CtxAssociation) ctxBroker.retrieve(listISMemberOf.get(0)).get();
						Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();

						for(CtxEntityIdentifier entId : entIDSet){
							cisId = commManager.getIdManager().fromJid(entId.getOwnerId());
							System.out.println("cis id : "+cisId );
						}
						if( cisId!= null){
							System.out.println("generate new community model for cisID:" + cisId );
							caciDiscovery.generateNewCommunityModel(cisId);
						}

					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CtxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidFormatException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}	
			}
		});
		btnLearnCaci.setBounds(454, 98, 89, 23);
		panelModel.add(btnLearnCaci);


		// monitor action
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 342, 414, 32);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);

		textField = new JTextField();
		textField.setBounds(0, 11, 86, 20);
		panel_2.add(textField);
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setBounds(114, 11, 86, 20);
		panel_2.add(textField_1);
		textField_1.setColumns(10);
		
		System.out.println("monitor action button starting 1");
		JButton btnPerformAction = new JButton("monitoraction");
		btnPerformAction.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
			
				System.out.println("monitor action button clicked");
				setSymbolicLocation();
				ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
				try {
					serviceId1.setIdentifier(new URI("css://nikosk@societies.org/radioService"));
					serviceId1.setServiceInstanceIdentifier("css://nikosk@societies.org/radioService");
					IAction action1 = null;
					if(textField.getText().equals("a")){
						 action1 = new Action(serviceId1, "serviceType", textField.getText(), textField_1.getText(), false, true, true);
						 System.out.println("creating NON impl action :"+ action1);		
						 //Action(ServiceResourceIdentifier serviceID, String serviceType, String parameterName, String value, boolean implementable, boolean contextDependent, boolean proactive){
					} else action1 = new Action(serviceId1, "serviceType", textField.getText(), textField_1.getText());
					
					IIdentity cssOwnerId = getOwnerId();
					
					System.out.println(" action: "+ action1 );
					System.out.println( "cssOwnerId "+ cssOwnerId);
					uam.monitor(cssOwnerId, action1);

				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnPerformAction.setBounds(231, 10, 120, 23);
		panel_2.add(btnPerformAction);

		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

		//setContentPane( panel );
		//pack();
		setVisible( true );
	
		
	}

	static CauiGUI2 cauiGUI2;
	private JTextField textField;
	private JTextField textField_1;




	public void setHistoryData(){

	}
	public void setSymbolicLocation(){
		
		IIdentity localID = getOwnerId();
		try {
			
			List<CtxIdentifier> locList = this.ctxBroker.lookup(localID, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			if(!locList.isEmpty()){
				CtxAttribute loc = (CtxAttribute) this.ctxBroker.retrieve(locList.get(0)).get();
				DateTime now = new DateTime();
				if(now.getSecondOfMinute() > 30){
					loc.setStringValue("home");
				} else {
					loc.setStringValue("out");	
				}
				
				this.ctxBroker.update(loc);
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
		cauiGUI2 = new CauiGUI2();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	} 

	
	public void setCaciDiscovery(ICACIDiscovery caciDiscovery) {

		this.caciDiscovery = caciDiscovery;
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

	public void setCauiPrediction(ICAUIPrediction cauiPrediction){
		this.cauiPrediction = cauiPrediction;
	}

	
	public ICAUIPrediction getCauiPrediction() {
		return cauiPrediction;
	}

	public IUserActionMonitor getUam() {
		return uam;
	}

	public void setUam(IUserActionMonitor uam){
		this.uam = uam;
	}

	public ICAUIDiscovery getCauiDiscovery() {

		return cauiDiscovery;
	}
	public ICACIDiscovery getCaciDiscovery() {

		return caciDiscovery;
	}

	

	public void setCauiDiscovery(ICAUIDiscovery cauiDiscovery) {

		this.cauiDiscovery = cauiDiscovery;
	}

	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTupleData(){

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		try {
			results = ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, listOfEscortingAttributeIds, null, null).get();
			System.out.println(" retrieveHistoryTupleData: " +results);
			
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}


	public String printModel(HashMap<IUserIntentAction, HashMap<IUserIntentAction,Double>> model){

		String data ="";
		for(IUserIntentAction action: model.keySet()){
			String line =  action.getparameterName()+" "+ action.getvalue()+ "->"+ model.get(action).toString()+newline;
			data = data + line;
		}


		return data;
	}

	public String printHistory(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData){

		String data =""; 
		int i = 0;
		for(CtxHistoryAttribute ctxHocAttr :mapHocData.keySet()){

			try {
				Date time = ctxHocAttr.getLastUpdated();
				IAction action = (IAction)SerialisationHelper.deserialise(ctxHocAttr.getBinaryValue(), this.getClass().getClassLoader());
				List<CtxHistoryAttribute> escortingAttrList = mapHocData.get(ctxHocAttr);
				
			
				CtxHistoryAttribute attr1 = escortingAttrList.get(0);
				CtxHistoryAttribute attr2 = escortingAttrList.get(1);
				CtxHistoryAttribute attr3 = escortingAttrList.get(2);
				CtxHistoryAttribute attr4 = escortingAttrList.get(3);
				String escortingHistory = getValueFromAttr(attr1)+" "+getValueFromAttr(attr2)+" "+getValueFromAttr(attr3)+" "+getValueFromAttr(attr4);
				System.out.println(i+" primary Attr: {"+action.getparameterName() +" "+action.getvalue()+"} escorting: {" +escortingHistory+"}");
				//System.out.println(i+" primary Attr: {"+action.getparameterName() +" "+action.getvalue()+"} escorting: { ctx1, ctx2, ctx3 }");
				i++;
				data = data + time+" "+action.getparameterName()+" "+action.getvalue()+" "+escortingHistory+" "+newline;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return data;
	}

	
	private String getValueFromAttr(CtxHistoryAttribute attr){
		
		String result = "";
		System.out.println("getValueFromAttr   attr.getId() " +attr.getId());
		if(attr.getValueType().equals(CtxAttributeValueType.STRING)){
			result = attr.getStringValue();
			System.out.println("getValueFromAttr  attr.getStringValue() " +result);
			return result;
		} else if (attr.getValueType().equals(CtxAttributeValueType.INTEGER)){
			Integer intResult = attr.getIntegerValue();
			result = String.valueOf(intResult);
			System.out.println("getValueFromAttr  attr.getIntegerValue() " +result);
			return result;
		}	
		
		return result;
		
	}
	
	
	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = commManager.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = commManager.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}
}
