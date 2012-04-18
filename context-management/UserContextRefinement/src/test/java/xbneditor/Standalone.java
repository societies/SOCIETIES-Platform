package xbneditor;

import java.lang.System;

/**
  * This class provides the ability for the Viewer to be run in a Standalone
  * version.  It make it easy to do component testing.
  *
  * @author Laura Kruse
  * @version 1.0
  */
public class Standalone {
	/**
	  * Creates a standalone instance of the class, ignoring any command
	  * line arguements that were passed in.
	  */
	public static void main(String[] args) {
		new XBN();
	}
}
