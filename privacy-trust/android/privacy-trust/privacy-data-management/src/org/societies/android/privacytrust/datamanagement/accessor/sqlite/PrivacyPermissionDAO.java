package org.societies.android.privacytrust.datamanagement.accessor.sqlite;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.privacytrust.model.PrivacyPolicyTypeConstants;
import org.societies.android.privacytrust.datamanagement.PrivacyDataManager;
import org.societies.android.privacytrust.datamanagement.accessor.Constants;
import org.societies.android.privacytrust.datamanagement.accessor.DAOException;
import org.societies.android.privacytrust.datamanagement.accessor.IPrivacyPermissionDAO;
import org.societies.android.privacytrust.model.PrivacyPermission;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Decision;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class PrivacyPermissionDAO implements IPrivacyPermissionDAO {
	private final static String TAG = PrivacyDataManager.class.getSimpleName();

	/**
	 * @uml.property  name="db"
	 */
	private SQLiteDatabase db;


	public PrivacyPermissionDAO(SQLiteDatabase db) {
		this.db = db;
	}


	public int updatePrivacyPermission(PrivacyPermission permission) throws DAOException {
		ContentValues values = permissionToContentValues(permission);
		int id = -1;
		// Creation
		if (-1 == permission.getId()) {
			id = (int) db.insert(Constants.TABLE_PRIVACY_PERMISSION, null, values);
		}
		// Update
		else {
			id = (int) db.update(Constants.TABLE_PRIVACY_PERMISSION,
					values,
					" requestorId=? AND subRequestorId=? AND permissionType=? AND ownerId=? AND dataId=? ",
					new String[] {permission.getRequestorId()+"", permission.getSubRequestorId()+"", permission.getPermissionType().name(), permission.getOwnerId()+"", permission.getDataId()+""});
		}

		if (-1 == id) {
			throw new DAOException(Constants.ADD_PrivacyPermission, "Erreur when creating / updating the privacy permission");
		}
		return id;
	}

	public int deletePrivacyPermission(RequestorBean requestor, String ownerId, String dataId) throws DAOException {
		int id = (int) db.delete(Constants.TABLE_PRIVACY_PERMISSION,
				" requestorId=? AND subRequestorId=? AND permissionType=? AND ownerId=? AND dataId=? ",
				new String[] {requestor.getRequestorId()+""
				, ((requestor instanceof RequestorServiceBean) ? ((RequestorServiceBean)requestor).getRequestorServiceId().getIdentifier().toString() : ((requestor instanceof RequestorCisBean) ?  ((RequestorCisBean)requestor).getCisRequestorId() : ""))+""
				, ((requestor instanceof RequestorServiceBean) ? PrivacyPolicyTypeConstants.SERVICE : ((requestor instanceof RequestorCisBean) ? PrivacyPolicyTypeConstants.CIS : PrivacyPolicyTypeConstants.NOTHING))+""
				, ownerId+""
				, dataId+""});
		if (-1 == id) {
			throw new DAOException(Constants.DELETE_PrivacyPermission, "Erreur when deleting the privacy permission");
		}
		return id;
	}

	public int delAllPrivacyPermission() throws DAOException {
		db.delete(Constants.TABLE_PRIVACY_PERMISSION, null, null);
		try {
			db.execSQL("REINDEX "+Constants.TABLE_PRIVACY_PERMISSION);
		}
		catch(SQLException e) {
			throw new DAOException(Constants.DELETE_All_PrivacyPermission, "Erreur when deleting all privacy permissions", e);
		}
		return 1;
	}

	public PrivacyPermission findPrivacyPermission(RequestorBean requestor, String ownerId, String dataId) {
		Cursor c = null;
		PrivacyPermission permission = null;
		try {
			c = db.query(Constants.TABLE_PRIVACY_PERMISSION,
					new String[] {"rowid", "requestorId", "subRequestorId", "permissionType", "ownerId", "dataId", "actions", "decision"},
					" requestorId=? AND subRequestorId=? AND permissionType=? AND ownerId=? AND dataId=? ",
					new String[] {requestor.getRequestorId()+""
					, ((requestor instanceof RequestorServiceBean) ? ((RequestorServiceBean)requestor).getRequestorServiceId().getIdentifier().toString() : ((requestor instanceof RequestorCisBean) ?  ((RequestorCisBean)requestor).getCisRequestorId() : ""))+""
					, ((requestor instanceof RequestorServiceBean) ? PrivacyPolicyTypeConstants.SERVICE : ((requestor instanceof RequestorCisBean) ? PrivacyPolicyTypeConstants.CIS : PrivacyPolicyTypeConstants.NOTHING))+""
					, ownerId+""
					, dataId+""}, null, null, null);
			permission = cursorToPrivacyPermission(c);
		}
		finally {
			if (null != c) {
				c.close();
			}
		}
		return permission;
	}

	public List<PrivacyPermission> findAllPrivacyPermissions() {
		Cursor c = db.query(Constants.TABLE_PRIVACY_PERMISSION,
				new String[] {"rowid", "requestorId", "serviceId", "subRequestorId", "permissionType", "ownerId", "dataId", "actions", "decision"},
				null,
				null, null, null, null);
		List<PrivacyPermission> permissions = cursorToPrivacyPermissions(c);
		c.close();
		return permissions;
	}



	public ContentValues permissionToContentValues(PrivacyPermission permission) {
		if (null == permission) {
			return null;
		}
		ContentValues values = new ContentValues();
		values.put("requestorId", permission.getRequestorId());
		values.put("subRequestorId", permission.getSubRequestorId());
		values.put("permissionType", permission.getPermissionType().name());
		values.put("ownerId", permission.getOwnerId());
		values.put("dataId", permission.getDataId());
		values.put("actions", permission.getActions());
		values.put("decision", permission.getDecision().name());
		return values;
	}

	public List<PrivacyPermission> cursorToPrivacyPermissions(Cursor c) {
		if (null == c || c.getCount() == 0) {
			return null;
		}
		List<PrivacyPermission> permissions = new ArrayList<PrivacyPermission>();
		int length = c.getCount();
		for(int i=0; i<length; i++) {
			c.moveToPosition(i);
			PrivacyPermission permission = new PrivacyPermission();
			permission.setId(c.getInt(c.getColumnIndex("rowid")));
			permission.setRequestorId(c.getString(c.getColumnIndex("requestorId")));
			permission.setSubRequestorId(c.getString(c.getColumnIndex("subRequestorId")));
			permission.setPermissionType(PrivacyPolicyTypeConstants.valueOf(c.getString(c.getColumnIndex("permissionType"))));
			permission.setOwnerId(c.getString(c.getColumnIndex("ownerId")));
			permission.setDataId(c.getString(c.getColumnIndex("dataId")));
			permission.setActions(c.getString(c.getColumnIndex("actions")));
			permission.setDecision(Decision.fromValue(c.getString(c.getColumnIndex("decision"))));
			permissions.add(permission);
		}
		return permissions;
	}

	public PrivacyPermission cursorToPrivacyPermission(Cursor c) {
		if (null == c || c.getCount() == 0) {
			return null;
		}
		return cursorToPrivacyPermissions(c).get(0);
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
