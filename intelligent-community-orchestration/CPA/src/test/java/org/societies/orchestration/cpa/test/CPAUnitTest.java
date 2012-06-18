package org.societies.orchestration.cpa.test;

import java.util.ArrayList;

import org.junit.Test;
import org.societies.api.activity.IActivity;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.orchestration.cpa.impl.CPACreationPatterns;
import org.societies.orchestration.cpa.impl.SocialGraphVertex;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
@ContextConfiguration(locations = { "../../../../../CPAUnitTest-context.xml" })
public class CPAUnitTest  extends AbstractTransactionalJUnit4SpringContextTests {
	@Test
	public void cpaTestPlaceHolder(){
		CPACreationPatterns pa = new CPACreationPatterns();
		SocialGraphVertex m1 = null; 
		SocialGraphVertex m2 = null;
		assert(pa.cooperation(m1, m2, new ArrayList<IActivity>()) == 0);
	}
}
