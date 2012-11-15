package org.societies.webapp.service;

import java.util.Collection;

import org.societies.api.cis.management.ICis;

public class CisManagerUtils {

	public static boolean isJidOnCommunityCollection(Collection<ICis> listOfIcis, String jid) {
		for (ICis temp : listOfIcis) {
			if(temp.getCisId().equalsIgnoreCase(jid))
				return true;
		}
		return false;
	  }

}
