package org.societies.context.user.refinement.test;

import java.io.File;


public class TestConverterHuginBIF {


//	private static String pathName = "C:\\Dokumente und Einstellungen\\fran_ko\\Desktop"+File.separator;
//	private static String inputfilename = "asia2.net";
//	private static String outputfilename = "asia2.net.xml";	

	private static String pathName = "resources"+File.separator+"learnt"+File.separator;
	private static String inputfilename = "input_tab_separated.csv.net";
	private static String outputfilename = "input_tab_separated.csv.xml";	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ((args.length == 2)) {

			inputfilename = args[0];
			outputfilename = args[1];

		}
		else if ((args.length == 3)) {
			pathName = args[2];
			if (!pathName.endsWith(File.separator)) pathName+=File.separator;
			
			inputfilename = pathName+args[0];
			outputfilename = pathName+args[1];

		}

		String informat = inputfilename.substring(inputfilename
				.lastIndexOf(".") + 1, inputfilename.length());
		String outformat = outputfilename.substring(outputfilename
				.lastIndexOf(".") + 1, outputfilename.length());

		System.out.println("informat = " + informat);
		System.out.println("outformat = " + outformat);

		if (informat.equals(outformat)) {
			System.out.println("Input format equals output format. - Exiting!");
			System.exit(0);
		}

		ConverterHuginBIF converter = ConverterHuginBIF.getInstance(pathName+inputfilename, pathName+outputfilename);
		converter.convert();

	}

}
