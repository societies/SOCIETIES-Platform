package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueTextNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;


public class SimpleRandomVariable implements RandomVariable,Serializable{

	private int[] nodeRange; 
	private String name;
	private Map<String,Integer> range;
	private Map<IntegerContainer,String> rangeIntToText;
	private Map<IntegerContainer,Integer> inverseRangeIntToRangePosition;
	private IntegerContainer tmpIntegerContainer;
	private int storedHash;
	protected boolean doesNotAllowOutgoingArrows;
	protected boolean allowsOnlyOutgoingArrows;
	/**
	 * 0 means highest priority
	 */
	protected int hierarchy; // 0 means highest priority


	
	/**
	 * Creates a new SimpleRandomVariable called name
	 * Range values must be zero or positive
	 * @param name the name of the SimpleRandomVariable
	 * @param range a Map<String,Integer> with the name of the range value mapped to the integer value
	 */
	public SimpleRandomVariable(String name, Map<String,Integer> range) {
		this.doesNotAllowOutgoingArrows= false;
		this.hierarchy = 0;
		this.allowsOnlyOutgoingArrows=false;
		this.name = name;
		this.storedHash = this.name.hashCode();
		this.range = new HashMap<String,Integer>();
		this.inverseRangeIntToRangePosition = new HashMap<IntegerContainer,Integer>();
		this.rangeIntToText = new HashMap<IntegerContainer,String>();
		this.tmpIntegerContainer = new IntegerContainer(0);
		
		if (range!=null) this.buildTables(range); // if needed for case when called with range=null from other constructor
	}
	
	/**
	 * Creates a new SimpleRandomVariable called name, with range values ordered according to
	 * the passed Vector "values". The range values as integers will be numbered from
	 * baseRangeValue ... values.size()+baseRangeValue-1 accordingly
	 * @param name the name of the SimpleRandomVariable
	 * @param baseRangeValue the starting value for the numbering of the range, must be >= 0
	 * @param values a Vector of Strings with the names of the range values 
	 */
	public SimpleRandomVariable(String name, int baseRangeValue, Vector<String> values) {
		this(name, baseRangeValue, (String[]) values.toArray(new String[0]));
	}

	/**
	 * Creates a new SimpleRandomVariable called name, with range values ordered according to
	 * the passed Vector "values". The range values as integers will be numbered from
	 * baseRangeValue ... values.size()+baseRangeValue-1 accordingly
	 * @param name the name of the SimpleRandomVariable
	 * @param baseRangeValue the starting value for the numbering of the range, must be >= 0
	 * @param values an array of Strings with the names of the range values 
	 */
	public SimpleRandomVariable(String name, int baseRangeValue, String[] values) {
		this(name, null);
		Map<String,Integer> tmpMap = new HashMap<String,Integer>();
		int counter = baseRangeValue;
		
		for (int i=0;i<values.length;i++) {
			tmpMap.put(values[i], new Integer(counter++));
		}
		this.buildTables(tmpMap);
	}
	
	
	private void buildTables(Map<String, Integer> range) {
//		this.nodeRange = new int[range.size()+1];
//		this.range.putAll(range);
//		this.nodeRange[0] = -1;
//		this.range.put("NOOP", new Integer(-1));
//		this.rangeIntToText.put(new IntegerContainer(-1), "NOOP");
//		this.inverseRangeIntToRangePosition.put(new IntegerContainer(-1), new Integer(0));
//		
//		String[] rangeTextArray = (String[]) range.keySet().toArray(new String[0]);
//		for (int i=0;i<rangeTextArray.length;i++) {
//			String rangeValueName = rangeTextArray[i];
//			this.nodeRange[i+1] = ((Integer) range.get(rangeValueName)).intValue();
//	//		System.out.println("i: " + i + " " + rangeValueName + " " + range.get(rangeValueName).intValue());
//
//			this.rangeIntToText.put(new IntegerContainer(this.nodeRange[i+1]), rangeValueName);
//			this.inverseRangeIntToRangePosition.put(new IntegerContainer(this.nodeRange[i+1]), new Integer(i+1));
//		}
		
		this.nodeRange = new int[range.size()];
		this.range.putAll(range);
				
		String[] rangeTextArray = (String[]) range.keySet().toArray(new String[0]);
		for (int i=0;i<rangeTextArray.length;i++) {
			String rangeValueName = rangeTextArray[i];
			this.nodeRange[i] = ((Integer) range.get(rangeValueName)).intValue();
			this.rangeIntToText.put(new IntegerContainer(this.nodeRange[i]), rangeValueName);
			this.inverseRangeIntToRangePosition.put(new IntegerContainer(this.nodeRange[i]), new Integer(i));
		}		
		
	}	
	
	public int[] getNodeRange() {
		return this.nodeRange;
	}

	public int getNodeRangePositionFromValue(int nodeValue) throws NodeValueIndexNotInNodeRangeException {
		synchronized (this.tmpIntegerContainer) {
			this.tmpIntegerContainer.setIntegerValue(nodeValue);
			
			Integer ret = (Integer) this.inverseRangeIntToRangePosition.get(this.tmpIntegerContainer);
			if (ret==null) throw new NodeValueIndexNotInNodeRangeException("\nValue: " + nodeValue+ " is not in this RV: "+ this.toString());
			return ret.intValue();
		}
	}

	public String getNodeValueText(int nodeValue) throws NodeValueIndexNotInNodeRangeException {
		synchronized (this.tmpIntegerContainer) {
			this.tmpIntegerContainer.setIntegerValue(nodeValue);
			
			String ret = (String) this.rangeIntToText.get(this.tmpIntegerContainer);
			if (ret==null) throw new NodeValueIndexNotInNodeRangeException(" Value: " + nodeValue+ " is not in this RV: "+ this.toString());
			return ret;
		}
	}

	public String getName() {
		return this.name;
	}

	public int getNodeValueFromText(String nodeValueText) throws NodeValueTextNotInNodeRangeException {
		
		Integer ret = (Integer) this.range.get(nodeValueText);
		if (ret==null) throw new NodeValueTextNotInNodeRangeException("\n Value: " + nodeValueText+ " is not in this RV: "+ this.toString());
		return ret.intValue();
	}
	
	public boolean equals (Object o) {
		if (o instanceof RandomVariable) {
			return ((RandomVariable) o).getName().equals(this.getName());
		}
		return false;
	}
	
	public int hashCode () {
		return this.storedHash;
	}	
	
	public int compareTo(RandomVariable arg0) {
		return this.getName().compareTo(((RandomVariable) arg0).getName());
	}	
	
	public String toString() {
		return this.getName() + "/Rg:" + this.nodeRange.length;
	}	
	
	public String toStringLong() {
		return this.getName() + "/Rg:" + this.nodeRange.length + " Ranges: \n " + this.range;
	}	
	
	public boolean doesNotAllowOutgoingArrows() {
		return this.doesNotAllowOutgoingArrows;
	}

	public void setDoesNotAllowOutgoingArrows(boolean doesNotAllowOutgoingArrows) {
		if (doesNotAllowOutgoingArrows) this.allowsOnlyOutgoingArrows = false;
		this.doesNotAllowOutgoingArrows = doesNotAllowOutgoingArrows;
	}

	public int getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(int hierarchy) {
		this.hierarchy = hierarchy;
	}

	public boolean allowsOnlyOutgoingArrows() {
		return allowsOnlyOutgoingArrows;
	}

	public void setAllowsOnlyOutgoingArrows(boolean allowsOnlyOutgoingArrows) {
		if (allowsOnlyOutgoingArrows) this.doesNotAllowOutgoingArrows = false;
		this.allowsOnlyOutgoingArrows = allowsOnlyOutgoingArrows;
	}
	
	
	private class IntegerContainer implements Serializable{


		private long intValue;
		
		/**
		 * @param i
		 */
		public IntegerContainer(int intValue) {
			this.intValue = intValue;
		}
		/**
		 * @return Returns the intValue.
		 */
		public long getIntegerValue() {
			return this.intValue;
		}
		/**
		 * @param intValue The intValue to set.
		 */
		public void setIntegerValue(int intValue) {
			this.intValue = intValue;
		}
		
		public boolean equals (Object obj) {
			if (obj instanceof IntegerContainer) {
				return this.intValue == ((IntegerContainer) obj).getIntegerValue();
			}
			return false;
		}
		
		public int hashCode() {
			return (int) this.intValue;
		}
		
	}


}
