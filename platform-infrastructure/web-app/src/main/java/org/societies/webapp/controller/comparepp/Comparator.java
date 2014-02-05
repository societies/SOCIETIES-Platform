package org.societies.webapp.controller.comparepp;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;

public final class Comparator {

	private static Logger log = LoggerFactory.getLogger(Comparator.class);

	private static String[] sensedDataTypes = new String[]{CtxAttributeTypes.TEMPERATURE, 
		CtxAttributeTypes.STATUS,
		CtxAttributeTypes.LOCATION_SYMBOLIC,
		CtxAttributeTypes.LOCATION_COORDINATES,
		CtxAttributeTypes.ACTION};


	public static boolean isAttributeSensed(String type) {
		for (String sensedType : sensedDataTypes){
			if (sensedType.equalsIgnoreCase(type)){
				return true;
			}
		}
		return false;
	}

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
						changes.put(policyItem, agreementItem);
					} 
					if(agreementResponse.getDecision().equals(Decision.DENY)) {
						changes.put(policyItem, agreementItem);
					}
					break;
				}

			}

		}
		return changes;
	}

	public static boolean checkAccessControl(AccessControlPreferenceTreeModel model) {
		if(null!=model) {
			IPrivacyPreference p = model.getRootPreference();
			if(p!=null) {
				Enumeration d = p.depthFirstEnumeration();
				while(d.hasMoreElements()) {
					PrivacyPreference nxt = (PrivacyPreference) d.nextElement();
					if(nxt.getOutcome() instanceof AccessControlOutcome) {
						AccessControlOutcome outcome = (AccessControlOutcome) nxt.getOutcome();
						if(outcome.getEffect().equals(PrivacyOutcomeConstantsBean.BLOCK)) {
							return true;						
						} 
					}
					if (nxt.getOutcome() instanceof DObfOutcome) {
						DObfOutcome outcome = (DObfOutcome) nxt.getOutcome();
						if(outcome.getObfuscationLevel()!=1.0) {
							return true;
						}

					}
				}
			} else {
				log.debug("Model is null!");
			}
		}
		return false;
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
