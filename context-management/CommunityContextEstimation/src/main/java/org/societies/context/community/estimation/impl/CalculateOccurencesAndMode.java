package org.societies.context.community.estimation.impl;

import java.awt.List;
import java.util.ArrayList;
import java.util.Hashtable;


public class CalculateOccurencesAndMode {
	public ArrayList CalculateAttributeStatistics(List attrList) {


	      Hashtable <String, Integer> frequencyMap = new Hashtable<String, Integer>();
	      ArrayList<String> finalList = new ArrayList<String>();


	      String mode;
	      int max=0;


	      for (int i=0; i<attrList.size(); i++){
	         if (finalList.contains(attrList.get(i))){
	            int elementCount =Integer.parseInt(frequencyMap.get(attrList.get(i)).toString());
	            elementCount++;
	            frequencyMap.put(attrList.get(i), elementCount);

	            if (elementCount>max){
	               max=elementCount;
	               mode=attrList.get(i);
	            }

	         }
	         else
	         {
	            finalList.add(attrList.get(i));
	            frequencyMap.put(attrList.get(i), 1);
	         }
	      }

	// Epistrefoume to frequencyMap h to mode ... o,ti theloume ...

	   }

}
