package org.societies.context.user.refinement.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.BayesEngine;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.tools.DataFileFilter;
import org.societies.context.user.refinement.impl.tools.NetworkConverter;

/**
 * 
 * Datafiles:
 * RVnames containing:
 * 	- AOOG: Allow Only OutGoing arcs from this RV
 *  - DNAOG: Don't Allow OutGoing arcs from this RV
 *  - _HIERARCHYx where x is an integer >= zero. A node can have an arc to another iff it has lower or equal hierarchy
 * 
 * @author fran_ko
 *
 */
public class MainTestLearning {

	
	
	public static Logger logger = LoggerFactory.getLogger(MainTestLearning.class);
	static File[] files;

	
	private static boolean multipleFiles = false;
	static boolean naiveBayes = false;
	static double learningMinutes = 1.5 * 60;
	static int maxNumberParentsPerNode = 5;

	private static String inputfilePathName = "resources";//+File.separator+"ActivityQuantizationFiles";
	private static String filePattern = ".*(25).*(Groundtruth)\\..*";
	private static String learntfilePathName = inputfilePathName + File.separator + "learnt";

	private static String learningFile = "sarah.txt";//celldata-discrete_customBinned_onlyStressGT_AOOG.txt";//celldata-discrete_customBinned_onlyStressGT.txt";
	private static String cause = "Play_Tennis";
	
	//OUTPUT configuration
	private static final boolean GENIE = false;
	private static final boolean SERIALISATION = false;
	private static final boolean LAYOUTTXTFILE = false;
	private static final boolean CONVERSION2BIF = false;



	public static void main(String[] args) {
//		System.out.println("log_e(10^4)="+Math.log(10000000));
//		for (int i=1;i<10000000;i*=2){
//			int nodesModifiedByRestart = 1 + (int) Math.round(17*Math.pow((Math.log(i)-2)/17,2));
//			System.out.println(i + "\t=>\t"+nodesModifiedByRestart);
//		}
//		System.exit(0);
		
		int learningMillis = (int)learningMinutes*60000;
		try {			
			BayesEngine engine = BayesEngine.getInstance();		
			
			DAG dag = null;
			if (multipleFiles){
				File dir = new File(inputfilePathName);
				
				//files = dir.listFiles(new ActivityDataFileFilter(Pattern.compile("[(Elena)|(Cristina)].*(SampUpd_25_Best_Features_Groundtruth)\\..*")));
				//files = dir.listFiles(new ActivityDataFileFilter(Pattern.compile("(Cristina).*(SampUpd_25_Best_Features_Groundtruth)\\..*")));
				files = dir.listFiles(new DataFileFilter(Pattern.compile(filePattern )));
				if (logger.isDebugEnabled())
					for (File f:files)
						System.out.println(f.getName());

				dag = engine.learnDAGFromDataFiles(files, learningMillis, naiveBayes, cause, maxNumberParentsPerNode);
			}
			else{
				File f = new File(inputfilePathName + File.separator + learningFile );
	//			File f = new File("resources/speaking/2se1_talking_learning_1600_800");

				// learns the network structure and parameters from the given files
				dag = engine.learnDAGFromDataFiles(new File[]{f}, learningMillis, naiveBayes, cause, maxNumberParentsPerNode);
	//			dag = engine.learnDAGFromDataFile(f, learningMinutes*60000, maxNumberParentsPerNode);
			}
			logger.info("Learning finished.");

			if (GENIE){
				NetworkConverter.javaToGenie(learntfilePathName + File.separator +learningFile + ".xdsl", dag);
				logger.info("Genie written.");
			}
			

			FileOutputStream fos = null;
			FileWriter fw = null;

			if (LAYOUTTXTFILE){
				String network = learntfilePathName + File.separator +learningFile + "_layout.txt";
				try {
					fw = new FileWriter(network);
					dag.printToFile(fw);
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				logger.info("Network Layout written.");	
			}

			if (SERIALISATION){
				ObjectOutputStream out = null;
				String filename = learntfilePathName + File.separator +learningFile + ".ser";
				try
				{
					fos = new FileOutputStream(filename);
					out = new ObjectOutputStream(fos);
					out.writeObject(dag);
					out.close();
					fos.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
				logger.info("DAG serialised.");		
			}
			
			if(CONVERSION2BIF){
				System.out.println("You have chosen the option to convert your created Genie (\".xdsl\") file to XML-BIF (\".xml\", as e.g. for Weka).");
				System.out.println("To this end, save the created Genie file within Genie as \".net\".");
				System.out.println("Press <ENTER> when you have created the file.\nI will assume it to have the same name as the Genie file with modified extension and to be in the same folder");
				System.out.println("To skip this step, press <Ctrl+Z>.");
				
			    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			    String s;
			    if ((s = in.readLine()) != null){
			      ConverterHuginBIF converter = ConverterHuginBIF.getInstance(learntfilePathName + File.separator +learningFile+".net", learntfilePathName + File.separator +learningFile+".xml");
			      converter.convert();
			      logger.info("BIF file produced and stored.");	
			    }
			    // Ctrl-Z terminates the program
			    else {
			    	logger.info("No BIF file produced, as chosen.");
			    }
			}
			
			//System.out.println(dag); //-> This will print the network in the console and after you can export it to hugin.
			//DAG dag = engine.learnDAGFromDataFiles(files, 30000, naiveBayes, "activity",18);
			
			/*
			BufferedReader evidence  = null;
			
			try{
				//It prints the network into a file called activityNW.txt
				FileWriter filewriter= new FileWriter("resources/activityNW.txt");
				dag.printToFile(filewriter);
				fw.close();
				
				ConcatReader multipleFiles = new ConcatReader();
				ConcatReader multipleFilesGround = new ConcatReader();
				
				//Files to inference, it contains the Evidence.
				//String regexpression = "[(Elena)|(Cristina)].*(SampUpd_25_Best_Features";
				String regexpression = "(Elena).*(SampUpd_25_Best_Features";
				files = dir.listFiles(new DataFileFilter(Pattern.compile(regexpression+"_Evidence)\\..*")));
				File[] filesG = dir.listFiles(new DataFileFilter(Pattern.compile(regexpression+"_Groundtruth)\\..*")));
				for (File file:files){
					System.out.println(file.getName()+"leke");
				}
				for (File file:filesG){
					System.out.println(file.getName()+"lekeG");
				}
				if (files.length == 0){
					System.out.print("0");
				}
				for(File file:files) multipleFiles.addReader(new FileReader(file));
				multipleFiles.lastReaderAdded();

				for(File file:filesG) multipleFilesGround.addReader(new FileReader(file));
				multipleFilesGround.lastReaderAdded();			

				evidence = new BufferedReader(multipleFiles);
				SimpleInference.infer(dag, evidence); // It gives an output file with the result of the classification
				
				// BufferedReader groundtruth  = new BufferedReader(multipleFilesGround); // It will create a buffered reader for the class that will do the confusion matrix
				// clasifEval.evaluateClassifier(groundtruth); // It will create a file with the confusion matrix values
				
				multipleFiles.close();
				multipleFilesGround.close();
				evidence.close();
				
			}
			catch(IOException e){
				e.printStackTrace();
			}
			finally{
				evidence.close();
			}*/
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
