package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.CountsNotCompleteException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeNotAvailableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentsNotContainedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.ConditionalProbabilityTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CountTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.InstantiatedRV;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.JointMeasurement;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.PriorTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;

public class SimpleCountingLearningEngine implements BayesianProbabilitiesEstimator,Serializable{

	private static final long serialVersionUID = -6170201680160868746L;

	protected Map<RandomVariable,Map<SortedSet<RandomVariable>,CountTable>> countTablesUnderLearning;
	
	private Vector<JointMeasurement> measurements;
	
	public SimpleCountingLearningEngine() {
		this.countTablesUnderLearning = new HashMap<RandomVariable,Map<SortedSet<RandomVariable>,CountTable>>();
		this.measurements = new Vector<JointMeasurement>();
	}


	public void addMeasurement(JointMeasurement meas) {
		this.measurements.add(meas);
	}

	
	public void refreshAllLearningTables() throws NodeNotAvailableException{
		RandomVariable[] targetNodes = this.countTablesUnderLearning.keySet().toArray(new RandomVariable[0]);
		for (int i=0;i<targetNodes.length;i++) {
//			System.out.println("Learning for RV : " + targetNodes[i]);
			Map<SortedSet<RandomVariable>,CountTable> parentsToCountTableMap = (this.countTablesUnderLearning.get(targetNodes[i]));
			SortedSet<RandomVariable>[] parentsArray = parentsToCountTableMap.keySet().toArray(new TreeSet/*<RandomVariable>*/[0]);
			
			for (int j=0;j<parentsArray.length;j++) {
				this.computeCountTable(targetNodes[i], parentsArray[j]);
			}
		}
	}	
	
	/**
	 * Computes the specified currently requested count table for targetNode and parents, based on the measurements.
	 * @param targetNode
	 * @param parents
	 * @throws NodeNotAvailableException
	 */
	private void computeCountTable(RandomVariable targetNode, SortedSet<RandomVariable>parents) throws NodeNotAvailableException {
		CountTable target_ct = (CountTable) (this.countTablesUnderLearning.get(targetNode)).get(parents);
		Enumeration<JointMeasurement> enumer = this.measurements.elements();
		while (enumer.hasMoreElements()) {
			JointMeasurement jm = enumer.nextElement();
			// System.out.println("... using measurement : " + jm);
			InstantiatedRV rvnvp_target = (InstantiatedRV) jm.getInstantiatedRV().get(targetNode);
			if (rvnvp_target == null) {
				throw new NodeNotAvailableException("\nNode: " + targetNode + " not in JointMeasurement: " + jm);
			}
			int targetNode_value_target;
			if (!rvnvp_target.isMissingInstantiation()) { 
				int targetNode_value_index = 0;
				try {
					targetNode_value_target = rvnvp_target.getRVValue();
					targetNode_value_index = targetNode.getNodeRangePositionFromValue(targetNode_value_target);
				} catch (RVNotInstantiatedException e1) {
					System.err.println("In computeCountTable() catch (RVNotInstantiatedException e); this should not happen.");
					e1.printStackTrace();
				} catch (NodeValueIndexNotInNodeRangeException e) {
					System.err.println("In computeCountTable() catch (NodeValueIndexNotInNodeRangeException e); this should not happen.");						
					e.printStackTrace();
				}
				
				try {
					int parentConfiguration = target_ct.computeParentConfiguration(jm, true);
					if (parentConfiguration>=0) {
						target_ct.incrementCount(parentConfiguration, targetNode_value_index + 1);
					}
					else {
						System.err.println("In computeCountTables(): found a missing RV value for some parent of RV: " + targetNode);
					}
				} catch (ParentConfigurationNotApplicableException e) {
					e.printStackTrace();
				} catch (RangeValueNotApplicableException e) {
					e.printStackTrace();
				} catch (ParentsNotContainedException e) { // actually, as long as
					// argument "true" in
					// computeParentConfiguration() above,
					// this will not occur!
					System.err.println("In computeCountTables(), catch (ParentsNotContainedException e): This should not happen");
					e.printStackTrace();
				}
			}
		}
		target_ct.setCounted(true);
	}


	/**
	 * Adds any new internal nodes to the passed countTable needed to incorporate the current Learning request.
	 * Here, this means checking that the CountTables hold all nodes that are
	 * in the parents of node i or the node i itself.
	 * @param node_i
	 * @param parents_of_node_i
	 */
	private void extendTables(RandomVariable node_i, SortedSet<RandomVariable> parents_of_node_i) {
		Map<RandomVariable,Map<SortedSet<RandomVariable>,CountTable>> countTableMapMap;
		
		countTableMapMap = this.countTablesUnderLearning;
		
		Map<SortedSet<RandomVariable>,CountTable> ct_mapSortedSet_node_i = null;
		if (!countTableMapMap.containsKey(node_i)) {
			ct_mapSortedSet_node_i = new HashMap<SortedSet<RandomVariable>,CountTable>();
			countTableMapMap.put(node_i, ct_mapSortedSet_node_i);
		} else {
			ct_mapSortedSet_node_i = countTableMapMap.get(node_i);
		}
		this.updateParentMembership(node_i, ct_mapSortedSet_node_i, parents_of_node_i);
	}

	/** 
	 * Make sure that the Map of SortedSet of RandomVariable to CountTable, ct_mapSortedSet_node_i, 
	 * contains a SortedSet of RandomVariables with exactly all parents_of_node_i.
	 * If not, then make a new counting table and store it in ct_mapSortedSet_node_i.
	 * @param node_i
	 * @param ct_mapSortedSet_node_i
	 * @param parents_of_node_i
	 */
	private void updateParentMembership(RandomVariable node_i, 
			     Map<SortedSet<RandomVariable>,CountTable> ct_mapSortedSet_node_i, SortedSet<RandomVariable> parents_of_node_i) {
	//	System.out.println("ZZZ updateParentMembership for " +  node_i + ". Parents: " + parents_of_node_i + " ct_mapSortedSet_node_i " + ct_mapSortedSet_node_i);

		if (ct_mapSortedSet_node_i.containsKey(parents_of_node_i)) return;
		CountTable newcountTable = null;
		newcountTable = createNewCountingTable(node_i, parents_of_node_i);
		ct_mapSortedSet_node_i.put(new TreeSet<RandomVariable>(parents_of_node_i), newcountTable);
	}

	/**
	 * @param node_i
	 * @param parents_of_node_i
	 * @return
	 */
	private CountTable createNewCountingTable(RandomVariable node_i, SortedSet<RandomVariable> parents_of_node_i) {
		CountTable ct_node_i = new NaiveCountTable(node_i, parents_of_node_i);
		return ct_node_i;
	}

	
	/**
	 * @param node_i
	 * @param parents_of_node_i
	 * @return
	 */
	private PriorTable createNewPriorTable(int n_equiv, RandomVariable node_i, SortedSet<RandomVariable> parents_of_node_i) {
		PriorTable pt_node_i = new NaivePriorTable(n_equiv, node_i, parents_of_node_i);
		return pt_node_i;
	}

	
	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#getCounts(eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.RandomVariable, java.util.SortedSet)
	 */
	public CountTable getCounts(RandomVariable targetNode, SortedSet<RandomVariable> parentNodes) {

		this.extendTables(targetNode, parentNodes);

		Map<SortedSet<RandomVariable>,CountTable> ct_mapSortedSet_node_i = this.countTablesUnderLearning.get(targetNode);
		
		if (!(ct_mapSortedSet_node_i.get(parentNodes)).isCounted()) {	
			try {
				this.computeCountTable(targetNode, parentNodes);
			} catch (NodeNotAvailableException e) {
				e.printStackTrace();
			}
		}
		return ct_mapSortedSet_node_i.get(parentNodes);
	}
	

	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#getCPT(eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.RandomVariable, java.util.SortedSet)
	 */
	public ConditionalProbabilityTable getCPT(RandomVariable targetNode, SortedSet<RandomVariable> parentNodes, PriorTable alphas) 
					throws CountsNotCompleteException{
		
		
		ConditionalProbabilityTable cpt = this.computeCPT(targetNode, parentNodes, 
				this.getCounts(targetNode, parentNodes), alphas);
		return cpt;
	}
	
	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#computeCPT(eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.RandomVariable, java.util.SortedSet, eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.CountTable, eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.PriorTable)
	 */
	public ConditionalProbabilityTable computeCPT(RandomVariable targetNode, SortedSet<RandomVariable> parentNodes, 
			CountTable counts, PriorTable alphas) throws CountsNotCompleteException{
		
		ConditionalProbabilityTable cpt = new CountingCPT(this.getCounts(targetNode, parentNodes), alphas);
	
		return cpt;
	}


	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#resetTables()
	 */
	public void resetTables() {
		this.countTablesUnderLearning.clear();
		// TODO: call any registered consumers that want to listen to this event. Eg cache in BNCandidate local fitness
	}


	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#clearMeasurements()
	 */
	public void clearMeasurements() {
		this.measurements.clear();
	}

	@Override
	public PriorTable getUniformPriors(int n_equiv, RandomVariable rv, SortedSet<RandomVariable> parents_of_node_i) {
		return this.createNewPriorTable(n_equiv, rv, parents_of_node_i);
	}

	public String toString() {
		return ("Simple Counting LE " + this.measurements.size());
	}

	public void setCurrentBayesianNetworkStructure(BayesianNetworkCandidate bnc) {
		// Not needed here!
	}

}
