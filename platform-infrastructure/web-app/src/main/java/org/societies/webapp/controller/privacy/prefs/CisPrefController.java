package org.societies.webapp.controller.privacy.prefs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.bean.ManagedProperty;




import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.controller.CISController;
import org.societies.webapp.models.CisInfo;

@ViewScoped
@ManagedBean(name="cisPrefControlller")
public class CisPrefController extends BasePageController{
	
	
	private ICommunityPreferenceManager communityPreferenceManager;
	
	@ManagedProperty(value="#{cismanager}")
	private CISController cisController;
	//CIS MANAGER

	
	private List<CisInfo> allCIS;
	
	public CisPrefController() {
		
	}

	@PostConstruct
	public void init() {
		allCIS = new ArrayList<CisInfo>();
		
		List<CisInfo> ownedCis = cisController.getownedcommunities();
		List<CisInfo> memberCis = cisController.getmembercommunities();
		
		allCIS.addAll(ownedCis);
		allCIS.addAll(memberCis);
	}

	public List<CisInfo> getAllCIS() {
		return this.allCIS;
	}

	/*public List<IPreferenceTreeModel> getAllPref() {
		List<IPreferenceTreeModel> list = communityPreferenceManager.getAllCommunityPreferences(arg0);
		list.get(0).g
	}*/
	
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
	


}
