package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.ConnectingNodes;


/**
 * @author fran_ko
 *
 */
public class UndirectedEdge implements ConnectingNodes {
	
	private Node border1;
	private Node border2;
	
	public UndirectedEdge(Node source, Node target)
	{
		border1 = source;
		border2 = target;
	}

	public String toString()
	{
		return border1 +" ---- "+ border2 + "\n";
	}

	/* (non-Javadoc)
	 * @see structures.ConnectingNodes#getBorder1()
	 */
	public Node getBorder1() {
		return border1;
	}

	/* (non-Javadoc)
	 * @see structures.ConnectingNodes#getBorder2()
	 */
	public Node getBorder2() {
		return border2;
	}
	
	/**
	 * input: 2 Nodes
	 * tests if this edge has the same borders
	 * 
	 * returns 	0 if not
	 * 			1 if same source and same target
	 * 		   -1 if source and target are switched
	 */
	public int hasBorders(Node s, Node t){
		if (!s.equals(this.border1)){
			if (!s.equals(this.border2)) return 0;
			else if (t.equals(border1)) return -1;
		}
		else if (t.equals(border2)) return 1;
		
		return 0;
		//
	}
	
	public boolean equals(Object ue){

		if (ue instanceof UndirectedEdge) return (hasBorders(((UndirectedEdge)ue).border1,((UndirectedEdge)ue).border2)!=0);
		else return false;		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		return toString().compareTo(((UndirectedEdge)arg0).toString());
	}
}
