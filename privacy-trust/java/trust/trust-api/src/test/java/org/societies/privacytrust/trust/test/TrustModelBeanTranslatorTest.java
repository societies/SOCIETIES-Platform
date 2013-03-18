/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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
package org.societies.privacytrust.trust.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.privacytrust.trust.model.TrustEvidenceTypeBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityIdBean;
import org.societies.api.schema.privacytrust.trust.model.TrustedEntityTypeBean;

/**
 * Tests the {@link TrustModelBeanTranslator} class.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.1
 */
public class TrustModelBeanTranslatorTest {

	private static final String CSS_ENTITY_ID = "jane.societies.local";
	private static final String CIS_ENTITY_ID = "cis-b5a2a338-b7ff-452f-ae94-0b79f23a40ca.societies.local";
	private static final String SVC_ENTITY_ID = "http://societies.local/service";
	
	private static TrustedEntityId cssTeid;
	private static TrustedEntityIdBean cssTeidBean;
	
	private static TrustedEntityId cisTeid;
	private static TrustedEntityIdBean cisTeidBean;
	
	private static TrustedEntityId svcTeid;
	private static TrustedEntityIdBean svcTeidBean;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		cssTeid = new TrustedEntityId(TrustedEntityType.CSS, CSS_ENTITY_ID);
		cssTeidBean = new TrustedEntityIdBean();
		cssTeidBean.setEntityType(TrustedEntityTypeBean.CSS);
		cssTeidBean.setEntityId(CSS_ENTITY_ID);
		
		cisTeid = new TrustedEntityId(TrustedEntityType.CIS, CIS_ENTITY_ID);
		cisTeidBean = new TrustedEntityIdBean();
		cisTeidBean.setEntityType(TrustedEntityTypeBean.CIS);
		cisTeidBean.setEntityId(CIS_ENTITY_ID);
		
		svcTeid = new TrustedEntityId(TrustedEntityType.SVC, SVC_ENTITY_ID);
		svcTeidBean = new TrustedEntityIdBean();
		svcTeidBean.setEntityType(TrustedEntityTypeBean.SVC);
		svcTeidBean.setEntityId(SVC_ENTITY_ID);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
		cssTeid = null;
		cssTeidBean = null;
		
		cisTeid = null;
		cisTeidBean = null;
		
		svcTeid = null;
		svcTeidBean = null;
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
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityId(TrustedEntityId)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCssTrustedEntityId() throws Exception {
		
		final TrustedEntityIdBean teidBean = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityId(cssTeid);
		assertNotNull(teidBean);
		assertEquals(cssTeidBean.getEntityType(), teidBean.getEntityType());
		assertEquals(cssTeidBean.getEntityId(), teidBean.getEntityId());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityIdBean(TrustedEntityIdBean)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCssTrustedEntityIdBean() throws Exception {
		
		final TrustedEntityId teid = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityIdBean(cssTeidBean);
		assertNotNull(teid);
		assertEquals(cssTeid.getEntityType(), teid.getEntityType());
		assertEquals(cssTeid.getEntityId(), teid.getEntityId());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityType(TrustedEntityType)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCisTrustedEntityType() throws Exception {
		
		final TrustedEntityTypeBean typeBean = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityType(TrustedEntityType.CIS);
		assertNotNull(typeBean);
		assertEquals(TrustedEntityTypeBean.CIS, typeBean);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityTypeBean(TrustedEntityTypeBean)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCisTrustedEntityTypeBean() throws Exception {
		
		final TrustedEntityType type = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityTypeBean(TrustedEntityTypeBean.CIS);
		assertNotNull(type);
		assertEquals(TrustedEntityType.CIS, type);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityType(TrustedEntityType)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromServiceTrustedEntityType() throws Exception {
		
		final TrustedEntityTypeBean typeBean = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityType(TrustedEntityType.SVC);
		assertNotNull(typeBean);
		assertEquals(TrustedEntityTypeBean.SVC, typeBean);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityTypeBean(TrustedEntityTypeBean)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromServiceTrustedEntityTypeBean() throws Exception {
		
		final TrustedEntityType type = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityTypeBean(TrustedEntityTypeBean.SVC);
		assertNotNull(type);
		assertEquals(TrustedEntityType.SVC, type);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityType(TrustedEntityType)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCssTrustedEntityType() throws Exception {
		
		final TrustedEntityTypeBean typeBean = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityType(TrustedEntityType.CSS);
		assertNotNull(typeBean);
		assertEquals(TrustedEntityTypeBean.CSS, typeBean);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityTypeBean(TrustedEntityTypeBean)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCssTrustedEntityTypeBean() throws Exception {
		
		final TrustedEntityType type = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityTypeBean(TrustedEntityTypeBean.CSS);
		assertNotNull(type);
		assertEquals(TrustedEntityType.CSS, type);
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityId(TrustedEntityId)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCisTrustedEntityId() throws Exception {
		
		final TrustedEntityIdBean teidBean = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityId(cisTeid);
		assertNotNull(teidBean);
		assertEquals(cisTeidBean.getEntityType(), teidBean.getEntityType());
		assertEquals(cisTeidBean.getEntityId(), teidBean.getEntityId());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityIdBean(TrustedEntityIdBean)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromCisTrustedEntityIdBean() throws Exception {
		
		final TrustedEntityId teid = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityIdBean(cisTeidBean);
		assertNotNull(teid);
		assertEquals(cisTeid.getEntityType(), teid.getEntityType());
		assertEquals(cisTeid.getEntityId(), teid.getEntityId());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityId(TrustedEntityId)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromServiceTrustedEntityId() throws Exception {
		
		final TrustedEntityIdBean teidBean = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityId(svcTeid);
		assertNotNull(teidBean);
		assertEquals(svcTeidBean.getEntityType(), teidBean.getEntityType());
		assertEquals(svcTeidBean.getEntityId(), teidBean.getEntityId());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustedEntityIdBean(TrustedEntityIdBean)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromServiceTrustedEntityIdBean() throws Exception {
		
		final TrustedEntityId teid = 
				TrustModelBeanTranslator.getInstance().fromTrustedEntityIdBean(svcTeidBean);
		assertNotNull(teid);
		assertEquals(svcTeid.getEntityType(), teid.getEntityType());
		assertEquals(svcTeid.getEntityId(), teid.getEntityId());
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustEvidenceType(TrustEvidenceType)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromTrustEvidenceType() throws Exception {
		
		assertEquals(TrustEvidenceTypeBean.USED_SERVICE, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceType(TrustEvidenceType.USED_SERVICE));
		assertEquals(TrustEvidenceTypeBean.FRIENDED_USER, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceType(TrustEvidenceType.FRIENDED_USER));
		assertEquals(TrustEvidenceTypeBean.UNFRIENDED_USER, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceType(TrustEvidenceType.UNFRIENDED_USER));
		assertEquals(TrustEvidenceTypeBean.JOINED_COMMUNITY, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceType(TrustEvidenceType.JOINED_COMMUNITY));
		assertEquals(TrustEvidenceTypeBean.LEFT_COMMUNITY, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceType(TrustEvidenceType.LEFT_COMMUNITY));
		assertEquals(TrustEvidenceTypeBean.RATED, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceType(TrustEvidenceType.RATED));
	}
	
	/**
	 * Test method for {@link org.societies.api.privacytrust.trust.model.TrustModelBeanTranslator#fromTrustEvidenceTypeBean(TrustEvidenceTypeBean)}.
	 * @throws Exception 
	 */
	@Test
	public void testFromTrustEvidenceTypeBean() throws Exception {
		
		assertEquals(TrustEvidenceType.USED_SERVICE, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceTypeBean(TrustEvidenceTypeBean.USED_SERVICE));
		assertEquals(TrustEvidenceType.FRIENDED_USER, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceTypeBean(TrustEvidenceTypeBean.FRIENDED_USER));
		assertEquals(TrustEvidenceType.UNFRIENDED_USER, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceTypeBean(TrustEvidenceTypeBean.UNFRIENDED_USER));
		assertEquals(TrustEvidenceType.JOINED_COMMUNITY, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceTypeBean(TrustEvidenceTypeBean.JOINED_COMMUNITY));
		assertEquals(TrustEvidenceType.LEFT_COMMUNITY, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceTypeBean(TrustEvidenceTypeBean.LEFT_COMMUNITY));
		assertEquals(TrustEvidenceType.RATED, 
				TrustModelBeanTranslator.getInstance().fromTrustEvidenceTypeBean(TrustEvidenceTypeBean.RATED));
	}
}