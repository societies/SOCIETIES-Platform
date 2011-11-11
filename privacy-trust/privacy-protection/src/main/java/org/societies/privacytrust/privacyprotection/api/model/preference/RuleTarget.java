package org.societies.privacytrust.privacyprotection.api.model.preference;

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
