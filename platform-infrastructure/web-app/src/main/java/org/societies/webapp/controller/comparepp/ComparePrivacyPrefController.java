package org.societies.webapp.controller.comparepp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyAgreementManager;
import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyManager;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.services.IServices;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyOutcomeConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;


@ViewScoped
@ManagedBean(name="ComparePPController")
public class ComparePrivacyPrefController {

	private static Logger log = LoggerFactory.getLogger(ComparePrivacyPrefController.class);

	@ManagedProperty(value = "#{commMngrRef}")
	private ICommManager commManager;
	@ManagedProperty(value = "#{cisManager}")
	private ICisManager cisManager;
	@ManagedProperty(value = "#{privacyAgreementManager}")
	private IPrivacyAgreementManager privacyAgreementManager;
	@ManagedProperty(value = "#{privacyPolicyManager}")
	private IPrivacyPolicyManager privacyPolicyManager;
	@ManagedProperty(value = "#{serviceDiscovery}")
	private IServiceDiscovery serviceDiscovery;
	@ManagedProperty(value = "#{IServices}")
	private IServices serviceMgmt;
	@ManagedProperty(value = "#{ctxBroker}")
	private ICtxBroker ctxBroker;
	@ManagedProperty(value = "#{privPrefMgr}")
	private IPrivacyPreferenceManager privacyPrefMgr;


	HashMap<ICis, HashMap<RequestItem, RequestItem>> changedCISPP;
	HashMap<Service, HashMap<RequestItem, RequestItem>> changedServicePP;
	List<AccessControlPreferenceTreeModel> changedAccCtrl;

	public ComparePrivacyPrefController() {
		changedCISPP = new HashMap<ICis, HashMap<RequestItem, RequestItem>> ();
		changedServicePP = new HashMap<Service, HashMap<RequestItem, RequestItem>> ();
		changedAccCtrl = new ArrayList<AccessControlPreferenceTreeModel>();
	}

	@PostConstruct
	public void initController() {
		compareCISPrivacyPolicies();
		//compareServicePrivacyPolicies();
		compareAccessControl();

		/*	try {
			//ctxBroker.lookup(CtxModelType.ATTRIBUTE,
			//CtxEntity css = ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT);
			//css.g
			IndividualCtxEntity entitys = this.ctxBroker.retrieveIndividualEntity(this.commManager.getIdManager().getThisNetworkNode()).get();
			Set<CtxAttribute> attributes = entitys.getAttributes();
			log.debug("atts: " + attributes.toString());
			Iterator it = attributes.iterator();
			while(it.hasNext()) {
				CtxAttribute att = (CtxAttribute) it.next();
				if(att.getType().startsWith(CtxAttributeTypes.PRIVACY_POLICY_AGREEMENT)) {
					org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope a = (AgreementEnvelope) SerialisationHelper.deserialise(att.getBinaryValue(), this.getClass().getClassLoader());
					if(a.getAgreement().getRequestor() instanceof RequestorCisBean) {
						log.debug("This attribute is an instance of a cis requestorBean");
					}
					else if(a.getAgreement().getRequestor() instanceof RequestorServiceBean) {
						log.debug("This is an isntance of a service requestor bean");
						RequestorServiceBean s = (RequestorServiceBean) a.getAgreement().getRequestor();
						log.debug(s.getRequestorId() +" " + s.getRequestorServiceId());
					}
				}
			}
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
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	public void compareAccessControl() {
		List<AccessControlPreferenceDetailsBean> accessControlList = this.privacyPrefMgr.getAccCtrlPreferenceDetails();
		for(AccessControlPreferenceDetailsBean bean : accessControlList) {
			AccessControlPreferenceTreeModel model = this.privacyPrefMgr.getAccCtrlPreference(bean);
			if(null!=model) {
				IPrivacyPreference p = model.getRootPreference();
				if(p.getOutcome()!=null) {
					log.debug(p.getOutcome().getOutcomeType().toString());
				}
				Enumeration d = p.depthFirstEnumeration();
				while(d.hasMoreElements()) {
					PrivacyPreference nxt = (PrivacyPreference) d.nextElement();

					if(nxt.getOutcome() instanceof AccessControlOutcome) {
						log.debug("Outcome is of AccessControlOutome");
						AccessControlOutcome outcome = (AccessControlOutcome) nxt.getOutcome();

						log.debug("Outcome effect is: " + outcome.getEffect());
						if(outcome.getEffect().equals(PrivacyOutcomeConstants.BLOCK.toString())) {
							changedAccCtrl.add(model);			
							log.debug("User has denied access! Adding to list!");
							log.debug(bean.getResource().toString());
							
							break;
						} 
					}
					if (nxt.getOutcome() instanceof DObfOutcome) {
						log.debug("Access is of DObfOutcome");
						DObfOutcome outcome = (DObfOutcome) nxt.getOutcome();
						if(outcome.getObfuscationLevel()!=1.0) {
							changedAccCtrl.add(model);
							log.debug("Adding to list - obfsucation level is not at default!");
							break;
						}

					}
				}
			} else {
				log.debug("Model is null!");
			}

		}
	}

	public void compareServicePrivacyPolicies() {
		List<Service> services = new ArrayList<Service>();
		try {
			services = serviceDiscovery.getLocalServices().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Service service : services) {
			RequestorServiceBean bean = new RequestorServiceBean();
			bean.setRequestorServiceId(service.getServiceIdentifier());
			bean.setRequestorId(commManager.getIdManager().getThisNetworkNode().getBareJid());
			try {
				RequestPolicy p = this.privacyPolicyManager.getPrivacyPolicy(bean);
				if(p!=null) {
					RequestorService r = new RequestorService(this.commManager.getIdManager().getThisNetworkNode(), service.getServiceIdentifier());
					org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope a = this.privacyAgreementManager.getAgreement(r);
					if(a!=null) {
						log.debug("Policy Agree for sservice is not null!");
					} else {
						log.debug("Agreement is null!");
					}
					HashMap<RequestItem, RequestItem> changedArticles = Comparator.comparePolicyToAgreement(p, a);
					if(changedArticles.size()==0) {
						log.debug("The user did not change the policy!");
					} else {
						changedServicePP.put(service, changedArticles);
						log.debug("The user has changed the policy!");
					}
				}
			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void compareCISPrivacyPolicies() {
		List<ICis> remoteCis = this.cisManager.getRemoteCis();
		for(ICis cis : remoteCis) {
			RequestorCisBean bean = new RequestorCisBean();
			bean.setCisRequestorId(cis.getCisId());
			bean.setRequestorId(cis.getOwnerId());
			try {
				RequestPolicy p = this.privacyPolicyManager.getPrivacyPolicy(bean);
				RequestorCis c = null;
				try {
					c = new RequestorCis(this.commManager.getIdManager().getThisNetworkNode(), this.commManager.getIdManager().fromJid(cis.getCisId()));
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.AgreementEnvelope a = this.privacyAgreementManager.getAgreement(c);
				HashMap<RequestItem, RequestItem> changedArticles = Comparator.comparePolicyToAgreement(p, a);
				if(changedArticles.size()==0) {
					log.debug("The user did not change the policy!");
				} else {
					changedCISPP.put(cis, changedArticles);
					log.debug("The user has changed the policy!");
				}

			} catch (PrivacyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public List<ICis> getChangedCISPPN() {
		List<ICis> cis = new ArrayList<ICis>() {{ addAll(changedCISPP.keySet()); }};
		return cis;
	}

	public List<RequestItem> getPolicyItems(ICis cis) {
		HashMap<RequestItem, RequestItem> policyAgreementMap = changedCISPP.get(cis);
		List<RequestItem> policyItems = new ArrayList<RequestItem>();
		policyItems.addAll(policyAgreementMap.keySet());
		return policyItems;
	}

	public RequestItem getAgreementItem(ICis cis, RequestItem requestItem) {
		HashMap<RequestItem, RequestItem> policyAgreementMap = changedCISPP.get(cis);
		return policyAgreementMap.get(requestItem);
	}

	public List<Service> getChangedServicePPN() {
		List<Service> services = new ArrayList<Service>() {{ addAll(changedServicePP.keySet()); }};
		return services;
	}

	public List<RequestItem> getServicePolicyItems(Service service) {
		HashMap<RequestItem, RequestItem> policyAgreementMap = changedServicePP.get(service);
		List<RequestItem> policyItems = new ArrayList<RequestItem>();
		policyItems.addAll(policyAgreementMap.keySet());
		return policyItems;
	}

	public RequestItem getServiceAgreementItem(Service service, RequestItem requestItem) {
		HashMap<RequestItem, RequestItem> policyAgreementMap = changedServicePP.get(service);
		return policyAgreementMap.get(requestItem);
	}


	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public IPrivacyAgreementManager getPrivacyAgreementManager() {
		return privacyAgreementManager;
	}

	public void setPrivacyAgreementManager(
			IPrivacyAgreementManager privacyAgreementManager) {
		this.privacyAgreementManager = privacyAgreementManager;
	}

	public IPrivacyPolicyManager getPrivacyPolicyManager() {
		return privacyPolicyManager;
	}

	public void setPrivacyPolicyManager(IPrivacyPolicyManager privacyPolicyManager) {
		this.privacyPolicyManager = privacyPolicyManager;
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	public IServices getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServices serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}

	public IPrivacyPreferenceManager getPrivacyPrefMgr() {
		return privacyPrefMgr;
	}

	public void setPrivacyPrefMgr(IPrivacyPreferenceManager privacyPrefMgr) {
		this.privacyPrefMgr = privacyPrefMgr;
	}





}
