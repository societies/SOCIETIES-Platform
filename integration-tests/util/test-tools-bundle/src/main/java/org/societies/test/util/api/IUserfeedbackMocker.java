package org.societies.test.util.api;

import org.societies.test.util.api.model.UserFeedbackMockResult;
import org.societies.test.util.api.model.UserFeedbackType;

public interface IUserfeedbackMocker {
	public void setEnabled(boolean enabled);
	public boolean isEnabled();
	/**
	 * Add a pre-selected reply to a user feedback request
	 * @param feedbackType
	 * @param reply
	 */
	public void addReply(UserFeedbackType feedbackType, UserFeedbackMockResult reply);
	/**
	 * Remove pre-selected replies for a specific request type
	 * @param feedbackType
	 */
	public void removeReply(UserFeedbackType feedbackType);
	/**
	 * Remove pre-selected replies
	 */
	public void removeAllReplies();
}
