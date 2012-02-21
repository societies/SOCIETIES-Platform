/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.api.internal.servicelifecycle.serviceMgmt;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.societies.api.internal.servicelifecycle.serviceMgmt.IServiceManagement;
import org.societies.api.internal.servicelifecycle.serviceMgmt.ServiceMgmtException;
import org.springframework.osgi.context.BundleContextAware;

/**
 * This class reads service metadata file URL and pass to service Management
 * component for further processing
 * 
 * @author pkuppuud
 * 
 */
public class ServiceMgmtMetadataBroker implements BundleContextAware {

	private List<String> serviceMetafileLocation;
	private IServiceManagement serviceMgmt;
	private BundleContext bctx;

	public ServiceMgmtMetadataBroker() {

	}

	public List<String> getServiceMetafileLocation() {
		return serviceMetafileLocation;
	}

	public void setServiceMetafileLocation(List<String> serviceMetafileLocation) {
		this.serviceMetafileLocation = serviceMetafileLocation;
	}

	public IServiceManagement getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServiceManagement serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public void registerData() {

		if (null == getBundleContext()) {
			System.out.println("BundleContext is not set in ServiceMgmtMetatdata broker");
			System.out.println("Service Metadata file(s) not processed");
			return;
		}

		if (!(getServiceMetafileLocation().size() > 0)) {
			System.out.println("No metadata file provided");
			return;
		}

		Bundle bndl = getBundleContext().getBundle();
		System.out.println("Registering Service Metadata for Bundle Symbolic name:"
						+ bndl.getSymbolicName()
						+ " Bundle Id: "
						+ bndl.getBundleId());

		List<URL> fileURL = new ArrayList<URL>();
		System.out.println("Reading service meta data files");
		try {
			Iterator<String> iterator = getServiceMetafileLocation().iterator();
			while (iterator.hasNext()) {
				fileURL.add(bndl.getResource(iterator.next()));
			}
			if (fileURL.size() > 0) {
				System.out.println("Passing URL to process in service mgmt");
				getServiceMgmt().processServiceMetaData(fileURL,
						bndl.getBundleId(), bndl.getSymbolicName());
			}
		} catch (ServiceMgmtException e) {
			System.out.println("error from registerData...");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BundleContext getBundleContext() {
		return this.bctx;
	}

	@Override
	public void setBundleContext(BundleContext bctx) {
		this.bctx = bctx;
	}

}
