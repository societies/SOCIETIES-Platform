package org.societies.useragent.decisionmaking.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.societies.api.internal.useragent.feedback.IUserFeedbackResponseEventListener;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.util.concurrent.*;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.useragent.conflict.ConflictResolutionRule;
import org.societies.useragent.conflict.ConflictResolutionManager;
import org.societies.useragent.decisionmaking.DecisionMaker;
import org.societies.useragent.decisionmaking.DecisionMakingCallback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.useragent.model.FeedbackForm;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;

import java.util.List;
import java.util.concurrent.Future;


public class MockUF implements IUserFeedback{
  
    public Future<List<String>> getExplicitFB(int type, final 
    		ExpProposalContent content){
    	return new Future<List<String>>(){
    		public List<String> get(){
    			List<String> res=new ArrayList<String>();
    			res.add("Yes");
    			return res;
    		}
    		public List<String> get(long time, TimeUnit unit){
    			List<String> res=new ArrayList<String>();
    			res.add("Yes");
    			return res;
    		}
    		public boolean isDone(){return true;}
    		public boolean isCancelled(){return false;}
    		public boolean cancel(boolean sign){return sign;}
    	};
    }
    public Future<List<String>> getExplicitFBAsync(int type, ExpProposalContent content){
    	return null;
    }
    
    public Future<List<String>> getExplicitFBAsync(int type, 
    		ExpProposalContent content, 
    		IUserFeedbackResponseEventListener<List<String>> callback){
    	return null;
    }


    public Future<Boolean> getImplicitFB(int type, ImpProposalContent content){
    	new Future<Boolean>(){
    		public Boolean get(){
    			return true;
    		}
    		public Boolean get(long time, TimeUnit unit){
    			return true;
    		}
    		public boolean isDone(){return true;}
    		public boolean isCancelled(){return false;}
    		public boolean cancel(boolean sign){return sign;}
    	};
    }
    public Future<Boolean> getImplicitFBAsync(int type, ImpProposalContent content){
    	return null;
    }
    public Future<Boolean> getImplicitFBAsync(int type, 
    		ImpProposalContent content, 
    		IUserFeedbackResponseEventListener<Boolean> callback){
    	return null;
    }
    public Future<ResponsePolicy> getPrivacyNegotiationFB(ResponsePolicy policy, 
    		NegotiationDetailsBean details){
    	return null;
    }
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy 
    		policy, NegotiationDetailsBean details){
    	return null;
    }
    public Future<ResponsePolicy> getPrivacyNegotiationFBAsync(ResponsePolicy policy, 
    		NegotiationDetailsBean details, 
    		IUserFeedbackResponseEventListener<ResponsePolicy> callback){
    	return null;
    }
    
    public Future<List<ResponseItem>> getAccessControlFB(Requestor requestor, 
    		List<ResponseItem> items){
    	return null;
    }
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, 
    		List<ResponseItem> items){
    	return null;
    }
    public Future<List<ResponseItem>> getAccessControlFBAsync(Requestor requestor, 
    		List<ResponseItem> items, 
    		IUserFeedbackResponseEventListener<List<ResponseItem>> callback){
    	return null;
    }
    public void showNotification(String notificationText){}

    public FeedbackForm getNextRequest(){return null;}

    public void submitExplicitResponse(String id, List<String> result){}

    /**
     * Submit an explicit response for privacy negotiation userfeedback request type
     *
     * @param requestId Id of the userfeedback request
     */
    public void submitExplicitResponse(String requestId, NegotiationDetailsBean 
    		negotiationDetails, ResponsePolicy result){}

    public void submitImplicitResponse(String id, Boolean result){}

    void clear(){}

}