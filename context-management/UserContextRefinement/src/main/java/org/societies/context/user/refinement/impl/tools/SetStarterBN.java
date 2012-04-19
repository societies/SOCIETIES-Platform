package org.societies.context.user.refinement.impl.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BayesianNetworkCandidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;

/**
 * SetStarterBN is an auxiliary class.
 * It sets a network described in a file as a starting candidate for learning. 
 * This file has to be created beforehand by the method "printToFile"
 * of the DAG class. 
 * This class allows as well to set a Naive Bayes structure as startingPoint.
 * 
 * @author vera_ma, fran_ko
 * 
 */
public class SetStarterBN {

	private static Logger logger = LoggerFactory.getLogger(SetStarterBN.class);
	
	public static BayesianNetworkCandidate setNaiveBayesStarterBN(Map<String, RandomVariable> rvMap,
			BayesianNetworkCandidate starterBN, String cause) {
		RandomVariable causeRV = rvMap.get(cause);

		for (RandomVariable effect : rvMap.values()) {
			if (effect != causeRV)
				starterBN.addArc(effect, causeRV);
		}
	
		return starterBN;
	}

	/**
	 * IMPLEMENT THE BN from the File as STARTER BAYESIAN NETWORK!
	 * 
	 * @param rvMap
	 * @param starterBN
	 * @param filenameStarterNetwork Absolute name of the file containing the layout of the BN, produced by DAG.printToFile(FileWriter) 
	 */
	public static BayesianNetworkCandidate setStarterBN(Map<String, RandomVariable> rvMap,
			BayesianNetworkCandidate starterBN, String filenameStarterNetwork) {

		String causeRVName;
		String effectRVName;
		String line;
		int counter = 0;

		try {
			FileReader freader = new FileReader(filenameStarterNetwork);
			BufferedReader in = new BufferedReader(freader);

			while ((line = in.readLine()) != null) {
				if (counter > rvMap.size() + 2) { // We do not care
													// about the lines
													// of the length of
													// the probability
													// table and the two
													// lines off.
					// We are starting to read the network structure
					int k = counter - 2 - rvMap.size();
					logger.debug(line);
					if (k % 2 == 0) {
						logger.debug("The cause - effect RV are: " + line);
						String[] variables = line.split("( ---> )");
						causeRVName = variables[0];
						effectRVName = variables[1];
						logger.debug("The cause: "
									+ causeRVName + " The effect: "
									+ effectRVName);
						
						RandomVariable causeRV = rvMap.get(causeRVName);
						RandomVariable effectRV = rvMap
								.get(effectRVName);
						starterBN.addArc(effectRV, causeRV);
					}

				}
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(""+e);
		}
	
		return starterBN;
	}


	/**
	 * IMPLEMENT THE BN from the layout-String as STARTER BAYESIAN NETWORK!
	 * 
	 * @param rvMap
	 * @param starterBN
	 * @param layout
	 * 
	 */
	public static BayesianNetworkCandidate setStarterBN(String layout, Map<String, RandomVariable> rvMap,
			BayesianNetworkCandidate starterBN) {

		String causeRVName;
		String effectRVName;
		String line;
		int counter = 0;

		try {
			StringReader freader = new StringReader(layout);
			BufferedReader in = new BufferedReader(freader);

			while ((line = in.readLine()) != null) {
				if (counter > rvMap.size() + 2) { // We do not care
													// about the lines
													// of the length of
													// the probability
													// table and the two
													// lines off.
					// We are starting to read the network structure
					int k = counter - 2 - rvMap.size();
					logger.debug(line);
					if (k % 2 == 0) {
						logger.debug("The cause - effect RV are: " + line);
						String[] variables = line.split("( ---> )");
						causeRVName = variables[0];
						effectRVName = variables[1];
						logger.debug("The cause: "
									+ causeRVName + " The effect: "
									+ effectRVName);
						
						RandomVariable causeRV = rvMap.get(causeRVName);
						RandomVariable effectRV = rvMap
								.get(effectRVName);
						starterBN.addArc(effectRV, causeRV);
					}

				}
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(""+e);
		}
	
		return starterBN;
	}


	/**
	 * IMPLEMENT THE BN from the layout-String as STARTER BAYESIAN NETWORK!
	 * 
	 * @param rvMap
	 * @param starterBN
	 * @param layout
	 * 
	 */
	public static BayesianNetworkCandidate setStarterBN(Map<String, RandomVariable> rvMap,
			BayesianNetworkCandidate starterBN, DAG bbn) {

		String causeRVName;
		String effectRVName;
		
		if (logger.isDebugEnabled())
			for (RandomVariable rv: rvMap.values())
				logger.debug(rv.getName());

		for (Edge e: bbn.getEdges()){
			causeRVName = e.getSource().getName();
			effectRVName = e.getTarget().getName();
			
			logger.debug("The cause: "
						+ causeRVName + " The effect: "
						+ effectRVName);
			
			RandomVariable causeRV = rvMap.get(causeRVName);
			if (causeRV == null) logger.error("Cause "+causeRVName+" returns null as RV.");
			RandomVariable effectRV = rvMap.get(effectRVName);
			if (effectRV == null) logger.error("Cause "+effectRVName+" returns null as RV.");

			starterBN.addArc(effectRV, causeRV);
		}
		
		return starterBN;
	}
}
