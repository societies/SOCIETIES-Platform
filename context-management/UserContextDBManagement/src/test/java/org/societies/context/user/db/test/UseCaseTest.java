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
package org.societies.context.user.db.test;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelObject;
import org.societies.context.user.db.impl.UserCtxDBMgr;

public class UseCaseTest {

	static UserCtxDBMgr userDB ;
	static CtxEntity entity;
	static CtxAttribute attribute;
	static CtxModelObject modObj;

	UseCaseTest(){
		userDB = new UserCtxDBMgr();

		System.out.println("start testing");
		testCreateIndividualCtxEntity();
		testCreateAttribute();
		testRetrieveAttribute();
		testUpdateAttribute();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new UseCaseTest(); 
	}


	private void testCreateIndividualCtxEntity(){
		System.out.println("---- testCreateIndividualCtxEntity");
		entity = userDB.createIndividualCtxEntity("person");
//		entity = callback.getCtxEntity();
	}

	private void testCreateAttribute(){
		System.out.println("---- testCreateAttribute");
		modObj = userDB.createAttribute(entity.getId(), CtxAttributeValueType.INDIVIDUAL, "name");
		attribute = (CtxAttribute) modObj;
//		attribute = callback.getCtxAttribute();
	}

	private void testRetrieveAttribute(){
		System.out.println("---- testRetrieveAttribute");
		modObj = userDB.retrieve(attribute.getId());
		attribute = (CtxAttribute) modObj;
//		attribute = (CtxAttribute) callback.getCtxModelObject();
	}

	private void testUpdateAttribute(){
		System.out.println("---- testUpdateAttribute");
		modObj = userDB.retrieve(attribute.getId());
		attribute = (CtxAttribute) modObj;
//		attribute = (CtxAttribute) callback.getCtxModelObject();
		attribute.setIntegerValue(5);
		userDB.update(attribute);
		//verify update
		modObj = userDB.retrieve(attribute.getId());
		attribute = (CtxAttribute) modObj;
		//		attribute = (CtxAttribute) callback.getCtxModelObject();
		System.out.println("attribute value should be 5 and it is:"+attribute.getIntegerValue());
	}
}