package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueTextNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.InstantiatedRV;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.JointMeasurement;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.TimeStamp;


public class SimpleJointMeasurement implements JointMeasurement{
	private static Logger log4j = LoggerFactory.getLogger(SimpleJointMeasurement.class);

	//private static final int MaxNumberOfRanges = 100;

	private TimeStamp timeStamp;
	
	Map<RandomVariable, InstantiatedRV> instantiatedRVs;

	private String strRep;
	
	public SimpleJointMeasurement() {
		this.instantiatedRVs = new HashMap<RandomVariable, InstantiatedRV>();
		this.strRep = "SimpleJointMeasurement: ";
		// TODO timeStamp init and set
	}

	public TimeStamp getTimeStamp() {
		return this.timeStamp;
	}

	public Map<RandomVariable, InstantiatedRV> getInstantiatedRV() {
		return this.instantiatedRVs;
	}

	public void add(InstantiatedRV irv) {
		this.instantiatedRVs.put(irv.getRV(), irv);
		this.strRep = this.strRep + " " + irv.toString();
	}
	
	public void addAll(Set<InstantiatedRV> irvs) {
		InstantiatedRV[] irvs_array = irvs.toArray(new InstantiatedRV[0]);
		
		for (int i=0;i<irvs_array.length;i++) {
			this.add(irvs_array[i]);
		} 
	}
	
	public String toString() {
		return this.strRep;
	}
	
	
	   /** @param filePath the name of the file to open. Not sure if it can accept URLs or just filenames. Path handling could be better, and buffer sizes are hardcoded
	    */ 
	    private static String readFileAsString(String filePath)
	    throws java.io.IOException{
	        StringBuffer fileData = new StringBuffer(1000);
	        BufferedReader reader = new BufferedReader(
	                new FileReader(filePath));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	            buf = new char[1024];
	        }
	        reader.close();
	        return fileData.toString();
	    }
	    
		   /** 
		    * @see readFileAsString
		    * First line omitted if counter>0;
		    */ 
		    private static String readFilesAsString(String filePath, int counter) throws java.io.IOException{
		        BufferedReader reader = new BufferedReader(new FileReader(filePath));
		        String result = "";
		        String temp = reader.readLine() +"\n";
		        if (counter==0) result+=temp;
		        while((temp=reader.readLine()) != null){
		        	result+=temp +"\n";
		        }
		        reader.close();
		        return result;
		    }
		
		public static SimpleJointMeasurement[] computeFromDataFile(Map<String, RandomVariable> rvmap, String filename) {
			try {
				return SimpleJointMeasurement.computeFromData(rvmap, readFileAsString(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}	
		
		public static SimpleJointMeasurement[] computeFromDataFiles(Map<String, RandomVariable> rvmap, File[] files) {
			String complete = "";
			int counter=0;
			for (File f: files){
				try {
					complete+=readFilesAsString(f.getAbsolutePath(),counter);
				} catch (IOException e) {
					e.printStackTrace();
				}
				counter++;
			}
			return SimpleJointMeasurement.computeFromData(rvmap, complete);
		}
	
	
	public static SimpleJointMeasurement[] computeFromData(Map<String, RandomVariable> rvmap, String data) {
		if (log4j.isTraceEnabled()) log4j.trace("data:\n" + data + "\ndata end");
		String[] entries = data.split("\\f|\\r|\\n",0);
		if (log4j.isDebugEnabled()) log4j.debug(""+entries.length);
		RandomVariable[] rvs = null;
		rvmap.clear();
		
		
		int counter = 0;
		String[] rvnames = null;
		SortedSet<String>[] rvranges = null;
		for (int i=0;i<entries.length;i++) {
			if (entries[i].length()>1) {
				String[] values = entries[i].split("\\t",0);
				
				if (counter==0) {
					rvnames = new String[values.length];
					if (log4j.isDebugEnabled()) log4j.debug("Counter: " + counter + " e: " + entries[i]);
					rvranges = new TreeSet[values.length];
					rvs = new RandomVariable[values.length];
					for (int j=0;j<values.length;j++) {
						rvnames[j] =values[j].trim();
						rvranges[j] = new TreeSet<String>();
						if (log4j.isDebugEnabled()) log4j.debug("getting name: " + rvnames[j]);
					}
				}
				else {
					if (log4j.isDebugEnabled()) log4j.debug("Counter: " + counter + " v: " + entries[i]);
					for (int j=0;j<values.length;j++) {
						if (values[j].trim().length()>0) rvranges[j].add(values[j].trim());
			//			System.out.println("Counter: " + counter + " v: " + values[j].trim());
					}
				}
				counter++;
			}
		}
		
		SimpleJointMeasurement[] ret = new SimpleJointMeasurement[counter-1];
		
		for (int r=0;r<rvnames.length;r++) {
	//		System.out.println("RVin: " + rvranges[r] + " " + rvnames[r]);
			SimpleRandomVariable rv = new SimpleRandomVariable(rvnames[r], 1, new Vector<String>(rvranges[r]));
			
			
			int indexTmp = rv.getName().lastIndexOf(RandomVariable.HierarchyIndicator);
			if (indexTmp > 0) {
				String levelStr = rv.getName().substring(indexTmp + RandomVariable.HierarchyIndicator.length());
				rv.setHierarchy(Integer.parseInt(levelStr));
			}
			
			if (rv.getName().lastIndexOf(RandomVariable.AllowsOnlyOutgoing)>=0) rv.setAllowsOnlyOutgoingArrows(true);
			if ((rv.getName().lastIndexOf(RandomVariable.DoesNotAllowOutgoing)>=0)) rv.setDoesNotAllowOutgoingArrows(true);
			
			rvs[r] = rv;
			rvmap.put(rvnames[r], rv);
	//		System.out.println("RV: \n" + rv);
		}
		
		
		counter = 0;
		
		for (int i=0;i<entries.length;i++) {
			if (entries[i].length()>1) {
				String[] values = entries[i].split("\\t",0);
				
				if (counter!=0) {
					ret[counter-1] = new SimpleJointMeasurement();
		//			System.out.println("2. Counter: " + counter + " v: " + entries[i]);
					for (int j=0;j<values.length;j++) {
		//				System.out.println("2. Counter: " + counter + " v: " + values[j].trim());
						SimpleInstantiatedRV sirv = null;
						try {
							boolean missing = (values[j].trim().length()<=0); 
							sirv = new SimpleInstantiatedRV(rvs[j], missing, values[j].trim());
						} catch (NodeValueTextNotInNodeRangeException e) {
							e.printStackTrace();
						}
						ret[counter-1].add(sirv);
					}
				}
				counter++;
			}
		}
		
		if (log4j.isDebugEnabled()) for (int r=0;r<ret.length;r++) log4j.debug("Result: \n" + ret[r]);
		return ret;
	}
}
