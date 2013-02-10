package org.societies.android.platform.content.container.test;
import org.societies.android.platform.content.ProviderImplemenation;

import android.test.ProviderTestCase2;

public class TestCSSContainerProvider extends ProviderTestCase2<ProviderImplemenation> {
	private static final String PROVIDER_AUTHORITY = "org.societies.android.platform.content.androidcssmanager";
	
	public TestCSSContainerProvider() {
		super(ProviderImplemenation.class, PROVIDER_AUTHORITY);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
