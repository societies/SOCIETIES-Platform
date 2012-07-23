/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.css.cssRegistry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

/**
 * This is the Class accepted by the CssRegistry when a css wants to register nodes.
 * 
 */

@Entity
@Table(name = "CssNodeEntry")
public class CssNodeEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8165777485272476371L;
	
	private String nodeIdentity;
	private int status;
	private int type;
	private String cssnodeMAC;
	private String interactable;
	private boolean archived;
	/**
	 * @return the identity
	 */
	@Id
	@Column(name = "NodeIdentity")
	public String getNodeIdentity() {
		return nodeIdentity;
	}
	/**
	 * @param identity the identity to set
	 */
	public void setNodeIdentity(String nodeIdentity) {
		this.nodeIdentity = nodeIdentity;
	}
	/**
	 * @return the status
	 */
	@Column(name = "Status")
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the type
	 */
	@Column(name = "Type")
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * @return the node MAC Address
	 */
	@Column(name = "cssNodeMAC")
	public String getcssNodeMAC() {
		return cssnodeMAC;
	}
	/**
	 * @param the node MAC Address to set
	 */
	public void setcssNodeMAC(String cssNodeMAC) {
		this.cssnodeMAC = cssNodeMAC;
	}
	
	
	/**
	 * @return the interactable flag
	 */
	
	
	@Column(name = "Interactable")
	public String getInteractable() {
		return interactable;
	}
	/**
	 * @param sets whether the node is interactable or not
	 */
	public void setInteractable(String interactable) {
		this.interactable = interactable;
	}
	
	/**
	 * @return the archived flag
	 */
	@Column(name = "Archived")
	public boolean getArchived() {
		return archived;
	}
	/**
	 * @param type the type to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	/**
	 * @param identity
	 * @param status
	 * @param type
	 */
	public CssNodeEntry(String nodeIdentity, int status, int type, String nodeMac, String interactable, boolean archived) {
		super();
		this.nodeIdentity = nodeIdentity;
		this.status = status;
		this.type = type;
		this.cssnodeMAC = nodeMac;
		this.interactable = interactable;
		this.archived = archived;
	}
    
	public CssNodeEntry() {
		super();
	}
	

	

	
}
