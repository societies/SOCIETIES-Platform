package org.societies.android.platform.useragent.feedback.guis;

import org.societies.android.platform.useragent.feedback.R;

public class SimpleNotificationPopup extends UserFeedbackPopup {

    public SimpleNotificationPopup() {
        super(R.layout.activity_simple_popup,
                R.id.simpleNotificationProposalText,
                R.id.simpleNotificationAcceptButton,
                UserFeedbackPopup.NOT_APPLICABLE);
    }

    @Override
    protected void populateOptions() {
        // do nothing
    }

}
