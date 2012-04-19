package xbneditor;

import java.io.*;
import java.util.*;

/**
  * This is a class that takes in a generated Bayesian network and a gold
  * standard bayesian network and will look for different graph error
  * calculations that have been found within them.
  *
  * @author Laura Kruse
  * @version v1.0
  */
public class CountGraphErrors {
	private LinkedList currentbn;

	private int[][] goldstandard;
	private int[][] current;

	private Hashtable nodenames;
	private Hashtable mapped;

	private String filename;

	private int size;

	/**
	  * This takes in the current Bayesian network and the filename of
	  * the gold standard network so that the generated network can be
	  * compared to the gold standard network for errors.
	  *
	  * @param currentbn the Bayesian Network that was generated
	  * @param filename the name of the file in which the gold standard
	  * 	network is stored
	  */
	public CountGraphErrors(LinkedList currentbn, String filename) {
		//System.out.println("LinkedList, String constructor");
		this.currentbn = currentbn;
		this.filename = filename;

		size = currentbn.size();

		goldstandard = new int[size][size];
		current = new int[size][size];

		nodenames = new Hashtable();
		mapped = new Hashtable();

		populateHash();
	}

	/**
	  * This function takes in two file names.  One is the generated
	  * bayesian network and the other is the gold standard bayesian
	  * network.
	  *
	  * @param fncurrent the generated bayesian network
	  * @param filename the gold standard bayesian network
	  */
	public CountGraphErrors(String fncurrent, String filename) {
		//System.out.println("String, String Constructor");
		//System.out.println("K2:  " + fncurrent);
		//System.out.println("GS:  " + filename);
		FileIO file;

		file = new FileIO();

		currentbn = file.load(fncurrent);
		this.filename = filename;

		size = currentbn.size();
		//System.out.println("size = " + size);

		goldstandard = new int[size][size];
		current = new int[size][size];

		nodenames = new Hashtable();
		mapped = new Hashtable();

		populateHash();
	}

	/**
	  * This function locates all of the names in the network and puts them
	  * into a hash table so that it can be easily determined where a
	  * node is in the comparison array.
	  */
	private void populateHash() {
		String name;

		for(int i=0;i<size;i++) {
			name = ((Item) currentbn.get(i)).getItem().getBlockName();
			nodenames.put(name, new Integer(i));
			mapped.put(new Integer(i), name);
		}

		getBNStandard();
		getGoldStandard();
	}

	/**
	  * This function retrieves the generated bayesian network and stores
	  * the location of the arcs and direction in an array.
	  */
	private void getBNStandard() {
		Item item;

		String name1;
		String name2;

		int num1;
		int num2;

		int parents;

		for(int i=0;i<size;i++) {
			item = (Item) currentbn.get(i);
			name1 = item.getItem().getBlockName();

			num1 = ((Integer) nodenames.get(name1)).intValue();

			parents = item.numParents();

			for(int j=0;j<parents;j++) {
				name2 = item.getParent(j).getItem().getBlockName();
				num2 = ((Integer) nodenames.get(name2)).intValue();
				current[num1][num2] = 1;
			}
		}

		//return current;
	}

	/**
	  * This function retireves the gold standard bayesian network and
	  * puts the location of the arcs of the nodes into an array.
	  */
	private void getGoldStandard() {
		String content;

		if(filename != null) {
			content = readGSFile(filename);

			// look for the different file format cases here
			// decide what function to parse them based on this
			// this should return a populated table
			if(content.indexOf("BIF VERSION=\"0.3\"") > 0) {
				getXMLGoldStandard(content, true);
			} else if(content.indexOf("<BIF>") > 0) {
				getXMLGoldStandard(content, false);
			} else {
				getBIFGoldStandard(content);
			}
		}

		//return goldstandard;
	}

	private String readGSFile(String filename) {
		StringBuffer sbuf;
		BufferedReader infile;

		sbuf = new StringBuffer();

		try {
			infile = new BufferedReader(new FileReader(filename));

			while(infile.ready()) {
				sbuf.append(infile.readLine());
			}
		} catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return sbuf.toString();
	}

	private void getXMLGoldStandard(String sbuf, boolean currentVer) {
		int startDefinition;
		int endDefinition;

		if(currentVer) {
			startDefinition = sbuf.indexOf("<DEFINITION>");
			endDefinition = sbuf.indexOf("</DEFINITION>");
		} else {
			startDefinition = sbuf.indexOf("<PROBABILITY>");
			endDefinition = sbuf.indexOf("</PROBABILITY>");
		}

		while(startDefinition > 0) {
			getXMLGoldStandardNames(sbuf.substring(startDefinition, endDefinition));

			if(currentVer) {
				startDefinition = sbuf.indexOf("<DEFINITION>", endDefinition);
				endDefinition = sbuf.indexOf("</DEFINITION>", startDefinition);
			} else {
				startDefinition = sbuf.indexOf("<PROBABILITY>", endDefinition);
				endDefinition = sbuf.indexOf("</PROBABILITY>", startDefinition);
			}
		}
	}

	private void getBIFGoldStandard(String sbuf) {
		int startDefinition;
		int endDefinition;

		startDefinition = sbuf.indexOf("probability (");
		endDefinition = sbuf.indexOf("}", startDefinition);

		while(startDefinition > 0) {
			getBIFGoldStandardNames(sbuf.substring(startDefinition, endDefinition));

			startDefinition = sbuf.indexOf("probability (", endDefinition);
			endDefinition = sbuf.indexOf("}", startDefinition);
		}
	}

	private void getXMLGoldStandardNames(String sbuf) {
		int startFor;
		int endFor;

		int startGiven;
		int endGiven;

		String name1;
		String name2;

		int num1;
		int num2;

		startFor = sbuf.indexOf("<FOR>");
		endFor = sbuf.indexOf("</FOR>");

		name1 = sbuf.substring(startFor + 5, endFor);
		num1 = ((Integer) nodenames.get(name1)).intValue();

		startGiven = sbuf.indexOf("<GIVEN>", endFor);
		endGiven = sbuf.indexOf("</GIVEN>", startGiven);

		while(startGiven > startFor) {
			name2 = sbuf.substring(startGiven + 7, endGiven);
			num2 = ((Integer) nodenames.get(name2)).intValue();

			// do something to put the 1 or 0 in the table here
			goldstandard[num1][num2] = 1;

			startGiven = sbuf.indexOf("<GIVEN>", endGiven);
			endGiven = sbuf.indexOf("</GIVEN>", startGiven);
		}
	}

	private void getBIFGoldStandardNames(String sbuf) {
		int startFor;
		int endFor;

		int startGiven;
		int endGiven;

		String name1;
		String name2;
		String tmp;

		int num1;
		int num2;

		startFor = sbuf.indexOf("(") + 1;
		endFor = sbuf.indexOf(")", startFor) - 1;

		tmp = trimexcess(sbuf.substring(startFor, endFor));

		if((startGiven = tmp.indexOf(" ")) > 0) {
			name1 = tmp.substring(0, startGiven);
			tmp = trimexcess(tmp.substring(startGiven));

			if((endGiven = tmp.indexOf(" ")) < 0) {
				endGiven = tmp.length();
			}
		} else {
			name1 = tmp;
			endGiven = -1;
		}

		num1 = ((Integer) nodenames.get(name1)).intValue();

		while(startGiven > 0) {
			name2 = tmp.substring(0, endGiven);
			num2 = ((Integer) nodenames.get(name2)).intValue();

			goldstandard[num1][num2] = 1;

			tmp = trimexcess(tmp.substring(endGiven));
			endGiven = tmp.indexOf(" ");

			if(endGiven < 0 && tmp.length() <= 0) {
				startGiven = -1;
			} else if(endGiven < 0) {
				endGiven = tmp.length();
			}
		}
	}

	private String trimexcess(String sbuf) {
		sbuf = sbuf.replace('"', ' ');
		sbuf = sbuf.replace('|', ' ');
		sbuf = sbuf.replace(',', ' ');

		return sbuf.trim();
	}

	public String countErrorAddition() {
		StringBuffer ret;
		ret = new StringBuffer();

		for(int i=0;i<size;i++) {
			for(int j=i+1;j<size;j++) {
				if(current[i][j] + current[j][i] > goldstandard[i][j] + goldstandard[j][i]) {
					if(current[i][j] == 1) {
						ret.append(mapped.get(new Integer(j)) + "->" + mapped.get(new Integer(i)) + " ");
					} else {
						ret.append(mapped.get(new Integer(i)) + "->" + mapped.get(new Integer(j)) + " ");
					}
				}
			}
		}

		//System.out.println("Addition:  " + ret);
		return ret.toString();
	}

	public String countErrorDeletion() {
		StringBuffer ret;
		ret = new StringBuffer();

		for(int i=0;i<size;i++) {
			for(int j=i+1;j<size;j++) {
				if(current[i][j] + current[j][i] < goldstandard[i][j] + goldstandard[j][i]) {
					if(goldstandard[i][j] == 1) {
						ret.append(mapped.get(new Integer(j)) + "->" + mapped.get(new Integer(i)) + " ");
					} else {
						ret.append(mapped.get(new Integer(i)) + "->" + mapped.get(new Integer(j)) + " ");
					}
				}
			}
		}

		//System.out.println("Deletion:  " + ret);
		return ret.toString();
	}

	public String countErrorReversal() {
		StringBuffer ret;
		ret = new StringBuffer();

		for(int i=0;i<size;i++) {
			for(int j=i+1;j<size;j++) {
				if((current[i][j] == goldstandard[j][i] && goldstandard[j][i] == 1) || (current[j][i] == goldstandard[i][j] && goldstandard[i][j] == 1)) {
					if(current[i][j] == goldstandard[j][i] && goldstandard[j][i] == 1) {
						ret.append(mapped.get(new Integer(j)) + "->" + mapped.get(new Integer(i)) + " ");
					} else {
						ret.append(mapped.get(new Integer(i)) + "->" + mapped.get(new Integer(j)) + " ");
					}
				}
			}
		}

		//System.out.println("Reversal:  " + ret);
		return ret.toString();
	}
}
