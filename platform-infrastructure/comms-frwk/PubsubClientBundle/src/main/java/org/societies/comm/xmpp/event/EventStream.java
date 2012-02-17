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
package org.societies.comm.xmpp.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class EventStream implements ApplicationEventMulticaster {
	
	protected String node;
	protected ApplicationEventMulticaster multicaster;
	
	
	public EventStream(String node, ApplicationEventMulticaster multicaster) {
		this.node = node;
		this.multicaster = multicaster;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.context.event.ApplicationEventMulticaster#addApplicationListener(org.springframework.context.ApplicationListener) */
	@Override
	public void addApplicationListener(ApplicationListener listener) {
		multicaster.addApplicationListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.event.ApplicationEventMulticaster#addApplicationListenerBean(java.lang.String) */
	@Override
	public void addApplicationListenerBean(String listener) {
		multicaster.addApplicationListenerBean(listener);
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.event.ApplicationEventMulticaster#multicastEvent(org.springframework.context.ApplicationEvent) */
	@Override
	public void multicastEvent(ApplicationEvent event) {
		if (event instanceof InternalEvent) {
			InternalEvent ie = (InternalEvent)event;
			ie.setEventNode(this.node);
			multicaster.multicastEvent(ie);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.event.ApplicationEventMulticaster#removeAllListeners() */
	@Override
	public void removeAllListeners() {
		multicaster.removeAllListeners();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.event.ApplicationEventMulticaster#removeApplicationListener(org.springframework.context.ApplicationListener) */
	@Override
	public void removeApplicationListener(ApplicationListener listener) {
		multicaster.removeApplicationListener(listener);
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.event.ApplicationEventMulticaster#removeApplicationListenerBean(java.lang.String)  */
	@Override
	public void removeApplicationListenerBean(String listener) {
		multicaster.removeApplicationListenerBean(listener);
	}
}
