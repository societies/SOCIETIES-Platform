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
package org.societies.context.evaluation.broker;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeBond;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;

import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * This class provides examples for using the internal Context Broker in OSGi. 
 */
@Service
public class CtxBrokerEvaluation {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerEvaluation.class);

	/** The Internal Context Broker service reference. */
	private ICtxBroker internalCtxBroker;
	private ICommManager commMgrService;

	CtxBrokerEvaluation(){
	
	}
	
	@Autowired(required=true)
	public CtxBrokerEvaluation(ICtxBroker internalCtxBroker, ICommManager commMgr) {


		
		this.internalCtxBroker = internalCtxBroker;
		LOG.info("*** CtxBroker service "+this.internalCtxBroker);

		this.commMgrService = commMgr;
		LOG.info("*** commMgrService instantiated "+this.commMgrService);

		System.out.println("CtxBrokerEvaluation");
		this.readData("./realityMining.xls");

	}

	private void readData(String file){
		
		//ReadExcel test = new ReadExcel();

		//test.setInputFile("./realityMining.xls");

		try {
			HashMap<Integer, List<Integer>> data = xlsReader(file);

			System.out.println("CtxBrokerEvaluation data "+data);	
			
			for( Integer i : data.keySet()){
				String identityString = "identity_"+i+"@societies.local";
				
			//	IIdentity cssIDx =  this.commMgrService.getIdManager().fromJid(identityString);
			//	IndividualCtxEntity indiEnt = (IndividualCtxEntity) this.internalCtxBroker.createIndividualEntity(cssIDx, CtxEntityTypes.PERSON);
			//	System.out.println(indiEnt.getId());
			}
			
			
			
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	
	
	
	public HashMap<Integer, List<Integer>> xlsReader(String inputFile) throws IOException, BiffException {

		HashMap<Integer, List<Integer>> mapOfContextData = new HashMap<Integer, List<Integer>>();
		
		File inputWorkbook = new File(inputFile);
		Workbook w;
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);
			Integer key = 0;
			// Loop over column and lines
			// get the labels of the columns


			for (int j = 0; j < sheet.getColumns(); j++) {
				List<Integer> data = new ArrayList<Integer>();
				key=j;


				// Cell cell = sheet.getCell(j, 0); //the first row only == the label
				// if (cell.getType() == CellType.LABEL) {
				//     contextAttribute = cell.getContents();
				// }

				//for each column store the data
				for (int i = 0; i < sheet.getRows(); i++) { //from the second row and on
					Cell cell = sheet.getCell(j, i);
					System.out.println("key:"+j);
					System.out.println("cell column:"+cell.getColumn());
					System.out.println("cell getContents:"+cell.getContents());
					if (cell.getContents().equals("1") ) {
						data.add(i+1); 
					}

				}
				mapOfContextData.put(key,data);

			}
			System.out.println("sheet.getColumns():"+sheet.getColumns());
			System.out.println("sheet.getRows():"+sheet.getRows());
		} catch (BiffException e) {
			e.printStackTrace();
		}

		return mapOfContextData;
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
		CtxBrokerEvaluation cmEval = new CtxBrokerEvaluation();
		cmEval.readData("./realityMining.xls");
	}
	
	
}