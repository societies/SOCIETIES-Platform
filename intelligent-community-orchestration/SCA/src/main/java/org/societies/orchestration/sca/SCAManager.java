package org.societies.orchestration.sca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.orchestration.communitylifecyclemanagementbean.Cis;
import org.societies.orchestration.sca.api.ISCAManager;
import org.societies.orchestration.sca.model.SuggestedCISImpl;

public class SCAManager extends EventListener implements ISCAManager {

	private static Logger log = LoggerFactory.getLogger(SCAManager.class);

	private IEventMgr eventMgr;
	private ICommManager commManager;
	private IUserFeedback userFeedback; //WILL NEED THIS AT SOMEPOINT?!?!
	private ICisManager cisManager;
	
	public SCAManager() {
		log.debug("Public Constructor called");

	}

	//CALLED BY OSGI???
	public void initSCAManager() {
		log.debug("Run initSCAManager()");
		//WE NEED TO LISTEN TO INTERNAL EVENTS
		registerForInternalEvents();
		
		//LETS SEND A FAKE SUGGESTED CIS
		SuggestedCISImpl cis = new SuggestedCISImpl();
		cis.setName("STuarts CIS222!");
		InternalEvent event = new InternalEvent(EventTypes.ICO_RECOMMENDTION_EVENT, "newSuggestion", "org/societies/ico/sca", cis);
		
		log.debug("Sending fake CIS!");
		try {
			this.eventMgr.publishInternalEvent(event);
		} catch (EMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void registerForInternalEvents() {
		/*String eventFilter = "(&" + 
					"(" + CSSEventConstants.EVENT_NAME + "=" + DeviceMgmtEventConstants.RFID_READER_EVENT + ")" +
					"(" + CSSEventConstants.EVENT_SOURCE + "=" + deviceId + ")" +
					")"; */
		this.eventMgr.subscribeInternalEvent(this, new String[]{EventTypes.ICO_RECOMMENDTION_EVENT}, null);


	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	//THEY ARE INTERNAL EVENTS!!!
	@Override
	public void handleInternalEvent(InternalEvent arg0) {
		// TODO Auto-generated method stub
		log.debug("Event listener has been called!");
		log.debug("We have been given a suggestion from another component, lets check it then send to user!");
		log.debug("The event is: " + arg0.geteventName());
		log.debug("Lets pretend it only comes from CPA just now ....");
		ICommunitySuggestion suggestedCis = (ICommunitySuggestion) arg0.geteventInfo();

		//DOES THIS suggestComm make any sense to make?
		//IF SO, UF TO ASK THE USER TO EDIT/CREATE/JOIN/ETC OR
		//TELL COMPONENT THAT ITS NOT GOOD? 
		//IF USER REJECTS UF- SEND BACK TO COMPONENT THAT SENT IT?!?!

		//LETS SEND IT TO UF AND ASK USER IF THIS IS WHAT THEY WANT
		 ExpProposalContent content = new ExpProposalContent("Would you like to create a new CIS: " + suggestedCis.getName(), new String[] {"Yes", "No"});
         List<String> reply = new ArrayList<String>();
		try {
			reply = this.userFeedback.getExplicitFB(org.societies.api.internal.useragent.model.ExpProposalType.ACKNACK, content).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         log.debug("Got: " + reply.get(0));
         List<ICisOwned> ownedCIS = new ArrayList<ICisOwned>();
         if(reply.get(0).equals("Yes")) {
        	try {
				ownedCIS =  (List<ICisOwned>) this.cisManager.createCis(suggestedCis.getName(), suggestedCis.getSuggestionType(), null, null).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	for(ICisOwned cis : ownedCIS) {
        		log.debug("I own: " + cis.getName());
        	}
        	
        	List<ICis> remoteCis = this.cisManager.getRemoteCis();
        	for(ICis otherCis : remoteCis){ 
        	log.debug("I am a member of " + otherCis.getName());
        	}
         }

	}

	//INJECTION
	public IEventMgr getEventMgr() {
		return eventMgr;
	}
	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

}
