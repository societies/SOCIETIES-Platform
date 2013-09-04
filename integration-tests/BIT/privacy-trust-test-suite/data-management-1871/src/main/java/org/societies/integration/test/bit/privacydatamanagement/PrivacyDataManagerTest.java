/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.integration.test.bit.privacydatamanagement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.DataWrapperFactory;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.LocationCoordinatesUtils;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.NameUtils;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.DataWrapper;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.LocationCoordinates;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.Name;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ActionConstants;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.integration.test.IntegrationTest;
import org.societies.integration.test.userfeedback.UserFeedbackMockResult;
import org.societies.integration.test.userfeedback.UserFeedbackType;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyDataManagerTest extends IntegrationTest
{
	private static Logger LOG = LoggerFactory.getLogger(PrivacyDataManagerTest.class);

	private DataIdentifier dataId;
	private DataIdentifier dataId2;
	private DataIdentifier cisPublicDataId;
	private DataIdentifier cisPrivateDataId;
	private IIdentity myCssId;
	private IIdentity otherCssId;
	private IIdentity cisPublicId;
	private IIdentity cisPrivateId;
	private RequestorCis requestorCis;
	private RequestorService requestorService;
	private List<Action> actionsRead;
	private List<Condition> conditionsPublic;
	private List<Condition> conditionsMembersOnly;
	private List<Condition> conditionsPrivate;
	private RequestPolicy privacyPolicy;
	private RequestPolicy privacyPolicyMembersOnly;
	private RequestPolicy privacyPolicyPrivate;


	@Before
	public void setUp() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
		// Dependency injection not ready
		if (!TestCase.isDepencyInjectionDone()) {
			throw new PrivacyException("[#"+testCaseNumber+"] [Dependency Injection] PrivacyDataManagerTest not ready");
		}
		// Data
		myCssId = TestCase.commManager.getIdManager().getThisNetworkNode();
		otherCssId =  TestCase.commManager.getIdManager().fromJid("othercss.societies.local");
		cisPublicId =  TestCase.commManager.getIdManager().fromJid("cis-public.societies.local");
		cisPrivateId =  TestCase.commManager.getIdManager().fromJid("cis-private.societies.local");
		requestorCis = getRequestorCis();
		requestorService = getRequestorService();
		// Data Id
		try {
			Random randomer = new Random((new Date()).getTime()); 
			String randomValue1 = ""+randomer.nextInt(200);
			String randomValue2 = ""+randomer.nextInt(200);
			dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId+"/ENTITY/person/1/ATTRIBUTE/name/"+randomValue1);
			dataId2 = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+myCssId+"/ENTITY/person/1/ATTRIBUTE/action/"+randomValue2);
			cisPublicDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPublicId+"/cis-member-list/");
			cisPrivateDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPrivateId+"/cis-member-list/");
			LOG.info("Data id: "+dataId.getUri()+" (scheme: "+dataId.getScheme()+", type: "+dataId.getType()+")");
			LOG.info("Data id 2: "+dataId2.getUri()+" (scheme: "+dataId2.getScheme()+", type: "+dataId2.getType()+")");
			LOG.info("Public Cis Data id: "+cisPublicDataId.getUri()+" (scheme: "+cisPublicDataId.getScheme()+", type: "+cisPublicDataId.getType()+")");
			LOG.info("Private Cis Data id: "+cisPrivateDataId.getUri()+" (scheme: "+cisPrivateDataId.getScheme()+", type: "+cisPrivateDataId.getType()+")");
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e.getMessage()+"\n", e);
			fail("setUp(): DataId creation error "+e.getMessage());
		}

		// - Actions
		actionsRead = new ArrayList<Action>();
		actionsRead.add(new Action(ActionConstants.READ));

		// - Conditions
		conditionsPublic = new ArrayList<Condition>();
		conditionsPublic.add(new Condition(ConditionConstants.SHARE_WITH_3RD_PARTIES, "1"));
		conditionsPublic.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));

		conditionsMembersOnly = new ArrayList<Condition>();
		conditionsMembersOnly.add(new Condition(ConditionConstants.SHARE_WITH_CIS_MEMBERS_ONLY, "1"));
		conditionsMembersOnly.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));

		conditionsPrivate = new ArrayList<Condition>();
		conditionsPrivate.add(new Condition(ConditionConstants.SHARE_WITH_CIS_OWNER_ONLY, "1"));
		conditionsPrivate.add(new Condition(ConditionConstants.STORE_IN_SECURE_STORAGE, "1"));

		// - Privacy Policy
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		requestItems.add(new RequestItem(new Resource(cisPublicDataId), actionsRead, conditionsPublic));
		List<RequestItem> requestItemsMembersOnly = new ArrayList<RequestItem>();
		requestItemsMembersOnly.add(new RequestItem(new Resource(cisPublicDataId), actionsRead, conditionsMembersOnly));
		List<RequestItem> requestItemsPrivate = new ArrayList<RequestItem>();
		requestItemsPrivate.add(new RequestItem(new Resource(cisPublicDataId), actionsRead, conditionsPrivate));

		privacyPolicy = new RequestPolicy(new RequestorCis(myCssId, cisPublicId), requestItems);
		privacyPolicyMembersOnly = new RequestPolicy(new RequestorCis(myCssId, cisPublicId), requestItemsMembersOnly);
		privacyPolicyPrivate = new RequestPolicy(new RequestorCis(myCssId, cisPublicId), requestItemsPrivate);
	}

	@After
	public void tearDown() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::tearDown");
	}

	/* --- CHECK PERMISSION CSS --- */

	@Test
	public void testCheckPermissionFirstTime()
	{
		String testTitle = new String("CheckPermission: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		List<ResponseItem> permissions = null;
		try {
			// Random Data ID
			//			DataIdentifier dataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CONTEXT+"://"+currentJid+"/ENTITY/person/1/ATTRIBUTE/name/13");
			Random randomer = new Random((new Date()).getTime()); 
			String randomValue = ""+randomer.nextInt(200);
			DataIdentifier randomDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+myCssId+"/"+randomValue);
			TestCase.getUserFeedbackMocker().addReply(UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
			permissions = TestCase.privacyDataManager.checkPermission(requestorCis, randomDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("[#"+testCaseNumber+"] [MalformedCtxIdentifierException] "+testTitle, e);
			fail("MalformedCtxIdentifierException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		assertNotNull("No (real) permission retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());
	}

	@Test
	public void testCheckPermissionPreviouslyAdded()
	{
		String testTitle = new String("CheckPermission: retrieve a privacy two times");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		try {
			TestCase.getUserFeedbackMocker().addReply(UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "READ"));
			permissions1 = TestCase.privacyDataManager.checkPermission(requestorCis, dataId, actionsRead);
			TestCase.getUserFeedbackMocker().removeAllReplies(); // Just to be sure
			permissions2 = TestCase.privacyDataManager.checkPermission(requestorCis, dataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No permission retrieved", permissions1);
		assertTrue("No permission retrieved", permissions1.size() > 0);
		assertNotNull("No (real) permission retrieved", permissions1.get(0).getDecision());
		assertEquals("Bad permission retrieved",  Decision.PERMIT.name(), permissions1.get(0).getDecision().name());
		assertNotNull("No permission retrieved", permissions2);
		assertNotNull("No (real) permission retrieved", permissions2.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions2.get(0).getDecision().name());
		assertEquals("Two requests, not the same answer", permissions1.get(0).toXMLString(), permissions2.get(0).toXMLString());
	}
	
	@Test
	public void testCheckPermissionDenied()
	{
		String testTitle = new String("CheckPermission denied result: retrieve a decision two times, the first one is DENIED by the user");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		try {
			TestCase.getUserFeedbackMocker().addReply(UserFeedbackType.CHECKBOXLIST, new UserFeedbackMockResult(1, "WROOONG"));
			permissions1 = TestCase.privacyDataManager.checkPermission(requestorCis, dataId2, actionsRead);
			TestCase.getUserFeedbackMocker().removeAllReplies(); // Just to be sure
			permissions2 = TestCase.privacyDataManager.checkPermission(requestorCis, dataId2, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException "+testTitle+": "+e);
		}
		assertNotNull("No permission retrieved", permissions1);
		assertTrue("No permission retrieved", permissions1.size() > 0);
		assertNotNull("No (real) permission retrieved", permissions1.get(0).getDecision());
		assertEquals("Bad permission retrieved",  Decision.DENY.name(), permissions1.get(0).getDecision().name());
		assertNotNull("No permission retrieved", permissions2);
		assertNotNull("No (real) permission retrieved", permissions2.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissions2.get(0).getDecision().name());
		assertEquals("Two requests, not the same answer", permissions1.get(0).toXMLString(), permissions2.get(0).toXMLString());
	}

	/* --- CHECK PERMISSION CIS --- */

	@Test
	public void testCheckPermissionPublicCis()
	{
		String testTitle = new String("CheckPermission public Cis: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		List<ResponseItem> permissions = null;
		try {
			privacyPolicyAdded = TestCase.privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
			permissions = TestCase.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			privacyPolicyDeleted = TestCase.privacyPolicyManager.deletePrivacyPolicy(privacyPolicy.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicy.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("No permission retrieved", permissions);
		assertTrue("No permission retrieved", permissions.size() > 0);
		assertNotNull("No (real) permission retrieved", permissions.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions.get(0).getDecision().name());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}

	@Test
	public void testCheckPermissionPublicCisPreviouslyAdded()
	{
		String testTitle = new String("CheckPermission public Cis: retrieve a privacy two times");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		try {
			privacyPolicyAdded = TestCase.privacyPolicyManager.updatePrivacyPolicy(privacyPolicy);
			permissions1 = TestCase.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			permissions2 = TestCase.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			privacyPolicyDeleted = TestCase.privacyPolicyManager.deletePrivacyPolicy(privacyPolicy.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicy.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("No permission retrieved", permissions1);
		assertTrue("No permission retrieved", permissions1.size() > 0);
		assertNotNull("No (real) permission retrieved", permissions1.get(0).getDecision());
		assertEquals("Bad permission retrieved",  Decision.PERMIT.name(), permissions1.get(0).getDecision().name());
		assertNotNull("No permission retrieved", permissions2);
		assertTrue("No permission retrieved", permissions2.size() > 0);
		assertNotNull("No (real) permission retrieved", permissions2.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissions2.get(0).getDecision().name());
		assertEquals("Two requests, not the same answer", permissions1.get(0).toXMLString(), permissions2.get(0).toXMLString());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}

	@Test
	public void testCheckPermissionPrivateCis()
	{
		String testTitle = new String("CheckPermission Private Cis: retrieve a privacy for the first time");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		List<ResponseItem> permissionOthers = null;
		List<ResponseItem> permissionMes = null;
		try {
			privacyPolicyAdded = TestCase.privacyPolicyManager.updatePrivacyPolicy(privacyPolicyPrivate);
			LOG.info("[#"+testCaseNumber+"] Requested by: "+requestorService);
			permissionOthers = TestCase.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			LOG.info("[#"+testCaseNumber+"] Requested by me: "+myCssId.getJid());
			permissionMes = TestCase.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			privacyPolicyDeleted = TestCase.privacyPolicyManager.deletePrivacyPolicy(privacyPolicyPrivate.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicyPrivate.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("No permission retrieved", permissionOthers);
		assertTrue("No permission retrieved", permissionOthers.size() > 0);
		assertNotNull("No (real) permission retrieved", permissionOthers.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissionOthers.get(0).getDecision().name());

		assertNotNull("No permission retrieved", permissionMes);
		assertTrue("No permission retrieved", permissionMes.size() > 0);
		assertNotNull("No (real) permission retrieved", permissionMes.get(0).getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissionMes.get(0).getDecision().name());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}

	@Test
	public void testCheckPermissionPrivateCisPreviouslyAdded()
	{
		String testTitle = new String("CheckPermission Private Cis: retrieve a privacy two times");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		RequestPolicy privacyPolicyAdded = null;
		boolean privacyPolicyDeleted = false;
		List<ResponseItem> permissionOther1s = null;
		List<ResponseItem> permissionMe1s = null;
		List<ResponseItem> permissionOther2s = null;
		List<ResponseItem> permissionMe2s = null;
		try {
			privacyPolicyAdded = TestCase.privacyPolicyManager.updatePrivacyPolicy(privacyPolicyPrivate);
			permissionOther1s = TestCase.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionMe1s = TestCase.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			permissionOther2s = TestCase.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionMe2s = TestCase.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			privacyPolicyDeleted = TestCase.privacyPolicyManager.deletePrivacyPolicy(privacyPolicyPrivate.getRequestor());
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}
		assertNotNull("No privacy policy added", privacyPolicyAdded);
		assertEquals("Privacy policy added: not the good one", privacyPolicyPrivate.toXMLString(), privacyPolicyAdded.toXMLString());

		assertNotNull("Other: No permission retrieved", permissionOther1s);
		assertTrue("No permission retrieved", permissionOther1s.size() > 0);
		assertNotNull("Other: No (real) permission retrieved", permissionOther1s.get(0).getDecision());
		assertEquals("Other: Bad permission retrieved",  Decision.DENY.name(), permissionOther1s.get(0).getDecision().name());
		assertNotNull("Other: No permission retrieved", permissionOther2s);
		assertTrue("No permission retrieved", permissionOther2s.size() > 0);
		assertNotNull("Other: No (real) permission retrieved", permissionOther2s.get(0).getDecision());
		assertEquals("Other: Bad permission retrieved", Decision.DENY.name(), permissionOther2s.get(0).getDecision().name());
		assertEquals("Other: Two requests, not the same answer", permissionOther1s.get(0).toXMLString(), permissionOther2s.get(0).toXMLString());

		assertNotNull("Me: No permission retrieved", permissionMe1s);
		assertTrue("No permission retrieved", permissionMe1s.size() > 0);
		assertNotNull("Me: No (real) permission retrieved", permissionMe1s.get(0).getDecision());
		assertEquals("Me: Bad permission retrieved",  Decision.PERMIT.name(), permissionMe1s.get(0).getDecision().name());
		assertNotNull("Me: No permission retrieved", permissionMe2s);
		assertTrue("No permission retrieved", permissionMe2s.size() > 0);
		assertNotNull("Me: No (real) permission retrieved", permissionMe2s.get(0).getDecision());
		assertEquals("Me: Bad permission retrieved", Decision.PERMIT.name(), permissionMe2s.get(0).getDecision().name());
		assertEquals("Me: Two requests, not the same answer", permissionMe1s.get(0).toXMLString(), permissionMe2s.get(0).toXMLString());

		assertTrue("Privacy policy not deleted", privacyPolicyDeleted);
	}


	/* --- OBFUSCATION --- */

	@Test
	public void testObfuscateDataName()
	{
		String testTitle = "ObfuscateData: name";
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		try {
			DataWrapper wrapper = DataWrapperFactory.getNameWrapper("Olivier", "Maridat");
			Future<DataWrapper> obfuscatedDataWrapperAsync = TestCase.privacyDataManager.obfuscateData(RequestorUtils.toRequestorBean(requestorCis), wrapper);
			DataWrapper obfuscatedDataWrapper = obfuscatedDataWrapperAsync.get();
			// Verify
			assertNotNull("Obfuscated data null", obfuscatedDataWrapper);
			Name originalData = DataWrapperFactory.retrieveName(wrapper);
			Name obfuscatedData = DataWrapperFactory.retrieveName(obfuscatedDataWrapper);
			LOG.info("[#"+testCaseNumber+"] Orginal name: "+NameUtils.toString(originalData));
			LOG.info("[#"+testCaseNumber+"] Obfuscated name: "+NameUtils.toString(obfuscatedData));
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
		catch (InterruptedException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async error] "+testTitle, e);
			fail("InterruptedException async error ("+e.getMessage()+") "+testTitle);
		}
		catch (ExecutionException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async exec error] "+testTitle, e);
			fail("InterruptedException async exec error ("+e.getMessage()+") "+testTitle);
		}
	}

	@Test
	public void testObfuscateDataLocationCoordinates()
	{
		String testTitle = new String("ObfuscateData: coordinate location");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		try {
			DataWrapper wrapper = DataWrapperFactory.getLocationCoordinatesWrapper(48.856666, 2.350987, 542.0);
			Future<DataWrapper> obfuscatedDataWrapperAsync = TestCase.privacyDataManager.obfuscateData(RequestorUtils.toRequestorBean(requestorCis), wrapper);
			DataWrapper obfuscatedDataWrapper = obfuscatedDataWrapperAsync.get();
			// Verify
			LocationCoordinates originalData = DataWrapperFactory.retrieveLocationCoordinates(wrapper);
			LocationCoordinates obfuscatedData = DataWrapperFactory.retrieveLocationCoordinates(obfuscatedDataWrapper);
			assertNotNull("Obfuscated data should not be null", obfuscatedDataWrapper);
			LOG.info("[#"+testCaseNumber+"] Orginal location:\n"+LocationCoordinatesUtils.toJsonString(originalData));
			LOG.info("[#"+testCaseNumber+"] Obfuscated location:\n"+LocationCoordinatesUtils.toJsonString(obfuscatedData));
		}
		catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException obfuscator error] "+testTitle, e);
			fail("PrivacyException obfuscator error ("+e.getMessage()+") "+testTitle);
		}
		catch (InterruptedException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async error] "+testTitle, e);
			fail("InterruptedException async error ("+e.getMessage()+") "+testTitle);
		}
		catch (ExecutionException e) {
			LOG.error("[#"+testCaseNumber+"] [InterruptedException async exec error] "+testTitle, e);
			fail("InterruptedException async exec error ("+e.getMessage()+") "+testTitle);
		}
	}



	/* ****************************
	 *            Tools           *
	 ******************************/

	private RequestorService getRequestorService() throws InvalidFormatException, URISyntaxException{
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://"+otherCssId+"/HelloEarth");
		serviceId.setIdentifier(new URI("css://"+otherCssId+"/HelloEarth"));
		return new RequestorService(otherCssId, serviceId);
	}

	private RequestorCis getRequestorCis() throws InvalidFormatException{
		return new RequestorCis(otherCssId, cisPublicId);
	}
}