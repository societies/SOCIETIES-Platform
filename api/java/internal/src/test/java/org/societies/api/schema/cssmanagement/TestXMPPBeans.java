package org.societies.api.schema.cssmanagement;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.css.CSSManagerEnums;

public class TestXMPPBeans {
	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "CSSProfile1";
	
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	
	public static final String TEST_NAME = "The CSS";
	public static final String TEST_POSITION = "CEO";
	public static final String TEST_WORKPLACE = "LAKE";
	

	private CssRecord record;
	private CssNode cssNode_1, cssNode_2;
	private ArrayList<CssNode> cssNodes;

	@Before
	public void setUp() throws Exception {
		cssNode_1 = new CssNode();
		cssNode_1.setIdentity(TEST_IDENTITY_1);
		cssNode_1.setStatus(CSSManagerEnums.nodeStatus.Available.ordinal());
		cssNode_1.setType(CSSManagerEnums.nodeType.Rich.ordinal());

		cssNode_2 = new CssNode();
		cssNode_2.setIdentity(TEST_IDENTITY_2);
		cssNode_2.setStatus(CSSManagerEnums.nodeStatus.Hibernating.ordinal());
		cssNode_2.setType(CSSManagerEnums.nodeType.Android.ordinal());
		
		cssNodes = new ArrayList<CssNode>();
		cssNodes.add(cssNode_1);
		cssNodes.add(cssNode_2);
		
		this.record = new CssRecord();
		this.record.setCssIdentity(TEST_IDENTITY);
		
		this.record.getCssNodes().add(cssNode_1);
		this.record.getCssNodes().add(cssNode_2);
				
		this.record.setEmailID(TEST_EMAIL);
		this.record.setEntity(CSSManagerEnums.entityType.Organisation.ordinal());
		this.record.setForeName(TEST_FORENAME);
		
		this.record.setName(TEST_NAME);
		
		this.record.setSex(CSSManagerEnums.genderType.Unspecified.ordinal());
		this.record.setWorkplace(TEST_WORKPLACE);
		this.record.setPosition(TEST_POSITION);
		
		
		
		assertEquals(TEST_IDENTITY, this.record.getCssIdentity());
		assertEquals(cssNodes.size(), this.record.getCssNodes().size());
		assertEquals(TEST_EMAIL, this.record.getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), this.record.getEntity());
		assertEquals(TEST_FORENAME, this.record.getForeName());
		assertEquals(TEST_NAME, this.record.getName());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), this.record.getSex());
		assertEquals(TEST_POSITION, this.record.getPosition());
		assertEquals(TEST_WORKPLACE, this.record.getWorkplace());
		
	}

	@After
	public void tearDown() throws Exception {
		cssNode_1 = null;
		cssNode_2 = null;
		cssNodes = null;
		this.record = null;
	}

	@Test
	public void testMessageBean() {
		CssManagerMessageBean bean = new CssManagerMessageBean();
		bean.setMethod(MethodType.LOGIN_CSS);
		bean.setProfile(this.record);
		
		assertNotNull(bean);
		assertEquals(bean.getMethod(), MethodType.LOGIN_CSS);
		assertNotNull(bean.getProfile());
		
		
		assertEquals(TEST_IDENTITY, bean.getProfile().getCssIdentity());
		assertEquals(cssNodes.size(), bean.getProfile().getCssNodes().size());
		assertEquals(TEST_EMAIL, bean.getProfile().getEmailID());
		assertEquals(CSSManagerEnums.entityType.Organisation.ordinal(), bean.getProfile().getEntity());
		assertEquals(TEST_FORENAME, bean.getProfile().getForeName());
		assertEquals(TEST_NAME, bean.getProfile().getName());
		assertEquals(CSSManagerEnums.genderType.Unspecified.ordinal(), bean.getProfile().getSex());
		assertEquals(TEST_POSITION, bean.getProfile().getPosition());
		assertEquals(TEST_WORKPLACE, bean.getProfile().getWorkplace());
	}

	@Test
	public void testMessageResultBean() {
		CssInterfaceResult result = new CssInterfaceResult();
		result.setResultStatus(false);
		result.setProfile(this.record);
		
		CssManagerResultBean resultBean = new CssManagerResultBean();
		resultBean.setResult(result);
		
		assertNotNull(resultBean);
		assertNotNull(resultBean.getResult());
		assertFalse(resultBean.getResult().isResultStatus());
		assertNotNull(resultBean.getResult().getProfile());
	}
}
