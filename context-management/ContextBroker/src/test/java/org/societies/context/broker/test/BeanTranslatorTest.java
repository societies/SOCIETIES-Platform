package org.societies.context.broker.test;


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.context.api.user.inference.IUserCtxInferenceMgr;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.community.db.impl.CommunityCtxDBMgr;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;

public class BeanTranslatorTest {

	private static final String OWNER_IDENTITY_STRING = "myFooIIdentity@societies.local";
	private static final String NETWORK_NODE_STRING = "myFooIIdentity@societies.local/node";
	private static final String CIS_IDENTITY_STRING = "FooCISIIdentity@societies.local";
	//myFooIIdentity@societies.local
	private static final List<String> INF_TYPES_LIST = new ArrayList<String>(); 
	
	
	private InternalCtxBroker internalCtxBroker;

	private static IIdentityManager mockIdentityMgr = mock(IIdentityManager.class);
	private static IIdentity cssMockIdentity = mock(IIdentity.class);
	private static IIdentity cisMockIdentity = mock(IIdentity.class);
	private static INetworkNode mockNetworkNode = mock(INetworkNode.class);


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		INF_TYPES_LIST.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		INF_TYPES_LIST.add(CtxAttributeTypes.LOCATION_COORDINATES);
		
		when(mockIdentityMgr.getThisNetworkNode()).thenReturn(mockNetworkNode);
		when(mockNetworkNode.getBareJid()).thenReturn(OWNER_IDENTITY_STRING);
		when(mockIdentityMgr.fromJid(OWNER_IDENTITY_STRING)).thenReturn(cssMockIdentity);
		when(mockNetworkNode.toString()).thenReturn(NETWORK_NODE_STRING);

		when(cssMockIdentity.toString()).thenReturn(OWNER_IDENTITY_STRING);
		when(cssMockIdentity.getType()).thenReturn(IdentityType.CSS);
		when(mockIdentityMgr.isMine(cssMockIdentity)).thenReturn(true);
		
		when(cisMockIdentity.getType()).thenReturn(IdentityType.CIS);
		when(cisMockIdentity.toString()).thenReturn(CIS_IDENTITY_STRING);

		//IIdentity scopeID = this.idMgr.fromJid(communityCtxEnt.getOwnerId());
		when(mockIdentityMgr.fromJid(CIS_IDENTITY_STRING)).thenReturn(cisMockIdentity);
		
		//this.commMgr.getIdManager().fromJid(ctxModelObj.getOwnerId());

	//	when(mockUserCtxInferenceMgr.getInferrableTypes()).thenReturn(INF_TYPES_LIST);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception { 

		internalCtxBroker = new InternalCtxBroker();
		internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
		internalCtxBroker.setCommunityCtxDBMgr(new CommunityCtxDBMgr());
		internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
		//internalCtxBroker.setUserCtxInferenceMgr(new UserCtxInferenceMgr());
		//internalCtxBroker.setIdentityMgr(mockIdentityMgr);
		internalCtxBroker.createIndividualEntity(cssMockIdentity, CtxEntityTypes.PERSON); // TODO remove?
		//internalCtxBroker.createCssNode(mockNetworkNode); // TODO remove?
		
		//this.commMgr.getIdManager().fromJid(ctxModelObj.getOwnerId());
		
		//internalCtxBroker.setUserCtxInferenceMgr(mockUserCtxInferenceMgr);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

		internalCtxBroker = null;
	}
	
	
	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveIndividualEntity(IIdentity)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 */
	@Ignore
	@Test
	public void testRetrieveIndividualEntity() throws Exception {

		final IndividualCtxEntity ownerEnt = 
				internalCtxBroker.retrieveIndividualEntity(cssMockIdentity).get();
		assertNotNull(ownerEnt);
		assertEquals(OWNER_IDENTITY_STRING, ownerEnt.getId().getOwnerId());
		assertEquals(CtxEntityTypes.PERSON, ownerEnt.getType());
		assertFalse(ownerEnt.getAttributes(CtxAttributeTypes.ID).isEmpty());
		assertEquals(1, ownerEnt.getAttributes(CtxAttributeTypes.ID).size());
		
		CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
		CtxEntityBean entBean = ctxBeanTranslator.fromCtxEntity(ownerEnt);
		System.out.println("******** entBean "+entBean.toString() );
		System.out.println("******** entBean "+entBean.toString() );
		CtxEntity ctxEntity = ctxBeanTranslator.fromCtxEntityBean(entBean);
		
		System.out.println("******** ent "+ctxEntity.getId().toString() );
		if(ctxEntity.equals(ownerEnt)){
			System.out.println("equal obj ");
		}else System.out.println("not equal obj ");
		
		if(ctxEntity.getId().equals(ownerEnt.getId())){
			System.out.println("equal ids");
		}else System.out.println("not equal ids");
	
		for( CtxAttribute attr : ctxEntity.getAttributes()){
			System.out.println("attrs"+ attr.getId());	
		}
		
		CtxModelObjectBean ctxObjBean = ctxBeanTranslator.fromCtxModelObject(ctxEntity);
		System.out.println("ctxObjBean "+ctxObjBean );
		System.out.println("ctxObjBean  id "+ctxObjBean.getId().toString() );
		
		CtxModelObject ctxObj = ctxBeanTranslator.fromCtxModelObjectBean(ctxObjBean);
		System.out.println("ctxObj "+ctxObj );
		System.out.println("ctxObj  id "+ctxObj.getId().toString() );
				

	}
	
}