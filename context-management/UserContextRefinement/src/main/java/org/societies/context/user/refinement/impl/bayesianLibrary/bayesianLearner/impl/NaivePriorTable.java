package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.util.Set;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.PriorTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;


public class NaivePriorTable extends AbstractRVwithParents implements PriorTable{

	private double n_equiv;
	
	public NaivePriorTable(int n_equiv, RandomVariable targetRV, Set<RandomVariable> parents_of_targetRV) {
		super(targetRV, parents_of_targetRV);
		
		this.n_equiv = (double) n_equiv;
		
		
		
		
	}

	public double getVirtualCount(int parent_configuration_j, int node_i_range_value_index_k) throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException {
		return this.n_equiv/((double) (this.getK_max()* (double) this.getJ_max()));
	}

	public double getVirtualCount(int parent_configuration_j) throws ParentConfigurationNotApplicableException {
		return this.n_equiv/ (double) this.getJ_max();
	}



	
}
