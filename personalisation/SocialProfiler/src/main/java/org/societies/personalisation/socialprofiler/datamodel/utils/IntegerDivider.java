/**
 * 
 */
package org.societies.personalisation.socialprofiler.datamodel.utils;

import org.neo4j.graphalgo.impl.centrality.CostDivider;


public class IntegerDivider implements CostDivider<Integer>{

	//@Override
	public Integer divideByCost(Double c, Integer cost) {
		if (cost!=0){
			double res=c/cost;
			return (int)res;
		}else{
			return 0;
		}
	}

	//@Override
	public Integer divideCost(Integer cost, Double c) {
		if (c!=0){
			double res=cost/c;
			return (int) res;
		}else{
			return 0;
		}
		
	}
	
}
