package org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Clique;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Separator;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.UndirectedEdge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;

/**
 * @author fran_ko
 * 
 *         Known Issues: - remove evidence before trying to introduce different
 *         hard evidence for the same node - Error in JTree initialisation for
 *         not fully connected nodes (i.e. two different BNs)
 * 
 */
public class JunctionTree {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private DAG original;
	private HashMap<String, HasProbabilityTable> nodes = new HashMap<String, HasProbabilityTable>();
	private ArrayList edges = new ArrayList();

	private ArrayList cliques = new ArrayList();
	private ArrayList separators = new ArrayList();

	private boolean initialised = false;

	public static int messageNumber = 0;

	/* internal instance variables */
	private ArrayList ues2add = new ArrayList();
	private ArrayList tempEdges = new ArrayList();
	private ArrayList trees = new ArrayList();
	private ArrayList observedNodes = new ArrayList();

	public JunctionTree(DAG dag) {
		this.original = dag;

		for (int i = 0; i < dag.getNodes().length; i++) {
			nodes.put(dag.getNodes()[i].getName().toLowerCase(),
					dag.getNodes()[i]);
		}
		for (int i = 0; i < dag.getEdges().length; i++) {
			edges.add(dag.getEdges()[i]);
		}
		moralize();

		triangulate();

		buildCliques();

		buildOptimalJTree();

	}

	public JunctionTree() {
	}

	public void initialiseJTree() {
		/*
		 * get Nodes out of Clique, use CPDs out of nodes
		 */

		// System.out.println("\n\n\n\n\n\nHIERHIERHIER\n\n\n\n\n\n\n");

		HasProbabilityTable[] cliqueSeps = new HasProbabilityTable[cliques
				.size() + separators.size()];
		System.arraycopy(cliques.toArray(new Clique[0]), 0, cliqueSeps, 0,
				cliques.size());
		System.arraycopy(separators.toArray(new Separator[0]), 0, cliqueSeps,
				cliques.size(), separators.size());

		/*
		 * For each cluster and sepset X, set each Potential_X = 1:
		 */
		for (int i = 0; i < cliqueSeps.length; i++) {
			// System.out.println(cliqueSeps[i]);
			HasProbabilityTable current = cliqueSeps[i];
			Node[] represented = current.getParticipants();

			int crossProductSize = 1;
			for (int j = 0; j < represented.length; j++) {
				crossProductSize *= represented[j].countStates();
			}

			Probability[] table = new Probability[crossProductSize];

			String[] temp = new String[represented.length];
			ArrayList states = new ArrayList();
			ProbabilityDistribution.createStatePermutations(
					Arrays.asList(represented), states, temp, 0);

			if (crossProductSize != states.size())
				logger.error("WRONG CROSS PRODUCT calculated!!!");

			for (int j = 0; j < crossProductSize; j++)
				table[j] = new Probability((String[]) states.get(j), 1);

			current.setProbDistribution(table);
			// if (current instanceof Separator)
			// System.out.println("Separator: "+table);
		}

		/*
		 * For each Variable V,
		 */

		for (HasProbabilityTable h : nodes.values()) {
			Node current = (Node) h;
			/*
			 * assign to V a cluster X that contains Family_V
			 */
			Clique pc = current.assignClique(cliques);

			/*
			 * Multiply Potential_X by P(V|Parents_V):
			 */
			ProbabilityDistribution table = pc.getProbTable();

			Probability[] currentNodeProbs = current.getProbTable()
					.getProbabilities();

			for (int j = 0; j < currentNodeProbs.length; j++) {
				int[] indices = table.fitsIndex(current.getParticipants(),
						currentNodeProbs[j].getStates());

				for (int k = 0; k < indices.length; k++) {
					table.getProbabilities()[indices[k]]
							.multiplyProbability(currentNodeProbs[j]
									.getProbability());
				}
			}

			/*
			 * 2b: (Huang, Darwiche: 6.3) set each likelihoodelement Lambda_V(v)
			 * = 1
			 */
			current.initializeObservation();
		}

		/*
		 * fertig
		 */

		/*
		 * Testing for (int i=0;i<cliqueSeps.length;i++)
		 * System.out.println(cliqueSeps[i].getProbTable()); /*
		 */
		initialised = true;
	}

	/**
	 * convenience function for first Propagation, starting to propagate with a
	 * randomly chosen Clique
	 * 
	 */
	public void propagate() {
		if (cliques.isEmpty())
			return;
		int zufall = new Random().nextInt(cliques.size());
		Clique chosen = (Clique) cliques.get(zufall);
		// System.err.println("\n\n\n\n\n"+ "Zufallszahl: "+ zufall + ", "+
		// cliques.get(zufall) +"\n\n\n\n");
		propagate(chosen);
	}

	/**
	 * Global Propagation for local consistency: in two phases: 5.3.2
	 * Coordinating Multiple Messages - COLLECT-EVIDENCE - DISTRIBUTE-EVIDENCE
	 */
	public void propagate(Clique chosen) {
		/*
		 * Algorithm: Choose arbitrary clique unmark all cliques, call
		 * COLLECT-Evidence unmark all cliques, call DISTRIBUTE-Evidence
		 */

		for (int i = 0; i < cliques.size(); i++)
			((Clique) cliques.get(i)).mark(false);
		chosen.collectEvidence(null);

		for (int i = 0; i < cliques.size(); i++)
			((Clique) cliques.get(i)).mark(false);
		chosen.distributeEvidence();

		/* Testing */
		HasProbabilityTable[] cliqueSeps = new HasProbabilityTable[cliques
				.size() + separators.size()];
		System.arraycopy(cliques.toArray(new Clique[0]), 0, cliqueSeps, 0,
				cliques.size());
		System.arraycopy(separators.toArray(new Separator[0]), 0, cliqueSeps,
				cliques.size(), separators.size());
		for (int i = 0; i < cliqueSeps.length; i++)
			logger.debug(""+cliqueSeps[i].getProbTable());
		/**/

		for (HasProbabilityTable h : nodes.values()) {
			marginalize((Node) h);
			// System.err.println(((Node)h).printMarginalization());
		}
	}

	public boolean isInitialised() {
		return initialised;
	}

	/**
	 * constructor called: 1st step
	 * 
	 */
	private void moralize() {
		Node n;
		Node parent1;
		for (HasProbabilityTable h : nodes.values()) { // i = alle Knoten in
														// nodes
		// System.out.println(nodes.get(i));
			n = (Node) h;

			if (n.getIncoming().size() > 1) {
				for (int j = n.getIncoming().size() - 1; j >= 0; j--) // j =
																		// Eingehende
																		// Kanten
																		// von
																		// i.
																		// Verbindung
																		// zu
																		// den
																		// Parents
				{
					parent1 = ((Edge) n.getIncoming().get(j)).getSource();
					for (int k = j - 1; k >= 0; k--) { // k = die anderen
														// Parents (zwischen 0
														// und j)

						Node parent2 = ((Edge) n.getIncoming().get(k))
								.getSource();
						boolean found = false;
						// alle anderen parents durchlaufen, checken ob Edge in
						// dieser oder anderer richtung schon vorhanden
						for (int l = 0; l < edges.size(); l++) { // l = alle
																	// Kanten im
																	// Graphen
							int existing = ((UndirectedEdge) edges.get(l))
									.hasBorders(parent1, parent2);
							if (existing != 0)
								found = true;
						}

						// neue ungerichtete Kante anlegen --> AUCH EINTRAG IN
						// KONTEN???
						if (!found) {
							UndirectedEdge neu = new UndirectedEdge(parent1,
									parent2);
							edges.add(neu);
							parent1.addConnection(neu);
							parent2.addConnection(neu);

						}

					}
				}
			}
		}

		// remove directionalities ==> ONLY TREAT UndirectedEdge IN THE
		// FOLLOWING and never again getIncoming or getOutgoing
		/*
		 * for (int i=0; i<edges.size();i++){
		 * System.out.println((UndirectedEdge)edges.get(i)); }
		 */
	}

	/**
	 * constructor called: 2nd step tries to realize a Minimal Triangulation
	 * procedure adapted from Kjaerulff
	 * 
	 */
	private void triangulate() {
		ArrayList tempNodes = new ArrayList(nodes.values());

		while (!tempNodes.isEmpty()) {
			Node v = rankNodes(tempNodes);

			edges.addAll(ues2add);
			UndirectedEdge e = null;
			for (int i = 0; i < ues2add.size(); i++) {
				e = (UndirectedEdge) ues2add.get(i);
				e.getBorder1().addConnection(e);
				e.getBorder2().addConnection(e);
			}
			// TODO check if changes applied to nodes and tempNodes

			tempNodes.remove(v);

			Clique toIntroduce = new Clique((Node[]) formCluster(v, tempNodes)
					.toArray(new Node[0]));
			cliques.add(toIntroduce);
			// DISPLAY
			/*
			 * for (int i=0; i<edges.size();i++){
			 * System.out.println((UndirectedEdge)edges.get(i)); }
			 * 
			 * 
			 * System.out.println("REMAINING tempNodes: "+ tempNodes.size());
			 */}
		for (int i = 0; i < edges.size(); i++) {
			logger.debug(""+(UndirectedEdge) edges.get(i));
		}
	}

	/**
	 * @param tempNodes
	 * @return node to be treated next
	 */
	private Node rankNodes(ArrayList tempNodes) {

		if (tempNodes == null)
			return null;
		Node minimal = null;
		int countMin = tempNodes.size() * tempNodes.size(); // Quadrat aller
															// Knoten > maximale
															// Anzahl an
															// moglichen Kanten
		Node temp = null;

		for (int i = 0; i < tempNodes.size(); i++) {

			temp = (Node) tempNodes.get(i);
			int countTemp = calculateEdges2Add(temp, tempNodes);

			if (countTemp <= countMin) {
				if (countTemp == countMin
						&& clusterWeight(temp, tempNodes) > clusterWeight(
								minimal, tempNodes))
					continue;
				minimal = temp;
				countMin = countTemp;
				ues2add = tempEdges;

				/*
				 * System.out.println("Neuer Minimaler: "+minimal +
				 * " soviele Kanten dazu: "+countMin);
				 */
			}
		}

		return minimal;
	}

	/**
	 * @param temp
	 *            Node which determines the cluster
	 * @param tempNodes
	 *            the changed Graph to use for calculation
	 * 
	 * @return weight of the cluster induced by this node
	 */
	private int clusterWeight(Node temp, ArrayList tempNodes) {

		if (temp == null)
			return Integer.MAX_VALUE; // in case "minimal" is not yet set

		ArrayList neighbours = formCluster(temp, tempNodes);

		int product = 1;
		for (int i = 0; i < neighbours.size(); i++) {
			product *= ((Node) neighbours.get(i)).countStates();
		}

		/* System.out.println("Clusterweight Node "+ temp + ": "+product); */

		return product;
	}

	/**
	 * @param temp
	 *            Node which determines the cluster
	 * @param tempNodes
	 *            the changed Graph to use for calculation
	 * 
	 * @return number of edges to add to the graph
	 */
	private int calculateEdges2Add(Node temp, ArrayList tempNodes) {

		ArrayList neighbours = formCluster(temp, tempNodes);

		tempEdges = new ArrayList();

		for (int i = 0; i < neighbours.size(); i++) {
			Node n = (Node) neighbours.get(i);
			for (int j = i + 1; j < neighbours.size(); j++) {
				Node nn = (Node) neighbours.get(j);
				UndirectedEdge u = new UndirectedEdge(n, nn);
				if (!edges.contains(u))
					tempEdges.add(u);
			}
		}

		return tempEdges.size();
	}

	/**
	 * used in @see clusterWeight(temp,tempNodes) and @see
	 * calculateEdges2Add(temp,tempNodes)
	 * 
	 * @param temp
	 *            Node for Cluster
	 * @param tempNodes
	 *            graph surrounding this node
	 * 
	 * @return ArrayList of nodes representing the cluster
	 */
	private ArrayList formCluster(Node temp, ArrayList tempNodes) {

		ArrayList cluster = new ArrayList();
		cluster.add(temp);

		Node[] neighbours = temp.getNeighbours();

		for (int i = 0; i < neighbours.length; i++) {
			Node n = neighbours[i];
			if (tempNodes.contains(n))
				cluster.add(n);
		} // alternativ aber ohne testen:
			// cluster.addAll(Arrays.asList(temp.getNeighbours()));

		return cluster;
	}

	/**
	 * constructor called: 3rd step
	 * 
	 * Only "Pruning of Cliques" added in the second step "triangulation" Cares
	 * for Maximality (completeness already realized in triangulation algorithm)
	 * 
	 */
	private void buildCliques() {
		for (int i = cliques.size() - 1; i >= 0; i--) {
			Clique test = (Clique) cliques.get(i);

			for (int j = 0; j < i; j++) {
				if (Arrays.asList(((Clique) cliques.get(j)).getParticipants())
						.containsAll(Arrays.asList(test.getParticipants()))) {
					cliques.remove(test);
					break;
				}
			}
		}

		for (int i = 0; i < cliques.size(); i++) {
			logger.debug(""+cliques.get(i));
		}
	}

	/**
	 * constructor called: 4th step following Huang/Darwiche Procedural Guide,
	 * p.15
	 * 
	 */
	private void buildOptimalJTree() {
		ArrayList sepCandidates = new ArrayList();

		for (int i = cliques.size() - 1; i >= 0; i--) {
			Clique temp = (Clique) cliques.get(i);

			for (int j = 0; j < i; j++) {
				Separator cand = new Separator(temp, (Clique) cliques.get(j));
				sepCandidates.add(cand);
				/* System.out.println(cand); */
			}
		}

		while (separators.size() < cliques.size() - 1) {
			Separator selected = rankSeparators(sepCandidates);
			sepCandidates.remove(selected);

			if (connectingTrees(selected)) {
				separators.add(selected);
				Node eins = selected.getBorder1();
				Node zwei = selected.getBorder2();
				eins.addConnection(selected);
				zwei.addConnection(selected);
			}
		}

		for (int i = 0; i < separators.size(); i++) {
			logger.debug(""+separators.get(i));
		}
	}

	/**
	 * @param selected
	 * @param trees
	 * @return
	 */
	private boolean connectingTrees(Separator selected) {

		boolean connectingTrees = false;
		boolean bothUnknown = true;
		Node eins = selected.getBorder1();
		Node zwei = selected.getBorder2();

		for (int i = 0; i < trees.size(); i++) {
			ArrayList connected = (ArrayList) trees.get(i);
			if (connected.contains(eins)) { // FIRST FOUND
				bothUnknown = false;
				if (connected.contains(zwei))
					return false; // BOTH ALREADY IN SAME TREE => NOTHING
				else {
					for (int j = 0; j < trees.size(); j++) {
						ArrayList temp = (ArrayList) trees.get(j);
						if (temp.contains(zwei)) { // BOTH IN TWO DIFFERENT
													// TREES => JOIN TREES
							connected.addAll(temp);
							trees.remove(temp);
							return true;
						}
					}
					connected.add(zwei); // "FOR" RUN THROUGH: ONLY eins FOUND
											// => ADD THE NEW CLIQUE
					connectingTrees = true;
				}
			} else if (connected.contains(zwei)) // SECOND FOUND, BUT FIRST NOT
													// IN THE SAME TREE
			{
				bothUnknown = false;
				for (int j = 0; j < trees.size(); j++) {
					ArrayList temp = (ArrayList) trees.get(j);
					if (temp.contains(eins)) { // BOTH IN TWO DIFFERENT TREES =>
												// JOIN TREES
						connected.addAll(temp);
						trees.remove(temp);
						return true;
					}
				}
				connected.add(eins); // "FOR" RUN THROUGH: ONLY zwei FOUND =>
										// ADD THE NEW CLIQUE
				connectingTrees = true;
			}
		}

		if (bothUnknown) { // NOTHING FOUND
			ArrayList temp = new ArrayList();
			temp.add(eins);
			temp.add(zwei);
			trees.add(temp);
			connectingTrees = true;
		}
		return connectingTrees;
	}

	/**
	 * @param sepCandidates
	 * @return
	 */
	private Separator rankSeparators(ArrayList sepCandidates) {

		if (sepCandidates == null)
			return null;
		Separator maximal = null;
		int maxMass = -1;
		Separator temp = null;

		for (int i = 0; i < sepCandidates.size(); i++) {

			temp = (Separator) sepCandidates.get(i);
			int mass = temp.getLabel().length;

			if (maxMass <= mass) {
				if (maxMass == mass
						&& separatorCost(temp) > separatorCost(maximal))
					continue; // don't change maximal in this case: we have to
								// choose the separator with smallest cost
				maximal = temp;
				maxMass = mass;
			}
		}

		return maximal;
	}

	/**
	 * @param sepset
	 * @return sum of the weights of the limiting cliques
	 */
	private int separatorCost(Separator sepset) {

		return cliqueWeight((Clique) sepset.getBorder1())
				+ cliqueWeight((Clique) sepset.getBorder2());
	}

	/**
	 * @param temp
	 *            Clique whose weight is to be calculated
	 * 
	 * @return weight of the clique
	 */
	private int cliqueWeight(Clique temp) {

		if (temp == null)
			return Integer.MAX_VALUE;

		Node[] participants = temp.getParticipants();

		int product = 1;
		for (int i = 0; i < participants.length; i++) {
			product *= participants[i].countStates();
		}

		/* System.out.println("Cluiqueweight Clique "+ temp + ": "+product); */

		return product;
	}

	/**
	 * after Propagation: get changed probabilities of original nodes:
	 * Marginalisation
	 * 
	 * optimizable (see Huang, Darwiche, 10.2) to reduce number of message
	 * passes
	 */
	private void marginalize(Node n) {
		Clique usedClique = n.getParentClique();
		ProbabilityDistribution probDist = usedClique.getProbTable();
		// System.err.println(probDist);

		Node[] n_array = { n };
		Probability[] newProbs = new Probability[n.getStates().length];
		double newProbsSum = 0;

		for (int i = 0; i < newProbs.length; i++) {

			String[] value = { n.getStates()[i] };
			int[] indices = probDist.fitsIndex(n_array, value);

			double prob = 0;
			for (int y = 0; y < indices.length; y++) {
				prob += probDist.getProbabilities()[indices[y]]
						.getProbability();
			}
			newProbsSum += prob;

			newProbs[i] = new Probability(value, prob);
		}
		if (newProbsSum == 0)
			System.err.println("Error updating the probabilities of Node "
					+ n.getName() + ": SumPerNode=0");

		/*
		 * TESTING System.out.println(
		 * "\nSUMME DER MARGINALISIERTEN WAHRSCHEINLICHKEITEN VON NODE "
		 * +n.getName()+" IST: "+newProbsSum+"\n"); /*
		 */
		for (int i = 0; i < newProbs.length; i++)
			newProbs[i].multiplyProbability(1 / newProbsSum);

		n.setMarginalization(new ProbabilityDistribution(n, newProbs));

		/*
		 * IF observedData != leer: divide each prob by sum of all probs
		 */
	}

	/**
	 * 
	 * @param nodename
	 *            Name of the Node (Context Attribute
	 * @param probabilityPosition
	 *            the most probable value is 1, the second most probable value
	 *            is 2...
	 * @return the value of this RV
	 */
	public Probability getMarginalized(String nodename, int probabilityPosition) {
		if (probabilityPosition == 0)
			probabilityPosition = 1;
		Node wanted = null;
		Node temp = null;
		for (HasProbabilityTable h : nodes.values()) {
			temp = (Node) h;
			if (temp.getName().equalsIgnoreCase(nodename)) {
				wanted = temp;
				break;
			}
		}
		if (wanted == null)
			return null;
		ProbabilityDistribution pd = wanted.getMarginalization();
		Probability[] probs = pd.getProbabilities();

		double[] originalOrder = new double[probs.length];
		double[] sorted = new double[probs.length];
		for (int i = 0; i < originalOrder.length; i++) {
			originalOrder[i] = probs[i].getProbability();
			sorted[i] = probs[i].getProbability();
		}

		Arrays.sort(sorted);
		double searchedProb = sorted[sorted.length - probabilityPosition];
		String[] states = null;
		for (int target = 0; target < probs.length; target++) {
			if (originalOrder[target] == searchedProb) {
				states = probs[target].getStates();
				break;
			}
		}
		if (states.length > 1)
			logger.error("error with Marginalization!!!");
		return new Probability(states, searchedProb);

	}

	public Clique[] getCliques() {
		return (Clique[]) cliques.toArray(new Clique[0]);
	}

	public Separator[] getSeparators() {
		return (Separator[]) separators.toArray(new Separator[0]);
	}

	/**
	 * @param node
	 *            The observed node
	 * @param values
	 *            Ordered list of the values belonging to the proabilities in
	 *            perCents
	 * @param perCents
	 *            Probabilities, see above
	 */
	public boolean addEvidence(Node node, String[] values, double[] perCents) {
		Node[] nArray = { node };
		ProbabilityDistribution nodeLikelihood = node.setObservation(values,
				perCents);
		if (nodeLikelihood == null) {
			observedNodes.remove(node);
		} else if (!observedNodes.contains(node))
			observedNodes.add(node);

		Clique update = node.getParentClique();
		ProbabilityDistribution updatePD = update.getProbTable();
		Probability[] nodeProbs = nodeLikelihood.getProbabilities();

		for (int i = 0; i < nodeProbs.length; i++) {
			int[] indices = updatePD
					.fitsIndex(nArray, nodeProbs[i].getStates());
			for (int j = 0; j < indices.length; j++) {
				updatePD.getProbabilities()[indices[j]]
						.multiplyProbability(nodeProbs[i].getProbability());
			}
		}

		propagate(update);
		return true;
	}

	/**
	 * @param node
	 *            The observed node
	 * @param values
	 *            Ordered list of the values belonging to the probabilities in
	 *            perCents
	 * @param perCents
	 *            Probabilities, see above
	 */
	public boolean addEvidenceWithoutPropagating(Node node, String[] values,
			double[] perCents) {
		Node[] nArray = { node };
		ProbabilityDistribution nodeLikelihood = node.setObservation(values,
				perCents);
		if (nodeLikelihood == null) {
			observedNodes.remove(node);
		} else if (!observedNodes.contains(node))
			observedNodes.add(node);

		Clique update = node.getParentClique();
		ProbabilityDistribution updatePD = update.getProbTable();
		Probability[] nodeProbs = nodeLikelihood.getProbabilities();

		for (int i = 0; i < nodeProbs.length; i++) {
			int[] indices = updatePD
					.fitsIndex(nArray, nodeProbs[i].getStates());
			for (int j = 0; j < indices.length; j++) {
				updatePD.getProbabilities()[indices[j]]
						.multiplyProbability(nodeProbs[i].getProbability());
			}
		}

		return true;
	}

	public boolean addEvidenceWithoutPropagating(Node node, String state) {
		boolean found = false;
		String[] values = node.getStates();
		double[] probs = new double[values.length];

		for (int i = 0; i < values.length; i++) {
			if (state.equalsIgnoreCase(values[i])) {
				probs[i] = 1;
				found = true;
			} else
				probs[i] = 0;
		}

		logger.debug("ADD EVIDENCE(node, state) called. FOUND=" + found);

		if (found)
			return addEvidenceWithoutPropagating(node, values, probs);
		else
			return false;
	}

	/**
	 * @param node
	 *            The observed node
	 * @param value
	 *            value/state of the value range which will be assigned
	 *            'perCent' as probability
	 * @param perCent
	 *            Probabilities, see above
	 */
	public boolean addEvidence(Node node, String value, double perCent) {
		boolean found = false;
		if (node == null)
			return false;
		Node[] nArray = { node };
		String[] states = node.getStates();
		for (int i = 0; i < states.length; i++) {
			if (value.equalsIgnoreCase(states[i])) {
				found = true;
			}
		}
		if (!found)
			return false;

		double equalDistributionOfRemainingStates = (1 - perCent)
				/ (states.length - 1);
		double[] perCents = new double[states.length];
		for (int i = 0; i < states.length; i++) {
			if (states[i].equalsIgnoreCase(value))
				perCents[i] = perCent;
			else
				perCents[i] = equalDistributionOfRemainingStates;
		}

		ProbabilityDistribution nodeLikelihood = node.setObservation(states,
				perCents);
		if (nodeLikelihood == null) {
			observedNodes.remove(node);
		} else if (!observedNodes.contains(node))
			observedNodes.add(node);

		Clique update = node.getParentClique();
		ProbabilityDistribution updatePD = update.getProbTable();
		Probability[] nodeProbs = nodeLikelihood.getProbabilities();

		for (int i = 0; i < nodeProbs.length; i++) {
			int[] indices = updatePD
					.fitsIndex(nArray, nodeProbs[i].getStates());
			for (int j = 0; j < indices.length; j++) {
				updatePD.getProbabilities()[indices[j]]
						.multiplyProbability(nodeProbs[i].getProbability());
			}
		}

		propagate(update);
		return true;
	}

	/**
	 * sets RandomVariable to value "state". Uses overloaded method with
	 * probability 100 (in perCent)
	 * 
	 * @param node
	 * @param state
	 */
	public boolean addEvidence(Node node, String state) {
		boolean found = false;
		String[] values = node.getStates();
		double[] probs = new double[values.length];

		for (int i = 0; i < values.length; i++) {
			if (state.equalsIgnoreCase(values[i])) {
				probs[i] = 1;
				found = true;
			} else
				probs[i] = 0;
		}

		logger.debug("ADD EVIDENCE(node, state) called. FOUND=" + found);

		if (found)
			return addEvidence(node, values, probs);
		else
			return false;
	}

	/*
	 * TODO Is still removing ALL evidence
	 */
	public void removeEvidence(Node node) {
		node.initializeObservation();
		observedNodes.remove(node);
		ProbabilityDistribution[] observations = new ProbabilityDistribution[observedNodes
				.size()];

		for (int i = 0; i < observedNodes.size(); i++)
			observations[i] = ((Node) observedNodes.get(i)).getObservation();

		initialiseJTree();

		for (int i = 0; i < observedNodes.size(); i++) {
			Node temp = (Node) observedNodes.get(i);
			temp.setObservation(observations[i]);

			propagate(temp.getParentClique());
		}
	}

	/*
	 * should remove ALL evidence
	 */
	public void removeAllEvidence() {
		for (Object node : observedNodes)
			((Node) node).initializeObservation();
		initialiseJTree();
		propagate();

		observedNodes = new ArrayList();
	}

	public HasProbabilityTable getNode(String nodename) {
		return nodes.get(nodename.toLowerCase());
	}

	public ArrayList<UndirectedEdge> getEdges() {
		return (this.edges);
	}

	public HashMap<String, HasProbabilityTable> getNodes() {
		return this.nodes;
	}

	public ArrayList<Clique> getCliquesArrayList() {
		return this.cliques;
	}

	public ArrayList<Separator> getSeparatorsArrayList() {
		return this.separators;
	}
}
