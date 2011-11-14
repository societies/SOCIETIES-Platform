package org.societies.context.model.api;

import java.io.Serializable;
import java.util.Date;

public abstract class CtxModelObject implements Serializable {

	private static final long serialVersionUID = 7349640661605024918L;
	
	private CtxIdentifier id;
	private Date lastModified;

	CtxModelObject() {}

	public CtxIdentifier getId(){
		return this.id;
	}

	public Date getLastModified(){
		return this.lastModified;
	}
}
