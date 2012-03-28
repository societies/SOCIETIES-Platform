/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.css.mgmt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.internal.css.management.CSSNode;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.utilities.DBC.Dbc;

import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.servicelifecycle.model.Service;

public class CSSManager implements ICSSLocalManager {
	private static Logger LOG = LoggerFactory.getLogger(CSSManager.class);

	private ICssRegistry cssRegistry;
	private ICssDirectoryRemote cssDirectoryRemote;
	private IServiceDiscovery serviceDiscovery;

	/**
	 * @return the cssRegistry
	 */
	public ICssRegistry getCssRegistry() {
		return cssRegistry;
	}

	/**
	 * @param cssRegistry
	 *            the cssRegistry to set
	 */
	public void setCssRegistry(ICssRegistry cssRegistry) {
		this.cssRegistry = cssRegistry;
	}

	/**
	 * @return the cssDiscoveryRemote
	 */
	public ICssDirectoryRemote getCssDiscoveryRemote() {
		return cssDirectoryRemote;
	}

	/**
	 * @param cssDiscoveryRemote
	 *            the cssDiscoveryRemote to set
	 */
	public void setCssDiscoveryRemote(ICssDirectoryRemote cssDiscoveryRemote) {
		this.cssDirectoryRemote = cssDiscoveryRemote;
	}

	/**
	 * @return the serviceDiscovery
	 */
	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	/**
	 * @param serviceDiscovery
	 *            the serviceDiscovery to set
	 */
	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

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
	/**
	 * Requires that CssRecord parameter has one node in its collection and that 
	 * the node corresponds to the node being logged in. The CSS identity and password
	 * must also be set to appropriate values
	 */
	public Future<CssInterfaceResult> loginCSS(CssRecord profile) {
		LOG.debug("Calling loginCSS");

		Dbc.require("CssRecord parameter cannot be null", profile != null);
		Dbc.require("Cssrecord parameter must contain CSS identity",
				profile.getCssIdentity() != null
						&& profile.getCssIdentity().length() > 0);
		Dbc.require("Cssrecord parameter must contain CSS password",
				profile.getPassword() != null
						&& profile.getPassword().length() > 0);

		CssInterfaceResult result = new CssInterfaceResult();
		result.setProfile(profile);
		result.setResultStatus(false);

		CssRecord record;
		try {
			record = this.cssRegistry.getCssRecord();
			if (profile.getCssIdentity().equals(record.getCssIdentity())
					&& profile.getPassword().equals(record.getPassword())) {
				// add new node to login to cloud CssRecord
				record.getCssNodes().add(profile.getCssNodes().get(0));
				// update the CSS registry
				this.cssRegistry.updateCssRecord(record);

				result.setProfile(record);
				result.setResultStatus(true);
			}
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> loginXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Requires that CssRecord parameter has one node in its collection and that 
	 * the node corresponds to the node being logged out.
	 */
	public Future<CssInterfaceResult> logoutCSS(CssRecord profile) {
		LOG.debug("Calling logoutCSS");

		Dbc.require("CssRecord parameter cannot be null", profile != null);
		Dbc.require("Cssrecord parameter must contain CSS identity",
				profile.getCssIdentity() != null
						&& profile.getCssIdentity().length() > 0);

		CssInterfaceResult result = new CssInterfaceResult();
		result.setProfile(profile);
		result.setResultStatus(false);

		CssRecord record;
		try {
			record = this.cssRegistry.getCssRecord();

			if (profile.getCssIdentity().equals(record.getCssIdentity())) {
				// remove new node to login to cloud CssRecord
				for (Iterator<CssNode> iter = record.getCssNodes().iterator(); iter
						.hasNext();) {
					CssNode node = (CssNode) iter.next();
					CssNode logoutNode = profile.getCssNodes().get(0);
					if (node.getIdentity().equals(logoutNode.getIdentity())
							&& node.getType() == logoutNode.getType()) {
						iter.remove();
						break;
					}
				}
				// update the CSS registry
				this.cssRegistry.updateCssRecord(record);

				result.setProfile(record);
				result.setResultStatus(true);
			}
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> logoutXMPPServer(CssRecord profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<CssInterfaceResult> modifyCssRecord(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		try {
			cssRegistry.unregisterCss(profile);
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> registerCSS(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		try {
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	@Override
	public Future<CssInterfaceResult> registerCSSNode(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		try {
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	@Async
	public Future<CssInterfaceResult> registerXMPPServer(CssRecord profile) {

		CssInterfaceResult result = new CssInterfaceResult();
		try {
			result = cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		CssInterfaceResult result = new CssInterfaceResult();
		try {
			cssRegistry.unregisterCss(profile);
			result.setResultStatus(true);

		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	public Future<CssInterfaceResult> unregisterCSSNode(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		try {
			cssRegistry.unregisterCss(profile);
			result.setResultStatus(true);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);

	}

	@Override
	public Future<CssInterfaceResult> unregisterXMPPServer(CssRecord profile) {
		CssInterfaceResult result = new CssInterfaceResult();
		try {
			cssRegistry.unregisterCss(profile);
			result.setResultStatus(true);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new AsyncResult<CssInterfaceResult>(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * addAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void addAdvertisementRecord(CssAdvertisementRecord record) {
		getCssDiscoveryRemote().addCssAdvertisementRecord(record);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * deleteAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void deleteAdvertisementRecord(CssAdvertisementRecord record) {
		getCssDiscoveryRemote().deleteCssAdvertisementRecord(record);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void updateAdvertisementRecord(CssAdvertisementRecord currentRecord,
			CssAdvertisementRecord updatedRecord) {
		getCssDiscoveryRemote().updateCssAdvertisementRecord(currentRecord,
				updatedRecord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public Future<List<CssAdvertisementRecord>> findAllCssAdvertisementRecords() {
		List<CssAdvertisementRecord> recordList = new ArrayList<CssAdvertisementRecord>();

		CssDirectoryRemoteClient callback = new CssDirectoryRemoteClient();

		getCssDiscoveryRemote().findAllCssAdvertisementRecords(callback);
		recordList = callback.getResultList();

		return new AsyncResult<List<CssAdvertisementRecord>>(recordList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.api.internal.css.management.ICSSLocalManager#
	 * updateAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public Future<List<Service>> findAllCssServiceDetails(
			List<CssAdvertisementRecord> listCssAds) {
		List<Service> serviceList = new ArrayList<Service>();
		Future<List<Service>> asyncResult = null;
		List<Service> cssServiceList = null;

		String currentSearchCss = new String();

		for (CssAdvertisementRecord cssAdd : listCssAds) {
			try {
				asyncResult = getServiceDiscovery().getServices(cssAdd.getId()); // TODO
																					// on
				cssServiceList = asyncResult.get();
				if (cssServiceList != null) {
					for (Service cssService : cssServiceList) {
						serviceList.add(cssService);
					}
					cssServiceList.clear();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceDiscoveryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return new AsyncResult<List<Service>>(serviceList);
	}

}
