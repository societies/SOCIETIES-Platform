/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.orchestration.crm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.orchestration.ICommunityRecommendationManager;
import org.societies.api.cis.orchestration.model.IFilter;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;
import org.societies.css.mgmt.CssDirectoryRemoteClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Implementation of the {@link ICommunityRecommendationManager} interface.
 * 
 * @author Chris Lima
 */

@Service
public class  CommunityRecommendationManager implements ICommunityRecommendationManager {


	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CommunityRecommendationManager.class);

	private int limit = 10;
	private List<CssAdvertisementRecord> cssAdvertsList;
	private List<CisAdvertisementRecord> cisList;
	private CisDirectoryRemoteClient callback = new CisDirectoryRemoteClient();
	private CssDirectoryRemoteClient cssDirCallback = new CssDirectoryRemoteClient();

	/** Service references */
	private ICisDirectoryRemote cisDirectoryRemote;
	private ICssDirectoryRemote cssDirectoryRemote;
	private ICommManager commMgr;
	private final String cssOwnerStr;


	/**
	 * 
	 * Default is 10 results
	 * 
	 * @param limit Maximum results to return
	 */
	@Override
	public void setLimit(int limit){
		this.limit = limit;
	}

	/**
	 * 
	 */
	public CommunityRecommendationManager() {
		this.cssOwnerStr = null;
	}
	/**
	 * Only for JUnit testing!
	 */
	public CommunityRecommendationManager(CisDirectoryRemoteClient mockCallback, ICisDirectoryRemote cisDirectoryRemote, ICommManager commMgr) {
		this.cssOwnerStr = "mock.societies.local";
		this.cisDirectoryRemote = cisDirectoryRemote;
		this.callback = mockCallback;
		this.commMgr = commMgr;
		LOG.info(this.getClass() + " instantiated for Unit Tests");
	}

	/**
	 * For spring injection
	 */
	@Autowired(required=true)
	CommunityRecommendationManager(ICisDirectoryRemote cisDirectoryRemote, ICssDirectoryRemote cssDirectoryRemote,ICommManager commMgr) {
		this.cisDirectoryRemote = cisDirectoryRemote;
		this.cssDirectoryRemote = cssDirectoryRemote;
		this.commMgr = commMgr;
		this.cssOwnerStr = commMgr.getIdManager().getThisNetworkNode().getBareJid();

		LOG.info(this.getClass()+" instantiated");
		LOG.info("CSS Owner: "+cssOwnerStr);
	}

	/**
	 * @return the limit
	 */
	@Override
	public int getLimit(){
		return this.limit;
	}


	/**
	 * 
	 * Sort the results based on the rank available in {@link RankCisAdv}
	 * 
	 * @param filterType True for primary filter and false for secondary filter
	 * @param cisList List of all {@link CisAdvertisementRecord} retrieved
	 * @param filter Array of {@link Filter} to analyze
	 * @return sorted list of {@link CisAdvertisementRecord}
	 */
	private List<CisAdvertisementRecord> sort(boolean filterType, List<CisAdvertisementRecord> cisList, IFilter[] filter) {
		if (filter.length <= 0){
			LOG.error("Filter array needs at least one filter! Returning all the CISs...");
			//Returning all CISs if no filter is specified
			return cisList;
		}
		else {
			List<RankCisAdv> rankResultList = new ArrayList<RankCisAdv>();
			for (CisAdvertisementRecord cisAdv : cisList) {
				MembershipCrit criteriaList = cisAdv.getMembershipCrit();
				LOG.info("CIS CisAdvertisementRecord: "+cisAdv.getName() + " Number of Membership criteria: "+criteriaList.getCriteria().size()+" Owner: " + cisAdv.getCssownerid());
				if ((criteriaList != null)) {
					RankCisAdv tempRankCIS = new RankCisAdv();
					for (Criteria memberCriteria : criteriaList.getCriteria())
					{					
						for (IFilter tempFilter : filter)
						{
							//Check if context attribute is the same of the criteria
							if (tempFilter.getCtxAttribute().contains(memberCriteria.getAttrib())) {
								boolean condition = checkCriteria(memberCriteria, tempFilter);
								if (condition) {
									tempRankCIS.incrementRank();
								}
							}
						}
					}
					//Primary filter
					if (tempRankCIS.getRank() > 0){
						if (filterType == true && tempRankCIS.getRank() == criteriaList.getCriteria().size()){
							tempRankCIS.setCisAdv(cisAdv);
							rankResultList.add(tempRankCIS);
						}
						//Secondary filter
						else if (filterType == false) {
							tempRankCIS.setCisAdv(cisAdv);
							rankResultList.add(tempRankCIS);
						}
					}

				}
			}
			//Show results before sort
			for (RankCisAdv tempRankCisAdv : rankResultList) {
				LOG.info("Rank before sort: "+tempRankCisAdv.getRank()+ " Adv: "+tempRankCisAdv.getCisAdv().getName());
			}
			//order by rank
			Collections.sort(rankResultList, new RankCisAdv());
			List<CisAdvertisementRecord> result = new ArrayList<CisAdvertisementRecord>();
			for (RankCisAdv tempRankCisAdv : rankResultList) {
				LOG.info("Rank after sort: "+tempRankCisAdv.getRank()+ " Adv: "+tempRankCisAdv.getCisAdv().getName());
				result.add(tempRankCisAdv.getCisAdv());
			}
			
			return result;
		}

	}


	/**
	 * 
	 * Check the criteria available in {@link Criteria}. The Value2 of a criteria is not been used. 
	 * 
	 * @param criteria to evaluate
	 * @param filter used to compare
	 * @return Return true if a given criteria matches the filter operator
	 */
	private boolean checkCriteria(Criteria criteria, IFilter filter) {
		LOG.info("Criteria Attribute: "+criteria.getAttrib());
		LOG.info("Criteria Value1: "+criteria.getValue1());
		//TODO: Value2 is not been used.
//		LOG.info("Criteria Value2: "+criteria.getValue2());

		//Check if value is numeric or not
		switch (isNumeric(criteria.getValue1()) ? 1 : 2){
		//Numeric value
		case 1:
			double number = Double.parseDouble(criteria.getValue1());
			double filterNumber = Double.parseDouble(filter.getValue());
			switch (filter.getOperator()){
			case EQUAL:
				if(number == (filterNumber)) return true;
				else return false;
			case NOT_EQUAL:
				if(number != (filterNumber))  return true;
				else return false;
			case LESS:
				if(number < (filterNumber))  return true;
				else return false;
			case LESS_OR_EQUAL:
				if(number <= (filterNumber))  return true;
				else return false;
			case GREATER:
				if(number > (filterNumber))  return true;
				else return false;
			case GREATER_OR_EQUAL:
				if(number >= (filterNumber))  return true;
				else return false;
			}				
			break;

		//String value
		case 2:
			String stringValue = criteria.getValue1();
			switch (filter.getOperator()){
			case EQUAL:
				if(stringValue.equalsIgnoreCase(filter.getValue())) return true;
				else return false;
			case NOT_EQUAL:
				if(!stringValue.equalsIgnoreCase(filter.getValue())) return true;
				else return false;
			case SIMILAR:
				if (stringValue.length() > 5){
					String[] words = getSynonyms(stringValue);
					String[] filterWords = getSynonyms(filter.getValue());					
					LOG.info("Synonyms from remote cis: "+Arrays.toString(words));
					LOG.info("Synonyms from search: "+Arrays.toString(filterWords));
					for (String str1 : words){
						for (String str2 : filterWords){
							if (str1.equalsIgnoreCase(str2)){
								return true;
							}
						}
					}
				}
				return false;
			}			
			break;
		}
		return false;
	}


	/**
	 * 
	 * Find synonyms for string values. Used by {@link SIMILAR} operator
	 * 
	 * @param stringValue word to be submitted for NLP analyzes.
	 * @return A array of synonyms word. At least the stringValue is returned if no synonyms is found.
	 */
	private String[] getSynonyms(final String stringValue) {
		//TODO:Change API key
		final String APIKEY = "b9a370e35951ab0e1c7f973f90690a879546b35f";
		//AlchemyAPI api key allows 1000 queries a day
		final SynonymsAux alchemyObj = SynonymsAux.GetInstanceFromString(APIKEY);

		ExecutorService executor = Executors.newFixedThreadPool(2);
		List<FutureTask<String[]>> taskList = new ArrayList<FutureTask<String[]>>();

		// Start thread for the first half of the numbers
		FutureTask<String[]> futureTaskCategory = new FutureTask<String[]>(new Callable<String[]>() {
			@Override
			public String[] call() throws Exception {
				Document docCategory = null;
				docCategory = alchemyObj.TextGetCategory(stringValue);
				String[] arrayCategory = new String[0];
				if (docCategory != null) {
					NodeList result = docCategory.getElementsByTagName("text");
					arrayCategory = new String[result.getLength()];
					for (int i = 0; i < result.getLength(); i++) {
						arrayCategory[i] = result.item(i).getTextContent().toLowerCase();
					}
				}
				return arrayCategory;
			}

		});
		taskList.add(futureTaskCategory);
		executor.execute(futureTaskCategory);

		FutureTask<String[]> futureTaskConcept = new FutureTask<String[]>(new Callable<String[]>() {
			@Override
			public String[] call() throws Exception {
				Document docConcept = null;
				docConcept = alchemyObj.TextGetRankedConcepts(stringValue);
				String[] arrayConcept = new String[0];
				if (docConcept != null) {
					NodeList result = docConcept.getElementsByTagName("text");
					arrayConcept = new String[result.getLength()];
					for (int i = 0; i < result.getLength(); i++) {
						arrayConcept[i] = result.item(i).getTextContent().toLowerCase();
					}
				}
				return arrayConcept;
			}

		});
		taskList.add(futureTaskConcept);
		executor.execute(futureTaskConcept);

		List<String[]> list = new ArrayList<String[]>();
		for(FutureTask<String[]> fut : taskList){
			try {
				list.add(fut.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();

		String[] arrayCategory = list.get(0);
		String[] arrayConcept = list.get(1);

		//Create an array plus one for the default value
		String [] result = Arrays.copyOf(arrayCategory, arrayCategory.length + arrayConcept.length +1);
		System.arraycopy(arrayConcept, 0, result, arrayCategory.length, arrayConcept.length);

		//Include the string value in the last position
		result[result.length-1] = stringValue;
		return result;
	}


	/**
	 * @return list of remote {@link CisAdvertisementRecord} which the CSS user is not the owner
	 */
	private List<CisAdvertisementRecord> relevantCISs() {
		this.cisDirectoryRemote.findAllCisAdvertisementRecords(callback);
		List<CisAdvertisementRecord> cisAdvertsList = callback.getResultList();

		List<CisAdvertisementRecord> cisList = new ArrayList<CisAdvertisementRecord>();
		if (cisAdvertsList != null) {
			for (CisAdvertisementRecord cisAdv : cisAdvertsList)
			{
				//Considering only CISs which the user is not the owner
				if (!cssOwnerStr.equalsIgnoreCase(cisAdv.getCssownerid())) {
					cisList.add(cisAdv);
				}
			}
		} 
		else {
			LOG.error("CisAdvertisementRecord is null!");
		}
		return cisList;
	}

	/**
	 * 
	 * Return true if the string is a numeric value.
	 * 
	 * @return True for numeric values
	 */
	private static boolean isNumeric(String str)
	{
		for (char character : str.toCharArray())
		{
			if (!Character.isDigit(character)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * Return a list of CIS advertisements sorted by relevance. {@link CisAdvertisementRecord} enables to retrieve CIS information.
	 * 
	 * @param limit Maximum results to return. Default value if null is 10.
	 * @param primaryfilter An array of filters. The primary filter is applied for exact results. All the filters must match.
	 * @param secondaryfilter An array of filters. The secondary filter is applied for extended results. 
	 * @return list of CIS advertisements sorted by relevance
	 */
	@Override
	public List<CisAdvertisementRecord> getCISAdvResults(int limit, IFilter[] primaryfilter,	IFilter[] secondaryfilter) {
		if (limit > 0){
			this.limit = limit;
		}
		//Get remote CISs
		cisList = relevantCISs();
		if (!cisList.isEmpty()) {
			if (primaryfilter != null && secondaryfilter == null){ 
				return sort(true, cisList, primaryfilter);

			}
			else if (primaryfilter == null && secondaryfilter != null){
				return sort(false, cisList, secondaryfilter);
			} 
			else if (primaryfilter == null && secondaryfilter == null){
				LOG.error("Primary and secondary filters are nulls!");
			} 
			else {
				//TODO:Change to accept two filters?
				List<CisAdvertisementRecord> resultsPrimaryFilter = sort(true, cisList, primaryfilter);
				List<CisAdvertisementRecord> resultsSecondaryFilter = sort(false, cisList, secondaryfilter);
				resultsPrimaryFilter.addAll(resultsSecondaryFilter);
				return resultsPrimaryFilter;
			}
		}
		else {
			LOG.error("There is no remote CISs now!");
		}
		return new ArrayList<CisAdvertisementRecord>();
	}


	/**
	 * 
	 * Return a list of CIS identities sorted by relevance. The {@link IIdentity} list can be used to retrieve more information from the Context Broker
	 * 
	 * @param limit Maximum results to return. Default value if null is 10.
	 * @param primaryfilter An array of filters. The primary filter is applied when you want an exact result
	 * @param secondaryfilter An array of filters. The secondary filter is when not all parameters provide a match
	 * @return list of CIS advertisements sorted by relevance
	 */
	@Override
	public List<IIdentity> getResults(int limit, IFilter[] primaryfilter, IFilter[] secondaryfilter) {
		if (limit >= 0){
			this.limit = limit;
		}
		List<IIdentity> cisListID = new ArrayList<IIdentity>();
		List<CisAdvertisementRecord> results = new ArrayList<CisAdvertisementRecord>();
		cisList = relevantCISs();
		if (!cisList.isEmpty()) {
			if (primaryfilter != null && secondaryfilter == null){ 
				results = sort(true, cisList, primaryfilter);
			}
			else if (primaryfilter == null && secondaryfilter != null){
				results = sort(false, cisList, secondaryfilter);
			} 
			else if (primaryfilter == null && secondaryfilter == null){
				LOG.error("Primary and secondary filters are nulls!");
			} 
			else {
				//TODO:Change to accept two filters?
				List<CisAdvertisementRecord> resultsPrimaryFilter = sort(true, cisList, primaryfilter);
				List<CisAdvertisementRecord> resultsSecondaryFilter = sort(false, cisList, secondaryfilter);
				resultsPrimaryFilter.addAll(resultsSecondaryFilter);
				results = resultsPrimaryFilter;
			}

			IIdentity remoteCis = null;
			for (CisAdvertisementRecord cisAdv : results) {
				try {
					remoteCis = this.commMgr.getIdManager().fromJid(cisAdv.getId());
				} catch (InvalidFormatException e) {
					e.printStackTrace();
				}
				if (remoteCis != null) {
					LOG.info("CIS Identity: "+remoteCis.getJid());
					cisListID.add(remoteCis);
				}
			}
		}
		else {
			LOG.error("There is no remote CISs now!");
		}
		return cisListID;
	}


	/**
	 * Return a list of {@link CssAdvertisementRecord}
	 */
	@Deprecated
	private List<CssAdvertisementRecord> getCSSResults() {
		//CIS
		List<CisAdvertisementRecord> cisList = relevantCISs();
		List<CssAdvertisementRecord> cssAdvList = new ArrayList<CssAdvertisementRecord>();
		if (!cisList.isEmpty()) {
			List<String> cssList = new ArrayList<String>();
			for (CisAdvertisementRecord cisAdv : cisList)
			{
				cssList.add(cisAdv.getCssownerid());
				//CSS
				this.cssDirectoryRemote.searchByID(cssList, cssDirCallback);
				cssAdvertsList = cssDirCallback.getResultList();
				if (cssAdvertsList != null) {
					for (CssAdvertisementRecord temp : cssAdvertsList) {
						cssAdvList.add(temp);
						LOG.info("CSS CssAdvertisementRecord: "+temp.getName() + " \t ID " + temp.getId());
					}
				}
			}
		}
		return cssAdvList;
	}
}