package org.societies.personalisation.CAUIDiscovery.test;

import java.util.ArrayList;
import java.util.List;

public class CAUIDiscoveryLearningTest {

	List<MockHistoryData> historySet = null; 

	CAUIDiscoveryLearningTest(){

	}

	public void createHistorySet(){

		historySet = new ArrayList<MockHistoryData>();

		for(int j =0 ; j<10 ; j++){
			for(int i =0 ; i<10 ; i++){
				List<String> context = new ArrayList<String>();
				context.add("C"+i);
				String a = "A"+i;
				MockHistoryData hoc = new MockHistoryData(a,context);	
				historySet.add(hoc);
			}
		}

	}


	public void retrieveHistorySet(){

		for(MockHistoryData mockData :historySet ){
			System.out.println(mockData);
		}

		System.out.println(this.historySet.size());
	}

	public static void main(String[] args) {

		CAUIDiscoveryLearningTest cdt = new CAUIDiscoveryLearningTest();
		cdt.createHistorySet();
		cdt.retrieveHistorySet();
	}
}
