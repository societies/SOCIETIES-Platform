package org.societies.context.community.estimation.impl;


public class Range {
	
	public int[] range(ArrayList<int> numbers) {

	      r = new int[2];
	// Arkoun to min kai to max.


	      int min= Integer.MAX_VALUE;
	      int max = Integer.MIN_VALUE;


	// Ekfylismenh periptosh exoume an einai mono ena noumero ... allios kanonika ...

	  
	      for (int i=0; i<numbers.size(); ++i){
	         if (numbers.get(i) < min){
	            min=numbers.get(i);
	         }
	         if (numbers.get(i) > max){
	            max=points.get(i);
	         }
	      }

	      r[0]=min;
	      r[1]=max;


	      return {r};
	   }
}

