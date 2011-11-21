package org.societies.context.model.api;

import java.io.Serializable;
import java.util.Date;

public class CtxQuality implements Serializable {

	private static final long serialVersionUID = -5345272592676091780L;
	
	private Date lastUpdated;
	private CtxOriginType originType;
	private Double precision;
	
	private CtxQuality() {}

	public Date getLastUpdated(){
		return this.lastUpdated;
	}
	
	public long getFreshness() {
		return new Date().getTime() - this.lastUpdated.getTime();
	}
	
	public CtxOriginType getOriginType() {
		return this.originType;
	}
	
	public void setOriginType(CtxOriginType originType) {
		this.originType = originType;
	}

	public Double getPrecision() {
		return new Double(this.precision);
	}

	/**
	 * 
	 * @param precision
	 */
	public void setPrecision(Double precision){
		this.precision = new Double(precision);
	}
}