package org.societies.orchestration.sca;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisManagerCallback;
import org.societies.api.schema.cis.community.CommunityMethods;
import org.societies.api.schema.cis.community.Participant;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;
import org.societies.orchestration.sca.api.INotificationHandler;
import org.societies.orchestration.sca.api.ISCAManager;
import org.societies.orchestration.sca.api.ISCAManagerClient;
import org.societies.orchestration.sca.api.ISCARemote;
import org.societies.orchestration.sca.comms.SCACommsClient;
import org.societies.orchestration.sca.enums.CisCallbackType;
import org.societies.orchestration.sca.model.SCAInvitation;

public class SCAManagerClient implements ISCAManagerClient {

	private ISCARemote scaRemote;
	private INotificationHandler notificationHandler;
	private HashMap<String, SCAInvitation> invitations;
	private ISCAManager scaManager;
	private ICisManager cisManager;
	private static Logger log = LoggerFactory.getLogger(SCAManagerClient.class);


	public SCAManagerClient() {
		invitations = new HashMap<String, SCAInvitation>();
	}

	public void initSCAManagerClient() {
		this.scaRemote = scaManager.getSCARemote();
		this.notificationHandler = scaManager.getNotificationHandler();
		this.notificationHandler.setClient(this);
	}

	private void joinCIS(String cisID) {

	}

	private void leaveCIS(String cisID) {

	}

	@Override
	public void addInvitation(String requestID, SCAInvitation invitation) {
		if(invitation.isForceAction()) {
			switch(invitation.getMethodType()) {
			case CREATE :
				break;
			case DELETE :
				
				break;
			case LEAVE :
				//LEAVING THE CIS IS HANDLED REMOTELY, JUST NOTIFY USER OF IT.
				notificationHandler.sendMessage("The community " + invitation.getCisName() + " has been deleted");
				break;
			case JOIN :
				break;
			}
		}
		else {
			synchronized (invitations) {
				invitations.put(requestID, invitation);
			}
			notificationHandler.addInvitationNotification(requestID, invitation.getCisName(), invitation.getFromJID(), invitation.getMethodType());
		}
	}
	@Override
	public void processInvitation(String requestID, SCASuggestedResponseType result) {
		log.debug("Processing user feedback for the invitation!");
		SCAInvitation invitation = null;
		synchronized (invitations) {
			invitation = invitations.get(requestID);
		}
		if(invitation!=null) {
			if(result.equals(SCASuggestedResponseType.ACCEPTED)) {
				switch(invitation.getMethodType()) {
				case CREATE :
					break;
				case DELETE :
					break;
				case JOIN :
					log.debug("Sending the joinCIS to SCAMGR");
					scaManager.joinCIS(invitation.getCisID());
					break;
				case LEAVE :
					CisMgrCallback callback = new CisMgrCallback(requestID, CisCallbackType.LEAVE_CIS);
					this.cisManager.leaveRemoteCIS(invitation.getCisID(), callback);
					break;
				}
			} else {
				log.debug("User declined");
			}
			scaRemote.sendSuggestionResponse(requestID, invitation.getFromJID(), result);
		}
		else {
			log.debug("Something went wrong :(");
			//ERROR OCCURED, SEND MESSAGE BACK TO GET ANOTHER RESPONSE!
		}
	}

	private void handleLeaveCISCallback(String requestID) {
		SCAInvitation invitation = null;
		synchronized (invitations) {
			invitation = invitations.get(requestID);
		}
		if(invitation!=null) {
			scaRemote.sendSuggestionResponse(requestID, invitation.getFromJID(), SCASuggestedResponseType.ACCEPTED);
		}
		notificationHandler.sendMessage("You have been removed from CIS: " + invitation.getCisName());
		synchronized (invitations) { 
			invitations.remove(requestID);			
		}

	}

	public class CisMgrCallback implements ICisManagerCallback {

		private String requestID;
		private CisCallbackType callbackType ;
		private String cisID;

		public CisMgrCallback(String requestID, CisCallbackType callbackType) {
			this.requestID = requestID;
			this.callbackType = callbackType;
		}

		@Override
		public void receiveResult(CommunityMethods arg0) {
			switch(this.callbackType) {
			case JOIN_CIS :
				//a differentMethodHere();
				break;
			case LEAVE_CIS :
				//FOR NOW ASSUME ITS SUCCESS
				handleLeaveCISCallback(requestID);
				break;
			}

		}

	}

	public ISCAManager getScaManager() {
		return scaManager;
	}

	public void setScaManager(ISCAManager scaManager) {
		this.scaManager = scaManager;
	}

	/**
	 * @return the cisManager
	 */
	public ICisManager getCisManager() {
		return cisManager;
	}

	/**
	 * @param cisManager the cisManager to set
	 */
	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}





}

