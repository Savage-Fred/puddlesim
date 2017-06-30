/**
 * Title: PuddleSim
 * Description: PuddleSim is an extension to the iFogSim simulator
 */
package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.utils.FogEvents;
import org.fog.utils.Logger;
import org.fog.utils.Point;
import org.fog.utils.Polygon;

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
	protected List<Integer> nodeIds; 
	
	/**
	 * List of IDs of all sensors in the network.
	 */
	protected List<Integer> sensorIds;
	
	/**
	 * List of IDs of all actuators in the network. 
	 */
	protected List<Integer> actuatorIds; 
	
	
	/**
	 * Constructor of a GlobalBroker. The input name should be 'globalbroker' for use with PuddleSim capabilities. (See Note above)
	 * @param name
	 * @throws Exception
	 */
	public GlobalBroker(String name) throws Exception {
		super(name);

		setPuddleHeadIds(new ArrayList<Integer>());
		setPuddleHeadsByLevel(new HashMap<Integer, List<Integer>>());
		setNodeIds(new ArrayList<Integer>());
		setSensorIds(new ArrayList<Integer>());
		setActuatorIds(new ArrayList<Integer>()); 
	}
	
	/**
	 * Takes in the list of IDs of PuddleHeads and nodes and sets up the corresponding lists and map. 
	 * @param puddleHeadIn
	 * @param nodeIn
	 */
	public void setup(List<Integer> puddleHeadIn, List<Integer> nodeIn){
		setPuddleHeadIds(puddleHeadIn); 
		setNodeIds(nodeIn);
		for(int puddleHeadId : puddleHeadIds){
			PuddleHead puddleHead = (PuddleHead) CloudSim.getEntity(puddleHeadId);
			addPuddleHeadByLevel(puddleHeadId, puddleHead.getLevel());
		}
	}
	
	@Override
	public void processEvent(SimEvent ev){
		switch(ev.getTag()){
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
		Log.printLine("GlobalBroker is processing new location: " + ((FogNode) CloudSim.getEntity((int)ev.getData())).getLocation());
		Log.disable();
		
		int nodeId = (int) ev.getData();
		
		boolean inArea = checkNodeInPuddleHeadRange(nodeId);
		
		if(!inArea){
			int newPuddleHeadId = findNodeNewPuddleHead(nodeId);
			
			if(newPuddleHeadId > 0){
				send(newPuddleHeadId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_RELOCATE_PUDDLE, nodeId);
			}
			else {
				send(nodeId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_LEAVE);
				removeNodeId(nodeId);
			}
			
		}
	}
	
	
	/**
	 * Private function for checking if a node is in its current PuddleHeads polygon area of coverage
	 * Used by processNodeLocationUpdates
	 * @param nodeId
	 * @return true if it is in the current puddlehead's area of coverage
	 */
	private boolean checkNodeInPuddleHeadRange(int nodeId){
		FogNode node = (FogNode) CloudSim.getEntity(nodeId);
		PuddleHead puddlehead = (PuddleHead) CloudSim.getEntity(node.getPuddleHeadId());
		
		Point nodePoint = node.getLocation(); 
		Polygon area = puddlehead.getAreaOfCoverage(); 
		
		return area.contains(nodePoint);
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
	

	/**
	 * @return the nodeIds
	 */
	public List<Integer> getNodeIds() {
		return nodeIds;
	}

	/**
	 * @param nodeIds the nodeIds to set
	 */
	public void setNodeIds(List<Integer> nodeIds) {
		this.nodeIds = nodeIds;
	}
	
	/**
	 * Add a single node id to the list
	 * @param nodeId
	 */
	public void addNodeId(int nodeId){
		nodeIds.add(nodeId);
	}
	
	/**
	 * Remove a single node id from the list
	 * @param nodeId
	 */
	public void removeNodeId(int nodeId){
		nodeIds.remove((Integer)nodeId);
	}

	/**
	 * @return the sensorIds
	 */
	public List<Integer> getSensorIds() {
		return sensorIds;
	}

	/**
	 * @param sensorIds the sensorIds to set
	 */
	public void setSensorIds(List<Integer> sensorIds) {
		this.sensorIds = sensorIds;
	}
	
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
	public List<Integer> getActuatorIds() {
		return actuatorIds;
	}

	/**
	 * @param actuatorIds the actuatorIds to set
	 */
	public void setActuatorIds(List<Integer> actuatorIds) {
		this.actuatorIds = actuatorIds;
	}
	
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
	
}
