package org.societies.api.android.internal.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.societies.api.internal.css.management.CSSNode;
import org.societies.api.internal.css.management.CSSRecord;
import org.societies.api.schema.cssmanagement.CssNode;
import org.societies.api.schema.cssmanagement.CssRecord;

import android.content.ContentValues;
import android.database.Cursor;

public class CssUtils {
	
	public static ContentValues convertFromCssNode(CSSNode node){
		ContentValues data = new ContentValues();
		data.put(CssNodeTable.KEY_IDENTITY,  node.getIdentity());
		data.put(CssNodeTable.KEY_STATUS,    node.getStatus());
		data.put(CssNodeTable.KEY_TYPE,      node.getType());
		return data;
		
	}
	
	
	
	public static ContentValues convertFromCssNode(CssRecord profile){
		ContentValues data = new ContentValues();
		data.put(CssProfileTable.KEY_CSS_IDENTITY,  profile.getCssIdentity());
		data.put(CssProfileTable.KEY_CSS_INACTIV,   profile.getCssInactivation());
		data.put(CssProfileTable.KEY_CSS_NODES,        nodes2String(profile.getCssNodes()));
		data.put(CssProfileTable.KEY_CSS_NODES_ARCH,   nodes2String(profile.getArchiveCSSNodes()));
		data.put(CssProfileTable.KEY_CSS_REGISTR,   profile.getCssRegistration());
		data.put(CssProfileTable.KEY_EMAIL_ID,   profile.getEmailID());
		data.put(CssProfileTable.KEY_ENTITY,   profile.getEntity());
		data.put(CssProfileTable.KEY_ENTITY_NAME,   profile.getIdentityName());
		data.put(CssProfileTable.KEY_FORE_NAME,   profile.getForeName());
		data.put(CssProfileTable.KEY_HOMELOCATION, profile.getHomeLocation());
		data.put(CssProfileTable.KEY_UPTIME,   profile.getCssUpTime());
		data.put(CssProfileTable.KEY_IM_ID,   profile.getImID());
		data.put(CssProfileTable.KEY_PASSWORD,   profile.getPassword());
		data.put(CssProfileTable.KEY_SOCIAL_URI,   profile.getSocialURI());
		data.put(CssProfileTable.KEY_STATUS,   profile.getStatus());
		data.put(CssProfileTable.KEY_SEX,   profile.getSex());
		data.put(CssProfileTable.KEY_PRESENCE,   profile.getPresence());
		
		return data;
		
	}
	
	
	
	
	
	/**
	 * Transform generic Cursor Result in a list of CSSNode
	 * @param c
	 * @return
	 */
	public static List<CSSNode> cursor2Node(Cursor c){
		List<CSSNode> list = new ArrayList<CSSNode>();
		try{
			if (c==null) return null;
			if (c.getCount()==0) return null;
			c.moveToFirst();
			do{
				CSSNode node = new CSSNode();
				
				node.setIdentity(c.getString(c.getColumnIndex(CssNodeTable.KEY_IDENTITY)));
				node.setStatus(c.getInt(c.getColumnIndex(CssNodeTable.KEY_STATUS)));
				node.setType(c.getInt(c.getColumnIndex(CssNodeTable.KEY_TYPE)));
				list.add(node);
			}while (c.moveToNext());
			
			
			
			return list;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Transform generic Cursor Result in a list of CSSNode
	 * @param c
	 * @return
	 */
	public static List<CSSRecord> cursor2Profile(Cursor c, Map<String, CSSNode> nodes){
		List<CSSRecord> list = new ArrayList<CSSRecord>();
		try{
			if (c==null) return null;
			if (c.getCount()==0) return null;
			c.moveToFirst();
			do{
				CSSRecord profile = new CSSRecord();
				
				profile.setCssIdentity(c.getString(c.getColumnIndex(CssProfileTable.KEY_CSS_IDENTITY)));
				profile.setCssInactivation(c.getString(c.getColumnIndex(CssProfileTable.KEY_CSS_INACTIV)));
				profile.setCssRegistration(c.getString(c.getColumnIndex(CssProfileTable.KEY_CSS_REGISTR)));
				profile.setCssUpTime(c.getInt(c.getColumnIndex(CssProfileTable.KEY_UPTIME)));
				profile.setEmailID(c.getString(c.getColumnIndex(CssProfileTable.KEY_EMAIL_ID)));
				profile.setEntity(c.getInt(c.getColumnIndex(CssProfileTable.KEY_ENTITY)));
				profile.setForeName(c.getString(c.getColumnIndex(CssProfileTable.KEY_FORE_NAME)));
				profile.setHomeLocation(c.getString(c.getColumnIndex(CssProfileTable.KEY_HOMELOCATION)));
				profile.setIdentityName(c.getString(c.getColumnIndex(CssProfileTable.KEY_IDENTITY_NAME)));
				profile.setImID(c.getString(c.getColumnIndex(CssProfileTable.KEY_IM_ID)));
				profile.setName(c.getString(c.getColumnIndex(CssProfileTable.KEY_NAME)));
				profile.setPassword(c.getString(c.getColumnIndex(CssProfileTable.KEY_PASSWORD)));
				profile.setPresence(c.getInt(c.getColumnIndex(CssProfileTable.KEY_PRESENCE)));
				profile.setSex(c.getInt(c.getColumnIndex(CssProfileTable.KEY_SEX)));
				profile.setSocialURI(c.getString(c.getColumnIndex(CssProfileTable.KEY_SOCIAL_URI)));
				profile.setStatus(c.getInt(c.getColumnIndex(CssProfileTable.KEY_STATUS)));
				
				
				
				list.add(profile);
			}while (c.moveToNext());
			
			
			
			return list;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public static CSSNode[] string2Nodes(String concatList, Map<String, CSSNode>nodes){
		try{
			String[] entities = concatList.split(",");
			CSSNode[] cssNodes = new CSSNode[entities.length];
			int index=0;
			for(String entity: entities){
				cssNodes[index++]=nodes.get(entity);
			}
			return cssNodes;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	
	private  static String nodes2String(List<CssNode> list){
		
		String result="";
		Iterator<CssNode> it = list.iterator();
		while(it.hasNext()){
			if (result.length()>0) result += ",";
			result+=it.next().getIdentity();
			
		}
		return result;
	}
	
	
	
	
	
}
