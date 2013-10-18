package org.societies.integration.test.bit.comm_ctx_access;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeComplexValue;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

public class Tester {

	private static Logger LOG = LoggerFactory.getLogger(Tester.class);

	private IIdentity cssIDUniversity;
	private IIdentity cssIDEmma;

	// run test in university's container
	private static final String UNIVERSITY_ID = "university.ict-societies.eu";
	private static final String EMMA_ID= "emma.ict-societies.eu";

	private Hashtable<String, List<CisAdvertisementRecord>> resultTable = new Hashtable<String, List<CisAdvertisementRecord>>();
	List<CisAdvertisementRecord> cisAdvertisements;

	private IIdentity cisIdentity1 = null;

	private static final String MEMBERS_ONLY = "SHARE_WITH_CIS_MEMBERS_ONLY";
	private static final String PUBLIC = "SHARE_WITH_3RD_PARTIES";

	//private IndividualCtxEntity emma;
	private IndividualCtxEntity university;

	public ICtxBroker internalCtxBroker;
	public ICommManager commManager;
	public ICisManager cisManager;
	private ICisDirectoryRemote cisDirectory;


	public void setUp(){
		LOG.info("EstimateCommunityCtx started");

	}

	@After
	public void tearDown() throws Exception {


	}




	@Test
	public void TestCtx() throws Exception  {

		this.internalCtxBroker = Test2168.getCtxBroker();
		this.commManager= Test2168.getCommManager();
		this.cisManager = Test2168.getCisManager();
		this.cisDirectory = Test2168.getCisDirectory();


		LOG.info("Test starts  " +getOwnerId().getBareJid());



		if(getOwnerId().getBareJid().equals(UNIVERSITY_ID)){
			LOG.info("Test starts for  "+UNIVERSITY_ID );
			uniScenario2();
		}

		if(getOwnerId().getBareJid().equals(EMMA_ID)){
			LOG.info("Test starts for  "+EMMA_ID );
			emmaScenario();
		}




	}





	public void uniScenario() throws Exception{

		// create attribute language
		List<CtxIdentifier> langList = this.internalCtxBroker.lookup(this.getOwnerId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.LANGUAGES).get();
		CtxAttributeIdentifier langAttributeId = null;
		CtxAttribute languageAttr = null;

		if (langList.size()>0) {
			langAttributeId = (CtxAttributeIdentifier) langList.get(0);
			languageAttr = (CtxAttribute) this.internalCtxBroker.retrieve(langAttributeId).get();

		} else {
			CtxEntityIdentifier entID = this.internalCtxBroker.retrieveIndividualEntityId(null, this.getOwnerId()).get();
			languageAttr = this.internalCtxBroker.createAttribute(entID, CtxAttributeTypes.LANGUAGES).get();
		}

		languageAttr.setStringValue("English");

		this.internalCtxBroker.update(languageAttr);


		this.cisIdentity1 = this.createCis("testCIS1", new String[] {
				CtxAttributeTypes.LANGUAGES, CtxAssociationTypes.HAS_MEMBERS }, MEMBERS_ONLY);
		LOG.info("*** setUp:  MEMBERS_ONLYCisId={}", this.cisIdentity1);




	}



	public void uniScenario2() throws Exception{

		List<CtxEntityIdentifier> listCommEntID  = retrieveBelongingCIS();


		if( !listCommEntID.isEmpty()){
			CtxEntityIdentifier commEntID = listCommEntID.get(0);

			LOG.info("******** lookup community entity identifier  ********  "+commEntID);

			List<CtxIdentifier> langCommList = this.internalCtxBroker.lookup(commEntID, CtxModelType.ATTRIBUTE,CtxAttributeTypes.LANGUAGES ).get();

			LOG.info("******** lookup community context langCommList ********  "+langCommList);
			if(!langCommList.isEmpty()){

				LOG.info("************ :  retrieving comm attr ");
				CtxAttribute commLangAttribute = (CtxAttribute) this.internalCtxBroker.retrieve(langCommList.get(0)).get();
				LOG.info("**** commLangAttribute id : " + commLangAttribute.getId());
				LOG.info("************ :  commLangAttribute value type:"+ commLangAttribute.getValueType());
				
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue());
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue().getPairs());
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue().getAverage());
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue().getMode());



			}

		}




	}

	public void emmaScenario() throws InterruptedException, ExecutionException, CtxException, IOException, ClassNotFoundException {

		cssIDEmma = this.getOwnerId();
		LOG.info("emma id : "+cssIDEmma.getBareJid());

		List<CtxIdentifier> langList = this.internalCtxBroker.lookup(cssIDEmma, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LANGUAGES).get();
		CtxAttributeIdentifier langAttributeId = null;
		CtxAttribute languageAttr = null;

		if (langList.size()>0) {
			langAttributeId = (CtxAttributeIdentifier) langList.get(0);
			languageAttr = (CtxAttribute) this.internalCtxBroker.retrieve(langAttributeId).get();

		} else {
			CtxEntityIdentifier entID = this.internalCtxBroker.retrieveIndividualEntityId(null, cssIDEmma).get();
			languageAttr = this.internalCtxBroker.createAttribute(entID, CtxAttributeTypes.LANGUAGES).get();
		}
		languageAttr.setStringValue("English,Spanish,Italian");
		//languageAttr = (CtxAttribute) this.internalCtxBroker.update(languageAttr).get();



		

		List<CtxEntityIdentifier> listCommEntID  = retrieveBelongingCIS();


		if( !listCommEntID.isEmpty()){
			CtxEntityIdentifier commEntID = listCommEntID.get(0);

			LOG.info("******** lookup community entity identifier  ********  "+commEntID);

			List<CtxIdentifier> langCommList = this.internalCtxBroker.lookup(commEntID, CtxModelType.ATTRIBUTE,CtxAttributeTypes.LANGUAGES ).get();

			LOG.info("******** lookup community context langCommList ********  "+langCommList);
			if(!langCommList.isEmpty()){

				LOG.info("************ :  retrieving comm attr ");
				CtxAttribute commLangAttribute = (CtxAttribute) this.internalCtxBroker.retrieve(langCommList.get(0)).get();
				byte[] binaryValue = commLangAttribute.getBinaryValue();
				 CtxAttributeComplexValue complexValue = (CtxAttributeComplexValue) SerialisationHelper.deserialise(binaryValue, this.getClass().getClassLoader());
				
				 
				 
				 LOG.info("**** commLangAttribute complexValue: " + complexValue);
				 LOG.info("**** commLangAttribute complexValue : " + complexValue.getPairs());
				 
				 
				 
				 LOG.info("**** commLangAttribute id : " + commLangAttribute.getId());
				
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue());
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue().getPairs());
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue().getAverage());
				LOG.info("************ :  commLangAttribute complex value: "+ commLangAttribute.getComplexValue().getMode());



			}

		}



		//	this.cisDirectory.findAllCisAdvertisementRecords(new CisDirectoryCallback(uuid, UNIVERSITY_ID));

	}



	public List<CtxEntityIdentifier> retrieveBelongingCIS(){

		List<CtxEntityIdentifier> commEntIDList = new ArrayList<CtxEntityIdentifier>();

		List<CtxIdentifier> listISMemberOf = new ArrayList<CtxIdentifier>();

		final INetworkNode cssNodeId = 	this.commManager.getIdManager().getThisNetworkNode();
		final String cssOwnerStr = cssNodeId.getBareJid();
		try {
			IIdentity cssOwnerId = 	this.commManager.getIdManager().fromJid(cssOwnerStr);
			listISMemberOf = this.internalCtxBroker.lookup(cssOwnerId, CtxModelType.ASSOCIATION, CtxAssociationTypes.IS_MEMBER_OF).get();
			LOG.info(".............listISMemberOf................." +listISMemberOf);

			if(!listISMemberOf.isEmpty() ){
				CtxAssociation assoc = (CtxAssociation) this.internalCtxBroker.retrieve(listISMemberOf.get(0)).get();
				Set<CtxEntityIdentifier> entIDSet = assoc.getChildEntities();
				commEntIDList.addAll(entIDSet);
				LOG.info(".............listISMemberOf.................2: " +commEntIDList);
				/*
				for(CtxEntityIdentifier entId : entIDSet){
					IIdentity cisId = this.commManager.getIdManager().fromJid(entId.getOwnerId());
					LOG.debug("cis id : "+cisId );
					CtxEntityIdentifier commId = this.internalCtxBroker.retrieveCommunityEntityId(cisId).get();
					commEntIDList.add(commId);
				}
				 */
			}

		} catch (Exception e) {
			LOG.error("Unable to retrieve CISids that css belongs to " +e.getLocalizedMessage());
		} 

		return commEntIDList;
	}





	public List<CisAdvertisementRecord> getCisListByOwner(String ownerID){

		if (ownerID==null){
			return new ArrayList<CisAdvertisementRecord>();
		}
		this.cisAdvertisements = new ArrayList<CisAdvertisementRecord>();

		String uuid = UUID.randomUUID().toString();

		this.cisDirectory.findAllCisAdvertisementRecords(new CisDirectoryCallback(uuid, ownerID));

		LOG.debug("Asked cisDirectory CISs of: "+ownerID);

		while (!this.resultTable.containsKey(uuid)){
			synchronized (this.resultTable) {
				try {
					LOG.debug("Waiting for result");
					this.resultTable.wait();

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		LOG.debug("Retrieved "+cisAdvertisements.size()+ "CISs of "+ownerID);
		return cisAdvertisements;
	}





	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = this.commManager.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId =  this.commManager.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}

	/**
	 *
	 * @param name
	 * @param attrTypes
	 * @param mode {@link #MEMBERS_ONLY} or {@link TestLocalCommunityContext#PUBLIC}
	 * @return
	 * @throws Exception
	 */
	private IIdentity createCis(String name, String[] attrTypes, String mode) throws Exception {

		final Hashtable<String, MembershipCriteria> cisCriteria =
				new Hashtable<String, MembershipCriteria> ();
		LOG.info("*** createCis: name='{}'", name);
		final ICisOwned cisOwned =
				this.cisManager.createCis(name, "cisType", cisCriteria, "nice CIS",
						this.createPrivacyPolicy(attrTypes, mode)).get();
		final String cisIdStr = cisOwned.getCisId();

		return this.commManager.getIdManager().fromJid(cisIdStr);
	}






	/**
	 *
	 * @param attrTypes
	 * @param mode {@link #MEMBERS_ONLY} or {@link TestLocalCommunityContext#PUBLIC}
	 * @return
	 */
	private String createPrivacyPolicy(String[] attrTypes, String mode) {

		final StringBuilder sb = new StringBuilder();
		sb.append("<RequestPolicy>"
				+ "<Target>"
				+ "<Resource>"
				+ "<Attribute AttributeId=\"cis\""
				+ " DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
				+ "<AttributeValue>cis-member-list</AttributeValue>"
				+ "</Attribute>"
				+ "</Resource>"
				+ "<Action>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\""
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
				+ "<AttributeValue>READ</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Action>"
				+ "<Action>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\""
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
				+ "<AttributeValue>CREATE</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Action>"
				+ "<Condition>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\""
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
				+ "<AttributeValue DataType=\"RIGHT_TO_OPTOUT\">1</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Condition>"
				+ "<Condition>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\""
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
				+ "<AttributeValue DataType=\"STORE_IN_SECURE_STORAGE\">1</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Condition>"
				+ "<Condition>"
				+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\""
				+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
				+ "<AttributeValue DataType=\"SHARE_WITH_CIS_MEMBERS_ONLY\">1</AttributeValue>"
				+ "</Attribute>"
				+ "<optional>false</optional>"
				+ "</Condition>"
				+ "<optional>false</optional>"
				+ "</Target>");
		for (final String attrType : attrTypes) {
			sb.append("<Target>"
					+ "<Resource>"
					+ "<Attribute AttributeId=\"context\""
					+ " DataType=\"http://www.w3.org/2001/XMLSchema#string\">"
					+ "<AttributeValue>" + attrType + "</AttributeValue>"
					+ "</Attribute>"
					+ "</Resource>"
					+ "<Action>"
					+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\""
					+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
					+ "<AttributeValue>CREATE</AttributeValue>"
					+ "</Attribute>"
					+ "<optional>false</optional>"
					+ "</Action>"
					+ "<Action>"
					+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\""
					+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
					+ "<AttributeValue>READ</AttributeValue>"
					+ "</Attribute>"
					+ "<optional>false</optional>"
					+ "</Action>"
					+ "<Action>"
					+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\""
					+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants\">"
					+ "<AttributeValue>WRITE</AttributeValue>"
					+ "</Attribute>"
					+ "<optional>false</optional>"
					+ "</Action>"
					+ "<Condition>"
					+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\""
					+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
					+ "<AttributeValue DataType=\"RIGHT_TO_OPTOUT\">1</AttributeValue>"
					+ "</Attribute>"
					+ "<optional>false</optional>"
					+ "</Condition>"
					+ "<Condition>"
					+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\""
					+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
					+ "<AttributeValue DataType=\"STORE_IN_SECURE_STORAGE\">1</AttributeValue>"
					+ "</Attribute>"
					+ "<optional>false</optional>"
					+ "</Condition>"
					+ "<Condition>"
					+ "<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:condition-id\""
					+ " DataType=\"org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants\">"
					+ "<AttributeValue DataType=\"" + mode + "\">1</AttributeValue>"
					+ "</Attribute>"
					+ "<optional>false</optional>"
					+ "</Condition>"
					+ "<optional>false</optional>"
					+ "</Target>");
		}
		sb.append("</RequestPolicy>");

		return sb.toString();
	}





	private class CisDirectoryCallback implements ICisDirectoryCallback{


		private String uuid;
		private String ownerID;

		public CisDirectoryCallback(String uuid, String ownerID){
			this.uuid = uuid;
			this.ownerID = ownerID;
		}
		@Override
		public void getResult(List<CisAdvertisementRecord> records) {

			cisAdvertisements.clear();
			if (records!=null){
				LOG.debug("Received result from remote cisDirectory: "+records.size()+" CISs");

				for (CisAdvertisementRecord cisRecord : records){
					//if (cisRecord.getCssownerid().equalsIgnoreCase(ownerID)){
					if (cisRecord.getName().equalsIgnoreCase("testCIS1")){	
						cisAdvertisements.add(cisRecord);

					}

				}

				LOG.debug("From these, "+cisAdvertisements.size()+" are owned by "+ownerID);
				synchronized (resultTable) {
					resultTable.put(uuid, cisAdvertisements);
					resultTable.notifyAll();
				}
			}else{
				LOG.debug("Received null result from remote cisDirectory");

				synchronized (resultTable) {
					resultTable.put(this.uuid, new ArrayList<CisAdvertisementRecord>());
					resultTable.notifyAll();
				}
			}




		}

	}

}