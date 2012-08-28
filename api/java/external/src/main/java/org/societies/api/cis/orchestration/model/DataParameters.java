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
package org.societies.api.cis.orchestration.model;

import java.util.ArrayList;
import java.util.List;


import org.societies.api.identity.IIdentity;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * This class should be 
 *
 * @author Eliza
 *
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public class DataParameters {

	private List<String> primaryFilters;
	private List<String> secondaryFilters;
	private  int limit;
	private List<IIdentity> ciss;
	private  boolean scopeAllCisSubscribed;
	
	private DataParameters(){
		primaryFilters = new ArrayList<String>();
		secondaryFilters = new ArrayList<String>();
		this.ciss = new ArrayList<IIdentity>();
		this.limit = 0;
		this.setScopeAllCisSubscribed(false);
	}
	public DataParameters(List<String> primaryFilters, List<String> secondaryFilters, int limit){
		this();
		this.primaryFilters.addAll(primaryFilters);
		this.secondaryFilters.addAll(secondaryFilters);
		this.limit = limit;

	}
	
	public DataParameters(List<String> primaryFilters, List<String> secondaryFilters, int limit, List<IIdentity> ciss){
		this(primaryFilters,secondaryFilters,limit);
		
		this.ciss.addAll(ciss);
	}
	
	public DataParameters(List<String> primaryFilters, List<String> secondaryFilters, int limit, boolean allsubscribedCissScope){
		this(primaryFilters, secondaryFilters,limit);
		this.setScopeAllCisSubscribed(allsubscribedCissScope);
		
	}
	public void addPrimaryFilter(String filter){
		this.primaryFilters.add(filter);
	}
	
	public void addSecondaryFilter(String filter){
		this.secondaryFilters.add(filter);
	}
	
	public void addScopeCis(IIdentity cisScope){
		this.ciss.add(cisScope);
	}
	/**
	 * @return the primaryFilters
	 */
	public List<String> getPrimaryFilters() {
		return primaryFilters;
	}
	/**
	 * @param primaryFilters the primaryFilters to set
	 */
	public void setPrimaryFilters(List<String> primaryFilters) {
		this.primaryFilters = primaryFilters;
	}
	/**
	 * @return the secondaryFilters
	 */
	public List<String> getSecondaryFilters() {
		return secondaryFilters;
	}
	/**
	 * @param secondaryFilters the secondaryFilters to set
	 */
	public void setSecondaryFilters(List<String> secondaryFilters) {
		this.secondaryFilters = secondaryFilters;
	}
	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @return the scopeAllCisSubscribed
	 */
	public boolean isScopeAllCisSubscribed() {
		return scopeAllCisSubscribed;
	}
	/**
	 * @param scopeAllCisSubscribed the scopeAllCisSubscribed to set
	 */
	public void setScopeAllCisSubscribed(boolean scopeAllCisSubscribed) {
		this.scopeAllCisSubscribed = scopeAllCisSubscribed;
	}



}
