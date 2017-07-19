package org.fog.placement.flow;

import org.fog.placement.flow.*;
import org.fog.utils.*;
import org.fog.utils.Bag;
import org.fog.utils.Config;        // DEFAULT_FLOW_CAPACITY
import org.fog.utils.Queue;
import org.fog.entities.*;
import org.fog.entities.Tuple;
import org.fog.network.Link;
import java.util.List;
import java.util.Iterator;

/**
 * A class to analyze min cut / max flows on Fog networks 
 * @class   FlowPlacement 
 * @author  William McCarty
 * @email   wpm0002@auburn.edu
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
     * Estimate the number of waiting jobs at the northern node
     *  This is a very rough estimation that shouldn't be used for anything 
     * @param  link the link we want to estimate  
     * @return integer value for the number of waiting jobs.
     */
    private int estimateOccupancyNorth(Link link)
    {
     return link.getNorthTupleQueue().size();
    }

    /**
     * Estimate the number of waiting jobs at the southern node
     *  This is a very rough estimation that shouldn't be used for anything 
     * @param  link the link we want to estimate  
     * @return integer value for the number of waiting jobs.
     */
    private int estimateOccupancySouth(Link link)
    {
     return link.getSouthTupleQueue().size();
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