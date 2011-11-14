package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;


/**
 * This class represents a Rule in XACML format. The PPNPOutcome class contains the following:
 * Effect : PrivacyOutcomeConstants (ALLOW, BLOCK)
 * RuleTarget: A target specifies:
 * 		the Subject: by IDigitalPersonalIdentifier and if applicable an IServiceIdentifier
 * 		the Resource: by ICtxAttributeIdentifier
 * 		the Action: READ,WRITE,CREATE,DELETE
 * Conditions: a list of conditions that have to be satisfied by the other party. These are processed during the negotiation phase 
 * and not during the PPN preference evaluation phase.
 * 
 * @author Elizabeth
 *
 */
public class PPNPOutcome implements IPrivacyOutcome, Serializable {


	private ICtxAttributeIdentifier ctxID;
	private PrivacyOutcomeConstants effect;
	private RuleTarget rule;
	private List<Condition> conditions;
	private PrivacyPreferenceTypeConstants myOutcomeType;
	private int confidenceLevel;
	public PPNPOutcome(PrivacyOutcomeConstants effect, RuleTarget target, List<Condition> conditions) throws URISyntaxException{
		this.rule = target;
		this.effect = effect;
		this.ctxID = rule.getResource().getCtxIdentifier();
		this.conditions = conditions;
		this.myOutcomeType = PrivacyPreferenceTypeConstants.PPNP;
		
		
	}
	
	
	public RuleTarget getRuleTarget(){
		return this.rule;
	}
	public PrivacyOutcomeConstants getEffect(){
		return this.effect;
	}
	
	public boolean affectsSubject(Subject subject){
		if (null==this.rule.getSubjects()){
			return true;
		}
		if (this.rule.getSubjects().contains(subject)){
			return true;
		}
		return false;
	}
	
	public boolean isActionIncluded(Action a){
		if (null==this.rule.getActions()){
			return true;
		}
		if (this.rule.getActions().contains(a)){
			return true;
		}
		return false;
	}
	
	public List<Condition> getConditions(){
		return this.conditions;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getOutcomeType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getOutcomeType() {
		return PrivacyPreferenceTypeConstants.PPNP;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyOutcome#getConfidenceLevel()
	 */
	@Override
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}
	
	public void setConfidenceLevel(int c){
		this.confidenceLevel = c;
	}
	
	public String toString(){
		String str = "";
		str = str.concat(this.effect.toString()+":");
		List<Action> actions = this.rule.getActions();
		for (int i=0; i<actions.size(); i++){
			str = str.concat(actions.get(i).getActionType().toString());
			if (i<actions.size()-1){
				str = str.concat(",");
			}
		}
		
		
		return str;
	}
	public String toFullString(){
		String print = "Outcome:\n";
		
		print = print.concat("Effect: "+this.effect+"\n");
		
		print = print.concat(this.rule.toString());
		
		for (Condition c: conditions){
			print = print.concat(c.toString());
		}
		return print;
	}
	
	
}
