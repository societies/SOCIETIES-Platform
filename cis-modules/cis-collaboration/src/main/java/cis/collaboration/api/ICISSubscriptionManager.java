/**
 * This interface has the following responsibilities:
 * - Allow for management of CIS Subscription Records
 * 
 * @author Babak Farshchian
 * @version 0
 * 
 */

package cis.collaboration.api;


import cis.collaboration.impl.*;

public interface ICISSubscriptionManager {
	Boolean addSubscriptionRecord(CISSubscriptionRecord record);
	Boolean updateSubscriptionRecord(CISSubscriptionRecord record);
	Boolean deleteSubscriptionRecord(CISSubscriptionRecord record);
	CISSubscriptionRecord getSubscriptionRecord(String cssId, String cisId);
	CISSubscriptionRecord[] getSubscriptionRecords(String cssId);

}
