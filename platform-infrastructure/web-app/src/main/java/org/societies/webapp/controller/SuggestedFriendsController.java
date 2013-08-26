package org.societies.webapp.controller;

import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.FriendFilter;
import org.societies.api.internal.css.ICSSInternalManager;
import org.societies.api.schema.activity.MarshaledActivity;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.*;
import org.societies.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
	private List<CssAdvertisementRecord> search;
	
	private Future<List<CssRequest>> asynchFR;
	
	private List<MarshaledActivity> activities;
	private List<CssAdvertisementRecord> friends;
	private String friendid;
	private static String findfriend;
		
	public String getFindfriend() {
		return findfriend;
	}

	public void setFindfriend(String findfriend) {
		this.findfriend = findfriend;
	}

	private static String filterstring = "None";




	/**
	 * OSGI service get auto injected
	 */
	//@Autowired
	//private ICSSInternalManager cssLocalManager;
	@Autowired
	private ICommManager commMngrRef;
	private String name;
	private String Id;
	
	
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
	    log.debug("setUserService() has been called with " + userService);
	    this.userService = userService;
	}
	
	public SuggestedFriendsController() {
	    log.info("SuggestedFriendsController constructor called");
	}

	public List<CssAdvertisementRecordDetailed> getsnsFriendes(){
		log.debug("getsnsFriends method called");
		snsSuggestedFriends = this.getSuggestedfriends();
		log.debug("And we're BACK :-) ");
		List<CssAdvertisementRecord> snsFriends = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecord> otherFriends = new ArrayList<CssAdvertisementRecord>();
		snsFriendes = new ArrayList<CssAdvertisementRecordDetailed>();
		for(Entry<CssAdvertisementRecord, Integer> entry : snsSuggestedFriends.entrySet()){
			log.debug("snsFriends ID " +entry.getKey().getId());
			log.debug("snsFriends Name " +entry.getKey().getName());
			log.debug("snsFriends Hashmap value " +entry.getValue());
			if(entry.getValue() == 0){
				otherFriends.add(entry.getKey());
				log.debug("otherFriends SIZE is " +snsFriends.size());
			}else {
				snsFriends.add(entry.getKey());
				log.debug("snsFriends SIZE is " +snsFriends.size());
			}
			
			
		}
		this.setOtherFriends(otherFriends);
		asyncssdetails = this.cssLocalManager.getCssAdvertisementRecordsFull();
		try {
			allcssdetails = asyncssdetails.get();
			log.debug("allcssdetails SIZE is " +allcssdetails.size());
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
				log.debug("entry id is " +entry.getId());
				log.debug("allcssdetails ID is " +allcssdetails.get(i).getResultCssAdvertisementRecord().getId());
				if(entry.getId().contains(allcssdetails.get(i).getResultCssAdvertisementRecord().getId())){
					log.debug("ADDING record to list " +allcssdetails.get(i));
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
		 
		log.debug("getSuggestedFriends method called");
		HashMap<CssAdvertisementRecord,Integer> snsSuggestedFriends = null;
		
		filterstring = this.getFilterstring();
		log.debug("getSuggestedFriends filterstring has the value set to : " +filterstring);
		
		int facebook =		0x0000000001;
		int twitter = 		0x0000000010;
		int linkedin =		0x0000000100;
		int foursquare = 	0x0000001000;
		int googleplus = 	0x0000010000;
		int all = 			0x0000011111;
		int CISMemeber = 	0x0000100000;
		
		int filterType = 0;
		
		FriendFilter filter = new FriendFilter();
		
		if (filterstring.equalsIgnoreCase("facebook")) {
			filterType = facebook;
			filter.setFilterFlag(facebook);
			log.debug("getSuggestedFriends setting friend filter with filterflag as : " +filter.getFilterFlag());
			
		} 
		
		if (filterstring.equalsIgnoreCase("twitter")) {
			filterType = twitter;
			filter.setFilterFlag(twitter);
			log.debug("getSuggestedFriends setting friend filter with filterflag as : " +filter.getFilterFlag());
		} 
		
		if (filterstring.equalsIgnoreCase("linkedin")) {
			filterType = linkedin;
			filter.setFilterFlag(linkedin);
			log.debug("getSuggestedFriends setting friend filter with filterflag as : " +filter.getFilterFlag());
		} 
		
		if (filterstring.equalsIgnoreCase("foursquare")) {
			filterType = foursquare;
			filter.setFilterFlag(foursquare);
			log.debug("getSuggestedFriends setting friend filter with filterflag as : " +filter.getFilterFlag());
		} 
		
		if (filterstring.equalsIgnoreCase("googleplus")) {
			filterType = googleplus;
			filter.setFilterFlag(googleplus);
			log.debug("getSuggestedFriends setting friend filter with filterflag as : " +filter.getFilterFlag());
		} 
		
		if (filterstring.equalsIgnoreCase("none")) {
			filterType = all;
			filter.setFilterFlag(all);
			log.debug("getSuggestedFriends setting friend filter with filterflag as : " +filter.getFilterFlag());
		} 
		if (filterstring.equalsIgnoreCase("CISMember")) {
			filterType = CISMemeber;
			filter.setFilterFlag(CISMemeber);
			log.debug("getSuggestedFriends setting friend filter with filterflag as : " +filter.getFilterFlag());
			
		}
		
		try {
			log.debug("getSuggestedFriends this call has returned " +filter + "with flag set to: " +filter.getFilterFlag());
			
			log.debug("About to call the suggestedFriendDetails with filterflag: " +filter.getFilterFlag());
			Future<HashMap<CssAdvertisementRecord, Integer>> asynchSnsSuggestedFriends = getCssLocalManager().getSuggestedFriendsDetails(filter); //suggestedFriends();
			snsSuggestedFriends = asynchSnsSuggestedFriends.get();
	
			log.debug("Back from call the suggestedFriendDetails with result: " +snsSuggestedFriends);
			log.debug("snsSuggestedFriends contains" +snsSuggestedFriends);
			for(Entry<CssAdvertisementRecord, Integer> entry : snsSuggestedFriends.entrySet()){
				log.debug("snsSuggestedFriends ID " +entry.getKey().getId());
				log.debug("snsSuggestedFriends Name " +entry.getKey().getName());
				log.debug("snsSuggestedFriends Hashmap value " +entry.getValue());
				
			}
			
	
		} catch (Exception e) {
			
		}
		;
		return snsSuggestedFriends;
		}
	
	public List<CssAdvertisementRecord> getfriends(){
			
		log.debug("getfriendslist method called");
		
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
			
		log.debug("Friends SIZE is now " +friends.size());
			
		return friends;
		
		
	}
		
	public void sendfriendrequest(String friendid){
			
		log.debug("sendfriendrequest method called");
		
		this.cssLocalManager.sendCssFriendRequest(friendid);
			
	}
	
	public void handlerequestaccept(String friendid){
		
		log.debug("ACCEPT method called");
		log.debug("ACCEPT method called friendid " +friendid);
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity(friendid); 
		pendingFR.setRequestStatus(CssRequestStatusType.ACCEPTED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		getCssLocalManager().acceptCssFriendRequest(pendingFR);
	
	}

	public void handlerequestdecline(String friendid){
		
		log.debug("DECLINE method called");
		log.debug("DECLINE method called friendid " +friendid);
		CssRequest pendingFR = new CssRequest();
		pendingFR.setCssIdentity(friendid);
		pendingFR.setRequestStatus(CssRequestStatusType.DENIED);
		pendingFR.setOrigin(CssRequestOrigin.LOCAL);
		getCssLocalManager().declineCssFriendRequest(pendingFR);
		
	}

	public void handlerequestcancelled(String friendid){
		
		log.debug("CANCELLED method called");
		log.debug("CANCELLED method called friendid " +friendid);
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
		log.debug("getActivities called");
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
	
	public List<CssAdvertisementRecordDetailed> getOtherFriendes() {
		
		List<CssAdvertisementRecord> otherFriends = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecordDetailed> otherFriendes = new ArrayList<CssAdvertisementRecordDetailed>();
		
		log.debug("getOtherFriends is called");
		log.debug("allcssdetails SIZE is " +allcssdetails.size());
		otherFriends = this.getOtherFlist();
		log.debug("otherFriends SIZE is " +otherFriends.size());
		
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
				log.debug("entry id is " +entry.getId());
				log.debug("allcssdetails ID is " +allcssdetails.get(i).getResultCssAdvertisementRecord().getId());
				if(entry.getId().contains(allcssdetails.get(i).getResultCssAdvertisementRecord().getId())){
					log.debug("ADDING record to list " +allcssdetails.get(i));
					otherFriendes.add(allcssdetails.get(i));
				}
			}
		}
		
		return otherFriendes;
	}
	
	public List<CssAdvertisementRecord> getOtherFlist(){
		log.debug("Called getOtherFlist to get-> OtherFriends list ");
		return otherFriends;
		
	}

	public void setOtherFriends(List<CssAdvertisementRecord> otherFriends) {
		log.debug("Setting OtherFriends list ");
		
		this.otherFriends = otherFriends;
		
		log.debug("List size is " +otherFriends.size());
	}

	public String getFilterstring() {
		return filterstring;
	}

	public void setFilterstring(String filters) {
		log.debug("setfilterstring called with string: " +filters);
		
		SuggestedFriendsController.filterstring = filters;
		
		
	}
	
	public List<CssAdvertisementRecord> search(String name){
		log.info("SuggestedFriendsController Search method called ");
		List<CssAdvertisementRecord> cssadverts = new ArrayList<CssAdvertisementRecord>();
		List<CssAdvertisementRecord> result = new ArrayList<CssAdvertisementRecord>();
		
		//name = "John";
	
		cssadverts = this.getfriends();
		
		log.info("cssadverts size is : " +cssadverts.size());
		
		log.info("now search through the cssadverts for the name : " +name);
		
		if (name.isEmpty()) {
			log.info("Search String is empty ");
			return null;
		}else {
			for (CssAdvertisementRecord  advert: cssadverts) {
				log.info("advert name is: " +advert.getName());
	            if (advert.getName().toLowerCase().contains(name.toLowerCase())) {
	            	log.info("this is the CssAdvertisement we are looking for " +advert.getId());
	            	result.add(advert);
	            } else {
	            	log.info("this is NOT the CssAdvertisement we are looking for keep going " +advert.getId() +" name is " +advert.getName());
	            }
			}
		}
		
		return result;
		
	}

	
}

	 
