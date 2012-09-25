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
package org.societies.css.devicemgmt.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.css.devicemgmt.controller.gui.PressureMatMonitorGUI;
import org.societies.css.devicemgmt.controller.model.Controller;
import org.societies.css.devicemgmt.controller.model.IPluggableResource;
import org.societies.css.devicemgmt.controller.model.PressureMat;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class ContextDataManager {

	private final ICtxBroker ctxBroker;

	private List<Controller> controllers;
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private List<PressureMatMonitorGUI> guis;
	public ContextDataManager(ICtxBroker ctxBroker, List<PressureMatMonitorGUI> guis){
		this.ctxBroker = ctxBroker;
		this.controllers = new ArrayList<Controller>();
		this.guis = guis;

		
	}
	
	
	
	public void updateContext(String controllerId, String resourceId, Object value){
		this.updateGUI(controllerId, resourceId, value);
		this.logging.debug("Updating context");
		for (Controller controller : controllers){
			if (controller.getControllerId().equalsIgnoreCase(controllerId)){
				this.logging.debug("Found controller with id: "+controllerId);
				for (IPluggableResource resource : controller.getPluggableResources()){
					if (resource.getPortId().equalsIgnoreCase(resourceId)){
						this.logging.debug("Found pressureMat with id: "+resourceId);
						try {
							CtxAttribute ctxAttr = (CtxAttribute) ctxBroker.retrieve(resource.getCtxId()).get();
							if (resource.getValueType().equals(CtxAttributeValueType.INTEGER)){
								//int intValue = Integer.parseInt((String) value);
								ctxAttr.setIntegerValue((Integer) value);
								
							}
							else if (resource.getValueType().equals(CtxAttributeValueType.STRING)){
								ctxAttr.setStringValue((String) value);
								
							}else if (resource.getValueType().equals(CtxAttributeValueType.DOUBLE)){
								ctxAttr.setDoubleValue((Double) value);
							}
							else if (resource.getValueType().equals(CtxAttributeValueType.BINARY)){
								ctxAttr.setBinaryValue(SerialisationHelper.serialise((Serializable) value));
							}
							
							ctxBroker.update(ctxAttr);
							this.logging.debug("Updated ctxAttribute: "+ctxAttr.getId());
							//ctxBroker.updateAttribute(resource.getCtxId(), value);
							return;
						} catch (CtxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

	}
	
	
	
	private void updateGUI(String controllerId, String resourceId, Object value) {
		for (PressureMatMonitorGUI gui : this.guis){
			if (gui.getControllerID().equalsIgnoreCase(controllerId)){
				gui.updatePressureMatInfo(resourceId, value.toString());
			}
		}
		
	}



	public CtxEntityIdentifier createControllerEntity(String contextType){

		 try {
				//first we are going to check if it's already stored from previous runs
			 List<CtxIdentifier> list = ctxBroker.lookup(CtxModelType.ENTITY, contextType).get();
			if (list.size()==0){
				return ctxBroker.createEntity(contextType).get().getId();
			}else{
				return (CtxEntityIdentifier) list.get(0);
			}
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
		 

		return null;
	}
	
	public CtxAttributeIdentifier createPressureMatAttribute(CtxEntityIdentifier scope, String contextType){
	
		try {
			//first we are going to check if it's already stored from previous runs
			List<CtxIdentifier> list = ctxBroker.lookup(CtxModelType.ATTRIBUTE, contextType).get();
			if (list.size()==0){
				CtxAttribute ctxAttr = ctxBroker.createAttribute(scope, contextType).get();
				ctxAttr.setValueType(CtxAttributeValueType.INTEGER);
				return ctxAttr.getId();
			}else{
				return (CtxAttributeIdentifier) list.get(0);
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
		return null;
	}

	public void addController(Controller controller) {
		this.controllers.add(controller);
	}
}
