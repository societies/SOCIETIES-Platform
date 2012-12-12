package org.societies.context.broker.test;


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelObjectFactory;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.IdentityType;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxHistoryAttributeBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.HistoryMapBean;
import org.societies.context.api.user.inference.IUserCtxInferenceMgr;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.community.db.impl.CommunityCtxDBMgr;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;

public class BeanTranslatorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		/*
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
	*/
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
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}
	
	
	/**
	 * Test method for {@link org.societies.context.broker.impl.InternalCtxBroker#retrieveIndividualEntity(IIdentity)}.
	 * 
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 */
	
	@Test
	public void testBeanTranslator() throws Exception {

		CtxEntityIdentifier entityID = new CtxEntityIdentifier("context://john.societies.local/ENTITY/person/31");
		CtxEntity entity = new CtxEntity(entityID);
		CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
		CtxEntityBean entBean = ctxBeanTranslator.fromCtxEntity(entity);
		CtxEntity entityFromBean = ctxBeanTranslator.fromCtxEntityBean(entBean);
		
		//System.out.println("******** entityBean "+entBean.toString() );
		assertEquals(entityFromBean,entity);
				
		CtxAttributeIdentifier attrActionID = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/action/30");
		CtxAttribute attrTemperature = CtxModelObjectFactory.getInstance().createAttribute(attrActionID, new Date(), new Date(), "hot");
		CtxModelBeanTranslator ctxBeanTranslator1 = CtxModelBeanTranslator.getInstance();
		CtxAttributeBean attrBean  = ctxBeanTranslator1.fromCtxAttribute(attrTemperature);
		
		CtxAttribute attrTemperatureFromBean = ctxBeanTranslator1.fromCtxAttributeBean(attrBean);		
		//System.out.println("******** attrBean "+attrTemperature);
		//System.out.println("******** attrBean1 "+attrTemperatureFromBean);
		
		assertEquals(attrTemperature,attrTemperatureFromBean);
	}
	

	@Test
	public void testHistoryBeanTranslator() throws Exception {
		
		CtxAttributeIdentifier attrActionID = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/action/30");
		CtxAttribute attrAction = CtxModelObjectFactory.getInstance().createAttribute(attrActionID, new Date(), new Date(), "hot");
		
		CtxHistoryAttribute hocAttr = CtxModelObjectFactory.getInstance().createHistoryAttribute(attrAction);
		CtxModelBeanTranslator ctxBeanTranslator1 = CtxModelBeanTranslator.getInstance();
		CtxHistoryAttributeBean hocBean = ctxBeanTranslator1.fromCtxHoCAttribute(hocAttr);
		
		CtxHistoryAttribute hocAttrTranslated = ctxBeanTranslator1.fromCtxHoCAttributeBean(hocBean);
		assertEquals(hocAttr,hocAttrTranslated);
	}

	@Test
	public void testHistoryMapBeanTranslator() throws Exception {
		
		CtxAttributeIdentifier attrActionID = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/action/30");
		CtxAttribute attrAction = CtxModelObjectFactory.getInstance().createAttribute(attrActionID, new Date(), new Date(), "turnOn");
		CtxHistoryAttribute hocAttrAction = CtxModelObjectFactory.getInstance().createHistoryAttribute(attrAction);
		
		CtxAttributeIdentifier attrTemperatureID = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/temperature/30");
		CtxAttribute attrTemp = CtxModelObjectFactory.getInstance().createAttribute(attrTemperatureID, new Date(), new Date(), "hot");
		CtxHistoryAttribute hocattrTemp = CtxModelObjectFactory.getInstance().createHistoryAttribute(attrTemp);
		
		CtxAttributeIdentifier attrStatusID = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/status/30");
		CtxAttribute attrStatus = CtxModelObjectFactory.getInstance().createAttribute(attrStatusID, new Date(), new Date(), "free");
		CtxHistoryAttribute hocAttrStatus = CtxModelObjectFactory.getInstance().createHistoryAttribute(attrStatus);
				
		CtxAttributeIdentifier attrActionID2 = new CtxAttributeIdentifier("context://john.societies.local/ENTITY/person/31/ATTRIBUTE/action/31");
		CtxAttribute attrAction2 = CtxModelObjectFactory.getInstance().createAttribute(attrActionID2, new Date(), new Date(), "turnOff");
		CtxHistoryAttribute hocAttrAction2 = CtxModelObjectFactory.getInstance().createHistoryAttribute(attrAction2);
				
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> historyMap = new HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		
		List<CtxHistoryAttribute> historyList = new ArrayList<CtxHistoryAttribute>();
		historyList.add(hocattrTemp);
		historyList.add(hocAttrStatus);
		historyMap.put(hocAttrAction, historyList);
		historyMap.put(hocAttrAction2, historyList);
		
		
		System.out.println("historyMap "+historyMap);
		
		CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
		HistoryMapBean hocMapBean = ctxBeanTranslator.fromHistoryMap(historyMap);
		System.out.println("hocMapBean "+hocMapBean);
		
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> historyMapTranslated = new HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		historyMapTranslated = ctxBeanTranslator.fromHistoryMapBean(hocMapBean);
		System.out.println("historyMapTranslated "+historyMapTranslated);
		
		assertEquals(historyMap,historyMapTranslated);
	}

}















