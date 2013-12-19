package org.societies.webapp.controller.privacy.prefs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedProperty;








import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.controller.CISController;
import org.societies.webapp.models.CisInfo;

@ViewScoped
@ManagedBean(name="cisPrefControlller")
public class CisPrefController extends BasePageController{
	
	@ManagedProperty(value="#{communityPreferenceManager}")
	private ICommunityPreferenceManager communityPreferenceManager;
	
	@ManagedProperty(value="#{cismanager}")
	private CISController cisController;
	
	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commManager;

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private List<CisInfo> allCIS;
	
	public CisPrefController() {
		
	}

	@PostConstruct
	public void init() {
		log.debug("In init!");
		allCIS = new ArrayList<CisInfo>();
		
		List<CisInfo> ownedCis = cisController.getownedcommunities();
		List<CisInfo> memberCis = cisController.getmembercommunities();
		
		allCIS.addAll(ownedCis);
		allCIS.addAll(memberCis);
		
	}

	public List<IPreferenceTreeModel> getAllCISPref() {
		List<IPreferenceTreeModel> allPref = new ArrayList<IPreferenceTreeModel>();
		for(CisInfo cis : this.allCIS)
		{
			allPref.addAll(getAllPref(cis));
		}
		return allPref;
	}

	public List<IPreferenceTreeModel> getAllPref(CisInfo cis) {
		String cisID = cis.getCisid();
		log.debug("Getting all preferences for CIS: " + cisID);
		List<IPreferenceTreeModel> prefList = new ArrayList<IPreferenceTreeModel>();
		try {
			prefList = communityPreferenceManager.getAllCommunityPreferences(commManager.getIdManager().fromJid(cisID));
			log.debug("Got all preferences for cis:" + cisID);
			log.debug("Size of list is: " + prefList.size());
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("Error getting preferences! Could not change cisid to iddentity");
		}
		return prefList;
		//list.get(0).getPreferenceDetails().
	}
	
	public ICommunityPreferenceManager getCommunityPreferenceManager() {
		return communityPreferenceManager;
	}

	public void setCommunityPreferenceManager(
			ICommunityPreferenceManager communityPreferenceManager) {
		this.communityPreferenceManager = communityPreferenceManager;
	}

	public CISController getCisController() {
		return cisController;
	}

	public void setCisController(CISController cisController) {
		this.cisController = cisController;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

}
