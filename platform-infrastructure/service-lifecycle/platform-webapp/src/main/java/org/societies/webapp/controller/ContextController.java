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
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
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
	
	private static final String CTX_MODELS			 = "models";
	private static final String ATTRIBUTE_TYPES 	 = "attributeTypes";
	private static final String ASSOCITATION_TYPES 	 = "associationTypes";
	private static final String ENTITY_TYPES		 = "entityTypes";
	
	private static final String ACTION_LOOKUP		 = "lookup";
	private static final String ACTION_RETREIVE		 = "retreive";
	private static final String ACTIONS				 = "actions";
	
	private static final String[] ACTION_LIST = {ACTION_LOOKUP, ACTION_RETREIVE};
	
	
	/** Not implemented right now **/
	private static final String ACTION_EDIT			= "edit";
	private static final String ACTION_DELETE		= "delete";
	
	
	private static final String CTX_FORM			= "ctxForm";
	private static final String CTX_RESULTS			= "results";
	private static final String CTX_AVAILABLE_ID	= "idList";
	
	private static final String PARENT				= "parent";

	private static final String ATTRIBUTE_LABEL 	= "attribute_label";
	private static final String ENTITY_LABEL 		= "entity_label";
	private static final String ASSOCIATION_LABEL 	= "association_label";
	
	
	@Autowired
	private ICtxBroker internalCtxBroker;
	
	public ICtxBroker getInternalCtxBroker() {
		return internalCtxBroker;
	}


	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {
		this.internalCtxBroker = internalCtxBroker;
	}
	
	
	/** 
	 * Get the list of the enumeration type of a class as  STRINGs list
	 * @param name Input Enumeration Class
	 * @return List of Strings that compose the enumeration class.
	 */
	private List<String> getTypesList(Class name) {
		logger.info("Extracting parmas from: " + name.getCanonicalName());
		Field[] 	fields = name.getDeclaredFields();
		 List<String> results = new ArrayList<String>();
		 for (Field field: fields){
//			 if (field.isEnumConstant()){
//				 results.add(field.getName());
//				 logger.info("add  Enum: " + field.getName());
//			 } 
//			 else 
				try {
					//logger.info("This is not an Enumeration!!! " + field.get(null));
					logger.info("add " + field.get(null));
					String field_string = ""+field.get(null);
					results.add(field_string);
				} catch (IllegalArgumentException e) {
					logger.error("Error casting to String:"+e.getLocalizedMessage());
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					logger.error("Error casting to String:"+e.getLocalizedMessage());
					e.printStackTrace();
				}
			 
			 
			 //logger.info("add fields "+field.getName());
		 }
		 
		 logger.info(" Return " + results.size() + "elements");
		 return results;
	}
	
	
	
	

	@RequestMapping(value = "/context.html", method = RequestMethod.GET)
	public ModelAndView ContextService() {

		logger.info("====== CONTEXT GUI --> GET");

		//CREATE A HASHMAP OF ALL OBJECTS REQUIRED TO PROCESS THIS PAGE
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(CTX_FORM, new ContextForm());
		
		model.put(CTX_MODELS, getTypesList(CtxModelType.class));
		
		List<String> values =  getTypesList(org.societies.api.context.model.CtxAttributeTypes.class);
		values.addAll(         getTypesList(org.societies.api.internal.context.model.CtxAttributeTypes.class));
		model.put(ATTRIBUTE_TYPES, values);
		
		
		values =  getTypesList(org.societies.api.context.model.CtxEntityTypes.class);
		values.addAll(         getTypesList(org.societies.api.internal.context.model.CtxEntityTypes.class));
		model.put(ENTITY_TYPES, values);
		
		
		values =  getTypesList(org.societies.api.context.model.CtxAssociationTypes.class);
		values.addAll(         getTypesList(org.societies.api.internal.context.model.CtxAssociationTypes.class));
		model.put(ASSOCITATION_TYPES, values);	
		
		
		model.put(CTX_RESULTS, getAllCtxEntityData());
		model.put(CTX_AVAILABLE_ID, getAllIDs());		
		model.put(PARENT, "<a href='#' onclick='javascript:location.href=\"context.html\"'> ROOT </a>");
		model.put(ATTRIBUTE_LABEL, CtxModelType.ATTRIBUTE.name().toString());
		model.put(ASSOCIATION_LABEL, CtxModelType.ASSOCIATION.name().toString());
		model.put(ENTITY_LABEL, CtxModelType.ENTITY.name().toString());
		model.put(ACTIONS, ACTION_LIST);


		return new ModelAndView("context", model);
	}

	
	
	/**
	 * Retreive all ENTITY Values in the internalCtxBroker
	 * @return List<CtxUIElement> that provide the serialized obj to be rendered in the GUI
	 */
	private List<CtxUIElement> getAllCtxEntityData() {
		
		List<CtxUIElement> results  = new ArrayList<CtxUIElement>();
        
		for (String type: getTypesList(CtxEntityTypes.class)){
        	results.addAll(lookup(CtxModelType.ENTITY.toString() , type));
        }
		
        return results;
	}

	
	
	
	
	

	private List<CtxUIElement> retreive(String ctxID){

		logger.info("ContextGUI ACTION:Retrieve    [id] => "+ctxID);
		List<CtxUIElement> results = new ArrayList<CtxUIElement>();
		if (ctxID.length()==0){
			logger.warn("Context ID is empty");
			return results;
		}



		try {



			CtxIdentifier 			ctxIdentifier 	= CtxIdentifierFactory.getInstance().fromString(ctxID);
			CtxModelObject  		ctxModel 		= internalCtxBroker.retrieve(ctxIdentifier).get();
			List<CtxModelObject> 	details			= new ArrayList<CtxModelObject>();

			if (ctxModel.getModelType().equals(CtxModelType.ENTITY)) {

				CtxEntity entity = (CtxEntity)ctxModel;
				details.addAll(entity.getAttributes());
				Set<CtxAssociationIdentifier> associations = entity.getAssociations();
				for(CtxAssociationIdentifier aId: associations){
					details.add(internalCtxBroker.retrieve(aId).get());
				}
			}
			else if (ctxModel.getModelType().equals(CtxModelType.ASSOCIATION)){
				CtxAssociation association = (CtxAssociation)ctxModel;
				// ADD PARENT
				details.add(internalCtxBroker.retrieve(association.parentEntity).get());
				
				// ADD CHILDREN
				for(CtxEntityIdentifier aId:  association.getChildEntities()){
					details.add(internalCtxBroker.retrieve(aId).get());
				}
			}
			else if (ctxModel.getModelType().equals(CtxModelType.ATTRIBUTE)){
				CtxAttribute attribute = (CtxAttribute)ctxModel;
				details.add(ctxModel);
			}

			// ADD Serialize Entities
			for (CtxModelObject elm: details){
				results.add( serliazeCtxModel(elm));
			}



		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			logger.error("Error in Ctx Retrieve :" +e);
			e.printStackTrace();
		}		
		return results;
	}



		
	
	
	private CtxModelType string2Model(String value){
		if (CtxModelType.ASSOCIATION.toString().equalsIgnoreCase(value)) return CtxModelType.ASSOCIATION;
		if (CtxModelType.ATTRIBUTE.toString().equalsIgnoreCase(value)) return CtxModelType.ATTRIBUTE;
		return CtxModelType.ENTITY;
	}
	
	
	
	
	/**
	 * Get List of Ctx ID Avaliable in the internal Cxt Broker
	 * @param model   Context Model	
	 * @param type	  Context Model type
	 * @return	List<String> that contains the ID of each CtxModel + type element
	 */
	private List<String> getContextIDs(String model, String type){
		//logger.info("Get ContextID for "+model + " - " + type);
		List<String> result = new ArrayList<String>(); 
		try {
			List<CtxIdentifier> list = internalCtxBroker.lookup(string2Model(model), type).get();
			for(CtxIdentifier ctxId : list){
				result.add(ctxId.getUri());
				//logger.info("Add ID:"+ctxId.getUri());
			}
		} 
		catch (CtxException e) {
			logger.error("CtxException:"+e.getLocalizedMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	private List<String> getAllIDs(){
		
		//logger.info("Get All ctxIDs");
		List<String> result = new ArrayList<String>(); 
		
		for(String type : getTypesList(CtxAssociationTypes.class)){
			result.addAll(getContextIDs(CtxModelType.ASSOCIATION.toString(), type));
		}
		for(String type : getTypesList(CtxAttributeTypes.class)){
			result.addAll(getContextIDs(CtxModelType.ATTRIBUTE.toString(), type));
		}
		for(String type : getTypesList(CtxEntityTypes.class)){
			result.addAll(getContextIDs(CtxModelType.ENTITY.toString(), type));
		}
		return result;
	}
	
	/**
	 * LookUp Action of the context Broker
	 * @param model	Context Model
	 * @param type  Context Model type	
	 * @return Serialized list of element to be displaied
	 */
	private List<CtxUIElement> lookup(String model, String type){
		
		
		logger.info("Lookup for model:"+model + ", type:"+type);
		List<CtxUIElement> results = new ArrayList<CtxUIElement>();
			
			
			List<CtxIdentifier> list;
			try {
				list = internalCtxBroker.lookup(string2Model(model), type).get();
				for (CtxIdentifier id: list){
					CtxUIElement ctxBean = serliazeCtxModel(internalCtxBroker.retrieve(id).get());
					results.add(ctxBean);					
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
	
			//logger.info("Elements:"+results.size());
		return results;
	}
			
	
	

	private CtxUIElement serliazeCtxModel(CtxModelObject elm){
		
		
		CtxUIElement ctxBean = new CtxUIElement();
		String ctxValue =  elm.getId().toString().replace("context://", "");
		String info[] = ctxValue.split("/");

		// Log info
		logger.info("====> Found new Element ");
		logger.info("FULL ID:"+elm.getId().toString());
		logger.info("Source:"+info[0]);
		logger.info("Model:"+info[1]);
		logger.info("Type:"+info[2]);
		logger.info("ID:"+info[info.length-1]);
		logger.info("==== ");


		ctxBean.setId(elm.getId().toString());
		ctxBean.setType(elm.getType());
		ctxBean.setDiplayId(info[info.length-1]);
		ctxBean.setId(elm.getId().toString());
		ctxBean.setSource(info[0]);   // android.societies.local;
		ctxBean.setQuality("");
		ctxBean.setModel(elm.getModelType().toString());
		
		if (elm.getModelType().equals(CtxModelType.ATTRIBUTE)){
			
			CtxAttribute attr = (CtxAttribute) elm;
			logger.info("Ctx Attribute type "+attr.getValueType());
			
			
			if (attr.getValueType().equals(CtxAttributeValueType.STRING)){
				ctxBean.setValue(""  + attr.getStringValue());
			}
			else if (attr.getValueType().equals(CtxAttributeValueType.DOUBLE)){
				ctxBean.setValue(""+attr.getDoubleValue());
			}
			else if (attr.getValueType().equals( CtxAttributeValueType.INTEGER)){
				ctxBean.setValue(""+attr.getIntegerValue());
			}
			else if (attr.getValueType().equals(CtxAttributeValueType.BINARY)){
				ctxBean.setValue("Binary [" + attr.getBinaryValue().length+"bytes]");
			}
			else {
				ctxBean.setValue(" -- ");
			}
			
			if (attr.getQuality().getPrecision()!=null)
				ctxBean.setQuality("Precision:" +attr.getQuality().getPrecision());
			
		}
		else if (elm.getModelType().equals(CtxModelType.ASSOCIATION)){
			CtxAssociation assoc = (CtxAssociation) elm;
			String childs = "";
			
			int index= 1;
			for(CtxEntityIdentifier assocID: assoc.getChildEntities()){
				
				childs += "[Child] " +genLink(assocID.getUri(),  assocID.getUri()) + "\n";
				index++;
			}
			
			String print_value  ="";
			
			if (assoc.getParentEntity()!=null){
				print_value = "[Parent] "+genLink(assoc.getParentEntity().getUri(), assoc.getParentEntity().getUri())+ "\n\n";
			}
			
			print_value +=  childs; 
			ctxBean.setValue(print_value);
			
		}
		return ctxBean;
		
	}	
	
	private String genLink(String id, String label){
		return "<a href='#' onclick='retrieve(\""+ id +"\");'>"+label+"</a>";
	}

	private String generateParent(String id){
		
		if (id==null) return "<a href='#' onclick='javascript:location.href=context.html'>  - Reload -  </a>";
		String filteredID = id.replace("context://", "");
		String info[] = filteredID.split("/");
		for(String s: info) logger.debug("path:"+s);

		String path = "";
		try{
			path ="<a href='#' onclick='javascript:location.href=\"context.html\"'> "+info[0]+" </a>";
			path+="<a href='#' onclick='lookup(\""+info[1]+"\", \""+ info[2] +"\");'>"+info[1] +" /  "+info[2] + "</a>";
			path+="<a href='#' onclick='retrieve(\"context://"+info[0]+"/"+info[1] +"/" + info[2]+"/" + info[3]+ "\");'>"+info[3]+"</a>";
			if (info.length>4){
				path+="<a href='#' onclick='lookup(\""+info[4]+", "+info[5] +"\");'>"+info[4] +" / "+info[5] + "</a>";
				path+="<a href='#' onclick='retrieve(\""+ id +"\");'>"+info[6]+"</a>";
			}
		}catch(Exception e){
			logger.error("Unexpected error:"+e);
			e.printStackTrace();
		}
		return path;
		
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/context.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid ContextForm ctxForm, BindingResult result, Map model) {

		logger.info("====== CONTEXT GUI --> POST");
		
		model.put(CTX_MODELS, getTypesList(CtxModelType.class));
		
//		model.put(ATTRIBUTE_TYPES, getTypesList(CtxAttributeTypes.class));
//		model.put(ENTITY_TYPES, getTypesList(CtxEntityTypes.class));
//		model.put(ASSOCITATION_TYPES, getTypesList(CtxAssociationTypes.class));	
//		
		List<String> values =  getTypesList(org.societies.api.context.model.CtxAttributeTypes.class);
		values.addAll(         getTypesList(org.societies.api.internal.context.model.CtxAttributeTypes.class));
		model.put(ATTRIBUTE_TYPES, values);
		
		
		values =  getTypesList(org.societies.api.context.model.CtxEntityTypes.class);
		values.addAll(         getTypesList(org.societies.api.internal.context.model.CtxEntityTypes.class));
		model.put(ENTITY_TYPES, values);
		
		
		values =  getTypesList(org.societies.api.context.model.CtxAssociationTypes.class);
		values.addAll(         getTypesList(org.societies.api.internal.context.model.CtxAssociationTypes.class));
		model.put(ASSOCITATION_TYPES, values);	
		
		model.put(CTX_AVAILABLE_ID, getAllIDs());
		model.put(ATTRIBUTE_LABEL, CtxModelType.ATTRIBUTE.name().toString());
		model.put(ASSOCIATION_LABEL, CtxModelType.ASSOCIATION.name().toString());
		model.put(ENTITY_LABEL, CtxModelType.ENTITY.name().toString());
		
		
		model.put(PARENT, generateParent(ctxForm.getCtxID()));
		
		
		
		
		
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
			model.put("ctxForm", ctxForm);
			model.put("error", errorMessage);
			return new ModelAndView("context", model);
		}

     	String method 	 = ctxForm.getMethod();
     	String id		 = ctxForm.getCtxID();
     	
     	logger.info("Method:"+ method +"Passed Ctx ID:"+id);
     
		
		
	
		if (ACTION_LOOKUP.equalsIgnoreCase(method)){
			logger.info("Lookup (model: "  +  ctxForm.getModel() + ", type:"+ctxForm.getType()+")");
			model.put(CTX_RESULTS, lookup(ctxForm.getModel(), ctxForm.getType()));
		}
		else if(ACTION_RETREIVE.equalsIgnoreCase(method)){
			logger.info("Retreive ID: "  + id);
			model.put(CTX_RESULTS, retreive(ctxForm.getCtxID()));
		}
		model.put(CTX_FORM, ctxForm);
		return new ModelAndView("context", model);
		

	}
}
