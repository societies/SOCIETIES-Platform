package org.societies.orchestration.sca;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;
import org.societies.orchestration.sca.api.INotificationHandler;
import org.societies.orchestration.sca.api.ISCAManager;
import org.societies.orchestration.sca.api.ISCAManagerClient;
import org.societies.orchestration.sca.api.ISCARemote;
import org.societies.orchestration.sca.comms.SCACommsClient;
import org.societies.orchestration.sca.model.SCAInvitation;

public class SCAManagerClient implements ISCAManagerClient {

	private ISCARemote scaRemote;
	private INotificationHandler notificationHandler;
	private HashMap<String, SCAInvitation> invitations;
	private ISCAManager scaManager;
	private static Logger log = LoggerFactory.getLogger(SCAManagerClient.class);


	public SCAManagerClient() {
		invitations = new HashMap<String, SCAInvitation>();
	}
	
	public void initSCAManagerClient() {
		this.scaRemote = scaManager.getSCARemote();
		this.notificationHandler = scaManager.getNotificationHandler();
		this.notificationHandler.setClient(this);
	}

	@Override
	public void addInvitation(String requestID, SCAInvitation invitation) {
		synchronized (invitations) {
			invitations.put(requestID, invitation);
		}
		notificationHandler.addInvitationNotification(requestID, invitation.getCisName(), invitation.getFromJID(), invitation.getMethodType());
	}

	@Override
	public void processInvitation(String requestID, SCASuggestedResponseType result) {
		log.debug("Processing user feedback for the invitation!");
		SCAInvitation invitation = null;
		synchronized (invitations) {
			invitation = invitations.get(requestID);
			invitations.remove(requestID);
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
					break;
				}
			}
			scaRemote.sendSuggestionResponse(requestID, invitation.getFromJID(), result);
		}
		else {
			//ERROR OCCURED, SEND MESSAGE BACK TO GET ANOTHER RESPONSE!
		}
	}

	public ISCAManager getScaManager() {
		return scaManager;
	}

	public void setScaManager(ISCAManager scaManager) {
		this.scaManager = scaManager;
	}
	
		

}

