package org.societies.android.platform.useragent.feedback.guis;

import org.societies.android.platform.useragent.feedback.R;

public class TimedAbortPopup extends UserFeedbackPopup {

    public TimedAbortPopup() {
        super(R.layout.activity_timedabort_popup,
                R.id.timedAbortProposalText,
                UserFeedbackPopup.NOT_APPLICABLE,
                UserFeedbackPopup.NOT_APPLICABLE);
    }

    @Override
    protected void populateOptions() {

    }
}
