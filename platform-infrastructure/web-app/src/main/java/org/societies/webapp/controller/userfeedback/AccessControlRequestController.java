package org.societies.webapp.controller.userfeedback;

import org.societies.api.internal.privacytrust.privacy.model.dataobfuscation.ObfuscatorInfo;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.ObfuscatorInfoFactory;
import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.ObfuscationLevelType;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackAccessControlEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.controller.privacy.prefs.RequestorsController;
import org.societies.webapp.wrappers.AccessControlResponseItemWrapper;
import org.societies.webapp.wrappers.RequestItemWrapper;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "accessControlController")
@ViewScoped
public class AccessControlRequestController extends BasePageController {
	
	private boolean obChecked = false;
	private int sliderValue;
	
	private List<Decision> decisions;
	
	private List<RequestorScopeValues> requestScope;
	
	private List<ActionConstants> actions;
	
	private List<AccessControlResponseItemWrapper> responseItems;
	
	public List<AccessControlResponseItemWrapper> getResponseItems()
	{
		return this.responseItems;
	}

	public List<RequestorScopeValues> getRequestScope()
	{
		return this.requestScope;
	}
	public int getSliderValue() {
		return sliderValue;
	}

	public void setSliderValue(int sliderValue) {
		this.sliderValue = sliderValue;
	}

	public boolean isObChecked() {
		return obChecked;
	}

	public void setObChecked(boolean obChecked) {
		this.obChecked = obChecked;
	}

	private static void getIdFromQueryString() {
        HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        if (hsr.getParameter("id") != null)
        {
            eventID = hsr.getParameter("id");
        }
        else
        {
        	eventID ="";
        }
        if(hsr.getParameter("redirect") != null)
        {
        	redirectPage = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath().concat(hsr.getParameter("redirect"));
        }
        else
        {
        	redirectPage = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath().concat("/index.xhtml");
        }
    }

    @ManagedProperty(value = "#{notifications}")
    private NotificationsController notificationsController;

    @ManagedProperty(value = "#{userFeedback}")
    private IUserFeedback userFeedback;
    
    @ManagedProperty(value = "#{RequestorsController}")
    private RequestorsController requestorsController;

    private final static ObfuscatorInfoFactory obfuscatorInfoFactory = new ObfuscatorInfoFactory();
    private static String redirectPage;
    private static String eventID;
    private UserFeedbackAccessControlEvent event;
    private ConditionConstants newConditionToAdd;
    private HashMap<String, Double> idToObfuscation;

    public AccessControlRequestController() {
        if (log.isDebugEnabled())
            log.debug("AccessControlRequestController ctor()");
        idToObfuscation = new HashMap<String, Double>();
        this.decisions = new ArrayList<Decision>();
        this.decisions.add(Decision.DENY);
        this.decisions.add(Decision.PERMIT);
        this.requestScope = new ArrayList<RequestorScopeValues>();
        this.requestScope.add(RequestorScopeValues.EVERYONE);
        this.requestScope.add(RequestorScopeValues.ONLY_THIS_APP);

    }
    
    
    
    public RequestorsController getRequestorsController() {
		return requestorsController;
	}

	public void setRequestorsController(RequestorsController requestorsController) {
		this.requestorsController = requestorsController;
	}
	
	public CisAdvertisementRecord getCisRecord(RequestorCisBean requestorCis) {
		List<CisAdvertisementRecord> cisAdvRecords = this.requestorsController.getCisListByOwner(requestorCis.getRequestorId());
		for (CisAdvertisementRecord record: cisAdvRecords){
			if (record.getId().equalsIgnoreCase(requestorCis.getCisRequestorId())){
				return record;
				}
		}
		return null;
	}

	public String getDataMessage(ResponseItem item) {
		String message = "";
		String data = item.getRequestItem().getResource().getDataType();
		if(this.event.getRequestor() instanceof RequestorCisBean) {
			RequestorCisBean requestorCis = (RequestorCisBean) this.event.getRequestor();
			CisAdvertisementRecord cisAdv = getCisRecord(requestorCis);
			if(cisAdv!=null) {
				message = "The Community " + cisAdv.getName() + " on behalf of " + requestorCis.getRequestorId() + " requests <b>" +
			formatActions(item.getRequestItem().getActions()) + "</b> access to your "+data+".";
			} else {
				message =  message + "the CSS " + requestorCis.getRequestorId()+ " will have <b>" +
						formatActions(item.getRequestItem().getActions()) + "</b> access to your "+data+".";
			}
			
		} else if (this.event.getRequestor() instanceof RequestorServiceBean) {
			RequestorServiceBean requestorService = (RequestorServiceBean) this.event.getRequestor();
			
			message = "The Service " + requestorService.getRequestorServiceId().getServiceInstanceIdentifier() + " on behalf of " + requestorService.getRequestorId() + " requests <b>" +
					formatActions(item.getRequestItem().getActions()) + "</b> access to your "+data+".";
		} else {
			message = "The CSS " + this.event.getRequestor().getRequestorId()+ " requests <b>" +
					formatActions(item.getRequestItem().getActions()) + "</b> access to your "+data+".";
		}
		return message;
	}
	
	private String formatActions(List<Action> actions) {
		String s = "";
		for(Action act : actions) {
			s = s + act.getActionConstant() +", ";
		}
		return s.substring(0, s.length()-2);
	}
	
	
	public String getRequestor() {
		if(this.event!=null) {	
			
			if(this.event.getRequestor() instanceof RequestorCisBean) {
				
				RequestorCisBean requestorCis = (RequestorCisBean) this.event.getRequestor();
				List<CisAdvertisementRecord> cisAdvRecords = this.requestorsController.getCisListByOwner(requestorCis.getRequestorId());
				for (CisAdvertisementRecord record: cisAdvRecords){
					if (record.getId().equalsIgnoreCase(requestorCis.getCisRequestorId())){
						return "Access Control Request with Community: " + record.getName() + " of CSS: "+record.getCssownerid(); 
					}
				}
				
				return "Access Control Request with Community: <Error>";
				
			} else if (this.event.getRequestor() instanceof RequestorServiceBean) {
				RequestorServiceBean requestorService = (RequestorServiceBean) this.event.getRequestor();
				return "Access Control Reques with Service " + requestorService.getRequestorServiceId().getServiceInstanceIdentifier()+" provided by CSS: "+requestorService.getRequestorId();
			}
			return "Access Control Request with CSS: "+ this.event.getRequestor().getRequestorId();
		} else {
			return "Error";
		}
	}

    public NotificationsController getNotificationsController() {
        return notificationsController;
    }

    public void setNotificationsController(NotificationsController notificationsController) {
        this.notificationsController = notificationsController;
    }

    public IUserFeedback getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(IUserFeedback userFeedback) {
        this.userFeedback = userFeedback;
    }
    

    @PostConstruct
    public void initMethod() {
        if (log.isDebugEnabled())
            log.debug("init()");
        getIdFromQueryString();
        event = notificationsController.getAcceessControlEvent(eventID);

        if (event != null) {
            if (log.isDebugEnabled())
                log.debug("Preparing event for GUI with ID " + eventID);

            prepareEventForGUI();
        } else {
            log.warn("Event not found for ID " + eventID);
        }
        setResponseItems();

    }

    @SuppressWarnings("MethodMayBeStatic")
    public Decision[] getDecisionOptions() {

        return new Decision[]{
                Decision.DENY,
                Decision.PERMIT
        };
    }

    @SuppressWarnings("MethodMayBeStatic")
    public String[] getAvailableConditionValues(ConditionConstants condition) {
        if (condition == null) {
            log.warn("Condition is null in getAvailableConditionValues(..)");
            return null;
        }

        return PrivacyConditionsConstantValues.getValues(condition);
    }

    @SuppressWarnings("MethodMayBeStatic")
    public List<ConditionConstants> getAvailableConditionConstants(RequestItem requestItem) {
        List<ConditionConstants> availableConstants = new ArrayList<ConditionConstants>();

        // add any missing ConditionConstants
        for (ConditionConstants constant : ConditionConstants.values()) {
            boolean found = false;

            for (Condition condition : requestItem.getConditions()) {
                if (constant.equals(condition.getConditionConstant())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                availableConstants.add(constant);
            }
        }

        return availableConstants;
    }

    public void setResponseItems() {
    	this.responseItems = new ArrayList<AccessControlResponseItemWrapper>();
        if (event != null)
        {
        	for (ResponseItem item : event.getResponseItems())
        	{
        		this.responseItems.add((AccessControlResponseItemWrapper) item);
        	}
                
        }    
    }
    

    public UserFeedbackAccessControlEvent getCurrentAccessEvent() {
        return event;
    }

    public void addNewCondition(RequestItem selectedRequestItem) {
        if (newConditionToAdd == null) {
            log.warn("newConditionToAdd is null");
            return;
        }
        if (selectedRequestItem == null) {
            log.warn("selectedRequestItem is null");
            return;
        }

        Condition condition = new Condition();
        condition.setOptional(true);
        condition.setConditionConstant(newConditionToAdd);
        condition.setValue("");

        selectedRequestItem.getConditions().add(condition);

        if(log.isDebugEnabled()) log.debug("Adding condition {} to request item for {}",
                new String[]{newConditionToAdd.name(), selectedRequestItem.getResource().getDataType()});

        newConditionToAdd = null;
    }

   /* public void completeAccessRequestAction() {
    	//TODO: remember flag to set
        log.debug("completeAccessRequestAction() id=" + eventID);

        if (event == null) {
            log.warn("'event' is null - cannot proceed with completeAccessRequestAction() method");
            return;
        }

        // TODO: validate action check boxes

        prepareEventForTransmission(event);

        try {
            List<AccessControlResponseItem> responseItems = event.getResponseItems();
			userFeedback.submitAccessControlResponse(eventID, responseItems, event.getRequestor());
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of completed access control event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of completed negotiation", e);
        }

        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(redirectPage);
		} catch (IOException e) {
			addGlobalMessage("Cannot redirect you to your previous page!",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
			return;
		}//redirectPage; // previously, could redirect to next negotiation - but this makes no sense now
    }
    
    public void completeAccessRequestActionRemember() {
    	//TODO: remember flag to set
        log.debug("completeAccessRequestAction() id=" + eventID);

        if (event == null) {
            log.warn("'event' is null - cannot proceed with completeAccessRequestAction() method");
            return;
        }

        // TODO: validate action check boxes

        prepareEventForTransmission(event);

        try {
            List<AccessControlResponseItem> responseItems = event.getResponseItems();
			userFeedback.submitAccessControlResponse(eventID, responseItems, event.getRequestor());
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of completed access control event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of completed negotiation", e);
        }
        
        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(redirectPage);
		} catch (IOException e) {
			addGlobalMessage("Cannot redirect you to your previous page!",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
			return;
		}
        /*try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //return "validationMessages?faces-redirect=true";

       // return "home?faces-redirect=true"; // previously, could redirect to next negotiation - but this makes no sense now
    }
    public void cancelAccessRequestAction() {
        log.debug("cancelAccessRequestAction()");

        if (event == null) {
            log.warn("'event' is null - cannot proceed with cancelAccessRequestAction() method");
            return;
        }

        prepareEventForTransmission(event);

        try {
            List<AccessControlResponseItem> responseItems = event.getResponseItems();
            //event.
			userFeedback.submitAccessControlResponse(eventID, responseItems, event.getRequestor());
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of cancelled access control event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of cancelled access control event", e);
        }

        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(redirectPage);
		} catch (IOException e) {
			addGlobalMessage("Cannot redirect you to your previous page!",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
			return;
		} // previously, could redirect to next negotiation - but this makes no sense now
    }

    public void cancelAccessRequestActionRemember() {
        log.debug("cancelAccessRequestAction()");

        if (event == null) {
            log.warn("'event' is null - cannot proceed with cancelAccessRequestAction() method");
            return;
        }

        prepareEventForTransmission(event);

        try {
            List<AccessControlResponseItem> responseItems = event.getResponseItems();
            for (AccessControlResponseItem respItem : responseItems){
            	respItem.setRemember(true);
            }
			userFeedback.submitAccessControlResponse(eventID, responseItems, event.getRequestor());
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of cancelled access control event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of cancelled access control event", e);
        }

        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(redirectPage);
		} catch (IOException e) {
			addGlobalMessage("Cannot redirect you to your previous page!",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
			return;
		} // previously, could redirect to next negotiation - but this makes no sense now
    } */
    
    public void completeAccess() {
        if(log.isDebugEnabled()) log.debug("completeAccessRequestAction()");

        if (event == null) {
            log.warn("'event' is null - cannot proceed with cancelAccessRequestAction() method");
            return;
        }

    	List<AccessControlResponseItem> newResponseItems = new ArrayList<AccessControlResponseItem>();
        for (AccessControlResponseItem item : this.responseItems){
        	newResponseItems.add(item);

        	if(log.isDebugEnabled()) log.debug("Returning: "+item.getRequestItem().getResource().getDataType()+" decision: "+item.getDecision()+" remember set: "+item.isRemember()+" and obfuscationSelected: "+item.isObfuscationInput());
        }
        event.getResponseItems().clear();
        event.setResponseItems(newResponseItems);
        if(log.isDebugEnabled()) log.debug(event.getResponseItems().toString());
        for (AccessControlResponseItem item : event.getResponseItems()){
        	if(log.isDebugEnabled()) log.debug("Returning: ! "+item.getRequestItem().getResource().getDataType()+" decision: "+item.getDecision()+" remember set: "+item.isRemember()+" and obfuscationSelected: "+item.isObfuscationInput());
        }
        prepareEventForTransmission();


        try {
            for (AccessControlResponseItem item : event.getResponseItems()){

            	if(log.isDebugEnabled()) log.debug("HEH !! Returning: "+item.getRequestItem().getResource().getDataType()+" decision: "+item.getDecision()+" remember set: "+item.isRemember()+" and obfuscationSelected: "+item.isObfuscationInput());
            }

			userFeedback.submitAccessControlResponse(eventID, event.getResponseItems(), event.getRequestor());
        } catch (Exception e) {
            addGlobalMessage("Error publishing notification of cancelled access control event",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            log.error("Error publishing notification of cancelled access control event", e);
        }

        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(redirectPage);
		} catch (IOException e) {
			addGlobalMessage("Cannot redirect you to your previous page!",
                    e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
			return;
		}
    }// previously, could redirect to next negotiation - but this makes no sense now
    
    private void prepareEventForGUI() {
        AccessControlResponseItemWrapper.wrapList(this.event.getResponseItems());

        for (ResponseItem response : event.getResponseItems()) {

        	
            // wrap the sub items
            if (!(response.getRequestItem() instanceof RequestItemWrapper))
                response.setRequestItem(new RequestItemWrapper(response.getRequestItem()));

            // set to permit by default - the user can then change
            response.setDecision(Decision.PERMIT);
            
           ((AccessControlResponseItemWrapper) response).setRemember(true);;

            // obfucsation
            ObfuscatorInfo obfuscatorInfo = obfuscatorInfoFactory.getObfuscatorInfo(response.getRequestItem().getResource().getDataType());
            ((RequestItemWrapper) response.getRequestItem()).setObfuscatorInfo(obfuscatorInfo);

            // quickly sort by condition name
            Collections.sort(response.getRequestItem().getConditions(), new Comparator<Condition>() {
                @Override
                public int compare(Condition o1, Condition o2) {
                    return o1.getConditionConstant().name().compareTo(o2.getConditionConstant().name());
                }
            });

        }

    } 

    private void prepareEventForTransmission() {
        // Convert ResponseItemWrappers back to ResponseItems if necessary
        for (AccessControlResponseItem response : this.event.getResponseItems()) {
        	if(log.isDebugEnabled()) log.debug("PREPARING:" +  response.isRemember());
        }
        AccessControlResponseItemWrapper.unwrapList(this.event.getResponseItems());
        for (AccessControlResponseItem response : this.event.getResponseItems()) {
        	if(log.isDebugEnabled()) log.debug("PREPARING2:" +  response.isRemember());
        }

        for (ResponseItem response : this.event.getResponseItems()) {
        	
        	if(log.isDebugEnabled()) log.debug(response.toString());

            RequestItemWrapper requestItemWrapper = (RequestItemWrapper) response.getRequestItem();

        	if(log.isDebugEnabled()) log.debug(requestItemWrapper.toString());

            // Action strings need to be converted back to Actions
            // Actually we're just filtering out the unselected ones


            // upon return, the "Actions" field should only contain selected actions
            requestItemWrapper.setActions(requestItemWrapper.getSelectedActions());

            // unwrap the sub items
            RequestItem requestItem;
            if (response.getRequestItem() instanceof RequestItemWrapper) {
            	requestItem = ((RequestItemWrapper) response.getRequestItem()).getRequestItem();
            	
                response.setRequestItem(requestItem);

            } else {
                requestItem = response.getRequestItem();
            }

            // remove any optional, unset ConditionConstants
           /* for (int i = 0; i < requestItem.getConditions().size(); i++) {
                Condition condition = requestItem.getConditions().get(i);

                if (condition.isOptional() && condition.getValue() == null || "".equals(condition.getValue())) {
                    requestItem.getConditions().remove(i);
                    i--;
                }
            }*/
        }
    }

    public void setNewConditionToAdd(ConditionConstants newConditionToAdd) {
        this.newConditionToAdd = newConditionToAdd;

        if(log.isDebugEnabled()) log.debug("New condition to add: {}", newConditionToAdd);
    }

    public ConditionConstants getNewConditionToAdd() {
        return newConditionToAdd;
    }

	public List<Decision> getDecisions() {
		return decisions;
	}

	public void setDecisions(List<Decision> decisions) {
		this.decisions = decisions;
	}

}
