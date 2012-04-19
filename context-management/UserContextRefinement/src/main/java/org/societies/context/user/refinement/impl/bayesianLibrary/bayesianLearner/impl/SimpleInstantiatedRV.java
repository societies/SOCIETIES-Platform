package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.Serializable;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueTextNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.InstantiatedRV;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;


public class SimpleInstantiatedRV implements InstantiatedRV,Serializable{

	private static final long serialVersionUID = 9094757624448166137L;

	private RandomVariable randomVariable;
	
	int value;

	private boolean missing;

	public SimpleInstantiatedRV(RandomVariable randomVariable, boolean missing, int value) throws NodeValueIndexNotInNodeRangeException {
		this.randomVariable = randomVariable;
		this.missing = missing;
		if (!this.missing) {
			this.getNodeRangePositionFromValue(value);
			this.value = value;
		}
	}
	
	public SimpleInstantiatedRV(RandomVariable randomVariable, boolean missing, String valueText) throws NodeValueTextNotInNodeRangeException {
		this.randomVariable = randomVariable;
		this.missing = missing;
		if (!this.missing) this.value = this.getNodeValueFromText(valueText);
	}

	public int getRVValue() throws RVNotInstantiatedException {
		if (this.missing) {
			throw new RVNotInstantiatedException("\nThis RV: " + this + " is not instantiated");
		}
		return this.value;
	}

	public int[] getNodeRange() {
		return this.randomVariable.getNodeRange();
	}

	public int getNodeRangePositionFromValue(int nodeValue) throws NodeValueIndexNotInNodeRangeException {
		return this.randomVariable.getNodeRangePositionFromValue(nodeValue);
	}

	public String getNodeValueText(int nodeValue) throws NodeValueIndexNotInNodeRangeException {
		return this.randomVariable.getNodeValueText(nodeValue);
	}

	public String getName() {
		return this.randomVariable.getName();
	}

	public int getNodeValueFromText(String nodeValueText) throws NodeValueTextNotInNodeRangeException {
		return this.randomVariable.getNodeValueFromText(nodeValueText);
	}
	public String toString() {
		try {
			try {
				return "Instantiated RV of RV : " + this.getName() + " Value: " + (this.missing?"missing":(""+this.getRVValue())) + 
						" = " + (this.missing?"missing":this.getNodeValueText(this.getRVValue())) + " as " + 
						(this.missing?"missing":(""+this.getNodeRangePositionFromValue(this.getRVValue()))) + "'th range element.";
			} catch (RVNotInstantiatedException e) {
				e.printStackTrace();
				return "Instantiated RV toString: internal format error";
			}
		} catch (NodeValueIndexNotInNodeRangeException e) {
			e.printStackTrace();
			return "Instantiated RV toString: internal format error";
		}
	}

	public RandomVariable getRV() {
		return this.randomVariable;
	}

//	public int getNOBSValue() {
//		return this.randomVariable.getNOBSValue();
//	}

	public int compareTo(RandomVariable arg0) {
		// TODO sync with equals contract
		return this.getRV().compareTo(arg0);
	}

	public String toStringLong() {
		return toString();
	}

	public boolean isMissingInstantiation() {
		return this.missing;
	}

	public boolean allowsOnlyOutgoingArrows() {
		return this.randomVariable.allowsOnlyOutgoingArrows();
	}

	public boolean doesNotAllowOutgoingArrows() {
		return this.randomVariable.doesNotAllowOutgoingArrows();
	}

	@Override
	public int getHierarchy() {
		return this.randomVariable.getHierarchy();
	}
	

	
}
