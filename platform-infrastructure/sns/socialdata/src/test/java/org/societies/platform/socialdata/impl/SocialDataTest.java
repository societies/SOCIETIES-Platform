package org.societies.platform.socialdata.impl;

import junit.framework.TestCase;

import org.societies.api.internal.sns.ISocialData;
import org.societies.platform.socialdata.SocialData;

public class SocialDataTest extends TestCase {

    public void testBeanIsABean() {
	   ISocialData socialData = new SocialData();
       assertNotNull(socialData);
    }

}