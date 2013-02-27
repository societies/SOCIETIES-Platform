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
import org.societies.webapp.models.requests.CssServiceModel;

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
	
	
//	CssRequestModel[] cssFriendRequests = new CssRequestModel[5];
	
	//CssServiceModel[] cssFriendService = new CssServiceModel[15];
	
	
	CssServiceModel cssFriendService11 = new CssServiceModel();
	CssServiceModel cssFriendService12 = new CssServiceModel();
	CssServiceModel cssFriendService13 = new CssServiceModel();
	CssServiceModel cssFriendService14 = new CssServiceModel();
	CssServiceModel cssFriendService15 = new CssServiceModel();

	CssServiceModel cssFriendService21 = new CssServiceModel();
	CssServiceModel cssFriendService22 = new CssServiceModel();
	CssServiceModel cssFriendService23 = new CssServiceModel();
	CssServiceModel cssFriendService24 = new CssServiceModel();
	CssServiceModel cssFriendService25 = new CssServiceModel();
	CssServiceModel cssFriendService31 = new CssServiceModel();
	CssServiceModel cssFriendService32 = new CssServiceModel();
	CssServiceModel cssFriendService33 = new CssServiceModel();
	CssServiceModel cssFriendService34 = new CssServiceModel();
	CssServiceModel cssFriendService35 = new CssServiceModel();
	
	CssServiceModel cssService1 = new CssServiceModel();
	CssServiceModel cssService2 = new CssServiceModel();
	CssServiceModel cssService3 = new CssServiceModel();
	CssServiceModel cssService4 = new CssServiceModel();
	CssServiceModel cssService5 = new CssServiceModel();
	
	
	
	int remoteServiceCount;
	


			
		   
/** Css Record Details Start **/
		@Size(min = 1, max = 20)
	    private String password;
	    @Size(min = 1, max = 50)
	    private String cssIdentity;
	   

	    private String cssHostingLocation;
	    private String domainServer;
	    private String emailID;
	    private String homeLocation;
	    private String identityName;
	    private String imID;
	    private String name;
	    private int sex;
	    private String socialURI;
	    private String workplace;
	    private String position;
	    private int entity;
	    /** Css Record Details end **/
		
	    
	 // CssAdvertisementRecordDetails
		private String cssAdName;
		private String cssAdId;
		private String cssAdUri;

		
	    String buttonLabel;
	    
	    

	
	    
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
		 * @return the remoteServiceCount
		 */
		public int getRemoteServiceCount() {
			return remoteServiceCount;
		}
		/**
		 * @param remoteServiceCount the remoteServiceCount to set
		 */
		public void setRemoteServiceCount(int remoteServiceCount) {
			this.remoteServiceCount = remoteServiceCount;
		}
		/**
		 * @return the cssFriendService11
		 */
		public CssServiceModel getCssFriendService11() {
			return cssFriendService11;
		}
		/**
		 * @param cssFriendService11 the cssFriendService11 to set
		 */
		public void setCssFriendService11(CssServiceModel cssFriendService11) {
			this.cssFriendService11 = cssFriendService11;
		}
		/**
		 * @return the cssFriendService12
		 */
		public CssServiceModel getCssFriendService12() {
			return cssFriendService12;
		}
		/**
		 * @param cssFriendService12 the cssFriendService12 to set
		 */
		public void setCssFriendService12(CssServiceModel cssFriendService12) {
			this.cssFriendService12 = cssFriendService12;
		}
		/**
		 * @return the cssFriendService13
		 */
		public CssServiceModel getCssFriendService13() {
			return cssFriendService13;
		}
		/**
		 * @param cssFriendService13 the cssFriendService13 to set
		 */
		public void setCssFriendService13(CssServiceModel cssFriendService13) {
			this.cssFriendService13 = cssFriendService13;
		}
		/**
		 * @return the cssFriendService14
		 */
		public CssServiceModel getCssFriendService14() {
			return cssFriendService14;
		}
		/**
		 * @param cssFriendService14 the cssFriendService14 to set
		 */
		public void setCssFriendService14(CssServiceModel cssFriendService14) {
			this.cssFriendService14 = cssFriendService14;
		}
		/**
		 * @return the cssFriendService15
		 */
		public CssServiceModel getCssFriendService15() {
			return cssFriendService15;
		}
		/**
		 * @param cssFriendService15 the cssFriendService15 to set
		 */
		public void setCssFriendService15(CssServiceModel cssFriendService15) {
			this.cssFriendService15 = cssFriendService15;
		}
		/**
		 * @return the cssFriendService21
		 */
		public CssServiceModel getCssFriendService21() {
			return cssFriendService21;
		}
		/**
		 * @param cssFriendService21 the cssFriendService21 to set
		 */
		public void setCssFriendService21(CssServiceModel cssFriendService21) {
			this.cssFriendService21 = cssFriendService21;
		}
		/**
		 * @return the cssFriendService22
		 */
		public CssServiceModel getCssFriendService22() {
			return cssFriendService22;
		}
		/**
		 * @param cssFriendService22 the cssFriendService22 to set
		 */
		public void setCssFriendService22(CssServiceModel cssFriendService22) {
			this.cssFriendService22 = cssFriendService22;
		}
		/**
		 * @return the cssFriendService23
		 */
		public CssServiceModel getCssFriendService23() {
			return cssFriendService23;
		}
		/**
		 * @param cssFriendService23 the cssFriendService23 to set
		 */
		public void setCssFriendService23(CssServiceModel cssFriendService23) {
			this.cssFriendService23 = cssFriendService23;
		}
		/**
		 * @return the cssFriendService24
		 */
		public CssServiceModel getCssFriendService24() {
			return cssFriendService24;
		}
		/**
		 * @param cssFriendService24 the cssFriendService24 to set
		 */
		public void setCssFriendService24(CssServiceModel cssFriendService24) {
			this.cssFriendService24 = cssFriendService24;
		}
		/**
		 * @return the cssFriendService25
		 */
		public CssServiceModel getCssFriendService25() {
			return cssFriendService25;
		}
		/**
		 * @param cssFriendService25 the cssFriendService25 to set
		 */
		public void setCssFriendService25(CssServiceModel cssFriendService25) {
			this.cssFriendService25 = cssFriendService25;
		}
		/**
		 * @return the cssFriendService31
		 */
		public CssServiceModel getCssFriendService31() {
			return cssFriendService31;
		}
		/**
		 * @param cssFriendService31 the cssFriendService31 to set
		 */
		public void setCssFriendService31(CssServiceModel cssFriendService31) {
			this.cssFriendService31 = cssFriendService31;
		}
		/**
		 * @return the cssFriendService32
		 */
		public CssServiceModel getCssFriendService32() {
			return cssFriendService32;
		}
		/**
		 * @param cssFriendService32 the cssFriendService32 to set
		 */
		public void setCssFriendService32(CssServiceModel cssFriendService32) {
			this.cssFriendService32 = cssFriendService32;
		}
		/**
		 * @return the cssFriendService33
		 */
		public CssServiceModel getCssFriendService33() {
			return cssFriendService33;
		}
		/**
		 * @param cssFriendService33 the cssFriendService33 to set
		 */
		public void setCssFriendService33(CssServiceModel cssFriendService33) {
			this.cssFriendService33 = cssFriendService33;
		}
		/**
		 * @return the cssFriendService34
		 */
		public CssServiceModel getCssFriendService34() {
			return cssFriendService34;
		}
		/**
		 * @param cssFriendService34 the cssFriendService34 to set
		 */
		public void setCssFriendService34(CssServiceModel cssFriendService34) {
			this.cssFriendService34 = cssFriendService34;
		}
		/**
		 * @return the cssFriendService35
		 */
		public CssServiceModel getCssFriendService35() {
			return cssFriendService35;
		}
		/**
		 * @param cssFriendService35 the cssFriendService35 to set
		 */
		public void setCssFriendService35(CssServiceModel cssFriendService35) {
			this.cssFriendService35 = cssFriendService35;
		}
		/**
		 * @return the cssHostingLocation
		 */
		public String getCssHostingLocation() {
			return cssHostingLocation;
		}
		/**
		 * @param setCssHostingLocation the cssHostingLocation to set
		 */
		public void setCssHostingLocation(String cssHostingLocation) {
			this.cssHostingLocation = cssHostingLocation;
		}
		/**
		 * @return the domainServer
		 */
		public String getDomainServer() {
			return domainServer;
		}
		/**
		 * @param domainServer the domainServer to set
		 */
		public void setDomainServer(String domainServer) {
			this.domainServer = domainServer;
		}
		/**
		 * @return the emailID
		 */
		public String getEmailID() {
			return emailID;
		}
		/**
		 * @param emailID the emailID to set
		 */
		public void setEmailID(String emailID) {
			this.emailID = emailID;
		}
		/**
		 * @return the homeLocation
		 */
		public String getHomeLocation() {
			return homeLocation;
		}
		/**
		 * @param homeLocation the homeLocation to set
		 */
		public void setHomeLocation(String homeLocation) {
			this.homeLocation = homeLocation;
		}
		/**
		 * @return the identityName
		 */
		public String getIdentityName() {
			return identityName;
		}
		/**
		 * @param identityName the identityName to set
		 */
		public void setIdentityName(String identityName) {
			this.identityName = identityName;
		}
		/**
		 * @return the imID
		 */
		public String getImID() {
			return imID;
		}
		/**
		 * @param imID the imID to set
		 */
		public void setImID(String imID) {
			this.imID = imID;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
				
		/**
		 * @return the sex
		 */
		public int getSex() {
			return sex;
		}
		/**
		 * @param sex the sex to set
		 */
		public void setSex(int sex) {
			this.sex = sex;
		}
		
		/**
		 * @return the entity
		 */
		public int getEntity() {
			return entity;
		}
		/**
		 * @param entity the entity to entity
		 */
		public void setEntity(int entity) {
			this.entity = entity;
		}
		
		/**
		 * @return the socialURI
		 */
		public String getSocialURI() {
			return socialURI;
		}
		/**
		 * @param socialURI the socialURI to set
		 */
		public void setSocialURI(String socialURI) {
			this.socialURI = socialURI;
		}
		/**
		 * @return the buttonLabel
		 */
		public String getButtonLabel() {
			return buttonLabel;
		}
		/**
		 * @param buttonLael the buttonLabel to set
		 */
		public void setButtonLabel(String buttonLabel) {
			this.buttonLabel = buttonLabel;
		}
		/**
		 * @return the cssService1
		 */
		public CssServiceModel getCssService1() {
			return cssService1;
		}
		/**
		 * @param cssService1 the cssService1 to set
		 */
		public void setCssService1(CssServiceModel cssService1) {
			this.cssService1 = cssService1;
		}
		/**
		 * @return the cssService2
		 */
		public CssServiceModel getCssService2() {
			return cssService2;
		}
		/**
		 * @param cssService2 the cssService2 to set
		 */
		public void setCssService2(CssServiceModel cssService2) {
			this.cssService2 = cssService2;
		}
		/**
		 * @return the cssService3
		 */
		public CssServiceModel getCssService3() {
			return cssService3;
		}
		/**
		 * @param cssService3 the cssService3 to set
		 */
		public void setCssService3(CssServiceModel cssService3) {
			this.cssService3 = cssService3;
		}
		/**
		 * @return the cssService4
		 */
		public CssServiceModel getCssService4() {
			return cssService4;
		}
		/**
		 * @param cssService4 the cssService4 to set
		 */
		public void setCssService4(CssServiceModel cssService4) {
			this.cssService4 = cssService4;
		}
		/**
		 * @return the cssService5
		 */
		public CssServiceModel getCssService5() {
			return cssService5;
		}
		/**
		 * @param cssService5 the cssService5 to set
		 */
		public void setCssService5(CssServiceModel cssService5) {
			this.cssService5 = cssService5;
		}
		/**
		 * @return the cssAdName
		 */
		public String getCssAdName() {
			return cssAdName;
		}
		/**
		 * @param cssAdName the cssAdName to set
		 */
		public void setCssAdName(String cssAdName) {
			this.cssAdName = cssAdName;
		}
		/**
		 * @return the cssAdId
		 */
		public String getCssAdId() {
			return cssAdId;
		}
		/**
		 * @param cssAdId the cssAdId to set
		 */
		public void setCssAdId(String cssAdId) {
			this.cssAdId = cssAdId;
		}
		/**
		 * @return the cssAdUri
		 */
		public String getCssAdUri() {
			return cssAdUri;
		}
		/**
		 * @param cssAdUri the cssAdUri to set
		 */
		public void setCssAdUri(String cssAdUri) {
			this.cssAdUri = cssAdUri;
		}

		/**
		 * @return the workplace
		 */
		public String getWorkplace() {
			return workplace;
		}
		/**
		 * @param workplace the workplace to set
		 */
		public void setWorkplace(String workplace) {
			this.workplace = workplace;
		}

		/**
		 * @return the position
		 */
		public String getPosition() {
			return position;
		}
		/**
		 * @param position the position to set
		 */
		public void setPosition(String position) {
			this.position = position;
		}

	    
	    
}
