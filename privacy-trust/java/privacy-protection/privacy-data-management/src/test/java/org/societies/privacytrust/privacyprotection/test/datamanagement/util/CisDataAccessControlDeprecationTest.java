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
package org.societies.privacytrust.privacyprotection.test.datamanagement.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.api.cis.model.CisAttributeTypes;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyDataManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
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
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.services.ServiceUtils;
import org.societies.privacytrust.privacyprotection.datamanagement.PrivacyDataManager;
import org.societies.util.commonmock.MockIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Olivier Maridat (Trialog)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "../PrivacyDataManagerInternalTest-context.xml" })
public class CisDataAccessControlDeprecationTest {
	private static final Logger LOG = LoggerFactory.getLogger(CisDataAccessControlDeprecationTest.class.getSimpleName());

	public static ICisManager cisManager;
	public static ICommManager commManager;
	public static IPrivacyPolicyManager privacyPolicyManager;
	@Autowired
	IPrivacyDataManager privacyDataManager;

	private static DataIdentifier cisPublicDataId;
	private static DataIdentifier cisMembersOnlyDataId;
	private static DataIdentifier cisPrivateDataId;
	private static Requestor requestorMyCssId;
	private static Requestor requestorMemberCssId;
	private static Requestor requestorOtherCssId;
	private static RequestorCis requestorCisPublic;
	private static RequestorCis requestorCisMembersOnly;
	private static RequestorCis requestorCisPrivate;
	private static RequestorService requestorMyService;
	private static IIdentity myCssId;
	private static IIdentity memberCssId;
	private static IIdentity otherCssId;
	private static ICisParticipant memberCssCisParticipant;
	private static IIdentity cisPublicId;
	private static IIdentity cisMembersOnlyId;
	private static IIdentity cisPrivateId;
	private static ICisOwned cisPublic;
	private static ICisOwned cisMembersOnly;
	private static ICisOwned cisPrivate;
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
		LOG.info("CisDataAccessControl::setUpClass");
	}

	@Before
	public void setUp() throws Exception
	{
		LOG.info(""+getClass().getSimpleName()+"::setUp");
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
			// Comm Manager
			String host = "societies.local";
			commManager = Mockito.mock(ICommManager.class);
			IIdentityManager idManager = Mockito.mock(IIdentityManager.class);
			INetworkNode myCssNetworkNode = Mockito.mock(INetworkNode.class);
			String myCssIdString = "university."+host;
			String memberCssIdString = "emma."+host;
			String otherCssIdString = "arthur."+host;
			String cisPublicIdString = "cis-public."+host;
			String cisMembersOnlyIdString = "cis-members-only."+host;
			String cisPrivateIdString = "cis-private."+host;
			myCssId = new MockIdentity(myCssIdString);
			memberCssId = new MockIdentity(memberCssIdString);
			otherCssId = new MockIdentity(otherCssIdString);
			cisPublicId = new MockIdentity(cisPublicIdString);
			cisMembersOnlyId = new MockIdentity(cisMembersOnlyIdString);
			cisPrivateId = new MockIdentity(cisPrivateIdString);
			cisPublic = Mockito.mock(ICisOwned.class);
			cisMembersOnly = Mockito.mock(ICisOwned.class);
			cisPrivate = Mockito.mock(ICisOwned.class);
			Set<ICisParticipant> emptyMemberList = new HashSet<ICisParticipant>();
			Set<ICisParticipant> memberList = new HashSet<ICisParticipant>();
			memberCssCisParticipant = Mockito.mock(ICisParticipant.class);
			Mockito.when(memberCssCisParticipant.getMembershipType()).thenReturn("participant");
			Mockito.when(memberCssCisParticipant.getMembersJid()).thenReturn(memberCssId.getJid());
			memberList.add(memberCssCisParticipant);
			Mockito.when(cisPublic.getCisId()).thenReturn(cisPublicId.getJid());
			Mockito.when(cisPublic.getOwnerId()).thenReturn(myCssId.getJid());
			Mockito.when(cisPublic.getMemberList()).thenReturn(emptyMemberList);
			Mockito.when(cisMembersOnly.getCisId()).thenReturn(cisMembersOnlyId.getJid());
			Mockito.when(cisMembersOnly.getOwnerId()).thenReturn(myCssId.getJid());
			Mockito.when(cisMembersOnly.getMemberList()).thenReturn(memberList);
			Mockito.when(cisPrivate.getCisId()).thenReturn(cisPrivateId.getJid());
			Mockito.when(cisPrivate.getOwnerId()).thenReturn(myCssId.getJid());
			Mockito.when(cisPrivate.getMemberList()).thenReturn(emptyMemberList);
			Mockito.when(myCssNetworkNode.getJid()).thenReturn(myCssId.getJid());
			Mockito.when(idManager.getThisNetworkNode()).thenReturn(myCssNetworkNode);
			Mockito.when(idManager.fromJid(myCssIdString)).thenReturn(myCssId);
			Mockito.when(idManager.fromJid(memberCssIdString)).thenReturn(memberCssId);
			Mockito.when(idManager.fromJid(otherCssIdString)).thenReturn(otherCssId);
			Mockito.when(idManager.fromJid(cisPublicId.getJid())).thenReturn(cisPublicId);
			Mockito.when(idManager.fromJid(cisMembersOnlyId.getJid())).thenReturn(cisMembersOnlyId);
			Mockito.when(idManager.fromJid(cisPrivateId.getJid())).thenReturn(cisPrivateId);
			Mockito.when(commManager.getIdManager()).thenReturn(idManager);

			// CIS Manager
			cisManager = Mockito.mock(ICisManager.class);
			Mockito.when(cisManager.deleteCis(Mockito.anyString())).thenReturn(true);
			Mockito.when(cisManager.getOwnedCis(cisPublicId.getJid())).thenReturn(cisPublic);
			Mockito.when(cisManager.getOwnedCis(cisMembersOnlyId.getJid())).thenReturn(cisMembersOnly);
			Mockito.when(cisManager.getOwnedCis(cisPrivateId.getJid())).thenReturn(cisPrivate);

			// - Privacy Policy
			List<RequestItem> requestItemsPublic = new ArrayList<RequestItem>();
			requestItemsPublic.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, CisAttributeTypes.MEMBER_LIST), actionsRead, conditionsPublic));
			List<RequestItem> requestItemsMembersOnly = new ArrayList<RequestItem>();
			requestItemsMembersOnly.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, CisAttributeTypes.MEMBER_LIST), actionsRead, conditionsMembersOnly));
			List<RequestItem> requestItemsPrivate = new ArrayList<RequestItem>();
			requestItemsPrivate.add(new RequestItem(new Resource(DataIdentifierScheme.CIS, CisAttributeTypes.MEMBER_LIST), actionsRead, conditionsPrivate));

			requestorCisPublic = new RequestorCis(myCssId, cisPublicId);
			requestorCisMembersOnly = new RequestorCis(myCssId, cisMembersOnlyId);
			requestorCisPrivate = new RequestorCis(myCssId, cisPrivateId);
			requestorMyService = new RequestorService(myCssId, ServiceUtils.generateServiceResourceIdentifierFromString("mygreatservice test"));
			requestorMyCssId = new Requestor(myCssId);
			requestorMemberCssId = new Requestor(memberCssId);
			requestorOtherCssId = new Requestor(otherCssId);
			privacyPolicyPublic = new RequestPolicy(requestorCisPublic, requestItemsPublic);
			privacyPolicyMembersOnly = new RequestPolicy(requestorCisMembersOnly, requestItemsMembersOnly);
			privacyPolicyPrivate = new RequestPolicy(requestorCisPrivate, requestItemsPrivate);

			// Privacy Policy Manager
			privacyPolicyManager = Mockito.mock(IPrivacyPolicyManager.class);
			Mockito.when(privacyPolicyManager.getPrivacyPolicy(RequestorUtils.toRequestorBean(requestorCisPublic))).thenReturn(RequestPolicyUtils.toRequestPolicyBean(privacyPolicyPublic));
			Mockito.when(privacyPolicyManager.getPrivacyPolicy(RequestorUtils.toRequestorBean(requestorCisMembersOnly))).thenReturn(RequestPolicyUtils.toRequestPolicyBean(privacyPolicyMembersOnly));
			Mockito.when(privacyPolicyManager.getPrivacyPolicy(RequestorUtils.toRequestorBean(requestorCisPrivate))).thenReturn(RequestPolicyUtils.toRequestPolicyBean(privacyPolicyPrivate));
			Mockito.when(privacyPolicyManager.getPrivacyPolicy(requestorCisPublic)).thenReturn(privacyPolicyPublic);
			Mockito.when(privacyPolicyManager.getPrivacyPolicy(requestorCisMembersOnly)).thenReturn(privacyPolicyMembersOnly);
			Mockito.when(privacyPolicyManager.getPrivacyPolicy(requestorCisPrivate)).thenReturn(privacyPolicyPrivate);

			// Privacy Data Manager
			((PrivacyDataManager) privacyDataManager).setCommManager(commManager);
			((PrivacyDataManager) privacyDataManager).setCisManager(cisManager);
			((PrivacyDataManager) privacyDataManager).setPrivacyPolicyManager(privacyPolicyManager);

			// - Data Id
			cisPublicDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPublic.getCisId()+"/"+CisAttributeTypes.MEMBER_LIST);
			cisMembersOnlyDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisMembersOnly.getCisId()+"/"+CisAttributeTypes.MEMBER_LIST);
			cisPrivateDataId = DataIdentifierFactory.fromUri(DataIdentifierScheme.CIS+"://"+cisPrivate.getCisId()+"/"+CisAttributeTypes.MEMBER_LIST);
		}
		catch (MalformedCtxIdentifierException e) {
			LOG.error("setUpClass(): DataId creation error", e);
			fail("setUpClass(): DataId creation error "+e);
		}
		catch (Exception e) {
			LOG.error("setUpClass(): error", e);
			fail("setUpClass(): error "+e);
		}
	}

	@After
	public void tearDown() throws Exception
	{
		LOG.info(""+getClass().getSimpleName()+"::tearDown");
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
		LOG.info("CisDataAccessControl::tearDownClass");
		try {
			cisManager.deleteCis(cisPublic.getCisId());
			cisManager.deleteCis(cisMembersOnly.getCisId());
			cisManager.deleteCis(cisPrivate.getCisId());
		}
		catch(Exception e) {
			LOG.error("tearDownClass(): Can't delete CISs "+e+"\n", e);
			fail("tearDownClass(): Can't delete CISs "+e);
		}
	}


	/* --- CHECK PERMISSION CIS --- */

	@Test
	@Rollback(true)
	public void testCheckPermissionPublicCis()
	{
		String testTitle = new String("testCheckPermissionPublicCis - Check permission public CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info(""+testTitle);

		List<ResponseItem> permissionsOther0 = null;
		List<ResponseItem> permissionsOther1 = null;
		List<ResponseItem> permissionsMember1 = null;
		List<ResponseItem> permissionsMe1 = null;
		List<ResponseItem> permissionsOther2 = null;
		List<ResponseItem> permissionsMember2 = null;
		List<ResponseItem> permissionsMe2 = null;
		try {
			permissionsOther0 = privacyDataManager.checkPermission(requestorMyService, cisPublicDataId, actionsRead);
			permissionsOther1 = privacyDataManager.checkPermission(requestorOtherCssId, cisPublicDataId, actionsRead);
			permissionsMember1 = privacyDataManager.checkPermission(requestorMemberCssId, cisPublicDataId, actionsRead);
			permissionsMe1 = privacyDataManager.checkPermission(requestorMyCssId, cisPublicDataId, actionsRead);
			permissionsOther2 = privacyDataManager.checkPermission(requestorOtherCssId, cisPublicDataId, actionsRead);
			permissionsMember2 = privacyDataManager.checkPermission(requestorMemberCssId, cisPublicDataId, actionsRead);
			permissionsMe2 = privacyDataManager.checkPermission(requestorMyCssId, cisPublicDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e+") "+testTitle);
		}

		assertNotNull("Other0: No permission retrieved", permissionsOther0);
		assertTrue("Other0: No permission retrieved", permissionsOther0.size() > 0);
		assertNotNull("Other0: No (real) permission retrieved", permissionsOther0.get(0).getDecision());
		assertEquals("Other0: Bad permission retrieved",  Decision.PERMIT.name(), permissionsOther0.get(0).getDecision().name());
		assertNotNull("Other1: No permission retrieved", permissionsOther1);
		assertTrue("Other1: No permission retrieved", permissionsOther1.size() > 0);
		assertNotNull("Other1: No (real) permission retrieved", permissionsOther1.get(0).getDecision());
		assertEquals("Other1: Bad permission retrieved",  Decision.PERMIT.name(), permissionsOther1.get(0).getDecision().name());
		assertNotNull("Other2: No permission retrieved", permissionsOther2);
		assertTrue("Other2: No permission retrieved", permissionsOther2.size() > 0);
		assertNotNull("Other2: No (real) permission retrieved", permissionsOther2.get(0).getDecision());
		assertEquals("Other2: Bad permission retrieved", Decision.PERMIT.name(), permissionsOther2.get(0).getDecision().name());
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
	@Rollback(true)
	public void testCheckPermissionMembersOnlyCis()
	{
		String testTitle = new String("testCheckPermissionMembersOnlyCis - Check permission members only CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info(""+testTitle);

		List<ResponseItem> permissionsOther0 = null;
		List<ResponseItem> permissionsOther1 = null;
		List<ResponseItem> permissionsMember1 = null;
		List<ResponseItem> permissionsMe1 = null;
		List<ResponseItem> permissionsOther2 = null;
		List<ResponseItem> permissionsMember2 = null;
		List<ResponseItem> permissionsMe2 = null;
		try {
			permissionsOther0 = privacyDataManager.checkPermission(requestorMyService, cisMembersOnlyDataId, actionsRead);
			permissionsOther1 = privacyDataManager.checkPermission(requestorOtherCssId, cisMembersOnlyDataId, actionsRead);
			permissionsMember1 = privacyDataManager.checkPermission(requestorMemberCssId, cisMembersOnlyDataId, actionsRead);
			permissionsMe1 = privacyDataManager.checkPermission(requestorMyCssId, cisMembersOnlyDataId, actionsRead);
			permissionsOther2 = privacyDataManager.checkPermission(requestorOtherCssId, cisMembersOnlyDataId, actionsRead);
			permissionsMember2 = privacyDataManager.checkPermission(requestorMemberCssId, cisMembersOnlyDataId, actionsRead);
			permissionsMe2 = privacyDataManager.checkPermission(requestorMyCssId, cisMembersOnlyDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e+") "+testTitle);
		}

		assertNotNull("Other0: No permission retrieved", permissionsOther0);
		assertTrue("Other0: No permission retrieved", permissionsOther0.size() > 0);
		assertNotNull("Other0: No (real) permission retrieved", permissionsOther0.get(0).getDecision());
		assertEquals("Other0: Bad permission retrieved",  Decision.DENY.name(), permissionsOther0.get(0).getDecision().name());
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
	@Rollback(true)
	public void testCheckPermissionPrivateCis()
	{
		String testTitle = new String("testCheckPermissionPrivateCis- Check permission private CIS: retrieve a privacy (for the first time, and then retrieved)");
		LOG.info(""+testTitle);

		List<ResponseItem> permissionsOther0 = null;
		List<ResponseItem> permissionsOther1 = null;
		List<ResponseItem> permissionsMember1 = null;
		List<ResponseItem> permissionsMe1 = null;
		List<ResponseItem> permissionsOther2 = null;
		List<ResponseItem> permissionsMember2 = null;
		List<ResponseItem> permissionsMe2 = null;
		try {
			permissionsOther0 = privacyDataManager.checkPermission(requestorMyService, cisPrivateDataId, actionsRead);
			permissionsOther1 = privacyDataManager.checkPermission(requestorOtherCssId, cisPrivateDataId, actionsRead);
			permissionsMember1 = privacyDataManager.checkPermission(requestorMemberCssId, cisPrivateDataId, actionsRead);
			permissionsMe1 = privacyDataManager.checkPermission(requestorMyCssId, cisPrivateDataId, actionsRead);
			permissionsOther2 = privacyDataManager.checkPermission(requestorOtherCssId, cisPrivateDataId, actionsRead);
			permissionsMember2 = privacyDataManager.checkPermission(requestorMemberCssId, cisPrivateDataId, actionsRead);
			permissionsMe2 = privacyDataManager.checkPermission(requestorMyCssId, cisPrivateDataId, actionsRead);
		} catch (PrivacyException e) {
			LOG.error("[PrivacyException] "+testTitle, e);
			fail("PrivacyException ("+e+") "+testTitle);
		}

		assertNotNull("Other0: No permission retrieved", permissionsOther0);
		assertTrue("Other0: No permission retrieved", permissionsOther0.size() > 0);
		assertNotNull("Other0: No (real) permission retrieved", permissionsOther0.get(0).getDecision());
		assertEquals("Other0: Bad permission retrieved",  Decision.DENY.name(), permissionsOther0.get(0).getDecision().name());
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
		assertEquals("Member1: Bad permission retrieved",  Decision.DENY.name(), permissionsMember1.get(0).getDecision().name());
		assertNotNull("Member2: No permission retrieved", permissionsMember2);
		assertTrue("Member2: No permission retrieved", permissionsMember1.size() > 0);
		assertNotNull("Member2: No (real) permission retrieved", permissionsMember2.get(0).getDecision());
		assertEquals("Member2: Bad permission retrieved", Decision.DENY.name(), permissionsMember2.get(0).getDecision().name());
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

	// -- Dependency Injection
	public void setPrivacyDataManager(IPrivacyDataManager privacyDataManager) {
		this.privacyDataManager = privacyDataManager;
		LOG.info("[Dependency Injection] IPrivacyDataManager injected");
	}
}