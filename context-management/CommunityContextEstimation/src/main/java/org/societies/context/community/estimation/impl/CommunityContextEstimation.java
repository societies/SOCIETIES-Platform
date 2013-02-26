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
package org.societies.context.community.estimation.impl;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.societies.context.api.community.estimation.estimationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author yboul 07-Dec-2011 4:15:14 PM
 */

/**
 * The CommunityContextEstimation class contains the methods to be called in order to estimate the community context.
 * It has four types of methods. These that contain the letters "Num" in their name and deal with numeric attributes,
 * these that contain the letters "Geom" in their name and deal with geometric attributes (e.g. location),
 * these containing the letters "Special" and deal with other attributes and these containing the letters "String" in 
 * their name that deal with string attributes
 */
@Service
public class CommunityContextEstimation implements ICommunityCtxEstimationMgr{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CommunityContextEstimation.class);

	@Autowired(required=false)
	private ICtxBroker internalCtxBroker;

	public CommunityContextEstimation() {
		LOG.info(this.getClass() + "CommunityContextEstimation instantiated ");
	}

	@Override
	public CtxAttribute estimateCommunityCtx(CtxEntityIdentifier ctxId,
			CtxAttributeIdentifier ctxAttributeIdentifier, estimationModel model) {
		// TODO Auto-generated method stub

		switch (model) {
		case mean:

			break;

		case median:

			break;

		case mode:

			break;

		case range:

			break;

		case minBB:

			break;

		case stringMode:

			break;

		case convexHull:

			break;
		default:
			break;
		}

		return null;
	}


	@Override
	public CtxAttribute estimateCommunityCtx(CtxEntityIdentifier communityCtxId, CtxAttributeIdentifier ctxAttributeIdentifier) {

		CtxAttribute retrievedType = null;
		CtxAttribute result = null;
		ArrayList<String> finalArrayStringList = new ArrayList<String>();

		try {
			retrievedType = (CtxAttribute) internalCtxBroker.retrieve(ctxAttributeIdentifier).get();

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CtxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(retrievedType.getType().toString().equals("temperature"))
		{
			ArrayList<Integer> inputValues = new ArrayList<Integer>();

			// TODO code optimization

			try {
				CommunityCtxEntity retrievedCommunity;
				try {
					retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();
					Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();
						
					for(CtxEntityIdentifier comMemb:communityMembers){
						IndividualCtxEntity individualMember = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
						Set<CtxAttribute> list = individualMember.getAttributes("TEMPERATURE");	
						
						for (CtxAttribute ca:list){
							inputValues.add(ca.getIntegerValue());
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			double meanValue = cceNumMean(inputValues);

			try {
				//CtxAttribute meanV = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.TEMPERATURE).get();
				//retrievedType = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.TEMPERATURE).get();
				//replaced meanV with the already existing retrievedType
				retrievedType.setDoubleValue(meanValue);
				retrievedType.setValueType(CtxAttributeValueType.DOUBLE);
				retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType).get();
				result =retrievedType;
				result.getDoubleValue();

			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (CtxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if (retrievedType.getType().toString().equals("interests")) 
		{
			ArrayList<String> stringInputValues = new ArrayList<String>();
			ArrayList<String> individualsStrings = new ArrayList<String>();

			try {
				CommunityCtxEntity retrievedCommunity;
				try {
					retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();

					Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();

					for(CtxEntityIdentifier comMemb:communityMembers){

						IndividualCtxEntity individualMemeber = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
						Set<CtxAttribute> setAttributesInterests = individualMemeber.getAttributes("INTERESTS");

						for (CtxAttribute ca:setAttributesInterests){
							stringInputValues.add(ca.getStringValue());
						}
					}

					individualsStrings.addAll(stringInputValues);

					for (String s:individualsStrings){
						String[] helper = s.split(",");
						for (String s1:helper){
							finalArrayStringList.add(s1);
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> modeStringValue= cceStringMode(finalArrayStringList); //[cinema]

			try {
				//CtxAttribute interestsMode = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.INTERESTS).get();
				//replaced interestsMode with retrievedType
				if (modeStringValue.size()!=0){

					retrievedType.setStringValue(modeStringValue.get(0).toString());
					retrievedType.setValueType(CtxAttributeValueType.STRING);
					retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType).get();
					result =retrievedType;
					result.getStringValue();

				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		if (retrievedType.getType().toString().equals("books")) 
		{
			ArrayList<String> stringInputValues = new ArrayList<String>();
			ArrayList<String> individualsStrings = new ArrayList<String>();

			try {
				CommunityCtxEntity retrievedCommunity;
				try {
					retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();

					Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();

					for(CtxEntityIdentifier comMemb:communityMembers){

						IndividualCtxEntity individualMemeber = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
						Set<CtxAttribute> setAttributesBooks = individualMemeber.getAttributes("BOOKS");

						for (CtxAttribute ca:setAttributesBooks){
							stringInputValues.add(ca.getStringValue());
						}
					}

					individualsStrings.addAll(stringInputValues);

					for (String s:individualsStrings){
						String[] helper = s.split(",");
						for (String s1:helper){
							finalArrayStringList.add(s1);
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> modeStringValue= cceStringMode(finalArrayStringList); //[cinema]

			try {
				//CtxAttribute interestsMode = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.INTERESTS).get();
				//replaced interestsMode with retrievedType
				if (modeStringValue.size()!=0){
					retrievedType.setStringValue(modeStringValue.get(0).toString());
					retrievedType.setValueType(CtxAttributeValueType.STRING);
					retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType).get();
					result =retrievedType;
					result.getStringValue();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		
		if (retrievedType.getType().toString().equals("movies")) 
		{
			ArrayList<String> stringInputValues = new ArrayList<String>();
			ArrayList<String> individualsStrings = new ArrayList<String>();

			try {
				CommunityCtxEntity retrievedCommunity;
				try {
					retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();

					Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();

					for(CtxEntityIdentifier comMemb:communityMembers){

						IndividualCtxEntity individualMemeber = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
						Set<CtxAttribute> setAttributesMovies = individualMemeber.getAttributes("MOVIES");

						for (CtxAttribute ca:setAttributesMovies){
							stringInputValues.add(ca.getStringValue());
						}
					}

					individualsStrings.addAll(stringInputValues);

					for (String s:individualsStrings){
						String[] helper = s.split(",");
						for (String s1:helper){
							finalArrayStringList.add(s1);
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> modeStringValue= cceStringMode(finalArrayStringList); //[cinema]

			try {
				//CtxAttribute interestsMode = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.INTERESTS).get();
				//replaced interestsMode with retrievedType
				if (modeStringValue.size()!=0){
					retrievedType.setStringValue(modeStringValue.get(0).toString());
					retrievedType.setValueType(CtxAttributeValueType.STRING);
					retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType).get();
					result =retrievedType;
					result.getStringValue();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		if (retrievedType.getType().toString().equals("languages")) 
		{
			ArrayList<String> stringInputValues = new ArrayList<String>();
			ArrayList<String> individualsStrings = new ArrayList<String>();

			try {
				CommunityCtxEntity retrievedCommunity;
				try {
					retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();

					Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();

					for(CtxEntityIdentifier comMemb:communityMembers){

						IndividualCtxEntity individualMemeber = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
						Set<CtxAttribute> setAttributesLanguages = individualMemeber.getAttributes("LANGUAGES");

						for (CtxAttribute ca:setAttributesLanguages){
							stringInputValues.add(ca.getStringValue());
						}
					}

					individualsStrings.addAll(stringInputValues);

					for (String s:individualsStrings){
						String[] helper = s.split(",");
						for (String s1:helper){
							finalArrayStringList.add(s1);
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> modeStringValue= cceStringMode(finalArrayStringList); //[cinema]

			try {
				//CtxAttribute interestsMode = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.INTERESTS).get();
				//replaced interestsMode with retrievedType
				if (modeStringValue.size()!=0){
					retrievedType.setStringValue(modeStringValue.get(0).toString());
					retrievedType.setValueType(CtxAttributeValueType.STRING);
					retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType).get();
					result =retrievedType;
					result.getStringValue();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		if (retrievedType.getType().toString().equals("favourite_quotes")) 
		{
			ArrayList<String> stringInputValues = new ArrayList<String>();
			ArrayList<String> individualsStrings = new ArrayList<String>();

			try {
				CommunityCtxEntity retrievedCommunity;
				try {
					retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();

					Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();

					for(CtxEntityIdentifier comMemb:communityMembers){

						IndividualCtxEntity individualMemeber = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
						Set<CtxAttribute> setAttributesFavouriteQuotes = individualMemeber.getAttributes("FAVOURITE_QUOTES");

						for (CtxAttribute ca:setAttributesFavouriteQuotes){
							stringInputValues.add(ca.getStringValue());
						}
					}

					individualsStrings.addAll(stringInputValues);

					for (String s:individualsStrings){
						String[] helper = s.split(",");
						for (String s1:helper){
							finalArrayStringList.add(s1);
						}
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> modeStringValue= cceStringMode(finalArrayStringList); //[cinema]

			try {
				//CtxAttribute interestsMode = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.INTERESTS).get();
				//replaced interestsMode with retrievedType
				if (modeStringValue.size()!=0){
					retrievedType.setStringValue(modeStringValue.get(0).toString());
					retrievedType.setValueType(CtxAttributeValueType.STRING);
					retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType).get();
					result =retrievedType;
					result.getStringValue();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		//***************************************************************************************
		if (retrievedType.getType().toString().equals("location_coordinates")){
			
			ArrayList<String> stringLocationValues = new ArrayList<String>();
			CommunityCtxEntity retrievedCommunity;

			try {
				retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();
				Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();


				for(CtxEntityIdentifier comMemb:communityMembers){

					IndividualCtxEntity individualMemeber = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
					Set<CtxAttribute> setAttributesCoordinatesLocations = individualMemeber.getAttributes("LOCATION_COORDINATES");

					for (CtxAttribute ca:setAttributesCoordinatesLocations){
						stringLocationValues.add(ca.getStringValue());			
					}
							
				}
				
					String LocationsAsString = stringLocationValues.toString();
					CommunityContextEstimation cce = new CommunityContextEstimation();
					ArrayList<Point2D> points = CommunityContextEstimation.splitString(LocationsAsString);
					ArrayList<Point2D> conHull = cce.cceGeomConvexHull(points);
					
					//CtxAttribute comLocationCoordinates = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.LOCATION_COORDINATES).get();
					//replace comLocationCoordinates with retrievedType
					if(conHull.size()!=0){
						
						retrievedType.setStringValue(conHull.toString());   
						retrievedType.setValueType(CtxAttributeValueType.STRING);
						retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType);
						result = retrievedType;
						result.getStringValue();
						
					}			
					
																	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$	
		
		if (retrievedType.getType().toString().equals("location_symbolic")){

			ArrayList<String> stringLocationSymbolicValues = new ArrayList<String>();
			ArrayList<String> individualsLocationSymbolicStrings = new ArrayList<String>();

			try {
				CommunityCtxEntity retrievedCommunity;
				retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();
				Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();


				for(CtxEntityIdentifier comMemb:communityMembers){

					IndividualCtxEntity individualMemeber = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
					Set<CtxAttribute> setAttributesSymbolicLocations = individualMemeber.getAttributes("LOCATION_SYMBOLIC");

					for (CtxAttribute ca:setAttributesSymbolicLocations){
						stringLocationSymbolicValues.add(ca.getStringValue());			
					}
				}
				
				individualsLocationSymbolicStrings.addAll(stringLocationSymbolicValues);
				
				for (String s:individualsLocationSymbolicStrings){
					String[] helper = s.split(",");
					for (String s1:helper){
						finalArrayStringList.add(s1);
					}
				}
								
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> modeStringValue= cceStringMode(finalArrayStringList);
			
			try {
				//CtxAttribute symbolicLocationMode = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
				//symbolicLocationMode.setStringValue(modeStringValue.get(0).toString());
				if (modeStringValue != null) {
					retrievedType.setStringValue(modeStringValue.get(0).toString());
					retrievedType.setValueType(CtxAttributeValueType.STRING);
					retrievedType = (CtxAttribute) this.internalCtxBroker.update(retrievedType).get();
					result =retrievedType;
					result.getStringValue();						
				}
				
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		//****************************************************************************************

		//Methods that can also be called
		//double medianValue = cceNumMedian(inputValues);
		//ArrayList<Integer> modeValue = cceNumMode(inputValues);
		//Integer[] numRange = cceNumRange(inputValues);	
		
//		
//		LOG.info("Before checking the LOCATION, :"+retrievedType.getType());
//		if (retrievedType.getType().toString().equals("location")) 
//		{
//			ArrayList<Double> doubleInputValues = new ArrayList<Double>();
//			ArrayList<String> individualsStrings = new ArrayList<String>();
//
//			CommunityCtxEntity retrievedCommunity;
//
//			try {
//				retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();
//
//				Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();
//
//				for(CtxEntityIdentifier comMemb:communityMembers){
//
//					IndividualCtxEntity objMemebers = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
//
//					Set<CtxAttribute> setAttributesInterests = objMemebers.getAttributes("LOCATION");
//
//					for (CtxAttribute ca:setAttributesInterests){
//						doubleInputValues.add(ca.getDoubleValue());
//					}
//				}
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (CtxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			ArrayList<Point2D> modeStringValue= cceGeomConvexHull(doubleInputValues);
//		
//			try {
//				CtxAttribute interestsMode = (CtxAttribute) this.internalCtxBroker.createAttribute(communityCtxId, CtxAttributeTypes.INTERESTS).get();
//				
//				interestsMode.setStringValue(modeStringValue.get(0).toString());//(interestsMode.getStringValue());//(ctxAttributeIdentifier.getType()+" mean Value");
//				LOG.info("The value I am trying to uodate is :"+modeStringValue.get(0).toString() );
//				interestsMode.setValueType(CtxAttributeValueType.STRING);
//				interestsMode = (CtxAttribute) this.internalCtxBroker.update(interestsMode).get();
//				result =interestsMode;
//				result.getStringValue();
//				LOG.info("mode String value for update "+ interestsMode.getStringValue());
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (CtxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			//Call the cceNumMean method
//
//			//	}
//
//			//TO DO 
//			//set the value to a new community attribute
//
//			//result.getDoubleValue();
//
//	}
//		
		return result;
	}

	
	
	//@Override
	/*
	 * Returns the mean value of an integers' ArrayList 
	 * @param an array list of integers
	 * @return a double as the mean value of the input integers
	 * 
	 */
	public double cceNumMean(ArrayList<Integer> inputValuesList) {

		Assert.notEmpty(inputValuesList,"Cannot use estimation without attributes");
		int total = 0; 
		//double res = 0.0;

		for (int i=0; i<inputValuesList.size(); i++) {
			total = total + inputValuesList.get(i);
		}		

		double res = (double)total/(double)inputValuesList.size();
//		double res =roundTwoDecimals(total/inputValuesList.size());
//		DecimalFormat twoDForm = new DecimalFormat("#.##");
//		double resF = Double.valueOf(twoDForm.format(res)).doubleValue();				

		return res;
	}
	
	/*
	 * Returns the median of an integers' ArrayList
	 * @param an array list of integers
	 * @return a double as the median value of the input integers
	 */
	//@Override
	public double cceNumMedian(ArrayList<Integer> inputValuesList) {
		Assert.notEmpty(inputValuesList,"Cannot use estimation without attributes");
		Integer med,med1,med2=0;
		Collections.sort(inputValuesList);

		if (inputValuesList.size()%2 == 1 ){
			med = inputValuesList.get((inputValuesList.size()-1)/2);	
		}
		else {
			med1 = inputValuesList.get((inputValuesList.size())/2-1);
			med2 = inputValuesList.get((inputValuesList.size())/2);
			med = (med1+med2)/2;
		}
		return med;	
	}

	//@Override
	/*
	 * Returns the mode of an integer's ArrayList
	 * @param an array list of integers
	 * @return an ArrayList of integers representing the mode value of the input integers
	 */
	public ArrayList<Integer> cceNumMode(ArrayList<Integer> inputValuesList) {

		Assert.notEmpty(inputValuesList,"Cannot use estimation without attributes");
		Hashtable <Integer, Integer> frequencyMap = new Hashtable<Integer, Integer>();
		ArrayList<Integer> finalList = new ArrayList<Integer>();

		ArrayList<Integer> mode = new ArrayList<Integer>();
		int max=0;

		for (int i=0; i<inputValuesList.size(); i++){
			if (finalList.contains(inputValuesList.get(i))){
				int elementCount =frequencyMap.get(inputValuesList.get(i));
				elementCount++;
				frequencyMap.put(inputValuesList.get(i), elementCount);

				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				finalList.add(inputValuesList.get(i));
				frequencyMap.put(inputValuesList.get(i), 1);
			}	
		}
		Enumeration<Integer> e = frequencyMap.keys();
		while(e.hasMoreElements()){
			if (frequencyMap.get(e)==max){
				mode.add(Integer.parseInt(e.toString()));
			}
		}

		return mode;
	}

	//@Override
	/*
	 * Returns the range of an integers' ArrayList
	 * @param an array list of integers
	 * @return the range of the input integers as Integer[]
	 */
	public Integer[] cceNumRange(ArrayList<Integer> inputValuesList) {

		Integer[] r = new Integer[2];

		Integer min= Integer.MAX_VALUE;
		Integer max = Integer.MIN_VALUE;

		for (int i=0; i<inputValuesList.size(); ++i){
			if (inputValuesList.get(i) < min){
				min=inputValuesList.get(i);
			}
			if (inputValuesList.get(i) > max){
				max=inputValuesList.get(i);
			}
		}
		r[0]=min;
		r[1]=max;
		return r;
	}	

	//@Override
	/*
	 * Returns the convex hull of a points' ArrayList. It recursively uses the singleSideHulSet method
	 * @param an array list of points.
	 * @return an ArrayList of points, representing the convex hull set of the input points
	 */
	public ArrayList<Point2D> cceGeomConvexHull(ArrayList<Point2D> points) {
				
		ArrayList<Point2D> convexHullSet = new ArrayList<Point2D>();
		double minX= Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		int minPointIndex = -1;
		int maxPointIndex = -1;
		ArrayList<Point2D> leftPointsSet = new ArrayList<Point2D>();
		ArrayList<Point2D> rightPointsSet = new ArrayList<Point2D>();

		if (points.size()<3){
			return points;
		}
		
		for (int i=0; i<points.size(); ++i){
			if (points.get(i).getX() < minX){
				minX=points.get(i).getX();
				minPointIndex = i;
			}
			if (points.get(i).getX() > maxX){
				maxX=points.get(i).getX();
				maxPointIndex =i;
			}
		}
		
		Point2D minP = points.get(minPointIndex);
		Point2D maxP = points.get(maxPointIndex);	
		//Point2D p = new Point2D();
		convexHullSet.add(minP);
		convexHullSet.add(maxP);
		points.remove(minP);
		points.remove(maxP);
		
		for (int i=0; i<points.size(); ++i){
			Point2D p = points.get(i);
			double crossProduct = (maxP.getX()-minP.getY())*(p.getY()-minP.getY()) - (maxP.getY()-minP.getY())*(p.getX()-minP.getX());
			if (crossProduct>0){
				leftPointsSet.add(p);
			}
			else rightPointsSet.add(p);
		}
		
		singleSideHullSet(leftPointsSet,minP,maxP,convexHullSet);
		singleSideHullSet(rightPointsSet,maxP,minP,convexHullSet);
		return convexHullSet;
	}

	/*
	 * This method finds the points of the given pointsSet, that belong to convex hull and adds them to the given convexHull set. 
	 * It constructs a segment with the points minPoint and maxPoint and calculates if the points belonging to the pointsSet and are at the left of the segment
	 * belong to the convexHull set
	 * @param minPoint, maxPoint the two points that construct the segment
	 * @param pointsSet a set of points that are lying at the left of the segment (minPoint,maxPoint)
	 * @param convexHullSet the set that contains the points belonging to the convex hull
	 */
	private void singleSideHullSet(ArrayList<Point2D> pointsSet, Point2D minPoint,
			Point2D maxPoint, ArrayList<Point2D> convexHullSet) {

		
		Point2D fP = new Point();
		Point2D rP = new Point();

		double distance_max = Integer.MIN_VALUE;
		double relativeDistance = 0;
		int farthestPointIndex = -1;
		int insertPosition = convexHullSet.indexOf(maxPoint);

		ArrayList<Point2D> set1 = new ArrayList<Point2D>();
		ArrayList<Point2D> set2 = new ArrayList<Point2D>();		
		
		if (pointsSet.size()==0){
			return ;
		}
		if (pointsSet.size()==1){
			Point2D p = pointsSet.get(0);
			pointsSet.remove(p);
			convexHullSet.add(insertPosition, p);
			return;
		}

		for (int i=0; i<pointsSet.size(); i++){	
			Point2D m =pointsSet.get(i);				
			relativeDistance=(maxPoint.getX()-minPoint.getX())*(minPoint.getY()-m.getY())-(maxPoint.getY()-minPoint.getY())*(minPoint.getX()-m.getX());
			if (relativeDistance < 0){
				relativeDistance= -relativeDistance;
			}

			if (relativeDistance > distance_max){			
				distance_max =relativeDistance;
				farthestPointIndex=i;			
			}		
		}
		
		fP=pointsSet.get(farthestPointIndex);
		convexHullSet.add(insertPosition,fP);
		pointsSet.remove(farthestPointIndex);
		
		for (int i=0; i<pointsSet.size(); ++i){
			rP = pointsSet.get(i);
			double crossProduct = (fP.getX()-minPoint.getX())*(rP.getY()-minPoint.getY()) - (fP.getY()-minPoint.getY())*(rP.getX()-minPoint.getX());
			if (crossProduct >= 0){
				set1.add(rP);
			}
		}
			
		for (int i=0; i<pointsSet.size(); ++i){
			rP = pointsSet.get(i);
			double crossProduct = (maxPoint.getX()-fP.getX())*(rP.getY()-fP.getY()) - (maxPoint.getY()-fP.getY())*(rP.getX()-fP.getX());
			if (crossProduct >= 0){
				set2.add(rP);	
			}
		}
		if (set1.size()!=0){
			singleSideHullSet(set1, minPoint, fP, convexHullSet); 
		}
		if (set2.size()!=0){
			singleSideHullSet(set2,fP,maxPoint,convexHullSet);
		}
			
	}

	//@Override
	/*
	 * Returns the minimum bounding box that contains all the given points
	 * @param an array list of integers
	 * @return an array of points representing the minimum bounding box of the input points
	 */
	public Point2D[] cceGeomMinBB(ArrayList<Point2D> points) {
		
		Point2D[] minBB = new Point2D[2];
		double minX= Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double minY= Integer.MAX_VALUE;
		double maxY = Integer.MIN_VALUE;

		for (int i=0; i<points.size(); ++i){
			if (points.get(i).getX() < minX){
				minX=points.get(i).getX();
			}
			if (points.get(i).getX() > maxX){
				maxX=points.get(i).getX();
			}
			if (points.get(i).getY() < minY){
				minY=points.get(i).getY();
			}
			if (points.get(i).getY() > maxY){
				maxY=points.get(i).getY();
			}
		}

		//Point2D topLeft = new Point2D(minX,maxY);
		//Point2D bottomRight = new Point2D(maxX,minY);

		Point2D topLeft = null;
		minBB[0]=topLeft;
		Point2D bottomRight = null;
		minBB[1]=bottomRight;
		return minBB;      
	}
	
	//@Override
	/*
	 * Returns the range of a strings' ArrayList
	 * @param an array list of strings
	 * @return an ArrayList of strings showing the mode of the input strings
	 */
	public ArrayList<String> cceStringMode(ArrayList<String> inputValuesList) {

		Hashtable <String, Integer> frequencyMap = new Hashtable<String, Integer>();
		ArrayList<String> finalList = new ArrayList<String>();

		ArrayList<String> mode = new ArrayList<String>();
		int max=0;
		for (int i=0; i<inputValuesList.size(); i++){
			if (finalList.contains(inputValuesList.get(i))){
				int elementCount = Integer.parseInt(frequencyMap.get(inputValuesList.get(i)).toString());
				elementCount++;
				frequencyMap.put(inputValuesList.get(i), elementCount);				
				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				finalList.add(inputValuesList.get(i));
				frequencyMap.put(inputValuesList.get(i), 1);
			}	
		}

		Enumeration<String> e = frequencyMap.keys();

		while(e.hasMoreElements()){
			String curString = e.nextElement();
			if (frequencyMap.get(curString)==max){
				mode.add(curString.toString());
			}
		}

		return mode;
	}
	
	//method for splitting the LOCATION string. Location should be in String representation of a pair of double  values
	public static ArrayList<Point2D> splitString (String s){
		
		Point2D.Double p = new Point2D.Double();
		double x=0.0;
		double y=0.0;
		int l = 0;
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		String[] splited_string = s.split(",");
		
		System.out.println("The size of splitted string is "+splited_string.length);
		
		for (int k = 0; k< splited_string.length -1; k++){
			x = Double.parseDouble(splited_string[l]);
			y = Double.parseDouble(splited_string[l+1]);
			l=l+2;
			p.setLocation(x, y);
			points.add(p);
			//returns the point with the coordinates			
		}
			return points;	
				
	}

	//@Override
	public void cceSpecial1() {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void cceSpecial2() {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void cceSpecial3() {
		// TODO Auto-generated method stub
		
	}


}
