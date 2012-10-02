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
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.DataIdentifierFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Action;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Condition;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.Resource;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.ResponseItem;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ActionConstants;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.ConditionConstants;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class CisDataAccessControlTest
{
	private static Logger LOG = LoggerFactory.getLogger(CisDataAccessControlTest.class.getSimpleName());
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
		if (!TestCase1266.isDepencyInjectionDone()) {
			throw new PrivacyException("[#"+testCaseNumber+"] [Dependency Injection] PrivacyDataManagerTest not ready");
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
		List<RequestItem> requestItemsPublic = new ArrayList<RequestItem>();
		requestItemsPublic.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRead, conditionsPublic));
		List<RequestItem> requestItemsMembersOnly = new ArrayList<RequestItem>();
		requestItemsMembersOnly.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRead, conditionsMembersOnly));
		List<RequestItem> requestItemsPrivate = new ArrayList<RequestItem>();
		requestItemsPrivate.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, "cis-member-list"), actionsRead, conditionsPrivate));

		privacyPolicyPublic = new RequestPolicy(requestItemsPublic);
		privacyPolicyMembersOnly = new RequestPolicy(requestItemsMembersOnly);
		privacyPolicyPrivate = new RequestPolicy(requestItemsPrivate);

		// - CIS creation
		Future<ICisOwned> cisPublicFuture = TestCase1266.cisManager.createCis("Public Cis", "1", null, "My Public Cis", privacyPolicyPublic.toXMLString());
		cisPublic = cisPublicFuture.get();

		Future<ICisOwned> cisMembersOnlyFuture = TestCase1266.cisManager.createCis("Members only Cis", "1", null, "My Members only Cis", privacyPolicyMembersOnly.toXMLString());
		cisMembersOnly = cisMembersOnlyFuture.get();

		Future<ICisOwned> cisPrivateFuture = TestCase1266.cisManager.createCis("Private Cis", "1", null, "My Private Cis", privacyPolicyPrivate.toXMLString());
		cisPrivate = cisPrivateFuture.get();

		// - Identities
		myCssId = TestCase1266.commManager.getIdManager().getThisNetworkNode();
		memberCssId =  TestCase1266.commManager.getIdManager().fromJid("university.societies.local");
		otherCssId =  TestCase1266.commManager.getIdManager().fromJid("emma.societies.local");
		requestorService = getRequestorService();

		// - Let memberCssId joins CIS "Members Only Cis"
		cisMembersOnly.addMember(memberCssId.getJid(), "participant");


		// - Data Id
		try {
			cisPublicDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPublic.getCisId()+"/cis-member-list");
			cisMembersOnlyDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisMembersOnly.getCisId()+"/cis-member-list");
			cisPrivateDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPrivate.getCisId()+"/cis-member-list");
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUp(): DataId creation error "+e.getMessage()+"\n", e);
			fail("setUp(): DataId creation error "+e.getMessage());
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
		TestCase1266.cisManager.deleteCis(cisPublic.getCisId());
		TestCase1266.cisManager.deleteCis(cisMembersOnly.getCisId());
		TestCase1266.cisManager.deleteCis(cisPrivate.getCisId());
	}


	/* --- CHECK PERMISSION CIS --- */

	@Test
	public void testCheckPermissionPublicCis()
	{
		String testTitle = new String("Check permission public CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		ResponseItem permission1 = null;
		ResponseItem permission2 = null;
		try {
			permission1 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
			permission2 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPublicDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}

		assertNotNull("First: No permission retrieved", permission1);
		assertNotNull("First: No (real) permission retrieved", permission1.getDecision());
		assertEquals("First: Bad permission retrieved",  Decision.PERMIT.name(), permission1.getDecision().name());
		assertNotNull("Second: No permission retrieved", permission2);
		assertNotNull("Second: No (real) permission retrieved", permission2.getDecision());
		assertEquals("Second: Bad permission retrieved", Decision.PERMIT.name(), permission2.getDecision().name());
		assertEquals("Two requests, not the same answer", permission1.toXMLString(), permission2.toXMLString());
	}

	@Test
	public void testCheckPermissionMembersOnlyCis()
	{
		String testTitle = new String("Check permission members only CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		ResponseItem permissionOther1 = null;
		ResponseItem permissionMember1 = null;
		ResponseItem permissionMe1 = null;
		ResponseItem permissionOther2 = null;
		ResponseItem permissionMember2 = null;
		ResponseItem permissionMe2 = null;
		try {
			permissionOther1 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisMembersOnlyDataId, actionsRead);
			permissionMember1 = TestCase1266.privacyDataManager.checkPermission(new Requestor(memberCssId), cisMembersOnlyDataId, actionsRead);
			permissionMe1 = TestCase1266.privacyDataManager.checkPermission(new Requestor(myCssId), cisMembersOnlyDataId, actionsRead);
			permissionOther2 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisMembersOnlyDataId, actionsRead);
			permissionMember2 = TestCase1266.privacyDataManager.checkPermission(new Requestor(memberCssId), cisMembersOnlyDataId, actionsRead);
			permissionMe2 = TestCase1266.privacyDataManager.checkPermission(new Requestor(myCssId), cisMembersOnlyDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}

		assertNotNull("Other: No permission retrieved", permissionOther1);
		assertNotNull("Other: No (real) permission retrieved", permissionOther1.getDecision());
		assertEquals("Other: Bad permission retrieved",  Decision.DENY.name(), permissionOther1.getDecision().name());
		assertNotNull("Other: No permission retrieved", permissionOther2);
		assertNotNull("Other: No (real) permission retrieved", permissionOther2.getDecision());
		assertEquals("Other: Bad permission retrieved", Decision.DENY.name(), permissionOther2.getDecision().name());
		assertEquals("Other: Two requests, not the same answer", permissionOther1.toXMLString(), permissionOther2.toXMLString());

		assertNotNull("Member: No permission retrieved", permissionMember1);
		assertNotNull("Member: No (real) permission retrieved", permissionMember1.getDecision());
		assertEquals("Member: Bad permission retrieved",  Decision.PERMIT.name(), permissionMember1.getDecision().name());
		assertNotNull("Member: No permission retrieved", permissionMember2);
		assertNotNull("Member: No (real) permission retrieved", permissionMember2.getDecision());
		assertEquals("Member: Bad permission retrieved", Decision.PERMIT.name(), permissionMember2.getDecision().name());
		assertEquals("Member: Two requests, not the same answer", permissionMember1.toXMLString(), permissionMember2.toXMLString());

		assertNotNull("Me: No permission retrieved", permissionMe1);
		assertNotNull("Me: No (real) permission retrieved", permissionMe1.getDecision());
		assertEquals("Me: Bad permission retrieved",  Decision.PERMIT.name(), permissionMe1.getDecision().name());
		assertNotNull("Me: No permission retrieved", permissionMe2);
		assertNotNull("Me: No (real) permission retrieved", permissionMe2.getDecision());
		assertEquals("Me: Bad permission retrieved", Decision.PERMIT.name(), permissionMe2.getDecision().name());
		assertEquals("Me: Two requests, not the same answer", permissionMe1.toXMLString(), permissionMe2.toXMLString());
	}

	@Test
	public void testCheckPermissionPrivateCis()
	{
		String testTitle = new String("Check permission private CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info("[#"+testCaseNumber+"] "+testTitle);

		ResponseItem permissionOther1 = null;
		ResponseItem permissionMe1 = null;
		ResponseItem permissionOther2 = null;
		ResponseItem permissionMe2 = null;
		try {
			permissionOther1 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionMe1 = TestCase1266.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
			permissionOther2 = TestCase1266.privacyDataManager.checkPermission(requestorService, cisPrivateDataId, actionsRead);
			permissionMe2 = TestCase1266.privacyDataManager.checkPermission(new Requestor(myCssId), cisPrivateDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[#"+testCaseNumber+"] [PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e.getMessage()+") "+testTitle);
		}

		assertNotNull("No permission retrieved", permissionOther1);
		assertNotNull("No (real) permission retrieved", permissionOther1.getDecision());
		assertEquals("Bad permission retrieved",  Decision.DENY.name(), permissionOther1.getDecision().name());
		assertNotNull("No permission retrieved", permissionOther2);
		assertNotNull("No (real) permission retrieved", permissionOther2.getDecision());
		assertEquals("Bad permission retrieved", Decision.DENY.name(), permissionOther2.getDecision().name());
		assertEquals("Two requests, not the same answer", permissionOther1.toXMLString(), permissionOther2.toXMLString());

		assertNotNull("No permission retrieved", permissionMe1);
		assertNotNull("No (real) permission retrieved", permissionMe1.getDecision());
		assertEquals("Bad permission retrieved",  Decision.PERMIT.name(), permissionMe1.getDecision().name());
		assertNotNull("No permission retrieved", permissionMe2);
		assertNotNull("No (real) permission retrieved", permissionMe2.getDecision());
		assertEquals("Bad permission retrieved", Decision.PERMIT.name(), permissionMe2.getDecision().name());
		assertEquals("Two requests, not the same answer", permissionMe1.toXMLString(), permissionMe2.toXMLString());
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