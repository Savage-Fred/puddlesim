package org.fog.placement.flow;

import org.fog.placement.flow.*;
import org.fog.utils.*;
import org.fog.utils.Bag;
import org.fog.utils.Config;        // DEFAULT_FLOW_CAPACITY
import org.fog.entities.*;
import org.fog.network.Link;
import java.util.List;
import java.util.Iterator;

/**
 *
 *
 *
 *
 */
public class FlowPlacement {

    private int numberOfNodes;



    /**
     * @param LinkIDList a list of the link ID's 
     * @param numberOfNodes - the number of nodes in the graph 
     */
    FlowPlacement(List<Link> LinkList, int numOfNodes)
    {
        setNumberOfNodes(numOfNodes);
        FlowNetwork flowNet = new FlowNetwork(numberOfNodes);

        for (Link temp : LinkList)
        {
            FlowEdge edge = new FlowEdge(temp.getEndpointSouth(), temp.getEndpointNorth(), temp.getBandwidth());
            flowNet.addEdge(edge);
        }
    }

    /**
     * 
     * @param n number of edges 
     */
    private void setNumberOfNodes(int n)
    {
        this.numberOfNodes = n;
    }

}