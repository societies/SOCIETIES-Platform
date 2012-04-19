package org.societies.context.user.refinement.test;

import java.io.File;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.BayesEngine;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.tools.DataFileFilter;
import org.societies.context.user.refinement.impl.tools.NetworkConverter;

public class ImportGenieIntoLearning {
	private static Logger logger = LoggerFactory.getLogger(ImportGenieIntoLearning.class);
	
	private static String inputfilePathName = "resources";//+File.separator+"ActivityQuantizationFiles";
	private static String filePattern = ".*(25).*(Groundtruth)\\..*";
	private static String genieFile = "celldata-discrete_customBinned_onlyStressGT_weka.xdsl";//learnt/celldata-discrete.csv.xdsl";
	private static String learningFile = "celldata-discrete_customBinned_onlyStressGT_AOOG.txt";//celldata-discrete.csv";
	
	
	//learning parameters
	private static boolean multipleFiles = false;
	private static boolean structureLearning = false;
	private static int maxNumberParentsPerNode = 5;
	private static int milliseconds = 1000 * 60 * 2;
	
	public static void main(String[] args){
		System.out.println("Take care that logging for 'de.dlr.kn.bayesianLibrary.bayesianLearner.impl.BayesLearnerImpl=INFO'.\nOtherwise the logScore is not shown!");
		DAG loadedBN = NetworkConverter.genieToJava(inputfilePathName +File.separator+ genieFile);
		/*
		StringWriter fakeFileWriter = new StringWriter();
		try {
			genie.printToFile(fakeFileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String networkLayout = fakeFileWriter.toString();
		
		logger.info(networkLayout);
		*/
		
		File[] input = new File[]{new File(inputfilePathName +File.separator+ learningFile)};

		if (multipleFiles){
			File dir = new File(inputfilePathName);
			input = dir.listFiles(new DataFileFilter(Pattern.compile(filePattern )));
			if (logger.isDebugEnabled())
				for (File f:input)
					System.out.println(f.getName());
		}
		
		BayesEngine learner = BayesEngine.getInstance();
		DAG neu = learner.learnDAGFromFilesGivenDAGStructure(input, milliseconds, maxNumberParentsPerNode, structureLearning, loadedBN);
		
		//logger.info(neu);


	}

}
