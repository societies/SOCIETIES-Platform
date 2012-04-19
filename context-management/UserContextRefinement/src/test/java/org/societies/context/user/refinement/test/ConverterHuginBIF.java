package org.societies.context.user.refinement.test;

import xbneditor.FileIO;

public class ConverterHuginBIF {

	public static FileIO convertor;

	public final String GENIE_HUGIN = ".net";
	public final String BIF = ".xml";


	private String inputfilename;
	private String outputfilename;

	private static ConverterHuginBIF instance;

	private FileIO fileIO;

	private ConverterHuginBIF(String inputfilename, String outputfilename){
		this.inputfilename = inputfilename;
		this.outputfilename = outputfilename;
		fileIO = new FileIO();
	}

	public static ConverterHuginBIF getInstance(String inputfilename, String outputfilename){
		if (instance!=null) return instance;
		return new ConverterHuginBIF(inputfilename, outputfilename);
	}
	
	
	public void convert(){
		fileIO.convert(outputfilename, fileIO.load(inputfilename), outputfilename.substring(outputfilename.lastIndexOf(".")+1));
	}
}
