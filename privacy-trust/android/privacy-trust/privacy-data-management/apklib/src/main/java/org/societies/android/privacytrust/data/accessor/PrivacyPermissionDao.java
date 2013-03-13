package org.societies.android.privacytrust.data.accessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.societies.android.api.identity.util.DataIdentifierUtils;
import org.societies.android.api.identity.util.RequestorUtils;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.ResourceUtils;
import org.societies.android.api.privacytrust.privacy.util.privacypolicy.ActionUtils;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPermission;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.RequestorBean;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class PrivacyPermissionDao {
	private final static String TAG = PrivacyPermissionDao.class.getSimpleName();
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


	public PrivacyPermissionDao(SQLiteDatabase db) {
		this.db = db;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		defaultValidityDuration = 24*60*60; // 1 day
	}


	public int addPrivacyPermission(RequestorBean requestor, ResponseItem permission) throws PrivacyException {
		ContentValues values = privacyPermissionToContentValues(requestor, permission);
		int id = (int) db.insert(Constants.TABLE_PRIVACY_PERMISSION, null, values);
		if (-1 == id) {
			throw new PrivacyException("Error during storage of privacy permission");
		}
		return id;
	}

	public int delAllPrivacyPermission() throws PrivacyException {
		db.delete(Constants.TABLE_PRIVACY_PERMISSION, null, null);
		try {
			db.execSQL("REINDEX "+Constants.TABLE_PRIVACY_PERMISSION);
		}
		catch(SQLException e) {
			throw new PrivacyException("Error during delete of privacy permission");
		}
		return 1;
	}


	public ResponseItem findPermissionById(int id) {
		Cursor c = db.query(Constants.TABLE_PRIVACY_PERMISSION,
				Constants.TABLE_PRIVACY_PERMISSION_FIELDS,
				" rowid=? ",
				new String[] {id+""}, null, null, null);
		ResponseItem permission = cursorToPermission(c);
		c.close();
		return permission;
	}


	public ResponseItem findPermission(RequestorBean requestor, DataIdentifier dataId, List<Action> actions) {
		Cursor c = db.query(Constants.TABLE_PRIVACY_PERMISSION,
				Constants.TABLE_PRIVACY_PERMISSION_FIELDS,
				" requestor=? ",
				new String[] {RequestorUtils.toFormattedString(requestor), DataIdentifierUtils.toUriString(dataId), ActionUtils.toFormattedString(actions)}, null, null, null);
		ResponseItem permission = cursorToPermission(c);
		c.close();
		return permission;
	}


	public List<ResponseItem> findPermissions() {
		Cursor c = db.query(Constants.TABLE_PRIVACY_PERMISSION,
				Constants.TABLE_PRIVACY_PERMISSION_FIELDS,
				null,
				null, null, null, null);
		List<ResponseItem> permissionList = cursorToPermissionList(c);
		c.close();
		return permissionList;
	}

	public ContentValues privacyPermissionToContentValues(RequestorBean requestor, ResponseItem permission) {
		if (null == permission) {
			return null;
		}
		ContentValues values = new ContentValues();
		values.put("requestor", RequestorUtils.toFormattedString(requestor));
		values.put("data_id_uri", ResourceUtils.getDataIdUri(permission.getRequestItem().getResource()));
		values.put("actions", ActionUtils.toFormattedString(permission.getRequestItem().getActions()));
		values.put("decision", permission.getDecision().name());
		values.put("conditions", "");//permission.getRequestItem().getConditions());
		if (permission instanceof PrivacyPermission) {
			PrivacyPermission permission2 = (PrivacyPermission) permission;
			values.put("obfuscation_level", permission2.getObfuscationLevel());
			values.put("creation_date", permission2.getCreationDate().toString());
			values.put("validity_duration", permission2.getValidityDuration());
		}
		else {
			values.put("obfuscation_level", -1);
			values.put("creation_date", dateFormat.format(new Date()));
			values.put("validity_duration", defaultValidityDuration);
		}
		return values;
	}


	public List<ResponseItem> cursorToPermissionList(Cursor c) {
		if (null == c || c.getCount() == 0) {
			return null;
		}
		List<ResponseItem> permissionList = new ArrayList<ResponseItem>();
		int length = c.getCount();
		for(int i=0; i<length; i++) {
			c.moveToPosition(i);
			PrivacyPermission permission = new PrivacyPermission();
			//			permission.setId(c.getInt(c.getColumnIndex("rowid")));
			// - Requestor
			permission.setRequestor(RequestorUtils.fromFormattedString(c.getString(c.getColumnIndex("requestor"))));
			// - Decision
			permission.setDecision(Decision.fromValue(c.getString(c.getColumnIndex("decision"))));
			// - Request Item
			RequestItem requestItem = new RequestItem();
			// Resource
			Resource resource = new Resource();
			resource.setDataIdUri(c.getString(c.getColumnIndex("data_id_uri")));
			requestItem.setResource(resource);
			// Actions
			requestItem.setActions(ActionUtils.fromFormattedString(c.getString(c.getColumnIndex("actions"))));
			// Conditions
			//			requestItem.setConditions(getConditionsFromString(c.getString(c.getColumnIndex("conditions"))));
			permission.setRequestItem(requestItem);
			// - Obfuscation level
			permission.setObfuscationLevel(c.getDouble(c.getColumnIndex("obfuscation_level")));
			// - Creation data
			String dateString = c.getString(c.getColumnIndex("creation_date"));
			try {
				permission.setCreationDate(dateFormat.parse(dateString));
			} catch (ParseException e) {
				permission.setCreationDate(new Date());
			}
			// - Validity duration
			permission.setValidityDuration(c.getLong(c.getColumnIndex("validity_duration")));
			permissionList.add(permission);
		}
		return permissionList;
	}


	public ResponseItem cursorToPermission(Cursor c) {
		if (null == c || c.getCount() == 0) {
			return null;
		}
		return cursorToPermissionList(c).get(0);
	}

	/**
	 * Getter of the property <tt>db</tt>
	 * @return  Returns the db.
	 * @uml.property  name="db"
	 */
	public SQLiteDatabase getDb() {
		return db;
	}

	/**
	 * Setter of the property <tt>db</tt>
	 * @param db  The db to set.
	 * @uml.property  name="db"
	 */
	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}
}
