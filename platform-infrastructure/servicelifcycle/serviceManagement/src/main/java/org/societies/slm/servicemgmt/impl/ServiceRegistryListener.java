/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
package org.societies.slm.servicemgmt.impl;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.util.OsgiListenerUtils;
/**
 * 
 * @author pkuppuud
 *
 */
public class ServiceRegistryListener implements BundleContextAware,
		ServiceListener {

	private BundleContext bctx;

	public ServiceRegistryListener() {

	}

	public void registerListener() {
		Filter fltr = null;
		try {
			fltr = this.bctx.createFilter("(TargetPlatform=SOCIETIES)");
		} catch (InvalidSyntaxException e) {
			System.out.println("Error creating Service Listener Filter");
			e.printStackTrace();
		}
		OsgiListenerUtils.addServiceListener(this.bctx, this, fltr);
	}
	
	public void unRegisterListener() {
		
		OsgiListenerUtils.removeServiceListener(this.bctx, this);
	}

	@Override
	public void setBundleContext(BundleContext ctx) {
		this.bctx = ctx;
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		
		System.out.println("Service Listener event received");
		Bundle serBndl=event.getServiceReference().getBundle();
		String propKeys[]=event.getServiceReference().getPropertyKeys();
		
		for (String key:  propKeys){
			System.out.println("Property Key"+key);
			System.out.println("Property value"+event.getServiceReference().getProperty(key));			
		}
		System.out.println("Bundle Id"+serBndl.getBundleId()+
				"Bundle State"+serBndl.getState()+
				"Bundle Symbolic Name"+serBndl.getSymbolicName());

		if (event.getType() == ServiceEvent.MODIFIED) {
			System.out.println("Service Modification");
			// do something here
		} else if (event.getType() == ServiceEvent.REGISTERED) {
			System.out.println("Service Registered");
			// do something here
		} else if (event.getType() == ServiceEvent.UNREGISTERING) {
			System.out.println("Service Unregistered");
			// do something here
		}
	}
}
