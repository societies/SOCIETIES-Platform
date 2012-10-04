package org.societies.webapp.controller;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxUIElement;
import org.societies.webapp.models.ContextForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ContextController {

	
	private static final Logger logger = LoggerFactory.getLogger(ContextController.class);
	
	
	private static final String ATTRIBUTE_TYPES 	 = "attributeTypes";
	private static final String ASSOCITATION_TYPES 	 = "associationTypes";
	private static final String ENTITY_TYPES		 = "entityTypes";
	
	private static final String ACTION_LOOKUP			= "lookup";
	private static final String ACTION_RETREIVE			= "retreive";
	private static final String ACTION_EDIT				= "edit";
	private static final String ACTION_DELETE			= "delete";
	
	
	
	@Autowired
	private ICtxBroker internalCtxBroker;
	
	public ICtxBroker getInternalCtxBroker() {
		return internalCtxBroker;
	}


	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {
		this.internalCtxBroker = internalCtxBroker;
	}
	
	
	private List<String> getTypesList(Class name) {
		 Field[] 	fields = name.getDeclaredFields();
		 String[] 	names = new String[fields.length];
		 List<String> results = new ArrayList<String>();
		 for (Field field: fields){
			 results.add(field.getName().toLowerCase());
			 //logger.info("add fields "+field.getName());
		 }
		 return results;
	}
	
	

	@RequestMapping(value = "/context.html", method = RequestMethod.GET)
	public ModelAndView ContextService() {

		   logger.info("====== CONTEXT GUI --> GET");
			
			//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
			Map<String, Object> model = new HashMap<String, Object>();
			
			
			ContextForm ctxForm = new ContextForm();
			model.put("ctxForm", ctxForm);
			model.put("models", getTypesList(CtxModelType.class));
			model.put(ATTRIBUTE_TYPES, getTypesList(CtxAttributeTypes.class));
			model.put(ENTITY_TYPES, getTypesList(CtxEntityTypes.class));
			model.put(ASSOCITATION_TYPES, getTypesList(CtxAssociationTypes.class));	
			model.put("results", getAllCtxEntityData());
			
			

		return new ModelAndView("context", model);
	}
	
	
	
	
	private List<CtxUIElement> getAllCtxEntityData() {
		List<CtxUIElement> results = lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.PERSON);
		
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.COMMUNITY));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.CSS_NODE));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.DEVICE));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.ORGANISATION));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.PRIVACY_POLICY));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.SERVICE));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.PRIVACY_PREFERENCE));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.SERVICE_PARAMETER));
		results.addAll(lookup(CtxModelType.ENTITY.toString(), CtxEntityTypes.SOCIAL_NETWORK));
		
		return results;
	}


	private List<CtxUIElement> retreive(String ctxID){
		
		
		logger.info("Retrieve id:"+ctxID);
		List<CtxUIElement> results = new ArrayList<CtxUIElement>();
		
		

		try {

			if (ctxID.length()>0){
				CtxIdentifier ctxIdentifier = CtxIdentifierFactory.getInstance().fromString(ctxID);


				CtxModelObject  ctxModel = internalCtxBroker.retrieve(ctxIdentifier).get();
				if (ctxModel.getModelType().equals(CtxModelType.ENTITY)) {

					CtxEntity entity = (CtxEntity)ctxModel;
					List<CtxModelObject> details = new ArrayList<CtxModelObject>();

					details.addAll(entity.getAttributes());
					Set<CtxAssociationIdentifier> associations = entity.getAssociations();
					for(CtxAssociationIdentifier aId: associations){
						details.add(internalCtxBroker.remove(aId).get());
					}


					// Entities
					for (CtxModelObject elm: details){


						CtxUIElement ctxBean = new CtxUIElement();

						String ctxValue =  elm.getId().toString().replace("context://", "");
						String info[] = ctxValue.split("/");
						ctxBean.setSource(info[0]);   // android.societies.local;
						
						logger.info("Source:"+info[0]);
						logger.info("Model:"+info[1]);
						logger.info("Type:"+info[2]);
						logger.info("ID:"+info[3]);
						
						
						
						ctxBean.setId(elm.getId().toString());
						ctxBean.setType(elm.getType());
						ctxBean.setDiplayId(info[5]);
						
						

						if (elm.getModelType().equals(CtxModelType.ENTITY)){
							ctxBean.setModel(CtxModelType.ENTITY.toString());
							//CtxEntity entity = (CtxEntity) elm;
							ctxBean.setValue("");
						}
						else if (elm.getModelType().equals(CtxModelType.ATTRIBUTE)){
							ctxBean.setModel(CtxModelType.ATTRIBUTE.toString());
							CtxAttribute attr = (CtxAttribute) elm;
							ctxBean.setValue(attr.getStringValue());
						}
						else if (elm.getModelType().equals(CtxModelType.ATTRIBUTE)){
							ctxBean.setModel(CtxModelType.ASSOCIATION.toString());
							//CtxAssociation assoc = (CtxAssociation) elm;
							ctxBean.setValue("");
						}
						results.add(ctxBean);	

					}
				}

			}else logger.info("Context ID is null! --> retreive aborted");
				
				
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
		
			return results;
	}
	
	
	
	private CtxModelType string2Model(String value){
		if (CtxModelType.ASSOCIATION.toString().equalsIgnoreCase(value)) return CtxModelType.ASSOCIATION;
		if (CtxModelType.ATTRIBUTE.toString().equalsIgnoreCase(value)) return CtxModelType.ATTRIBUTE;
		return CtxModelType.ENTITY;
	}
	
	private List<CtxUIElement> lookup(String model, String type){
		
		
		logger.info("Lookup for model:"+model + ", type:"+type);
		List<CtxUIElement> results = new ArrayList<CtxUIElement>();
		try {
			
			
			List<CtxIdentifier> list = internalCtxBroker.lookup(string2Model(model), type).get();
			
			
			// Entities
			
			
			for (CtxIdentifier id: list){
				CtxModelObject elm =  internalCtxBroker.retrieve(id).get();
			
				
				CtxUIElement ctxBean = new CtxUIElement();
				

				String ctxValue =  elm.getId().toString().replace("context://", "");
				String info[] = ctxValue.split("/");
				
				logger.info("FULL ID:"+elm.getId().toString());
				logger.info("Source:"+info[0]);
				logger.info("Model:"+info[1]);
				logger.info("Type:"+info[2]);
				logger.info("ID:"+info[3]);
				
				
				
				ctxBean.setId(elm.getId().toString());
				ctxBean.setType(elm.getType());
				ctxBean.setDiplayId(info[3]);
				ctxBean.setId(elm.getId().toString());
				ctxBean.setSource(info[0]);   // android.societies.local;
				
				
				if (elm.getModelType().equals(CtxModelType.ENTITY)){
					ctxBean.setModel(CtxModelType.ENTITY.toString());
					CtxEntity entity = (CtxEntity) elm;
					ctxBean.setValue("");
				}
				else if (elm.getModelType().equals(CtxModelType.ATTRIBUTE)){
					ctxBean.setModel(CtxModelType.ATTRIBUTE.toString());
					CtxAttribute attr = (CtxAttribute) elm;
					ctxBean.setValue(attr.getStringValue());
					
					
				}
				else if (elm.getModelType().equals(CtxModelType.ASSOCIATION)){
					ctxBean.setModel(CtxModelType.ASSOCIATION.toString());
					CtxAssociation assoc = (CtxAssociation) elm;
					ctxBean.setValue("owner:" +assoc.getOwnerId());
				}
				
				logger.info("Element Model:"+ctxBean.getModel() + " type:"+ctxBean.getType() + " value:"+ctxBean.getValue());
				results.add(ctxBean);					
				
			
				
			}
			
		}
		catch(Exception ex){
			logger.error("Unable to make lookup:"+ex);
			ex.printStackTrace();
		}
		
		logger.info("Elements:"+results.size());
		return results;
	}
			
			
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/context.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid ContextForm ctxForm, BindingResult result, Map model) {

		logger.info("====== CONTEXT GUI --> POST");
		
		
		
		model.put("models", getTypesList(CtxModelType.class));
		model.put(ATTRIBUTE_TYPES, getTypesList(CtxAttributeTypes.class));
		model.put(ENTITY_TYPES, getTypesList(CtxEntityTypes.class));
		model.put(ASSOCITATION_TYPES, getTypesList(CtxAssociationTypes.class));	
		model.put("ctxForm", ctxForm);
		
		if (result.hasErrors()) {
			
			logger.info("====== CONTEXT GUI --> result Errors :"+ result.getErrorCount());
			String errorMessage ="<ul>";
			for (ObjectError error :result.getAllErrors()){
				
				errorMessage += "<li> Code:"+error.getCode() + " - "+error.getDefaultMessage() +"["+error.getObjectName()+"]</li>";
				
				logger.info("====== CtxGUI ===> ERROR <====");
				logger.info("====== CtxGUI --> Error code:" + error.getCode());
				logger.info("====== CtxGUI --> Error Msg :" + error.getDefaultMessage());
				logger.info("====== CtxGUI --> Error Obj :" + error.getObjectName());
				logger.info("====== CtxGUI --> Error [ALL] :" + error.toString());
				
				
			}
			errorMessage ="</ul>";
			
			model.put("error", errorMessage);
			return new ModelAndView("context", model);
		}

     	String method 	 = ctxForm.getMethod();
     	String id		 = ctxForm.getCtxID();
     	
     	logger.info("Method:"+ method +"Passed Ctx ID:"+id);
     
		
		
	
		if (ACTION_LOOKUP.equalsIgnoreCase(method)){
			
			logger.info("Lookup (model: "  +  ctxForm.getModel() + ", type:"+ctxForm.getType()+")");
			model.put("results", lookup(ctxForm.getModel(), ctxForm.getType()));
			
		}
		else if(ACTION_RETREIVE.equalsIgnoreCase(method)){
			
			logger.info("Retreive ID: "  + id);
			model.put("results", retreive(ctxForm.getCtxID()));
		}
		

		return new ModelAndView("context", model);
		

	}
}