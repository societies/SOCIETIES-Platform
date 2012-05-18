/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.orchestration.api;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
//import org.societies.api.cis.management.ICis;
import org.societies.orchestration.api.ICis;
import org.societies.orchestration.api.SuggestedCommunityAnalyserMethodType;

import org.societies.api.activity.IActivity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.css.management.ICssActivity;
import org.societies.api.identity.IIdentity;

import java.util.ArrayList;
import java.util.HashMap;


public class SuggestedCommunityAnalyserBean {

	private SuggestedCommunityAnalyserMethodType method;
	private HashMap<String, ArrayList<ICisProposal>> ciss;
	private HashMap<String, ArrayList<ArrayList<ICisProposal>>> configureCiss;
	private ArrayList<String> cissMetadata;
	
	private ArrayList<IIdentity> cssList;
	private ArrayList<CtxAttribute> sharedContextAttributes;
	private ArrayList<CtxAssociation> sharedContextAssociations;
	private ArrayList<ICssActivity> sharedCssActivities;
	private ArrayList<IActivity> sharedCisActivities;
	
	public SuggestedCommunityAnalyserBean() {
		
	}
	
	public SuggestedCommunityAnalyserMethodType getMethod(){
		return method;
	}

	public void setMethod(SuggestedCommunityAnalyserMethodType method){
		this.method = method;
	}
	
	public HashMap<String, ArrayList<ICisProposal>> getCiss(){
		return this.ciss;
	}
	
	public void setCiss(HashMap<String, ArrayList<ICisProposal>> ciss){
		this.ciss = ciss;
	}
	
	public HashMap<String, ArrayList<ArrayList<ICisProposal>>> getConfigureCiss(){
		return this.configureCiss;
	}
	
	public void setConfigureCiss(HashMap<String, ArrayList<ArrayList<ICisProposal>>> configureCiss){
		this.configureCiss = configureCiss;
	}
	
	public ArrayList<String> getCissMetadata(){
		return this.cissMetadata;
	}
	
	public void setCissMetadata(ArrayList<String> cissMetadata){
		this.cissMetadata = cissMetadata;
	}
	
	public ArrayList<IIdentity> getCssList(){
		return this.cssList;
	}
	
	public void setCssList(ArrayList<IIdentity> cssList){
		this.cssList = cssList;
	}
	
	public ArrayList<CtxAttribute> getSharedContextAttributes(){
		return this.sharedContextAttributes;
	}
	
	public void setSharedContextAttributes(ArrayList<CtxAttribute> sharedContextAttributes){
		this.sharedContextAttributes = sharedContextAttributes;
	}
	
	public ArrayList<CtxAssociation> getSharedContextAssociations(){
		return this.sharedContextAssociations;
	}
	
	public void setSharedContextAssociations(ArrayList<CtxAssociation> sharedContextAssociations){
		this.sharedContextAssociations = sharedContextAssociations;
	}
	
	public ArrayList<ICssActivity> getSharedCssActivities(){
		return this.sharedCssActivities;
	}
	
	public void setSharedCssActivities(ArrayList<ICssActivity> sharedCssActivities){
		this.sharedCssActivities = sharedCssActivities;
	}
	
	public ArrayList<IActivity> getSharedCisActivities(){
		return this.sharedCisActivities;
	}
	
	public void setSharedCisActivities(ArrayList<IActivity> sharedCisActivities){
		this.sharedCisActivities = sharedCisActivities;
	}
	
}
