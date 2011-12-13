/**
 * We use the definition of an Activity from www.activitystrea.ms. "The Activity
 * in ActivityStreams is a description of an action that was performed (the verb)
 * at some instant in time by someone or something (the actor) against some kind
 * of person, place, or thing (the object). There may also be a target (like a 
 * photo album or wishlist) involved".
 * 
 * @author Babak Farshchian
 * @version 0
 */
package org.societies.cis.management.api;

public class CISActivity {
	public String verb;
	public String actor;
	public String object;
	public String target;

	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
}
