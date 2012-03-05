package org.societies.css.mgmt;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.utilities.DBC.Dbc;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class CSSManager implements ICSSLocalManager{
	private static Logger LOG = LoggerFactory.getLogger(CSSManager.class);

	/**
	 * Default constructor
	 */
	public CSSManager() {
		LOG.debug("CSS Manager initialised");
	}

	@Override
	public Future<CssInterfaceResult> changeCSSNodeStatus(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> getCssRecord() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> loginCSS(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> loginXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> logoutCSS(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> logoutXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> modifyCssRecord(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> registerCSS(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> registerCSSNode(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Async
	public Future<CssInterfaceResult> registerXMPPServer(CssRecord profile) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Dbc.require("Profile cannot be null", profile != null);
		LOG.debug("registerXMPPServer invoked");
		LOG.debug("Thread: " + Thread.currentThread());
		
		CssInterfaceResult result = new CssInterfaceResult();
		result.setResultStatus(true);
		profile.setCssRegistration("2012-02-23");
		profile.setName("TestCSS");
		result.setProfile(profile);
		
		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> setPresenceStatus(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> synchProfile(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> unregisterCSS(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> unregisterCSSNode(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> unregisterXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}


}
