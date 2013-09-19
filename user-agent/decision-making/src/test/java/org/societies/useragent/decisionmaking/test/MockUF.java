package org.societies.useragent.decisionmaking.test;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.AccessControlResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class MockUF implements IUserFeedback {

    @Override
    public Future<List<String>> getExplicitFB(String requestId, int type, ExpProposalContent content) {
        return null;
    }

    @Override
    public Future<List<String>> getExplicitFB(int type, final ExpProposalContent content) {
        return new Future<List<String>>() {
            @Override
            public List<String> get() {
                List<String> res = new ArrayList<String>();
                res.add("Yes");
                return res;
            }

            @Override
            public List<String> get(long time, TimeUnit unit) {
                List<String> res = new ArrayList<String>();
                res.add("Yes");
                return res;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean cancel(boolean sign) {
                return sign;
            }
        };
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content) {
        return null;
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(int type,
                                                   ExpProposalContent content,
                                                   IUserFeedbackResponseEventListener<List<String>> callback) {
        return null;
    }

    @Override
    public Future<List<String>> getExplicitFBAsync(String requestId, int type, ExpProposalContent content, IUserFeedbackResponseEventListener<List<String>> callback) {
        return null;
    }

    @Override
    public Future<Boolean> getImplicitFB(String requestId, int type, ImpProposalContent content) {
        return null;
    }


    @Override
    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
        return new Future<Boolean>() {
            @Override
            public Boolean get() {
                return true;
            }

            @Override
            public Boolean get(long time, TimeUnit unit) {
                return true;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean cancel(boolean sign) {
                return sign;
            }
        };
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content) {
        return null;
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(int type,
                                              ImpProposalContent content,
                                              IUserFeedbackResponseEventListener<Boolean> callback) {
        return null;
    }

    @Override
    public Future<Boolean> getImplicitFBAsync(String requestId, int type, ImpProposalContent content, IUserFeedbackResponseEventListener<Boolean> callback) {
        return null;
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(String requestId, ResponsePolicy policy, NegotiationDetailsBean details) {
        return null;
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy,
                                                          NegotiationDetailsBean details) {
        return null;
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy
                                                                       policy, NegotiationDetailsBean details) {
        return null;
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy,
                                                               NegotiationDetailsBean details,
                                                               IUserFeedbackResponseEventListener<ResponsePolicy> callback) {
        return null;
    }

    @Override
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(String requestId, ResponsePolicy policy, NegotiationDetailsBean details, IUserFeedbackResponseEventListener<ResponsePolicy> callback) {
        return null;
    }

    public Future<List<AccessControlResponseItem>> getAccessControlFB(String requestId, Requestor requestor, List<AccessControlResponseItem> items) {
        return null;
    }

    public Future<List<AccessControlResponseItem>> getAccessControlFB(Requestor requestor, List<AccessControlResponseItem> items) {
        return null;
    }

    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(Requestor requestor, List<AccessControlResponseItem> items) {
        return null;
    }

    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(Requestor requestor, List<AccessControlResponseItem> items, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>> callback) {
        return null;
    }

    public Future<List<AccessControlResponseItem>> getAccessControlFBAsync(String requestId, Requestor requestor, List<AccessControlResponseItem> items, IUserFeedbackResponseEventListener<List<AccessControlResponseItem>> callback) {
        return null;
    }

//    public Future<List<ResponseItem>> getAccessControlFB(Requestor requestor,
//                                                         List<ResponseItem> items) {
//        return null;
//    }

//    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor,
//                                                              List<ResponseItem> items) {
//        return null;
//    }

//    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor,
//                                                              List<ResponseItem> items,
//                                                              IUserFeedbackResponseEventListener<List<ResponseItem>> callback) {
//        return null;
//    }

//    public Future<List<ResponseItem>> getAccessControlFBAsync(String requestId, Requestor requestor, List<ResponseItem> items, IUserFeedbackResponseEventListener<List<ResponseItem>> callback) {
//        return null;
//    }

    @Override
    public void showNotification(String notificationText) {
    }

    @Override
    public FeedbackForm getNextRequest() {
        return null;
    }

    @Override
    public void submitExplicitResponse(String id, List<String> result) {
    }

    /**
     * Submit an explicit response for privacy negotiation userfeedback request type
     *
     * @param requestId Id of the userfeedback request
     */
    @Override
    public void submitPrivacyNegotiationResponse(String requestId, NegotiationDetailsBean
            negotiationDetails, ResponsePolicy result) {
    }

    @Override
    public void submitAccessControlResponse(String requestId, List<AccessControlResponseItem> responseItems, RequestorBean requestorBean) {

    }

    @Override
    public void submitImplicitResponse(String id, Boolean result) {
    }

//    @Override
//    public void submitAccessControlResponse(String s, List<ResponseItem> responseItems, RequestorBean requestorBean) {
//
//    }

    @Override
    public void clear() {
    }

}
