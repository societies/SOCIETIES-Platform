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
package org.societies.api.context.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Base class for representing context model objects. This class defines methods
 * for accessing information common to all <code>CtxModelOject</code>
 * implementations. More specifically, every context model object can be referenced
 * by its {@link CtxIdentifierBean}. In addition, upon modification of a CtxModelOject
 * the last modification time is updated.   
 * 
 * @see CtxModelTypeBean
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model", propOrder= {"id", "lastModified"})
public abstract class CtxModelObjectBean implements Serializable {

	private static final long serialVersionUID = 7349640661605024918L;
	
	/** The identifier of this context model object. */
	@XmlElement(required = true, nillable=false)
	private /* final */ CtxIdentifierBean id;
	
	/** The last modification time of this context model object. */
	@XmlElement(required = true, nillable=false)
	private Date lastModified;

	CtxModelObjectBean() {}
	
	/**
	 * Constructs a CtxModelObjectBean with the specified identifier
	 * 
	 * @param id
	 *            the identifier of the newly created context model object
	 */
	CtxModelObjectBean(CtxIdentifierBean id) {
		this.id = id;
	}

	/**
	 * Returns the identifier of this context model object
     * 
     * @return the identifier of this context model object
     * @see CtxIdentifierBean
	 */
	public CtxIdentifierBean getId(){
		return this.id;
	}

	/**
	 * Returns the last modification time of this context model object
	 * 
	 * @return the last modification time of this context model object
	 */
	public Date getLastModified(){
		return this.lastModified;
	}
	
	void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	/**
	 * Returns the model type of this context model object, i.e. Entity,
	 * Attribute or Association
	 * 
	 * @return the enum constant for the context model type
	 * @see CtxIdentifierBean#getModelType()
	 */
	public CtxModelTypeBean getModelType() {
	    return this.getId().getModelType();
	}
	
	/**
	 * Returns the semantic tag (e.g. "person") of this context model object
	 * 
	 * @return the semantic tag of this context model object
	 * @see CtxIdentifierBean#getType()
	 */
	public String getType() {
		return this.getId().getType();
	}
	
	/**
	 * Returns the numeric part of this context model object identifier 
	 * 
	 * @return the numeric part of this context model object identifier
	 * @see CtxIdentifierBean#getObjectNumber()
	 */
	public Long getObjectNumber() {
		return this.getId().getObjectNumber();
	}
	
	/* TODO
	 * Returns a String representation of this context model object
     * 
     * @return a String representation of this context model object
     * 
	@Override
	public String toString() {
	}*/
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @since 0.0.2
	 */
	@Override
    public int hashCode() {
		
        final int prime = 31;
        int result = 1;
        
        result = prime * result
                + ((this.id == null) ? 0 : this.id.hashCode());
        
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 0.0.2
     */
    @Override
    public boolean equals(Object that) {
    	
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (this.getClass() != that.getClass())
            return false;
        
        CtxModelObjectBean other = (CtxModelObjectBean) that;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        
        return true;
    }
}