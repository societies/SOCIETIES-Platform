package org.societies.webapp.models;

import javax.validation.constraints.Pattern;

@Deprecated // No longer used after move from JSP to JSF
public class AddActivityForm {

	@Pattern(regexp="cis[A-Za-z0-9._-]+.[A-Za-z0-9._-]",
			message="Invalid jid.")
	private String cisId;
	private String verb;
	private String object;
	
	public AddActivityForm() {
		// TODO Auto-generated constructor stub

	}

	public String getCisId() {
		return cisId;
	}

	public void setCisId(String cisId) {
		this.cisId = cisId;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}


	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	
	

}
