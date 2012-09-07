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
package org.societies.css.devicemgmt.controller.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.css.devicemgmt.controller.ContextDataManager;
import org.societies.css.devicemgmt.controller.model.Controller;
import org.societies.css.devicemgmt.controller.model.IPluggableResource;
import org.societies.css.devicemgmt.controller.model.PressureMat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class XMLReader {

	private final ContextDataManager ctxDataMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	public XMLReader(ContextDataManager ctxDataMgr){
		this.ctxDataMgr = ctxDataMgr;
		
	}
	public ArrayList<Controller> getConfiguration(File file){
		logging.debug("Reading configuration file:"+file.getPath());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			return this.readXML(doc);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		ArrayList<Controller> controllers = new ArrayList<Controller>();
		
		return controllers;
	}

	private ArrayList<Controller> readXML(Document doc) {
		ArrayList<Controller> controllers = new ArrayList<Controller>();
		doc.getDocumentElement().normalize();
		NodeList controllerNodes = doc.getElementsByTagName("controller");
		
		if (controllerNodes.getLength()>0){
			for (int i=0; i<controllerNodes.getLength(); i++){
				Element controllerElement = (Element) controllerNodes.item(i);
				String locationName = controllerElement.getAttribute("location");
				String IPAdress = controllerElement.getAttribute("IPAddress");
				String controllerId = controllerElement.getAttribute("id");
				CtxEntityIdentifier ctxEntityId = this.ctxDataMgr.createControllerEntity(controllerId);
				Controller controller = new Controller(IPAdress, controllerId, locationName, ctxEntityId);
				NodeList matNodes = controllerElement.getElementsByTagName("mats");
				if (matNodes.getLength()>0){
					Element matsElement = (Element) matNodes.item(0);
					NodeList matList = matsElement.getElementsByTagName("mat");
					for (int m = 0; m < matList.getLength(); m++){
						Element matField = (Element) matList.item(m);
						String pressureMatId  = matField.getAttribute("id");
						String pressureMatLocation = matField.getAttribute("location");
						CtxAttributeIdentifier ctxAttId = this.ctxDataMgr.createPressureMatAttribute(ctxEntityId, controllerId+"."+pressureMatId);
						PressureMat pressureMat = new PressureMat(pressureMatId, pressureMatLocation, ctxAttId);
						controller.addPluggableResource(pressureMat);
					}
				}
				controllers.add(controller);
				this.logging.debug("Added controller with id: "+controller.getControllerId());
				ctxDataMgr.addController(controller);
			}
		}

		return controllers;
	}
	
	public static void main(String[] args){
		File file = new File("C:\\Users\\Eliza\\git\\SOCIETIES-SCE-Services\\3rdPartyServices\\StudentServices\\HWUControllerDriver\\src\\main\\resources\\config\\controllerConfig.xml");
		XMLReader reader = new XMLReader(null);
		ArrayList<Controller> list = reader.getConfiguration(file);
		for (Controller controller : list){
			System.out.println(controller.getControllerId()+": "+controller.getLocationName());
			for (IPluggableResource mat: controller.getPluggableResources()){
				System.out.println(((PressureMat) mat).getPressureMatId()+": "+((PressureMat) mat).getPressureMatLocation());
			}
		}
	}
}
