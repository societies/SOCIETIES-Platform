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
package org.societies.personalisation.socialprofiler.datamodel;

import org.neo4j.graphdb.Traverser;
import org.societies.personalisation.socialprofiler.datamodel.behaviour.Profile;


public interface SocialPerson {

	
	public final String ROOT = "ROOT";



	/**
	 * Set a new profile in the Person Behaviour
	 * @param profile Settings
	 */
	public void addProfile(Profile profile);
	
	/**
     * Returns the percentage of a profile
     * this tell an idea about how narcissist the person is
     * @return String percentage
     * 			
     */
	public String getProfilePercentage(Profile.Type profileType);
	
	
	/**
	 * Set the current Percentage value for a specific Profile
	 * @param profileType profile identifier
	 * @param numberOfActions related to this Profile
	 */
	public void setProfilePercentage(Profile.Type profileType, String numberOfActions);
	
	/**
     * returns the total number of actions this user realized on the Social Network
     * this may be taken into consideration when comparing 2 users with similar percentages
     * @return String total number
     */
	public String getTotalNumberOfActions();
	
	
	/**
     * sets the total number of actions this user realized on a Social Network
     * @param totalActions
     *            number of actions realized
     */
	void setTotalNumberOfActions( String totalActions );
	

	/**
     * returns the name of the person
     * @return person name
     */
	public String getName();
	
	/**
     * Sets the person name.
     * @param name
     *            name of person
     */
	void setName( String name );
	
	
	
	/**
     * returns the friends of the person
     * @return listFriends
     */
	public Traverser getFriends();	

	
	
	/**
     * returns the friends of the person+friends of the friends till depth n
     * @return listFriends
     */
	public Traverser getFriends(final Integer depth);
	
	
	
	/**
	 * returns the betweeness parameter of the person
	 * @return double - Betweeness_parameter
	 */
	public double getParamBetweenessCentr ();
	
	
	
	/**
	 * sets the betweeness parameter of the person
	 * the first time it will be set with undefined in order not to have null error execption
	 * or notFOundException
	 * @param value
	 */
	public void setParamBetweenessCentr (double value);
	

	/**
	 * returns the eigenvetor centrality parameter of the person
	 * @return double - eigenvector_parameter
	 */
	public double getParamEigenVectorCentr ();
	
	
	
	/**
	 * sets the eigenvector parameter of the person
	 * the first time it will be set with undefined in order not to have null error execption
	 * or notFOundException
	 * @param value
	 */
	public void setParamEigenVectorCentr (double value);
	
	/**
	 * returns the closeness centrality parameter of the person
	 * @return double - eigenvector_parameter
	 */
	public int getParamClosenessCentr ();
	
	
	
	/**
	 * sets the closeness parameter of the person
	 * the first time it will be set with undefined in order not to have null error execption
	 * or notFOundException
	 * @param value
	 */
	public void setParamClosenessCentr (int value);

}
