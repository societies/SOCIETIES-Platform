/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM ISRAEL
 * SCIENCE AND TECHNOLOGY LTD (IBM), INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA
 * PERIORISMENIS EFTHINIS (AMITEC), TELECOM ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD
 * (NEC))
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
package org.societies.api.comm.xmpp.pubsub;

import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.identity.IIdentity;



/**
 * The Class Subscription.
 */
public class Subscription {

	// TODO subId is ignored for equals and hash
	/** The pubsub service. */
	private IIdentity pubsubService;
	
	/** The subscriber. */
	private IIdentity subscriber;
	
	/** The node. */
	private String node;
	
	/** The sub id. */
	private String subId;
	
	/**
	 * Instantiates a new subscription.
	 *
	 * @param pubsubService the pubsub service
	 * @param subscriber the subscriber
	 * @param node the node
	 * @param subId the sub id
	 */
	public Subscription(IIdentity pubsubService, IIdentity subscriber,
			String node, String subId) {
		super();
		this.pubsubService = pubsubService;
		this.subscriber = subscriber;
		this.node = node;
		this.subId = subId;
	}
	
	/**
	 * Gets the pubsub service.
	 *
	 * @return the pubsub service
	 */
	public IIdentity getPubsubService() {
		return pubsubService;
	}
	
	/**
	 * Sets the pubsub service.
	 *
	 * @param pubsubService the new pubsub service
	 */
	public void setPubsubService(IIdentity pubsubService) {
		this.pubsubService = pubsubService;
	}
	
	/**
	 * Gets the subscriber.
	 *
	 * @return the subscriber
	 */
	public IIdentity getSubscriber() {
		return subscriber;
	}
	
	/**
	 * Sets the subscriber.
	 *
	 * @param subscriber the new subscriber
	 */
	public void setSubscriber(IIdentity subscriber) {
		this.subscriber = subscriber;
	}
	
	/**
	 * Gets the node.
	 *
	 * @return the node
	 */
	public String getNode() {
		return node;
	}
	
	/**
	 * Sets the node.
	 *
	 * @param node the new node
	 */
	public void setNode(String node) {
		this.node = node;
	}
	
	/**
	 * Gets the sub id.
	 *
	 * @return the sub id
	 */
	public String getSubId() {
		return subId;
	}
	
	/**
	 * Sets the sub id.
	 *
	 * @param subId the new sub id
	 */
	public void setSubId(String subId) {
		this.subId = subId;
	}
	
	/**
	 * Generate a hash code representation.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result
				+ ((pubsubService == null) ? 0 : pubsubService.hashCode());
		result = prime * result
				+ ((subscriber == null) ? 0 : subscriber.hashCode());
		return result;
	}
	
	/**
	 * Equals.
	 *
	 * @param obj the object to compare
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subscription other = (Subscription) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (pubsubService == null) {
			if (other.pubsubService != null)
				return false;
		} else if (!pubsubService.equals(other.pubsubService))
			return false;
		if (subscriber == null) {
			if (other.subscriber != null)
				return false;
		} else if (!subscriber.equals(other.subscriber))
			return false;
		return true;
	}

}
