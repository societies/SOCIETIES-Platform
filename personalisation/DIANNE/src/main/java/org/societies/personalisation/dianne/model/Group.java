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

package org.societies.personalisation.dianne.model;

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
