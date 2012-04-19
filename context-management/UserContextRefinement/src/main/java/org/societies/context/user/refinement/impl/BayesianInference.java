package org.societies.context.user.refinement.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.context.model.CtxQuality;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.JunctionTree;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.springframework.beans.factory.annotation.Autowired;

public class BayesianInference// implements ICtxRefiner<BayesianRule> {
{

	/**
	 * The Context Broker service reference.
	 * 
	 * @see {@link #setCtxBroker(ICtxBroker)}
	 */
	@Autowired(required = true)
	protected ICtxBroker broker;

	/**
	 * Sets the Context Broker service reference
	 * 
	 * @param ctxBroker
	 *            the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.broker = ctxBroker;
	}
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String readFileAsString(String filePath) throws java.io.IOException {
		InputStream stream = null;
		BufferedInputStream f = null;
		StringBuffer file = new StringBuffer();
		try {
			// logger
			// .debug("org/personalsmartspaces/cm/reasoning/proximity/impl/"+relativePathOSGI+"diffusionConfiguration.properties");
			stream = this.getClass().getResourceAsStream(filePath);
			logger.debug("loaded file = " + stream);
			byte[] buffer = new byte[(int) new File(filePath).length()];

			f = new BufferedInputStream(stream);

			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = f.read(contents)) != -1)
				file.append(new String(contents, 0, bytesRead));
		} catch (IOException e) {
			logger.error("Error while reading File\n" + e.getLocalizedMessage());
		} finally {
			if (f != null)
				try {
					f.close();
					stream.close();
				} catch (IOException ignored) {
				}
		}
		return new String(file);
	}

	/* hardcoded rules
	private DAG getTaskRule() {
		// DAG taskRule = GenieJava.genieToJava("TaskRule.xdsl");

		String ruleString = "";
		try {
			ruleString = readFileAsString("/TaskRule.xdsl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		DAG result = new DAG();
		
		
		result.setRule(ruleString);
		ArrayList<String> outputs = new ArrayList<String>();
		outputs.add("Task");
		ArrayList<String> inputs = new ArrayList<String>();
		inputs.add("Activity");
		inputs.add("AgendaSlot");
		inputs.add("Microphone");
		// inputs.add(CtxAttributeTypes.SYMBOLIC_LOCATION);

		result.setInputs(inputs);
		result.setOutputs(outputs);
		return result;
	}

	private DAG getStatusRule() {

		String ruleString = "";
		try {
			ruleString = readFileAsString("/AvailabilityRule.xdsl");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		DAG result = new DAG();
		result.setRule(ruleString);
		ArrayList<String> outputs = new ArrayList<String>();
		outputs.add("status");
		ArrayList<String> inputs = new ArrayList<String>();
		inputs.add("activity");
		inputs.add("Caller");
		inputs.add("Used_Services");
		inputs.add("Time");
		inputs.add("Working_Day");
		inputs.add("Movement");
		inputs.add("symLoc");
		inputs.add("Microphone");

		result.setInputs(inputs);
		result.setOutputs(outputs);
		return result;
	}
	*/

	public Collection<CtxAttribute> eval(CtxAttribute inferredAttribute,
			DAG graph) {
		JunctionTree jtree = new JunctionTree(graph);
		jtree.initialiseJTree();

		//get Input types:
		Node[] inputArray = graph.getNodes();
		HashSet<String> inputs = new HashSet<String>();
		
		for (Node n:inputArray){
			if (!n.getName().equals(inferredAttribute.getType()))
				inputs.add(n.getName());
		}
		
		boolean evidenceAvailable = false;

		HashMap<String, Node> nodeMap = new HashMap<String, Node>();
		Node[] nodes = graph.getNodes();
		for (int i = 0; i < nodes.length; i++) {
			nodeMap.put(nodes[i].getName().toLowerCase(), nodes[i]);
		}

		/* add Evidence */
		CtxEntityIdentifier target = inferredAttribute.getScope();
		Collection<CtxAttribute> temp = null;
		CtxAttribute ctxAttribute = null;
		CtxQuality iqual = null;

		if (inputs != null && inputs.size() != 0)
			for (String ctxType : inputs) {
				
				//TODO get all evaluation inputs from rule
				//TODO try to retrieve them from the broker or inference manager.
//				temp = broker.getEvaluationInputs(target, ctxType);
//				if (temp == null || temp.size() == 0) {
//					try {
//						temp = caller.getEvaluationInputs(broker
//								.retrieveDevice().getCtxIdentifier(), ctxType);
//						logger.debug("From device entity: " + temp);
//					} catch (CtxException e) {
//						e.printStackTrace();
//					}
//				}
				if (temp != null && temp.size() != 0) {
					ctxAttribute = temp.iterator().next(); // we assume there is
															// only one. if
															// there are
															// several, the
															// first one will be
															// used
					Serializable value = null;
					if (ctxAttribute != null) {
						value = ctxAttribute.getStringValue();
						if (value == null)
							try {
								//TODO BLOB handling
								//getBlobValue(this.getClass().getClassLoader()); 
								value = (String) ctxAttribute.getStringValue();
							} catch (Exception e) { // was: CtxException e) {
								logger.debug("Error retrieving Blob value for attribute "
										+ ctxType);
							}
						if (value == null)
							continue;

						iqual = ctxAttribute.getQuality();
						boolean found = false;
						if (iqual != null && iqual.getPrecision() != null
								&& !(iqual.getPrecision() > 1)) {
							found = jtree.addEvidence(nodeMap.get(ctxAttribute
									.getType().toLowerCase()),
									value.toString(), iqual.getPrecision());
							if (found)
								logger.debug("Adding soft Evidence for "
										+ ctxAttribute.getType().toLowerCase()
										+ ": " + value.toString()
										+ ", precision=" + iqual.getPrecision());
						} else {
							found = jtree
									.addEvidence(nodeMap.get(ctxAttribute
											.getType().toLowerCase()), value
											.toString());
							if (found)
								logger.debug("Adding hard Evidence for "
										+ ctxAttribute.getType().toLowerCase()
										+ ": " + value.toString());
						}

						if (found)
							evidenceAvailable = true;
					}
				}
			}
		/* evidence added */

		if (!evidenceAvailable)
			jtree.propagate();

		HashSet<CtxAttribute> result = new HashSet<CtxAttribute>();

		Probability marginalisation = jtree.getMarginalized(
				inferredAttribute.getType(), 1);
		String resultValue = marginalisation.getStates()[0];
		inferredAttribute.setStringValue(resultValue);
		inferredAttribute.setSourceId("BayesianInference");
		iqual = inferredAttribute.getQuality();
		iqual.setOriginType(CtxOriginType.INFERRED);
		iqual.setPrecision(marginalisation.getProbability());

		logger.debug("inferred Attribute value = "
				+ inferredAttribute.getStringValue());

		result.add(inferredAttribute);
		return result;
	}

}
