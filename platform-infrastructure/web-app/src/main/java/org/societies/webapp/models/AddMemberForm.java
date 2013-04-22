package org.societies.webapp.models;

import javax.validation.constraints.Pattern;

@Deprecated // No longer used after move from JSP to JSF
public class AddMemberForm {

	@Pattern(regexp="cis[A-Za-z0-9._-]+.[A-Za-z0-9._-]",
			message="Invalid jid.")
	private String cssJid;
	@Pattern(regexp="cis[A-Za-z0-9._-]+.[A-Za-z0-9._-]",
			message="Invalid jid.")
	private String cisJid;
	
	public AddMemberForm() {
		// TODO Auto-generated constructor stub

	}

	public String getCssJid() {
		return cssJid;
	}

	public void setCssJid(String cssJid) {
		this.cssJid = cssJid;
	}

	public String getCisJid() {
		return cisJid;
	}

	public void setCisJid(String cisJid) {
		this.cisJid = cisJid;
	}


	
	

}
