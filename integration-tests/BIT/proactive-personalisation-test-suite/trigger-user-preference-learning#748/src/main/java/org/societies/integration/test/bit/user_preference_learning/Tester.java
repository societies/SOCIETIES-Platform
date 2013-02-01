package org.societies.integration.test.bit.user_preference_learning;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.junit.*;

public class Tester {
	private IUserActionMonitor uam;
	private IIdentity userId;
	private ICtxBroker ctxBroker;
	private CtxEntity person;
	private CtxAttribute symLocAttribute;
	private CtxAttribute statusAttribute;
	private CtxAttribute activitiesAttribute;
	private IPersonalisationManager personMan;
	// private Logger logging = LoggerFactory.getLogger(this.getClass());
	private IAction action$1;
	private IAction action$2;
	private IIdentityManager idm;
	// private IAction action$3;
	private ServiceResourceIdentifier id;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	public Tester() {

	}

	@Before
	public void setUp() {
		try {
			this.uam = Test748.getUam();
			this.idm = Test748.getCommManager().getIdManager();
			logging.debug("initializing test");
			this.ctxBroker = Test748.getCtxBroker();
			this.userId = idm.getThisNetworkNode();
			id = new ServiceResourceIdentifier();
			id.setIdentifier(new URI("http://ss.ss"));
			id.setServiceInstanceIdentifier("http://ss.ss");
			this.action$1 = new Action(id, "serviceintest", "volume", "0");
			this.action$2 = new Action(id, "serviceintest", "volume", "10");
			logging.debug("initializing actions");
			// this.action$3=new Action(id,"serviceintest","volume","90");
			this.personMan = Test748.getPersonMan();
			setupContext();
			logging.debug("initializing first contexts");
			changeContext("home", "free", "sleep");
			logging.debug("changing contexts");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@org.junit.Test
	public void Test() {
		try {
			for (int i = 1; i < 21; i++) {
				if (i % 2 == 0) {
					uam.monitor(userId, action$1);
				} else if (i % 2 == 1) {
					uam.monitor(userId, action$2);
					// }else if(i%3==2){
					// uam.monitor(userId, action$3);
				}
				Thread.sleep(500);
				if (i % 2 == 0) {
					this.changeContext("home", "busy", "working");
				} else if (i % 2 == 1) {
					this.changeContext("office", "free", "cafe");
				}
				Thread.sleep(500);
			}
			logging.debug("iterating context changes");
			this.changeContext("office", "free", "cafe");
			logging.debug("final context settings");
			Future<IAction> value = this.personMan.getPreference(this.userId,
					"serviceintest", this.id, "volume");
			logging.debug("query for preference learning");
			IAction preference = value.get();
			Assert.assertNotNull(preference);
			logging.debug("get preference learning result");
			Assert.assertNotNull(preference.getvalue());
			Assert.assertEquals(preference.getvalue(),"0");
			logging.debug("check the result");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void changeContext(String symLocValue, String statusValue,
			String activityValue) {
		try {
			this.symLocAttribute = this.ctxBroker.updateAttribute(this.symLocAttribute.getId(), symLocValue).get();
			
			/* 31/1/2013: Eliza replaced the following lines with the above. 
			 * Lines below assume that symlocAttribute is not changed in the DB which causes a conflict 
			 * if another component has updated this attribute after it was retrieved by this instance
			 */
			
			/*this.symLocAttribute.setStringValue(symLocValue);
			this.symLocAttribute = (CtxAttribute) this.ctxBroker.update(
					symLocAttribute).get();*/

			/*
			 * 31/1/2013: same as above
			 */
			
			this.statusAttribute = this.ctxBroker.updateAttribute(this.statusAttribute.getId(), statusValue).get();
			/*this.statusAttribute.setStringValue(statusValue);
			this.statusAttribute = (CtxAttribute) this.ctxBroker.update(
					statusAttribute).get();*/
			
			/*
			 * 31/1/2013: same as above
			 */
			
			this.activitiesAttribute = this.ctxBroker.updateAttribute(this.activitiesAttribute.getId(), activityValue).get();
			/*this.activitiesAttribute.setStringValue(activityValue);
			this.activitiesAttribute = (CtxAttribute) this.ctxBroker.update(
					activitiesAttribute).get();*/
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

	/**
	 * PreTest setup:
	 */

	private void setupContext() {
		logging.debug("get person entity");
		this.getPersonEntity();
		logging.debug("get sym loc");
		this.getSymLocAttribute();
		logging.debug("get stat attr");
		this.getStatusAttribute();
		logging.debug("get activities");
		this.getActivitiesAttribute();

	}

	//
	private void getPersonEntity() {
		try {
			Future<List<CtxIdentifier>> futurePersons = this.ctxBroker.lookup(
					CtxModelType.ENTITY, CtxEntityTypes.PERSON);
			List<CtxIdentifier> persons = futurePersons.get();
			if (persons.size() == 0) {
				person = this.ctxBroker.createEntity(CtxEntityTypes.PERSON)
						.get();

			} else {
				person = (CtxEntity) this.ctxBroker.retrieve(persons.get(0))
						.get();
			}

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

	private void getSymLocAttribute() {
		try {
			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker
					.lookup(CtxModelType.ATTRIBUTE,
							CtxAttributeTypes.LOCATION_SYMBOLIC);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0) {
				symLocAttribute = this.ctxBroker.createAttribute(
						person.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC)
						.get();
			} else {
				symLocAttribute = (CtxAttribute) this.ctxBroker.retrieve(
						attrs.get(0)).get();
			}

			this.symLocAttribute.setHistoryRecorded(true);
			this.symLocAttribute = (CtxAttribute) this.ctxBroker.update(symLocAttribute).get();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void getStatusAttribute() {
		try {
			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker.lookup(
					CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0) {
				statusAttribute = this.ctxBroker.createAttribute(
						person.getId(), CtxAttributeTypes.STATUS).get();
			} else {
				statusAttribute = (CtxAttribute) this.ctxBroker.retrieve(
						attrs.get(0)).get();
			}

			this.statusAttribute.setHistoryRecorded(true);
			this.statusAttribute = (CtxAttribute) this.ctxBroker.update(statusAttribute).get();
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

	private void getActivitiesAttribute() {
		try {
			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker.lookup(
					CtxModelType.ATTRIBUTE, CtxAttributeTypes.ACTION);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0) {
				activitiesAttribute = this.ctxBroker.createAttribute(
						person.getId(), CtxAttributeTypes.ACTION).get();
			} else {
				activitiesAttribute = (CtxAttribute) this.ctxBroker.retrieve(
						attrs.get(0)).get();
			}

			this.activitiesAttribute.setHistoryRecorded(true);
			this.activitiesAttribute = (CtxAttribute) this.ctxBroker.update(activitiesAttribute).get();
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
}
