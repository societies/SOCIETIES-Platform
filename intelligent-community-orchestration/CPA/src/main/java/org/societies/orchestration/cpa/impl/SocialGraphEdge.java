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
package org.societies.orchestration.cpa.impl;

import org.societies.api.internal.orchestration.ISocialGraphEdge;
import org.societies.api.internal.orchestration.ISocialGraphVertex;

public class SocialGraphEdge implements ISocialGraphEdge {
	private double weight;
	private SocialGraphVertex from;
	private SocialGraphVertex to;
	public SocialGraphEdge(){
		weight = 0;
		from = null; to = null;
	}
	public SocialGraphEdge(SocialGraphVertex from, SocialGraphVertex to){
		this.from = from; this.to = to;
	}
	public SocialGraphEdge(SocialGraphVertex from, SocialGraphVertex to, double weight){
		this.weight = weight;
		this.from = from; this.to = to;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public ISocialGraphVertex getFrom() {
		return from;
	}
	public void setFrom(SocialGraphVertex from) {
		this.from = from;
	}
	public ISocialGraphVertex getTo() {
		return to;
	}
	public void setTo(SocialGraphVertex to) {
		this.to = to;
	}
	@Override
	public boolean equals(Object o){
		if(!o.getClass().equals(SocialGraphEdge.class))
			return false;
		SocialGraphEdge e = (SocialGraphEdge)o;
		long l = e.getFrom().hashCode()+e.getTo().hashCode();
		long l2 = this.getFrom().hashCode()+this.getTo().hashCode();
		return l==l2;
	}
	public void addToWeight(double dw){
		weight += dw;
	}
	public String toString(){
		return "E:"+weight;
	}
}
