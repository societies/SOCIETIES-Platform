
package org.societies.personalisation.socialprofiler.datamodel.utils;

import org.neo4j.graphalgo.impl.centrality.CostDivider;

public class DoubleDivider implements CostDivider<Double> {

	//@Override
	public Double divideByCost(Double arg0, Double cost) {
		return arg0/cost;
	}

	//@Override
	public Double divideCost(Double cost, Double arg1) {
		return cost/arg1;
	}

}
