package org.societies.personalisation.socialprofiler.datamodel.utils;

import org.neo4j.graphalgo.CostAccumulator;


public class IntegerAdder implements CostAccumulator<Integer>
	{

	public Integer addCosts(Integer c1, Integer c2) {
//	            if (c1==null){logger.error("ERROR FATALLLLLL c1=null");}    
//	            if (c2==null){logger.error("ERROR FATALLLLLL c2=null");}  
	            return c1 + c2;
	}
}