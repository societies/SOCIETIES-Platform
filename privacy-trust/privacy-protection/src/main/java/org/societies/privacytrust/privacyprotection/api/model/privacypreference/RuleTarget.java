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
package org.societies.privacytrust.privacyprotection.api.model.privacypreference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The RuleTarget is an XACML defined tag and encapsulates a Resource, a list of Actions and a list of Conditions. 
 * The RuleTarget defines the type of resource the rule applies to using the Resource class, the list of operations 
 * that can be performed on the Resource using the Action class and the list of conditions that should be met if 
 * access to the resources is to be allowed using the Condition class.  
 * @author Elizabeth
 *
 */
public class RuleTarget implements Serializable{
	private List<Subject> subjects;
	private Resource resource;
	private List<Action> actions;
	
	private RuleTarget(){
		this.subjects = new ArrayList<Subject>();
		this.actions = new ArrayList<Action>();
	}
	public RuleTarget(List<Subject> subjects, Resource resource, List<Action> actions){
		this.subjects = subjects;
		this.resource = resource;
		this.actions = actions;
	}
	
	public Resource getResource(){
		return this.resource;
		
	}
	
	public List<Subject> getSubjects(){
		return this.subjects;
	}
	
	public List<Action> getActions(){
		return this.actions;
	}
	
	public void addSubject(Subject subject){
		if (null==this.subjects){
			this.subjects = new ArrayList<Subject>();
		}
		if (!this.subjects.contains(subject)){
			this.subjects.add(subject);
		}
	
	}
	
	public void addAction(Action a){
		if (null==this.actions){
			this.actions = new ArrayList<Action>();
		}
		if (!this.actions.contains(a)){
			this.actions.add(a);
		}
	}
	
	public String toString(){
		String print = "RuleTarget:\n";
		print = print.concat("\tSubjects\n");
		for (Subject s : subjects){
			print = print.concat("\t\t"+s.toString()+"\n");
		}
		
		print = print.concat("Resource:\n");
		print = print.concat(this.resource.toString());
		print = print.concat("Actions:\n");
		for (Action a : actions){
			print = print.concat("\t\t"+a.toString()+"\n");
		}
		return print;
	}
}
