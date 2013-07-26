package org.societies.integration.test.bit.userCtxInheritance;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;




public class UserCtxInheritanceTest {

	private static Logger LOG = LoggerFactory.getLogger(UserCtxInheritanceTest.class);

	private IIdentity cssIDUniversity;

	// run test in university's container
	private String targetUniversity = "university.ict-societies.eu";

	public ICtxBroker ctxBroker;
	public ICommManager commManager;
	public ICisManager cisManager;

	public void setUp(){

		LOG.info("UserInheritanceCTX started");

	}

	@Test
	public void TestUserCtxInheritance() {

		LOG.info("TestUserInheritabceCtx");
		this.ctxBroker=Test1999.getCtxBroker();
		this.commManager= Test1999.getCommManager();
		this.cisManager = Test1999.getCisManager();

		LOG.info("Context broker service: "+ this.ctxBroker);
		LOG.info("comm manager service"+ this.commManager);

		try {
			this.cssIDUniversity =  this.commManager.getIdManager().fromJid(targetUniversity);

			// setting university attributes 



			// create CIS
			IIdentity cisID = this.createCIS();

			// at this point a community Entity should be created in university's container
			// at this point an association should be created in university's container

			Thread.sleep(40000);

			CtxEntityIdentifier ctxCommunityEntityIdentifier = this.ctxBroker.retrieveCommunityEntityId(cisID).get();			
			CtxAttribute locationSym1stCis = updateCISStringAttr(cisID, CtxAttributeTypes.LOCATION_SYMBOLIC, "Zone 1");

			//create second CIS
			IIdentity cisId2 = this.createCIS2();
			Thread.sleep(60000);
			CtxEntityIdentifier ctxCommunityEntityIdentifier2 = this.ctxBroker.retrieveCommunityEntityId(cisId2).get();
			CtxAttribute locationSym2ndCis = updateCISStringAttr2(cisId2, CtxAttributeTypes.LOCATION_SYMBOLIC, "Zone 2");
			
			//test inheritance 
			LOG.info("testing inheritance - start");
			CtxAttribute a = ctxBroker.communityInheritance(locationSym1stCis.getId());
			LOG.info("The returned attribute is :"+a);
	
			LOG.info("testing inheritance - end");


		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	

	private CtxAttribute updateCISStringAttr(IIdentity identity, String attributeType, String value){
		
		CtxAttribute attributeUpdated = null;
		CtxAttribute attribute = null;
		try {
		
			List<CtxIdentifier> attributeList = this.ctxBroker.lookup(new Requestor(cssIDUniversity), identity, CtxModelType.ATTRIBUTE, attributeType).get();

			if( attributeList.size() == 0){
				CtxEntityIdentifier entityId = this.ctxBroker.retrieveCommunityEntityId(new Requestor(cssIDUniversity), identity).get();
				attribute = this.ctxBroker.createAttribute(entityId, attributeType).get();

			} else {
				attribute = (CtxAttribute) this.ctxBroker.retrieve(attributeList.get(0)).get();
			}

			attribute.setStringValue(value);
			attributeUpdated = (CtxAttribute) this.ctxBroker.update(attribute).get();
			
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
		//LOG.info("The attribute of the updated CIS String Attr Method is :"+attributeUpdated);
		return attributeUpdated;
	}
	
	private CtxAttribute updateCISStringAttr2(IIdentity identity2, String attributeType, String value){
		CtxAttribute attributeUpdated = null;
		CtxAttribute attribute = null;
		try {
		
			List<CtxIdentifier> attributeList = this.ctxBroker.lookup(new Requestor(cssIDUniversity), identity2, CtxModelType.ATTRIBUTE, attributeType).get();

			if( attributeList.size() == 0){
				CtxEntityIdentifier entityId = this.ctxBroker.retrieveCommunityEntityId(new Requestor(cssIDUniversity), identity2).get();
				attribute = this.ctxBroker.createAttribute(entityId, attributeType).get();

			} else {
				attribute = (CtxAttribute) this.ctxBroker.retrieve(attributeList.get(0)).get();
			}

			attribute.setStringValue(value);
			attributeUpdated = (CtxAttribute) this.ctxBroker.update(attribute).get();
			
			//attributeUpdated = (CtxAttribute) this.ctxBroker.update(new Requestor(cssIDUniversity), attribute);
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
		//LOG.info("The attribute of the updated CIS String Attr Method is :"+attributeUpdated);
		return attributeUpdated;
	}

	
	protected IIdentity createCIS() throws CtxException {
		IIdentity cisID = null;
		try {
			Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria> ();

			LOG.info("*** trying to create cis:");
			ICisOwned cisOwned = this.cisManager.createCis("testCIS-AA", "cisType", cisCriteria, "nice CIS-AA").get();		

			String cisIDString = cisOwned.getCisId();
			cisID = this.commManager.getIdManager().fromJid(cisIDString);
			LOG.info("*** cis created: "+cisOwned.getCisId());
			LOG.info("*** cisOwned " +cisOwned);
			LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());


		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cisID;
	}

	protected IIdentity createCIS2() {
		LOG.info("Iam trying to create the 2nd CIS");
		IIdentity cisID = null;
		try {
			Hashtable<String, MembershipCriteria> cisCriteria2 = new Hashtable<String, MembershipCriteria> ();

			LOG.info("*** trying to create cis-B:");
			ICisOwned cisOwned = this.cisManager.createCis("testCIS-BB", "cisType", cisCriteria2, "nice CIS-BB").get();		

			String cisIDString = cisOwned.getCisId();
			cisID = this.commManager.getIdManager().fromJid(cisIDString);
			LOG.info("*** cis created: "+cisOwned.getCisId());
			LOG.info("*** cisOwned " +cisOwned);
			LOG.info("*** cisOwned.getCisId() " +cisOwned.getCisId());

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cisID;
	}

}
