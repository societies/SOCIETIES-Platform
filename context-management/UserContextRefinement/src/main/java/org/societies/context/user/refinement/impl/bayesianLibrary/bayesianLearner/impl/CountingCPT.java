
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.util.Set;
import java.util.SortedSet;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentsNotContainedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.PriorAndCountTablesMismatchException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.ConditionalProbabilityTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CountTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.InstantiatedRV;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.JointMeasurement;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.PriorTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;


public class CountingCPT implements ConditionalProbabilityTable{

	private CountTable ct;
	private PriorTable pt;
	
	public CountingCPT(CountTable ct, PriorTable pt){
		this.ct = ct;
		this.pt = pt;
	}

	

	private void checkCompatibilityCTandPT() throws PriorAndCountTablesMismatchException {
		if (this.ct==null) {
			throw new NullPointerException("\nCountTable is null");
		}
		if (this.pt==null) {
			throw new NullPointerException("\nPriorTable is null");
		}		
		if (!this.ct.getTargetRV().equals(this.pt.getTargetRV())) {
			System.err.println("\nct: " + this.ct.getTargetRV().getName() + " pt: " + this.pt.getTargetRV().getName());
			throw new PriorAndCountTablesMismatchException("\nTarget node of CountTable " + 
					this.ct.getTargetRV() + " does not match Target node of Prior Table " + this.pt.getTargetRV());
		}
		if (!this.ct.getOrderedParents().equals(this.pt.getOrderedParents())) {
			System.err.println("\nct: " + this.ct.getOrderedParents() + " pt: " + this.pt.getOrderedParents());
			throw new PriorAndCountTablesMismatchException("\nOrdered Parents of CountTable " + 
					this.ct.getOrderedParents() + " does not match Ordered Parents of Prior Table " + this.pt.getOrderedParents());			
		}
	}

	public double[][] getProbabilityTable() throws PriorAndCountTablesMismatchException {
		this.checkCompatibilityCTandPT();
		double[][] cpTable = new double[this.ct.getK_max()+1][this.ct.getJ_max()+1];
		this.fillTable(cpTable);
		return cpTable;
	}
	
	private void fillTable(double[][] cpTable) {
		for (int k=1;k<=this.ct.getK_max();k++) {
			for (int j=1;j<=this.ct.getJ_max();j++) {
				try {
					cpTable[k][j] = this.computeTableValue(j, k);
				} catch (ParentConfigurationNotApplicableException e) {
					System.err.println("In fillTables() catch (ParentConfigurationNotApplicableException e); this should not happen.");
					e.printStackTrace();
				} catch (RangeValueNotApplicableException e) {
					System.err.println("In fillTables() catch (RangeValueNotApplicableException e); this should not happen.");
					e.printStackTrace();
				}
			}
		}
	}

	public double getProbability(int parentConfiguration, int node_i_range_value_index_k) 
			throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException, PriorAndCountTablesMismatchException {
		this.checkCompatibilityCTandPT();
		return computeTableValue(parentConfiguration, node_i_range_value_index_k);
	}
 


	private double computeTableValue(int parentConfiguration, int node_i_range_value_index_k) 
			throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException {
		int n_ijk = this.ct.getCount(parentConfiguration, node_i_range_value_index_k);
		int n_ij = this.ct.getCount(parentConfiguration);
		double alpha_ijk = this.pt.getVirtualCount(parentConfiguration, node_i_range_value_index_k);
		double alpha_ij = this.pt.getVirtualCount(parentConfiguration);
		return (alpha_ijk + (double) n_ijk)/(alpha_ij+ (double)n_ij);
	}

	public double getProbability(Set<InstantiatedRV> parents, int node_i_range_value) 
				throws NodeValueIndexNotInNodeRangeException, ParentsNotContainedException, 
				PriorAndCountTablesMismatchException, RVNotInstantiatedException {
		try {
			return this.getProbability(this.computeParentConfiguration(parents, false), 
						this.getTargetRV().getNodeRangePositionFromValue(node_i_range_value)+1);
		} catch (ParentConfigurationNotApplicableException e) {
			System.err.println("In getProbability() catch (ParentConfigurationNotApplicableException e); this should not happen.");
			e.printStackTrace();
			return 0.0;
		} catch (RangeValueNotApplicableException e) {
			e.printStackTrace();
			throw new NodeValueIndexNotInNodeRangeException(e);
		} 
	}	
	
	public boolean containsExactlyAllParents(Set<RandomVariable> test_set) {
		return this.ct.containsExactlyAllParents(test_set);
	}

	public RandomVariable getTargetRV() {
		return this.ct.getTargetRV();
	}

	public int computeParentConfiguration(JointMeasurement instantiatedMeasurements, boolean replaceMissingParentsWithNOOBs) throws ParentsNotContainedException {
		return this.ct.computeParentConfiguration(instantiatedMeasurements, replaceMissingParentsWithNOOBs);
	}

	public int computeParentConfiguration(Set<InstantiatedRV> instantiatedMeasurements, boolean replaceMissingParentsWithNOOBs)
						throws ParentsNotContainedException {
		return this.ct.computeParentConfiguration(instantiatedMeasurements, replaceMissingParentsWithNOOBs);
	}

	public SortedSet<RandomVariable> getOrderedParents() {
		return this.ct.getOrderedParents();
	}



	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#getK_max()
	 */
	public int getK_max() {
		return this.ct.getK_max();
	}



	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#getJ_max()
	 */
	public int getJ_max() {
		return this.ct.getJ_max();
	}

	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#addParent(de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RandomVariable)
	 */
	public void addParent(RandomVariable parent) {
		this.ct.addParent(parent);
	}


	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#addParent(de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RandomVariable)
	 */
	public void removeParent(RandomVariable parent) {
		this.ct.removeParent(parent);
	}

	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#addParent(de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RandomVariable)
	 */
	public void removeAllParents() {
		this.ct.removeAllParents();
	}
	
	public String toString() {
    	StringBuffer ob = new StringBuffer();
    	ob.append("Probability Table for ");
    	ob.append(this.getTargetRV());
    	ob.append(". With these parents: \n");
    	RandomVariable[] parents_array = (RandomVariable[]) this.getOrderedParents().toArray(new RandomVariable[0]);
    	
    	for (int p=0;p<this.getOrderedParents().size();p++) {
    		ob.append(parents_array[p]+" \n");
    	}
		
		try {
			this.checkCompatibilityCTandPT();
		} catch (PriorAndCountTablesMismatchException e1) {
			e1.printStackTrace();
			ob.append("error: " + e1.getMessage());
			return ob.toString();
		}
		
		
    	ob.append(" \nProbabilities (" + this.ct.getJ_max() + "):");
		for (int k=1;k<=this.ct.getK_max();k++) {
			ob.append("\nk: " + k+"||");
			for (int j=1;j<=this.ct.getJ_max();j++) {
				try {
					double prob = this.computeTableValue(j, k);
					ob.append("j:"+j+"\\c="+(prob)+"|");
					
				} catch (ParentConfigurationNotApplicableException e) {
					System.err.println("In fillTables() catch (ParentConfigurationNotApplicableException e); this should not happen.");
					e.printStackTrace();
				} catch (RangeValueNotApplicableException e) {
					System.err.println("In fillTables() catch (RangeValueNotApplicableException e); this should not happen.");
					e.printStackTrace();
				}
			}
			try {
				ob.append("|" + this.getTargetRV().getNodeValueText(this.getTargetRV().getNodeRange()[k-1]));
			} catch (NodeValueIndexNotInNodeRangeException e) {
				e.printStackTrace();
			}
		}
	   	ob.append("\n-----------------------------------------------------");
		return ob.toString();
	}	
	
}
