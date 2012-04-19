package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.BayesletJTree;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils.EvidenceRecord;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils.ReadyFlag;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils.Util;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.ConnectingNodes;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;

/**
 * @author gall_pa
 * 
 * @param name
 *            Name of the bayeslet
 * @param bayesianNetwork
 *            Direct acyclic graph
 * @param junctionTree
 *            Junction tree obtained from the attribute bayesianNetwork.
 * @param interfaceNodesInfo
 *            List of interface nodes contained in this bayeslet.
 * @param color
 *            To be used in the bayeslets cycle detection.
 * @param alreadyUpdated
 *            To be used in the soft evidence approach. It is true when the
 *            bayeslet already propagated its probabilities as a response to
 *            another bayeslet request. If another bayeslet requests some
 *            information to this bayeslet, it does not need to propagate again.
 * @uml.dependency 
 *                 supplier="eu.ist.daidalos.pervasive.bayesianLibrary.structures.DAG"
 */
public class Bayeslet extends Thread {

	private static Logger logger = LoggerFactory.getLogger(Bayeslet.class);
	final boolean screenEnabled = false;

	public String name;
	private DAG bayesianNetwork;
	private BayesletJTree junctionTree;
	public ArrayList<InterfaceNodeRecord> interfaceNodesInfo;
	private String color;
	protected boolean alreadyUpdated;
	Bayeslet[] storedBayeslets;
	ReadyFlag[] semaphores;
	long startingTime;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name to be assigned to the new bayeslet.
	 */
	public Bayeslet(String name) {
		this.name = name;
		this.bayesianNetwork = new DAG();
		this.alreadyUpdated = false;
		this.interfaceNodesInfo = new ArrayList<InterfaceNodeRecord>();
	}

	/**
	 * Constructor
	 * 
	 * @param d
	 *            Direct acyclic graph to be assigned to the new bayeslet.
	 * @param name
	 *            Name given to the new bayeslet
	 */
	public Bayeslet(DAG d, String name) {
		bayesianNetwork = d;
		this.name = name;
		this.junctionTree = new BayesletJTree(bayesianNetwork);
		this.junctionTree.initialiseJTree();
		this.junctionTree.propagate();
		this.interfaceNodesInfo = new ArrayList<InterfaceNodeRecord>();
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name given to the bayeslet.
	 * @param n
	 *            nodes to be added to the direct acyclic graph that will be
	 *            built inside the bayeslet.
	 * @param e
	 *            edges to be added to the direct acyclic graph that will be
	 *            built inside the bayeslet.
	 */
	public Bayeslet(ArrayList<NodeBL> n, ArrayList<Edge> e, String name) {
		ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
		for (NodeBL node : n) {
			nodes.add(node);
		}
		ArrayList<ConnectingNodes> edges = new ArrayList<ConnectingNodes>();
		for (Edge edge : e) {
			edges.add(edge);
		}
		this.bayesianNetwork = new DAG(nodes, edges);
		this.name = name;
		this.alreadyUpdated = false;
		this.junctionTree = new BayesletJTree(bayesianNetwork);
		this.junctionTree.initialiseJTree();
		this.junctionTree.propagate();
		// this.connectedBayeslets=new ArrayList<BayesletInfo>();
		this.interfaceNodesInfo = new ArrayList<InterfaceNodeRecord>();
		// this.changedStructure=false;

	}

	public Bayeslet(ArrayList<NodeBL> n, ArrayList<Edge> e, String name,
			String node, String state) {
		ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
		for (NodeBL bayesletnode : n) {
			nodes.add(bayesletnode);
		}
		ArrayList<ConnectingNodes> edges = new ArrayList<ConnectingNodes>();
		for (Edge edge : e) {
			edges.add(edge);
		}
		this.bayesianNetwork = new DAG(nodes, edges);
		this.name = name;
		this.alreadyUpdated = false;
		this.junctionTree = new BayesletJTree(bayesianNetwork);
		this.junctionTree.initialiseJTree();
		this.junctionTree.addEvidence(this.getNode(node), state);
		this.junctionTree.propagate();

		// this.connectedBayeslets=new ArrayList<BayesletInfo>();
		this.interfaceNodesInfo = new ArrayList<InterfaceNodeRecord>();
		// this.changedStructure=false;

	}

	/**
	 * This method returns the junction tree contained in this bayeslet.
	 * 
	 * @return
	 */
	public BayesletJTree getJunctionTree() {
		return this.junctionTree;
	}

	/**
	 * This method returns the name of this bayeslet.
	 * 
	 * @return
	 */
	public String getBName() {
		return (this.name);
	}

	/**
	 * This method return the node called wanted. In case of not finding it, it
	 * returns null.
	 * 
	 * @param wanted
	 * @return The node inside the bayeslet whose name is wanted, or null.
	 */
	public NodeBL getNode(String wanted) {

		boolean found = false;
		int i = 0;
		Node[] search = bayesianNetwork.getNodes();

		while (!found) {
			if (search[i].getName().contains(wanted)) {
				found = true;
				return (NodeBL) (search[i]);
			} else {
				i++;
			}
		}
		return null;
	}

	/**
	 * Connecting bayeslets method . Sets node named wanted as an interface
	 * node, connected to the bayeslet and the node that are defined in
	 * bayesletToBeConnected.
	 * 
	 * @param wanted
	 * @param bayesletToBeConnected
	 * @param incoming
	 *            Defines whether the interface node is an incoming interface
	 *            node, or not.
	 */
	public void connectBayeslet(String wanted,
			BayesletInfo bayesletToBeConnected, boolean incoming) {

		int alreadyRegistered = registered(wanted);

		if (alreadyRegistered == -1) { // if this node was never registered as
										// an interface node
			InterfaceNodeRecord newInterfaceNode = new InterfaceNodeRecord(
					wanted, bayesletToBeConnected, incoming);
			interfaceNodesInfo.add(newInterfaceNode);

		} else { // if it is already registered, the information for the new
					// connection for this interface node is added
			InterfaceNodeRecord toBeChanged = (InterfaceNodeRecord) interfaceNodesInfo
					.get(alreadyRegistered);
			if (incoming == toBeChanged.isIncomingInterfaceNode()) {
				toBeChanged.addConnectedBayeslet(bayesletToBeConnected);
			}
		}

	}

	/**
	 * This method returns an integer depending on if the node named String is
	 * already registered as an interface node, or not. If it is an interface
	 * node, it returns its position in interfaceNodesInfo, if it is not an
	 * interface node, it returns -1.
	 * 
	 * @param wanted
	 *            Name of the node.
	 * @return Returns an integer containing the position of the information
	 *         related to that node contained in the ArrayList
	 *         interfaceNodesInfo, or -1 if that node is not founded.
	 */
	private int registered(String wanted) {
		boolean found = false;
		int i = 0;
		while ((!found) && (i < interfaceNodesInfo.size())) {
			if (((InterfaceNodeRecord) interfaceNodesInfo.get(i)).getName()
					.equals(wanted)) {
				found = true;
			} else {
				i++;
			}
		}
		if (!found) {
			return (-1);
		} else {
			return (i);
		}
	}

	/**
	 * This method returns the array list containing the information about the
	 * interface nodes existing in this bayeslet.
	 * 
	 * @return
	 */
	public ArrayList<InterfaceNodeRecord> getInterfaceNodes() {
		return (interfaceNodesInfo);
	}

	/**
	 * This method return the InterfaceNodeRecord related to that node whose
	 * name is the input parameter name.
	 * 
	 * @param name
	 *            Name of the node whose InterfaceNodeRecord is going to be
	 *            searched.
	 * @return
	 */
	public InterfaceNodeRecord getInterfaceNodeInfo(String name) {
		boolean found = false;
		int indexInterfaceNodes = 0;
		while ((indexInterfaceNodes < this.interfaceNodesInfo.size())
				&& (!found)) {
			InterfaceNodeRecord temp = (InterfaceNodeRecord) interfaceNodesInfo
					.get(indexInterfaceNodes);
			if (temp.getName().equals(name)) {
				found = true;
				return temp;
			} else {
				indexInterfaceNodes++;
			}
		}
		return null;

	}

	/**
	 * This method returns an array list containing the information about the
	 * incoming interface nodes existing in this bayeslet.
	 * 
	 * @return
	 */
	public ArrayList<InterfaceNodeRecord> getIncomingInterfaceNodes() {

		ArrayList<InterfaceNodeRecord> incomingInterfaceNodes = new ArrayList<InterfaceNodeRecord>();

		InterfaceNodeRecord temp;

		int i = 0;

		while (i < interfaceNodesInfo.size()) {

			temp = (InterfaceNodeRecord) interfaceNodesInfo.get(i);

			if (temp.isIncomingInterfaceNode()) {
				incomingInterfaceNodes.add(temp);
			}

			i++;
		}

		return (incomingInterfaceNodes);
	}

	/**
	 * This method returns the information about all the outgoing interface
	 * nodes. It is used to propagate the change information in the first
	 * approach (soft evidence).
	 * 
	 * @return
	 */
	public ArrayList<InterfaceNodeRecord> getOutgoingInterfaceNodes() {

		ArrayList<InterfaceNodeRecord> outgoingInterfaceNodes = new ArrayList<InterfaceNodeRecord>();

		InterfaceNodeRecord temp;

		int i = 0;

		while (i < interfaceNodesInfo.size()) {

			temp = (InterfaceNodeRecord) interfaceNodesInfo.get(i);

			if (!temp.isIncomingInterfaceNode()) {
				outgoingInterfaceNodes.add(temp);
			}

			i++;
		}

		return (outgoingInterfaceNodes);
	}

	/**
	 * This method returns an object from class BayesletInfo containing its
	 * name.
	 * 
	 * @param nodeName
	 * @return
	 */
	public BayesletInfo getBayesletInfo(String nodeName) {
		BayesletInfo toReturn = new BayesletInfo();
		toReturn.setBayesletName(this.name);
		toReturn.setNodeName(nodeName);
		return (toReturn);
	}

	/**
	 * This method returns the normalization of a certain node.
	 * 
	 * @param nodeName
	 *            Name of the node.
	 * @return
	 */
	public ProbabilityDistributionBL getNormalization(String nodeName) {
		NodeBL search = getNode(nodeName);
		ProbabilityDistributionBL toReturn = null;
		if (search.getMarginalization() instanceof ProbabilityDistributionBL)
			toReturn = (ProbabilityDistributionBL) search.getMarginalization();
		return toReturn;
	}

	/**
	 * This method adds hard evidence to node called variable. It sets it to
	 * state fixedState.
	 * 
	 * @param variable
	 * @param fixedState
	 */
	public void addEvidence(String variable, String fixedState) {
		if (screenEnabled)
			System.out.println(variable + " fixed to " + fixedState);
		NodeBL target = this.getNode(variable);
		junctionTree.addEvidence(target, fixedState);

	}

	/**
	 * This method adds soft evidence to a certain node. Then, it resets its
	 * observations, so another soft evidence can be introduced into the same
	 * node.
	 * 
	 * @param nodeName
	 *            Name where the soft evidence is going to be applied.
	 * @param probDis
	 *            Soft evidence to be applied.
	 */

	public void addSoftEvidence(String nodeName,
			ProbabilityDistributionBL probDis) {

		if (screenEnabled)
			System.out.println("Adding Soft Evidence to node :" + nodeName);

		NodeBL target = this.getNode(nodeName);

		String[] values = target.getStates();

		// RESET THE OBSERVATION AFTER ADDING SOFT EVIDENCE

		this.junctionTree.addEvidence(target, values,
				Util.probabilityToDouble(probDis.getProbabilities()));
		Probability[] reset = target.getObservation().getProbabilities();
		int indexReset = 0;

		while (indexReset < reset.length) {
			reset[indexReset].setProbability(1);
			indexReset++;
		}

		if (screenEnabled) {
			System.out
					.println("------------------------------------------------------");
			System.out.println("Marginalization Node: " + nodeName);
			System.out.println(target.getMarginalization().toString());
			System.out.println("Observation Node: " + nodeName);
			System.out.println(target.getObservation().toString());
			System.out.println("Probability Table Node: " + nodeName);
			System.out.println(target.getProbTable().toString());
			System.out
					.println("------------------------------------------------------");
		}

	}

	/**
	 * This method adds soft evidence to node target. It is used in the soft
	 * evidence approach, in those cases where soft evidence is accumulated
	 * before being introduced.
	 * 
	 * @param target
	 *            Node where the soft evidence is added.
	 * 
	 * @param input
	 *            double[] to be added as soft evidence.
	 */

	public void addSoftEvidence2(NodeBL target, double[] input) {

		if (screenEnabled)
			System.out.println("Adding Soft Evidence to node :"
					+ target.getName());

		this.junctionTree.addEvidence(target, target.getStates(), input);

		if (screenEnabled) {
			System.out
					.println("------------------------------------------------------");
			System.out.println("Marginalization Node: " + target.getName());
			System.out.println(target.getMarginalization().toString());
			System.out.println("Observation Node: " + target.getName());
			System.out.println(target.getObservation().toString());
			System.out.println("Probability Table Node: " + target.getName());
			System.out.println(target.getProbTable().toString());
			System.out
					.println("------------------------------------------------------");
		}
		//

	}

	public boolean getAlreadyUpdated() {
		return alreadyUpdated;
	}

	public void setAlreadyUpdated(boolean state) {
		this.alreadyUpdated = state;
	}

	public NodeBL[] getAllNodes() {
		Node[] general = this.bayesianNetwork.getNodes();
		if (general instanceof NodeBL[])
			return (NodeBL[]) general;
		return null;
	}

	public Edge[] getAllEdges() {
		return (this.bayesianNetwork.getEdges());
	}

	/**
	 * This method returns the array list containing the name of all bayeslet
	 * this bayeslet is connected to through an output interface node. It is
	 * used during the bayeslet cycle detection process.
	 */
	public ArrayList<String> getConnectedBayeslets() {
		ArrayList<String> connBayeslets = new ArrayList<String>();
		int indexInterfaceNodes = 0;
		int indexConnectedBayeslets = 0;
		// Goes through all the outgoing interface nodes from this bayeslet.
		while (indexInterfaceNodes < this.getOutgoingInterfaceNodes().size()) {
			InterfaceNodeRecord tempINR = this.getOutgoingInterfaceNodes().get(
					indexInterfaceNodes);

			indexConnectedBayeslets = 0;

			while (indexConnectedBayeslets < tempINR.getConnectedBayeslets()
					.size()) {
				BayesletInfo tempBI = tempINR.getConnectedBayeslets().get(
						indexConnectedBayeslets);
				// If bayeslet described in tempBI was not already added to the
				// ArrayList
				if (!connBayeslets.contains(tempBI.getName())) {
					connBayeslets.add(tempBI.getName());
				}
				indexConnectedBayeslets++;

			}

			indexInterfaceNodes++;
		}
		return connBayeslets;

	}

	public void setColor(String c) {
		this.color = c;
	}

	public String getColor() {
		return (this.color);
	}

	public DAG getDAG() {
		return this.bayesianNetwork;
	}

	/**
	 * This method obtains the marginalization of a certain node in a certain
	 * bayeslet. To do that it gets the marginalization of it goes through all
	 * incoming interface nodes, and request the related bayeslets to provide
	 * with the marginalization of their corresponding output interface nodes.
	 * This marginalization is applied as soft evidence to the incoming
	 * interface nodes.
	 * 
	 * 
	 * @param requestedBayeslet
	 * @param requestedNode
	 * @param storedBayeslets
	 * @return
	 */
	public ProbabilityDistributionBL obtainMarginalization(
			String requestedNode, Bayeslet[] storedBayeslets) {

		ArrayList incomingInterfaceNodesBayeslet;
		ArrayList connectedBayeslets;
		InterfaceNodeRecord nodeInfo;
		String searchedBayesletName;
		int indexIncomingInterfaceNodes = 0;
		int indexConnectedBayeslets = 0;

		NodeBL nTarget = this.getNode(requestedNode);
		incomingInterfaceNodesBayeslet = this.getIncomingInterfaceNodes();

		if ((incomingInterfaceNodesBayeslet.size() != 0)
				&& (!this.getAlreadyUpdated())) {

			while (indexIncomingInterfaceNodes < incomingInterfaceNodesBayeslet
					.size()) {

				nodeInfo = (InterfaceNodeRecord) incomingInterfaceNodesBayeslet
						.get(indexIncomingInterfaceNodes);

				connectedBayeslets = nodeInfo.getConnectedBayeslets();

				while (indexConnectedBayeslets < connectedBayeslets.size()) {

					BayesletInfo tempBayesletInfo = ((BayesletInfo) connectedBayeslets
							.get(indexConnectedBayeslets));

					searchedBayesletName = tempBayesletInfo.getName();
					Bayeslet searchedBayeslet = Util.searchBayeslet(
							searchedBayesletName, storedBayeslets);
					String nodeName = tempBayesletInfo.getNodeName();

					ProbabilityDistributionBL probDis = searchedBayeslet
							.obtainMarginalization(nodeName, storedBayeslets);
					System.out.println("SE calculada: " + nodeName);
					System.out.println(probDis.toStringWithoutParents());
					this.addSoftEvidence(nodeInfo.getName(), probDis);

					indexConnectedBayeslets++;
				}
				indexConnectedBayeslets = 0;

				indexIncomingInterfaceNodes++;
			}

		}

		this.setAlreadyUpdated(true);

		return (this.getNormalization(nTarget.getName()));

	}

	/**
	 * This method has the same objective as the method obtainMarginalization
	 * (see above). This one is the version used during the parallel testing
	 * (where each Bayeslet runs in an independent thread).
	 * 
	 * @param requestedNode
	 * @param storedBayeslets
	 * @return
	 * @throws InterruptedException
	 */
	public ProbabilityDistributionBL obtainMarginalizationParallel(
			String requestedNode, Bayeslet[] storedBayeslets)
			throws InterruptedException {

		ArrayList incomingInterfaceNodesBayeslet;
		ArrayList connectedBayeslets;
		InterfaceNodeRecord nodeInfo;
		String searchedBayesletName;
		int indexIncomingInterfaceNodes = 0;
		int indexConnectedBayeslets = 0;

		// Bayeslet bTarget =
		// utils.searchBayeslet(requestedBayeslet,storedBayeslets);

		NodeBL nTarget = this.getNode(requestedNode);
		incomingInterfaceNodesBayeslet = this.getIncomingInterfaceNodes();

		if ((incomingInterfaceNodesBayeslet.size() != 0)
				&& (!this.getAlreadyUpdated())) {

			while (indexIncomingInterfaceNodes < incomingInterfaceNodesBayeslet
					.size()) {

				nodeInfo = (InterfaceNodeRecord) incomingInterfaceNodesBayeslet
						.get(indexIncomingInterfaceNodes);

				connectedBayeslets = nodeInfo.getConnectedBayeslets();

				while (indexConnectedBayeslets < connectedBayeslets.size()) {

					BayesletInfo tempBayesletInfo = ((BayesletInfo) connectedBayeslets
							.get(indexConnectedBayeslets));

					searchedBayesletName = tempBayesletInfo.getName();

					String nodeName = tempBayesletInfo.getNodeName();

					Bayeslet searchedBayeslet = Util.searchBayeslet(
							searchedBayesletName, storedBayeslets);
					if (this.screenEnabled)
						System.out
								.println("Bayeslet " + this.name
										+ " waiting for "
										+ searchedBayeslet.getBName());

					// waitOtherBayeslets(searchedBayeslet);
					// searchedBayeslet.join();
					ReadyFlag tempRF = Util.searchSemaphore(
							searchedBayesletName, semaphores);

					synchronized (tempRF) {
						while (!searchedBayeslet.getAlreadyUpdated())
							tempRF.wait();
					}

					/*
					 * synchronized (searchedBayeslet){
					 * while(!searchedBayeslet.getAlreadyUpdated()){
					 * 
					 * try{ System.out.println("Bayeslet "+this.getBName()+
					 * " sigue esperando..."); wait();
					 * }catch(InterruptedException e){}; }
					 */

					if (this.screenEnabled)
						System.out.println("Bayeslet " + this.getBName()
								+ " ya tiene lo que quiere!");

					ProbabilityDistributionBL probDis = searchedBayeslet
							.obtainMarginalizationParallel(nodeName,
									storedBayeslets);
					// System.out.println("SE calculada: "+ nodeName);
					// System.out.println(probDis.toStringWithoutParents());
					this.addSoftEvidence(nodeInfo.getName(), probDis);

					indexConnectedBayeslets++;
				}
				indexConnectedBayeslets = 0;

				indexIncomingInterfaceNodes++;
			}

		}

		if (this.screenEnabled)
			System.out.println(this.getBName() + " about to notifyAll()");
		// notifyRestOfBayeslets();
		this.setAlreadyUpdated(true);
		ReadyFlag tempRF2 = Util.searchSemaphore(this.name, semaphores);

		synchronized (tempRF2) {
			tempRF2.notifyAll();
		}

		/*
		 * if (this.getOutgoingInterfaceNodes().get(0)!=null){
		 * InterfaceNodeRecord tempINR=this.getOutgoingInterfaceNodes().get(0);
		 * Bayeslet
		 * tempBL=utils.searchBayeslet(tempINR.getConnectedBayeslets().get
		 * (0).getName(), this.storedBayeslets);
		 * //notifyRestOfBayeslets(tempBL); }
		 */
		return (this.getNormalization(nTarget.getName()));

	}

	/**
	 * This method adds all nodes and edges from all bayeslets in the input
	 * array of bayeslets to the Bayesian network in this Bayeslet. It is the
	 * first step to construct the big bayesian network in the second approach.
	 * 
	 * @param bayeslets
	 * @param combinedDAG
	 */
	public void addNodesEdges(Bayeslet[] bayeslets) {

		ArrayList tempNodes;
		ArrayList tempEdges;
		ArrayList combinedNodes = this.bayesianNetwork.getNodesArrayList();
		ArrayList combinedEdges = this.bayesianNetwork.getEdgesArrayList();

		int i = 0;
		int indexNodes = 0;
		int indexEdges = 0;

		while (i < bayeslets.length) {

			tempNodes = bayeslets[i].getDAG().getNodesArrayList();

			tempEdges = bayeslets[i].getDAG().getEdgesArrayList();

			// Adding nodes from this DAG to the combined one

			while (indexNodes < tempNodes.size()) {
				combinedNodes.add(tempNodes.get(indexNodes));
				indexNodes++;
			}

			indexNodes = 0;

			// Adding edges from this DAG to the combined one

			while (indexEdges < tempEdges.size()) {
				combinedEdges.add(tempEdges.get(indexEdges));
				indexEdges++;
			}

			indexEdges = 0;

			i++;

		}
		i = 0;
	}

	/**
	 * This method is the second step to construct the the Big Bayesian network
	 * in the second approach Combines the interface nodes involved in bayeslets
	 * connections
	 * 
	 * @param bayeslets
	 */
	public void updateConnections(Bayeslet[] bayeslets) {

		int indexInterfaceNodes = 0;

		ArrayList combinedNodes = this.bayesianNetwork.getNodesArrayList();
		ArrayList combinedEdges = this.bayesianNetwork.getEdgesArrayList();
		ArrayList tempConnectedBayeslets;
		InterfaceNodeRecord tempInterfaceNode;
		NodeBL tempSourceNode;
		NodeBL tempTargetNode;
		String sourceNodeName;
		String targetNodeName;
		BayesletInfo tempBayesletInfo;

		int indexConnectedBayeslets = 0;
		int i = 0;

		while (i < bayeslets.length) {
			// Goes through all bayeslets
			ArrayList tempIncomingInterfaceNodes = bayeslets[i]
					.getIncomingInterfaceNodes();

			while (indexInterfaceNodes < tempIncomingInterfaceNodes.size()) {
				// Goes through all incoming interface nodes of each bayeslet
				tempInterfaceNode = ((InterfaceNodeRecord) tempIncomingInterfaceNodes
						.get(indexInterfaceNodes));

				if (tempInterfaceNode.getToBeUpdatedNode()) {

					targetNodeName = tempInterfaceNode.getName();
					tempTargetNode = bayeslets[i].getNode(targetNodeName);
					tempConnectedBayeslets = tempInterfaceNode
							.getConnectedBayeslets();

					while (indexConnectedBayeslets < tempConnectedBayeslets
							.size()) {
						// Goes through all connected bayeslets to a certain
						// incoming interface node
						tempBayesletInfo = (BayesletInfo) tempConnectedBayeslets
								.get(indexConnectedBayeslets);
						// tempBayesletName=tempBayesletInfo.getName();
						sourceNodeName = tempBayesletInfo.getNodeName();
						// tempBayeslet=utils.searchBayeslet(tempBayesletName,bayeslets);
						// tempSourceNode=tempBayeslet.getNode(sourceNodeName);

						tempTargetNode = Util.searchNode(targetNodeName,
								combinedNodes);
						tempSourceNode = Util.searchNode(sourceNodeName,
								combinedNodes);

						ProbabilityDistribution targetProbTable = tempTargetNode
								.getProbTable();

						if (!(targetProbTable instanceof ProbabilityDistributionBL)) {
							logger.error("ProbabilityDistribution of target node is not a Bayeslet-ProbabilityDistribution.\n\tExiting");
							return;
						}

						((ProbabilityDistributionBL) targetProbTable)
								.combineProbabilityDistribution(tempSourceNode,
										tempTargetNode);

						Util.transferEdges(tempSourceNode, tempTargetNode,
								combinedEdges);

						tempTargetNode.updateName(tempSourceNode.getName());

						combinedNodes.remove(tempSourceNode);

						indexConnectedBayeslets++;
					}

					indexConnectedBayeslets = 0;

					tempInterfaceNode.setToBeUpdatedNode(false);
				}
				indexInterfaceNodes++;
			}
			indexInterfaceNodes = 0;
			i++;
		}
		this.buildJunctionTree();
	}

	/**
	 * This method adds a set of evidences to this bayeslet.
	 * 
	 * @param evidences
	 */
	public void addEvidences(ArrayList evidences) {
		// Propagation of evidence is included in the execution time!!!
		if (evidences != null) {
			for (int i = 0; i < (evidences).size(); i++) {
				EvidenceRecord tempEv = (EvidenceRecord) evidences.get(i);
				this.addEvidence(tempEv.getNodeName(), tempEv.getEvidence());

			}
		}
	}

	public void setStoredBayeslets(Bayeslet[] storedBayeslets) {
		this.storedBayeslets = storedBayeslets;
	}

	public void setSemaphores(ReadyFlag[] semaphores) {
		this.semaphores = semaphores;
	}

	public void setStartingTime(long input) {
		this.startingTime = input;
	}

	/**
	 * This method is used during the execution time testing, in the parallel
	 * processing version. Each Bayeslet is asked to marginalize a certain node.
	 */
	public void run() {

		if (this.screenEnabled)
			System.out
					.println("Bayeslet " + this.getBName() + " began to run.");
		if (name == "B1") {
			try {
				System.out.println(this.obtainMarginalizationParallel(
						"B1:Action_Brake", storedBayeslets));

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else {
			try {
				this.obtainMarginalizationParallel(getAllNodes()[0].getName(),
						storedBayeslets);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (this.screenEnabled)
			System.out.println("Bayeslet " + this.getBName()
					+ " finished to run.");
	}

	/**
	 * This mehotd constructs, initialises and propagates the junction tree of
	 * this Bayeslet.
	 */
	void buildJunctionTree() {
		this.junctionTree = new BayesletJTree(this.bayesianNetwork);
		this.junctionTree.initialiseJTree();
		this.junctionTree.propagate();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////NOT USED SO
	// FAR//////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method is dissolving a combined node.
	 * 
	 * @param name
	 *            Name of the node to be dissolved.
	 * @param bayeslets
	 *            Structure containing all bayeslets.
	 * @param backUpBayeslets
	 *            Structure containing the original version of all bayeslets.
	 */
	/*
	 * public void dissolveCombinedNode(String name,Bayeslet[]
	 * bayeslets,Bayeslet[] backUpBayeslets){
	 * 
	 * Node toBeRemoved=this.getNode(name);
	 * this.getDAG().removeNode(toBeRemoved);
	 * //this.disconnectSelfInterfaceNode(name);
	 * String[]combinedNodes=toBeRemoved.getName().split("-"); int
	 * indexCombinedNodes=0;
	 * 
	 * while(indexCombinedNodes<combinedNodes.length){
	 * 
	 * String nodeName=combinedNodes[indexCombinedNodes]; String[]
	 * bayesletAndNode=combinedNodes[indexCombinedNodes].split(":"); String
	 * bayesletName=bayesletAndNode[0];
	 * 
	 * Bayeslet current=utils.searchBayeslet(bayesletName, bayeslets); Bayeslet
	 * backUp=utils.searchBayeslet(bayesletName, backUpBayeslets);
	 * 
	 * InterfaceNodeRecord tempINR=current.getInterfaceNodeInfo(nodeName);
	 * 
	 * this.resetNode(nodeName,backUp,current,tempINR.isIncomingInterfaceNode());
	 * 
	 * tempINR.setToBeUpdatedNode(true);
	 * 
	 * indexCombinedNodes++; } }
	 */
	/**
	 * This method returns a node to its original state (before being combined).
	 * To do that it checks in the backup structure (backUpBayeslet) and updates
	 * the edges and the probabilities table.
	 * 
	 * 
	 * @param connectedNodeName
	 *            Name of the node to be dissolved.
	 * @param backUpBayeslet
	 *            Structure containing the original state of the bayeslets.
	 * @param current
	 *            Current state of the bayeslet containing the node to be
	 *            dissolved.
	 * @param incomingInterfaceNode
	 *            Defines whether the node is an input interface node or an
	 *            output interface node.
	 */
	/*
	 * public void resetNode(String connectedNodeName, Bayeslet
	 * backUpBayeslet,Bayeslet current,boolean incomingInterfaceNode){
	 * 
	 * Node backUpNode= backUpBayeslet.getNode(connectedNodeName); Node newNode=
	 * new Node(backUpNode.getName(),backUpNode.getStates());
	 * 
	 * utils.transferEdgesBackUp(backUpNode, newNode,
	 * this.getDAG().getNodesArrayList(),this.getDAG().getEdgesArrayList());
	 * //Probability[] temp = backUpNode.getProbTable().getProbabilities();
	 * newNode
	 * .setProbDistribution(backUpNode.getProbTable().getProbabilities());
	 * 
	 * Node currentNode = current.getNode(connectedNodeName);
	 * 
	 * currentNode.setName(connectedNodeName);
	 * currentNode.setProbDistribution(backUpNode
	 * .getProbTable().getProbabilities());
	 * 
	 * if (incomingInterfaceNode) { utils.transferEdgesBackUp(backUpNode,
	 * currentNode,
	 * current.getDAG().getNodesArrayList(),current.getDAG().getEdgesArrayList
	 * ()); }
	 * 
	 * this.getDAG().addNode(newNode);
	 * 
	 * }
	 * 
	 * /** This method is used when disconnecting a certain interface node from
	 * a bayeslet. It clears the information about all connected bayeslets that
	 * were connected to this one through that interface node.
	 * 
	 * @param name Name of the interface node to be disconnected.
	 */
	/*
	 * public void disconnectSelfInterfaceNode(String name){ int indexIN=0;
	 * boolean found=false;
	 * 
	 * ArrayList interfaceNodes=this.getInterfaceNodes();
	 * 
	 * while ((indexIN<interfaceNodes.size())&&(!found)){ InterfaceNodeRecord
	 * temINR=(InterfaceNodeRecord) interfaceNodes.get(indexIN); if
	 * (temINR.getName().contains(name)){
	 * temINR.getConnectedBayeslets().clear(); found=true; }else{ indexIN++; } }
	 * }
	 */

	/**
	 * This method disconnects removes the information about every single
	 * connection between this bayeslet and another one. To do that it goes
	 * through all interface nodes, and in case of finding information about one
	 * connection related to a certain bayeslet, it deletes it.
	 * 
	 * @param name
	 *            Name of the bayeslet which we want this bayeslet to get
	 *            disconnected from.
	 */
	/*
	 * public void disconnectFromBayeslet(String name){ int indexIN=0; int
	 * iConnectedBayeslets=0; ArrayList interfaceNodes=this.getInterfaceNodes();
	 * while (indexIN<interfaceNodes.size()){ InterfaceNodeRecord
	 * temINR=(InterfaceNodeRecord) interfaceNodes.get(indexIN); while
	 * (iConnectedBayeslets<temINR.getConnectedBayeslets().size()){ BayesletInfo
	 * tempBI= temINR.getConnectedBayeslets().get(iConnectedBayeslets);
	 * 
	 * if (tempBI.getNodeName().equals(name)){
	 * temINR.getConnectedBayeslets().remove(iConnectedBayeslets); }else{
	 * iConnectedBayeslets++; }
	 * 
	 * 
	 * } indexIN++; } }
	 */
}
