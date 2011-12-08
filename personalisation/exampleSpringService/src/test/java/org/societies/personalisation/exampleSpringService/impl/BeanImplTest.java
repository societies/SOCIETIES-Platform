package org.societies.personalisation.exampleSpringService.impl;

import junit.framework.TestCase;
import org.societies.personalisation.exampleSpringService.Bean;

public class BeanImplTest extends TestCase {

    public void testBeanIsABean() {
	Bean aBean = new BeanImpl();
        assertTrue(aBean.isABean());
    }

}