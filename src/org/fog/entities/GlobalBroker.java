package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.utils.FogEvents;
import org.fog.utils.Point;
import org.fog.utils.Polygon;

public class GlobalBroker extends FogBroker {
	
	protected List<Integer> puddleHeadIds; 
	
	protected Map<Integer, List<Integer>> puddleHeadsByLevel; 
	
	
	public GlobalBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
		setPuddleHeadIds(new ArrayList<Integer>());
		setPuddleHeadsByLevel(new HashMap<Integer, List<Integer>>()); 
	}
	
	@Override
	public void processEvent(SimEvent ev){
		switch(ev.getTag()){
		case FogEvents.PROCESS_NODE_MOVE:
			processNodeMove(ev); 
			break;
//		case FogEvents.NODE_LEAVE:
//			processNodeLeave(ev); 
//			break;
		}
	}

	
	/**
	 * Event to be run to check if a node is still within the area of coverage of its current puddlehead
	 * If the node has gone out of bounds of its puddlehead, a new puddlehead is determined. If a new puddlehead 
	 * can be found then the node relocate is called. Otherwise, the node has left the bounds of all puddleheads
	 * and node leave is called. 
	 * @param ev
	 */
	public void processNodeMove(SimEvent ev){
		int nodeId = (int) ev.getData();
		
		boolean inArea = checkNodeInPuddleHeadRange(nodeId);
		
		if(!inArea){
			FogNode node = (FogNode) CloudSim.getEntity(nodeId);
			//send(node.getPuddleHeadId(), CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_LEAVE_PUDDLEHEAD, nodeId); 
			
			int puddleHeadId = findNodeNewPuddleHead(nodeId);
			
			if(puddleHeadId > 0){
				int currentPuddleHeadId = node.getPuddleHeadId();
				send(puddleHeadId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_RELOCATE_PUDDLE, nodeId);
				send(currentPuddleHeadId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_LEAVE_PUDDLEHEAD, nodeId); 
				//send(puddleHeadId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_JOIN_PUDDLEHEAD, node);
			}
			else {
				send(nodeId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_LEAVE); 
			}
			
		}
		
	}
	
//	public void processNodeLeave(SimEvent ev){
//		
//	}
	
	/**
	 * Private function for checking if a node is in its current puddleheads polygon area of coverage
	 * Used by processNodeLocationUpdates
	 * @param nodeId
	 * @return
	 */
	private boolean checkNodeInPuddleHeadRange(int nodeId){
		FogNode node = (FogNode) CloudSim.getEntity(nodeId);
		PuddleHead puddlehead = (PuddleHead) CloudSim.getEntity(node.getPuddleHeadId());
		
		Point nodePoint = node.mobile.getPoint(); 
		Polygon area = puddlehead.getAreaOfCoverage(); 
		
		return area.contains(nodePoint);
	}
	
	/**
	 * Private function for finding the puddlehead's area of coverage where a node currently is. 
	 * If it returns -2 it means there are no viable puddleheads. 
	 * Used by processNodeLocationUpdates
	 * @param nodeId
	 * @return
	 */
	private int findNodeNewPuddleHead(int nodeId){
		FogNode node = (FogNode) CloudSim.getEntity(nodeId);
		Point nodePoint = node.mobile.getPoint();
		
		List<Integer> viablePuddleHeads = puddleHeadsByLevel.get(node.getLevel());
		
		for(Integer puddleHeadId : viablePuddleHeads){
			PuddleHead puddlehead = (PuddleHead) CloudSim.getEntity(puddleHeadId);
			Polygon area = puddlehead.getAreaOfCoverage(); 
			
			if(area.contains(nodePoint)){
				return puddleHeadId; 
			}
		}
		
		return -2; 
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
	
	
}
