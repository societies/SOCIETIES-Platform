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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.context.model.CtxUIElement;
import org.societies.webapp.models.ContextForm;
import org.societies.webapp.models.ContextModel;
import org.societies.webapp.models.ErrorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class ContextController {

	private static final Logger logger = LoggerFactory.getLogger(ContextController.class);

	private ContextModel contextModel;

	private static final String ACTION_LOOKUP		 = "lookup";
	private static final String ACTION_RETREIVE		 = "retreive";

	private static final String CTX_ENTITY_RESULTS	= "entity_results";
	private static final String CTX_ATTR_RESULTS	= "attr_results";
	private static final String CTX_ASSO_RESULTS	= "asso_results";

	@Autowired
	private ICtxBroker internalCtxBroker;

	public ICtxBroker getInternalCtxBroker() {
		return internalCtxBroker;
	}

	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {
		this.internalCtxBroker = internalCtxBroker;
	}

	@RequestMapping(value = "/context.html", method = RequestMethod.GET)
	public ModelAndView ContextService(@Valid ContextForm ctxForm, BindingResult result, HttpServletRequest req) {

		try{
			if(result.hasErrors()){
				throw new Exception(result.getGlobalError().toString());
			}
			
			req.getSession().removeAttribute("path_parent");
			contextModel = new ContextModel(internalCtxBroker);

			/*if (result.hasErrors()) {

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
				contextModel.setCtxForm(ctxForm);
				contextModel.setError(errorMessage);

				return new ModelAndView("context", "model", contextModel);
			}*/
			
			logger.info("====== CONTEXT GUI --> GET");

			if(ctxForm!= null && ctxForm.getViewType()!= null){
				contextModel.setViewType(ctxForm.getViewType());
			}

			switch(contextModel.getViewType()){
			case LINKED_ENTITIES:
				contextModel.setEntity_results(getRoots(contextModel.getEntityTypes()));
				break;
			case ALL_ENTITIES:
			default:
				contextModel.setEntity_results(getAllCtxEntityData(contextModel.getEntityTypes()));
			}
			
		}catch(Exception e){
			logger.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			return new ModelAndView("contextError","error", error);	
		}

		return new ModelAndView("context","model", contextModel);
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/context.html", method = RequestMethod.POST)
	public ModelAndView serviceDiscovery(@Valid ContextForm ctxForm, BindingResult result, HttpServletRequest req) {

		try{
			if(result.hasErrors()){
				throw new Exception(result.getGlobalError().toString());
			}
			
			logger.info("====== CONTEXT GUI --> POST");

			String method 	 = ctxForm.getMethod();
			String id		 = ctxForm.getCtxID();
			if(id == null || id.length()<=0){
				contextModel = new ContextModel(internalCtxBroker);
			} else {
				contextModel = new ContextModel(internalCtxBroker,id);
			}

			contextModel.setViewType(ctxForm.getViewType());
			contextModel.setPath((List<HashMap<String, String>>)req.getSession().getAttribute("path_parent"));
			req.getSession().setAttribute("path_parent",contextModel.getPath_parent());
			
			if(ctxForm.getPathIndex()>=0){
				contextModel.setIndexPathToCut(ctxForm.getPathIndex());
				req.getSession().setAttribute("path_parent",contextModel.getPath_parent());
			}
			
			if(contextModel.isIs_association()){
				contextModel.setEntity_link(getAllCtxEntityData(contextModel.getEntityTypes()));
			}

			/*if (result.hasErrors()) {

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
				contextModel.setCtxForm(ctxForm);
				contextModel.setError(errorMessage);

				return new ModelAndView("context", "model", contextModel);
			}*/

			logger.info("Method:"+ method +"Passed Ctx ID:"+id);

			if (ACTION_LOOKUP.equalsIgnoreCase(method)){
				logger.info("Lookup (model: "  +  ctxForm.getModel() + ", type:"+ctxForm.getType()+")");
				contextModel.setEntity_results(lookup(ctxForm.getModel(), ctxForm.getType()));
			}
			else if(ACTION_RETREIVE.equalsIgnoreCase(method)){
				logger.info("Retreive ID: "  + id);
				Map<String, List<CtxUIElement>> ctxList = retreive(ctxForm.getCtxID());
				contextModel.setEntity_results(ctxList.get(CTX_ENTITY_RESULTS));
				contextModel.setAsso_results(ctxList.get(CTX_ASSO_RESULTS));
				contextModel.setAttr_results(ctxList.get(CTX_ATTR_RESULTS));
			} else {
				switch(contextModel.getViewType()){
				case LINKED_ENTITIES:
					contextModel.setEntity_results(getRoots(contextModel.getEntityTypes()));
					break;
				case ALL_ENTITIES:
				default:
					contextModel.setEntity_results(getAllCtxEntityData(contextModel.getEntityTypes()));
				}
			}
			contextModel.setCtxForm(ctxForm);
		}
		catch(Exception e){
			logger.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			return new ModelAndView("contextError","error", error);	
		}

		return new ModelAndView("context", "model", contextModel);
	}

	@RequestMapping(value="saveModel.html", method = RequestMethod.GET)
	public ModelAndView saveModel(@Valid ContextForm ctxForm, BindingResult result, Map<String,Object> model_resp,HttpServletRequest request) throws Exception{

		String parentId = request.getParameter("parentId");
		String model_req = request.getParameter("model");
		String type = request.getParameter("type");
		String source = request.getParameter("source");
		String value = request.getParameter("value");

		logger.info("Param to save: ");
		logger.info("parentId: "+ parentId);
		logger.info("model_req: "+ model_req);
		logger.info("type: "+ type);
		logger.info("source: "+ source);
		logger.info("value: "+ value);

		CtxModelObject model = null;

		CtxIdentifier ctxIdentifier = null;
		if(parentId!= null && parentId.length()>0){
			ctxIdentifier = CtxIdentifierFactory.getInstance().fromString(parentId);
		}

		CtxModelType modelType = string2Model(model_req);
		logger.info("model type: "+modelType);

		switch (modelType) {
		case ENTITY:
			Future<CtxEntity> entity = internalCtxBroker.createEntity(type);
			logger.info("entity: "+entity);
			model = entity.get();
			logger.info("model: "+model);

			//if parent is an association I must create map from parent to child
			if(ctxIdentifier != null && ctxIdentifier.getModelType() == CtxModelType.ASSOCIATION){
				CtxAssociation ctxAssociation = (CtxAssociation)internalCtxBroker.retrieve(ctxIdentifier).get();
				ctxAssociation.addChildEntity((CtxEntityIdentifier)model.getId());
				internalCtxBroker.update(ctxAssociation);
			}

			break;
		case ASSOCIATION:
			Future<CtxAssociation> association = internalCtxBroker.createAssociation(type);
			model = association.get();
			CtxEntityIdentifier parentAsso = new CtxEntityIdentifier(parentId);
			((CtxAssociation)model).parentEntity = parentAsso;
			break;
		case ATTRIBUTE:
			CtxEntityIdentifier parentAttr = new CtxEntityIdentifier(parentId);
			Future<CtxAttribute> attribute= internalCtxBroker.createAttribute(parentAttr, type);
			model = attribute.get();
			setAttributeValue((CtxAttribute)model, value);
			break;

		default:
			break;
		}
		Future<CtxModelObject> update = internalCtxBroker.update(model);
		logger.info("update: "+update);

		model_resp.put("value", model.getId().toString());
		return new ModelAndView("ajaxResponse", model_resp);

	}

	@RequestMapping(value="updateModel.html", method = RequestMethod.GET)
	public ModelAndView updateModel(@Valid ContextForm ctxForm, BindingResult result, Map<String,String> model_resp,HttpServletRequest request) throws Exception{

		String id=request.getParameter("id");
		String model_req = request.getParameter("model");
		String value = request.getParameter("value");
		logger.info("update from id: "+id);


		CtxModelObject model = null;

		CtxModelType modelType = string2Model(model_req);
		logger.info("model type: "+modelType);


		switch (modelType) {
		case ENTITY:

			CtxEntityIdentifier ctxNttId = new CtxEntityIdentifier(id);				
			model = internalCtxBroker.retrieve(ctxNttId).get();

			break;
		case ASSOCIATION:

			CtxAssociationIdentifier ctxAssId = new CtxAssociationIdentifier(id);
			model = internalCtxBroker.retrieve(ctxAssId).get();

			break;
		case ATTRIBUTE:

			CtxAttributeIdentifier ctxAttrId = new CtxAttributeIdentifier(id);
			model = internalCtxBroker.retrieve(ctxAttrId).get();
			setAttributeValue((CtxAttribute)model, value);

			break;

		default:
			break;
		}

		Future<CtxModelObject> update = internalCtxBroker.update(model);
		logger.info("update: "+update);


		model_resp.put("value", model.getId().toString());
		return new ModelAndView("ajaxResponse", model_resp);

	}

	@RequestMapping(value="deleteModel.html", method = RequestMethod.GET)
	public ModelAndView deleteModel(@Valid ContextForm ctxForm, BindingResult result, Map<String,String> model_resp, HttpServletRequest request) throws Exception{

		String id=request.getParameter("id");
		logger.info("delete from id: "+id);

		try{
			CtxIdentifier ctxIdentifier = CtxIdentifierFactory.getInstance().fromString(id);
			internalCtxBroker.remove(ctxIdentifier);
		}catch(CtxException e){
			logger.error("", e);
			throw new Exception("generic error");
		}

		return new ModelAndView("ajaxResponse", model_resp);
	}
	
	@RequestMapping(value="linkEntity.html", method = RequestMethod.GET)
	public ModelAndView linkModel(@Valid ContextForm ctxForm, BindingResult result,HttpServletRequest request) throws Exception{
		HashMap<String, Object> model = new HashMap<String, Object>();
		String parentId = request.getParameter("parentId");
		String entityIdStr = request.getParameter("entity");
		
		logger.info("==> linkModel ("+parentId+", "+entityIdStr+")");
		
		CtxAssociationIdentifier assoId = (CtxAssociationIdentifier)CtxIdentifierFactory.getInstance().fromString(parentId);
		CtxEntityIdentifier entityId = (CtxEntityIdentifier)CtxIdentifierFactory.getInstance().fromString(entityIdStr);
		CtxEntity entity = (CtxEntity)internalCtxBroker.retrieve(entityId).get();
		CtxAssociation asso = (CtxAssociation)internalCtxBroker.retrieve(assoId).get();
		
		asso.addChildEntity(entityId);
		internalCtxBroker.update(asso);
		
		model.put("value", entity.getType().toString());

		return new ModelAndView("ajaxResponse",model);

	}

	/**
	 * Retreive all ENTITY Values in the internalCtxBroker
	 * @return List<CtxUIElement> that provide the serialized obj to be rendered in the GUI
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private List<CtxUIElement> getAllCtxEntityData(List<String> entityTypes) 
			throws InterruptedException, ExecutionException, CtxException {

		List<CtxUIElement> results  = new ArrayList<CtxUIElement>();

		for (String type: entityTypes){
			results.addAll(lookup(CtxModelType.ENTITY.toString() , type));
		}
		
		//workaround to take source
		if(results.size()>0){
			modifySource(results.get(0).getSource());
		}

		logger.info("number of found element: "+results.size());
		return results;
	}


	private Map<String,List<CtxUIElement>> retreive(String ctxID) 
			throws InterruptedException, ExecutionException, CtxException{

		logger.info("ContextGUI ACTION:Retrieve    [id] => "+ctxID);
		HashMap<String, List<CtxUIElement>> results = new HashMap<String, List<CtxUIElement>>();
		if (ctxID.length()==0){
			logger.warn("Context ID is empty");
			return results;
		}

		CtxIdentifier 			ctxIdentifier 	= CtxIdentifierFactory.getInstance().fromString(ctxID);
		CtxModelObject  		ctxModel 		= internalCtxBroker.retrieve(ctxIdentifier).get();
		List<CtxAttribute> 		attrList 		= new ArrayList<CtxAttribute>();
		List<CtxAssociation> 	assoList 		= new ArrayList<CtxAssociation>();
		List<CtxEntity>			entityList 		= new ArrayList<CtxEntity>();


		if (ctxModel.getModelType().equals(CtxModelType.ENTITY)) {

			CtxEntity entity = (CtxEntity)ctxModel;
			attrList.addAll(entity.getAttributes());
			logger.info("added "+attrList.size()+" attributes");

			Set<CtxAssociationIdentifier> associations = entity.getAssociations();
			for(CtxAssociationIdentifier aId: associations){
				assoList.add((CtxAssociation)internalCtxBroker.retrieve(aId).get());
			}
			logger.info("added "+assoList.size()+" associations");
		}

		else if (ctxModel.getModelType().equals(CtxModelType.ASSOCIATION)){
			CtxAssociation association = (CtxAssociation)ctxModel;
			// ADD PARENT
			//details.add(internalCtxBroker.retrieve(association.parentEntity).get());

			// ADD CHILDREN
			for(CtxEntityIdentifier aId:  association.getChildEntities()){
				entityList.add((CtxEntity)internalCtxBroker.retrieve(aId).get());
			}
		}

		// ADD Serialize Model
		results.put(CTX_ATTR_RESULTS, new ArrayList<CtxUIElement>());
		results.put(CTX_ASSO_RESULTS, new ArrayList<CtxUIElement>());
		results.put(CTX_ENTITY_RESULTS, new ArrayList<CtxUIElement>());
		for(CtxModelObject elm : attrList){
			results.get(CTX_ATTR_RESULTS).add(serliazeCtxModel(elm));
		}
		for (CtxModelObject elm : entityList){
			results.get(CTX_ENTITY_RESULTS).add(serliazeCtxModel(elm));
		}
		for (CtxModelObject elm : assoList){
			results.get(CTX_ASSO_RESULTS).add(serliazeCtxModel(elm));
		}
		
		//workaround to take source
		if(results.get(CTX_ATTR_RESULTS).size()>0){
			modifySource(results.get(CTX_ATTR_RESULTS).get(0).getSource());
		} else if(results.get(CTX_ENTITY_RESULTS).size()>0){
			modifySource(results.get(CTX_ENTITY_RESULTS).get(0).getSource());
		} else if (results.get(CTX_ASSO_RESULTS).size()>0){
			modifySource(results.get(CTX_ASSO_RESULTS).get(0).getSource());
		}

		return results;
	}



	private CtxModelType string2Model(String value){
		if (CtxModelType.ASSOCIATION.toString().equalsIgnoreCase(value)) return CtxModelType.ASSOCIATION;
		if (CtxModelType.ATTRIBUTE.toString().equalsIgnoreCase(value)) return CtxModelType.ATTRIBUTE;
		return CtxModelType.ENTITY;
	}

	/**
	 * LookUp Action of the context Broker
	 * @param model	Context Model
	 * @param type  Context Model type	
	 * @return Serialized list of element to be displaied
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	private List<CtxUIElement> lookup(String model, String type) throws InterruptedException, ExecutionException, CtxException{

		logger.info("Lookup for model:"+model + ", type:"+type);
		List<CtxUIElement> results = new ArrayList<CtxUIElement>();


		List<CtxIdentifier> list;
		list = internalCtxBroker.lookup(string2Model(model), type).get();
		for (CtxIdentifier id: list){
			CtxModelObject ctxModelObject = internalCtxBroker.retrieve(id).get();
			((CtxEntity)ctxModelObject).getAssociations("");
			CtxUIElement ctxBean = serliazeCtxModel(internalCtxBroker.retrieve(id).get());
			results.add(ctxBean);				
		}
		logger.info("values found for "+type+": "+results.size());

		//workaround to take source
		if(results.size()>0){
			modifySource(results.get(0).getSource());
		}
		
		//logger.info("Elements:"+results.size());
		return results;
	}

	private List<CtxUIElement> getRoots(List<String> entityTypes) throws CtxException, InterruptedException, ExecutionException {

		List<CtxUIElement> results  = new ArrayList<CtxUIElement>();

		for (String type: entityTypes){
			List<CtxIdentifier> list = internalCtxBroker.lookup(CtxModelType.ENTITY, type).get();

			logger.info("number of entity: "+list.size());
			for(CtxIdentifier id : list){
				CtxEntity  entity = (CtxEntity)internalCtxBroker.retrieve(id).get();
				Set<CtxAssociationIdentifier> associations = entity.getAssociations();

				logger.info("entity id: "+id+" number of ass: "+associations.size());
				if(associations.size()<=0){
					results.add(serliazeCtxModel(entity));
				} else {

					//if entity is parent in every association, it is root!
					boolean isRoot= true;
					for(CtxAssociationIdentifier assocId : associations){
						CtxAssociation assoc = (CtxAssociation)internalCtxBroker.retrieve(assocId).get();

						if(!assoc.getParentEntity().toString().equals(entity.getId().toString())){
							logger.info(id+" has parent: "+assoc.getParentEntity().toString());
							isRoot = false;
						}
					}
					if(isRoot){
						results.add(serliazeCtxModel(entity));
					}
				}
			}

		}
		
		//workaround to take source
		if(results.size()>0){
			modifySource(results.get(0).getSource());
		}

		logger.info("number of found element: "+results.size());
		return results;


	}

	private CtxUIElement serliazeCtxModel(CtxModelObject elm){

		CtxUIElement ctxBean = new CtxUIElement();
		if(elm!= null){
			logger.info("element: "+elm);
			String ctxValue =  elm.getId().toString().replace("context://", "");
			String info[] = ctxValue.split("/");

			// Log info
			logger.info("====> Found new Element ");
			logger.info("FULL ID:"+elm.getId().toString());
			logger.info("Source:"+info[0]);
			logger.info("Model:"+info[1]);
			logger.info("Type:"+info[2]);
			logger.info("ID:"+info[info.length-1]);
			logger.info("ID_noSpecChar:"+elm.getId().toString().replaceAll("\\W", ""));
			logger.info("==== ");


			ctxBean.setId(elm.getId().toString());
			ctxBean.setType(elm.getType());
			ctxBean.setDiplayId(info[info.length-1]);
			ctxBean.setId(elm.getId().toString());
			ctxBean.setSource(info[0]);   // android.societies.local;
			ctxBean.setQuality("");
			ctxBean.setModel(elm.getModelType().toString());
			ctxBean.setIdNoSpecChar(elm.getId().toString().replaceAll("\\W", ""));


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

				for(CtxEntityIdentifier assocID: assoc.getChildEntities()){

					childs += "[Child] " +genLink(assocID.getUri(),  assocID.getUri()) + "\n";
				}

				String print_value  ="";

				if (assoc.getParentEntity()!=null){
					print_value = "[Parent] "+genLink(assoc.getParentEntity().getUri(), assoc.getParentEntity().getUri())+ "\n\n";
				}

				print_value +=  childs; 
				ctxBean.setValue(print_value);

			}
		}
		return ctxBean;

	}	

	private String genLink(String id, String label){
		return "<a href='#' onclick='retrieve(\""+ id +"\");'>"+label+"</a>";
	}


	private void setAttributeValue(CtxAttribute attr,String value){
		attr.setValueType(getCtxAttributeValueType(value));
		switch(attr.getValueType()){
		case INTEGER:
			logger.info("value setted as integer");
			attr.setIntegerValue(Integer.valueOf(value));
			break;
		case DOUBLE:
			logger.info("value setted as double");
			attr.setDoubleValue(Double.valueOf(value));
			break;
		case STRING:
			logger.info("value setted as string");
			attr.setStringValue(value);
			break;
		default: 
			logger.info("value not setted");
			break;
		}
	}

	private CtxAttributeValueType getCtxAttributeValueType(String value){
		logger.info("searching type for value: "+value+"...");
		if(value == null || value.length()<=0){
			logger.info("Attribute is: " +CtxAttributeValueType.EMPTY);
			return CtxAttributeValueType.EMPTY;
		}

		if(isNumber(value)){
			try{
				Integer.parseInt(value);
				logger.info("Attribute is: " +CtxAttributeValueType.INTEGER);
				return CtxAttributeValueType.INTEGER;
			} catch (Exception dropped){}
			try{
				Double.parseDouble(value);
				logger.info("Attribute is: " +CtxAttributeValueType.DOUBLE);
				return CtxAttributeValueType.DOUBLE;
			} catch (Exception dropped){}
		}
		logger.info("Attribute is: " +CtxAttributeValueType.STRING);
		return CtxAttributeValueType.STRING;

	}

	private static boolean isNumber(String num){
		return num.matches("^[-+]?[0-9]+(\\.?[0-9]+)?$");
	}
	
	private void modifySource(String source){
		contextModel.setSource(source);
	}
}
