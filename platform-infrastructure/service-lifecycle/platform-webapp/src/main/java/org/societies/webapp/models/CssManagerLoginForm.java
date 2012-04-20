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
/**
 * 
 */
package org.societies.webapp.models;



import javax.validation.constraints.Size;

import org.societies.webapp.models.requests.CssRequestModel;

/**
 * 
 * @author Maria Mannion
 * 
 *         This is a data model class for login into a Css Manager .
 */
public class CssManagerLoginForm {

	
	/* Spring does't handle dynamic lists very well!
	 * Because of time constraints the code below was implemented for the demo 
	 */
	
	CssRequestModel cssAdRequests1 = new CssRequestModel();
	CssRequestModel cssAdRequests2 = new CssRequestModel();
	CssRequestModel cssAdRequests3 = new CssRequestModel();
	CssRequestModel cssAdRequests4 = new CssRequestModel();
	CssRequestModel cssAdRequests5 = new CssRequestModel();
	
	CssRequestModel cssRequests1 = new CssRequestModel();
	CssRequestModel cssRequests2 = new CssRequestModel();
	CssRequestModel cssRequests3 = new CssRequestModel();
	CssRequestModel cssRequests4 = new CssRequestModel();
	CssRequestModel cssRequests5 = new CssRequestModel();
	
	
	CssRequestModel cssFriendRequests1 = new CssRequestModel();
	CssRequestModel cssFriendRequests2 = new CssRequestModel();
	CssRequestModel cssFriendRequests3 = new CssRequestModel();
	CssRequestModel cssFriendRequests4 = new CssRequestModel();
	CssRequestModel cssFriendRequests5 = new CssRequestModel();
	


			
		   

		@Size(min = 1, max = 20)
	    private String password;
	    @Size(min = 1, max = 50)
	    private String cssIdentity;
	    
		//Radio buttons for css friend requests - sending
	    

	
	    
	    public void setPassword(String password) {
	            this.password = password;
	    }
	    public String getPassword() {
	            return password;
	    }
	    
	    public void setCssIdentity(String cssIdentity) {
            this.cssIdentity = cssIdentity;
	    }
	    public String getCssIdentity() {
            return cssIdentity;
	    }
		/**
		 * @return the cssAdRequests1
		 */
		public CssRequestModel getCssAdRequests1() {
			return cssAdRequests1;
		}
		/**
		 * @param cssAdRequests1 the cssAdRequests1 to set
		 */
		public void setCssAdRequests1(CssRequestModel cssAdRequests1) {
			this.cssAdRequests1 = cssAdRequests1;
		}
		/**
		 * @return the cssAdRequests2
		 */
		public CssRequestModel getCssAdRequests2() {
			return cssAdRequests2;
		}
		/**
		 * @param cssAdRequests2 the cssAdRequests2 to set
		 */
		public void setCssAdRequests2(CssRequestModel cssAdRequests2) {
			this.cssAdRequests2 = cssAdRequests2;
		}
		/**
		 * @return the cssAdRequests3
		 */
		public CssRequestModel getCssAdRequests3() {
			return cssAdRequests3;
		}
		/**
		 * @param cssAdRequests3 the cssAdRequests3 to set
		 */
		public void setCssAdRequests3(CssRequestModel cssAdRequests3) {
			this.cssAdRequests3 = cssAdRequests3;
		}
		/**
		 * @return the cssAdRequests4
		 */
		public CssRequestModel getCssAdRequests4() {
			return cssAdRequests4;
		}
		/**
		 * @param cssAdRequests4 the cssAdRequests4 to set
		 */
		public void setCssAdRequests4(CssRequestModel cssAdRequests4) {
			this.cssAdRequests4 = cssAdRequests4;
		}
		/**
		 * @return the cssAdRequests5
		 */
		public CssRequestModel getCssAdRequests5() {
			return cssAdRequests5;
		}
		/**
		 * @param cssAdRequests5 the cssAdRequests5 to set
		 */
		public void setCssAdRequests5(CssRequestModel cssAdRequests5) {
			this.cssAdRequests5 = cssAdRequests5;
		}
		/**
		 * @return the cssRequests1
		 */
		public CssRequestModel getCssRequests1() {
			return cssRequests1;
		}
		/**
		 * @param cssRequests1 the cssRequests1 to set
		 */
		public void setCssRequests1(CssRequestModel cssRequests1) {
			this.cssRequests1 = cssRequests1;
		}
		/**
		 * @return the cssRequests2
		 */
		public CssRequestModel getCssRequests2() {
			return cssRequests2;
		}
		/**
		 * @param cssRequests2 the cssRequests2 to set
		 */
		public void setCssRequests2(CssRequestModel cssRequests2) {
			this.cssRequests2 = cssRequests2;
		}
		/**
		 * @return the cssRequests3
		 */
		public CssRequestModel getCssRequests3() {
			return cssRequests3;
		}
		/**
		 * @param cssRequests3 the cssRequests3 to set
		 */
		public void setCssRequests3(CssRequestModel cssRequests3) {
			this.cssRequests3 = cssRequests3;
		}
		/**
		 * @return the cssRequests4
		 */
		public CssRequestModel getCssRequests4() {
			return cssRequests4;
		}
		/**
		 * @param cssRequests4 the cssRequests4 to set
		 */
		public void setCssRequests4(CssRequestModel cssRequests4) {
			this.cssRequests4 = cssRequests4;
		}
		/**
		 * @return the cssRequests5
		 */
		public CssRequestModel getCssRequests5() {
			return cssRequests5;
		}
		/**
		 * @param cssRequests5 the cssRequests5 to set
		 */
		public void setCssRequests5(CssRequestModel cssRequests5) {
			this.cssRequests5 = cssRequests5;
		}
		/**
		 * @return the cssFriendRequests1
		 */
		public CssRequestModel getCssFriendRequests1() {
			return cssFriendRequests1;
		}
		/**
		 * @param cssFriendRequests1 the cssFriendRequests1 to set
		 */
		public void setCssFriendRequests1(CssRequestModel cssFriendRequests1) {
			this.cssFriendRequests1 = cssFriendRequests1;
		}
		/**
		 * @return the cssFriendRequests2
		 */
		public CssRequestModel getCssFriendRequests2() {
			return cssFriendRequests2;
		}
		/**
		 * @param cssFriendRequests2 the cssFriendRequests2 to set
		 */
		public void setCssFriendRequests2(CssRequestModel cssFriendRequests2) {
			this.cssFriendRequests2 = cssFriendRequests2;
		}
		/**
		 * @return the cssFriendRequests3
		 */
		public CssRequestModel getCssFriendRequests3() {
			return cssFriendRequests3;
		}
		/**
		 * @param cssFriendRequests3 the cssFriendRequests3 to set
		 */
		public void setCssFriendRequests3(CssRequestModel cssFriendRequests3) {
			this.cssFriendRequests3 = cssFriendRequests3;
		}
		/**
		 * @return the cssFriendRequests4
		 */
		public CssRequestModel getCssFriendRequests4() {
			return cssFriendRequests4;
		}
		/**
		 * @param cssFriendRequests4 the cssFriendRequests4 to set
		 */
		public void setCssFriendRequests4(CssRequestModel cssFriendRequests4) {
			this.cssFriendRequests4 = cssFriendRequests4;
		}
		/**
		 * @return the cssFriendRequests5
		 */
		public CssRequestModel getCssFriendRequests5() {
			return cssFriendRequests5;
		}
		/**
		 * @param cssFriendRequests5 the cssFriendRequests5 to set
		 */
		public void setCssFriendRequests5(CssRequestModel cssFriendRequests5) {
			this.cssFriendRequests5 = cssFriendRequests5;
		}
	

	    
	    
}
