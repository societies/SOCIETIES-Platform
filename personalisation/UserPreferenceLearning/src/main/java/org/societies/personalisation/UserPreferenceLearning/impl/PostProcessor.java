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

package org.societies.personalisation.UserPreferenceLearning.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;

public class PostProcessor 
{
	Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	public IPreferenceTreeModel process(
			String paramName, 
			String treeString, 
			CtxIdentifierCache cache,
			ServiceResourceIdentifier serviceId,
			String serviceType){

		if (LOG.isDebugEnabled()){
			LOG.debug("Converting String to tree: "+treeString);
		}
		System.out.println("Converting String to tree: "+treeString);

		//create root node
		IPreference root = new PreferenceTreeNode();
		IPreference currentNode = root;
		int currentLevel = -1;

		String[] lines = treeString.split("\n");

		//process output line by line to build tree
		for(int i=0; i<lines.length; i++){
			String nextLine = lines[i].trim();
			if(!nextLine.equals("") && !nextLine.equals("Id3")){
				int level = countOccurrences(nextLine, '|');

				if(level > currentLevel){
					//check for leaf
					if(containsLeaf(nextLine)){
						String[] tmp = nextLine.split(":");
						if(tmp.length>1){ //there is a condition for this leaf
							String branchString = tmp[0].trim();
							String leafString = tmp[1].trim();

							IPreference branch = 
									new PreferenceTreeNode(createCondition(branchString, cache));
							IPreference leaf = 
									new PreferenceTreeNode(createOutcome(paramName, leafString, serviceId, serviceType));

							//add branch and leaf
							currentNode.add(branch);
							currentNode = branch;
							currentLevel = level;
							currentNode.add(leaf);
							currentNode = leaf;
							currentLevel ++;
						}else{ //there is no condition for this leaf
							String leafString = tmp[0].trim();
							
							IPreference leaf = 
									new PreferenceTreeNode(createOutcome(paramName, leafString, serviceId, serviceType));
							currentLevel = level;
							currentNode.add(leaf);
							currentNode = leaf;
							currentLevel ++;
						}
					}else{
						IPreference branch = 
								new PreferenceTreeNode(createCondition(nextLine, cache));

						//add branch
						currentNode.add(branch);
						currentNode = branch;
						currentLevel = level;
					}
				}else{
					if(level == currentLevel){
						if(containsLeaf(nextLine)){
							String[] tmp = nextLine.split(":");
							if(tmp.length>1){  //condition for this leaf
								String branchString = tmp[0].trim();
								String leafString = tmp[1].trim();

								IPreference branch = 
										new PreferenceTreeNode(createCondition(branchString, cache));
								IPreference leaf = 
										new PreferenceTreeNode(createOutcome(paramName, leafString, serviceId, serviceType));

								//get parent node to add branch and leaf
								currentNode = 
										(IPreference)
										currentNode.getParent();
								currentNode.add(branch);
								currentNode = branch;
								currentLevel = level;
								currentNode.add(leaf);
								currentNode = leaf;
								currentLevel ++;
							}else{ //no condition for this leaf
								String leafString = tmp[0].trim();
								
								IPreference leaf = 
										new PreferenceTreeNode(createOutcome(paramName, leafString, serviceId, serviceType));
								currentLevel = level;
								currentNode.add(leaf);
								currentNode = leaf;
								currentLevel++;
							}
						}else{
							IPreference branch = 
									new PreferenceTreeNode(createCondition(nextLine, cache));

							//get parent node to add branch
							currentNode = 
									(IPreference)
									currentNode.getParent();
							currentNode.add(branch);
							currentNode = branch;
							currentLevel = level;
						}
					}else{
						if(level < currentLevel){
							if(containsLeaf(nextLine)){
								String[] tmp = nextLine.split(":");
								if(tmp.length>1){ //condtion for this leaf
									String branchString = tmp[0].trim();
									String leafString = tmp[1].trim();

									IPreference branch = 
											new PreferenceTreeNode(createCondition(branchString, cache));
									IPreference leaf = 
											new PreferenceTreeNode(createOutcome(paramName, leafString, serviceId, serviceType));

									//move back up the tree
									while(currentLevel > level){
										currentNode = (IPreference)
												currentNode.getParent();
										currentLevel --;
									}

									//get parent node to add branch and leaf
									currentNode = 
											(IPreference)
											currentNode.getParent();
									currentNode.add(branch);
									currentNode = branch;
									currentLevel = level;
									currentNode.add(leaf);
									currentNode = leaf;
									currentLevel ++;
								}else{ //no condition for this leaf
									String leafString = tmp[0].trim();
									
									IPreference leaf = 
											new PreferenceTreeNode(createOutcome(paramName, leafString, serviceId, serviceType));
									
									//move back up the tree
									while(currentLevel > level){
										currentNode = (IPreference)
												currentNode.getParent();
										currentLevel--;
									}
									currentLevel = level;
									currentNode.add(leaf);
									currentNode = leaf;
									currentLevel++;
								}
							}else{
								IPreference branch = 
										new PreferenceTreeNode(createCondition(nextLine, cache));

								//move back up the tree
								while(currentLevel > level){
									currentNode = (IPreference)
											currentNode.getParent();
									currentLevel --;
								}

								//get parent node to add branch to
								currentNode = 
										(IPreference)
										currentNode.getParent();
								currentNode.add(branch);
								currentNode = branch;
								currentLevel = level;
							}
						}
					}
				}
			}
		}

		//Add tree to return list
		PreferenceDetails details = new PreferenceDetails(serviceType, serviceId, paramName);
		IPreferenceTreeModel newTree = new PreferenceTreeModel(details, root);

		return newTree;
	}


	private boolean containsLeaf(String nextLine){

		if(nextLine.contains(":")){
			return true;
		}
		return false;
	}

	private IPreferenceCondition createCondition(String temp, CtxIdentifierCache cache){

		if (LOG.isDebugEnabled()){
			LOG.debug("Creating condition from String: "+temp);
		}
		String noBars = removeChar(temp, '|');
		noBars = noBars.trim();
		String[] tuple = noBars.split("=");
		//IAction action = new Action(ServiceResourceIdentifier, ServiceType, tuple[0].trim(), tuple[1].trim());
		IPreferenceCondition condition = cache.getPreferenceCondition(tuple[0].trim(), tuple[1].trim());

		return condition;
	}

	private IOutcome createOutcome(String paramName, String value, ServiceResourceIdentifier serviceId, String serviceType){

		if (LOG.isDebugEnabled()){
			LOG.debug("Creating outcome from String: "+value);
		}
		IOutcome outcome = new PreferenceOutcome(serviceId, serviceType, paramName, value);

		return outcome;
	}

	private String removeChar(String s, char c) {
		String r = "";
		for (int i = 0; i < s.length(); i ++) {
			if (s.charAt(i) != c) r += s.charAt(i);
		}
		return r;
	}

	private int countOccurrences(String line, char object)
	{
		int count = 0;
		for (int i=0; i < line.length(); i++)
		{
			if (line.charAt(i) == object)
			{
				count++;
			}
		}
		return count;
	}
}
