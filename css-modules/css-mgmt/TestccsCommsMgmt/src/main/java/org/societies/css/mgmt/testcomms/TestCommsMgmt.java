package org.societies.css.mgmt.testcomms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.css.management.ICSSManagerCallback;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssProfile;

public class TestCommsMgmt {

	private ICSSRemoteManager remoteCSSManager;
	private static Logger LOG = LoggerFactory.getLogger(TestCommsMgmt.class);

	
	public void startTest() {
		
		
		for (int i = 0; i < 20; i++) {
			
			this.remoteCSSManager.registerXMPPServer(new CssProfile(), new ICSSManagerCallback() {
				
				@Override
				public void receiveResult(CssInterfaceResult result) {
					LOG.info("Received result from remote call");
					LOG.info("Result Status: " + result.isResultStatus());
					
				}
			});
			
		}
	}

	//Spring injection methods

	public ICSSRemoteManager getRemoteCSSManager() {
		return remoteCSSManager;
	}

	public void setRemoteCSSManager(ICSSRemoteManager remoteCSSManager) {
		this.remoteCSSManager = remoteCSSManager;
	}


}
