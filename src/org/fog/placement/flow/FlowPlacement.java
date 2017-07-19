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
 * Currently uses Ford Fulkerson to calculate max flow.
 * @todo    change implementaiton to Ahura-Orlin (http://www.stefanoscerra.it/java-max-flow-graph-algorithm/)
 * @class   FlowPlacement 
 * @author  William McCarty
 * @email   wpm0002@auburn.edu
 */
public class FlowPlacement {

    private int             numberOfNodes;
    private double          maxFlowValue;
    private FlowNetwork     flowNet;
    private FlowNetwork     maxFlowNetwork;
    private FlowNetwork     minCutEdgeNetwork;
    private FordFulkerson   maxFlow;

    /**
     * @param LinkIDList a list of the link ID's 
     * @param numberOfNodes - the number of nodes in the graph 
     */
    FlowPlacement(List<Link> linkList, int numOfNodes) {
        setNumberOfNodes(numOfNodes);
        flowNet = new FlowNetwork(numberOfNodes);
        for (Link temp : linkList)
        {
            FlowEdge edge = new FlowEdge(temp.getEndpointSouth(), temp.getEndpointNorth(), temp.getBandwidth());
            flowNet.addEdge(edge);
        }
    }


    /**
     * Function to calculate the max flow.
     * @param s source  
     * @param t sink
     * @return a network with only the edges corresponding to the maximum flow 
     */
    public FlowNetwork getMaxFlow(int s, int t) {
         maxflow = new FordFulkerson(flowNet, s, t);
         maxFlowNetwork = new FlowNetwork(numberOfNodes);
         for (int v = 0; v < G.V(); v++) {
             for (FlowEdge e : G.adj(v)) {
                 if ((v == e.from()) && e.flow() > 0)
                     maxFlowNetwork.addEdge(e);
             }
         }

         return maxFlowNetwork;
    }

    /**
     * Function to generate min cut 
     * @param s source
     * @param v sink
     * @return FlowNetwork 
     * @TODO make this work
     */
    //public FlowNetwork getMinCut(int s, int t)
    //{    
    //    FordFulkerson maxflow = new FordFulkerson(flowNet, s, t);
    //    for (int v = 0; v < flowNet.V(); v++) {
    //        if (maxflow.inCut(v)) {
    //        }
    //    }
    //}


    /**
     * Estimate the number of waiting jobs at the northern node
     *  This is a very rough estimation that shouldn't be used for anything 
     * @param  link the link we want to estimate  
     * @return integer value for the number of waiting jobs.
     */
    private int estimateOccupancyNorth(Link link) {
     return link.getNorthTupleQueue().size();
    }

    /**
     * Estimate the number of waiting jobs at the southern node
     *  This is a very rough estimation that shouldn't be used for anything 
     * @param  link the link we want to estimate  
     * @return integer value for the number of waiting jobs.
     */
    private int estimateOccupancySouth(Link link) {
     return link.getSouthTupleQueue().size();
    }


    /**
     * @return value - returns the value of the max flow. 
     */
    public double getMaxFlowValue() {
        return maxFlowValue;
    }

    /**
     * @param n number of edges 
     */
    private void setNumberOfNodes(int n) {
        this.numberOfNodes = n;
    }

}