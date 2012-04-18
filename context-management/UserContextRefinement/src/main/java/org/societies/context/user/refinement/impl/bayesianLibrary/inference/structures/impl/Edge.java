package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;

/**
 * @author fran_ko
 *
 */
public class Edge extends UndirectedEdge{

	/* (non-Javadoc)
	 * @see structures.ConnectingNodes#getSource()
	 */
	protected Node source;
	protected Node target;
	
	
	public Edge(Node source, Node target)
	{
		super(source, target);
		this.source = source;
		this.target = target;
		this.target.addIncoming(this);
		this.source.addOutgoing(this);
	}
	
	public Node getSource() {
		return source;
	}

	/* (non-Javadoc)
	 * @see structures.ConnectingNodes#getTarget()
	 */
	public Node getTarget() {
		return target;
	}
	


	public String toString(){
		return source +" ---> "+ target + "\n";
	}

}
