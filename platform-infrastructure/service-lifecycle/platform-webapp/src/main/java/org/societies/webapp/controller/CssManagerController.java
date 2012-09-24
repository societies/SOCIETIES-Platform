package org.societies.webapp.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.validation.Valid;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.comm.ICommManagerController;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.internal.servicelifecycle.IServiceControl;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceControlException;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlResult;
import org.societies.webapp.models.CssManagerLoginForm;
import org.societies.webapp.models.requests.CssRequestModel;
import org.societies.webapp.models.requests.CssServiceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CssManagerController {

	/**
	 * OSGI service get auto injected
	 */
	@Autowired
	private ICSSLocalManager cssLocalManager;
	@Autowired
	private ICommManager commManager;
	@Autowired
	private IServiceDiscovery sdService;
	@Autowired
	private IServiceControl scService;
	@Autowired
	private ICommManagerController commManagerControl;
	
	
	private CssManagerLoginForm cmControllerLoginForm = new CssManagerLoginForm();

	/**
	 * @return the commManagerControl
	 */
	public ICommManagerController getCommManagerControl() {
		return commManagerControl;
	}

	/**
	 * @param commManagerControl the commManagerControl to set
	 */
	public void setCommManagerControl(ICommManagerController commManagerControl) {
		this.commManagerControl = commManagerControl;
	}

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
	
	public IServiceDiscovery getSDService() {
		return sdService;
	}

	public void getSDService(IServiceDiscovery sdService) {
		this.sdService = sdService;
	}
	
	public IServiceControl getSCService() {
		return scService;
	}

	public void setSCService(IServiceControl scService) {
		this.scService = scService;
	}
	
	final int MAX_REQUESTS = 5;

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
		int friendServiceCount = 1;

		if (result.hasErrors()) {
			model.put("message", "Css Manager form error");
			return new ModelAndView("cssmanager", model);
		}

		if (getCssLocalManager() == null) {
			model.put("message", "Css ManagerService reference not avaiable");
			model.put("cmLoginForm", cmLoginForm);
			return new ModelAndView("cssmanager", model);
		}

		if(getCommManagerControl() != null) {
			Set<INetworkNode> allNodes = getCommManagerControl().getOtherNodes();
			allNodes.add(getCommManager().getIdManager().getThisNetworkNode());
			model.put("allNodes", allNodes);
		}
		
		if (cmLoginForm.getPassword() != null
				&& cmLoginForm.getPassword().length() == 0) {
			model.put("message", "Error : Password must be entered");
			model.put("cmLoginForm", cmLoginForm);
			return new ModelAndView("cssmanager", model);
		}

		cmControllerLoginForm.setCssIdentity(cmLoginForm.getCssIdentity());
		cmControllerLoginForm.setCssAdId(cmLoginForm.getCssIdentity());
		
		// Now we go a logon to the Css
		CssRecord loginRecord = new CssRecord();
		setupEmptyCssRecord(loginRecord);

		loginRecord.setCssIdentity(cmLoginForm.getCssIdentity());
		loginRecord.setPassword(cmLoginForm.getPassword());
		
		if (cmLoginForm.getButtonLabel().contentEquals("Save"))
		{
			
			loginRecord.setCssHostingLocation(cmLoginForm.getCssHostingLocation());
			loginRecord.setDomainServer(cmLoginForm.getDomainServer());
			loginRecord.setEmailID(cmLoginForm.getEmailID());
			loginRecord.setHomeLocation(cmLoginForm.getHomeLocation());
			loginRecord.setIdentityName(cmLoginForm.getIdentityName());
			loginRecord.setImID(cmLoginForm.getImID());
			loginRecord.setName(cmLoginForm.getName());
			loginRecord.setSex(cmLoginForm.getSex());
		}
		
		
		if (cmLoginForm.getButtonLabel().contentEquals("Save"))
		{
			doUpdatesMessaging(cmLoginForm);
			model.put("buttonmessage", "Did the updates");
		}
		else
		{
			model.put("buttonmessage", "didn't do updates");
				
		}
				
		
			
	
		try {

			Future<CssInterfaceResult> loginResult = getCssLocalManager()
					.getCssRecord();
			
			CssInterfaceResult cssDetails = null; 
			if (loginResult == null) {
				// No CssRecord we eed to create one
				loginResult = getCssLocalManager().registerCSS(loginRecord);
				
				// If there was no cssRecord, there was no css advertisement record
				 CssAdvertisementRecord cssAdvert = new CssAdvertisementRecord();
				 cssAdvert.setId(cmLoginForm.getCssIdentity());
				 cssAdvert.setName(" ");
				 cssAdvert.setUri(" ");
				 getCssLocalManager().addAdvertisementRecord(cssAdvert);
				
				
				model.put("message", "created Css Record");
			} else {
				if (((CssInterfaceResult) loginResult.get()).isResultStatus() == false) {
					// No CssRecord we eed to create one
					loginResult = getCssLocalManager().registerCSS(loginRecord);
					
					// If there was no cssRecord, there was no css advertisement record
					 CssAdvertisementRecord cssAdvert = new CssAdvertisementRecord();
					 cssAdvert.setId(cmLoginForm.getCssIdentity());
					 cssAdvert.setName(" ");
					 cssAdvert.setUri(" ");
					getCssLocalManager().addAdvertisementRecord(cssAdvert);
					
					model.put("message", "created Css Record");
				} else {
					cssDetails = loginResult.get();
					
					if (cmLoginForm.getButtonLabel().contentEquals("Logon"))
					{
						loginResult = getCssLocalManager().loginCSS(loginRecord);
						

						if (cssDetails.isResultStatus() == false) {

							model.put("message",
								"Css ManagerService Incorrect Password");
							return new ModelAndView("cssmanager", model);
						}
						model.put("message",
							"Welcome to the Css Manager Controller Page");
					}
					else if (cmLoginForm.getButtonLabel().contentEquals("Save"))
					{
						getCssLocalManager().modifyCssRecord(loginRecord);
						Future<CssInterfaceResult> asynCssDetails = getCssLocalManager().getCssRecord();
						 cssDetails = asynCssDetails.get();
						 
						
						 CssAdvertisementRecord cssAdOld = new CssAdvertisementRecord();
						 CssAdvertisementRecord cssAdNew = new CssAdvertisementRecord();
						 cssAdOld.setId(cmControllerLoginForm.getCssAdId());
						 cssAdOld.setName(cmControllerLoginForm.getCssAdName());
						 cssAdOld.setUri(cmControllerLoginForm.getCssAdUri());
						 cssAdNew.setId(cmLoginForm.getCssAdId());
						 cssAdNew.setName(cmLoginForm.getCssAdName());
						 cssAdNew.setUri(cmLoginForm.getCssAdUri());
						 
						 getCssLocalManager().updateAdvertisementRecord(cssAdOld, cssAdNew);
						 
						 
					}
					
					
					cmControllerLoginForm.setCssHostingLocation(cssDetails.getProfile().getCssHostingLocation());
					cmControllerLoginForm.setDomainServer(cssDetails.getProfile().getDomainServer());
					cmControllerLoginForm.setEmailID(cssDetails.getProfile().getEmailID());
					cmControllerLoginForm.setHomeLocation(cssDetails.getProfile().getHomeLocation());
					cmControllerLoginForm.setIdentityName(cssDetails.getProfile().getIdentityName());
					cmControllerLoginForm.setImID(cssDetails.getProfile().getImID());
					cmControllerLoginForm.setName(cssDetails.getProfile().getName());
					cmControllerLoginForm.setSex(cssDetails.getProfile().getSex());
					
				}
			}
			
			
			
			
		
			
			
				// Update all data
			Future<List<CssAdvertisementRecordDetailed>> cssadverts = getCssLocalManager()
					.getCssAdvertisementRecordsFull();
			Future<List<CssRequest>> friendCss = getCssLocalManager()
					.findAllCssFriendRequests();
			Future<List<CssRequest>> cssRequests = getCssLocalManager()
					.findAllCssRequests();
			Future<List<CssAdvertisementRecord>> asynchCssFriends = getCssLocalManager().getCssFriends();
						
			Future<List<Service>> asynchServices = null;
			List<Service> friendServices =  new ArrayList<Service>();
			

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
					if (cssAdDetails.getResultCssAdvertisementRecord().getId().contentEquals(cmLoginForm.getCssIdentity())) {
						cmControllerLoginForm.setCssAdId(cssAdDetails.getResultCssAdvertisementRecord().getId());
						cmControllerLoginForm.setCssAdName(cssAdDetails.getResultCssAdvertisementRecord().getName());
						cmControllerLoginForm.setCssAdUri(cssAdDetails.getResultCssAdvertisementRecord().getUri());
									
					}
					else {
						switch (requestActiveCount) {
						case 1:
							cmControllerLoginForm.getCssAdRequests1()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests1()
									.setActive(true);
							requestActiveCount++;
							
							cmControllerLoginForm.getCssFriendService11().setActive(false);
							cmControllerLoginForm.getCssFriendService12().setActive(false);
							cmControllerLoginForm.getCssFriendService13().setActive(false);
							cmControllerLoginForm.getCssFriendService14().setActive(false);
							cmControllerLoginForm.getCssFriendService15().setActive(false);
							
							
							// if friends, then get services
							if (cssAdDetails.getStatus() == CssRequestStatusType.ACCEPTED)
							{
								try {
									asynchServices = this.getSDService().getServices(cssAdDetails.getResultCssAdvertisementRecord().getId());
									friendServices = asynchServices.get();
									System.out.println("~~~~~~~~~~~~~~~ asynchServices is : " +asynchServices);
									System.out.println("~~~~~~~~~~~~~~~ getId() is : " +cssAdDetails.getResultCssAdvertisementRecord().getId());
									System.out.println("~~~~~~~~~~~~~~~ friendServices size is : " +friendServices);
								} catch (ServiceDiscoveryException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								friendServiceCount = 1;
								//don't fall over if friend container not available!
								if (friendServices != null) {
								for ( int j = 0; (j < friendServices.size()) && (j < 5); j++)
								{
									switch (friendServiceCount) {
									case 1:
										cmControllerLoginForm.getCssFriendService11().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService11().setActive(true);
										break;
									case 2:
										cmControllerLoginForm.getCssFriendService12().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService12().setActive(true);
										break;
									case 3:
										cmControllerLoginForm.getCssFriendService13().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService13().setActive(true);
										break;
									case 4:
										cmControllerLoginForm.getCssFriendService14().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService14().setActive(true);
										break;
									case 5:
										cmControllerLoginForm.getCssFriendService15().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService15().setActive(true);
										break;
									}	
									friendServiceCount++;
								}
								}
							}
							
							break;
						case 2:
							cmControllerLoginForm.getCssAdRequests2()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests2()
									.setActive(true);
							requestActiveCount++;
							
							cmControllerLoginForm.getCssFriendService21().setActive(false);
							cmControllerLoginForm.getCssFriendService22().setActive(false);
							cmControllerLoginForm.getCssFriendService23().setActive(false);
							cmControllerLoginForm.getCssFriendService24().setActive(false);
							cmControllerLoginForm.getCssFriendService25().setActive(false);
							
							// if friends, then get services
							if (cssAdDetails.getStatus() == CssRequestStatusType.ACCEPTED)
							{
								try {
									asynchServices = this.getSDService().getServices(cssAdDetails.getResultCssAdvertisementRecord().getId());
									friendServices = asynchServices.get();
								} catch (ServiceDiscoveryException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								friendServiceCount = 1;
								//don't fall over if friend container not available!
								if (friendServices != null) {
								for ( int j = 0; (j < friendServices.size()) && (j < 5); j++)
								{
									switch (friendServiceCount) {
									case 1:
										cmControllerLoginForm.getCssFriendService21().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService21().setActive(true);
										break;
									case 2:
										cmControllerLoginForm.getCssFriendService22().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService22().setActive(true);
										break;
									case 3:
										cmControllerLoginForm.getCssFriendService23().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService23().setActive(true);
										break;
									case 4:
										cmControllerLoginForm.getCssFriendService24().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService24().setActive(true);
										break;
									case 5:
										cmControllerLoginForm.getCssFriendService25().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService25().setActive(true);
										break;
									}	

									friendServiceCount++;
								}
								}
							
							}
							break;
						case 3:
							cmControllerLoginForm.getCssAdRequests3()
									.setAdRecObj(cssAdDetails);
							cmControllerLoginForm.getCssAdRequests3()
									.setActive(true);
							requestActiveCount++;
							
							cmControllerLoginForm.getCssFriendService31().setActive(false);
							cmControllerLoginForm.getCssFriendService32().setActive(false);
							cmControllerLoginForm.getCssFriendService33().setActive(false);
							cmControllerLoginForm.getCssFriendService34().setActive(false);
							cmControllerLoginForm.getCssFriendService35().setActive(false);
							
							// if friends, then get services
							if (cssAdDetails.getStatus() == CssRequestStatusType.ACCEPTED)
							{
								try {
									asynchServices = this.getSDService().getServices(cssAdDetails.getResultCssAdvertisementRecord().getId());
									friendServices = asynchServices.get();
								} catch (ServiceDiscoveryException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								friendServiceCount = 1;
								//don't fall over if friend container not available!
								if (friendServices != null) {
								for ( int j = 0; (j < friendServices.size()) && (j < 5); j++)
								{
									switch (friendServiceCount) {
									case 1:
										cmControllerLoginForm.getCssFriendService31().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService31().setActive(true);
										break;
									case 2:
										cmControllerLoginForm.getCssFriendService32().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService32().setActive(true);
										break;
									case 3:
										cmControllerLoginForm.getCssFriendService33().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService33().setActive(true);
										break;
									case 4:
										cmControllerLoginForm.getCssFriendService34().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService34().setActive(true);
										break;
									case 5:
										cmControllerLoginForm.getCssFriendService35().setServiceDetails(friendServices.get(j));
										cmControllerLoginForm.getCssFriendService35().setActive(true);
										break;
									}	

									friendServiceCount++;
								}
								}
								
							}
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
			
			model.put("cssFriends", asynchCssFriends.get());
			
			
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
			
			
			List<Service> myServices = null;
			Future<List<Service>> asynchMyServices = null;
			try {
				asynchMyServices = this.getSDService().getLocalServices();
				myServices = asynchMyServices.get();
			} catch (ServiceDiscoveryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cmControllerLoginForm.getCssService1().setActive(false);
			cmControllerLoginForm.getCssService2().setActive(false);
			cmControllerLoginForm.getCssService3().setActive(false);
			cmControllerLoginForm.getCssService4().setActive(false);
			cmControllerLoginForm.getCssService5().setActive(false);
			
			for ( int j = 0; (j < myServices.size()) && (j < 5); j++)
			{
				switch (j) {
				case 0:
					cmControllerLoginForm.getCssService1().setServiceDetails(myServices.get(j));
					cmControllerLoginForm.getCssService1().setActive(true);
					break;
				case 1:
					cmControllerLoginForm.getCssService2().setServiceDetails(myServices.get(j));
					cmControllerLoginForm.getCssService2().setActive(true);
					break;
				case 2:
					cmControllerLoginForm.getCssService3().setServiceDetails(myServices.get(j));
					cmControllerLoginForm.getCssService3().setActive(true);
					break;
				case 3:
					cmControllerLoginForm.getCssService4().setServiceDetails(myServices.get(j));
					cmControllerLoginForm.getCssService4().setActive(true);
					break;
				case 4:
					cmControllerLoginForm.getCssService5().setServiceDetails(myServices.get(j));
					cmControllerLoginForm.getCssService5().setActive(true);
					break;
				}	
				friendServiceCount++;
			}
			
		
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
	
	void startService(CssServiceModel serviceModel)
	{
		
	
	ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
	
	Future<ServiceControlResult> asynchResult = null;
	
	serviceId.setServiceInstanceIdentifier(serviceModel.getServiceDetails().getServiceIdentifier().getServiceInstanceIdentifier());
	try {
		serviceId.setIdentifier(new URI(serviceModel.getServiceDetails().getServiceIdentifier().getIdentifier().toString()));
		asynchResult=this.getSCService().startService(serviceId);
		asynchResult.get();
		
	} catch (URISyntaxException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (ServiceControlException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	void stopService(CssServiceModel serviceModel)
	{
		
	
	ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
	
	Future<ServiceControlResult> asynchResult = null;
	
	serviceId.setServiceInstanceIdentifier(serviceModel.getServiceDetails().getServiceIdentifier().getServiceInstanceIdentifier());
	try {
		serviceId.setIdentifier(new URI(serviceModel.getServiceDetails().getServiceIdentifier().getIdentifier().toString()));
		asynchResult=this.getSCService().stopService(serviceId);
		asynchResult.get();
		
	} catch (URISyntaxException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (ServiceControlException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	void doControlService(CssServiceModel serviceModel, String command)
	{
		if (command.contentEquals("1")) { //stop service
			
			this.stopService(serviceModel);
			
		} else if (command.contentEquals("2")) { //start service
			this.startService(serviceModel);
			
		}
		
	}
	
	void doControlFriendRequests(CssAdvertisementRecordDetailed requestModel, String command)
	{
		if (command.contentEquals("1")) { //send friend request
			
			getCssLocalManager().sendCssFriendRequest(requestModel.getResultCssAdvertisementRecord().getId());
		} else if (command.contentEquals("2") //cancel pending request
			|| command.contentEquals("3")) { //leave
			CssRequest request = new CssRequest();
			request.setOrigin(CssRequestOrigin.LOCAL);
			request.setCssIdentity(requestModel.getResultCssAdvertisementRecord().getId());
			request.setRequestStatus(CssRequestStatusType.CANCELLED);

			getCssLocalManager().updateCssFriendRequest(request);

		}
		
	}
	
	void doControlCssRequests(CssRequest requestModel, String command)
	{
		CssRequest request = new CssRequest();
		CssRequest requestrecipricol = new CssRequest();
		
		if (command.contentEquals("1")) { //accept
			request.setOrigin(CssRequestOrigin.LOCAL);
			request.setCssIdentity(requestModel.getCssIdentity());
			request.setRequestStatus(CssRequestStatusType.ACCEPTED);
			getCssLocalManager().updateCssRequest(request);
			
			//Add accepted friend as friend to local friends list
			requestrecipricol.setOrigin(CssRequestOrigin.REMOTE);
			requestrecipricol.setCssIdentity(requestModel.getCssIdentity());
			requestrecipricol.setRequestStatus(CssRequestStatusType.ACCEPTED);
			getCssLocalManager().updateCssFriendRequest(requestrecipricol);
			
		}else if (command.contentEquals("2")) { //reject
			
			request.setOrigin(CssRequestOrigin.LOCAL);
			request.setCssIdentity(requestModel.getCssIdentity());
			request.setRequestStatus(CssRequestStatusType.DENIED);
			getCssLocalManager().updateCssRequest(request);
				
		} else if (command.contentEquals("3")) { //reject
			request.setOrigin(CssRequestOrigin.LOCAL);
			request.setCssIdentity(requestModel.getCssIdentity());
			request.setRequestStatus(CssRequestStatusType.CANCELLED);
			getCssLocalManager().updateCssRequest(request);
				
		} 
		
	
		
	}
	
	void doUpdatesMessaging(CssManagerLoginForm cmLoginForm)
	{

		
		// Check Services first
		
		if ((cmControllerLoginForm.getCssService1().isActive()) &&  (cmLoginForm.getCssService1().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssService1(), cmLoginForm.getCssService1().getValue());

		}
		
		if ((cmControllerLoginForm.getCssService2().isActive()) &&  (cmLoginForm.getCssService2().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssService2(), cmLoginForm.getCssService2().getValue());

		}
		if ((cmControllerLoginForm.getCssService3().isActive()) &&  (cmLoginForm.getCssService3().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssService3(), cmLoginForm.getCssService3().getValue());

		}
		if ((cmControllerLoginForm.getCssService4().isActive()) &&  (cmLoginForm.getCssService4().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssService4(), cmLoginForm.getCssService4().getValue());

		}
		if ((cmControllerLoginForm.getCssService5().isActive()) &&  (cmLoginForm.getCssService5().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssService5(), cmLoginForm.getCssService5().getValue());

		}
		
		if ((cmControllerLoginForm.getCssFriendService11().isActive()) &&  (cmLoginForm.getCssFriendService11().getValue() != null)) {
				doControlService(cmControllerLoginForm.getCssFriendService11(), cmLoginForm.getCssFriendService11().getValue());
	
		}
		if ((cmControllerLoginForm.getCssFriendService12().isActive()) &&  (cmLoginForm.getCssFriendService12().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssFriendService12(), cmLoginForm.getCssFriendService12().getValue());

		}
		if ((cmControllerLoginForm.getCssFriendService13().isActive()) &&  (cmLoginForm.getCssFriendService13().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssFriendService13(), cmLoginForm.getCssFriendService13().getValue());

		}
		if ((cmControllerLoginForm.getCssFriendService14().isActive()) &&  (cmLoginForm.getCssFriendService14().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssFriendService14(), cmLoginForm.getCssFriendService14().getValue());

		}
		if ((cmControllerLoginForm.getCssFriendService15().isActive()) &&  (cmLoginForm.getCssFriendService15().getValue() != null)) {
			doControlService(cmControllerLoginForm.getCssFriendService15(), cmLoginForm.getCssFriendService15().getValue());

		}
		
		
		
		

		// now each 'every one' of the request to see if they changed!
		if ((cmControllerLoginForm.getCssAdRequests1().isActive())  && (cmLoginForm.getCssAdRequests1().getValue() != null)) {
			doControlFriendRequests(cmControllerLoginForm.getCssAdRequests1().getAdRecObj(), cmLoginForm.getCssAdRequests1().getValue());
		}
		if ((cmControllerLoginForm.getCssAdRequests2().isActive())  && (cmLoginForm.getCssAdRequests2().getValue() != null)) {
			doControlFriendRequests(cmControllerLoginForm.getCssAdRequests2().getAdRecObj(), cmLoginForm.getCssAdRequests2().getValue());
		}
		if ((cmControllerLoginForm.getCssAdRequests3().isActive())  && (cmLoginForm.getCssAdRequests3().getValue() != null)) {
			doControlFriendRequests(cmControllerLoginForm.getCssAdRequests3().getAdRecObj(), cmLoginForm.getCssAdRequests3().getValue());
		}
		
	
		
		if ((cmControllerLoginForm.getCssRequests1().isActive()) &&  (cmLoginForm.getCssRequests1().getValue() != null)) {
			this.doControlCssRequests(cmControllerLoginForm.getCssRequests1().getCssRequestObj(), cmLoginForm.getCssRequests1().getValue());
		}
		if ((cmControllerLoginForm.getCssRequests2().isActive()) &&  (cmLoginForm.getCssRequests2().getValue() != null)) {
			this.doControlCssRequests(cmControllerLoginForm.getCssRequests2().getCssRequestObj(), cmLoginForm.getCssRequests2().getValue());
		}
		if ((cmControllerLoginForm.getCssRequests3().isActive()) &&  (cmLoginForm.getCssRequests3().getValue() != null)) {
			this.doControlCssRequests(cmControllerLoginForm.getCssRequests3().getCssRequestObj(), cmLoginForm.getCssRequests3().getValue());
		}
		

	}

	
}
