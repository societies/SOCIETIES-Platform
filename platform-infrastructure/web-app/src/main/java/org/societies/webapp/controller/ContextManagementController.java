package org.societies.webapp.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.RowEditEvent;
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
import org.societies.api.schema.context.model.CtxOriginTypeBean;
import org.societies.api.schema.context.model.CtxQualityBean;
import org.societies.api.schema.context.model.CtxUIElement;
import org.societies.webapp.models.ContextForm;
import org.societies.webapp.models.ContextModel;
import org.societies.webapp.models.ContextModel.ViewType;
import org.societies.webapp.models.ErrorModel;
import org.societies.webapp.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
@ManagedBean(name="contextManagementController")
@SessionScoped
public class ContextManagementController extends BasePageController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String ACTION_LOOKUP		 = "lookup";
	private static final String ACTION_RETRIEVE		 = "retrieve";
	
	private static final String CTX_ENTITY_RESULTS	= "entity_results";
	private static final String CTX_ATTR_RESULTS	= "attr_results";
	private static final String CTX_ASSO_RESULTS	= "asso_results";
	
	private String lookupLabel = "Lookup";
	private String retrieveLabel = "Retrieve";
	private String newModelLabel = "Create";
	private String linkLabel = "Link to Model";
	
	@ManagedProperty(value = "#{userService}")
    private UserService userService;
    
    /**
	 * @return the userService
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
    
	@ManagedProperty(value = "#{internalCtxBroker}")
	private ICtxBroker internalCtxBroker;

	public ICtxBroker getInternalCtxBroker() {
		return internalCtxBroker;
	}

	public void setInternalCtxBroker(ICtxBroker internalCtxBroker) {
		this.internalCtxBroker = internalCtxBroker;
	}
	
	private ContextModel contextModel;
	private ContextForm contextForm;
	
	
	private SelectItem[] attributeTypesItems;
	private SelectItem[] entityTypesItems;
	private SelectItem[] associationTypesItems;
	private SelectItem[] selectItemsForModel;
	
	private List<HashMap<String, String>> path_parent;
	
	/**
	 * Per Actions
	 */
	private String selectedOperationType;
	
	private boolean lookup;
	private boolean retrieve;
	private boolean newmodel;
	private boolean linkto;
	
	/**
	 * Per Lookup table
	 */
	private String selectedModelType;
	
	private boolean entityLookup;
	private boolean associationLookup;
	private boolean attributeLookup;
	
	private String selectedEntityType;
	private String selectedAttributeType;
	private String selectedAssociationType;
	
	/**
	 * Per Retrieve table
	 */
	private SelectItem[] contextIdItems;
	private String selectedContextId;
	private String inputContextId;
	
	/**
	 * Per new model
	 */
	private String selectedModelTypeNewModel;
	
	private boolean entityNewModel;
	private boolean associationNewModel;
	private boolean attributeNewModel;
	
	private String selectedNewModel;
	private String attributeValue;
	
	/**
	 * Per link model
	 */
	private String selectedEntityLink;
	private SelectItem[] entityLinkItems;
	
	private Date predictedDate;
	
	public ContextManagementController()
	{
		log.debug("ContextManagementController constructor called");
	}
	
	@PostConstruct
	public void service()
	{
		try
		{
			path_parent = null;
			//gestione errore nel msg
			contextModel = new ContextModel(internalCtxBroker);
			
			//log.debug("====== CONTEXT GUI --> GET");
	
			if(contextForm!= null && contextForm.getViewType()!= null){
				contextModel.setViewType(contextForm.getViewType());
			}
			else
			{
				contextForm = new ContextForm();
				contextForm.setViewType("ALL_ENTITIES");
			}
			contextModel.setViewType(contextForm.getViewType());
			
			switch(contextModel.getViewType()){
			case LINKED_ENTITIES:
				contextModel.setEntity_results(getRoots(contextModel.getEntityTypes()));
				break;
			case ALL_ENTITIES:
			default:
				contextModel.setEntity_results(getAllCtxEntityData(contextModel.getEntityTypes()));
			}
		
		}catch(Exception e){
			log.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));	
		}
		
	}
	
	public void submit()
	{
		try
		{
			//gestione errore nel msg
		
			//log.info("====== CONTEXT GUI --> POST");

			String method 	 = contextForm.getMethod();
			String id		 = contextForm.getCtxID();
			if(id == null || id.length()<=0){
				contextModel = new ContextModel(internalCtxBroker);
			} else {
				contextModel = new ContextModel(internalCtxBroker,id);
			}

			contextModel.setViewType(contextForm.getViewType());
			
			if(contextForm.getPathIndex()>=0){
				contextModel.setIndexPathToCut(contextForm.getPathIndex());
			}
			
			if(contextModel.isIs_association()){
				contextModel.setEntity_link(getAllCtxEntityData(contextModel.getEntityTypes()));
			}
			
		//	log.info("Method:"+ method +" Passed Ctx ID:"+id);

			if (ACTION_LOOKUP.equalsIgnoreCase(method)){
				//log.info("Lookup (model: "  +  contextForm.getModel() + ", type:"+contextForm.getType()+")");
				contextModel.setEntity_results(lookup(contextForm.getModel(), contextForm.getType()));
			}
			else if(ACTION_RETRIEVE.equalsIgnoreCase(method)){
				//log.info("Retreive ID: "  + id);
				Map<String, List<CtxUIElement>> ctxList = retrieve(contextForm.getCtxID());
				contextModel.setEntity_results(ctxList.get(CTX_ENTITY_RESULTS));
				contextModel.setAsso_results(ctxList.get(CTX_ASSO_RESULTS));
				contextModel.setAttr_results(ctxList.get(CTX_ATTR_RESULTS));
				contextModel.setPath(path_parent);
				path_parent = contextModel.getPath_parent();
			} else {
				switch(contextModel.getViewType()){
				case LINKED_ENTITIES:
					contextModel.setEntity_results(getRoots(contextModel.getEntityTypes()));
					break;
				case ALL_ENTITIES:
				default:
					contextModel.setEntity_results(getAllCtxEntityData(contextModel.getEntityTypes()));
				}
				contextModel.setPath(path_parent);
				path_parent = contextModel.getPath_parent();
			}
		}
		catch(Exception e){
			log.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));	
		}
		
	}
    
    public String getUsername() {
        return userService.getUsername();
    }
	
	/**
	 * Retreive all ENTITY Values in the internalCtxBroker
	 * @return List<CtxUIElement> that provide the serialized obj to be rendered in the GUI
	 * @throws CtxException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public List<CtxUIElement> getAllCtxEntityData(List<String> entityTypes) 
			throws InterruptedException, ExecutionException, CtxException {

		List<CtxUIElement> results  = new ArrayList<CtxUIElement>();

		for (String type: entityTypes){
			results.addAll(lookup(CtxModelType.ENTITY.toString() , type));
		}
		
		//workaround to take source
		if(results.size()>0){
			modifySource(results.get(0).getSource());
		}

		//log.info("number of found element: "+results.size());
		return results;
	}


	public Map<String,List<CtxUIElement>> retrieve(String ctxID) 
			throws InterruptedException, ExecutionException, CtxException{

		//log.info("ContextGUI ACTION:Retrieve    [id] => "+ctxID);
		HashMap<String, List<CtxUIElement>> results = new HashMap<String, List<CtxUIElement>>();
		if (ctxID.length()==0){
			//log.warn("Context ID is empty");
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
			//log.info("added "+attrList.size()+" attributes");

			Set<CtxAssociationIdentifier> associations = entity.getAssociations();
			for(CtxAssociationIdentifier aId: associations){
				assoList.add((CtxAssociation)internalCtxBroker.retrieve(aId).get());
			}
			//log.info("added "+assoList.size()+" associations");
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
		
		//log.info(""+results.get(CTX_ENTITY_RESULTS));
		
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



	public CtxModelType string2Model(String value){
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
	public List<CtxUIElement> lookup(String model, String type) throws InterruptedException, ExecutionException, CtxException{

		//log.info("Lookup for model:"+model + ", type:"+type);
		List<CtxUIElement> results = new ArrayList<CtxUIElement>();


		List<CtxIdentifier> list;
		list = internalCtxBroker.lookup(string2Model(model), type).get();
		for (CtxIdentifier id: list){
			CtxModelObject ctxModelObject = internalCtxBroker.retrieve(id).get();
			if(ctxModelObject instanceof CtxEntity)
				((CtxEntity)ctxModelObject).getAssociations("");
			CtxUIElement ctxBean = serliazeCtxModel(internalCtxBroker.retrieve(id).get());
			results.add(ctxBean);				
		}
		//log.info("values found for "+type+": "+results.size());

		//workaround to take source
		if(results.size()>0){
			modifySource(results.get(0).getSource());
		}
		
		//log.info("Elements:"+results.size());
		return results;
	}

	public List<CtxUIElement> getRoots(List<String> entityTypes) throws CtxException, InterruptedException, ExecutionException {

		List<CtxUIElement> results  = new ArrayList<CtxUIElement>();

		for (String type: entityTypes){
			List<CtxIdentifier> list = internalCtxBroker.lookup(CtxModelType.ENTITY, type).get();

			//log.info("number of entity: "+list.size());
			for(CtxIdentifier id : list){
				CtxEntity  entity = (CtxEntity)internalCtxBroker.retrieve(id).get();
				Set<CtxAssociationIdentifier> associations = entity.getAssociations();

			//	log.info("entity id: "+id+" number of ass: "+associations.size());
				if(associations.size()<=0){
					results.add(serliazeCtxModel(entity));
				} else {

					//if entity is parent in every association, it is root!
					boolean isRoot= true;
					for(CtxAssociationIdentifier assocId : associations){
						CtxAssociation assoc = (CtxAssociation)internalCtxBroker.retrieve(assocId).get();

						if(!assoc.getParentEntity().toString().equals(entity.getId().toString())){
						//	log.info(id+" has parent: "+assoc.getParentEntity().toString());
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

	//	log.info("number of found element: "+results.size());
		return results;


	}

	public CtxUIElement serliazeCtxModel(CtxModelObject elm){

		CtxUIElement ctxBean = new CtxUIElement();
		if(elm!= null){
		//	log.debug("element: "+elm);
			String ctxValue =  elm.getId().toString().replace("context://", "");
			String info[] = ctxValue.split("/");

			// Log info
		/*
			log.debug("====> Found new Element ");
			log.debug("FULL ID:"+elm.getId().toString());
			log.debug("Source:"+info[0]);
			log.debug("Model:"+info[1]);
			log.debug("Type:"+info[2]);
			log.debug("ID:"+info[info.length-1]);
			log.debug("ID_noSpecChar:"+elm.getId().toString().replaceAll("\\W", ""));
			log.debug("==== ");
*/

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
				//log.debug("Ctx Attribute type "+attr.getValueType());


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

				CtxQualityBean qualityBean = new CtxQualityBean();
				qualityBean.setLastUpdated(attr.getQuality().getLastUpdated());
				if(attr.getQuality().getOriginType() != null)
					qualityBean.setOriginType(CtxOriginTypeBean.valueOf(attr.getQuality().getOriginType().toString()));
				qualityBean.setPrecision(attr.getQuality().getPrecision());
				qualityBean.setUpdateFrequency(attr.getQuality().getUpdateFrequency());
				
				ctxBean.setQualityBean(qualityBean);
				
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
	
	public CtxUIElement serializeCtxAttribute(CtxAttribute attr)
	{
		//log.info("Ctx Attribute type "+attr.getValueType());
		
		CtxUIElement ctxBean = new CtxUIElement();

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

		CtxQualityBean qualityBean = new CtxQualityBean();
		qualityBean.setLastUpdated(attr.getQuality().getLastUpdated());
		if(attr.getQuality().getOriginType() != null)
			qualityBean.setOriginType(CtxOriginTypeBean.valueOf(attr.getQuality().getOriginType().toString()));
		qualityBean.setPrecision(attr.getQuality().getPrecision());
		qualityBean.setUpdateFrequency(attr.getQuality().getUpdateFrequency());
		
		ctxBean.setQualityBean(qualityBean);
		
		if (attr.getQuality().getPrecision()!=null)
			ctxBean.setQuality("Precision:" +attr.getQuality().getPrecision());
		
		return ctxBean;
	}

	private String genLink(String id, String label){
		return "<a href='#' onclick='retrieve(\""+ id +"\");'>"+label+"</a>";
	}


	private void setAttributeValue(CtxAttribute attr,String value){
		attr.setValueType(getCtxAttributeValueType(value));
		switch(attr.getValueType()){
		case INTEGER:
			//log.info("value setted as integer");
			attr.setIntegerValue(Integer.valueOf(value));
			break;
		case DOUBLE:
			//log.info("value setted as double");
			attr.setDoubleValue(Double.valueOf(value));
			break;
		case STRING:
			//log.info("value setted as string");
			attr.setStringValue(value);
			break;
		default: 
			//log.info("value not setted");
			break;
		}
	}

	private CtxAttributeValueType getCtxAttributeValueType(String value){
	//	log.info("searching type for value: "+value+"...");
		if(value == null || value.length()<=0){
		//	log.info("Attribute is: " +CtxAttributeValueType.EMPTY);
			return CtxAttributeValueType.EMPTY;
		}

		if(isNumber(value)){
			try{
				Integer.parseInt(value);
			//	log.info("Attribute is: " +CtxAttributeValueType.INTEGER);
				return CtxAttributeValueType.INTEGER;
			} catch (Exception dropped){}
			try{
				Double.parseDouble(value);
			//	log.info("Attribute is: " +CtxAttributeValueType.DOUBLE);
				return CtxAttributeValueType.DOUBLE;
			} catch (Exception dropped){}
		}
		//log.info("Attribute is: " +CtxAttributeValueType.STRING);
		return CtxAttributeValueType.STRING;

	}

	private boolean isNumber(String num){
		return num.matches("^[-+]?[0-9]+(\\.?[0-9]+)?$");
	}
	
	private void modifySource(String source){
		contextModel.setSource(source);
	}

	/**
	 * @return the contextModel
	 */
	public ContextModel getContextModel() {
		return contextModel;
	}

	/**
	 * @param contextModel the contextModel to set
	 */
	public void setContextModel(ContextModel contextModel) {
		this.contextModel = contextModel;
	}

	/**
	 * @return the selectedModelType
	 */
	public String getSelectedModelType() {
		return selectedModelType;
	}

	/**
	 * @param selectedModelType the selectedModelType to set
	 */
	public void setSelectedModelType(String selectedModelType) {
		this.selectedModelType = selectedModelType;
	}
	
	/**
	 * @return the attributeTypesItems
	 */
	public SelectItem[] getAttributeTypesItems() {
		attributeTypesItems = getSelectItemsFromList(contextModel.getAttributeTypes(),"-- Select Attribute --","");
		return attributeTypesItems;
	}

	/**
	 * @param attributeTypesItems the attributeTypesItems to set
	 */
	public void setAttributeTypesItems(SelectItem[] attributeTypesItems) {
		this.attributeTypesItems = attributeTypesItems;
	}

	/**
	 * @return the associationTypesItems
	 */
	public SelectItem[] getAssociationTypesItems() {
		associationTypesItems = getSelectItemsFromList(contextModel.getAssociationTypes(),"-- Select Association --","");
		return associationTypesItems;
	}

	/**
	 * @param associationTypesItems the associationTypesItems to set
	 */
	public void setAssociationTypesItems(SelectItem[] associationTypesItems) {
		this.associationTypesItems = associationTypesItems;
	}
	
	/**
	 * @return the entityTypesItems
	 */
	public SelectItem[] getEntityTypesItems() {
		entityTypesItems = getSelectItemsFromList(contextModel.getEntityTypes(),"-- Select Entity --","");
		return entityTypesItems;
	}

	/**
	 * @param entityTypesItems the entityTypesItems to set
	 */
	public void setEntityTypesItems(SelectItem[] entityTypesItems) {
		this.entityTypesItems = entityTypesItems;
	}

	private SelectItem[] getSelectItemsFromList(List<String> elementsFrom, String firstLabel, String firstValue)
	{
		SelectItem elementsTo[] = null;
		if(elementsFrom != null)
		{
			elementsTo = new SelectItem[elementsFrom.size()+1];
			elementsTo[0] = new SelectItem(firstValue,firstLabel);
			for(int i = 0; i < elementsFrom.size(); i++)
			{
				elementsTo[i+1] = new SelectItem(elementsFrom.get(i), elementsFrom.get(i));
			}
		}
		return elementsTo;
	}
	
	private SelectItem[] getSelectItemsFromListCtxUIElement(List<CtxUIElement> elementsFrom, String firstLabel, String firstValue)
	{
		SelectItem elementsTo[] = null;
		if(elementsFrom != null)
		{
			elementsTo = new SelectItem[elementsFrom.size()+1];
			elementsTo[0] = new SelectItem(firstValue,firstLabel);
			for(int i = 0; i < elementsFrom.size(); i++)
			{
				elementsTo[i+1] = new SelectItem(elementsFrom.get(i).getId(), elementsFrom.get(i).getType());
			}
		}
		return elementsTo;
	}
	
	public void changeView(String view)
	{
	//	log.info("Changing view to: "+view);
		contextForm = new ContextForm();
		contextForm.setViewType(view);
		submit();
	}
	
	public void changeView(ViewType v)
	{
		changeView(v.toString());
	}
	
	public SelectItem [] getSelectItemsForModel()
	{
		selectItemsForModel = getSelectItemsFromList(contextModel.getModels(), "-- Select Model Type --", "");
		return selectItemsForModel;
		
	}
	
	public void deleteRow(String idElem)
	{
	//	log.info("Removing id: "+idElem);
		
		try
		{
			CtxIdentifier ctxIdentifier = CtxIdentifierFactory.getInstance().fromString(idElem);
			internalCtxBroker.remove(ctxIdentifier).get();
			
			submit();
			
	        FacesMessage msg = new FacesMessage("Element removed", ctxIdentifier.toString());  
	        FacesContext.getCurrentInstance().addMessage(null, msg);  
	        
		}
		catch(Exception e){
			log.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));	
		}
	}
	
	public void retrieveFromRow(String idElem)
	{
		contextForm.setCtxID(idElem);
		contextForm.setValue(idElem);
		contextForm.setType("");
		contextForm.setModel("");
		contextForm.setMethod("retrieve");
		submit();
	}
	
	public void retrieveWithCut(String idElem, int pathIndex)
	{
		contextForm.setPathIndex(pathIndex);
		retrieveFromRow(idElem);
	}
	
	public void modifyRow(RowEditEvent event)
	{
		CtxUIElement elem = ((CtxUIElement) event.getObject());
	//	log.info("Editing row: "+elem.getId());
		
		try
		{
			
			CtxModelObject model = null;
	
			CtxModelType modelType = string2Model(elem.getModel());
		//	log.info("model type: "+modelType);
	
	
			switch (modelType) {
			case ENTITY:
	
				CtxEntityIdentifier ctxNttId = new CtxEntityIdentifier(elem.getId());				
				model = internalCtxBroker.retrieve(ctxNttId).get();
	
				break;
			case ASSOCIATION:
	
				CtxAssociationIdentifier ctxAssId = new CtxAssociationIdentifier(elem.getId());
				model = internalCtxBroker.retrieve(ctxAssId).get();
	
				break;
			case ATTRIBUTE:
	
				CtxAttributeIdentifier ctxAttrId = new CtxAttributeIdentifier(elem.getId());
				model = internalCtxBroker.retrieve(ctxAttrId).get();
				setAttributeValue((CtxAttribute)model, elem.getValue());
	
				break;
	
			default:
				break;
			}
	
//			Future<CtxModelObject> update = internalCtxBroker.update(model);
			CtxModelObject update = internalCtxBroker.update(model).get();
	//		log.debug("update: "+update);
			submit();
			
	        FacesMessage msg = new FacesMessage(modelType+" Edited", ((CtxUIElement) event.getObject()).getValue());  
	        FacesContext.getCurrentInstance().addMessage(null, msg);  
	
		}
		catch(Exception e){
			log.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));	
		}
		
	}

	/**
	 * @return the path_parent
	 */
	public List<HashMap<String, String>> getPath_parent() {
		return path_parent;
	}

	/**
	 * @param path_parent the path_parent to set
	 */
	public void setPath_parent(List<HashMap<String, String>> path_parent) {
		this.path_parent = path_parent;
	}

	/**
	 * @return the lookup
	 */
	public boolean isLookup() {
		return lookup;
	}

	/**
	 * @param lookup the lookup to set
	 */
	public void setLookup(boolean lookup) {
		this.lookup = lookup;
	}

	/**
	 * @return the retrieve
	 */
	public boolean isRetrieve() {
		return retrieve;
	}

	/**
	 * @param retrieve the retrieve to set
	 */
	public void setRetrieve(boolean retrieve) {
		this.retrieve = retrieve;
	}

	/**
	 * @return the newmodel
	 */
	public boolean isNewmodel() {
		return newmodel;
	}

	/**
	 * @param newmodel the newmodel to set
	 */
	public void setNewmodel(boolean newmodel) {
		this.newmodel = newmodel;
	}

	/**
	 * @return the linkto
	 */
	public boolean isLinkto() {
		return linkto;
	}

	/**
	 * @param linkto the linkto to set
	 */
	public void setLinkto(boolean linkto) {
		this.linkto = linkto;
	}

	/**
	 * @return the entityLookup
	 */
	public boolean isEntityLookup() {
		return entityLookup;
	}

	/**
	 * @param entityLookup the entityLookup to set
	 */
	public void setEntityLookup(boolean entityLookup) {
		this.entityLookup = entityLookup;
	}

	/**
	 * @return the associationLookup
	 */
	public boolean isAssociationLookup() {
		return associationLookup;
	}

	/**
	 * @param associationLookup the associationLookup to set
	 */
	public void setAssociationLookup(boolean associationLookup) {
		this.associationLookup = associationLookup;
	}

	/**
	 * @return the attributeLookup
	 */
	public boolean isAttributeLookup() {
		return attributeLookup;
	}

	/**
	 * @param attributeLookup the attributeLookup to set
	 */
	public void setAttributeLookup(boolean attributeLookup) {
		this.attributeLookup = attributeLookup;
	}

	/**
	 * @param selectItemsForModel the selectItemsForModel to set
	 */
	public void setSelectItemsForModel(SelectItem[] selectItemsForModel) {
		this.selectItemsForModel = selectItemsForModel;
	}

	/**
	 * @return the selectedOperationType
	 */
	public String getSelectedOperationType() {
		return selectedOperationType;
	}

	/**
	 * @param selectedOperationType the selectedOperationType to set
	 */
	public void setSelectedOperationType(String selectedOperationType) {
		this.selectedOperationType = selectedOperationType;
	}

	/**
	 * @return the selectedEntityType
	 */
	public String getSelectedEntityType() {
		return selectedEntityType;
	}

	/**
	 * @param selectedEntityType the selectedEntityType to set
	 */
	public void setSelectedEntityType(String selectedEntityType) {
		this.selectedEntityType = selectedEntityType;
	}

	/**
	 * @return the selectedAttributeType
	 */
	public String getSelectedAttributeType() {
		return selectedAttributeType;
	}

	/**
	 * @param selectedAttributeType the selectedAttributeType to set
	 */
	public void setSelectedAttributeType(String selectedAttributeType) {
		this.selectedAttributeType = selectedAttributeType;
	}

	/**
	 * @return the selectedAssociationType
	 */
	public String getSelectedAssociationType() {
		return selectedAssociationType;
	}

	/**
	 * @param selectedAssociationType the selectedAssociationType to set
	 */
	public void setSelectedAssociationType(String selectedAssociationType) {
		this.selectedAssociationType = selectedAssociationType;
	}
	
	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue() {
		return attributeValue;
	}

	/**
	 * @param attributeValue the attributeValue to set
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public void operationsListener()
	{
		if(lookupLabel.equals(selectedOperationType))
		{
			lookup = true;
			retrieve = false;
			newmodel = false;
			linkto = false;
		}
		else if(retrieveLabel.equals(selectedOperationType))
		{
			lookup = false;
			retrieve = true;
			newmodel = false;
			linkto = false;
		}
		else if(newModelLabel.equals(selectedOperationType))
		{
			lookup = false;
			retrieve = false;
			newmodel = true;
			linkto = false;
		}
		else if(linkLabel.equals(selectedOperationType))
		{
			lookup = false;
			retrieve = false;
			newmodel = false;
			linkto = true;
		}
		else
		{
			lookup = false;
			retrieve = false;
			newmodel = false;
			linkto = false;
		}
	}
	
	public void modelListener()
	{
		if(contextModel.getEntity_label().equals(selectedModelType))
		{
			entityLookup = true;
			attributeLookup = false;
			associationLookup = false;
		}
		else if(contextModel.getAssociation_label().equals(selectedModelType))
		{
			entityLookup = false;
			attributeLookup = false;
			associationLookup = true;
		}
		else if(contextModel.getAttribute_label().equals(selectedModelType))
		{
			entityLookup = false;
			attributeLookup = true;
			associationLookup = false;
		}
		else
		{
			entityLookup = false;
			attributeLookup = false;
			associationLookup = false;
		}
	}
	
	public void executeLookup()
	{
		String method = ACTION_LOOKUP;
		String model = selectedModelType;
		String type = "";
		if(contextModel.getEntity_label().equals(selectedModelType))
			type = selectedEntityType;
		else if(contextModel.getAssociation_label().equals(selectedModelType))
			type = selectedAssociationType;
		else if(contextModel.getAttribute_label().equals(selectedModelType))
			type = selectedAttributeType;
		
		contextForm.setMethod(method);
		contextForm.setModel(model);
		contextForm.setType(type);
		contextForm.setCtxID("");
		contextForm.setValue("");
		submit();
	}
	
	public void executeRetrieve()
	{
		String method = ACTION_RETRIEVE;
		String ctxId = inputContextId;
		String value = inputContextId;
		
		contextForm.setMethod(method);
		contextForm.setModel("");
		contextForm.setType("");
		contextForm.setCtxID(ctxId);
		contextForm.setValue(value);
		submit();
	}

	/**
	 * @return the lookupLabel
	 */
	public String getLookupLabel() {
		return lookupLabel;
	}

	/**
	 * @return the retrieveLabel
	 */
	public String getRetrieveLabel() {
		return retrieveLabel;
	}

	/**
	 * @return the newModelLabel
	 */
	public String getNewModelLabel() {
		return newModelLabel;
	}

	/**
	 * @return the linkLabel
	 */
	public String getLinkLabel() {
		return linkLabel;
	}

	/**
	 * @return the selectedContextId
	 */
	public String getSelectedContextId() {
		return selectedContextId;
	}

	/**
	 * @param selectedContextId the selectedContextId to set
	 */
	public void setSelectedContextId(String selectedContextId) {
		this.selectedContextId = selectedContextId;
	}

	/**
	 * @return the inputContextId
	 */
	public String getInputContextId() {
		return inputContextId;
	}

	/**
	 * @param inputContextId the inputContextId to set
	 */
	public void setInputContextId(String inputContextId) {
		this.inputContextId = inputContextId;
	}

	/**
	 * @return the contextIdItems
	 */
	public SelectItem[] getContextIdItems() {
		contextIdItems = getSelectItemsFromList(contextModel.getIdList(), "-- Select Context Id --", "");
		return contextIdItems;
	}
	
	public void contextIdListener()
	{
		if(selectedContextId != null && selectedContextId.length() > 0)
		{
			inputContextId = selectedContextId;
		}
	}
	
	
	/**
	 * @return the selectedModelTypeNewModel
	 */
	public String getSelectedModelTypeNewModel() {
		return selectedModelTypeNewModel;
	}

	/**
	 * @param selectedModelTypeNewModel the selectedModelTypeNewModel to set
	 */
	public void setSelectedModelTypeNewModel(String selectedModelTypeNewModel) {
		this.selectedModelTypeNewModel = selectedModelTypeNewModel;
	}

	/**
	 * @return the entityNewModel
	 */
	public boolean isEntityNewModel() {
		return entityNewModel;
	}

	/**
	 * @param entityNewModel the entityNewModel to set
	 */
	public void setEntityNewModel(boolean entityNewModel) {
		this.entityNewModel = entityNewModel;
	}

	/**
	 * @return the associationNewModel
	 */
	public boolean isAssociationNewModel() {
		return associationNewModel;
	}

	/**
	 * @param associationNewModel the associationNewModel to set
	 */
	public void setAssociationNewModel(boolean associationNewModel) {
		this.associationNewModel = associationNewModel;
	}

	/**
	 * @return the attributeNewModel
	 */
	public boolean isAttributeNewModel() {
		return attributeNewModel;
	}

	/**
	 * @param attributeNewModel the attributeNewModel to set
	 */
	public void setAttributeNewModel(boolean attributeNewModel) {
		this.attributeNewModel = attributeNewModel;
	}


	public void modelListenerNewModel()
	{
		if(contextModel.getEntity_label().equalsIgnoreCase(selectedModelTypeNewModel))
		{
			entityNewModel = true;
			attributeNewModel = false;
			associationNewModel = false;
		}
		else if(contextModel.getAssociation_label().equalsIgnoreCase(selectedModelTypeNewModel))
		{
			entityNewModel = false;
			attributeNewModel = false;
			associationNewModel = true;
		}
		else if(contextModel.getAttribute_label().equalsIgnoreCase(selectedModelTypeNewModel))
		{
			entityNewModel = false;
			attributeNewModel = true;
			associationNewModel = false;
		}
		else
		{
			entityNewModel = false;
			attributeNewModel = false;
			associationNewModel = false;
		}
	}
	
	/**
	 * @return the selectedNewModel
	 */
	public String getSelectedNewModel() {
		return selectedNewModel;
	}

	/**
	 * @param selectedNewModel the selectedNewModel to set
	 */
	public void setSelectedNewModel(String selectedNewModel) {
		this.selectedNewModel = selectedNewModel;
	}
	

	/**
	 * @return the selectedEntityLink
	 */
	public String getSelectedEntityLink() {
		return selectedEntityLink;
	}

	/**
	 * @param selectedEntityLink the selectedEntityLink to set
	 */
	public void setSelectedEntityLink(String selectedEntityLink) {
		this.selectedEntityLink = selectedEntityLink;
	}

	/**
	 * @return the entityLinkItems
	 */
	public SelectItem[] getEntityLinkItems() {
		entityLinkItems = getSelectItemsFromListCtxUIElement(contextModel.getEntity_link(), "-- Select Entity to Link --", "");
		return entityLinkItems;
	}
	
	/**
	 * @return the predictedDate
	 */
	public Date getPredictedDate() {
		return predictedDate;
	}

	/**
	 * @param predictedDate the predictedDate to set
	 */
	public void setPredictedDate(Date predictedDate) {
		this.predictedDate = predictedDate;
	}

	public void saveModel()
	{
		try
		{
			String parentId = contextModel.getParent_id();
			String selectedModel = selectedModelTypeNewModel;
			String type = selectedNewModel;
			String value = attributeValue;
		/*	
			log.debug("Param to save: ");
			log.debug("parentId: "+ parentId);
			log.debug("model_req: "+ selectedModel);
			log.debug("type: "+ type);
			log.debug("value: "+ value);
	*/
			CtxModelObject model = null;
	
			CtxIdentifier ctxIdentifier = null;
			if(parentId!= null && parentId.length()>0){
				ctxIdentifier = CtxIdentifierFactory.getInstance().fromString(parentId);
			}
	
			CtxModelType modelType = string2Model(selectedModel);
		//	log.debug("model type: "+modelType);
	
			switch (modelType) {
			case ENTITY:
				Future<CtxEntity> entity = internalCtxBroker.createEntity(type);
			//	log.debug("entity: "+entity);
				model = entity.get();
			//	log.debug("model: "+model);
	
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
		//	log.debug("update: "+update);
			model = update.get();
			
			//reset view
			selectedOperationType = "";
			operationsListener();
			submit();
	
		}
		catch(Exception e){
			log.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));	
		}
	}
	
	public void linkModel()
	{
		try
		{
			String parentId = contextModel.getParent_id();
			String entityIdStr = selectedEntityLink;
			
	//		log.info("==> linkModel ("+parentId+", "+entityIdStr+")");
			
			CtxAssociationIdentifier assoId = (CtxAssociationIdentifier)CtxIdentifierFactory.getInstance().fromString(parentId);
			CtxEntityIdentifier entityId = (CtxEntityIdentifier)CtxIdentifierFactory.getInstance().fromString(entityIdStr);
			CtxAssociation asso = (CtxAssociation)internalCtxBroker.retrieve(assoId).get();
			
			asso.addChildEntity(entityId);
			internalCtxBroker.update(asso).get();
			submit();
			
		}
		catch(Exception e){
			log.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));	
		}
	}
	
	public void viewPredictedValues(String ctxId)
	{
		try
		{
			
			Date paramDate = predictedDate;
			CtxAttributeIdentifier ctx = new CtxAttributeIdentifier(ctxId);
			
	//		log.debug("date picked: "+predictedDate);
	//		log.debug("ctxId: "+ctxId);
			
			List<CtxAttribute> ctxAttributeList = internalCtxBroker.retrieveFuture(ctx, paramDate).get();
			List<CtxUIElement> ctxDisplayList = null;
			if(ctxAttributeList != null && !ctxAttributeList.isEmpty())
			{
				ctxDisplayList = new ArrayList<CtxUIElement>();
				for(CtxAttribute elem : ctxAttributeList)
					ctxDisplayList.add(serializeCtxAttribute(elem));
			}
			contextModel.setPredictedAttributeList(ctxDisplayList);
			
			
		}
		catch(Exception e){
			log.error("",e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));	
		}
	}
	
	public void handleClose()
	{
		predictedDate = null;
	}
	
}