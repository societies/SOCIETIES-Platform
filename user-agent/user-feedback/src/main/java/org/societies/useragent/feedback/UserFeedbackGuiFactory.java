package org.societies.useragent.feedback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.useragent.model.*;
import org.societies.useragent.feedback.guis.AckNackGUI;
import org.societies.useragent.feedback.guis.CheckBoxGUI;
import org.societies.useragent.feedback.guis.RadioGUI;
import org.societies.useragent.feedback.guis.TimedGUI;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.List;
import java.util.concurrent.Future;

public class UserFeedbackGuiFactory {
    private static final Logger log = LoggerFactory.getLogger(UserFeedbackGuiFactory.class);

    //GUI types for forms
    private static final String RADIO = "radio";
    private static final String CHECK = "check";
    private static final String ACK = "ack";
    private static final String ABORT = "abort";
    private static final String NOTIFICATION = "notification";
    private static final String PRIVACY_NEGOTIATION = "privacy-negotiation";
    private static final String PRIVACY_ACCESS_CONTROL = "privacy-access-control";
    private static final String UNDEFINED = "undefined";

    // disallow the creation of instances
    private UserFeedbackGuiFactory() {
    }

    /**
     * Helper methods to generate feedback forms - explicit, implicit and notification
     */
    public static FeedbackForm generateExpFeedbackForm(String requestId, int type, String proposalText, List<String> optionsList) {
        FeedbackForm newFbForm = new FeedbackForm();
        //add unique id
        newFbForm.setID(requestId);
        //add text to show to user
        newFbForm.setText(proposalText);
        //add data
        String[] optionsArray = new String[optionsList.size()];
        for (int i = 0; i < optionsList.size(); i++) {
            optionsArray[i] = optionsList.get(i);
        }
        newFbForm.setData(optionsArray);
        //add type
        if (type == ExpProposalType.RADIOLIST) {
            newFbForm.setType(RADIO);
        } else if (type == ExpProposalType.CHECKBOXLIST) {
            newFbForm.setType(CHECK);
        } else if (type == ExpProposalType.ACKNACK) {
            newFbForm.setType(ACK);
        } else {
            log.error("Could not understand this type of explicit GUI: " + type);
        }
        return newFbForm;
    }

    public static FeedbackForm generateImpFeedbackForm(String requestId, int type, String proposalText, int timeout) {
        FeedbackForm newFbForm = new FeedbackForm();
        //add unique id
        newFbForm.setID(requestId);
        //add text to show user
        newFbForm.setText(proposalText);
        //add data
        String[] data = {Integer.toString(timeout)};
        newFbForm.setData(data);
        //add type
        if (type == ImpProposalType.TIMED_ABORT) {
            newFbForm.setType(ABORT);
        } else {
            log.error("Could not understand this type of implicit GUI: " + type);
        }
        return newFbForm;
    }

    public static FeedbackForm generateNotificationForm(String requestId, String notificationTxt) {
        FeedbackForm newFbForm = new FeedbackForm();
        //add unique id
        newFbForm.setID(requestId);
        //add text to show user
        newFbForm.setText(notificationTxt);
        //add data
        String[] data = {"5000"};
        newFbForm.setData(data);
        //add type
        newFbForm.setType(NOTIFICATION);
        return newFbForm;
    }


    /**
     * Called by UACommsServer to request explicit feedback for remote User Agent
     * (non-Javadoc)
     *
     * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getExplicitFBforRemote(int, org.societies.api.internal.useragent.model.ExpProposalContent)
     */
    public static Future<List<String>> getExplicitFBforRemote(int type, ExpProposalContent content) {
        log.debug("Request for explicit feedback received from remote User Agent");
        List<String> result;

        //show GUIs on local device
        log.debug("Returning explicit feedback to UACommsServer");
        String proposalText = content.getProposalText();
        String[] options = content.getOptions();
        if (type == ExpProposalType.RADIOLIST) {
            log.debug("Radio list GUI");
            RadioGUI gui = new RadioGUI();
            result = gui.displayGUI(proposalText, options);
        } else if (type == ExpProposalType.CHECKBOXLIST) {
            log.debug("Check box list GUI");
            CheckBoxGUI gui = new CheckBoxGUI();
            result = gui.displayGUI(proposalText, options);
        } else { //ACK-NACK
            log.debug("ACK/NACK GUI");
            result = AckNackGUI.displayGUI(proposalText, options);
        }

        return new AsyncResult<List<String>>(result);
    }

    /**
     * Called by UACommsServer to request implicit feedback for remote User Agent
     * (non-Javadoc)
     *
     * @see org.societies.useragent.api.feedback.IInternalUserFeedback#getImplicitFBforRemote(int, org.societies.api.internal.useragent.model.ImpProposalContent)
     */
    public static Future<Boolean> getImplicitFBforRemote(int type, ImpProposalContent content) {
        log.debug("Request for implicit feedback received from remote User Agent");
        Boolean result = null;

        //show GUIs on local device
        if(log.isDebugEnabled()) log.debug("Returning implicit feedback to UACommsServer");
        String proposalText = content.getProposalText();
        int timeout = content.getTimeout();
        if (type == ImpProposalType.TIMED_ABORT) {
            if(log.isDebugEnabled()) log.debug("Timed Abort GUI");
            TimedGUI gui = new TimedGUI();
            result = gui.displayGUI(proposalText, timeout);
        }

        return new AsyncResult<Boolean>(result);
    }
}
