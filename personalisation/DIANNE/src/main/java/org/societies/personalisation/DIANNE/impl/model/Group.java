package org.societies.personalisation.DIANNE.impl.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Group 
{
    public String groupName;
    public ArrayList<Node> groupNodes;
    public Node activeNode;

    public Group(String groupName)
    {
        this.groupName = groupName;
        groupNodes = new ArrayList<Node>();
        activeNode = null;
    }

    public String getGroupName(){
        return groupName;
    }
    
    public ArrayList<Node> getGroupNodes(){
        return groupNodes;
    }
    
    public Node getActiveNode(){
        return activeNode;
    }

    public void addNode(Node node){
        groupNodes.add(node);
        activateNode(node);
    }

    public Node getNode(String nodeName)
    {
        Node requestedNode = null;

        Iterator<Node> groupNodes_it = groupNodes.iterator();
        while(groupNodes_it.hasNext())
        {
            Node node = (Node)groupNodes_it.next();
            if(node.getNodeName().equals(nodeName))
            {
                requestedNode = node;
                break;
            }
        }
        return requestedNode;
    }

    //activate node and deactivate all others in group
    public void activateNode(Node node)
    {
        activeNode = node;
        Iterator<Node> groupNodes_it = groupNodes.iterator();
        while(groupNodes_it.hasNext())
        {
            Node nextNode = (Node)groupNodes_it.next();
            String nextNodeName = nextNode.getNodeName();
            String nodeName = node.getNodeName();
            if(nextNodeName.equals(nodeName)) //node to activate
            {
                nextNode.activate();
            }else{  //not node to activate (so deactivate)
                nextNode.deactivate();
            }
        }
    }
}
