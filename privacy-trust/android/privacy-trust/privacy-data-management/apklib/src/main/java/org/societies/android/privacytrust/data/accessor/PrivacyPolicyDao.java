package org.societies.android.privacytrust.data.accessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.societies.android.api.identity.util.DataIdentifierUtils;
import org.societies.android.api.identity.util.RequestorUtils;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class PrivacyPolicyDao {
	private final static String TAG = PrivacyPolicyDao.class.getName();
	/**
	 * Date base accessor instance
	 */
	private SQLiteDatabase db;
	/**
	 * Date formatter
	 */
	private SimpleDateFormat dateFormat;
	/**
	 * Default number of seconds for validity duration
	 */
	private long defaultValidityDuration;


	public PrivacyPolicyDao(SQLiteDatabase db) {
		this.db = db;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		defaultValidityDuration = 7*24*60*60; // 7 days
	}


	public int updatePrivacyPolicy(RequestPolicy privacyPolicy) throws PrivacyException {
		if (null == privacyPolicy) {
			throw new PrivacyException("Can't store an empty privacy policy");
		}
		ContentValues values = privacyPolicyToContentValues(privacyPolicy);
		RequestorBean requestor = privacyPolicy.getRequestor();
		RequestPolicy existingPrivacyPolicy = findPrivacyPolicy(requestor);
		int id = 0;
		// Insert
		if (null == existingPrivacyPolicy) {
			id = (int) db.insert(Constants.TABLE_PRIVACY_POLICY, null, values);
			if (-1 == id) {
				throw new PrivacyException("Error during storage of privacy policy");
			}
		}
		// Update
		else {
			id = (int) db.update(Constants.TABLE_PRIVACY_POLICY, values,
					" "+TablePrivacyPolicyFieldNames.RequestorOwnerId.value()+"=? AND "+TablePrivacyPolicyFieldNames.RequestorThirdId.value()+"=? ",
					new String[] {RequestorUtils.getRequestorOwnerId(requestor), RequestorUtils.getRequestorThirdId(requestor)});
			if (0 == id) {
				throw new PrivacyException("Error during storage of privacy policy");
			}
		}
		return id;
	}

	public boolean deleteAllPrivacyPolicy() throws PrivacyException {
		db.delete(Constants.TABLE_PRIVACY_POLICY, null, null);
		try {
			db.execSQL("REINDEX "+Constants.TABLE_PRIVACY_POLICY);
		}
		catch(SQLException e) {
			throw new PrivacyException("Error during delete of privacy policies");
		}
		return true;
	}

	public boolean deletePrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		db.delete(Constants.TABLE_PRIVACY_POLICY,
				" "+TablePrivacyPolicyFieldNames.RequestorOwnerId.value()+"=? AND "+TablePrivacyPolicyFieldNames.RequestorThirdId.value()+"=? ",
				new String[] {RequestorUtils.getRequestorOwnerId(requestor), RequestorUtils.getRequestorThirdId(requestor)});
		try {
			db.execSQL("REINDEX "+Constants.TABLE_PRIVACY_POLICY);
		}
		catch(SQLException e) {
			throw new PrivacyException("Error during delete of a privacy policy");
		}
		return true;
	}

	public RequestPolicy findPrivacyPolicyById(int id) throws PrivacyException {
		Cursor c = db.query(Constants.TABLE_PRIVACY_POLICY,
				Constants.TABLE_PRIVACY_POLICY_FIELDS,
				" rowid=? ",
				new String[] {id+""}, null, null, null);
		RequestPolicy policy = cursorToPolicy(c);
		c.close();
		return policy;
	}

	public RequestPolicy findPrivacyPolicy(RequestorBean requestor) throws PrivacyException {
		Cursor c = db.query(Constants.TABLE_PRIVACY_POLICY,
				Constants.TABLE_PRIVACY_POLICY_FIELDS,
				" "+TablePrivacyPolicyFieldNames.RequestorOwnerId.value()+"=? AND "+TablePrivacyPolicyFieldNames.RequestorThirdId.value()+"=? ",
				new String[] {RequestorUtils.getRequestorOwnerId(requestor), RequestorUtils.getRequestorThirdId(requestor)},
				null, null, null);
		RequestPolicy policy = cursorToPolicy(c);
		c.close();
		return policy;
	}


	public List<RequestPolicy> findPolicys() throws PrivacyException {
		Cursor c = db.query(Constants.TABLE_PRIVACY_POLICY,
				Constants.TABLE_PRIVACY_POLICY_FIELDS,
				null,
				null, null, null, null);
		List<RequestPolicy> policyList = cursorToPolicyList(c);
		c.close();
		return policyList;
	}

	public ContentValues privacyPolicyToContentValues(RequestPolicy policy) throws PrivacyException {
		if (null == policy || null == policy.getRequestor()) {
			return null;
		}
		ContentValues values = new ContentValues();
		values.put(TablePrivacyPolicyFieldNames.RequestorOwnerId.value(), RequestorUtils.getRequestorOwnerId(policy.getRequestor()));
		values.put(TablePrivacyPolicyFieldNames.RequestorThirdId.value(), RequestorUtils.getRequestorThirdId(policy.getRequestor()));
		values.put(TablePrivacyPolicyFieldNames.RawXmlData.value(), RequestPolicyUtils.toRawXmlString(policy));
		values.put(TablePrivacyPolicyFieldNames.DateCreated.value(), dateFormat.format(new Date()));
		values.put(TablePrivacyPolicyFieldNames.DateModified.value(), dateFormat.format(new Date()));
		return values;
	}


	public List<RequestPolicy> cursorToPolicyList(Cursor c) throws PrivacyException {
		// -- No policy
		if (null == c || c.getCount() == 0) {
			return null;
		}
		// -- Retrieve every privacy policies
		List<RequestPolicy> policyList = new ArrayList<RequestPolicy>();
		int length = c.getCount();
		for(int i=0; i<length; i++) {
			c.moveToPosition(i);
			// Retrieve using XML raw data
			RequestPolicy policy = new RequestPolicy();
			String rawXmlString= c.getString(c.getColumnIndex(TablePrivacyPolicyFieldNames.RawXmlData.value()));
			policy = RequestPolicyUtils.fromRawXmlString(rawXmlString);
			policyList.add(policy);
		}
		return policyList;
	}


	public RequestPolicy cursorToPolicy(Cursor c) throws PrivacyException {
		if (null == c || c.getCount() == 0) {
			return null;
		}
		return cursorToPolicyList(c).get(0);
	}

	/**
	 * Getter of the property db
	 * @return  Returns the db.
	 */
	public SQLiteDatabase getDb() {
		return db;
	}

	/**
	 * Setter of the property db
	 * @param db  The db to set.
	 */
	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}
}
