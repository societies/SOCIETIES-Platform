package org.societies.personalisation.CRISTTesting.impl;

import junit.framework.TestCase;
import org.societies.personalisation.CRISTTesting.Bean;

public class BeanImplTest extends TestCase {

    public void testBeanIsABean() {
	Bean aBean = new BeanImpl();
        assertTrue(aBean.isABean());
    }

}