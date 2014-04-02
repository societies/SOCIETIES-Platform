package org.societies.orchestration.eca.synoyms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import weka.gui.graphvisualizer.BIFFormatException;



public class SynonymRetriever {

	private static final String service_url = "http://wikisynonyms.ipeirotis.com/api/";

	private static Set<String> returnSynoymsToStrings(Set<String> contextList) {
		Set<String> synoyms = new HashSet<String>(contextList);
		for(String context : contextList) {
			synoyms.addAll(retrieveSynoyms(context));
		}
		return synoyms;
	}

	private static Set<SynonymModel> returnSynoymsToModels(Set<String> contextList) {
		Set<SynonymModel> models = new HashSet<SynonymModel>();
		for(String context : contextList) {
			SynonymModel model = new SynonymModel(context);
			for(String synoym : retrieveSynoyms(context)) {
				model.addSynoym(synoym);
			}
			models.add(model);
		}
		return models;
	}

	private static List<String> retrieveSynoyms(String keyword) {
		List<String> synoyms = new ArrayList<String>();
		synoyms.add(keyword);
		try {
			System.out.println("Searching for : " + keyword);
			String urlString =service_url.concat(keyword);
			URL url = new URL(urlString);//term=abstract");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String jsonString = null;
			String line = null;
			while((line=br.readLine()) !=null) {
				if(jsonString==null) {
					jsonString = line;
				} else {
					jsonString = jsonString.concat(line);
				}
			}
			if(jsonString!=null) {
				JSONTokener tokener = new JSONTokener(jsonString);
				JSONObject root = new JSONObject(tokener);
				if(root.getString("message").equals("success")) {
					JSONArray jsonArray = (JSONArray) root.get("terms");
					for(int arrayCounter = 0; arrayCounter < jsonArray.length(); arrayCounter++) {
						String stringValue = ((JSONObject) jsonArray.get(arrayCounter)).getString("term");
						if(stringValue.trim().length()>0) {
							synoyms.add(stringValue);
							stringValue = stringValue.replace("(", "");
							stringValue = stringValue.replace(")", "");
							String[] singleSynoyms = stringValue.split(" ");
							for(String s : singleSynoyms) {
								if(s.length()>2) {
									s = s.trim();
									synoyms.add(s);
								}

							}

						}
					}
				}
			}
		}catch(IOException e) {

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return synoyms;
	}


	private static Set<String> splitContext(String context) {
		Set<String> synoymStrings = new HashSet<String>();
		String[] split;
		split = context.split(",");
		for(String splitString : split) {
			synoymStrings.add(splitString.trim());
		}

		System.out.println("Returning " + synoymStrings.toString());
		return synoymStrings;
	}


	private static String generateMatches(SynonymModel model, Set<String> remoteSynoyms) {
		HashMap<String, Integer> matchMap = new HashMap<String, Integer>();
		for(String localSynoym : model.getSynoyms()) {
			for(String remoteSynoym: remoteSynoyms) {
				if(localSynoym.equals(remoteSynoym) || remoteSynoym.equals(localSynoym)) {
					if(matchMap.containsKey(localSynoym)) {
						int count = matchMap.get(localSynoym);
						count++;
						matchMap.put(localSynoym, count);
					} else {
						matchMap.put(localSynoym, 1);
					}
				}
			}
		}
		Set<String> keys = new HashSet<String>(matchMap.keySet());
		for(String key : keys) {
			if(matchMap.get(key)==0) {
				matchMap.remove(key);
			}
		}
		//If theres lots of matches, the input is mroe likely to be more generalised hence
		//better use for a suggestion, so lets use that
		if(matchMap.size()>10) {
			//System.out.println("There are lots of matches, so lets just sue the keyword as it will be mroe generalised");
			int tempSize = matchMap.size();
			matchMap.clear();
			matchMap.put(model.getKeyword(), tempSize);
		}
		
		if(matchMap.size()==0) {
			return null;
		}
		
		return computeDecision(model.getKeyword(), matchMap);
	}

	private static String computeDecision(String keyword, HashMap<String, Integer> matchMap) {
		int largestSynoymCount = 0;
		String decision = null;
		boolean foundSameMatch = false;
		for(String synoym : matchMap.keySet()) {
			int synoymCount = matchMap.get(synoym);
			if(synoymCount>largestSynoymCount) {
				largestSynoymCount=synoymCount;
				decision = synoym;
				foundSameMatch = false;
			} else if (synoymCount == largestSynoymCount) {
				foundSameMatch = true;
			}
		}
		//RETURN KEYWORD IF MORE THAN 10 MATCHES (IT WILL BE MORE GENERAL)
		//OR IF THERE ARE SEVERAL MATCHES WITH THE SAME LARGEST COUNT NUMBER
		int mapSize = matchMap.size();
		if((foundSameMatch && largestSynoymCount > 3) || mapSize>=10) {
			return keyword;
		} else if((mapSize<10 && mapSize>=5 && largestSynoymCount>3) || (mapSize<5 && largestSynoymCount>5)) {
			return decision;
		} else {
			return null;
		}
	}


	public static Map<String, Set<String>> getSuggestions(String localContext, Map<String, String> cssToContext) {


		Map<String, Set<String>> remoteSynoyms = new HashMap<String, Set<String>>();
		Set<SynonymModel> localSynoyms = returnSynoymsToModels(splitContext(localContext));
		for(String user : cssToContext.keySet()) {
			remoteSynoyms.put(user, returnSynoymsToStrings(splitContext(cssToContext.get(user))));
		}
		//Set<String> remoteSynoyms = returnSynoymsToStrings(splitContext(theirInterests));


		//HashMap<String, Integer> keywordMatches = new HashMap<String, Integer>();
		//	HashMap<String, Integer> originalMatches = new HashMap<String, Integer>();

		//NOW, lets find matches.
		Map<String, Set<String>> decisions = new HashMap<String, Set<String>>();
		for(SynonymModel model : localSynoyms) {
			Set<String> users = new HashSet<String>();
			for(String remoteCSS : remoteSynoyms.keySet()) {
				System.out.println("Searching for matches on " + model.getKeyword() + " with CSS " + remoteCSS);
				String decision = generateMatches(model, remoteSynoyms.get(remoteCSS));
				if(decision!=null) {
					model.setDecision(decision);
					users.add(remoteCSS);
				}
				
			}
			if(users.size()>0) {
				decisions.put(model.getDecision(), users);
			}
		}
		for(String modelDecision : decisions.keySet()) {
			System.out.println("Match: " + modelDecision + " with " + decisions.get(modelDecision))	;
		}
		
		return decisions;
	}

	

}
