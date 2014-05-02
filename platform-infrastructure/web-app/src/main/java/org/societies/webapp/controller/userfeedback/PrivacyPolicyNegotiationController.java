package org.societies.webapp.controller.userfeedback;

import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.*;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.controller.CISController;
import org.societies.webapp.controller.privacy.prefs.RequestorsController;
import org.societies.webapp.models.CisInfo;
import org.societies.webapp.wrappers.RequestItemWrapper;
import org.societies.webapp.wrappers.ResponseItemWrapper;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name = "ppNegotiation", eager = true)
@ViewScoped
public class PrivacyPolicyNegotiationController extends BasePageController {

	private void getIdFromQueryString() {
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
		if(log.isDebugEnabled()) log.debug("REDIRECT PAGE: " + redirectPage);
		//QUICK HACK TO REDIRECT USER TO MY_COMMUNITIES PAGE 
		if(redirectPage.equals("/societies/your_suggested_communities_list.xhtml"))
		{
			//ASSUME THEY HAVE JUST ACCEPTED A PPN TO JOIN A CIS, TAKE THEM TO THERE CIS PAGE
			redirectPage = "/societies/your_communities_list.xhtml";
		}
	}

	@ManagedProperty(value = "#{notifications}")
	private NotificationsController notificationsController;

	@ManagedProperty(value = "#{userFeedback}")
	private IUserFeedback userFeedback;
	
	@ManagedProperty(value = "#{RequestorsController}")
	private RequestorsController requestorsController;

	



	private static String eventID;
	private static String redirectPage;
	private UserFeedbackPrivacyNegotiationEvent event;
	private ConditionConstants newConditionToAdd;

	public PrivacyPolicyNegotiationController() {
		if (log.isDebugEnabled())
			log.debug("PrivacyPolicyNegotiationController ctor()");

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
	
	public String convertConditionValues(String value) {
		if(value.equalsIgnoreCase("0")) {
			return "false";
		} else if (value.equalsIgnoreCase("1")) {
			return "true";
		}
		return value;
	}

	public String getDataMessage(ResponseItem item) {
		String message = "By accepting this Privacy Policy, ";
		String data = item.getRequestItem().getResource().getDataType();
		RequestorBean requestor = this.event.getNegotiationDetails().getRequestor();
		if(requestor instanceof RequestorCisBean) {
			RequestorCisBean requestorCis = (RequestorCisBean) requestor;
			CisAdvertisementRecord cisAdv = getCisRecord(requestorCis);
			if(cisAdv!=null) {
				message = message + "the Community " + cisAdv.getName() + " on behalf of " + requestorCis.getRequestorId() + " will have <b>" +
			formatActions(item.getRequestItem().getActions()) + "</b> access to your "+data+".";
			} else {
				message =  message + "the CSS " + requestor.getRequestorId()+ " will have <b>" +
						formatActions(item.getRequestItem().getActions()) + "</b> access to your "+data+".";
			}
			
		} else if (requestor instanceof RequestorServiceBean) {
			RequestorServiceBean requestorService = (RequestorServiceBean) requestor;
			
			message =  message + "the Service " + requestorService.getRequestorServiceId().getServiceInstanceIdentifier() + " on behalf of " + requestorService.getRequestorId() + " will have <b>" +
					formatActions(item.getRequestItem().getActions()) + "</b> access to your "+data+".";
		} else {
			message =  message + "the CSS " + requestor.getRequestorId()+ " will have <b>" +
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
			ResponsePolicy policy = this.event.getResponsePolicy();
			
			
			if(policy.getRequestor() instanceof RequestorCisBean) {
				
				RequestorCisBean requestorCis = (RequestorCisBean) policy.getRequestor();
				List<CisAdvertisementRecord> cisAdvRecords = this.requestorsController.getCisListByOwner(requestorCis.getRequestorId());
				for (CisAdvertisementRecord record: cisAdvRecords){
					if (record.getId().equalsIgnoreCase(requestorCis.getCisRequestorId())){
						return "Negotiation with Community: " + record.getName() + " of CSS: "+record.getCssownerid(); 
					}
				}
				
				return "Negotiation with Community: <Error>";
				
			} else if (policy.getRequestor() instanceof RequestorServiceBean) {
				RequestorServiceBean requestorService = (RequestorServiceBean) policy.getRequestor();
				return "Negotiation with Service " + requestorService.getRequestorServiceId().getServiceInstanceIdentifier()+" provided by CSS: "+requestorService.getRequestorId();
			}
			return "Negotiation with CSS: "+ policy.getRequestor().getRequestorId();
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
		//eventID = getIdFromQueryString();
		event = notificationsController.getPrivacyNegotiationEvent(eventID);

		if (event != null) {
			if (log.isDebugEnabled())
				log.debug("Preparing event for GUI with ID " + eventID);

			prepareEventForGUI(event);
		} else {
			log.warn("Event not found for ID " + eventID);
		}
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

	public ResponsePolicy getResponsePolicy() {
		return event != null
				? event.getResponsePolicy()
						: null;
	}

	public UserFeedbackPrivacyNegotiationEvent getCurrentNegotiationEvent() {
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

	public void completeNegotiationAction() {
		if(log.isDebugEnabled()) log.debug("completeNegotiation() id=" + eventID);

		ResponsePolicy responsePolicy = event.getResponsePolicy();
		NegotiationDetailsBean negotiationDetails = event.getNegotiationDetails();

		// TODO: validate action check boxes
		/*for (ResponseItem response : responsePolicy.getResponseItems()) {
            for (Action action : response.getRequestItem().getActions()) {
                ActionWrapper wrapper = (ActionWrapper) action;

                log.debug(String.format("%s: action %s selected = %s",
                        response.getRequestItem().getResource().getDataIdUri(),
                        action.getActionConstant().name(),
                        wrapper.isSelected()));

                // READ is always required
                if (!wrapper.isSelected() &&
                        (ActionConstants.READ.equals(action.getActionConstant()) || !action.isOptional())) {
                    String msg = "Action %s is required for %s";

                    addGlobalMessage("Validation failed",
                            String.format(msg, action.getActionConstant().name(), response.getRequestItem().getResource().getDataIdUri()),
                            FacesMessage.SEVERITY_WARN);
                    return;
                }

            }
        }*/

		prepareEventForTransmission(responsePolicy);

		responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);

		try {
			userFeedback.submitPrivacyNegotiationResponse(eventID, negotiationDetails, responsePolicy);
		} catch (Exception e) {
			addGlobalMessage("Error publishing notification of completed negotiation",
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
		} // previously, could redirect to next negotiation - but this makes no sense now
	}

	public void cancelNegotiationAction() {
		if(log.isDebugEnabled()) log.debug("cancelNegotiation()");

		ResponsePolicy responsePolicy = getCurrentNegotiationEvent().getResponsePolicy();
		NegotiationDetailsBean negotiationDetails = getCurrentNegotiationEvent().getNegotiationDetails();

		prepareEventForTransmission(responsePolicy);

		responsePolicy.setNegotiationStatus(NegotiationStatus.FAILED);

		try {
			userFeedback.submitPrivacyNegotiationResponse(eventID, negotiationDetails, responsePolicy);
		} catch (Exception e) {
			addGlobalMessage("Error publishing notification of cancelled privacy negotiation event",
					e.getMessage(),
					FacesMessage.SEVERITY_ERROR);
			log.error("Error publishing notification of cancelled privacy negotiation event", e);
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

	private static void prepareEventForGUI(UserFeedbackPrivacyNegotiationEvent event) {
		ResponseItemWrapper.wrapList(event.getResponsePolicy().getResponseItems());

		for (ResponseItem response : event.getResponsePolicy().getResponseItems()) {

			// wrap the sub items
			if (!(response.getRequestItem() instanceof RequestItemWrapper))
				response.setRequestItem(new RequestItemWrapper(response.getRequestItem()));

			// set to permit by default - the user can then change
			response.setDecision(Decision.PERMIT);

			// quickly sort by condition name
			Collections.sort(response.getRequestItem().getConditions(), new Comparator<Condition>() {
				@Override
				public int compare(Condition o1, Condition o2) {
					return o1.getConditionConstant().name().compareTo(o2.getConditionConstant().name());
				}
			});

		}

	}

	private static void prepareEventForTransmission(ResponsePolicy responsePolicy) {
		// Convert ResponseItemWrappers back to ResponseItems if necessary
		ResponseItemWrapper.unwrapList(responsePolicy.getResponseItems());

		for (ResponseItem response : responsePolicy.getResponseItems()) {

			RequestItemWrapper requestItemWrapper = (RequestItemWrapper) response.getRequestItem();

			// Action strings need to be converted back to Actions
			// Actually we're just filtering out the unselected ones

			// we always need read, add it if we haven't got it
			if (!requestItemWrapper.getSelectedActionNames().contains("READ"))
				requestItemWrapper.getSelectedActionNames().add("READ");

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
			for (int i = 0; i < requestItem.getConditions().size(); i++) {
				Condition condition = requestItem.getConditions().get(i);

				if (condition.isOptional() && condition.getValue() == null || "".equals(condition.getValue())) {
					requestItem.getConditions().remove(i);
					i--;
				}
			}
		}
	}

	public void setNewConditionToAdd(ConditionConstants newConditionToAdd) {
		this.newConditionToAdd = newConditionToAdd;

		if(log.isDebugEnabled()) log.debug("New condition to add: {}", newConditionToAdd);
	}

	public ConditionConstants getNewConditionToAdd() {
		return newConditionToAdd;
	}


	public RequestorsController getRequestorsController() {
		return requestorsController;
	}


	public void setRequestorsController(RequestorsController requestorsController) {
		this.requestorsController = requestorsController;
	}


}
