package org.societies.orchestration.cpa.test;

import org.junit.Test;
import org.societies.api.cis.management.ICisParticipant;
import org.societies.orchestration.cpa.impl.CPACreationPatterns;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
@ContextConfiguration(locations = { "../../../../../CPAUnitTest-context.xml" })
public class CPAUnitTest  extends AbstractTransactionalJUnit4SpringContextTests {
	@Test
	public void cpaTestPlaceHolder(){
		CPACreationPatterns pa = new CPACreationPatterns();
		ICisParticipant m1 = null; 
		ICisParticipant m2 = null;
		assert(pa.cooperation(m1, m2) == 0);
	}
}
