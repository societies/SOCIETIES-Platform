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
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.Vector;



import android.os.Parcel;
import android.os.Parcelable;



/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class APreference implements Parcelable {


	/**
     * An enumeration that is always empty. This is used when an enumeration
     * of a leaf node's children is requested.
     */
    static public final Enumeration<APreference> EMPTY_ENUMERATION
	= new Enumeration<APreference>() {
	    public boolean hasMoreElements() { return false; }
	    public APreference nextElement() {
		throw new NoSuchElementException("No more elements");
	    }
    };

    /** this node's parent, or null if this node has no parent */
    protected APreference   parent;

    /** array of children, may be null if this node has no children */
    protected Vector<APreference> children;

    /** optional user object */
    transient protected Object	userObject;

    /** true if the node is able to have children */
    protected boolean		allowsChildren;

	public APreference(){
		this(null, true);
	}
	
	public APreference(AContextPreferenceCondition pc){
		this(pc, true);
	}
	
	public APreference(APreferenceOutcome po){
		this(po,false);
		
	}

	private APreference(Object obj, boolean allowsCh){
		parent = null;
		this.allowsChildren = allowsCh;
		this.userObject = obj;
	}
	public APreferenceOutcome getOutcome() {
		Object obj = this.userObject;
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
		this.readChildren((APreference[]) in.readParcelableArray(APreference.class.getClassLoader()));
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
	
	private void readChildren(APreference[] readParcelableArray) {
		for (APreference pr : readParcelableArray){
			this.children.add((APreference) pr);
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
		
		if (this.userObject instanceof AContextPreferenceCondition){
			return (AContextPreferenceCondition) this.userObject;
		}
		return null;
	}

	public boolean isBranch() {
		
		if (this.userObject==null){
			return true;
		}
		if (this.userObject instanceof AContextPreferenceCondition){
			return true;
		}	
		return false;
	}
	
	public boolean isLeaf(){
		
		if (this.userObject instanceof AContextPreferenceCondition){
			return false;
		}	
		if (this.userObject==null){
			return false;
		}
		return true;	
	}
	
	
	public Object[] getUserObjectPath(){
		APreference[]          realPath = getPath();
		Object[]            retPath = new Object[realPath.length];

		for(int counter = 0; counter < realPath.length; counter++)
		    retPath[counter] = ((APreference)realPath[counter])
			               .getUserObject();
		return retPath;
	}



	public Object getUserObject(){
		return this.userObject;
	}
	
    public APreference[] getPath() {
	return getPathToRoot(this, 0);
    }
    
    protected APreference[] getPathToRoot(APreference aNode, int depth) {
    	APreference[]              retNodes;

	/* Check for null, in case someone passed in a null node, or
	   they passed in an element that isn't rooted at root. */
	if(aNode == null) {
	    if(depth == 0)
		return null;
	    else
		retNodes = new APreference[depth];
	}
	else {
	    depth++;
	    retNodes = getPathToRoot(aNode.getParent(), depth);
	    retNodes[retNodes.length - depth] = aNode;
	}
	return retNodes;
    }
    
	
    public void add(APreference newChild) {
	if(newChild != null && newChild.getParent() == this)
	    insert(newChild, getChildCount() - 1);
	else
	    insert(newChild, getChildCount());
    }
    
    public void insert(APreference newChild, int childIndex) {
	if (!allowsChildren) {
	    throw new IllegalStateException("node does not allow children");
	} else if (newChild == null) {
	    throw new IllegalArgumentException("new child is null");
	} else if (isNodeAncestor(newChild)) {
	    throw new IllegalArgumentException("new child is an ancestor");
	}

	APreference oldParent = (APreference)newChild.getParent();

	    if (oldParent != null) {
		oldParent.remove(newChild);
	    }
	    newChild.setParent(this);
	    if (children == null) {
		children = new Vector<APreference>();
	    }
	    children.insertElementAt(newChild, childIndex);
    }
    
    public void setParent(APreference newParent) {
	parent = newParent;
    }
    public boolean isNodeAncestor(APreference anotherNode) {
	if (anotherNode == null) {
	    return false;
	}

	APreference ancestor = this;

	do {
	    if (ancestor == anotherNode) {
		return true;
	    }
	} while((ancestor = ancestor.getParent()) != null);

	return false;
    }

    /**
     * Returns the number of children of this node.
     *
     * @return	an int giving the number of children of this node
     */
    public int getChildCount() {
	if (children == null) {
	    return 0;
	} else {
	    return children.size();
	}
    }
    
    public void remove(APreference aChild) {
	if (aChild == null) {
	    throw new IllegalArgumentException("argument is null");
	}

	if (!isNodeChild(aChild)) {
	    throw new IllegalArgumentException("argument is not a child");
	}
	remove(getIndex(aChild));	// linear search
    }
    
    
    public void remove(int childIndex) {
    	APreference child = (APreference)getChildAt(childIndex);
	children.removeElementAt(childIndex);
	child.setParent(null);
    }

    public APreference getChildAt(int index) {
	if (children == null) {
	    throw new ArrayIndexOutOfBoundsException("node has no children");
	}
	return (APreference)children.elementAt(index);
    }

    public int getIndex(APreference aChild) {
	if (aChild == null) {
	    throw new IllegalArgumentException("argument is null");
	}

	if (!isNodeChild(aChild)) {
	    return -1;
	}
	return children.indexOf(aChild);	// linear search
    }
    
    public boolean isNodeChild(APreference aNode) {
	boolean retval;

	if (aNode == null) {
	    retval = false;
	} else {
	    if (getChildCount() == 0) {
		retval = false;
	    } else {
		retval = (aNode.getParent() == this);
	    }
	}

	return retval;
    }

    public Enumeration<APreference> depthFirstEnumeration() {
    	return postorderEnumeration();
    }
    
    
    public Enumeration<APreference> breadthFirstEnumeration() {
	return new BreadthFirstEnumeration(this);
    }
	
    public APreference getRoot() {
    	APreference ancestor = this;
    	APreference previous;

	do {
	    previous = ancestor;
	    ancestor = ancestor.getParent();
	} while (ancestor != null);

	return previous;
    }
	
    public int getLevel() {
	APreference ancestor;
	int levels = 0;

	ancestor = this;
	while((ancestor = ancestor.getParent()) != null){
	    levels++;
	}

	return levels;
    }

	
    public int getDepth() {
	Object	last = null;
	Enumeration<APreference>	enum_ = breadthFirstEnumeration();
	
	while (enum_.hasMoreElements()) {
	    last = enum_.nextElement();
	}
	
	if (last == null) {
	    throw new Error ("nodes should be null");
	}
	
	return ((APreference)last).getLevel() - getLevel();
    }
    public Enumeration<APreference> children() {
	if (children == null) {
	    return EMPTY_ENUMERATION;
	} else {
	    return children.elements();
	}
    }

	
	/**
	 * @see DefaultMutableTreeNode#postorderEnumeration()
	 * @return	an enumeration of IPreference node objects traversed in post-order
	 */
    public Enumeration<APreference> postorderEnumeration() {
	return new PostorderEnumeration(this);
    }

    public Enumeration preorderEnumeration() {
	return new PreorderEnumeration(this);
    }

	
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
    final class BreadthFirstEnumeration implements Enumeration<APreference> {
	protected Queue	queue;
	
	public BreadthFirstEnumeration(APreference rootNode) {
	    super();
	    Vector<APreference> v = new Vector<APreference>(1);
	    v.addElement(rootNode);	// PENDING: don't really need a vector
	    queue = new Queue();
	    queue.enqueue(v.elements());
	}
	
	public boolean hasMoreElements() {
	    return (!queue.isEmpty() &&
		    ((Enumeration)queue.firstObject()).hasMoreElements());
	}
	
	public APreference nextElement() {
	    Enumeration	enumer = (Enumeration)queue.firstObject();
	    APreference	node = (APreference)enumer.nextElement();
	    Enumeration<APreference>	children = node.children();
	
	    if (!enumer.hasMoreElements()) {
		queue.dequeue();
	    }
	    if (children.hasMoreElements()) {
		queue.enqueue(children);
	    }
	    return node;
	}
	
	
	// A simple queue with a linked list data structure.
	final class Queue {
	    QNode head;	// null if empty
	    QNode tail;
	
	    final class QNode {
		public Object	object;
		public QNode	next;	// null if end
		public QNode(Object object, QNode next) {
		    this.object = object;
		    this.next = next;
		}
	    }
	
	    public void enqueue(Object anObject) {
		if (head == null) {
		    head = tail = new QNode(anObject, null);
		} else {
		    tail.next = new QNode(anObject, null);
		    tail = tail.next;
		}
	    }
	
	    public Object dequeue() {
		if (head == null) {
		    throw new NoSuchElementException("No more elements");
		}
	
		Object retval = head.object;
		QNode oldHead = head;
		head = head.next;
		if (head == null) {
		    tail = null;
		} else {
		    oldHead.next = null;
		}
		return retval;
	    }
	
	    public Object firstObject() {
		if (head == null) {
		    throw new NoSuchElementException("No more elements");
		}
	
		return head.object;
	    }
	
	    public boolean isEmpty() {
		return head == null;
	    }
	
	} // End of class Queue
	
	}  // End of class BreadthFirstEnumeration
	final class PreorderEnumeration implements Enumeration<APreference> {
	protected Stack stack;
	
	public PreorderEnumeration(APreference rootNode) {
	    super();
	    Vector<APreference> v = new Vector<APreference>(1);
	    v.addElement(rootNode);	// PENDING: don't really need a vector
	    stack = new Stack();
	    stack.push(v.elements());
	}
	
	public boolean hasMoreElements() {
	    return (!stack.empty() &&
		    ((Enumeration)stack.peek()).hasMoreElements());
	}
	
	public APreference nextElement() {
	    Enumeration	enumer = (Enumeration)stack.peek();
	    APreference	node = (APreference)enumer.nextElement();
	    Enumeration	children = node.children();
	
	    if (!enumer.hasMoreElements()) {
		stack.pop();
	    }
	    if (children.hasMoreElements()) {
		stack.push(children);
	    }
	    return node;
	}
	
	}  // End of class PreorderEnumeration
	final class PostorderEnumeration implements Enumeration<APreference> {
	protected APreference root;
	protected Enumeration<APreference> children;
	protected Enumeration<APreference> subtree;
	
	public PostorderEnumeration(APreference rootNode) {
	    super();
	    root = rootNode;
	    children = root.children();
	    subtree = EMPTY_ENUMERATION;
	}
	
	public boolean hasMoreElements() {
	    return root != null;
	}
	
	public APreference nextElement() {
		APreference retval;
	
	    if (subtree.hasMoreElements()) {
		retval = subtree.nextElement();
	    } else if (children.hasMoreElements()) {
		subtree = new PostorderEnumeration(
				(APreference)children.nextElement());
		retval = subtree.nextElement();
	    } else {
		retval = root;
		root = null;
	    }
	
	    return retval;
	}
	
	}  // End of class PostorderEnumeration


}