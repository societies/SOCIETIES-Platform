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

import java.util.HashMap;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.societies.personalisation.socialprofiler.datamodel.behaviour.Profile;
import org.societies.personalisation.socialprofiler.datamodel.behaviour.RelTypes;



public class PersonImpl implements Person, NodeProperties/*,DirtyMaker*/ {

	
	private final Node underlyingNode;
	private HashMap<Integer,Profile> behaviour = new HashMap<Integer, Profile>();
	
	
	/**
	 * @param underlyingNode
	 */
	public PersonImpl(final Node underlyingNode) {
		this.underlyingNode = underlyingNode;
	}

	
	
	public Node getUnderlyingNode()
    {
        return this.underlyingNode;
    }

	
	//@Override
	public final String getName() {
		return (String) underlyingNode.getProperty( NAME_PROPERTY );
	}

	
	//@Override
	public void setName(final String name) {
		underlyingNode.setProperty( NAME_PROPERTY, name );
	}
	
	
	
	//@Override
	public double getParamBetweenessCentr() {
		return  (double) Double.parseDouble(underlyingNode.getProperty(PARAM_BETWEEN_PROPERTY).toString());
		//parameter_betweeness_centr;
	}

	
	//@Override
	public void setParamBetweenessCentr(double value) {
		underlyingNode.setProperty(PARAM_BETWEEN_PROPERTY, value);
	}
	
	//@Override
	public double getParamEigenVectorCentr() {
		return  (double) Double.parseDouble(underlyingNode.getProperty(PARAM_EIGENVECTOR_PROPERTY).toString());
		//parameter_betweeness_centr;
	}

	
	//@Override
	public void setParamEigenVectorCentr(double value) {
		underlyingNode.setProperty(PARAM_EIGENVECTOR_PROPERTY, value);
	}
	
	
	//TODO add function to interface
	public int getParamClosenessCentr(){
		return  (int) Integer.parseInt((underlyingNode.getProperty(PARAM_CLOSENESS_PROPERTY)).toString());
	}
	
	//TODO add function to interface
	public void setParamClosenessCentr(int value){
		underlyingNode.setProperty(PARAM_CLOSENESS_PROPERTY, value);
	}
	
	//@Override
	public Traverser getFriends() {
		Traverser friendsTraverser = underlyingNode.traverse(
				Traverser.Order.BREADTH_FIRST,
				StopEvaluator.DEPTH_ONE,
				ReturnableEvaluator.ALL_BUT_START_NODE,
				RelTypes.IS_FRIEND_WITH,
				Direction.BOTH );
		return friendsTraverser;
	}

	
	//@Override
	public Traverser getFriends(final Integer depth) {
		Traverser friendsTraverser = underlyingNode.traverse(
				Traverser.Order.BREADTH_FIRST,
				new StopEvaluator(){

					//@Override
					public boolean isStopNode(TraversalPosition current_position) {
						if (current_position.depth()>= depth) {
							return true;
						}
						else{
							return false;
						}
					}
					
				},
				ReturnableEvaluator.ALL_BUT_START_NODE,
				RelTypes.IS_FRIEND_WITH,
				Direction.BOTH );
		return friendsTraverser;
	}
   
	
    @Override
    public int hashCode()
    {
        return this.underlyingNode.hashCode();
    }


    //////////////   PROFILE IMPLEMENTATION
    
    
	@Override
	public int getProfilePercentage(int profileID) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setProfilePercentage(int profileID, int numberOfActions) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public String getTotalNumberOfActions() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void setTotalNumberOfActions(int totalActions) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void addProfile(Profile profile) {
		
		
	}



	



}
