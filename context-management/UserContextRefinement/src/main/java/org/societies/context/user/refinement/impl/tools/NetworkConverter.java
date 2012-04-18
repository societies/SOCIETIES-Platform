package org.societies.context.user.refinement.impl.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.PriorAndCountTablesMismatchException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.ConditionalProbabilityTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.ConnectingNodes;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 
 * This class integrates methods to perform the conversion from GeNIe Files to
 * DAG object, and vice versa.
 * 
 * @author jime_ca
 * 
 */
public class NetworkConverter {
	private static final Logger log4j = LoggerFactory
			.getLogger(NetworkConverter.class);

	private static final String SMILE_VERSION = "1.0";
	private static final String NUM_SAMPLES = "1000";
	private static final String DISC_SAMPLES = "10000";
	private static final String XML_ENCODING = "ISO-8859-1";

	// *** Layout constant
	private static final String NODE_DEFAULT_COLOR = "e5f6f7";
	private static final String NODE_DEFAULT_OUTLINE_COLOR = "000080";
	private static final String NODE_FONT_COLOR = "000000";
	private static final String NODE_FONT_NAME = "Arial";
	private static final String NODE_FONT_SIZE = "8";
	private static final String NODE_POSITION = "420 260 450 300";

	// *** End Layout constant

	
	/**
	 * Creates a Java object of type
	 * de.dlr.kn.bayesianLibrary.inference.structures.impl.DAG from a file,
	 * representing a serialised object, produced with an ObjectOutputStream.
	 * 
	 * @param filenameNetwork
	 *            name of the file (including path) of the object stored with an
	 *            ObjectOutputStream. E.g. with the extension ".ser".
	 */
	public static DAG recoverNetwork(String filenameNetwork) {
		DAG net = null;
		FileInputStream fis = null;
		ObjectInputStream input = null;

		try {
			fis = new FileInputStream(filenameNetwork);
			log4j.debug("fis=" + fis + " network-filename=" + filenameNetwork);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			input = new ObjectInputStream(fis);
			try {
				net = (DAG) input.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return net;
	}

	/**
	 * <br/>
	 * Transforms Probabilities from {@link DAG}-order to Genie-order<br/>
	 * <br/>
	 * original probabilities order (DAG): 0.9 0.8 0.7 0.6 0.1 0.2 0.3 0.4<br/>
	 * final probabilities order (genie): 0.9 0.1 0.8 0.2 0.7 0.3 0.6 0.4 <br/>
	 * 
	 * @param probTable
	 *            {@link probabilityDistribution} object from the {@link DAG}
	 *            library
	 * @param currentNodeActivityNumber
	 *            {@link integer} object with the number of states of the
	 *            current node
	 * 
	 * @return the probabilities in a "genie order" from a
	 *         {@link ProbabilityDistribution} object
	 */
	private static String getProbabilities(ProbabilityDistribution probTable,
			int currentNodeActivityNumber) {

		String result = "";
		// Integer halfLength = probTable.getProbabilities().length/2;
		// String[] probString = new
		// String[probTable.getProbabilities().length];
		double[] prob = new double[probTable.getProbabilities().length];

		for (int i = 0; i < prob.length; i++) {
			prob[i] = probTable.getProbabilities()[i].getProbability();
		}

		prob = getProbabilities(prob, prob.length / currentNodeActivityNumber);

		// TODO eliminar este codigo cnd compruebe que funciona bien

		/*
		 * for (int i = 0; i < halfLength; i++) { probString [2*i] =
		 * Double.toString(probTable.getProbabilities()[i].getProbability());
		 * probString [2*i+1]=
		 * Double.toString(probTable.getProbabilities()[i+halfLength
		 * ].getProbability()); }
		 */
		;
		for (int i = 0; i < prob.length; i++) {
			result += prob[i] + " ";
		}
		return result;
	}

	/**
	 * <br/>
	 * Transforms Probabilities from Genie-order to {@link DAG}-order<br/>
	 * <br/>
	 * 
	 * @param genieProbArrayDouble
	 *            {@link double} array object with probability elements in a
	 *            "Genie-order"
	 * @param currentNodeActivityNumber
	 *            {@link integer} object with the number of states of the
	 *            current node
	 * @return the probabilities in a "{@link DAG}-order" from a "Genie-order"
	 *         probabilities array
	 */
	private static double[] getProbabilities(double[] genieProbArrayDouble,
			int currentNodeActivityNumber) {
		// *** Genie stores the probabilities in the following order:
		// ***
		// *** A(1) A(0)
		// *** D(0) D(1) D(0) D(1)
		// *** C(0) 0.9 0.8 0.7 0.6
		// *** C(1) 0.1 0.2 0.3 0.4
		// ***
		// ***
		// *** original probabilities order (genie): 0.9 0.1 0.8 0.2 0.7 0.3 0.6
		// 0.4 -> Table red by column
		// *** final probabilities order (DAG): 0.9 0.8 0.7 0.6 0.1 0.2 0.3 0.6
		// -> Table red by row

		// *** To change the order of the probability vector
		double[] result = new double[genieProbArrayDouble.length];

		int count = genieProbArrayDouble.length / currentNodeActivityNumber;
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < currentNodeActivityNumber; j++) {
				result[i + j * count] = genieProbArrayDouble[j
						+ currentNodeActivityNumber * i];
			}
		}
		/*
		 * for (int j = 0; j < (count); j++) {
		 * result[j]=genieProbArrayDouble[j*currentNodeActivityNumber]; }
		 */
		return result;
	}

	/**
	 * <br/>
	 * Changes from {@link String} of numbers, separated by a pattern, to an
	 * array of {@link double}<br/>
	 * <br/>
	 * original object: "1 0 0.7 0.3 ..."<br/>
	 * example pattern: " "<br/>
	 * final object: {1, 0, 0.7, 0.3 ...}<br/>
	 * 
	 * @param nodeStringProb
	 *            {@link String} of numbers separated by a pattern
	 * @return numbers in an array of {@link double}
	 * 
	 */
	private static double[] stringNumberToDouble(String nodeStringProb,
			String pattern) {
		double[] result = null;

		Pattern stringPattern = Pattern.compile(pattern); // I define the
		// pattern to
		// recognize between
		// to numbers
		String[] nodeProbArray = stringPattern.split(nodeStringProb);
		result = new double[nodeProbArray.length];
		for (int k = 0; k < nodeProbArray.length; k++) {
			result[k] = Double.parseDouble(nodeProbArray[k]);
		}

		return result;
	}

	/**
	 * <br/>
	 * Changes from {@link String} of numbers, separated by " ", to an array of
	 * {@link double}<br/>
	 * <br/>
	 * original object: "1 0 0.7 0.3 ..."<br/>
	 * final object: {1, 0, 0.7, 0.3 ...}<br/>
	 * <br/>
	 * 
	 * @param nodeStringProb
	 *            {@link String} of numbers separated by " "<br/>
	 * @return numbers in an array of {@link double}<br/>
	 * 
	 */
	private static double[] stringNumberToDouble(String nodeStringProb) {

		final String DEFAULT_SPLIT_PATTERN = " ";
		return stringNumberToDouble(nodeStringProb, DEFAULT_SPLIT_PATTERN);
	}

	/**
	 * <br/>
	 * Auxiliary method from {@link NetworkConverter.reOrder}
	 * 
	 * @param temp
	 *            {@link ArrayList}<
	 *            {@link de.dlr.kn.bayesianLibrary.inference.structures.impl.Node}
	 *            > object which contains the new ordered object.
	 * @param node
	 *            {@link ArrayList}<
	 *            {@link de.dlr.kn.bayesianLibrary.inference.structures.impl.Node}
	 *            > object which contains the original
	 *            {@link de.dlr.kn.bayesianLibrary.inference.structures.impl.Node}
	 *            .
	 */
	private static void insert(
			ArrayList<HasProbabilityTable> temp,
			org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node node) {

		if (temp.contains(node)) {
			return;
		} else {
			ArrayList<Edge> incomingEdges = node.getIncoming();
			ArrayList<org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node> parents = new ArrayList<org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node>();

			for (Edge e : (ArrayList<Edge>) incomingEdges) {
				parents.add(e.getSource());
				log4j.debug("Parent: " + e.getSource().getName());
			}
			int numParents = parents.size();
			for (int i = 0; i < numParents; i++) {
				NetworkConverter.insert(temp, parents.get(i));
			}
			temp.add(node);
		}

	}

	/**
	 * <br/>
	 * Orders the
	 * {@link de.dlr.kn.bayesianLibrary.inference.structures.impl.Node}s from a
	 * {@link DAG} object.
	 * 
	 * @param dag
	 *            {@link DAG} object out of order.
	 * @return ordered {@link DAG} object.
	 */
	private static DAG reOrder(DAG dag) {

		ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
		int numNodes = dag.getNodes().length;
		org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node[] originalNodes = dag
				.getNodes();
		for (int i = 0; i < numNodes; i++) {
			log4j.debug(originalNodes[i].getName());
			NetworkConverter.insert(nodes, originalNodes[i]);
			log4j.debug("Temp ordering after this node");
			for (HasProbabilityTable n : nodes) {
				log4j.debug(n.getName());
			}
			log4j.debug("Temp ordering finished");
		}

		return new DAG(nodes, new ArrayList<ConnectingNodes>(Arrays.asList(dag
				.getEdges())));
	}

	/**
	 * <br/>
	 * Transforms a {@link DAG} object to a {@link Document} object<br/>
	 * 
	 * @param dag
	 *            {@link DAG} contains a bayesian network structure
	 * @return {@link Document} which contains the bayesian network included in
	 *         DAG object
	 */
	private static Document dagToDoc(DAG dag, String dagNetworkName) {

		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document result = docBuilder.newDocument();
		// ** to get the name of the XML file

		// ** create the root element and add it to the document
		Element root = result.createElement("smile");
		root.setAttribute("version", SMILE_VERSION);
		root.setAttribute("id", dagNetworkName);
		root.setAttribute("numsamples", NUM_SAMPLES);
		root.setAttribute("discsamples", DISC_SAMPLES);
		result.appendChild(root);

		// *** Layout genie information
		Element nodes = result.createElement("nodes");
		Element extension = result.createElement("extensions");
		Element genie = result.createElement("genie");

		genie.setAttribute("version", SMILE_VERSION);
		genie.setAttribute("app", "");
		genie.setAttribute("name", dagNetworkName);
		genie.setAttribute("faultnameformat", "nodestate");

		root.appendChild(nodes);
		root.appendChild(extension);
		extension.appendChild(genie);

		// *** Writing nodes information

		Edge[] edgesDAG = dag.getEdges();
		org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node[] nodesDAG = dag
				.getNodes();

		for (int i = 0; i < nodesDAG.length; i++) {

			String parentsNodes = "";

			Element cpt = result.createElement("cpt");
			cpt.setAttribute("id", nodesDAG[i].getName());

			nodes.appendChild(cpt);

			// ***layout information
			Element node = result.createElement("node");
			node.setAttribute("id", nodesDAG[i].getName());
			genie.appendChild(node);

			Element name = result.createElement("name");
			name.setTextContent(nodesDAG[i].getName());
			node.appendChild(name);

			Element interior = result.createElement("interior");
			interior.setAttribute("color", NODE_DEFAULT_COLOR);
			node.appendChild(interior);

			Element outline = result.createElement("outline");
			outline.setAttribute("color", NODE_DEFAULT_OUTLINE_COLOR);
			node.appendChild(outline);

			Element font = result.createElement("font");
			font.setAttribute("color", NODE_FONT_COLOR);
			font.setAttribute("name", NODE_FONT_NAME);
			font.setAttribute("size", NODE_FONT_SIZE);
			node.appendChild(font);

			Element position = result.createElement("position");
			position.setTextContent(NODE_POSITION);
			node.appendChild(position);

			// *** end layout information
			log4j.debug("XML layout set " + i);

			int nodesDAGNumStates = nodesDAG[i].getStates().length;

			for (int j = 0; j < nodesDAGNumStates; j++) {
				Element state = result.createElement("state");
				state.setAttribute("id", nodesDAG[i].getStates()[j].toString());
				cpt.appendChild(state);
			}
			for (int j = 0; j < edgesDAG.length; j++) {
				if (edgesDAG[j].getTarget().getName()
						.equals(nodesDAG[i].getName())) {
					parentsNodes += edgesDAG[j].getSource().getName() + " ";
				}
			}
			if (!parentsNodes.equals("")) {
				Element parents = result.createElement("parents");
				parents.setTextContent(parentsNodes);
				cpt.appendChild(parents);
			}
			Element probabilities = result.createElement("probabilities");
			// *** parents number is: number of probabilities element / number
			// of states
			probabilities.setTextContent(NetworkConverter.getProbabilities(
					nodesDAG[i].getProbTable(), nodesDAGNumStates));
			cpt.appendChild(probabilities);
			log4j.debug("CPT " + i + " " + cpt);
		}
		return result;
	}

	/**
	 * <br/>
	 * Stores a {@link Document} object in a file using a xml method for
	 * outputting the result tree<br/>
	 * 
	 * @param xmlFilePath
	 *            destination path where the xml file is going to be store
	 * @param doc
	 *            {@link Document} which contains the representation of the xml
	 *            file to store
	 */
	private static void storeData(String xmlFilePath, Document doc) {

		FileOutputStream fileOS = null;

		try {
			// Create transformer
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			// Output Types (text/xml/html)
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, XML_ENCODING);
			// transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS,"ISO-8859-1");

			// Write the document to a file
			DOMSource source = new DOMSource(doc);
			// Create File to view your xml data as
			// vk.txt/vk.doc/vk.xls/vk.shtml/vk.html)
			log4j.debug(xmlFilePath);
			File file = new File(xmlFilePath);
			if (!file.exists())
				file.createNewFile();
			fileOS = new FileOutputStream(xmlFilePath);
			Result result = new StreamResult(fileOS);
			transformer.transform(source, result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOS != null)
				try {
					fileOS.flush();
					fileOS.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * <br/>
	 * Transforms a GeNie file in a {@link DAG} object<br/>
	 * 
	 * @param pathXMLfile
	 *            path of the GeNie file
	 * @return {@link DAG} object which contains GeNie file network
	 */
	public static DAG genieToJava(String pathXMLfile) {

		ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
		ArrayList<ConnectingNodes> edges = new ArrayList<ConnectingNodes>();
		// *** Read XML file and open like a Document in order to manage it

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;

		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			document = builder.parse(new File(pathXMLfile));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("IOException");
		}

		// *** To get root node name: "smile"

		Node rootNode = document.getFirstChild();

		// *** To get bayesian network elements
		// *** the odd elements are null, I don't know why. For this reason I
		// read the next sibling
		// *** the first child

		NodeList nodesChild = rootNode.getFirstChild().getNextSibling()
				.getChildNodes();
		int numNodesChild = nodesChild.getLength();

		for (int i = 0; i < numNodesChild; i++) {

			Node currentNodeChild = nodesChild.item(i);
			int numStatesNode = 0;

			if (currentNodeChild.getNodeName() == "cpt") {

				// *** To get the name, child, states and probabilities of a
				// bayesian network element
				// *** nodeName, , ,nodeProArrayFlo

				String nodeName = new String(currentNodeChild.getAttributes()
						.item(0).getNodeValue());
				ArrayList<String> nodeStateDym = new ArrayList<String>(); // I
				// use
				// dynamic
				// structure
				// because
				// initially
				// I
				// don't
				// know
				// how
				// many
				// states
				// there
				// will
				// be
				String[] nodeParents = null;
				double[] nodeProbArrayDouble = null;

				Node tempItem;
				String nodename;

				int numNodesChildChild = currentNodeChild.getChildNodes()
						.getLength();

				for (int j = 0; j < numNodesChildChild; j++) {
					tempItem = currentNodeChild.getChildNodes().item(j);
					nodename = tempItem.getNodeName();
					if (nodename == "state") {
						nodeStateDym.add(tempItem.getAttributes().item(0)
								.getNodeValue());
						numStatesNode++;

					} else if (nodename == "parents") {
						nodeParents = tempItem.getTextContent().split(" ");
					} else if (nodename == "probabilities") {
						nodeProbArrayDouble = NetworkConverter
								.stringNumberToDouble(currentNodeChild
										.getChildNodes().item(j)
										.getTextContent());
					}
				}
				// *** to change the node states information from the dynamic
				// string array to string array

				String[] nodeState = new String[nodeStateDym.size()];
				nodeStateDym.toArray(nodeState);

				// *** Updating node information

				org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node currentNode = new org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node(
						nodeName, nodeState);
				nodes.add(currentNode);

				// *** Creating the edges, when it is necessary
				// *** It looks like Genie writes parents before child in its
				// xml file

				if (nodeParents != null) { // that means the current node has
					// parents
					for (int j = 0; j < nodeParents.length; j++) {
						for (int j2 = 0; j2 < nodes.size(); j2++) {
							if (nodes.get(j2).getName().equals(nodeParents[j])) {
								edges.add(new Edge(
										(org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node) nodes
												.get(j2), currentNode));
							}
						}
					}
				}
				currentNode.setProbDistribution(NetworkConverter
						.getProbabilities(nodeProbArrayDouble, numStatesNode));
			}
		}
		return new DAG(nodes, edges);
	}

	/**
	 * <br/>
	 * Transforms a {@link DAG} object in a GeNie file (*.xdsl)<br/>
	 * <br/>
	 * The GeNie file created has a disorganized layout. In order to have an
	 * organized layout,<br/>
	 * the xdsl file should be opened in GeNie software, the whole network
	 * should be selected and <br/>
	 * it should be clicked the following menu: <br/>
	 * <br/>
	 * Layout-> Graph Layout-> Parent Ordering
	 * 
	 * @param xmlFilePath
	 *            destination path and name of the xdsl file (GeNie file)
	 * @param dag
	 *            {@link DAG} contains a Bayesian network structure
	 */
	public static void javaToGenie(String xmlFilePath, DAG dag) {
		// ** We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = docBuilder.newDocument();

		// ** The name of the Genie Network will be the same than the XLM file
		// name

		String networkName = xmlFilePath
				.substring(xmlFilePath.lastIndexOf("\\") + 1,
						xmlFilePath.lastIndexOf("."));

		DAG reOrdered = NetworkConverter.reOrder(dag);

		log4j.debug("reordered");
		/*
		 * Attention: with a big DAG, the following call can stop the JVM!
		 */
		if (log4j.isTraceEnabled())
			log4j.trace(reOrdered.toString());

		doc = NetworkConverter.dagToDoc(reOrdered, networkName);
		log4j.debug("XML doc generated");
		NetworkConverter.storeData(xmlFilePath, doc);
	}

	/**
	 * Converts the Map of RandomVariables and ConditionalProbabilityTable,
	 * produced as output of the learning engine
	 * de.dlr.kn.bayesianLibrary.bayesianLearner.BayesEngine, to the Java object
	 * of type de.dlr.kn.bayesianLibrary.inference.structures.impl.DAG.
	 * 
	 * @param rv_cpt_map
	 * @return
	 */
	public static DAG convertStructures(
			Map<RandomVariable, ConditionalProbabilityTable> rv_cpt_map) {

		ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
		ArrayList<ConnectingNodes> edges = new ArrayList<ConnectingNodes>();

		Iterator<RandomVariable> it = rv_cpt_map.keySet().iterator();

		while (it.hasNext()) {

			RandomVariable rv = it.next();

			int[] patrickValues = rv.getNodeRange();
			String[] values = new String[patrickValues.length];
			for (int i = 0; i < patrickValues.length; i++) {
				try {
					values[i] = rv.getNodeValueText(patrickValues[i]);
				} catch (NodeValueIndexNotInNodeRangeException e) {
					e.printStackTrace();
				}
			}

			org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node fromRV = new org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node(
					rv.getName(), values);
			nodes.add(fromRV);
		}

		/* second run-through: all nodes created, now edges + CPT */
		it = rv_cpt_map.keySet().iterator();
		while (it.hasNext()) {
			RandomVariable current = it.next();
			org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node target = findNode(
					nodes, current);
			ConditionalProbabilityTable cpt = rv_cpt_map.get(current);
			SortedSet<RandomVariable> parents = cpt.getOrderedParents();

			Iterator<RandomVariable> parIt = parents.iterator();
			while (parIt.hasNext()) {
				Edge neu = new Edge(
						(org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node) findNode(
								nodes, parIt.next()),
						(org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node) target);
				edges.add(neu);
			}

			/*
			 * k is index of targetRV (current), j index of parent-configuration
			 * NOTE: INDICES begin with 1 !!!
			 */
			try {
				double[][] cpt_k_j = cpt.getProbabilityTable();
				int configurationSize = cpt_k_j[0].length - 1;
				double[] pureProbs = new double[(configurationSize)
						* (cpt_k_j.length - 1)];

				for (int k = 1; k < cpt_k_j.length; k++) {
					for (int j = 1; j < cpt_k_j[k].length; j++) {
						pureProbs[(k - 1) * configurationSize + (j - 1)] = cpt_k_j[k][j];
					}
				}
				target.setProbDistribution(pureProbs);

			} catch (PriorAndCountTablesMismatchException e) {
				log4j.error("Problems with converting CPT");
			}
		}

		return new DAG(nodes, edges);
	}

	private static org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node findNode(
			ArrayList<HasProbabilityTable> nodes, RandomVariable rv) {
		for (int i = 0; i < nodes.size(); i++) {
			HasProbabilityTable current = nodes.get(i);
			if (current.getName().equals(rv.getName()))
				return (org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node) current;
		}
		log4j.error("NOT FOUND!!!! SCANDAL!!!!");
		return null;
	}

}
