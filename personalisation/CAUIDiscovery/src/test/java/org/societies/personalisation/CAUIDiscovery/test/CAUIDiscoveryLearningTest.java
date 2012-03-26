package org.societies.personalisation.CAUIDiscovery.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.societies.personalisation.CAUI.api.CAUIDiscovery.ICAUIDiscovery;
import org.societies.personalisation.CAUIDiscovery.impl.CAUIDiscovery;

public class CAUIDiscoveryLearningTest {

	CAUIDiscovery discover = null;

	CAUIDiscoveryLearningTest(){
		discover = new  CAUIDiscovery();
	}

	private void  startTesting(){
		System.out.println("createHistorySet()");
		createHistorySet();
		System.out.println("retrieveHistorySet()");
		retrieveHistorySet();
		System.out.println("discover.generateNewUserModel");
		//List<MockHistoryData> historySet =null;
		discover.generateNewUserModel(createHistorySet());
		LinkedHashMap<String,Integer> results = discover.getMaxFreqSeq(7);
		System.out.println("results higher than 8 "+ results);
	}

	public List<MockHistoryData> createHistorySet(){

		List<MockHistoryData>  historySet = new ArrayList<MockHistoryData>();
		//historySet = createNormalSeq();
		historySet = createSeqArray();
		return historySet;
	}

	private List<MockHistoryData> createSeqArray(){
		List<MockHistoryData>  historySet = new ArrayList<MockHistoryData>();
		//	List<String> arrayString = new ArrayList<String>(); 
		//String charsA = "ABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEF";
		//String charsB = "ABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEF";
		//String chars = charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB+charsA+charsB;
		String chars = "ABCxewDEFjfABCawqDEFewiABCdereqABCdqwDEFABCxewDEF";
		for (int i=0 ; i< chars.length() ; i++){
			char actChar = chars.charAt(i);
			String actString = Character.toString(actChar);
			MockHistoryData mockData = new MockHistoryData(actString,null);	
			historySet.add(mockData);
		}
		return historySet;
	}

	private List<MockHistoryData> createNormalSeq(){
		List<MockHistoryData>  historySet = new ArrayList<MockHistoryData>();

		for(int j =0 ; j<1 ; j++){
			for(int i =0 ; i<10 ; i++){
				List<String> context = new ArrayList<String>();
				context.add("C"+i);
				String a = "A"+i;
				MockHistoryData hoc = new MockHistoryData(a,context);	
				historySet.add(hoc);
			}
		}
		return historySet;
	}



	public List<MockHistoryData> retrieveHistorySet(){
		List<MockHistoryData>  historySet = new ArrayList<MockHistoryData>();

		for(MockHistoryData mockData :historySet ){
			//System.out.println(mockData);
		}
		System.out.println("historySet "+historySet);
		System.out.println("historySet.size() "+historySet.size());

		return historySet;
	}

	public static void main(String[] args) {
		CAUIDiscoveryLearningTest cdt = new CAUIDiscoveryLearningTest();
		cdt.startTesting();
	}
}