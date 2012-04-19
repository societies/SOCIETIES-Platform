package org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.exceptions.BayesletDAGIncompatibilityException;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils.CliqueBLOperations;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Clique;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Separator;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.UndirectedEdge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.Bayeslet;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.BayesletInfo;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.CliqueBL;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.InterfaceNodeRecord;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.NodeBL;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.ProbabilityDistributionBL;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;
import org.societies.context.user.refinement.impl.tools.ArrayListConverter;

public class BayesletJTree extends JunctionTree {
	static boolean screenEnabled = false;

	public BayesletJTree(DAG dag) {
		super(dag);
	}

	// ///////////////////////////////////////////////////////////
	// ////////////////////Added by Pablo/////////////////////////
	// ///////////////////////////////////////////////////////////

	/**
	 * @author gall_pa Empty constructor, necessary to create the combined JT in
	 *         the third approach.
	 */
	public BayesletJTree() {
		super();
	}

	/**
	 * @author gall_pa This method combines the parent cliques corresponding to
	 *         nodes called sourceNodeName and targetNodeName. It is used in the
	 *         third approach to construct the combined junction tree.
	 * 
	 * @param sourceNodeName
	 * 
	 * @param targetNodeName
	 * @throws BayesletDAGIncompatibilityException 
	 */

	public void combineCliques(String sourceNodeName, String targetNodeName) throws BayesletDAGIncompatibilityException {
		int index = 0;
		NodeBL targetNode = CliqueBLOperations.searchNode(targetNodeName,
				new ArrayList<HasProbabilityTable>(this.getNodes().values()));

		Clique tempClique = targetNode.getParentClique();
		if (!(tempClique instanceof CliqueBL))
			throw new BayesletDAGIncompatibilityException();

		CliqueBL targetClique = (CliqueBL) tempClique;
		ArrayList<NodeBL> nodesTargetClique = new ArrayList<NodeBL>(Arrays
				.asList((NodeBL[]) targetClique.getParticipants()));

		NodeBL sourceNode = CliqueBLOperations.searchNode(sourceNodeName,
				new ArrayList<HasProbabilityTable>(this.getNodes().values()));
		Clique sourceClique = sourceNode.getParentClique();

		// First step: combine both nodes.
		ProbabilityDistribution pede = targetNode.getProbTable();
		if (!(pede instanceof ProbabilityDistributionBL))
			throw new BayesletDAGIncompatibilityException();
		((ProbabilityDistributionBL) pede).combineProbabilityDistribution(
				sourceNode, targetNode);

		// Second step: transfer edges from sourceNode to targetNode
		CliqueBLOperations.transferEdges(sourceNode, targetNode, this
				.getEdges());
		CliqueBLOperations.transferUndirectedEdges(sourceNode, targetNode, this
				.getEdges());

		// Updating name of target node.
		targetNode.updateName(sourceNodeName);

		/*
		 * Testing
		 * System.out.println("Probability tables combination result: \n");
		 * System.out.println(targetNode.getProbTable().toString());
		 */

		/*
		 * Third step: add all nodes contained in the parent clique from the
		 * source node (except the source node) to the parent clique from the
		 * target node. (The source node is not added because its probabilistic
		 * information and edges are already represented by the node resulting
		 * of the combination of the source node and the target node.
		 */
		index = 0;
		ArrayList<NodeBL> nodesSourceClique = new ArrayList<NodeBL>(Arrays
				.asList((NodeBL[]) sourceClique.getParticipants()));// CliqueBLOperations.arrayToArrayList(());
		nodesSourceClique.remove(sourceNode);

		// Updating parent clique of nodes that are going to be added to the
		// target clique.
		// Those whose parent clique was the source clique, will be now assigned
		// the target clique as their parent clique.
		while (index < nodesSourceClique.size()) {
			NodeBL temp = (NodeBL) nodesSourceClique.get(index);
			if (temp.getParentClique() == sourceClique)
				temp.setParentClique(targetClique);
			index++;
		}

		nodesTargetClique.addAll(nodesSourceClique);
		targetClique.addNodes(nodesTargetClique);

		/*
		 * Fourth step: Connecting all nodes inside the combined clique. This
		 * way we are ensuring the clique is complete: this means that every
		 * pair of distinct nodes are connected by an edge.
		 */

		for (int i = 0; i < nodesTargetClique.size(); i++) {
			Node node1 = (Node) nodesTargetClique.get(i);
			for (int j = i + 1; j < nodesTargetClique.size(); j++) {
				Node node2 = (Node) nodesTargetClique.get(j);
				UndirectedEdge u = new UndirectedEdge(node1, node2);
				if (!getEdges().contains(u)) {
					getEdges().add(u);
					node1.addConnection(u);
					node2.addConnection(u);
				}

			}
		}

		/*
		 * Fifth step: Transfer all separators that were previously connected to
		 * the source clique (the parent clique from the source node) to the
		 * target clique (namely the parent clique from the target node). The
		 * source node and the source clique are removed.
		 */

		getNodes().remove(sourceNode.getName());
		// targetClique.addCombinedCliqueName("+"+sourceClique.getName());

		updateCliques(sourceNode, targetNode, ArrayListConverter
				.<CliqueBL, Clique> convertToSubclass(getCliquesArrayList()));
		transferSeparators(sourceClique, targetClique);
		getCliquesArrayList().remove(sourceClique);
	}

	/**
	 * This method modifies all cliques containing the source node, so they will
	 * contain the target node instead.
	 * 
	 * @param sourceNode
	 * @param targetNode
	 * @param inputCliques
	 */

	public void updateCliques(NodeBL sourceNode, NodeBL targetNode,
			ArrayList<CliqueBL> inputCliques) {

		int indexCliques = 0;

		int indexNodes = 0;

		while (indexCliques < inputCliques.size()) {

			indexNodes = 0;
			CliqueBL tempClique = inputCliques.get(indexCliques);
			NodeBL[] tempRepNodes = (NodeBL[]) tempClique.getParticipants();

			while (indexNodes < tempRepNodes.length) {

				if (tempRepNodes[indexNodes].getName().contains(
						sourceNode.getName())) {

					tempRepNodes[indexNodes] = targetNode;

					tempClique.updateClique(tempRepNodes);

					// updateSeparatorLabel(sourceNode,targetNode,tempRepNodes[])

				}

				indexNodes++;
			}

			indexCliques++;
		}
	}

	/**
	 * This method transfer the separators from one clique to another. Every
	 * separator that was connected to the source clique will be redirected to
	 * the target clique.
	 * 
	 * @author gall_pa
	 * @param sourceClique
	 * @param targetClique
	 */
	public void transferSeparators(Clique sourceClique, Clique targetClique) {

		int indexSeparators = 0;

		ArrayList<Separator> tempSep = this.getSeparatorsArrayList();

		// It goes through all separators related to the source clique

		while (indexSeparators < tempSep.size()) {

			Separator temp = (Separator) tempSep.get(indexSeparators);

			if (temp.getBorder1() == sourceClique) {

				// Creates a new separator changing border 2
				Separator newS = new Separator(targetClique, (Clique) temp
						.getBorder2());

				targetClique.addConnection(newS);

				temp.getBorder2().removeConnection(temp);
				temp.getBorder2().addConnection(newS);

				// Updates the separators contained in the clique
				getSeparatorsArrayList().remove(temp);
				getSeparatorsArrayList().add(newS);

				if (screenEnabled) {
					System.out
							.println("----------Separator CHANGE -----------------------------------------------------------");
					System.out.println(temp.toString() + " changes to \n"
							+ newS.toString());
					System.out
							.println("--------------------------------------------------------------------------------------");
				}
			} else if (temp.getBorder2() == sourceClique) {

				// Creates a new separator changing border 1
				Separator newS = new Separator((Clique) temp.getBorder1(),
						targetClique);

				targetClique.addConnection(newS);
				temp.getBorder1().removeConnection(temp);
				temp.getBorder1().addConnection(newS);

				// Updates the separators contained in the clique
				getSeparatorsArrayList().remove(temp);
				getSeparatorsArrayList().add(newS);

				if (screenEnabled) {
					System.out
							.println("----------Separator CHANGE -----------------------------------------------------------");
					System.out.println(temp.toString() + "\n changes to \n"
							+ newS.toString());
					System.out
							.println("--------------------------------------------------------------------------------------");
				}
			}

			indexSeparators++;
		}

	}

	/**
	 * This method adds all cliques and separators correponding to bayeslets
	 * contained in bayeslets[] to the common JT. It also adds their nodes and
	 * their edges. It is the first step when constructing the combined junction
	 * tree in the third approach.
	 * 
	 * @param bayeslets
	 */
	public void addCliqNodEdSep(Bayeslet bayeslets[]) {
		int indexBayeslets = 0;
		BayesletJTree tempJT;
		ArrayList<CliqueBL> tempCliques;

		while (indexBayeslets < bayeslets.length) {
			
			tempJT = bayeslets[indexBayeslets].getJunctionTree();
			
			tempCliques = CliqueBLOperations.arrayToArrayList((CliqueBL[]) tempJT
					.getCliques());
			
			getCliquesArrayList().addAll((ArrayList) tempCliques.clone());
			
			getNodes().putAll((HashMap<String,HasProbabilityTable>)tempJT.getNodes().clone());
			getEdges().addAll((ArrayList) tempJT.getEdges().clone());
			getSeparatorsArrayList().addAll(
					(ArrayList) tempJT.getSeparatorsArrayList().clone());
			indexBayeslets++;
		}

	}

	/**
	 * This method combines those cliques that are defined as the parent cliques
	 * from those interface nodes whose connections have to be updated. It is
	 * the second step when constructing the combined junction tree in the third
	 * approach.
	 * 
	 * @param bayeslets
	 * @throws BayesletDAGIncompatibilityException 
	 */
	public void updateConnections(Bayeslet bayeslets[]) throws BayesletDAGIncompatibilityException {

		int indexBayeslets = 0;
		int indexInterfaceNodes = 0;
		int indexConnectedBayeslets = 0;
		ArrayList tempIncomingInterfaceNodes;

		while (indexBayeslets < bayeslets.length) {

			tempIncomingInterfaceNodes = bayeslets[indexBayeslets]
					.getIncomingInterfaceNodes();
			indexInterfaceNodes = 0;

			while (indexInterfaceNodes < tempIncomingInterfaceNodes.size()) {
				// Goes through all incoming interface nodes.
				InterfaceNodeRecord tempINR = (InterfaceNodeRecord) tempIncomingInterfaceNodes
						.get(indexInterfaceNodes);

				if (tempINR.getToBeUpdatedNode()) {

					ArrayList<BayesletInfo> tempConnectedBayeslets = tempINR
							.getConnectedBayeslets();
					indexConnectedBayeslets = 0;

					while (indexConnectedBayeslets < tempConnectedBayeslets
							.size()) {
						// Goes through all bayeslets connected to interface
						// node represented by .
						BayesletInfo tempBI = ((BayesletInfo) tempConnectedBayeslets
								.get(indexConnectedBayeslets));
						String nodeName = tempBI.getNodeName();

						// Combines the corresponding cliques
						this.combineCliques(nodeName, tempINR.getName());

						indexConnectedBayeslets++;
					}

					tempINR.setToBeUpdatedNode(false);
				}
				indexInterfaceNodes++;
			}
			indexInterfaceNodes = 0;
			indexBayeslets++;
		}

		this.initialiseJTree();
		this.propagate();

	}

	// ////////////////////////////////////////////////////
	// ////////////////////NOT USED SO FAR/////////////////
	// ////////////////////////////////////////////////////
	/**
	 * This method dissolves the parent clique from the node called nodeName.
	 * 
	 * @param nodeName
	 */
	/*
	 * public void dissolveCombinedClique(String nodeName,Bayeslet[]
	 * bayeslets,Bayeslet[] backUpBayeslets){
	 * 
	 * ArrayList allDissolvedNodes=new ArrayList(); int indexCliquesNames=0; int
	 * indexNodeNames=0; ArrayList tempNodes= this.getNodes(); Node
	 * targetNode=CliqueOperations.searchNode(nodeName, tempNodes); Clique
	 * targetClique=targetNode.getParentClique();
	 * 
	 * this.getCliquesArrayList().remove(targetClique);
	 * 
	 * String[] cliqueNodesNames=targetClique.getName().split("/");
	 * 
	 * while (indexNodeNames<cliqueNodesNames.length){
	 * 
	 * String tempName=cliqueNodesNames[indexNodeNames];
	 * 
	 * Node tempNode=CliqueOperations.searchNode(tempName,tempNodes); String[]
	 * bayesletAndNode=tempName.split(":"); String
	 * bayesletName=bayesletAndNode[0]; Bayeslet
	 * current=CliqueOperations.searchBayeslet(bayesletName, bayeslets);
	 * Bayeslet backUp=CliqueOperations.searchBayeslet(bayesletName,
	 * backUpBayeslets);
	 * 
	 * if
	 * ((tempName.contains("-"))&&(tempNode.getParentClique().equals(targetClique
	 * ))){ allDissolvedNodes.addAll(this.dissolveCombinedNodeClique(tempName,
	 * bayeslets, backUpBayeslets));
	 * 
	 * } indexNodeNames++; } int indexADN=0; System.out.println("Pasa o q");
	 * 
	 * while (indexADN<allDissolvedNodes.size()){
	 * 
	 * Node temp=(Node) allDissolvedNodes.get(indexADN); String[]
	 * tempIDBLNode=temp.getName().split(":"); String
	 * bayesletName=tempIDBLNode[0]; Bayeslet
	 * backUpBayeslet=CliqueOperations.searchBayeslet(bayesletName,
	 * backUpBayeslets); Node backUpNode =
	 * backUpBayeslet.getNode(temp.getName());
	 * 
	 * Clique backUp = backUpNode.getParentClique(); Node[]
	 * tempPar=backUp.getParticipants(); Node[] newCliqueNodes=new
	 * Node[tempPar.length]; int indexPar=0;
	 * 
	 * while (indexPar<tempPar.length){ Node tempNode=tempPar[indexPar]; Node
	 * newNode=CliqueOperations.searchNode(tempNode.getName(),this.getNodes());
	 * newCliqueNodes[indexPar]=newNode; indexPar++; }
	 * 
	 * Clique newClique=new Clique(newCliqueNodes); this.cliques.add(newClique);
	 * 
	 * ArrayList tempSep=backUp.getUndirectedEdges();
	 * 
	 * 
	 * 
	 * 
	 * indexADN++; }
	 * 
	 * 
	 * indexNodeNames=0;
	 * 
	 * while (indexNodeNames<cliqueNodesNames.length){
	 * 
	 * String tempName=cliqueNodesNames[indexNodeNames]; Node
	 * tempNode=CliqueOperations.searchNode(tempName,tempNodes); Clique
	 * tempParent=tempNode.getParentClique();
	 * 
	 * 
	 * if (tempParent.equals(targetClique)){
	 * 
	 * String[] bayesletAndNode=tempName.split(":");
	 * 
	 * String bayesletName=bayesletAndNode[0];
	 * 
	 * Bayeslet current=CliqueOperations.searchBayeslet(bayesletName,
	 * bayeslets); Bayeslet backUp=CliqueOperations.searchBayeslet(bayesletName,
	 * backUpBayeslets);
	 * 
	 * //this.resetClique(tempParent,current,backUp); } indexNodeNames++; }
	 * 
	 * }
	 * 
	 * /*public void resetClique(Clique currentClique,Bayeslet current,Bayeslet
	 * backupBayeslet){
	 * 
	 * String cliqueName=currentClique.getName(); ArrayList
	 * backUpCliques=backupBayeslet.getJunctionTree().getCliquesArrayList(); int
	 * backUpCliquePos= CliqueOperations.searchPosition(cliqueName,
	 * backUpCliques); Clique backUpClique=(Clique)
	 * backUpCliques.get(backUpCliquePos); }
	 */
	/*
	 * 
	 * public void transferEdgesBackUp(Node backUpNode, Node target){
	 * 
	 * ArrayList combinedEdges=this.getEdges();
	 * 
	 * ArrayList combinedNodes=this.getNodes();
	 * 
	 * 
	 * int index=0; Edge tempEdge; Edge newEdge; ArrayList tempIncoming;
	 * ArrayList tempOutgoing; tempIncoming=backUpNode.getIncoming();
	 * 
	 * while (index<tempIncoming.size()){
	 * tempEdge=(Edge)tempIncoming.get(index); String
	 * tempSourceName=tempEdge.getSource().getName(); Node
	 * edgeSource=CliqueOperations.searchNode(tempSourceName, combinedNodes);
	 * newEdge=new Edge(edgeSource,target);
	 * //tempEdge.getSource().removeOutgoing(tempEdge);
	 * 
	 * //target.addIncoming(newEdge); combinedEdges.add(newEdge);
	 * combinedEdges.remove(tempEdge); index++; } index=0;
	 * 
	 * tempOutgoing=backUpNode.getOutgoing();
	 * 
	 * while (index<tempOutgoing.size()){
	 * tempEdge=(Edge)tempOutgoing.get(index); String
	 * tempTargetName=tempEdge.getTarget().getName(); Node
	 * edgeTarget=CliqueOperations.searchNode(tempTargetName, combinedNodes);
	 * newEdge=new Edge(target,edgeTarget);
	 * //tempEdge.getTarget().removeIncoming(tempEdge);
	 * 
	 * combinedEdges.add(newEdge); combinedEdges.remove(tempEdge); index++; }
	 * 
	 * }
	 * 
	 * public ArrayList dissolveCombinedNodeClique(String name,Bayeslet[]
	 * bayeslets,Bayeslet[] backUpBayeslets){
	 * 
	 * ArrayList dissolvedNodes=new ArrayList(); ArrayList
	 * combinedNodes=this.getNodes(); Node
	 * toBeRemoved=CliqueOperations.searchNode(name, combinedNodes);
	 * 
	 * combinedNodes.remove(toBeRemoved);
	 * 
	 * String[]combinedNodeNames=toBeRemoved.getName().split("-"); int
	 * indexCombinedNodes=0;
	 * 
	 * while(indexCombinedNodes<combinedNodeNames.length){
	 * 
	 * String nodeName=combinedNodeNames[indexCombinedNodes]; String[]
	 * bayesletAndNode=combinedNodeNames[indexCombinedNodes].split(":"); String
	 * bayesletName=bayesletAndNode[0];
	 * 
	 * Bayeslet current=CliqueOperations.searchBayeslet(bayesletName,
	 * bayeslets); Bayeslet backUp=CliqueOperations.searchBayeslet(bayesletName,
	 * backUpBayeslets);
	 * 
	 * InterfaceNodeRecord tempINR=current.getInterfaceNodeInfo(nodeName);
	 * 
	 * dissolvedNodes.add(this.resetNodeClique(nodeName,backUp,current,tempINR.
	 * isIncomingInterfaceNode()));
	 * 
	 * tempINR.setToBeUpdated(true);
	 * 
	 * indexCombinedNodes++; } return (dissolvedNodes); }
	 * 
	 * public Node resetNodeClique(String connectedNodeName, Bayeslet
	 * backUpBayeslet,Bayeslet current,boolean incomingInterfaceNode){
	 * 
	 * 
	 * Node backUpNode= backUpBayeslet.getNode(connectedNodeName); Node newNode=
	 * new Node(backUpNode.getName(),backUpNode.getStates());
	 * 
	 * CliqueOperations.transferEdgesBackUp(backUpNode, newNode,
	 * this.getNodes(),this.getEdges()); Probability[] temp =
	 * backUpNode.getProbTable().getProbabilities();
	 * newNode.setProbDistribution(
	 * backUpNode.getProbTable().getProbabilities());
	 * 
	 * Node currentNode = current.getNode(connectedNodeName);
	 * 
	 * currentNode.setName(connectedNodeName);
	 * currentNode.setProbDistribution(backUpNode
	 * .getProbTable().getProbabilities());
	 * 
	 * if (incomingInterfaceNode) {
	 * CliqueOperations.transferEdgesBackUp(backUpNode, currentNode,
	 * current.getDAG().getNodesArrayList(),current.getDAG().getNodesArrayList
	 * ()); }
	 * 
	 * this.getNodes().add(newNode); return (newNode); }
	 */

	/**
	 * It adds cliques to the set of cliques contained in the junction tree.
	 * 
	 * @param newCliques
	 *            ArrayList containing the cliques to be added.
	 */
	/*
	 * public void addCliques(ArrayList newCliques){
	 * 
	 * int index=0; while(index<newCliques.size()){
	 * this.cliques.add(newCliques.get(index)); index++; }
	 * 
	 * }
	 */

	/**
	 * This method adds a separator to the set of cliques contained in the
	 * junction tree.
	 * 
	 * @param newSeparator
	 */

	/*
	 * 
	 * public void addSeparator(Separator newSeparator){
	 * 
	 * this.separators.add(newSeparator);
	 * 
	 * }
	 */

	/**
	 * This method adds a set of separators to the set of separator contained in
	 * the junction tree.
	 * 
	 * @param input
	 */
	/*
	 * public void addSeparators(ArrayList input){
	 * 
	 * int index=0; while(index<input.size()){
	 * this.separators.add(input.get(index)); index++; } }
	 */
	/*
	 * public void addEdges(ArrayList input){ this.edges.addAll(input); }
	 */
	/*
	 * public void addNodes(ArrayList input){ nodes.addAll(input); }
	 */

	/**
	 * This method modifies the label from a separator, exchanging the source
	 * node with the target node.
	 * 
	 * @param sourceNode
	 * @param targetNode
	 * @param input
	 */

	/*
	 * public void updateSeparatorLabel(Node sourceNode, Node targetNode,Node[]
	 * input){
	 * 
	 * ArrayList inputA=CliqueOperations.arrayToArrayList(input); Node
	 * temp=CliqueOperations.searchNode(sourceNode.getName(),inputA);
	 * temp=targetNode; input=CliqueOperations.convertToArray(inputA);
	 * 
	 * }
	 */
}
