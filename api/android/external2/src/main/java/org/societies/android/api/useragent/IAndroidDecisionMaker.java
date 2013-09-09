package org.societies.android.api.useragent;

import org.societies.api.internal.personalisation.model.IOutcome;

public interface IAndroidDecisionMaker{
	//Array of interface method signatures
	String methodsArray [] = {"makeDecision(java.util.List<org.societies.api.internal.personalisation.model.IOutcome> intents, " +
			"								java.util.List<org.societies.api.internal.personalisation.model.IOutcome> preferences)"};


	 public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences);
}