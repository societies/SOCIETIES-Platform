package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.jet.stat.Gamma;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.Candidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CountTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.PriorTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RVwithParents;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;
import org.societies.context.user.refinement.impl.tools.LearningPropertyLoader;

/**
 * 
 * @author robert_p
 * 
 */
public class BayesianNetworkCandidate implements Candidate {
	//TODO Check Type Match in  localFitnessCache - was <CountTable,Double> while a get with BNSegment had been used. changed to common interface RVwithParents so.
	
	private Logger log4j = LoggerFactory.getLogger(BayesianNetworkCandidate.class);

	private static final double RestartConfiguration_RemoveAllParentsProbability = LearningPropertyLoader.getRestartConfiguration_RemoveAllParentsProbability();
	private static final double RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability = LearningPropertyLoader.getRestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability();
	private static final double RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability = LearningPropertyLoader.getRestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability();
	private static int RestartConfiguration_MaxNoNodesModifiedByRestart = LearningPropertyLoader.getRestartConfiguration_MaxNoNodesModifiedByRestart(); //range: 1:(#RVs-1)

	private static final int Nothing = 0;
	private static final int AddedArc = 1;
	private static final int RemovedArc = 2;
	private static final int SwappedArc = 3;

	private static final boolean RestartConfiguration_RandomHigherNumberOfModifiedNodes = LearningPropertyLoader.getRestartConfiguration_RandomHigherNumberOfModifiedNodes();;
	private static final boolean RestartConfiguration_IncreasingNumberOfModifiedNodes = LearningPropertyLoader.getRestartConfiguration_IncreasingNumberOfModifiedNodes();
	private static final double RestartConfiguration_CacheHitRateThreshold = LearningPropertyLoader.getRestartConfiguration_CacheHitRateThreshold();;

	
	
	public double score = Double.NEGATIVE_INFINITY;
	private boolean hasScore;

	private Map<RandomVariable, BNSegment> segments;
	private Map<RandomVariable, BNSegment> unmodifiableSegments;

	// ToDo: perhaps remove the cache as an own class outside this class
	private Map<RVwithParents,Double> localFitnessCache;		
	// TODO: Cache must be only for a given BN, not just a BNSegment when doing EM!!!!
	private BayesianProbabilitiesEstimator bpc;

	private int lastOperation;



	private RandomVariable lastTargetNode;
	private RandomVariable lastSourceNode;

	private boolean foundArcs;
	private int numberOfArcs;

	private int n_equiv;


	private int maxNumberParentsPerNode;

	private int cachedHashCode;
	private boolean hasCashedHashCode;




	/**
	 * Construct a BN with no arcs, just nodes
	 * @param bpc
	 * @param maxNumberParentsPerNode
	 */
	public BayesianNetworkCandidate(BayesianProbabilitiesEstimator bpc, int maxNumberParentsPerNode) {
		super();
		// System.out.println("bpc:" + bpc);
		// System.exit(0);
		this.segments = new HashMap<RandomVariable, BNSegment>();
		this.localFitnessCache = new HashMap<RVwithParents,Double>();
		this.unmodifiableSegments = Collections.unmodifiableMap(this.segments);
		this.bpc = bpc;
		this.maxNumberParentsPerNode = maxNumberParentsPerNode;		
	}

	public Set<RandomVariable> getNodes() {
		return this.segments.keySet();
	}

	/**
	 * Construct a BN with no arcs, just nodes
	 */
	public BayesianNetworkCandidate(BayesianProbabilitiesEstimator bpc,
			Collection<RandomVariable>nodes, int maxNumberParentsPerNode) {
		this(bpc, maxNumberParentsPerNode);
		RandomVariable[] rvs_array = nodes
				.toArray(new RandomVariable[0]);
		int domain_range_counter = 0;
		for (int i = 0; i < rvs_array.length; i++) {
			Set<RandomVariable> newParents = new HashSet<RandomVariable>();
			this.segments.put(rvs_array[i], new BNSegment(rvs_array[i],
					newParents));
			domain_range_counter += rvs_array[i].getNodeRange().length;
		}

		/*
		 * Korbinian has added (double) for quotient -> otherwise it would
		 * cause problems if less than two ranges per node
		 */
		if (rvs_array.length != 0)
			this.n_equiv = (int) (0.5 + (double) domain_range_counter
					/ (2 * rvs_array.length));
		else
			this.n_equiv = 1;
	}

	public Map<RandomVariable, BNSegment> getSegments() {
		return this.unmodifiableSegments;
	}

	public double getSecondaryFitness() {
		if (this.hasScore) {
			return 1.0 / (double) this.numberOfArcs;
		} else {
			return -1;
		}
	}

	/*
	 * Computes the log fitness score of this Candidate instance. It draws upon
	 * the data stored in the BayesianProbabilitiesEstimator associated with
	 * this object. It assumes that count tables can be computed for all RV
	 * values in this BN Candidate and their parent configurations.
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.ist.daidalos.pervasive.searchLibrary.greedySearch.interfaces.Candidate
	 * #computeFitness()
	 */
	public double computeFitness() {
		
		if (this.hasScore) {
			return this.score;
		}
		if (!this.isValid()) {
			// System.err.println("Found BN with cycles: " + this.toString());
			// System.out.println("Found BN with cycles: " + this.toString());
			this.score = Double.NEGATIVE_INFINITY;
			this.hasScore = true;
			return this.score;
		}

		this.numberOfArcs = 0;
		RandomVariable[] rvs_array = this.segments.keySet()
				.toArray(new RandomVariable[0]);
		double logfitness = 0.0;
		for (int i = 0; i < rvs_array.length; i++) {
			BNSegment segment = this.segments.get(rvs_array[i]);
			SortedSet<RandomVariable> parents = segment.getOrderedParents();
			this.numberOfArcs += parents.size();
			try {
				logfitness += this
						.computeLocalFitnessLog(this.n_equiv, segment);
			} catch (ParentConfigurationNotApplicableException e) {
				System.err
						.println("In computeFitness() catch (ParentConfigurationNotApplicableException e); this should not happen.");
				e.printStackTrace();
			} catch (RangeValueNotApplicableException e) {
				System.err
						.println("In computeFitness() catch (RangeValueNotApplicableException e); this should not happen.");
				e.printStackTrace();
			}
		}
		this.score = logfitness;
		// System.out.println("Computed new Log fitness: " + logfitness);
		this.hasScore = true;
		return logfitness;
	}

	/**
	 * This method goes and gets the CountTable for the segment by using the
	 * BayesianProbabilitiesEstimator in this.
	 * 
	 * @param segment
	 * @return the CountTable for this segment
	 */
	private CountTable getCountTable(BNSegment segment) {

		RandomVariable rv = segment.getTargetRV();
		SortedSet<RandomVariable>parents = segment.getOrderedParents();

		this.bpc.setCurrentBayesianNetworkStructure(this);

		CountTable ct = this.bpc.getCounts(rv, parents);
		// System.out.println("counts: " + ct);
		if (!ct.isCounted()) {
			System.out.println("ct uncounted \n" + ct);
			System.exit(-1);
		}
		return ct;
	}

	/**
	 * @param segment
	 * @return the PriorTable for this segment
	 */
	private PriorTable getPriorTable(BNSegment segment) {

		RandomVariable rv = segment.getTargetRV();
		SortedSet<RandomVariable>parents = segment.getOrderedParents();

		PriorTable pt = this.bpc.getUniformPriors(this.n_equiv, rv, parents);
		return pt;
	}

	private double computeLocalFitness(int n_equiv, CountTable ct)
			throws ParentConfigurationNotApplicableException,
			RangeValueNotApplicableException {
		// TODO add alphas
		int ri = ct.getK_max();
		int qi = ct.getJ_max();
		double outerprod = 1.0;

		double n_equiv_double = (double) n_equiv;
		double qi_double = (double) qi;

		try {
			for (int j = 1; j <= qi; j++) {
				double n_ij_double = (double) ct.getCount(j);
				// System.out.println("n_ij: " + n_ij);
				double innerprod = 1.0;
				for (int k = 1; k <= ri; k++) {
					double n_ijk_double = (double) ct.getCount(j, k);
					innerprod *= Gamma.gamma(n_equiv_double / (ri * qi_double)
							+ n_ijk_double)
							/ Gamma.gamma(n_equiv_double / (ri * qi_double));
					if (log4j.isDebugEnabled())
						System.out.println("n_ijk: "
								+ n_ijk_double
								+ " innerprod*=: "
								+ Gamma.gamma(n_equiv_double / (ri * qi_double)
										+ n_ijk_double)
								/ Gamma
										.gamma(n_equiv_double
												/ (ri * qi_double)));
				}
				outerprod *= Gamma.gamma(n_equiv_double / qi_double)
						* innerprod
						/ Gamma.gamma(n_equiv_double / qi_double + n_ij_double);
				if (log4j.isDebugEnabled())
					System.out.println("outerprod *= "
							+ innerprod
							+ " * "
							+ Gamma.gamma(n_equiv_double / qi_double)
							+ " /: "
							+ Gamma.gamma(n_equiv_double / qi_double
									+ n_ij_double)
							+ " = "
							+ Gamma.gamma(n_equiv_double / qi_double)
							* innerprod
							/ Gamma.gamma(n_equiv_double / qi_double
									+ n_ij_double) + "  as n_ij was: "
							+ n_ij_double);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// System.out.println("Returning = " + outerprod);

//		for (double arg = 0.5; arg<70;arg+=0.5) {
		// System.out.println("Gamma(" + arg + " ) = " + Gamma.gamma(arg)) ;
		// }
		// System.out.println("n_equiv:   " + n_equiv);
		// System.exit(0);

		return outerprod;
	}

	private double computeLocalFitnessLog(int n_equiv, BNSegment segment)
			throws ParentConfigurationNotApplicableException,
			RangeValueNotApplicableException {
		// TODO add alphas

		if (this.localFitnessCache.containsKey(segment)) {
			// System.out.println("Cache hit");
			return this.localFitnessCache.get(segment).doubleValue();
		}

		CountTable ct = this.getCountTable(segment);
		PriorTable pt = this.getPriorTable(segment);

		int ri = ct.getK_max();
		int qi = ct.getJ_max();
		double outerprodlog = 0.0;

		double n_equiv_double = (double) n_equiv;
		double qi_double = (double) qi;

		try {
			for (int j = 1; j <= qi; j++) {
				double n_ij_double = (double) ct.getCount(j);
				double alpha_ij = pt.getVirtualCount(j);

				// System.out.println("n_ij: " + n_ij);
				double innerprodlog = 0.0;
				for (int k = 1; k <= ri; k++) {
					double n_ijk_double = (double) ct.getCount(j, k);
					double alpha_ijk = pt.getVirtualCount(j, k);
					// innerprodlog+=
					// Gamma.logGamma(n_equiv_double/(ri*qi_double) +
					// n_ijk_double) -
					// Gamma.logGamma(n_equiv_double/(ri*qi_double));

					innerprodlog += Gamma.logGamma(alpha_ijk + n_ijk_double)
							- Gamma.logGamma(alpha_ijk);

					// System.out.println("n_ijk: " + n_ijk_double +
					// " n_equiv_double/ri*qi_double: " +
					// n_equiv_double/ri*qi_double +
					// " first half " +
					// Math.min(300.0,
					// Math.log10(Gamma.gamma((n_equiv_double/(ri*qi_double) +
					// n_ijk_double) ) ) ) + " minus second half " +
					// Math.min(300.0,
					// Math.log10(Gamma.gamma(n_equiv_double/(ri*qi_double)) ))
					// + " = " +
					// innerprodlog);

				}
				outerprodlog += innerprodlog + Gamma.logGamma(alpha_ij)
						- Gamma.logGamma(alpha_ij + n_ij_double);

				// System.out.println("outerprodlog = " + innerprodlog +
				// " outerpart1: " +
				// Math.min(300.0,Math.log10(Gamma.gamma(n_equiv_double/qi_double)))
				// + " minus outerpart2: " +
				// Math.min(300.0,Math.log10(Gamma.gamma(n_equiv_double/qi_double+n_ij_double)))
				// + " = " +
				// (Math.min(300.0,Math.log10(Gamma.gamma(n_equiv_double/qi_double)))
				// -
				// Math.min(300.0,Math.log10(Gamma.gamma(n_equiv_double/qi_double+n_ij_double))))
				// +
				// " as n_ij_double was: " + n_ij_double);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// System.out.println("Returning = " + outerprodlog + " neq:" +
		// n_equiv_double);

		// for (double arg = 0.5; arg<200;arg+=0.5) {
		// System.out.println("Gamma(" + arg + " ) = " + Gamma.gamma(arg)) ;
		// }
		// System.out.println("n_equiv:   " + n_equiv);
		// System.exit(0);

		this.localFitnessCache.put(ct, new Double(outerprodlog));
		// System.out.println("Cache size = " + this.localFitnessCache.size() +
		// " just added: Jmax: " + ct.getJ_max() + " kmax: " + ct.getK_max());

		// Every time we recompute the fitness we reset the counter table (see
		// next method)
		// in NaiveCountTable which is expensive!! Then we call gc(). We now
		// store the fitness only
		// - see the this.localFitnessCache.put() call above.

		ct.setCounted(false);
		Runtime.getRuntime().gc();
		return outerprodlog;
	}

	// private double GammaInt(double i) {
	// if (i>=2.0) return Factorial(i-1);
	// return 1;
	// }

	public void importFrom(Candidate newCandidate) {
		if (newCandidate instanceof BayesianNetworkCandidate) {
			this.segments.clear();
			BayesianNetworkCandidate newBNCandidate = (BayesianNetworkCandidate) newCandidate;
			this.bpc = newBNCandidate.bpc;
			this.score = newBNCandidate.score;
			this.n_equiv = newBNCandidate.n_equiv;
			this.hasScore = newBNCandidate.hasScore;
			this.hasCashedHashCode = newBNCandidate.hasCashedHashCode;
			this.cachedHashCode = newBNCandidate.cachedHashCode;
			this.numberOfArcs = newBNCandidate.numberOfArcs;
			this.foundArcs = newBNCandidate.foundArcs;
			this.localFitnessCache = newBNCandidate.localFitnessCache;
			RandomVariable[] rvs_candidate_array = (RandomVariable[]) newBNCandidate.segments
					.keySet().toArray(new RandomVariable[0]);
			for (int i = 0; i < rvs_candidate_array.length; i++) {
				SortedSet<RandomVariable>candidate_ordered_parents = ((BNSegment) newBNCandidate.segments
						.get(rvs_candidate_array[i])).getOrderedParents();
				Set<RandomVariable> newParents = new HashSet<RandomVariable>(candidate_ordered_parents);
				this.segments.put(rvs_candidate_array[i], new BNSegment(
						rvs_candidate_array[i], newParents));
			}
		} else {
			throw new IllegalArgumentException(
					"\n BayesianNetworkCandidate cannot importFrom "
							+ newCandidate.getClass());
		}
	}

	public void randomRestart(int RestartConfiguration_MaxNoNodesModifiedByRestart, double RestartConfiguration_RemoveAllParentsProbability, double RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability, double RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability, long restartsAttemptedSinceLastBestFound, double averageCacheHitRate) {

		if (this.hasDirectedCycles()) {
			if (log4j.isDebugEnabled())
				log4j.debug("Exiting because this BN candidate: " + this
						+ "\n has arcs");
			if (log4j.isDebugEnabled())
				log4j.error("Exiting because this BN candidate: " + this
						+ "\n has arcs");
			throw new IllegalArgumentException("\n This BN candidate: " + this
					+ "\n has arcs");
			// System.exit(-1);
		}

		//at least 1 node has to be modified
		int nodesModifiedByRestart = 1;
		if (RestartConfiguration_RandomHigherNumberOfModifiedNodes || averageCacheHitRate>RestartConfiguration_CacheHitRateThreshold){
			nodesModifiedByRestart += (int) Math.round(Math.random()*RestartConfiguration_MaxNoNodesModifiedByRestart);
			log4j.debug(System.currentTimeMillis()+": AverageCacheHitRate="+averageCacheHitRate+", nodesModifiedByRestart="+nodesModifiedByRestart);
		}
		else if (RestartConfiguration_IncreasingNumberOfModifiedNodes){
			//10 restarts are OK should, modify only 1 node. ==> (log_e restarts)-2; log_e(10^9)=20.72326583694641; log_e(10^7)=16.11809565095832
			nodesModifiedByRestart += (int) Math.round(segments.size()*Math.pow((Math.log(restartsAttemptedSinceLastBestFound)-2)/17,2));
			if (log4j.isDebugEnabled()) log4j.debug("Restarts:\t"+restartsAttemptedSinceLastBestFound+"\tModified Nodes:\t"+nodesModifiedByRestart);
		}
		
		for (int modifiedNodes=0;modifiedNodes<nodesModifiedByRestart;modifiedNodes++){
			RandomVariable[] rvs_array = this.segments.keySet().toArray(new RandomVariable[0]);
			int numnodes = rvs_array.length;
	
	
			if (Math.random() < RestartConfiguration_RemoveAllParentsProbability) {
				
				for (int i = 0; i < numnodes; i++) {
					this.segments.get(rvs_array[i])
							.removeAllParents();
				}
				this.hasScore = false;
				this.hasCashedHashCode = false;
				if (log4j.isDebugEnabled())
					log4j.debug("Doing random restart. Removed all parents of all nodes.");
				// System.exit(0);
			}
	
	
			/* Select one node and modify it */
			int rv_index = (int)Math.round(Math.random()* (rvs_array.length - 1));// Get a random node
			SortedSet<RandomVariable> parents = this.segments
					.get(rvs_array[rv_index]).getOrderedParents(); // Get its parents
			int random_target_node1;
			int random_target_node2;
			boolean no_arcs_removed = true;
			
			if (parents.size() != 0) { // If this random node has parents
				RandomVariable[] parents_array = parents
						.toArray(new RandomVariable[0]);
				int parent_index = (int)Math.max(0,Math.round(Math.random() * parents_array.length-1)); // Get a random parent
				this.removeArc(rvs_array[rv_index], parents_array[parent_index]);
				no_arcs_removed = false;
				if (log4j.isDebugEnabled())
					log4j.debug("Doing random restart. Removed "
							+ parents_array[parent_index] + " from node "
							+ rvs_array[rv_index]);
	
			} // So far, we have removed one arc randomly from the BN
			
			
			if (rvs_array.length > 1 && (no_arcs_removed || Math.random() < RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability) && (Math.random() > RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability)) {
				boolean foundCyclesHere;
				int tries = 0;
				int maxtries = 10;
				do {
					foundCyclesHere = false;
					random_target_node1 = (int)Math.round(Math.random()* (rvs_array.length - 1));;
					random_target_node2 = Math.max(0, Math.min(
							(int) Math.round(Math.random() * (double) numnodes),
							rvs_array.length - 1));
					if (rv_index != random_target_node1) {
						this.addArc(rvs_array[rv_index], rvs_array[random_target_node1]);
						if (log4j.isDebugEnabled())
							log4j.debug("Doing random restart. Added "
									+ rvs_array[random_target_node1] + " to node "
									+ rvs_array[rv_index]);
					}
					foundCyclesHere = this.hasDirectedCycles();
	
					if (foundCyclesHere) {
						this.rollbackLastOperation();
						if (log4j.isDebugEnabled())
							log4j.debug("found cycle during restart");
					}
					if (rv_index != random_target_node2) {
						this.addArc(rvs_array[rv_index], rvs_array[random_target_node2]);
						if (log4j.isDebugEnabled())
							log4j.debug("Doing random restart. Added "
									+ rvs_array[random_target_node2] + " to node "
									+ rvs_array[rv_index]);
					}
					foundCyclesHere = this.hasDirectedCycles();
	
					if (foundCyclesHere) {
						this.rollbackLastOperation();
						if (log4j.isDebugEnabled())
							log4j.debug("found arc during restart");
					}
					tries++;
				} while (tries < maxtries
						&& (rv_index == random_target_node1 || rv_index == random_target_node2 || foundCyclesHere));
			}
			/* Finished modification of one node. */
		}
		
		if (log4j.isDebugEnabled())
			log4j.debug("Finished random restart. New score: " + this.computeFitness());


		// TODO improve all this! Add greater jumps than adding two arcs to one node and / or removing one
	}

	public String toString() {
		return " BNC " + this.segments.values();
	}

	/**
	 * Adds an arc from newParent to node
	 * with the constraint of maxNumberParents and also conditions on outgoing or incoming arcs
	 * @param node
	 * @param newParent
	 * @return
	 */
	public BayesianNetworkCandidate addArc(RandomVariable node,
			RandomVariable newParent) {
		// if (this.lastOperation==BayesianNetworkCandidate.Nothing)
		// System.out.println("");
		this.lastOperation = BayesianNetworkCandidate.Nothing;
		BNSegment affectedSegment = this.segments.get(node);

		boolean target_Fits_Conditions_To_Add_Arc = true;
		boolean new_Parent_Fits_Conditions_To_Add_Arc = true;
		boolean target_node_has_sufficient_parent_capacity = true;
		boolean hierarchy_Relationship_Fits_Conditions_To_Add_Arc = true;

		// ADDED By Maria
		if (newParent.doesNotAllowOutgoingArrows())
			new_Parent_Fits_Conditions_To_Add_Arc = false;

		if (node.allowsOnlyOutgoingArrows())
			target_Fits_Conditions_To_Add_Arc = false;

		if (newParent.getHierarchy() > node.getHierarchy())
			hierarchy_Relationship_Fits_Conditions_To_Add_Arc = false;
		
		// Added by Maria, Korbinian and Patrick, Nov. 2011
		if (this.segments.get(node).getOrderedParents().size() >= this.maxNumberParentsPerNode) {
			target_node_has_sufficient_parent_capacity = false;
		}
			
		
		boolean is_Possible_To_Add_Arc = (new_Parent_Fits_Conditions_To_Add_Arc)
		&& (target_Fits_Conditions_To_Add_Arc) && (target_node_has_sufficient_parent_capacity) && 
		(hierarchy_Relationship_Fits_Conditions_To_Add_Arc);
	

		// System.out.println("Testing : \n" + this + "\n " +
		// affectedSegment.getOrderedParents());
		// if (! ( newParent.equals(affectedSegment.getTargetRV()) ||
		// affectedSegment.getOrderedParents().contains(newParent) )){
		if ((is_Possible_To_Add_Arc)
				&& (!(newParent.equals(affectedSegment.getTargetRV()) || affectedSegment
						.getOrderedParents().contains(newParent)))) {
			// END ADDED & MODIFIED By Maria
			this.score = Double.NEGATIVE_INFINITY;
			this.hasScore = false;
			this.hasCashedHashCode = false;
			affectedSegment.addParent(newParent);
			this.lastTargetNode = affectedSegment.getTargetRV();
			this.lastSourceNode = newParent;
			this.lastOperation = BayesianNetworkCandidate.AddedArc;
			// System.out.println("Added Arc from " +
			// this.lastSourceNode.getName() + " to " +
			// this.lastTargetNode.getName());
		} else {
			// System.out.println("Did not add Arc from " + newParent.getName()
			// + " to " + affectedSegment.getTargetRV().getName());
		}
		return this;
	}

	/**
	 * Removes an arc from parent to node
	 * 
	 * @param node
	 * @param parent
	 * @return
	 */
	public BayesianNetworkCandidate removeArc(RandomVariable node,
			RandomVariable parent) {
		// if (this.lastOperation==BayesianNetworkCandidate.Nothing)
		// System.out.println("");
		this.score = Double.NEGATIVE_INFINITY;
		this.hasScore = false;
		this.hasCashedHashCode = false;
		BNSegment affectedSegment = this.segments.get(node);
		// System.out.println("Removing Arc from " + parent.getName() + " to " +
		// affectedSegment.getTargetRV().getName());
		affectedSegment.removeParent(parent);
		this.lastTargetNode = affectedSegment.getTargetRV();
		this.lastSourceNode = parent;
		this.lastOperation = BayesianNetworkCandidate.RemovedArc;
		return this;
	}

	public BayesianNetworkCandidate swapArc(RandomVariable node,
			RandomVariable parent) {
		// if (this.lastOperation==BayesianNetworkCandidate.Nothing)
		// System.out.println("");

		boolean target_Fits_Conditions_To_Swap_Arc = true;
		boolean parent_Fits_Conditions_To_Swap_Arc = true;
		boolean hierarchy_Relationship_Fits_Conditions_To_Add_Arc = true;

		boolean new_baby_node_has_sufficient_parent_capacity = true;
		
		// ADDED By Maria
		if (parent.allowsOnlyOutgoingArrows())
			parent_Fits_Conditions_To_Swap_Arc = false;

		if (node.doesNotAllowOutgoingArrows())
			target_Fits_Conditions_To_Swap_Arc = false;
		
		if (parent.getHierarchy() < node.getHierarchy())
			hierarchy_Relationship_Fits_Conditions_To_Add_Arc = false;


		// Added by Maria, Korbinian and Patrick, Nov. 2011
		if (this.segments.get(parent).getOrderedParents().size() >= this.maxNumberParentsPerNode) {
			new_baby_node_has_sufficient_parent_capacity = false;
		}		
		
		boolean is_Possible_To_Swap_Arc = (parent_Fits_Conditions_To_Swap_Arc)
				&& (target_Fits_Conditions_To_Swap_Arc) && (new_baby_node_has_sufficient_parent_capacity) 
				&& (hierarchy_Relationship_Fits_Conditions_To_Add_Arc);

		if (is_Possible_To_Swap_Arc) {
			// END ADDED & MODIFIED By Maria
			this.score = Double.NEGATIVE_INFINITY;
			this.hasScore = false;
			this.hasCashedHashCode = false;
			BNSegment affectedSegmentOldRoot = this.segments
					.get(node);
			affectedSegmentOldRoot.removeParent(parent);
			BNSegment affectedSegmentNewRoot = this.segments
					.get(parent);
			affectedSegmentNewRoot.addParent(node);
			this.lastTargetNode = node;
			this.lastSourceNode = parent;
			this.lastOperation = BayesianNetworkCandidate.SwappedArc;
			// System.out.println("Swapping Arc from " +
			// this.lastSourceNode.getName() + " to " +
			// this.lastTargetNode.getName());
		} else {
			// System.out.println("Did not swap Arc from " + parent.getName() +
			// " to " + node.getName());
		}
		return this;
	}

	public boolean isValid() {
		return !this.hasDirectedCycles();
	}

	private boolean hasDirectedCycles() {
		boolean hadMoreMessages = false;
		this.foundArcs = false;
		this.resetAllMessages();
		// System.out.println("Testing for cycles: " + this.toString());
		do {
			hadMoreMessages = this.sendMessages();
			// System.out.println(" send Messages upper: " + hadMoreMessages +
			// " arcs: " + this.foundArcs);

		} while (hadMoreMessages && !this.foundArcs);
		// System.out.println("Tested for cycles. Found: " + this.foundArcs);
		return this.foundArcs;
	}

	/**
	 * @return true if there are more messages to send
	 */
	private boolean sendMessages() {
		boolean sentAnyMessages = false;
		RandomVariable[] rvs_array = this.segments.keySet()
				.toArray(new RandomVariable[0]);
		for (int i = 0; i < rvs_array.length; i++) {
			BNSegment bns = this.segments.get(rvs_array[i]);
			bns.moveFromInBoxToOutbox();
		}

		for (int i = 0; i < rvs_array.length; i++) {
			BNSegment bns = this.segments.get(rvs_array[i]);
			if (bns.propagateAllMessages(rvs_array.length, this)) {
				sentAnyMessages = true;
			}
			if (bns.hasFoundArc()) {
				this.foundArcs = true;
				return false;
			}
		}
		// System.out.println(" send Messages: " + sentAnyMessages + " arcs: " +
		// this.foundArcs);
		return sentAnyMessages;
	}

	BNSegment getBNSegment(RandomVariable rv) {
		return this.unmodifiableSegments.get(rv);
	}

	/**
	 * 
	 */
	private void resetAllMessages() {
		RandomVariable[] rvs_array = this.segments.keySet()
				.toArray(new RandomVariable[0]);
		for (int i = 0; i < rvs_array.length; i++) {
			BNSegment bns = this.segments.get(rvs_array[i]);
			bns.prepareInitialMessages();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.ist.daidalos.pervasive.searchLibrary.greedySearch.interfaces.Candidate
	 * #rollbackLastOperation()
	 */
	public void rollbackLastOperation() {
		// System.out.println("Rolling back from last: " + this.lastOperation);
		switch (this.lastOperation) {
		case BayesianNetworkCandidate.AddedArc: {
			this.removeArc(this.lastTargetNode, this.lastSourceNode);
			this.lastOperation = BayesianNetworkCandidate.Nothing;
			break;
		}
		case BayesianNetworkCandidate.RemovedArc: {
			this.addArc(this.lastTargetNode, this.lastSourceNode);
			this.lastOperation = BayesianNetworkCandidate.Nothing;
			break;
		}
		case BayesianNetworkCandidate.SwappedArc: {
			this.addArc(this.lastTargetNode, this.lastSourceNode);
			this.removeArc(this.lastSourceNode, this.lastTargetNode);
			this.lastOperation = BayesianNetworkCandidate.Nothing;
			break;
		}
		default: {
			break;
		}
		}
	}

	public boolean equals(Object o) {
		if (o instanceof BayesianNetworkCandidate) {
			BayesianNetworkCandidate obnc = (BayesianNetworkCandidate) o;
			return this.segments.equals(obnc.segments);
		}
		return false;
	}

	public int hashCode() {
		if (!this.hasCashedHashCode) this.calculateHash();
		return this.cachedHashCode;
	}

	private final void calculateHash() {
		int hc = 0;
		Iterator<RandomVariable> it = this.segments.keySet().iterator();
		while (it.hasNext()) {
			hc += this.segments.get(it.next()).hashCode();
		}
		// System.err.println("hc: " + hc + " other hc: " +
		// this.segments.hashCode());
		this.hasCashedHashCode = true;
		this.cachedHashCode = hc;
	}

	public Candidate cloneCandidate() {
		BayesianNetworkCandidate ret = new BayesianNetworkCandidate(this.bpc, this.maxNumberParentsPerNode);
		ret.importFrom(this);
		return ret;
	}

	public int getN_equiv() {
		return this.n_equiv;

	}

	@Override
	public int candidateSize() {
		return getNodes().size();
	}

	@Override
	public void randomRestart() {
		randomRestart(RestartConfiguration_MaxNoNodesModifiedByRestart, 
				RestartConfiguration_RemoveAllParentsProbability, 
				RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability, 
				RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability,-1,0.0);
	}

	@Override
	public void randomRestart(long numberOfcandidatesSearchedSinceLastBestFound, double averageCacheHitRate) {
		randomRestart(RestartConfiguration_MaxNoNodesModifiedByRestart, 
				RestartConfiguration_RemoveAllParentsProbability, 
				RestartConfiguration_AddNewArcsAlthoughArcsHaveBeenRemovedProbability, 
				RestartConfiguration_DontAddNewArcsWhenNoArcsAreRemovedProbability,numberOfcandidatesSearchedSinceLastBestFound,averageCacheHitRate);		
	}

}
