package org.societies.orchestration.sca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;
import org.societies.orchestration.sca.api.INotificationHandler;
import org.societies.orchestration.sca.api.ISCAManager;
import org.societies.orchestration.sca.api.ISCAManagerClient;

public class NotificationHandler implements INotificationHandler{

	private static final String FEEDBACK_YES = "Yes";
	private static final String FEEDBACK_NO = "No";
	private static final String[] FEEDBACK_OPTIONS = new String[] {FEEDBACK_YES, FEEDBACK_NO};

	private static final String CREATE_MESSAGE = "Would you like to create the community: ";
	private static final String JOIN_MESSAGE = "Would you like to join the community: "; 
	private static final String LEAVE_MESSAGE = "Would you like to leave the community: "; 
	private static final String DELETE_MESSAGE = "Would you like to delete the community: "; 
	private static final String INVITE_MESSAGE = "\nand invite the following users:\n";
	private static final String ACCEPT_INVITE_MESSAGE ="You have been invited to ";


	private static Logger log = LoggerFactory.getLogger(NotificationHandler.class);


	private IUserFeedback userFeedback;
	private ISCAManager scaManager;
	private ISCAManagerClient scaManagerClient;

	public NotificationHandler(IUserFeedback userFeedback, ISCAManager scaManager) {
		this.userFeedback=userFeedback;
		this.scaManager=scaManager;
	}

	@Override
	public void sendMessage(String message) {
		this.userFeedback.showNotification(message);
	}

	@Override
	public void sendJoinNotification(String id, String cisName,
			List<String> affectedUsers) {
		String message = JOIN_MESSAGE+cisName;
		if(null!=affectedUsers && affectedUsers.size()>0) {
			message = message + INVITE_MESSAGE;
			for(String user : affectedUsers) {
				message = message + user + "\n";
			}
		}
		ExpProposalContent content = new ExpProposalContent(message, FEEDBACK_OPTIONS);
		new Thread(new SendFeedbackThread(id, content, false)).start();

	}

	@Override
	public void sendCreateNotification(String id, String cisName,
			List<String> affectedUsers) {
		String message = CREATE_MESSAGE+cisName;
		if(null!=affectedUsers && affectedUsers.size()>0) {
			message = message + INVITE_MESSAGE;
			for(String user : affectedUsers) {
				message = message + user + "\n";
			}
		}

		ExpProposalContent content = new ExpProposalContent(message, FEEDBACK_OPTIONS);
		new Thread(new SendFeedbackThread(id, content, false)).start();
	}

	@Override
	public void sendLeaveNotification(String id, String cisName,
			List<String> affectedUsers) {
		String message = LEAVE_MESSAGE+cisName;	
		ExpProposalContent content = new ExpProposalContent(message, FEEDBACK_OPTIONS);
		new Thread(new SendFeedbackThread(id, content, false)).start();

	}

	@Override
	public void sendDeleteNotification(String id, String cisName,
			List<String> affectedUsers) {
		String message = DELETE_MESSAGE+cisName;	
		ExpProposalContent content = new ExpProposalContent(message, FEEDBACK_OPTIONS);
		new Thread(new SendFeedbackThread(id, content, false)).start();

	}

	@Override
	public void addInvitationNotification(String id, String cisName,
			String fromJID, SCASuggestedMethodType methodType) {
		String message = ACCEPT_INVITE_MESSAGE + methodType.toString().toLowerCase() + " the " +
				cisName + " from " + fromJID + ". Would you like to accept?";		
		ExpProposalContent content = new ExpProposalContent(message, FEEDBACK_OPTIONS);
		new Thread(new SendFeedbackThread(id, content, true)).start();
	}

	private void recieveResult(String id, SCASuggestedResponseType response, boolean invitation) {
		log.debug("I have recieved the result!");
		if(invitation) {
			log.debug("Sending result to ClientMgr");
			this.scaManagerClient.processInvitation(id, response);
		}
		else {
			log.debug("Sending feedback result back to MGR!");
			this.scaManager.feedbackResult(id, response);
		}

	}

	private class SendFeedbackThread extends Thread {

		private String id;
		private ExpProposalContent expProposalContent;
		private boolean invitation;

		public SendFeedbackThread(String id, ExpProposalContent expProposalContent, boolean invitation) {
			this.id=id;
			this.expProposalContent = expProposalContent;
			this.invitation = invitation;
		}

		public void run() {
			log.debug("Creating new UF Notification");

			String reply = FEEDBACK_NO;
			try {
				log.debug("Waiting for reply!");
				reply = userFeedback.getExplicitFB(org.societies.api.internal.useragent.model.ExpProposalType.ACKNACK, this.expProposalContent).get().get(0);
			} catch (InterruptedException e) {
				log.debug("Error");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.debug("Error2");
			}
			SCASuggestedResponseType feedback = SCASuggestedResponseType.DECLINED;
			if(reply.equals(FEEDBACK_YES)) {
				feedback = SCASuggestedResponseType.ACCEPTED;
			}

			log.debug("Returning result from UF thread.");
			recieveResult(this.id, feedback, this.invitation);
		}

	}

	@Override
	public void setClient(ISCAManagerClient scaMgrClient) {
		this.scaManagerClient = scaMgrClient;

	}


}