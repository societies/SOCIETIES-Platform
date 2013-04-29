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
package org.societies.webapp.models;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAssociationTypes;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.api.schema.context.model.CtxUIElement;

/**
 * Describe your class here...
 *
 * @author Mirko
 *
 */
public class ContextModel {

	private static final Logger logger = LoggerFactory.getLogger(ContextModel.class);

	public enum ViewType{ALL_ENTITIES,LINKED_ENTITIES};
	private ICtxBroker internalCtxBroker;

	//costants
	private String attribute_label = CtxModelType.ATTRIBUTE.name().toString();
	private String entity_label = CtxModelType.ENTITY.name().toString();
	private String association_label = CtxModelType.ASSOCIATION.name().toString();

	private ViewType viewType; //used to switch type of view. 
	private List<String> models;
	private List<String> attributeTypes;
	private List<String> entityTypes;
	private List<String> associationTypes;
	private List<String> idList;
	private String source;
	private String parent_id;
	private List<HashMap<String, String>> path_parent = null;
	private boolean is_entity;
	private boolean is_association;
	private List<CtxUIElement> entity_results;
	private List<CtxUIElement> attr_results;
	private List<CtxUIElement> asso_results;
	private List<CtxUIElement> entity_link; //used to inflate "link to model" selection

	private Integer indexPath; //used to create context path when user clink on it.

	public ContextModel(ICtxBroker internalCtxBroker){

		this.internalCtxBroker=internalCtxBroker;

		setViewType(ViewType.ALL_ENTITIES);

		setModels(getTypesList(CtxModelType.class));

		List<String> values = getTypesList(org.societies.api.context.model.CtxAttributeTypes.class);
		values.addAll(getTypesList(org.societies.api.internal.context.model.CtxAttributeTypes.class));
		setAttributeTypes(values);


		values =  getTypesList(org.societies.api.context.model.CtxEntityTypes.class);
		values.addAll(getTypesList(org.societies.api.internal.context.model.CtxEntityTypes.class));
		setEntityTypes(values);

		values =  getTypesList(org.societies.api.context.model.CtxAssociationTypes.class);
		values.addAll(getTypesList(org.societies.api.internal.context.model.CtxAssociationTypes.class));
		setAssociationTypes(values);

		setIdList(getAllIDs());

		setParent_id("");
		setIs_entity(false);
		setIs_association(false);

	}

	public ContextModel(ICtxBroker internalCtxBroker, String ctxId) throws MalformedCtxIdentifierException{
		this(internalCtxBroker);

		logger.debug("ctxId: "+ctxId);
		setParent_id(ctxId);

		
		//identifying model type
		CtxIdentifier ctxIdentifier = CtxIdentifierFactory.getInstance().fromString(ctxId);
		if(ctxIdentifier.getModelType() == CtxModelType.ENTITY){
			setIs_entity(true);
		} else if(ctxIdentifier.getModelType() == CtxModelType.ASSOCIATION){
			setIs_association(true);
		}

	}
	
	public void setPath(List<HashMap<String,String>> oldPath){
		//manage Condex Path
		path_parent = oldPath;
		if(path_parent == null || viewType == ViewType.ALL_ENTITIES){
			setPath_parent(generateParent("context://"+getSource(), getParent_id()));
		} else {
			if(path_parent == null){
				path_parent = new ArrayList<HashMap<String,String>>();
			}
			addPath_parent(generateParent("context://"+getSource(), getParent_id()));
		}
	}


	/** 
	 * Get the list of the enumeration type of a class as  STRINGs list
	 * @param name Input Enumeration Class
	 * @return List of Strings that compose the enumeration class.
	 */
	private List<String> getTypesList(Class<?> name) {
		logger.info("Extracting parmas from: " + name.getCanonicalName());
		Field[] 	fields = name.getDeclaredFields();
		List<String> results = new ArrayList<String>();
		for (Field field: fields){
			//			 if (field.isEnumConstant()){
			//				 results.add(field.getName());
			//				 logger.info("add  Enum: " + field.getName());
			//			 } 
			//			 else 
			if(!field.isSynthetic()){
    			try {
    				//logger.info("This is not an Enumeration!!! " + field.get(null));
    				//logger.info("add " + field.getName()+"("+field.getType()+")");
    				String field_string = ""+field.get(null);
    				results.add(field_string);
    			} catch (IllegalArgumentException e) {
    				logger.error("Error casting to String:"+e.getLocalizedMessage());
    				e.printStackTrace();
    			} catch (IllegalAccessException e) {
    				logger.error("Error casting to String:"+e.getLocalizedMessage());
    				e.printStackTrace();
    			}
    
    
    			logger.info("added fields: "+field.getName());
			}
		}

		logger.info(" Return " + results.size() + "elements");
		return results;
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

	private ArrayList<HashMap<String, String>> generateParent(String rootPath, String id){

		ArrayList<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

		if (id==null || id.length()<=0) return path;

		logger.info("generate parent list from: "+id);

		String strPath = id.replace("context://", "");
		strPath = strPath.substring(strPath.indexOf("/")+1)+"/";

		while(strPath.length() >0){
			HashMap<String, String> map = new HashMap<String, String>();
			String model = strPath.substring(0,strPath.indexOf("/"));
			strPath = strPath.substring(strPath.indexOf("/")+1);

			String type = strPath.substring(0,strPath.indexOf("/"));
			strPath = strPath.substring(strPath.indexOf("/")+1);

			String numId = strPath.substring(0,strPath.indexOf("/"));
			strPath = strPath.substring(strPath.indexOf("/")+1);

			map.put("name", model+":"+type);
			map.put("link", rootPath+"/"+model+"/"+type+"/"+numId);

			logger.info("name: "+map.get("name"));
			logger.info("link: "+map.get("link"));

			rootPath = map.get("link");
			path.add(map);
		}

		return path;
	}

	private void cutPath_parent(){
		if(path_parent.size()>indexPath){
			path_parent = (List<HashMap<String,String>>)path_parent.subList(0, indexPath);
		}
	}

	private CtxModelType string2Model(String value){
		if (CtxModelType.ASSOCIATION.toString().equalsIgnoreCase(value)) return CtxModelType.ASSOCIATION;
		if (CtxModelType.ATTRIBUTE.toString().equalsIgnoreCase(value)) return CtxModelType.ATTRIBUTE;
		return CtxModelType.ENTITY;
	}

	//GETTER & SETTER

	private ContextForm ctxForm;
	private String error;

	public List<String> getModels() {return models;}
	public void setModels(List<String> models) {this.models = models;}

	public List<String> getAttributeTypes() {return attributeTypes;}
	public void setAttributeTypes(List<String> attributeTypes) {this.attributeTypes = attributeTypes;}

	public List<String> getEntityTypes() {return entityTypes;}
	public void setEntityTypes(List<String> entityTypes) {this.entityTypes = entityTypes;}

	public List<String> getAssociationTypes() {return associationTypes;}
	public void setAssociationTypes(List<String> associationTypes) {this.associationTypes = associationTypes;}

	public List<String> getIdList() {return idList;}
	public void setIdList(List<String> idList) {this.idList = idList;}

	public String getSource() {return source;}
	public void setSource(String source) {this.source = source;}

	public String getParent_id() {return parent_id;}
	public void setParent_id(String parent_id) {this.parent_id = parent_id;}

	public List<HashMap<String, String>> getPath_parent() {return path_parent;}
	public void setPath_parent(List<HashMap<String, String>> path_parent) {this.path_parent = path_parent;}
	public void addPath_parent(List<HashMap<String, String>> path_parent) {this.path_parent.addAll(path_parent);}

	public boolean isIs_entity() {return is_entity;}
	public void setIs_entity(boolean is_entity) {this.is_entity = is_entity;}
	
	public boolean isIs_association() {return is_association;}
	public void setIs_association(boolean is_association) {this.is_association = is_association;}

	public List<CtxUIElement> getEntity_results() {return entity_results;}
	public void setEntity_results(List<CtxUIElement> entity_results) {this.entity_results = entity_results;}

	public ContextForm getCtxForm() {return ctxForm;}
	public void setCtxForm(ContextForm ctxForm) {this.ctxForm = ctxForm;}

	public String getError() {return error;}
	public void setError(String error) {this.error = error;}

	public List<CtxUIElement> getAttr_results() {return attr_results;}
	public void setAttr_results(List<CtxUIElement> attr_results) {this.attr_results = attr_results;}

	public List<CtxUIElement> getAsso_results() {return asso_results;}
	public void setAsso_results(List<CtxUIElement> asso_results) {this.asso_results = asso_results;}

	public String getAttribute_label() {return attribute_label;}
	public void setAttribute_label(String attribute_label) {this.attribute_label = attribute_label;}

	public String getEntity_label() {return entity_label;}
	public void setEntity_label(String entity_label) {this.entity_label = entity_label;}

	public String getAssociation_label() {return association_label;}
	public void setAssociation_label(String association_label) {this.association_label = association_label;}
	
	public List<CtxUIElement> getEntity_link() {return entity_link;}
	public void setEntity_link(List<CtxUIElement> entity_link) {this.entity_link = entity_link;}
	
	public ViewType getViewType() {return viewType;}
	public void setViewType(ViewType viewType) {this.viewType = viewType;}	
	public void setViewType(String viewType){
		this.viewType = (viewType.equalsIgnoreCase(ViewType.LINKED_ENTITIES.toString())) ? ViewType.LINKED_ENTITIES : ViewType.ALL_ENTITIES;
	}

	public int getIndexPath() {return indexPath;}
	public void setIndexPathToCut(Integer indexPath) {
		this.indexPath = indexPath;
		cutPath_parent();
	}
}
