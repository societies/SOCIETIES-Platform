/**
 * 
 */
package org.societies.userguiserver.handler;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.internal.schema.usergui.ComponentParameters;
import org.societies.api.internal.schema.usergui.UserGuiBean;
import org.societies.api.internal.schema.usergui.UserGuiBeanResult;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectoryRemote;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.cis.directory.client.CisDirectoryRemoteClient;

/**
 * @author mmanniox
 *
 */
public class CisManagerHandler {
	
	ICisManager  cisManager;
	
	
	ICisDirectoryRemote cisDirectoryRemote;
	
	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public ICisDirectoryRemote getCisDirectoryRemote() {
		return cisDirectoryRemote;
	}

	public void setCisDirectoryRemote(ICisDirectoryRemote cisDirectoryRemote) {
		this.cisDirectoryRemote = cisDirectoryRemote;
	}
	public void handleReceiveMessage(Stanza stanza, UserGuiBean messageBean) {
		// TODO Auto-generated method stub
	}

	public UserGuiBeanResult handleGetQuery(Stanza stanza,
			UserGuiBean messageBean) {
		UserGuiBeanResult resultBean = new UserGuiBeanResult();
		
		ComponentParameters commParam = messageBean.getTargetcomponentparameter();
		
		List<ICis> cisList = null;
		List<String> cisIds = null;
		
		switch(commParam.getComponentmethod()) {
			case GET_MY_CIS_LIST:
				cisList = getCisManager().getCisList();
				cisIds = new ArrayList<String>();
				
				for ( int i = 0;  i < cisList.size(); i++)
					cisIds.add(new String(cisList.get(i).getCisId()));
				
				resultBean.setStringList(cisIds);

				
				break;
			case GET_SUGGESTED_CIS_LIST:
				// TODO: The cis manager should do the logic to call the 
				// ico and cisdirectory and generate a proper suggested list!
				
				// For now, just return the list of cis's from the cisdirectory, 
				// minus the cis we are already a member of
				
				CisDirectoryRemoteClient cisDircallback = new CisDirectoryRemoteClient();

				getCisDirectoryRemote().findAllCisAdvertisementRecords(cisDircallback);
				List<CisAdvertisementRecord> adverts = cisDircallback.getResultList();
				
				cisList = getCisManager().getCisList();
				cisIds = new ArrayList<String>();
				
				for ( int i = 0;  i < adverts.size(); i++)
				{
					boolean bMember = false;
					for ( int j = 0; ((j < cisList.size()) && (bMember == false)); j++)
					{
						if (cisList.get(j).getCisId().contains(adverts.get(i).getId()))
							bMember = true;
					}
					if (bMember == false)
						cisIds.add(new String(adverts.get(i).getId()));
				}
				resultBean.setStringList(cisIds);

				
				break;	
		}

		return resultBean;
	}


}
