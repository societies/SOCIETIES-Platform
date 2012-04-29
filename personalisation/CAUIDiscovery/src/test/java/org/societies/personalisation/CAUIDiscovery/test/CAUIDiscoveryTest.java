package org.societies.personalisation.CAUIDiscovery.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;

public class CAUIDiscoveryTest {

	ICtxBroker ctxBroker = null;

	CAUIDiscovery discovery = null;


	IndividualCtxEntity operator = null;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {


	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		discovery = new CAUIDiscovery();
		discovery.setCtxBroker(new InternalCtxBroker());
		discovery.setCauiTaskManager(new CAUITaskManager());

		ctxBroker = discovery.getCtxBroker();
		createHistorySet();
	}

	void createHistorySet(){

		//IIdentity identity = new MockIdentity(IdentityType.CSS, "sarah", "societies.org");

		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("testServiceId"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		try {
			operator = ctxBroker.retrieveCssOperator().get();

			// primary attribute
			CtxAttribute actAttr = ctxBroker.createAttribute(operator.getId(), "Action").get();
			actAttr.setHistoryRecorded(true);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();

			//escorting attributes
			CtxAttribute statusAttr = ctxBroker.createAttribute(operator.getId(), "Status").get();
			statusAttr.setHistoryRecorded(true);
			statusAttr =  ctxBroker.updateAttribute(statusAttr.getId(),(Serializable)"free").get();

			CtxAttribute tempAttr = ctxBroker.createAttribute(operator.getId(), "Temperature").get();
			tempAttr.setHistoryRecorded(true);
			tempAttr =  ctxBroker.updateAttribute(tempAttr.getId(),(Serializable)"hot").get();

			// set history tuples
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			listOfEscortingAttributeIds.add(statusAttr.getId());
			listOfEscortingAttributeIds.add(tempAttr.getId());
			ctxBroker.setHistoryTuples(actAttr.getId(), listOfEscortingAttributeIds).get();	

			//primary attribute value
			IAction action1 = new Action(serviceId, "testService", "volume", "high");
			byte[] binaryAction1  = SerialisationHelper.serialise(action1);
			actAttr.setBinaryValue(binaryAction1);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();

			IAction action2 = new Action(serviceId, "testService", "volume", "low");
			byte[] binaryAction2 = SerialisationHelper.serialise(action2);
			actAttr.setBinaryValue(binaryAction2);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();

			IAction action3 = new Action(serviceId, "testService", "volume", "mute");
			byte[] binaryAction3 = SerialisationHelper.serialise(action3);
			actAttr.setBinaryValue(binaryAction3);
			actAttr = (CtxAttribute) ctxBroker.update(actAttr).get();

			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = ctxBroker.retrieveHistoryTuples(actAttr.getId(), listOfEscortingAttributeIds, null, null).get();

			System.out.println("hoc tuple results size "+tupleResults.size());
			System.out.println("hoc tuple results "+tupleResults);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGenerateNewUserModel() {

		discovery.generateNewUserModel();
	}

	@Ignore
	@Test
	public void testGetDictionary() {


	}

	@Ignore
	@Test
	public void testFindOccurences() {
		fail("Not yet implemented");
	}

}
