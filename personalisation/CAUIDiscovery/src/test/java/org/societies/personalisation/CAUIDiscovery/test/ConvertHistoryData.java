package org.societies.personalisation.CAUIDiscovery.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.personalisation.CAUIDiscovery.impl.MockHistoryData;

public class ConvertHistoryData {


	public ConvertHistoryData(){

	}


	/*
	protected List<MockHistoryData> convertHistoryData (Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> mapHocData){

		List<MockHistoryData> result = new ArrayList<MockHistoryData>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> ctxHocTuples = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>(); 
		
		for(CtxHistoryAttribute primaryHocAttr: ctxHocTuples.keySet()){
			
			String primaryCtxValue = primaryHocAttr.getStringValue();
			List<CtxHistoryAttribute> listHocAttrs = ctxHocTuples.get(primaryHocAttr);
			//assume that only one escorting context object exists 
			CtxHistoryAttribute escortingHocAttr = listHocAttrs.get(0);
			String escortingHocAttrValue = escortingHocAttr.getStringValue();
			
			MockHistoryData mockHocData = new MockHistoryData(primaryCtxValue,escortingHocAttrValue);
			result.add(mockHocData);
		}
		
		return result;
	}
*/
}
