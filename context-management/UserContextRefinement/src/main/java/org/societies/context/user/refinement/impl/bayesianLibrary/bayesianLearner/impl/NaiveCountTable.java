
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentsNotContainedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CountTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.InstantiatedRV;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;


public class NaiveCountTable extends AbstractRVwithParents implements CountTable,Serializable{

	/**
	 * The actual store.
	 * Uses indices k,j and counting from from 1 ...
	 */
	protected int [][] countTableMatrixijk;
	protected int [] countTableMatrixij;

	private boolean counted;

	public NaiveCountTable(RandomVariable targetRV, Collection<RandomVariable> parents_of_targetRV) {
		super(targetRV, parents_of_targetRV);
		this.counted = false;
//		System.out.println("Constructing new count table for  " + targetRV + " ... ");
		this.allocateMemory();
//		System.out.println(" ... made new count table for with j_max:" + this.j_max + " and k_max:" + this.k_max);
	}


	/**
	 * 
	 */
	private void allocateMemory() {
		if (this.countTableMatrixijk == null) {
			if ((long) (this.getK_max()+1) * (long) (this.getJ_max()+1) >800000000L) {
				System.out.flush();
				throw new RuntimeException("\nTrying to allocate too much space for CountTable: " + this);
			}

			try{
				this.countTableMatrixijk = new int[this.getK_max()+1][this.getJ_max()+1];
			}
			catch (OutOfMemoryError e){
				System.out.println("this table size of countTableMatrixijk caused the exception: "+(this.getK_max()+1)*(this.getJ_max()+1));
				e.printStackTrace();
				throw new RuntimeException("\nOut of memory when reserving space for countTableMatrixijk: " + this);
			}
		}
		if (this.countTableMatrixij == null) 
			try{
				this.countTableMatrixij = new int[this.getJ_max()+1];
			}
		catch (OutOfMemoryError e){
			System.out.println("this table size of countTableMatrixij caused the exception: "+(this.getJ_max()+1));
			e.printStackTrace();
			throw new RuntimeException("\nOut of memory when reserving space for countTableMatrixij: " + this);
		}
	}


	public int getCount(int parent_configuration_j, int node_i_range_value_index_k) 
	throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException {
		this.checkIndexRangesErrorHandling(parent_configuration_j, node_i_range_value_index_k);
		return this.countTableMatrixijk[node_i_range_value_index_k][parent_configuration_j];
	}

	public int getCount(int parent_configuration_j) 
	throws ParentConfigurationNotApplicableException {
		try {
			this.checkIndexRangesErrorHandling(parent_configuration_j, -1);
		} catch (RangeValueNotApplicableException e) {
			System.err.println("In getCount(parent_configuration_j) catch (ParentConfigurationNotApplicableException e); this should not happen.");
			e.printStackTrace();
		}
		return this.countTableMatrixij[parent_configuration_j];
	}	



	public int getCount(Set<InstantiatedRV>parents, int node_i_range_value)
					throws NodeValueIndexNotInNodeRangeException,ParentsNotContainedException, RVNotInstantiatedException {
		try {
			return this.getCount(this.computeParentConfiguration(parents, false), 
					this.getTargetRV().getNodeRangePositionFromValue(node_i_range_value)+1);
		} catch (ParentConfigurationNotApplicableException e) {
			System.err.println("In getCount(parents, node_i_range_value) catch (ParentConfigurationNotApplicableException e); this should not happen.");
			e.printStackTrace();
			return 0;
		} catch (RangeValueNotApplicableException e) {
			e.printStackTrace();
			throw new NodeValueIndexNotInNodeRangeException(e);
		}  
	}	

	public int getCount(Set<InstantiatedRV> parents) throws ParentsNotContainedException {
		try {
			return this.getCount(this.computeParentConfiguration(parents, false));
		} catch (ParentConfigurationNotApplicableException e) {
			System.err.println("In getCount(parents) catch (ParentConfigurationNotApplicableException e); this should not happen.");
			e.printStackTrace();
			throw new ParentsNotContainedException(e);
		}   
	}		

	public void incrementCount(int parent_configuration_j, int node_i_range_value_index_k) 
	throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException {
		this.allocateMemory();
		this.checkIndexRangesErrorHandling(parent_configuration_j, node_i_range_value_index_k);
		(this.countTableMatrixijk[node_i_range_value_index_k][parent_configuration_j])++;
		(this.countTableMatrixij[parent_configuration_j])++;
	}

	public void setCount(int countValue, int parent_configuration_j, int node_i_range_value_index_k) 
	throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException {
		this.allocateMemory();
		this.checkIndexRangesErrorHandling(parent_configuration_j, node_i_range_value_index_k);
		this.countTableMatrixijk[node_i_range_value_index_k][parent_configuration_j] = countValue;
		this.recomputeIJCount(parent_configuration_j);
	}

	private void recomputeIJCount(int parent_configuration_j) {
		int[] targetRVRange = this.getTargetRV().getNodeRange();
		int countNij = 0;
		for (int i = 0; i < targetRVRange.length; i++) {
			try {
				int k = 1 + this.getTargetRV().getNodeRangePositionFromValue(targetRVRange[i]);
				countNij += this.countTableMatrixijk[k][parent_configuration_j];
			} catch (NodeValueIndexNotInNodeRangeException e) {
				e.printStackTrace();
			}
		}
		this.countTableMatrixij[parent_configuration_j] = countNij;
	}		

	private void checkIndexRangesErrorHandling(int parent_configuration_j, int node_i_range_value_index_k) 
	throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException{
		if (node_i_range_value_index_k>=0) {
			if (node_i_range_value_index_k>=this.countTableMatrixijk.length) {
				System.err.println("Error: node_i_range_value_k: " + node_i_range_value_index_k + " >= " + this.countTableMatrixijk.length);
				throw new RangeValueNotApplicableException("\nError: node_i_range_value_k: " + 
						node_i_range_value_index_k + " >= " + this.countTableMatrixijk.length);
			}
			if (parent_configuration_j >= this.countTableMatrixijk[node_i_range_value_index_k].length) {
				System.err.println("Error: parent_configuration_j: " + parent_configuration_j + " >= " + this.countTableMatrixijk[node_i_range_value_index_k].length);
				throw new ParentConfigurationNotApplicableException("\nError: parent_configuration_j: " 
						+ parent_configuration_j + " >= " + this.countTableMatrixijk[node_i_range_value_index_k].length);
			}
		}
		else if (parent_configuration_j >= this.countTableMatrixij.length) {
			System.err.println("Error: parent_configuration_j: " + parent_configuration_j + " >= " + this.countTableMatrixijk[node_i_range_value_index_k].length);
			throw new ParentConfigurationNotApplicableException("\nError: parent_configuration_j: " 
					+ parent_configuration_j + " >= " + this.countTableMatrixijk[node_i_range_value_index_k].length);
		}

	}




	public String toString() {
		StringBuffer ob = new StringBuffer();
		ob.append("CountTable for ");
		ob.append(this.getTargetRV());
		ob.append(". With these parents: \n");

		RandomVariable[] parents_array = (RandomVariable[]) this.getOrderedParents().toArray(new RandomVariable[0]);

		for (int p=0;p<this.getOrderedParents().size();p++) {
			ob.append(parents_array[p]+" \n");
		}
		ob.append(" \nCounts:");
		if (this.countTableMatrixijk != null) {
			for (int k=1;k<this.countTableMatrixijk.length;k++) {  		
				ob.append("\nk: " + k+"||");
				for (int j=1;j<this.countTableMatrixijk[k].length;j++) {
					try {
						ob.append("j:"+j+"\\c="+(this.getCount(j,k))+"|");
					} catch (ParentConfigurationNotApplicableException e) {
						e.printStackTrace();
					} catch (RangeValueNotApplicableException e) {
						e.printStackTrace();
					}
					catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
						return ob.toString();
					}
				}
				try {
					ob.append("|" + this.getTargetRV().getNodeValueText(this.getTargetRV().getNodeRange()[k-1]));
				} catch (NodeValueIndexNotInNodeRangeException e) {
					e.printStackTrace();
				}
			}    	
		}
		ob.append("\n-----------------------------------------------------");
		return ob.toString();
	}

	public void addParent(RandomVariable parent) {
		throw new UnsupportedOperationException("\n CountTable does not support adding parents");
	}

	public void removeParent(RandomVariable parent) {
		throw new UnsupportedOperationException("\n CountTable does not support removing parents");
	}

	public void removeAllParents() {
		throw new UnsupportedOperationException("\n CountTable does not support removing all parents");
	}   

	public boolean isCounted() {
		return this.counted;
	}


	public void setCounted(boolean counted) {
		this.counted=counted;
		if (!this.counted) {
			this.countTableMatrixij = null;
			this.countTableMatrixijk = null;
		}
	}
}
