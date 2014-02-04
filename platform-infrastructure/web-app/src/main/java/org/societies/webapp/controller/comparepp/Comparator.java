package org.societies.webapp.controller.comparepp;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

public final class Comparator {
	
	private static Logger log = LoggerFactory.getLogger(Comparator.class);
	
	public static HashMap<RequestItem, RequestItem> comparePolicyToAgreement(RequestPolicy policy, AgreementEnvelope agreement) {
		HashMap<RequestItem, RequestItem> changes = new HashMap<RequestItem, RequestItem>();
		for(RequestItem policyItem : policy.getRequestItems()) {
			for(ResponseItem agreementResponse : agreement.getAgreement().getRequestedItems()) {
				if(policyItem.getResource().equals(agreementResponse.getRequestItem().getResource())){
					RequestItem agreementItem = agreementResponse.getRequestItem();
					if(!agreementItem.getActions().equals(policyItem.getActions())) {
						changes.put(policyItem, agreementItem);
					}
					if(!checkConditions(agreementItem.getConditions(), policyItem.getConditions())) {						
						log.debug("Conditions have changed");
						changes.put(policyItem, agreementItem);
					} else {
						log.debug("Conditions are the same!");
					}
					if(agreementResponse.getDecision().equals(Decision.DENY)) {
						log.debug("Decision has been changed");
						changes.put(policyItem, agreementItem);
					}
					break;
				}
				
			}
			
		}
		return changes;
	}
	
	private static boolean checkConditions(List<Condition> agreementConditions, List<Condition> policyConditions) {
		if(agreementConditions.size()!=policyConditions.size()) {
			return false;
		}
		for(Condition aC : agreementConditions) {
			boolean foundCon = false;
			for(Condition pC : policyConditions) {
				if(aC.getConditionConstant().equals(pC.getConditionConstant())) {
					foundCon = true;
					if(!pC.getValue().equals(aC.getValue())) {
						return false;
					}
					break;
				}
			}
			if(!foundCon) {
				return false;
			}
		}
		return true;
	}

}
