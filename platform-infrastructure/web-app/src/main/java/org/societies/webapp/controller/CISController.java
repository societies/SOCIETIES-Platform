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

package org.societies.webapp.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.webapp.models.CisInfo;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.activity.client.ActivityFeedClient;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.cis.attributes.Rule;
import org.societies.api.cis.attributes.Rule.OperationType;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.directory.ICisDirectoryCallback;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.privacytrust.privacyprotection.model.listener.IPrivacyPolicyManagerListener;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.remote.IPrivacyPolicyManagerRemote;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.JoinResponse;
import org.societies.api.schema.cis.community.LeaveResponse;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssManagerResultActivities;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;
import org.societies.cis.mgmtClient.CisManagerClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import org.societies.webapp.service.UserService;


@Controller
@ManagedBean(name = "cismanager")
@RequestScoped
public class CISController extends BasePageController{
	
	
	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	
	@ManagedProperty(value = "#{cssLocalManager}")
	private ICSSInternalManager cssLocalManager;
		
	
	@ManagedProperty(value = "#{cisDirectoryRemote}")
	private ICisDirectoryRemote  cisDirectory;
	
	@ManagedProperty(value = "#{cisManager}")
	
	@Autowired
	private ICisManager cisManager;
	//@Autowired
	private ICommManager commMngrRef;
	
	private IActivityFeed actFeed;
		
	@Autowired
	private ICisDirectoryRemote cisDirectoryRemote;
		
	@Autowired
	private IPrivacyPolicyManager privacyPolicyManager;
	@Autowired
	private IPrivacyPolicyManagerRemote privacyPolicyManagerRemote;
	
	private String cisname;
	public String getCisname() {
		return cisname;
	}
	public void setCisname(String cisname) {
		this.cisname = cisname;
	}
	public String getCistype() {
		return cistype;
	}
	public void setCistype(String cistype) {
		this.cistype = cistype;
	}
	public String getCisdesc() {
		return cisdesc;
	}
	public void setCisdesc(String cisdesc) {
		this.cisdesc = cisdesc;
	}

	private String cistype;
	private String cisdesc;
	
	// AUTOWIRING GETTERS AND SETTERS
		public ICisManager getCisManager() {
			return cisManager;
		}
		public void setCisManager(ICisManager cisManager) {
			this.cisManager = cisManager;
		}
		
		public IActivityFeed getactFeed() {
			return actFeed;
		}
		public void setCis(IActivityFeed actFeed) {
			this.actFeed = actFeed;
		}
		
		public ICommManager getCommMngrRef() {
			return this.commMngrRef;
		}
		
		
		public void setCommMngrRef(ICommManager commMngrRef) {
			this.commMngrRef = commMngrRef;
		}


		public IPrivacyPolicyManager getPrivacyPolicyManager() {
			return privacyPolicyManager;
		}


		public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
			this.privacyPolicyManager = privacyPolicyManager;
		}


		public IPrivacyPolicyManagerRemote getPrivacyPolicyManagerRemote() {
			return privacyPolicyManagerRemote;
		}


		public void setPrivacyPolicyManagerRemote(
				IPrivacyPolicyManagerRemote privacyPolicyManagerRemote) {
			this.privacyPolicyManagerRemote = privacyPolicyManagerRemote;
		}
		
		
		
		public ICisDirectoryRemote getcisDirectory() {
			return this.cisDirectory;
		}


		public void setcisDirectory(ICisDirectoryRemote cisDirectory) {
			this.cisDirectory = cisDirectory;
		}


		public UserService getUserService() {
			return userService;
		}
		public void setUserService(UserService userService) {
			this.userService = userService;
		}
		
		public ICSSInternalManager getCssLocalManager() {
			return cssLocalManager;
		}
		
		public void setCssLocalManager(ICSSInternalManager cssLocalManager) {
			this.cssLocalManager = cssLocalManager;
		}
	
	
	
	public CISController() {
	    log.info("CISController constructor called");
	}
	
	
	
	public void createcommunity(){
		log.info("CISController create community called");
		Hashtable<String, MembershipCriteria> cisCriteria = new Hashtable<String, MembershipCriteria>();
		
		log.info("create community cisname: " +cisname);
		log.info("create community cistype: " +cistype);
		log.info("create community cisdesc: " +cisdesc);
		log.info("create community cisCriteria: " +cisCriteria);
		
		
		cisManager.createCis(cisname, cistype, cisCriteria, cisdesc);
		
	}
	
	public List<CisInfo> getsuggestedcommunities(){
		log.info("CISController get suggested communities called");
		
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		
		ICisDirectoryRemote remote = getcisDirectory();
		
		log.info("ICisDirectoryRemote : " +remote);
		
		List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
		List<ICis> cisList = new ArrayList<ICis>();
		cisList = this.cisManager.getCisList();
		log.info("commList contains " +cisList);
		
		getcisDirectory().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		
		for ( int i = 0; i < cisList.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				if (adverts.get(j).getId().contains(cisList.get(i).getCisId()))
						bFound = true;
				else
					j++;
			} while ((bFound == false) && (j < adverts.size()));
			
			if (!bFound)
			{
				CisInfo cisInfo = new CisInfo();
				cisInfo.setCisid(cisList.get(i).getCisId());
				cisInfo.setCisname(adverts.get(j).getName());
				cisinfoList.add(cisInfo);
			}
			
		}
		
		
		
		//return adverts;
		return cisinfoList;
		
		
	}
	
	
	public List<MarshaledActivity> getactivities(){
		log.debug("getActivities called");
		Date date = new Date();
		long longDate=date.getTime();
		String timespan = "1262304000000 " + longDate;
		List<MarshaledActivity> listSchemaActivities = new ArrayList<MarshaledActivity>();  
		List<MarshaledActivity> Result = new ArrayList<MarshaledActivity>();
						
		Future<List<MarshaledActivity>> asyncActivitiesResult = this.cssLocalManager.getActivities(timespan, 20);
		//Future<List<MarshaledActivity>> asyncActivitiesResult = this.cisManager.getActivities(timespan, 20);
		CssManagerResultActivities results = new CssManagerResultActivities();
		try {
			results.setMarshaledActivity(asyncActivitiesResult.get());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		
		log.debug("Activities : " +results);
		log.debug("Activities Size: " +results.getMarshaledActivity().size());
		
		for(MarshaledActivity result : results.getMarshaledActivity()){
			log.debug("MarshaledActivity Published " +result.getPublished());
			log.debug("MarshaledActivity Verb " +result.getVerb());
			log.debug("MarshaledActivity Actor" +result.getActor());
			Result.add(result);
			
		}
		
		return Result;
	}
	
	public List<ICisOwned> getownedcommunities(){
		
		log.debug("getownedcommunities method called");
		
		Future<List<CssAdvertisementRecord>> asynchFriends = getCssLocalManager().getCssFriends();
		List<CssAdvertisementRecord> friends = new ArrayList<CssAdvertisementRecord>();
		List<ICisOwned> ownedCISs = cisManager.getListOfOwnedCis();
			
		log.info("ownedCISs SIZE is now " +ownedCISs.size());
			
		return ownedCISs;
		
		
	}
	
	public List<CisInfo> getcommunities(){
		
		log.info("getcommunities method called");
		List<ICis> cisList = getCisManager().getCisList();
		
		
		log.info("cisList size :" +cisList.size());
		
		//Future<List<CssAdvertisementRecord>> asynchFriends = getCssLocalManager().getCssFriends();
		List<CisInfo> cisinfoList = new ArrayList<CisInfo>();
		List<ICis> comList = new ArrayList<ICis>();
		//comList = cisManager.getCisList();
		
		CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
		getcisDirectory().findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> adverts = callback.getResultList();
		
		
		for ( int i = 0; i < cisList.size(); i++)
		{
			//find ad for this id
			boolean bFound = false;
			int j = 0;
			do
			{
				if (adverts.get(j).getId().contains(cisList.get(i).getCisId()))
						bFound = true;
				else
					j++;
			} while ((bFound == false) && (j < adverts.size()));
			
			if (bFound)
			{
				CisInfo cisInfo = new CisInfo();
				cisInfo.setCisid(cisList.get(i).getCisId());
				cisInfo.setCisname(adverts.get(j).getName());
				cisinfoList.add(cisInfo);
			}
			
		}
		
			
		return cisinfoList;
		
		
	}
	
	public void joincis(CisAdvertisementRecord adv){
		
		log.info("JoinCIS method called");
		log.info("JoinCIS method called CIS ID " +adv.getId());
		CisManagerClient joinCallback = new CisManagerClient();
		log.info("CisManagerClient joinCallback is " +joinCallback);
		log.info("cisManager instance is " +cisManager);
		cisManager.joinRemoteCIS(adv, joinCallback);
		log.info("and we're back :( ");
		
	}
	
	public void leavecis(CisAdvertisementRecord adv){
		
		log.info("leaveCIS method called");
		log.info("leaveCIS method called CIS ID " +adv.getId());
		CisManagerClient leaveCallback = new CisManagerClient();
		log.info("CisManagerClient joinCallback is " +leaveCallback);
		String CISID = adv.getId();
		
		cisManager.leaveRemoteCIS(CISID, leaveCallback);
		
		
	}
	
	
	

}
