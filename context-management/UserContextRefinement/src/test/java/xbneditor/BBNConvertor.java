package xbneditor;

import java.lang.System;

/**
 * Title:        xbneditor
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company: KSUKDD
 * @author Haipeng Guo
 * @version 1.0
 */

 class BBNConvertor {

  public static void main(String[] args) {
    if((args.length==0)|| (args.length>2)){System.out.println("Usage: Java BBNConvertor inputfilename outputfilename ...");}

    String inputfilename = args[0];
    String outputfilename = args[1];
    String informat = inputfilename.substring(inputfilename.indexOf(".")+1, inputfilename.length());
    String outformat = outputfilename.substring(outputfilename.indexOf(".")+1, outputfilename.length());

    System.out.println("informat = " + informat);
    System.out.println("outformat = " + outformat);
    if(informat.equals(outformat))
      { System.out.println("Come on, no joke...");
    } else {
      FileIO fileIO = new FileIO();
      fileIO.convert(outputfilename, fileIO.load(inputfilename), outformat);
    }
  }
}