package org.societies.orchestration.sca;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.orchestration.sca.api.ISCAManager;

public class NotificationHandler {
	
	private static final String FEEDBACK_YES = "Yes";
	private static final String FEEDBACK_NO = "No";
	private static final String[] FEEDBACK_OPTIONS = new String[] {FEEDBACK_YES, FEEDBACK_NO};
	
	private IUserFeedback userFeedback;
	private ISCAManager scaManager;
	
	public NotificationHandler(IUserFeedback userFeedback, ISCAManager scaManager) {
		this.userFeedback=userFeedback;
		this.scaManager=scaManager;
	}
	
	public void sendUserFeedback(String id, String message) {
		new Thread(new SendFeedbackThread(id, message)).start();;
	}
	
	private void recieveResult(String id, String reply) {
		this.scaManager.feedbackResult(id, reply);
	}
	
	private class SendFeedbackThread extends Thread {
		
		private String id;
		private String message;
		
		public SendFeedbackThread(String id, String message) {
			this.id=id;
			this.message=message;
		}
		
		public void run() {
			ExpProposalContent content = new ExpProposalContent(message, FEEDBACK_OPTIONS);
			List<String> reply = new ArrayList<String>();
				try {
					reply = userFeedback.getExplicitFB(org.societies.api.internal.useragent.model.ExpProposalType.ACKNACK, content).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String replyMsg = null;
				if(!reply.isEmpty())
				{
					replyMsg = reply.get(0);
				} 
				else {
					replyMsg = FEEDBACK_NO;
				}
				recieveResult(id, replyMsg);
		}
		
	}

}
