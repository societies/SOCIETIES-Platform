/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.internal.personalisation.model;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;

import org.societies.android.api.internal.personalisation.model.tree.MutableTreeNode;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class APreference extends DefaultMutableTreeNode implements Parcelable {

	
	public APreference(){
		super();
	}
	
	public APreference(AContextPreferenceCondition pc){
		super(pc);	
	}
	
	public APreference(APreferenceOutcome po){
		super(po,false);
		
	}

	public APreferenceOutcome getOutcome() {
		Object obj = this.getUserObject();
		if (obj instanceof APreferenceOutcome){
			return (APreferenceOutcome) obj;
		}
		return null;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeByte((byte) (allowsChildren ? 1 : 0));   
		
		
		Parcelable[] aChildren = new Parcelable[this.children.size()];
		for (int i =0; i< this.children.size(); i++){
			aChildren[i] = (Parcelable) this.children.get(i);
		}
		
		out.writeParcelableArray(aChildren, flags);
		
		out.writeParcelable((Parcelable) parent, flags);
		
		out.writeParcelable((Parcelable) userObject, flags);
	}
	
	public APreference(Parcel in){
		super();
		this.allowsChildren = in.readByte() == 1; 
		this.readChildren(in.readParcelableArray(MutableTreeNode.class.getClassLoader()));
		this.parent = (APreference) in.readParcelable(APreference.class.getClassLoader());
		if (this.allowsChildren){
			try{
			this.userObject = in.readParcelable(AContextPreferenceCondition.class.getClassLoader());
			} catch(Exception e){
				e.printStackTrace();
			}
		}else{
				this.userObject = in.readParcelable(APreferenceOutcome.class.getClassLoader());
			
		}
	}
	
	private void readChildren(Parcelable[] readParcelableArray) {
		for (Parcelable p : readParcelableArray){
			this.children.add(p);
		}
		
	}
	public static final Parcelable.Creator<APreference> CREATOR = new Parcelable.Creator<APreference>() {

        public APreference createFromParcel(Parcel in) {
            return new APreference(in);
        }

        public APreference[] newArray(int size) {
            return new APreference[size];
        }

    };
	public AContextPreferenceCondition getCondition() {
		Object obj = this.getUserObject();
		if (obj instanceof AContextPreferenceCondition){
			return (AContextPreferenceCondition) obj;
		}
		return null;
	}

	public boolean isBranch() {
		Object obj = this.getUserObject();
		if (obj==null){
			return true;
		}
		if (obj instanceof AContextPreferenceCondition){
			return true;
		}	
		return false;
	}
	
	public boolean isLeaf(){
		Object obj = this.getUserObject();
		if (obj instanceof AContextPreferenceCondition){
			return false;
		}	
		if (obj==null){
			return false;
		}
		return true;	
	}
	
	
	public Object[] getUserObjectPath(){
		return super.getUserObjectPath();
	}



	public void add(APreference p) {
	
		super.add(p);
		
	}

	public void remove(APreference p) {
		super.remove(p);
		
	}	
	
	public Enumeration<APreference> depthFirstEnumeration(){
		return super.depthFirstEnumeration();
	}
	
	public Enumeration<APreference> breadthFirstEnumeration(){
		return super.breadthFirstEnumeration();
	}
	
	public APreference getRoot(){
		return (APreference) super.getRoot();
	}
	
	public int getLevel(){
		return super.getLevel();
	}
	
	public int getDepth(){
		return super.getDepth();
	}
	
	/**
	 * @see DefaultMutableTreeNode#preorderEnumeration()
	 * @return	an enumeration of IPreference node objects traversed in pre-order
	 */
	public Enumeration<APreference> preorderEnumeration(){
		return super.preorderEnumeration();
	}
	
	/**
	 * @see DefaultMutableTreeNode#postorderEnumeration()
	 * @return	an enumeration of IPreference node objects traversed in post-order
	 */
	public Enumeration<APreference> postorderEnumeration(){
		return super.postorderEnumeration();
	}
	
	@Override
	public APreference getParent(){
		return (APreference) parent;
	}
	/*
	public String toString(){
		String str = "";
		if (this.isLeaf()){
			String tab = "\n";
			for (int i = 0; i<this.getLevel(); i++){
				tab = tab.concat("\t");
			}
			return tab.concat(this.getOutcome().toString());
		}else{
			String tab  = "\n";
			if (null!=this.userObject){
				for (int i=0; i<this.getLevel(); i++){
					str = str.concat("\t");
				}
				str = str + this.userObject.toString()+"\n";
			}
			Enumeration<IPreference> e = this.children();
			while (e.hasMoreElements()){
				str = str+e.nextElement().toString()+"\n";
			}
			return str;
		}
	}*/
	
	public String toString(){
		if (this.userObject==null){
			return "root";
		}
		return this.getUserObject().toString();
	}
	
	

	public String toTreeString(){
		String str = "";
		Enumeration<APreference> e = this.preorderEnumeration();
		
		while (e.hasMoreElements()){
			APreference p = e.nextElement();
			for (int i = 0; i<p.getLevel(); i++){
				str = str.concat("\t");
			}
			
			str = str.concat(p.toString()+"\n");
			
		}
		return str;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


}