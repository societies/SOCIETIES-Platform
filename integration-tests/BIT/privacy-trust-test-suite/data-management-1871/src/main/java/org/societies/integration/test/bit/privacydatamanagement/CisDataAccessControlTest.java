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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.model.CisAttributeTypes;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
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
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.ServiceUtils;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class CisDataAccessControlTest {
	private static final Logger LOG = LoggerFactory.getLogger(CisDataAccessControlTest.class.getName());
	public static Integer testCaseNumber = 0;

	private static DataIdentifier cisPublicDataId;
	private static DataIdentifier cisMembersOnlyDataId;
	private static DataIdentifier cisPrivateDataId;
	private static IIdentity myCssId;
	private static IIdentity memberCssId;
	private static IIdentity otherCssId;
	private static ICisOwned cisPublic;
	private static ICisOwned cisPrivate;
	private static ICisOwned cisMembersOnly;
	private static RequestorService requestorService;
	private static List<Action> actionsRead;
	private static List<Condition> conditionsPublic;
	private static List<Condition> conditionsMembersOnly;
	private static List<Condition> conditionsPrivate;
	private static RequestPolicy privacyPolicyPublic;
	private static RequestPolicy privacyPolicyMembersOnly;
	private static RequestPolicy privacyPolicyPrivate;


	@BeforeClass
	public static void setUpClass() throws PrivacyException, InvalidFormatException, URISyntaxException, InterruptedException, ExecutionException, CommunicationException
	{
		LOG.info("[#"+testCaseNumber+"] CisDataAccessControl::setUpClass");
		// Dependency injection not ready
		if (!TestCase.isDepencyInjectionDone()) {
			LOG.error("setUpClass(): [Dependency Injection] PrivacyDataManagerTest not ready");
			fail("setUpClass(): [Dependency Injection] PrivacyDataManagerTest not ready");
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
		try {
			// - Privacy Policy
			List<RequestItem> requestItemsPublic = new ArrayList<RequestItem>();
			requestItemsPublic.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRead, conditionsPublic));
			List<RequestItem> requestItemsMembersOnly = new ArrayList<RequestItem>();
			requestItemsMembersOnly.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRead, conditionsMembersOnly));
			List<RequestItem> requestItemsPrivate = new ArrayList<RequestItem>();
			requestItemsPrivate.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRead, conditionsPrivate));

			privacyPolicyPublic = new RequestPolicy(requestItemsPublic);
			privacyPolicyMembersOnly = new RequestPolicy(requestItemsMembersOnly);
			privacyPolicyPrivate = new RequestPolicy(requestItemsPrivate);

			// - Identities
			myCssId = TestCase.commManager.getIdManager().getThisNetworkNode();
			memberCssId =  TestCase.commManager.getIdManager().fromJid("membercss.ict-societies.eu");
			otherCssId =  TestCase.commManager.getIdManager().fromJid("othercss.ict-societies.eu");
			requestorService = new RequestorService(myCssId, ServiceUtils.generateServiceResourceIdentifierFromString("myGreatService testInstance"));

			// - CIS creation
			String privacyPolicyPublicString = PrivacyPolicyUtils.toXacmlString(RequestPolicyUtils.toRequestPolicyBean(privacyPolicyPublic));
			Future<ICisOwned> cisPublicFuture = TestCase.cisManager.createCis("Public Cis", "1", null, "My Public Cis", privacyPolicyPublicString);
			cisPublic = cisPublicFuture.get();

			Future<ICisOwned> cisMembersOnlyFuture = TestCase.cisManager.createCis("Members only Cis", "1", null, "My Members only Cis", privacyPolicyMembersOnly.toXMLString());
			cisMembersOnly = cisMembersOnlyFuture.get();

			Future<ICisOwned> cisPrivateFuture = TestCase.cisManager.createCis("Private Cis", "1", null, "My Private Cis", privacyPolicyPrivate.toXMLString());
			cisPrivate = cisPrivateFuture.get();

			// - Let memberCssId joins CIS "Members Only Cis"
			cisMembersOnly.addMember(memberCssId.getJid(), "participant");

			// - Data Id
			cisPublicDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPublic.getCisId()+"/"+CisAttributeTypes.MEMBER_LIST);
			cisMembersOnlyDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisMembersOnly.getCisId()+"/"+CisAttributeTypes.MEMBER_LIST);
			cisPrivateDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPrivate.getCisId()+"/"+CisAttributeTypes.MEMBER_LIST);
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUpClass(): DataId creation error "+e+"\n", e);
			fail("setUpClass(): DataId creation error "+e);
		}
		catch (Exception e) {
			LOG.error("setUpClass(): error "+e+"\n", e);
			e.printStackTrace();
			fail("setUpClass(): error "+e+" "+e.toString());
		}
	}

	@Before
	public void setUp() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::setUp");
	}

	@After
	public void tearDown() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] "+getClass().getSimpleName()+"::tearDown");
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
		LOG.info("[#"+testCaseNumber+"] CisDataAccessControl::tearDownClass");
		try {
			TestCase.cisManager.deleteCis(cisPublic.getCisId());
			TestCase.cisManager.deleteCis(cisMembersOnly.getCisId());
			TestCase.cisManager.deleteCis(cisPrivate.getCisId());
		}
		catch(Exception e) {
			LOG.error("tearDownClass(): Can't delete CISs "+e+"\n", e);
			fail("tearDownClass(): Can't delete CISs "+e);
		}
	}


	/* --- CHECK PERMISSION CIS --- */

	@Test
	public void testCheckPermissionPublicCis()
	{
		String testTitle = new String("Check permission public CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		List<ResponseItem> permissions1 = null;
		List<ResponseItem> permissions2 = null;
		try {
			permissions1 = TestCase.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			permissions2 = TestCase.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e+") "+testTitle);
		}

		assertNotNull("First: No permission retrieved", permissions1);
		assertTrue("No permission retrieved", permissions1.size() > 0);
		assertNotNull("First: No (real) permission retrieved", permissions1.get(0).getDecision());
		assertEquals("First: Bad permission retrieved",  Decision.PERMIT.name(), permissions1.get(0).getDecision().name());
		assertNotNull("Second: No permission retrieved", permissions2);
		assertTrue("No permission retrieved", permissions2.size() > 0);
		assertNotNull("Second: No (real) permission retrieved", permissions2.get(0).getDecision());
		assertEquals("Second: Bad permission retrieved", Decision.PERMIT.name(), permissions2.get(0).getDecision().name());
		assertEquals("Two requests, not the same answer", permissions1.get(0).toXMLString(), permissions2.get(0).toXMLString());
	}

	@Test
	public void testCheckPermissionMembersOnlyCis()
	{
		String testTitle = new String("Check permission members only CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		List<ResponseItem> permissionsOther1 = null;
		List<ResponseItem> permissionsMember1 = null;
		List<ResponseItem> permissionsMe1 = null;
		List<ResponseItem> permissionsOther2 = null;
		List<ResponseItem> permissionsMember2 = null;
		List<ResponseItem> permissionsMe2 = null;
		try {
//			permissionsOther1 = TestCase.privacyDataManager.checkPermission(requestorService, cisMembersOnlyDataId, actionsRead);
			permissionsOther1 = TestCase.privacyDataManager.checkPermission(new Requestor(otherCssId), cisMembersOnlyDataId, actionsRead);
			permissionsMember1 = TestCase.privacyDataManager.checkPermission(new Requestor(memberCssId), cisMembersOnlyDataId, actionsRead);
			permissionsMe1 = TestCase.privacyDataManager.checkPermission(new Requestor(myCssId), cisMembersOnlyDataId, actionsRead);
			permissionsOther2 = TestCase.privacyDataManager.checkPermission(new Requestor(otherCssId), cisMembersOnlyDataId, actionsRead);
			permissionsMember2 = TestCase.privacyDataManager.checkPermission(new Requestor(memberCssId), cisMembersOnlyDataId, actionsRead);
			permissionsMe2 = TestCase.privacyDataManager.checkPermission(new Requestor(myCssId), cisMembersOnlyDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e+") "+testTitle);
		}

		assertNotNull("Other1: No permission retrieved", permissionsOther1);
		assertTrue("Other1: No permission retrieved", permissionsOther1.size() > 0);
		assertNotNull("Other1: No (real) permission retrieved", permissionsOther1.get(0).getDecision());
		assertEquals("Other1: Bad permission retrieved",  Decision.DENY.name(), permissionsOther1.get(0).getDecision().name());
		assertNotNull("Other2: No permission retrieved", permissionsOther2);
		assertTrue("Other2: No permission retrieved", permissionsOther2.size() > 0);
		assertNotNull("Other2: No (real) permission retrieved", permissionsOther2.get(0).getDecision());
		assertEquals("Other2: Bad permission retrieved", Decision.DENY.name(), permissionsOther2.get(0).getDecision().name());
		assertEquals("Other1-2: Two requests, not the same answer", permissionsOther1.get(0).toXMLString(), permissionsOther2.get(0).toXMLString());

		assertNotNull("Member1: No permission retrieved", permissionsMember1);
		assertTrue("Member1: No permission retrieved", permissionsMember1.size() > 0);
		assertNotNull("Member1: No (real) permission retrieved", permissionsMember1.get(0).getDecision());
		assertEquals("Member1: Bad permission retrieved",  Decision.PERMIT.name(), permissionsMember1.get(0).getDecision().name());
		assertNotNull("Member2: No permission retrieved", permissionsMember2);
		assertTrue("Member2: No permission retrieved", permissionsMember1.size() > 0);
		assertNotNull("Member2: No (real) permission retrieved", permissionsMember2.get(0).getDecision());
		assertEquals("Member2: Bad permission retrieved", Decision.PERMIT.name(), permissionsMember2.get(0).getDecision().name());
		assertEquals("Member1-2: Two requests, not the same answer", permissionsMember1.get(0).toXMLString(), permissionsMember2.get(0).toXMLString());

		assertNotNull("Me1: No permission retrieved", permissionsMe1);
		assertTrue("Me1: No permission retrieved", permissionsMe1.size() > 0);
		assertNotNull("Me1: No (real) permission retrieved", permissionsMe1.get(0).getDecision());
		assertEquals("Me1: Bad permission retrieved",  Decision.PERMIT.name(), permissionsMe1.get(0).getDecision().name());
		assertNotNull("Me2: No permission retrieved", permissionsMe2);
		assertTrue("Me2: No permission retrieved", permissionsMe2.size() > 0);
		assertNotNull("Me2: No (real) permission retrieved", permissionsMe2.get(0).getDecision());
		assertEquals("Me2: Bad permission retrieved", Decision.PERMIT.name(), permissionsMe2.get(0).getDecision().name());
		assertEquals("Me1-2: Two requests, not the same answer", permissionsMe1.get(0).toXMLString(), permissionsMe2.get(0).toXMLString());
	}

	@Test
	public void testCheckPermissionPrivateCis()
	{
		String testTitle = new String("Check permission private CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		List<ResponseItem> permissionsOther1 = null;
		List<ResponseItem> permissionsMe1 = null;
		List<ResponseItem> permissionsOther2 = null;
		List<ResponseItem> permissionsMe2 = null;
		try {
			permissionsOther1 = TestCase.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionsMe1 = TestCase.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			permissionsOther2 = TestCase.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionsMe2 = TestCase.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e+") "+testTitle);
		}

		assertNotNull("Other1: No permission retrieved", permissionsOther1);
		assertTrue("Other1: No permission retrieved", permissionsOther1.size() > 0);
		assertNotNull("Other1: No (real) permission retrieved", permissionsOther1.get(0).getDecision());
		assertEquals("Other1: Bad permission retrieved",  Decision.DENY.name(), permissionsOther1.get(0).getDecision().name());
		assertNotNull("Other2: No permission retrieved", permissionsOther2);
		assertTrue("Other2:No permission retrieved", permissionsOther2.size() > 0);
		assertNotNull("Other2:No (real) permission retrieved", permissionsOther2.get(0).getDecision());
		assertEquals("Other2:Bad permission retrieved", Decision.DENY.name(), permissionsOther2.get(0).getDecision().name());
		assertEquals("Other1-2:Two requests, not the same answer", permissionsOther1.get(0).toXMLString(), permissionsOther2.get(0).toXMLString());

		assertNotNull("Me1: No permission retrieved", permissionsMe1);
		assertTrue("Me1: No permission retrieved", permissionsMe1.size() > 0);
		assertNotNull("Me1: No (real) permission retrieved", permissionsMe1.get(0).getDecision());
		assertEquals("Me1: Bad permission retrieved",  Decision.PERMIT.name(), permissionsMe1.get(0).getDecision().name());
		assertNotNull("Me2: No permission retrieved", permissionsMe2);
		assertTrue("Me2: No permission retrieved", permissionsMe2.size() > 0);
		assertNotNull("Me2: No (real) permission retrieved", permissionsMe2.get(0).getDecision());
		assertEquals("Me2: Bad permission retrieved", Decision.PERMIT.name(), permissionsMe2.get(0).getDecision().name());
		assertEquals("Me1-2: Two requests, not the same answer", permissionsMe1.get(0).toXMLString(), permissionsMe2.get(0).toXMLString());
	}

	/* ****************************
	 *            Tools           *
	 ******************************/

	private static RequestorService getRequestorService() throws InvalidFormatException, URISyntaxException{
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://"+otherCssId+"/HelloEarth");
		serviceId.setIdentifier(new URI("css://"+otherCssId+"/HelloEarth"));
		return new RequestorService(otherCssId, serviceId);
	}
}