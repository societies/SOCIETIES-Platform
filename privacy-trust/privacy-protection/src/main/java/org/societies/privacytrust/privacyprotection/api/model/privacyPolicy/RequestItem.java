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
package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.privacytrust.privacyprotection.api.model.privacypreference.Action;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.Condition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.Resource;

/**
 * The RequestItem class is used to represent a request to access a specific piece of personal data. 
 * It is embedded inside a RequestPolicy which is the privacy policy of a service provider. 
 * The RequestItem contains a Resource object, a list of Action objects, a list of Conditions (obligations) 
 * and a flag that declares the request as optional. 
 * @author Elizabeth
 *
 */
public class RequestItem implements Serializable{
	
	private Resource resource;
	private List<Action> actions;
	private List<Condition> conditions;
	private boolean optional;

	private RequestItem(){
		this.actions = new ArrayList<Action>();
		this.conditions = new ArrayList<Condition>();
		this.optional = false;
	}
	public RequestItem(Resource r, List<Action> actions, List<Condition> conditions){
		this.resource = r;
		this.actions = actions;
		this.conditions = conditions;
		this.optional = false;
	}
	
	public RequestItem(Resource r, List<Action> actions, List<Condition> conditions, boolean isOptional){
		this.resource = r;
		this.actions = actions;
		this.conditions = conditions;
		this.optional = isOptional;
	}
	
	public Resource getResource(){
		return this.resource;
	}
	
	public List<Action> getActions(){
		return this.actions;
	}
	
	public List<Condition> getConditions(){
		return this.conditions;
	}

	public void setConditions(List<Condition> conditions){
		this.conditions = conditions;
	}
	
	public void setActions(List<Action> actions){
		this.actions = actions;
	}
	public String toXMLString(){
		String str = "\n<Target>";
		str = str.concat(this.resource.toXMLString());
		for (Action action : actions){
			str = str.concat(action.toXMLString());
		}
		for (Condition con : conditions){
			str = str.concat(con.toXMLString());
		}
		str = str.concat(this.printOptional());
		str = str.concat("\n</Target>");
		return str;
	}
	
	public boolean isOptional(){
		return this.optional;
	}
	
	public void setOptional(boolean isOptional){
		this.optional = isOptional;
	}
	private String printOptional(){
		return "\n<optional>"+this.optional+"</optional>";
	}
	public String toString(){
		return this.toXMLString();
	}
}
