package org.societies.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.webapp.models.CssManagerForm;
import org.societies.webapp.models.CssManagerLoginForm;
import org.societies.webapp.models.LoginForm;
import org.societies.webapp.models.requests.CssRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;

@Controller
public class CssManagerController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICSSLocalManager cssLocalManager;
	@Autowired
	private ICommManager commManager;

	private CssManagerLoginForm cmControllerLoginForm = new CssManagerLoginForm();

	public ICSSLocalManager getCssLocalManager() {
		return cssLocalManager;
	}

	public void setCssLocalManager(ICSSLocalManager cssLocalManager) {
		this.cssLocalManager = cssLocalManager;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	@RequestMapping(value = "/cssmanager.html", method = RequestMethod.GET)
	public ModelAndView cssManager() {

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("message", "Welcome to the Css Manager Controller Page");

		/*
		 * We need to find out what Css we are logging into
		 */
		if (getCssLocalManager() == null) {
			model.put("message", "Css ManagerService reference not avaiable");
			return new ModelAndView("cssmanager", model);
		}

		// data model object to be used for displaying form in html page
		CssManagerLoginForm cmLoginForm = new CssManagerLoginForm();

		// TODO : Check should we do this some other way!
		INetworkNode myNode = this.getCommManager().getIdManager()
				.getThisNetworkNode();

		cmLoginForm.setCssIdentity(myNode.getBareJid());

		model.put("cmLoginForm", cmLoginForm);
		/*
		 * return modelandview object and passing login (jsp page name) and
		 * model object as constructor
		 */
		return new ModelAndView("cssmanager", model);

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/cssmanager.html", method = RequestMethod.POST)
	public ModelAndView cssManager(@Valid CssManagerLoginForm cmLoginForm,
			BindingResult result, Map model) {

		int requestActiveCount = 1;

		if (result.hasErrors()) {
			model.put("message", "Css Manager form error");
			return new ModelAndView("cssmanager", model);
		}

		if (getCssLocalManager() == null) {
			model.put("message", "Css ManagerService reference not avaiable");
			model.put("cmLoginForm", cmLoginForm);
			return new ModelAndView("cssmanager", model);
		}

		if (cmLoginForm.getPassword() != null
				&& cmLoginForm.getPassword().length() == 0) {
			model.put("message", "Error : Password must be entered");
			model.put("cmLoginForm", cmLoginForm);
			return new ModelAndView("cssmanager", model);
		}

		// Now we go a logon to the Css
		CssRecord loginRecord = new CssRecord();
		setupEmptyCssRecord(loginRecord);

		loginRecord.setCssIdentity(cmLoginForm.getCssIdentity());
		loginRecord.setPassword(cmLoginForm.getPassword());

		// List<CssRequestModel> cssads = cmLoginForm.getCssadLists();
		// List<CssRequestModel> cssFriends = cmLoginForm.getCssFriendsLists();
		// List<CssRequestModel> cssPendingRequests =
		// cmLoginForm.getCssRequestsLists();

		CssRequest request = new CssRequest();
		
		cmControllerLoginForm.setCssIdentity(cmLoginForm.getCssIdentity());
		

		// now each 'every one' of the request to see if they changed!
		if (cmControllerLoginForm.getCssAdRequests1().isActive()) {
			if (cmLoginForm.getCssAdRequests1().getValue() != null)
			{
			if (cmLoginForm.getCssAdRequests1().getValue().contentEquals("1")) { //send friend request
				getCssLocalManager().sendCssFriendRequest(
						cmControllerLoginForm.getCssAdRequests1().getAdRecObj()
								.getResultCssAdvertisementRecord().getId());
			} else if (cmLoginForm.getCssAdRequests1().getValue().contentEquals("2") //cancel pending request
					|| cmLoginForm.getCssAdRequests1().getValue().contentEquals("3")) { //leave 
				
				request.setCssIdentity(cmControllerLoginForm.getCssAdRequests1().getAdRecObj().getResultCssAdvertisementRecord().getId());
				request.setRequestStatus(CssRequestStatusType.CANCELLED);

				getCssLocalManager().updateCssFriendRequest(request);

			}
			}
		}
		if (cmControllerLoginForm.getCssAdRequests2().isActive()) {
			if (cmLoginForm.getCssAdRequests2().getValue() != null) {
			if (cmLoginForm.getCssAdRequests2().getValue().contentEquals("1")) {
				getCssLocalManager().sendCssFriendRequest(
						cmControllerLoginForm.getCssAdRequests2().getAdRecObj()
								.getResultCssAdvertisementRecord().getId());
			} else if (cmLoginForm.getCssAdRequests2().getValue().contentEquals("2") //cancel pending request
					|| cmLoginForm.getCssAdRequests2().getValue().contentEquals("3")) { //leave   
				request.setCssIdentity(cmControllerLoginForm.getCssAdRequests2().getAdRecObj().getResultCssAdvertisementRecord().getId());
				request.setRequestStatus(CssRequestStatusType.CANCELLED);

				getCssLocalManager().updateCssFriendRequest(request);
			}
			}
		}
		if (cmLoginForm.getCssAdRequests3().isActive()) {
			if (cmLoginForm.getCssAdRequests3().getValue() != null) {
			if (cmLoginForm.getCssAdRequests3().getValue().contentEquals("1")) {
				getCssLocalManager().sendCssFriendRequest(
						cmLoginForm.getCssAdRequests3().getAdRecObj()
								.getResultCssAdvertisementRecord().getId());
			} else if (cmLoginForm.getCssAdRequests3().getValue().contentEquals("2") //cancel pending request
					|| cmLoginForm.getCssAdRequests3().getValue().contentEquals("3")) { //leave   
				request.setCssIdentity(cmControllerLoginForm.getCssAdRequests3().getAdRecObj().getResultCssAdvertisementRecord().getId());
				request.setRequestStatus(CssRequestStatusType.CANCELLED);

				getCssLocalManager().updateCssFriendRequest(request);
			}
		}
		}
		
		if (cmControllerLoginForm.getCssRequests1().isActive()) {
			if (cmLoginForm.getCssRequests1().getValue() != null)
			{
				if (cmLoginForm.getCssRequests1().getValue().contentEquals("1")) { //accept
				
				request.setCssIdentity(cmControllerLoginForm.getCssRequests1().getCssRequestObj().getCssIdentity());
				request.setRequestStatus(CssRequestStatusType.ACCEPTED);
				getCssLocalManager().updateCssRequest(request);
				
					
				}else if (cmLoginForm.getCssRequests1().getValue().contentEquals("2")) { //reject
					
					request.setCssIdentity(cmControllerLoginForm.getCssRequests1().getCssRequestObj().getCssIdentity());
					request.setRequestStatus(CssRequestStatusType.DENIED);
					getCssLocalManager().updateCssRequest(request);
					
						
				} else if (cmLoginForm.getCssRequests1().getValue().contentEquals("3")) { //reject
					
					request.setCssIdentity(cmControllerLoginForm.getCssRequests1().getCssRequestObj().getCssIdentity());
					request.setRequestStatus(CssRequestStatusType.CANCELLED);
					getCssLocalManager().updateCssRequest(request);
					
						
				} 
				
			
			}
		}
		
		if (cmControllerLoginForm.getCssRequests2().isActive()) {
			if (cmLoginForm.getCssRequests2().getValue() != null)
			{
				if (cmLoginForm.getCssRequests2().getValue().contentEquals("1")) { //accept
				
				request.setCssIdentity(cmControllerLoginForm.getCssRequests2().getCssRequestObj().getCssIdentity());
				request.setRequestStatus(CssRequestStatusType.ACCEPTED);
				getCssLocalManager().updateCssRequest(request);
				
					
				}else if (cmLoginForm.getCssRequests2().getValue().contentEquals("2")) { //reject
					
					request.setCssIdentity(cmControllerLoginForm.getCssRequests2().getCssRequestObj().getCssIdentity());
					request.setRequestStatus(CssRequestStatusType.DENIED);
					getCssLocalManager().updateCssRequest(request);
					
						
				} else if (cmLoginForm.getCssRequests2().getValue().contentEquals("3")) { //reject
					
					request.setCssIdentity(cmControllerLoginForm.getCssRequests2().getCssRequestObj().getCssIdentity());
					request.setRequestStatus(CssRequestStatusType.CANCELLED);
					getCssLocalManager().updateCssRequest(request);
					
						
				} 
				
			
			}
		}
		
		if (cmControllerLoginForm.getCssRequests3().isActive()) {
			if (cmLoginForm.getCssRequests3().getValue() != null)
			{
				if (cmLoginForm.getCssRequests3().getValue().contentEquals("1")) { //accept
				
				request.setCssIdentity(cmControllerLoginForm.getCssRequests3().getCssRequestObj().getCssIdentity());
				request.setRequestStatus(CssRequestStatusType.ACCEPTED);
				getCssLocalManager().updateCssRequest(request);
				
					
				}else if (cmLoginForm.getCssRequests3().getValue().contentEquals("2")) { //reject
					
					request.setCssIdentity(cmControllerLoginForm.getCssRequests3().getCssRequestObj().getCssIdentity());
					request.setRequestStatus(CssRequestStatusType.DENIED);
					getCssLocalManager().updateCssRequest(request);
					
						
				} else if (cmLoginForm.getCssRequests3().getValue().contentEquals("3")) { //reject
					
					request.setCssIdentity(cmControllerLoginForm.getCssRequests3().getCssRequestObj().getCssIdentity());
					request.setRequestStatus(CssRequestStatusType.CANCELLED);
					getCssLocalManager().updateCssRequest(request);
					
						
				} 
				
			
			}
		}
		
	
		/*
		 * if (cssFriends != null && cssFriends.size() > 0){ for
		 * (CssRequestModel cssFriendRequestmodel : cssFriends) {
		 * 
		 * 
		 * if (cssFriendRequestmodel.getValue().contentEquals("1")) { //
		 * getCssLocalManager
		 * ().sendCssFriendRequest(cssFriendRequestmodel.getCssRequestObj
		 * ().getCssIdentity()); }
		 * 
		 * } } if (cssPendingRequests != null && cssPendingRequests.size() > 0){
		 * for (CssRequestModel cssPending : cssPendingRequests) {
		 * 
		 * 
		 * if (cssPending.getValue().contentEquals("1")) {
		 * cssPending.getCssRequestObj
		 * ().setRequestStatus(CssRequestStatusType.ACCEPTED);
		 * getCssLocalManager().updateCssRequest(cssPending.getCssRequestObj());
		 * } else if (cssPending.getValue().contentEquals("2")) {
		 * cssPending.getCssRequestObj
		 * ().setRequestStatus(CssRequestStatusType.DENIED);
		 * getCssLocalManager().updateCssRequest(cssPending.getCssRequestObj());
		 * }
		 * 
		 * } } /*break; case 2 : // accept friend request request = new
		 * CssRequest(); request.setCssIdentity(cssRequested.getIdentity());
		 * request.setRequestStatus(CssRequestStatusType.ACCEPTED);
		 * getCssLocalManager().updateCssRequest(request); break;
		 * 
		 * } }
		 */

		try {

			Future<CssInterfaceResult> loginResult = getCssLocalManager()
					.getCssRecord();

			if (loginResult == null) {
				// No CssRecord we eed to create one
				loginResult = getCssLocalManager().registerCSS(loginRecord);
				model.put("message", "created Css Record");
			} else {
				if (((CssInterfaceResult) loginResult.get()).isResultStatus() == false) {
					// No CssRecord we eed to create one
					loginResult = getCssLocalManager().registerCSS(loginRecord);
					model.put("message", "created Css Record");
				} else {
					loginResult = getCssLocalManager().loginCSS(loginRecord);

					if (((CssInterfaceResult) loginResult.get())
							.isResultStatus() == false) {

						model.put("message",
								"Css ManagerService Incorrect Password");
						return new ModelAndView("cssmanager", model);
					}
					model.put("message",
							"Welcome to the Css Manager Controller Page");
				}
			}

			// else if (model.get("state").equals("2"))
			// {
			// send requests
			// model.get(key)
			// }

			// Update all data
			Future<List<CssAdvertisementRecordDetailed>> cssadverts = getCssLocalManager()
					.getCssAdvertisementRecordsFull();
			Future<List<CssRequest>> friendCss = getCssLocalManager()
					.findAllCssFriendRequests();
			Future<List<CssRequest>> cssRequests = getCssLocalManager()
					.findAllCssRequests();

			List<CssAdvertisementRecordDetailed> dbCssAds = cssadverts.get();
			CssRequestModel cssRM = null;

			cmControllerLoginForm.getCssAdRequests1().setActive(false);
			cmControllerLoginForm.getCssAdRequests2().setActive(false);
			cmControllerLoginForm.getCssAdRequests3().setActive(false);
			cmControllerLoginForm.getCssAdRequests4().setActive(false);
			cmControllerLoginForm.getCssAdRequests5().setActive(false);

			if (dbCssAds != null && dbCssAds.size() > 0) {
				requestActiveCount = 1;
				for (CssAdvertisementRecordDetailed cssAdDetails : dbCssAds) {
					// We don't want to show ourselfs!
					if (!cssAdDetails.getResultCssAdvertisementRecord().getId()
							.contentEquals(cmLoginForm.getCssIdentity())) {
						switch (requestActiveCount) {
						case 1:
							cmControllerLoginForm.getCssAdRequests1()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests1()
									.setActive(true);
							requestActiveCount++;
							break;
						case 2:
							cmControllerLoginForm.getCssAdRequests2()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests2()
									.setActive(true);
							requestActiveCount++;
							break;
						case 3:
							cmControllerLoginForm.getCssAdRequests3()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests3()
									.setActive(true);
							requestActiveCount++;
							break;
						case 4:
							cmControllerLoginForm.getCssAdRequests4()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests4()
									.setActive(true);
							requestActiveCount++;
							break;
						case 5:
							cmControllerLoginForm.getCssAdRequests5()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests5()
									.setActive(true);
							requestActiveCount++;
							break;
						default:
							// since we can't se dynamic lists we are
							// constrainted to 5 so .....

						}
					}
				}

			}
			model.put("cssadverts", cssadverts.get());
			// model.put("cssads", cssads);

			
			List<CssRequest> dbCssRequests = cssRequests.get();
			
			cmControllerLoginForm.getCssRequests1().setActive(false);
			cmControllerLoginForm.getCssRequests2().setActive(false);
			cmControllerLoginForm.getCssRequests3().setActive(false);
			cmControllerLoginForm.getCssRequests4().setActive(false);
			cmControllerLoginForm.getCssRequests5().setActive(false);

			if (dbCssRequests != null && dbCssRequests.size() > 0) {
				requestActiveCount = 1;
				for (CssRequest cssRequestDB : dbCssRequests) {
					
					
						switch (requestActiveCount) {
						case 1:
							cmControllerLoginForm.getCssRequests1().setCssRequestObj(cssRequestDB);
							cmControllerLoginForm.getCssRequests1().setActive(true);
							requestActiveCount++;
							break;
						case 2:
							cmControllerLoginForm.getCssRequests2().setCssRequestObj(cssRequestDB);
							cmControllerLoginForm.getCssRequests2().setActive(true);
							requestActiveCount++;
							break;
						case 3:
							cmControllerLoginForm.getCssRequests3().setCssRequestObj(cssRequestDB);
							cmControllerLoginForm.getCssRequests3().setActive(true);
							requestActiveCount++;
							break;
						case 4:
							cmControllerLoginForm.getCssRequests4().setCssRequestObj(cssRequestDB);
							cmControllerLoginForm.getCssRequests4().setActive(true);
							requestActiveCount++;
							break;
						case 5:
							cmControllerLoginForm.getCssRequests5().setCssRequestObj(cssRequestDB);
							cmControllerLoginForm.getCssRequests5().setActive(true);
							requestActiveCount++;
							break;
						default:
							// since we can't se dynamic lists we are
							// constrainted to 5 so .....

						}
					}
				}

			
			List<CssRequest> dbCssFriendsRequests = friendCss.get();
			
			cmControllerLoginForm.getCssFriendRequests1().setActive(false);
			cmControllerLoginForm.getCssFriendRequests2().setActive(false);
			cmControllerLoginForm.getCssFriendRequests3().setActive(false);
			cmControllerLoginForm.getCssFriendRequests4().setActive(false);
			cmControllerLoginForm.getCssFriendRequests5().setActive(false);

			if (dbCssFriendsRequests != null && dbCssFriendsRequests.size() > 0) {
				requestActiveCount = 1;
				for (CssRequest cssFriendRequestDB : dbCssFriendsRequests) {
					
					
						switch (requestActiveCount) {
						case 1:
							cmControllerLoginForm.getCssFriendRequests1().setCssRequestObj(cssFriendRequestDB);
							cmControllerLoginForm.getCssFriendRequests1().setActive(true);
							requestActiveCount++;
							break;
						case 2:
							cmControllerLoginForm.getCssFriendRequests2().setCssRequestObj(cssFriendRequestDB);
							cmControllerLoginForm.getCssFriendRequests2().setActive(true);
							requestActiveCount++;
							break;
						case 3:
							cmControllerLoginForm.getCssFriendRequests3().setCssRequestObj(cssFriendRequestDB);
							cmControllerLoginForm.getCssFriendRequests3().setActive(true);
							requestActiveCount++;
							break;
						case 4:
							cmControllerLoginForm.getCssFriendRequests4().setCssRequestObj(cssFriendRequestDB);
							cmControllerLoginForm.getCssFriendRequests4().setActive(true);
							requestActiveCount++;
							break;
						case 5:
							cmControllerLoginForm.getCssFriendRequests5().setCssRequestObj(cssFriendRequestDB);
							cmControllerLoginForm.getCssFriendRequests5().setActive(true);
							requestActiveCount++;
							break;
						default:
							// since we can't se dynamic lists we are
							// constrainted to 5 so .....

						}
					}
				}

			
			
			/*
			 * 
			 * List<CssRequest> dbCssFriends = friendCss.get();
			 * cssFriends.clear();
			 * 
			 * if (dbCssFriends != null && dbCssFriends.size() > 0){ for
			 * (CssRequest cssFriendsDetails : dbCssFriends) {
			 * cssRM.setCssRequestObj(cssFriendsDetails);
			 * cssRM.setChanged(false); // cssRM.setValue("1");
			 * cssFriends.add(cssRM);
			 * 
			 * }
			 * 
			 * }
			 * 
			 * 
			 * model.put("friendCss", friendCss.get());
			 * model.put("friendCssDetails", cssFriends);
			 * 
			 * 
			 * List<CssRequest> dbCssRequest = cssRequests.get();
			 * cssPendingRequests.clear();
			 * 
			 * if (dbCssRequest != null && dbCssRequest.size() > 0){ for
			 * (CssRequest cssRequestDetails : dbCssRequest) {
			 * cssRM.setCssRequestObj(cssRequestDetails);
			 * cssRM.setChanged(false); // cssRM.setValue("1");
			 * cssPendingRequests.add(cssRM);
			 * 
			 * }
			 * 
			 * }
			 * 
			 * model.put("cssRequests", cssRequests.get());
			 * model.put("cssPendingRequests", cssPendingRequests); // We have
			 * sucessfully login in , now display details
			 */
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			model.put("message", e.getMessage());
			model.put("cmLoginForm", cmLoginForm);
			return new ModelAndView("cssmanager", model);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			model.put("message", e.getMessage());
			model.put("cmLoginForm", cmLoginForm);
			return new ModelAndView("cssmanager", model);
		}

		model.put("cmLoginForm", cmControllerLoginForm);

		return new ModelAndView("cssmanagerresult", model);

	}

	void setupEmptyCssRecord(CssRecord rec) {
		rec.setCssIdentity("");
		rec.setPassword("");
		rec.setDomainServer("");
		rec.setCssHostingLocation("");
		rec.setCssInactivation("");
		rec.setCssRegistration("");
		rec.setCssUpTime(0);
		rec.setDomainServer("");
		rec.setEmailID("");
		rec.setEntity(0);
		rec.setForeName("");
		rec.setHomeLocation("");
		rec.setIdentityName("");
		rec.setImID("");
		rec.setName("");
		rec.setPresence(0);
		rec.setSex(0);
		rec.setSocialURI("");
		rec.setStatus(0);

		List<CssNode> cssNodes = rec.getCssNodes();
		cssNodes = new ArrayList<CssNode>();

		List<CssNode> archiveCSSNodes = rec.getCssNodes();
		archiveCSSNodes = new ArrayList<CssNode>();
	}

}