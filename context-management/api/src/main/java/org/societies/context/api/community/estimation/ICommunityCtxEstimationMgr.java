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
package org.societies.context.api.community.estimation;

import java.awt.Point;
import java.util.ArrayList;

/**
 * @author yboul
 * @created 12-Nov-2011 7:15:14 PM
 */
public interface ICommunityCtxEstimationMgr {
	
	
	
	public double cceNumMean(ArrayList<Integer> inputValuesList);
	
	public double cceNumMedian(ArrayList<Integer> inputValuesList);
	
	public ArrayList<Integer> cceNumMode(ArrayList<Integer> inputValuesList);
	
	public Integer[] cceNumRange(ArrayList<Integer> inputValuesList);
	
	public ArrayList<Point> cceGeomConvexHull(ArrayList<Point> points);
	
	public Point[] cceGeomMinBB(ArrayList<Point> points);
	
	public ArrayList<String> cceStringMode(ArrayList<String> inputValuesList);
	
	public void cceSpecial1();
	
	public void cceSpecial2();
	
	public void cceSpecial3();
	

//	/**
//	 * 
//	 * @param estimationModel
//	 * @param list
//	 * @return 
//	 * @since 0.0.1
//	 */
//	public Integer estimateContext(EstimationModels estimationModel, List<CtxAttribute> list);
//	
//	public Hashtable<String, Integer> calculateStringAttributeStatistics(List<CtxAttribute> list);
//	
//	public void estimateContext(EstimationModels estimationmodel, CtxAttribute type, CtxIdentifier cisId);
//		

//	/**
//	 * 
//	 * @param Current
//	 * @param communityID
//	 * @param list
//	 * @since 0.0.1
//	 */
//	public void retrieveCurrentCisContext(boolean Current, CtxEntityIdentifier communityID, List<CtxAttribute> list);
//
//	/**
//	 * 
//	 * @param Current
//	 * @param communityID
//	 * @param list
//	 * @since 0.0.1
//	 */
//	public void retrieveHistoryCisContext(boolean Current, CtxEntityIdentifier communityID, List<CtxAttribute> list);
//
//	/**
//	 * 
//	 * @param estimatedContext
//	 * @since 0.0.1
//	 */
//	public void updateContextModelObject(CtxEntity estimatedContext);

}