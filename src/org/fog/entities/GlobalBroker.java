/**
 * Title: PuddleSim
 * Description: PuddleSim is an extension to the iFogSim simulator
 */
package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.network.Link;
import org.fog.utils.AdjacencyList;
import org.fog.utils.FogEvents;
import org.fog.utils.Logger;
import org.fog.utils.Point;
import org.fog.utils.Polygon;
import org.fog.utils.Graph;
import org.fog.utils.KruskalAlgorithm;

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 29, 2017
 * 
 * The GlobalBroker is a simulation only entity to allow the simulator to act in place of network capabilities found in the real world.
 * There should only be one GlobalBroker declared for any simulation. 
 * The GlobalBroker should contain information about every device in the network. It is used to send events between different devices
 * and track their movement in relation to each other. 
 * 
 * Note: It should have the name 'globalbroker' for the capabilities to work with mobility and updating between devices.
 * (If this name would like to be changed, code in the FogNode function processUpdateLocation needs to reflect the change).
 *
 */

public class GlobalBroker extends FogBroker {
	
	private static final String LOG_TAG = "GLOBAL_BROKER";
	
	/**
	 * List of the IDs for all PuddleHeads in the network.
	 */
	protected List<Integer> puddleHeadIds; 
	
	/**
	 * Map of all the PuddleHeads in the network by level. The key is the level and the list is the IDs of all PuddleHeads at that level.
	 */
	protected Map<Integer, List<Integer>> puddleHeadsByLevel; 
	
	/**
	 * List of IDs of all nodes in the network. 
	 */
	//protected List<Integer> nodeIds; 
	
	/**
	 * List of IDs of all sensors in the network.
	 */
//	protected List<Integer> sensorIds;
	
	/**
	 * List of IDs of all actuators in the network. 
	 */
//	protected List<Integer> actuatorIds; 
	
	/**
	 * List of all link IDs that have been created.
	 */
	protected List<Integer> linkIds;
	
	/**
	 * List of all end device IDs that have been created.
	 */
	protected List<Integer> endDeviceIds;
	
	/**
	 * Minimum spanning tree object
	 */
	KruskalAlgorithm MST= null;
	
	/**
	 * Adjacency List object used for MST
	 */
	AdjacencyList adjacencyList = null;
	
	int numberOfNodes = 0;
	
	/**
	 * Constructor of a GlobalBroker. The input name should be 'globalbroker' for use with PuddleSim capabilities. (See Note above)
	 * @param name
	 * @throws Exception
	 */
	public GlobalBroker(String name) throws Exception {
		super(name);
		Logger.debug(LOG_TAG, "Creating GlobalBroker with name " + name + " ID: " + this.getId());
		setPuddleHeadIds(new ArrayList<Integer>());
		setPuddleHeadsByLevel(new HashMap<Integer, List<Integer>>());
		setFogDeviceIds(new ArrayList<Integer>());
		setSensorIds(new ArrayList<Integer>());
		setActuatorIds(new ArrayList<Integer>());
		setLinkIds(new ArrayList<Integer>());
	}
	
	/**
	 * Takes in the list of IDs of PuddleHeads and nodes and sets up the corresponding lists and map. 
	 * @param puddleHeadIn
	 * @param nodeIn
	 */
	public void setup(List<Integer> puddleHeadIn, 
					List<Integer> nodeIn, 
					List<Integer> linkIds, 
					List<Integer> endDeviceIds){
		setPuddleHeadIds(puddleHeadIn); 
		setFogDeviceIds(nodeIn);
		setLinkIds(linkIds);
		setEndDeviceIds(endDeviceIds);
		
		for(int puddleHeadId : puddleHeadIds){
			PuddleHead puddleHead = (PuddleHead) CloudSim.getEntity(puddleHeadId);
			addPuddleHeadByLevel(puddleHeadId, puddleHead.getLevel());
		}
		
		numberOfNodes = endDeviceIds.size() + puddleHeadIn.size() + nodeIn.size();
		adjacencyList = new AdjacencyList(getHighestDeviceId()+1);
		setAdjacencyList();
		//setGraph();
		//setMST();
	}
	
	private void setAdjacencyList() {
		int linkId = 0;
		for(int i = 0; i < linkIds.size(); i++){
			linkId = linkIds.get(i);
			Link link = (Link)CloudSim.getEntity(linkId);
			adjacencyList.addEdge(link.getEndpointNorth(), link.getEndpointSouth(), 1);
			adjacencyList.addEdge(link.getEndpointSouth(), link.getEndpointNorth(), 1);
			Logger.debug(LOG_TAG, "Creating edge "+linkId);
		}
		adjacencyList.printAdjacencyList();
	}

	private int getHighestDeviceId() {
		int maxIndex = 0;
		for(int puddleHeadId : puddleHeadIds){
			if (puddleHeadId > maxIndex)
				maxIndex = puddleHeadId;
		}
		for(int fogNodeId : fogDeviceIds){
			if (fogNodeId > maxIndex)
				maxIndex = fogNodeId;
		}
		for(int endDeviceId : endDeviceIds){
			if (endDeviceId > maxIndex)
				maxIndex = endDeviceId;
		}
		return maxIndex;
	}

	public int getLinkIdBetweenTwoDevices(int id1, int id2){
		int id = -1;
		Logger.debug(LOG_TAG, "Finding link between: "+id1+"<->"+id2);
		for(Integer linkId : linkIds){
			Link possibleLink = (Link)CloudSim.getEntity(linkId);
			if(possibleLink.getEndpointNorth() == id1 && possibleLink.getEndpointSouth() == id2 || 
				possibleLink.getEndpointNorth() == id2 && possibleLink.getEndpointSouth() == id1)
				id = possibleLink.getId();
		}
		return id;
	}
	
//	/**
//	 * Setup function for the minimum spanning tree. Requires that fog device, puddlehead, end device, and link lists be set up beforehand.
//	 */
//	private void setGraph(){
//		int linkId = 0;
//		for(int i = 0; i < linkIds.size(); i++){
//			linkId = linkIds.get(i);
//			Link link = (Link)CloudSim.getEntity(linkId);
//			graph.edge[i].src = link.getEndpointNorth();
//			graph.edge[i].dest = link.getEndpointSouth();
//			graph.edge[i].weight = 1;
//			Logger.debug(LOG_TAG, "Creating edge "+linkId);
//		}
//		graph.KruskalMST();
//	}
//	
//	private void setMST(){
//		// Set up the minimum spanning tree
//		int maxIndex = 0;
//		int minIndex = puddleHeadIds.get(0);
//		for(int puddleHeadId : puddleHeadIds){
//			if (puddleHeadId > maxIndex)
//				maxIndex = puddleHeadId;
//			else if (puddleHeadId < minIndex)
//				minIndex = puddleHeadId;
//		}
//		for(int fogNodeId : fogDeviceIds){
//			if (fogNodeId > maxIndex)
//				maxIndex = fogNodeId;
//			else if (fogNodeId < minIndex)
//				minIndex = fogNodeId;
//		}
//		for(int endDeviceId : endDeviceIds){
//			if (endDeviceId > maxIndex)
//				maxIndex = endDeviceId;
//			else if (endDeviceId < minIndex)
//				minIndex = endDeviceId;
//		}
//		
//		int dimensions = puddleHeadIds.size()+fogDeviceIds.size()+endDeviceIds.size();
//		if(dimensions == 0)
//			throw new IllegalArgumentException("Error: Puddleheads + Nodes == 0");
//		MST = new KruskalAlgorithm(maxIndex);
//		int [][] adjacencyMatrix = new int[maxIndex+1][maxIndex+1];
//		Link link;
//		for(Integer linkId : linkIds){
//			link = (Link)CloudSim.getEntity(linkId);
//			Logger.debug(LOG_TAG, "Link: " + link.getId());
//			// Make a connection between the 2. Links are bi-directional.
//			adjacencyMatrix[link.getEndpointNorth()][link.getEndpointSouth()] = 1;
//			adjacencyMatrix[link.getEndpointSouth()][link.getEndpointNorth()] = 1;
//		}
//		MST.kruskalAlgorithm(adjacencyMatrix);
//	}
	
	@Override
	public void processEvent(SimEvent ev){
		switch(ev.getTag()){
		case FogEvents.APP_SUBMIT:
			deployApplication(ev.getData().toString());
			break;
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
			processResourceCharacteristicsRequest(ev);
			break;
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
			processResourceCharacteristics(ev);
			break;
		case FogEvents.PROCESS_NODE_MOVE:
			processNodeMove(ev); 
			break;
		}
	}

	
	/**
	 * Event to be run to check if a node is still within the area of coverage of its current PuddleHead
	 * If the node has gone out of bounds of its PuddleHead, a new PuddleHead is determined. If a new PuddleHead 
	 * can be found then the node relocate is called. Otherwise, the node has left the bounds of all PuddleHeads
	 * and node leave is called. 
	 * @param ev
	 */
	public void processNodeMove(SimEvent ev){
		Log.enable();
		//Log.printLine("GlobalBroker is processing new location: " + ((FogNode) CloudSim.getEntity((int)ev.getData())).getLocation());
		
		
		int nodeId = (int) ev.getData();
		
		boolean inArea = checkNodeInPuddleHeadRange(nodeId);
		
		if(!inArea){
			int newPuddleHeadId = findNodeNewPuddleHead(nodeId);
			
			if(newPuddleHeadId > 0){
				//Log.printLine("I GOT A NEW PUDDLEHEAD: " + newPuddleHeadId + " I am: " + nodeId);
				//Log.printLine("In processNodeMove IDs: " + newPuddleHeadId + " " + nodeId);
				send(newPuddleHeadId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_RELOCATE_PUDDLE, nodeId);
			}
			else {
				//Log.printLine("IM FREEEEEEEEEEEEEE " + nodeId);
				//Log.printLine("In processNodeMove ID: " + nodeId);
				send(nodeId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_LEAVE);
				removeFogDeviceId(nodeId);
			}
			
		}
		Log.disable();
	}
	
	
	/**
	 * Private function for checking if a node is in its current PuddleHeads polygon area of coverage
	 * Used by processNodeLocationUpdates
	 * @param nodeId
	 * @return true if it is in the current puddlehead's area of coverage
	 */
	private boolean checkNodeInPuddleHeadRange(int nodeId){
		FogNode node = (FogNode) CloudSim.getEntity(nodeId);
		int puddleHeadId = node.getPuddleHeadId();
		boolean result = false;
		if(puddleHeadId > 0){
			PuddleHead puddlehead = (PuddleHead) CloudSim.getEntity(node.getPuddleHeadId());
			Point nodePoint = node.getLocation(); 
			Polygon area = puddlehead.getAreaOfCoverage(); 
			result = area.contains(nodePoint);
		}
		return result;
	}
	
	/**
	 * Private function for finding the puddlehead's area of coverage where a node currently is.
	 * To save time, it only looks at PuddleHeads in the node's level (since that is the restriction on connections anyway).
	 * If it returns -1 it means there are no viable PuddleHeads. 
	 * Used by processNodeLocationUpdates
	 * @param nodeId
	 * @return
	 */
	private int findNodeNewPuddleHead(int nodeId){
		FogNode node = (FogNode) CloudSim.getEntity(nodeId);
		Point nodePoint = node.getLocation();
		
		List<Integer> viablePuddleHeads = puddleHeadsByLevel.get(node.getLevel());
		
		if(viablePuddleHeads != null){
			for(Integer puddleHeadId : viablePuddleHeads){
//				Log.enable();
//				Log.printLine("ViablePuddleHeads: " + puddleHeadId);
//				Log.disable();
				PuddleHead puddlehead = (PuddleHead) CloudSim.getEntity(puddleHeadId);
				Polygon area = puddlehead.getAreaOfCoverage(); 
				
				if(area.contains(nodePoint)){
					return puddleHeadId; 
				}
			}
		}
		return -1; 
	}


	/**
	 * @return the puddleHeadIds
	 */
	public List<Integer> getPuddleHeadIds() {
		return puddleHeadIds;
	}

	/**
	 * @param puddleHeadIds the puddleHeadIds to set
	 */
	public void setPuddleHeadIds(List<Integer> puddleHeadIds) {
		this.puddleHeadIds = puddleHeadIds;
	}
	
	/**
	 * Add a single PuddleHead
	 * @param puddleHeadId
	 */
	public void addPuddleHeadId(int puddleHeadId){
		puddleHeadIds.add(puddleHeadId);
	}
	
	/**
	 * Remove a single PuddleHead
	 * @param puddleHeadId
	 */
	public void removePuddleHeadId(int puddleHeadId){
		puddleHeadIds.remove((Integer)puddleHeadId);
	}
	
	/**
	 * @return the puddleHeadsByLevel
	 */
	public Map<Integer, List<Integer>> getPuddleHeadsByLevel() {
		return puddleHeadsByLevel;
	}

	/**
	 * @param puddleHeadsByLevel the puddleHeadsByLevel to set
	 */
	public void setPuddleHeadsByLevel(Map<Integer, List<Integer>> puddleHeadsByLevel) {
		this.puddleHeadsByLevel = puddleHeadsByLevel;
	}
	
	/**
	 * Adds a single PuddleHead into the by level map. If there are no PuddleHeads at that level, a new list is created. 
	 * @param puddleHeadId
	 * @param level
	 */
	public void addPuddleHeadByLevel(int puddleHeadId, int level){
		List<Integer> levelList = puddleHeadsByLevel.get(level); 
		if(levelList == null){
			ArrayList<Integer> newLevelList = new ArrayList<Integer>();
			newLevelList.add(puddleHeadId); 
			puddleHeadsByLevel.put(level, newLevelList);
		}
		else{
			levelList.add(puddleHeadId);
			puddleHeadsByLevel.put(level, levelList); 
		}
	}
	
	/**
	 * Removes a single PuddleHead from the by level map. Error catching for if the level is not in the map. 
	 * @param puddleHeadId
	 * @param level
	 */
	public void removePuddleHeadByLevel(int puddleHeadId, int level){
		List<Integer> levelList = puddleHeadsByLevel.get(level);
		if(levelList != null){
				levelList.remove((Integer)puddleHeadId); 
				puddleHeadsByLevel.put(level, levelList);
		}
		else {
			Logger.debug(LOG_TAG, "GLOBAL_BROKER", "Tried to remove a puddlehead from a level that doesn't exist");
		}
	}

//	/**
//	 * Gets the next node in a minimum spanning tree towards the destination from the source.</p>
//	 * Used for module routing.
//	 * @param sourceId the id of the entity requesting the next node id. 
//	 * @param destinationId the id of the entity the module must eventually be sent to.
//	 * @return Integer indicating the next node/entity a module should be sent to.
//	 * <p><b>-1 if no node.
//	 */
//	public int nextNodeInMST(int sourceId, int destinationId){
//		return MST.nextNodeInMST(sourceId, destinationId);
//	}
	
	public int getNextNodeInPath(int sourceId, int destinationId){
		return this.adjacencyList.pathFindingUsingBFS(sourceId, destinationId).getFirst();
	}
	

	/**
	 * @return the nodeIds
	 */
	public List<Integer> getFogDeviceIds() {
		return fogDeviceIds;
	}

	/**
	 * @param nodeIds the nodeIds to set
	 */
	public void setFogDeviceIds(List<Integer> nodeIds) {
		this.fogDeviceIds = nodeIds;
	}
	
	/**
	 * Add a single node id to the list
	 * @param nodeId
	 */
	public void addFogDeviceId(int nodeId){
		fogDeviceIds.add(nodeId);
	}
	
	/**
	 * Remove a single node id from the list
	 * @param nodeId
	 */
	public void removeFogDeviceId(int nodeId){
		fogDeviceIds.remove((Integer)nodeId);
	}

	/**
	 * @return the sensorIds
	 */
//	public List<Integer> getSensorIds() {
//		return sensorIds;
//	}
//
//	/**
//	 * @param sensorIds the sensorIds to set
//	 */
//	public void setSensorIds(List<Integer> sensorIds) {
//		this.sensorIds = sensorIds;
//	} 
	
	/**
	 * Add a single sensor.
	 * @param sensorId
	 */
	public void addSensorId(int sensorId){
		sensorIds.add(sensorId);
	}
	
	/**
	 * Remove a single sensor.
	 * @param sensorId
	 */
	public void removeSensorId(int sensorId){
		sensorIds.remove((Integer)sensorId);
	}

	/**
	 * @return the actuatorIds
	 */
//	public List<Integer> getActuatorIds() {
//		return actuatorIds;
//	}
//
//	/**
//	 * @param actuatorIds the actuatorIds to set
//	 */
//	public void setActuatorIds(List<Integer> actuatorIds) {
//		this.actuatorIds = actuatorIds;
//	}
	
	/**
	 * Add a single actuator
	 * @param actuatorId
	 */
	public void addActuatorId(int actuatorId){
		actuatorIds.add(actuatorId);
	}
	
	/**
	 * Remove a single actuator 
	 * @param actuatorId
	 */
	public void removeActuatorId(int actuatorId){
		actuatorIds.remove((Integer)actuatorId);
	}

	/**
	 * @return the linkIDs
	 */
	public List<Integer> getLinkIds() {
		return linkIds;
	}

	/**
	 * @param linkIDs the linkIDs to set
	 */
	public void setLinkIds(List<Integer> linkIds) {
		this.linkIds = linkIds;
	}

	/**
	 * @return the endDeviceIds
	 */
	public List<Integer> getEndDeviceIds() {
		return endDeviceIds;
	}

	/**
	 * @param endDeviceIds the endDeviceIds to set
	 */
	public void setEndDeviceIds(List<Integer> endDeviceIds) {
		this.endDeviceIds = endDeviceIds;
	}
	
}
