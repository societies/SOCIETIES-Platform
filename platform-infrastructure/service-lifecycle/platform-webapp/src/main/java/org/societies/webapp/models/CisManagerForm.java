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
package org.societies.webapp.models;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class CisManagerForm {

	private String method;
	private String cssId;
	private String cisPassword;
	private String cisName;
	private String cisType;
	private Integer cisMode;
	private String cisJid;
	private String role;
	
	private String attribute;
	private String operator;
	private String value;

	/*private crit[] critList;
	
	public class crit{
		public String attr;
		public String op;
		public String val;
		public String getAttr() {
			return attr;
		}
		public void setAttr(String attr) {
			this.attr = attr;
		}
		public String getOp() {
			return op;
		}
		public void setOp(String op) {
			this.op = op;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		
		
	}*/
	
	
	public CisManagerForm(){
		//critList = new crit[10];
	}
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the cssId
	 */
	public String getCssId() {
		return cssId;
	}
	/**
	 * @param cssId the cssId to set
	 */
	public void setCssId(String cisId) {
		this.cssId = cisId;
	}
	/**
	 * @return the cisPassword
	 */
	public String getCisPassword() {
		return cisPassword;
	}
	/**
	 * @param cisPassword the cisPassword to set
	 */
	public void setCisPassword(String cisPassword) {
		this.cisPassword = cisPassword;
	}
	/**
	 * @return the cisName
	 */
	public String getCisName() {
		return cisName;
	}
	/**
	 * @param cisName the cisName to set
	 */
	public void setCisName(String cisName) {
		this.cisName = cisName;
	}
	/**
	 * @return the cisType
	 */
	public String getCisType() {
		return cisType;
	}
	/**
	 * @param cisType the cisType to set
	 */
	public void setCisType(String cisType) {
		this.cisType = cisType;
	}
	/**
	 * @return the cisMode
	 */
	public Integer getCisMode() {
		return cisMode;
	}
	/**
	 * @param cisMode the cisMode to set
	 */
	public void setCisMode(Integer cisMode) {
		this.cisMode = cisMode;
	}
	
	public String getCisJid() {
		return cisJid;
	}
	public void setCisJid(String cisJid) {
		this.cisJid = cisJid;
	}
}
