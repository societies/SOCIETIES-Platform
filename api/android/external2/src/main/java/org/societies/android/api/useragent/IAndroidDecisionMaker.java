package org.societies.android.api.useragent;

import org.societies.android.api.useragent.model;
import java.util.List;

public interface IAndroidDecisionMaker{
	//Array of interface method signatures
	String methodsArray [] = {"makeDecision(java.util.List<org.societies.android.api.useragent.model.IOutcome> intents, " +
			"								java.util.List<org.societies.android.api.useragent.model.IOutcome> preferences)"};


	 public void makeDecision(List<IOutcome> intents, List<IOutcome> preferences);
}