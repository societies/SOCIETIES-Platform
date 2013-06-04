package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.FriendFilter;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Controller;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssManagerResultActivities;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Controller
@ManagedBean(name = "suggestedfriends")
@RequestScoped
public class SuggestedFriendsController extends BasePageController{
	
	@ManagedProperty(value = "#{userService}")
	private UserService userService;
	
	@Autowired
	//@ManagedProperty(value = "#{snsSuggestedFriends}")
	
	@ManagedProperty(value = "#{cssLocalManager}")
	private ICSSInternalManager cssLocalManager;
	
	private HashMap<CssAdvertisementRecord,Integer> snsSuggestedFriends;
	
	private List<CssAdvertisementRecord> snsFriends;
	
	private Future<List<CssAdvertisementRecordDetailed>> asyncssdetails;
	private List<CssAdvertisementRecordDetailed> allcssdetails;
	private List<CssAdvertisementRecordDetailed> snsFriendes;
	private List<CssAdvertisementRecord> otherFriends;
	private List<CssAdvertisementRecordDetailed> otherFriendes;
	
	private Future<List<CssRequest>> asynchFR;
	
	private List<MarshaledActivity> activities;
	private List<CssAdvertisementRecord> friends;
	private String friendid;
	
	private int selectedValue;




	/**
	 * OSGI service get auto injected
	 */
	//@Autowired
	//private ICSSInternalManager cssLocalManager;
	@Autowired
	private ICommManager commMngrRef;
	private String name;
	private String Id;
	
	private FriendFilter friendfilter;
	
	
	public FriendFilter getfriendfilter(){
		return friendfilter ;
	}
	
	public void setfriendfilter(FriendFilter filter){
		log.info("set filter called with filter as : " +filter.getFilterFlag());
		
		this.friendfilter=friendfilter;
	}
	
	public ICSSInternalManager getCssLocalManager() {
		return cssLocalManager;
	}
	
	public void setCssLocalManager(ICSSInternalManager cssLocalManager) {
		this.cssLocalManager = cssLocalManager;
	}
	
	private ICisDirectoryRemote cisDirectoryRemote;
	
	public ICisDirectoryRemote getCisDirectoryRemote() {
		return cisDirectoryRemote;
	}
	
	public void setCisDirectoryRemote(ICisDirectoryRemote cisDirectoryRemote) {
		this.cisDirectoryRemote = cisDirectoryRemote;
	}
	
	public ICommManager getCommManager() {
		return commMngrRef;
	}
	public void setCommManager(ICommManager commManager) {
		this.commMngrRef = commMngrRef;
	}

	@SuppressWarnings("UnusedDeclaration")
	public UserService getUserService() {
	    return userService;
	}
	
	@SuppressWarnings("UnusedDeclaration")
	public void setUserService(UserService userService) {
	    log.trace("setUserService() has been called with " + userService);
	    this.userService = userService;
	}
	
	public SuggestedFriendsController() {
	    log.info("SuggestedFriendsController constructor called");
	    log.info("SuggestedFriendsController constructor about to call getSuggestedFriends");
	   // this.getSuggestedfriends();
	}

	public List<CssAdvertisementRecordDetailed> getsnsFriendes(){
		log.info("getsnsFriends method called [][]][][][][][][] ");
		snsSuggestedFriends = this.getSuggestedfriends();
		log.info("[][]][][][][][][] And we're BACK :-) ");
		List<CssAdvertisementRecord> snsFriends = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecord> otherFriends = new ArrayList<CssAdvertisementRecord>();
		snsFriendes = new ArrayList<CssAdvertisementRecordDetailed>();
		for(Entry<CssAdvertisementRecord, Integer> entry : snsSuggestedFriends.entrySet()){
			log.info("snsFriends ID " +entry.getKey().getId());
			log.info("snsFriends Name " +entry.getKey().getName());
			log.info("snsFriends Hashmap value " +entry.getValue());
			if(entry.getValue().equals(0)){
				otherFriends.add(entry.getKey());
				log.info("otherFriends SIZE is " +snsFriends.size());
			}else {
				snsFriends.add(entry.getKey());
				log.info("snsFriends SIZE is " +snsFriends.size());
			}
			
			
		}
		this.setOtherFriends(otherFriends);
		asyncssdetails = this.cssLocalManager.getCssAdvertisementRecordsFull();
		try {
			allcssdetails = asyncssdetails.get();
			log.info("allcssdetails SIZE is " +allcssdetails.size());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Future<List<CssRequest>> asynchFR = getCssLocalManager().findAllCssRequests();
		try {
			List<CssRequest> friendReq = asynchFR.get();
			for(int index = 0; index < allcssdetails.size(); index++) {
			if (allcssdetails.get(index).getStatus() != CssRequestStatusType.ACCEPTED) 
				{
		
					for ( int indexFR = 0; indexFR < friendReq.size(); indexFR++)
					{
						if (allcssdetails.get(index).getResultCssAdvertisementRecord().getId().contains(friendReq.get(indexFR).getCssIdentity()) && (allcssdetails.get(index).getStatus() != CssRequestStatusType.DENIED))
						{
							// We have a pending FR from this people, change status. This should be done in the CssManager 
							// but not for the pilot
							allcssdetails.get(index).setStatus(CssRequestStatusType.NEEDSRESP);
							indexFR = friendReq.size();
		
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < allcssdetails.size(); i++){
			for(CssAdvertisementRecord entry : snsFriends){
				log.info("entry id is " +entry.getId());
				log.info("allcssdetails ID is " +allcssdetails.get(i).getResultCssAdvertisementRecord().getId());
				if(entry.getId().contains(allcssdetails.get(i).getResultCssAdvertisementRecord().getId())){
					
					log.info("ADDING record to list " +allcssdetails.get(i));
					log.info("snsFriendes size is " +snsFriendes.size());
					snsFriendes.add(allcssdetails.get(i));
				}
			}
		}
		return snsFriendes ;
	}

	public String getName() {
	    return name;
	 }
	
	 public void setName(String name) {
	    this.name = name;
	 }
	
	 public String getId() {
	    return Id;
	 }
	 
		 
	public HashMap<CssAdvertisementRecord, Integer> getSuggestedfriends() {
		 
		log.info("getSuggestedFriends method called {}{}{}{}{}{}{}{}{} == ");
		 
		FriendFilter filter = this.getfriendfilter();
		HashMap<CssAdvertisementRecord,Integer> snsSuggestedFriends = null;
		 
		try {
	
			Integer filterFlag = 0x00000001111;
			filter = cssLocalManager.getFriendfilter();
			if(filter==null){
				filter = new FriendFilter();
				filterFlag=0x00000011111;
				filter.setFilterFlag(filterFlag);
			}else{
				filterFlag = filter.getFilterFlag();
				filter.setFilterFlag(filterFlag );
			}
			
			log.info("About to call the suggestedFriendDetails with filterflag: " +filter);
			Future<HashMap<CssAdvertisementRecord, Integer>> asynchSnsSuggestedFriends = getCssLocalManager().getSuggestedFriendsDetails(filter); //suggestedFriends();
			snsSuggestedFriends = asynchSnsSuggestedFriends.get();
	
			log.info("Back from call the suggestedFriendDetails with result: " +snsSuggestedFriends);
			log.info("snsSuggestedFriends contains" +snsSuggestedFriends);
			for(Entry<CssAdvertisementRecord, Integer> entry : snsSuggestedFriends.entrySet()){
				log.info("snsSuggestedFriends ID " +entry.getKey().getId());
				log.info("snsSuggestedFriends Name " +entry.getKey().getName());
				log.info("snsSuggestedFriends Hashmap value " +entry.getValue());
				
			}
			
	
		} catch (Exception e) {
			
		}
		;
		return snsSuggestedFriends;
		}
	
	public List<CssAdvertisementRecord> getfriends(){
			
		log.info("@@@@@@@@@@@ getfriendslist method called @@@@@@@@@@@@@@ ");
		
		Future<List<CssAdvertisementRecord>> asynchFriends = getCssLocalManager().getCssFriends();
		List<CssAdvertisementRecord> friends = new ArrayList<CssAdvertisementRecord>();
		
		try {
			friends = asynchFriends.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		log.info("Friends SIZE is now " +friends.size());
			
		return friends;
		
		
	}
		
	public void sendfriendrequest(String friendid){
			
		log.info("@@@@@@@@@@@ sendfriendrequest method called @@@@@@@@@@@@@@ ");
		
		this.cssLocalManager.sendCssFriendRequest(friendid);
			
	}
	
	public void handlerequestaccept(String friendid){
		
		log.info("@@@@@@@@@@@ ACCEPT method called @@@@@@@@@@@@@@ ");
		log.info("@@@@@@@@@@@ ACCEPT method called friendid " +friendid);
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity(friendid); 
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		getCssLocalManager().acceptCssFriendRequest(pendingFR);
	
	}

	public void handlerequestdecline(String friendid){
		
		log.info("@@@@@@@@@@@ DECLINE method called @@@@@@@@@@@@@@ ");
		log.info("@@@@@@@@@@@ DECLINE method called friendid " +friendid);
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity(friendid);
		pendingFR.setRequestStatus(CssRequestStatusType.DENIED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		//getCssLocalManager().updateCssRequest(pendingFR);
		getCssLocalManager().declineCssFriendRequest(pendingFR);
		
	}

	public void handlerequestcancelled(String friendid){
		
		log.info("@@@@@@@@@@@ CANCELLED method called @@@@@@@@@@@@@@ ");
		log.info("@@@@@@@@@@@ CANCELLED method called friendid " +friendid);
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity(friendid); 
		pendingFR.setRequestStatus(CssRequestStatusType.CANCELLED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		getCssLocalManager().updateCssFriendRequest(pendingFR);
	
	}

	public String getFriendid() {
		return friendid;
	}

	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}
	
	public List<MarshaledActivity> getactivities(){
		log.info("getActivities called");
		Date date = new Date();
		long longDate=date.getTime();
		String timespan = "1262304000000 " + longDate;
		List<MarshaledActivity> listSchemaActivities = new ArrayList<MarshaledActivity>();  
		List<MarshaledActivity> Result = new ArrayList<MarshaledActivity>();
						
		Future<List<MarshaledActivity>> asyncActivitiesResult = this.cssLocalManager.getActivities(timespan, 20);
		CssManagerResultActivities results = new CssManagerResultActivities();
		try {
			results.setMarshaledActivity(asyncActivitiesResult.get());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
		
		log.info("Activities : " +results);
		log.info("Activities Size: " +results.getMarshaledActivity().size());
		
		for(MarshaledActivity result : results.getMarshaledActivity()){
			log.info("MarshaledActivity Published " +result.getPublished());
			log.info("MarshaledActivity Verb " +result.getVerb());
			log.info("MarshaledActivity Actor" +result.getActor());
			Result.add(result);
			
		}
		
		return Result;
	}
	
	public List<CssAdvertisementRecordDetailed> getOtherFriendes() {
		
		List<CssAdvertisementRecord> otherFriends = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecordDetailed> otherFriendes = new ArrayList<CssAdvertisementRecordDetailed>();
		
		log.info("getOtherFriends is called");
		log.info("allcssdetails SIZE is " +allcssdetails.size());
		otherFriends = this.getOtherFlist();
		log.info("otherFriends SIZE is " +otherFriends.size());
		
		asynchFR = getCssLocalManager().findAllCssRequests();
		try {
			List<CssRequest> friendReq = asynchFR.get();
			for(int index = 0; index < allcssdetails.size(); index++) {
			if (allcssdetails.get(index).getStatus() != CssRequestStatusType.ACCEPTED) 
				{
		
					for ( int indexFR = 0; indexFR < friendReq.size(); indexFR++)
					{
						if (allcssdetails.get(index).getResultCssAdvertisementRecord().getId().contains(friendReq.get(indexFR).getCssIdentity()) && (allcssdetails.get(index).getStatus() != CssRequestStatusType.DENIED))
						{
							// We have a pending FR from this people, change status. This should be done in the CssManager 
							// but not for the pilot
							allcssdetails.get(index).setStatus(CssRequestStatusType.NEEDSRESP);
							indexFR = friendReq.size();
		
						}
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < allcssdetails.size(); i++){
			for(CssAdvertisementRecord entry : otherFriends){
				log.info("entry id is " +entry.getId());
				log.info("allcssdetails ID is " +allcssdetails.get(i).getResultCssAdvertisementRecord().getId());
				if(entry.getId().contains(allcssdetails.get(i).getResultCssAdvertisementRecord().getId())){
					
					log.info("ADDING record to list " +allcssdetails.get(i));
					log.info("snsFriendes size is " +snsFriendes.size());
					otherFriendes.add(allcssdetails.get(i));
				}
			}
		}
		
		return otherFriendes;
	}
	
	public List<CssAdvertisementRecord> getOtherFlist(){
		log.info("Called getOtherFlist to get-> OtherFriends list ");
		return otherFriends;
		
	}

	public void setOtherFriends(List<CssAdvertisementRecord> otherFriends) {
		log.info("Setting OtherFriends list ");
		this.otherFriends = otherFriends;
	}

	public int getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(int selectedValue) {
		this.selectedValue = selectedValue;
	}

	
}

	 
