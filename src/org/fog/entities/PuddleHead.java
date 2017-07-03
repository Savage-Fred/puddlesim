/**
 * Title: PuddleSim
 * Description: PuddleSim is an extension of the iFogSim simulator.
 */
package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.network.Link;
import org.fog.utils.FogEvents;
import org.fog.utils.Point;
import org.fog.utils.Polygon;

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 26, 2017
 * 
 * The PuddleHead class represents the PuddleHead in our fog architecture. It is a control unit that monitors fog nodes and manages the
 * scheduling of applications. 
 *
 */
public class PuddleHead extends SimEntity {
	
	/**
	 * List of IDs of the FogNodes which this PuddleHead is in control of. 
	 */
	protected List<Integer> puddleDevices;
	
	/**
	 * This map contains the ID of a fog device and it's corresponding FogDeviceCharacteristics object. This is how the PuddleHead will 
	 * keep track of how many resources a device has and what resources are available.
	 */
	protected Map<Integer,FogDeviceCharacteristics> puddleDevicesCharacteristics;
	
	/**
	 * The ID of the parent PuddleHead.
	 */
	protected int parentId; 
	
	/**
	 * List of all the IDs of the other PuddleHeads in this puddle.
	 */
	protected List<Integer> puddleBuddies; 
	
	/**
	 * List of all the IDs of PuddleHeads who are children of this PuddleHead.
	 */
	protected List<Integer> childrenIds;
	
	/**
	 * Map of all the nodes that belong to each child PuddleHead.
	 */
	protected Map<Integer, List<Integer>> childrenPuddles;
	
	/**
	 * Level of fog that this PuddleHead belongs to.
	 */
	private int level; 
	
	/**
	 * The area that the PuddleHead would be the optimal connection to make. 
	 */
	protected Polygon areaOfCoverage;
	
	/**
	 * Location of the PuddleHead.
	 */
	protected Point location;
	
	/**
	 * The integer is the ID of the fog node and the list is of all modules currently running on that node. 
	 */
	protected Map<Integer, List<AppModule>> runningServices; 
	
	/**
	 * Map of the links by their id with the link object. 
	 */
	protected Map<Integer, Link> linksMap;
	
	/**
	 * List of all the services running in the puddle which belongs to this PuddleHead.
	 */
	protected List<Application> runningApplications;
	

	/**
	 * Constructor of a PuddleHead, initializes all Lists and Maps
	 * @param name
	 */
	public PuddleHead(String name, Polygon areaOfCoverage, Point location, int level) {
		super(name);
		
		this.areaOfCoverage = areaOfCoverage;
		this.location = location;
		this.level = level;
		
		setPuddleDevices(new ArrayList<Integer>());
		setPuddleDevicesCharacteristics(new HashMap<Integer, FogDeviceCharacteristics>());
		setPuddleBuddies(new ArrayList<Integer>()); 
		setChildrenIds(new ArrayList<Integer>());
		setChildrenPuddles(new HashMap<Integer, List<Integer>>());
		setRunningServices(new HashMap<Integer, List<AppModule>>());
		setLinksMap(new HashMap<Integer, Link>());
		setRunningApplications(new ArrayList<Application>());
		
		parentId = -1;
		
	}

	/* (non-Javadoc)
	 * @see org.cloudbus.cloudsim.core.SimEntity#startEntity()
	 */
	@Override
	public void startEntity() {

	}

	/* (non-Javadoc)
	 * @see org.cloudbus.cloudsim.core.SimEntity#processEvent(org.cloudbus.cloudsim.core.SimEvent)
	 */
	@Override
	public void processEvent(SimEvent ev) {
		switch(ev.getTag()){
		case FogEvents.NODE_JOIN_PUDDLEHEAD:
			processNodeJoinPuddleHead(ev); 
			break;
		case FogEvents.NODE_LEAVE_PUDDLEHEAD:
			processNodeLeavePuddleHead(ev);
			break;
		case FogEvents.PLACE_SERVICES:
			processPlaceServices(ev);
			break;
		case FogEvents.NODE_RELOCATE_PUDDLE: 
			processNodeRelocatePuddle(ev);
			break;
		default:
			break;
		}
	}

	/* (non-Javadoc)
	 * @see org.cloudbus.cloudsim.core.SimEntity#shutdownEntity()
	 */
	@Override
	public void shutdownEntity() {

	}

	//TODO add all these functions.
	/**
	 * Cares for when a initially joins a PuddleHead in the overall network. 
	 * The event must have in the data the nodeId of the node joining. 
	 * @param ev
	 */
	protected void processNodeJoinPuddleHead(SimEvent ev){
		int nodeId = (int) ev.getData(); 
		FogNode node = (FogNode) CloudSim.getEntity(nodeId); 
		
		node.setPuddleHeadId(this.getId());
		node.setGone(false);
		
		for(int buddyId : puddleDevices){
			FogNode buddy = (FogNode) CloudSim.getEntity(buddyId);
			buddy.addPuddleBuddy(nodeId);
		}
		
		node.setPuddleBuddies(puddleDevices);
		addPuddleDevice(nodeId);
		addPuddleDeviceCharacteristics(nodeId, node.getDeviceCharactersitics());
		
		if(parentId > 0){
			PuddleHead parent = (PuddleHead) CloudSim.getEntity(parentId);
			parent.addChildPuddle(getId(), puddleDevices);
		}
	}
	
	/**
	 * Cares for when a node leaves a PuddleHead. This occurs when a node relocates to a different PuddleHead 
	 * or leaves the network overall. Extra handling of running services occurs in the latter case. 
	 * Does the maintenance for updating all necessary lists.
	 * The data in the event should be the node ID. 
	 * @param ev
	 */
	protected void processNodeLeavePuddleHead(SimEvent ev){
		int nodeId = (int) ev.getData();
		FogNode node = (FogNode) CloudSim.getEntity(nodeId); 
		removePuddleDevice(nodeId); 
		removePuddleDeviceCharacteristics(nodeId);
		
		if(node.isGone()){
			//TODO HANDLE THE SERVICE STUFF HERE since the node is completely gone not relocated.
			node.setPuddleHeadId(-1);
		}
		else{
			for(int i = 0; i < puddleDevices.size(); i++){
				int buddyId = puddleDevices.get(i);
				FogNode buddy = (FogNode) CloudSim.getEntity(buddyId);
				buddy.removePuddleBuddy(nodeId);
			}
		}
			
		removeRunningServices(nodeId);
		
		//TODO deal with service migration if necessary. order of events may be an issue in removing running services
	}
	
	protected void processPlaceServices(SimEvent ev){
		//TODO make this work 
		//update the running services table 
	}
	
	/**
	 * Cares for when a node is moving between PuddleHeads. The PuddleHead who does the processing is the new PuddleHead. 
	 * The PuddleHead does maintenance within the node and for its own lists. If the PuddleHead has a parent PuddleHead, 
	 * it also updates the list of devices within its puddle in its parents list. This function deals with any service migration
	 * and placement that needs to occur. At the end of the function, it sends a node leave PuddleHead to the old PuddleHead. 
	 * The event should have the data of the node id. 
	 * @param ev (SimEvent)
	 */
	protected void processNodeRelocatePuddle(SimEvent ev){
		int nodeId = (int) ev.getData(); 
		FogNode node = (FogNode) CloudSim.getEntity(nodeId); 
		
		int oldPuddleHeadId = node.getPuddleHeadId();
		
		node.setPuddleHeadId(getId());
		addPuddleDevice(nodeId);
		addPuddleDeviceCharacteristics(nodeId, node.getDeviceCharactersitics());
		
		if(parentId > 0){
			PuddleHead parent = (PuddleHead) CloudSim.getEntity(parentId);
			parent.addChildPuddle(getId(), puddleDevices);
		}
		
		
		//TODO service relocation and placement algorithm
		//SERVICE STUFF
		
		
		send(oldPuddleHeadId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_LEAVE_PUDDLEHEAD, nodeId);
		
		
	}
	
	/**
	 * Adds a node to a PuddleHead's puddle. Does all necessary list maintenance. Should only be used for initializing architecture.
	 * During the simulation it should be an event that calls processNodeJoinPuddleHead. 
	 * @param newNodeId
	 */
	public void addNodetoPuddleHead(int newNodeId){
		FogNode node = (FogNode) CloudSim.getEntity(newNodeId);
		
		node.setPuddleHeadId(getId());
		
		for(int i = 0; i < puddleDevices.size(); i++){
			int buddyId = puddleDevices.get(i);
			FogNode buddy = (FogNode) CloudSim.getEntity(buddyId);
			buddy.addPuddleBuddy(newNodeId);
		}
		
		addPuddleDeviceCharacteristics(newNodeId, node.getDeviceCharactersitics());
		node.setPuddleBuddies(puddleDevices);
		addPuddleDevice(newNodeId);
		
		if(parentId > 0){
			PuddleHead parent = (PuddleHead) CloudSim.getEntity(parentId);
			parent.addChildPuddle(getId(), puddleDevices);
		}
	}
	
	
	/**
	 * Takes the ID of a PuddleHead that should become this PuddleHead's child and does all the list/map maintenance. 
	 * @param babyId
	 */
	public void newChildPuddleHead(int babyId){
		PuddleHead baby = (PuddleHead) CloudSim.getEntity(babyId);
		
		baby.setParentId(getId());
		
		for(int childId : childrenIds){
			PuddleHead child = (PuddleHead) CloudSim.getEntity(childId);
			child.addPuddleBuddy(babyId);
		}
		
		baby.setPuddleBuddies(childrenIds);
		addChildId(babyId);
		addChildPuddle(babyId, baby.getPuddleDevices());
	}
	
	/**
	 * Remove a child PuddleHead and do the list maintenance for your other children and your lists.
	 * @param badChildId
	 */
	public void removeChildPuddleHead(int badChildId){
		
		removeChildId(badChildId);
		removeChildPuddle(badChildId);
		
		for(int childId: childrenIds){
			PuddleHead child = (PuddleHead) CloudSim.getEntity(childId);
			child.removePuddleBuddy((Integer)badChildId);
		}
	}
	
	/**
	 * @return the puddleDevices
	 */
	public List<Integer> getPuddleDevices() {
		return puddleDevices;
	}

	/**
	 * @param puddleDevices the puddleDevices to set
	 */
	public void setPuddleDevices(List<Integer> puddleDevices) {
		this.puddleDevices = puddleDevices;
	}
	
	/**
	 * Add a single node the puddleDevices.
	 * @param deviceId
	 */
	public void addPuddleDevice(int deviceId){
		puddleDevices.add(deviceId);
	}
	
	/**
	 * Remove a single node from puddleDevices
	 * @param deviceId
	 */
	public void removePuddleDevice(int deviceId){
		puddleDevices.remove((Integer)deviceId);
	}
	
	/**
	 * Check if a node is one of my puddle devices.
	 * @param deviceId
	 * @return true if it is a puddle device.
	 */
	public boolean isMyPuddleDevice(int deviceId){
		return puddleDevices.contains(deviceId);
	}

	/**
	 * @return the puddleDevicesCharacteristics
	 */
	public Map<Integer, FogDeviceCharacteristics> getPuddleDevicesCharacteristics() {
		return puddleDevicesCharacteristics;
	}

	/**
	 * @param puddleDevicesCharacteristics the puddleDevicesCharacteristics to set
	 */
	public void setPuddleDevicesCharacteristics(Map<Integer, FogDeviceCharacteristics> puddleDevicesCharacteristics) {
		this.puddleDevicesCharacteristics = puddleDevicesCharacteristics;
	}
	
	/**
	 * Add the characteristics for a single node in the puddle.
	 * @param deviceId
	 * @param characteristics
	 */
	public void addPuddleDeviceCharacteristics(int deviceId, FogDeviceCharacteristics characteristics) {
		puddleDevicesCharacteristics.put(deviceId, characteristics);
	}
	
	/**
	 * Get the FogDeviceCharacteristics information for a single node in the puddle.
	 * @param deviceId
	 * @return
	 */
	public FogDeviceCharacteristics getPuddleDeviceCharacteristics(int deviceId){
		return puddleDevicesCharacteristics.get(deviceId); 
	}
	
	/**
	 * Remove a single node's characteristics. 
	 * @param deviceId
	 */
	public void removePuddleDeviceCharacteristics(int deviceId){
		puddleDevicesCharacteristics.remove(deviceId);
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the puddleBuddies
	 */
	public List<Integer> getPuddleBuddies() {
		return puddleBuddies;
	}

	/**
	 * @param puddleBuddies the puddleBuddies to set
	 */
	public void setPuddleBuddies(List<Integer> puddleBuddies) {
		this.puddleBuddies = puddleBuddies;
	}
	
	/**
	 * Add a PuddleHead to the puddleBuddies.
	 * @param buddyId
	 */
	public void addPuddleBuddy(int buddyId){
		puddleBuddies.add(buddyId);
	}
	
	/**
	 * Remove a single puddle buddy PuddleHead. 
	 * @param buddyId
	 */
	public void removePuddleBuddy(int buddyId){
		puddleBuddies.remove((Integer)buddyId);
	}
	
	/**
	 * Check if a PuddleHead is a puddle buddy.
	 * @param buddyId
	 * @return true if it is a puddle buddy
	 */ 
	public boolean isMyPuddleBuddy(int buddyId){
		return puddleBuddies.contains(buddyId);
	}

	/**
	 * @return the childrenIds
	 */
	public List<Integer> getChildrenIds() {
		return childrenIds;
	}

	/**
	 * @param childrenIds the childrenIds to set
	 */
	public void setChildrenIds(List<Integer> childrenIds) {
		this.childrenIds = childrenIds;
	}
	
	/**
	 * Add a single child id.
	 * @param deviceId
	 */
	public void addChildId(int deviceId){
		childrenIds.add(deviceId);
	}
	
	/**
	 * Remove a single child id. 
	 * @param deviceId
	 */
	public void removeChildId(int deviceId){
		childrenIds.remove((Integer)deviceId);
	}

	/**
	 * @return the childrenPuddles
	 */
	public Map<Integer, List<Integer>> getChildrenPuddles() {
		return childrenPuddles;
	}

	/**
	 * @param childrenPuddles the childrenPuddles to set
	 */
	public void setChildrenPuddles(Map<Integer, List<Integer>> childrenPuddles) {
		this.childrenPuddles = childrenPuddles;
	}
	
	/**
	 * Add the puddle of a child PuddleHead to the list. Also called when updating child puddle list. 
	 * @param childId
	 * @param childPuddle
	 */
	public void addChildPuddle(int childId, List<Integer> childPuddle){
		childrenPuddles.put(childId, childPuddle);
	}
	
	/**
	 * Removes a single child puddle from the list
	 * @param childId
	 */
	public void removeChildPuddle(int childId){
		childrenPuddles.remove(childId);
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return the areaOfCoverage
	 */
	public Polygon getAreaOfCoverage() {
		return areaOfCoverage;
	}

	/**
	 * @param areaOfCoverage the areaOfCoverage to set
	 */
	public void setAreaOfCoverage(Polygon areaOfCoverage) {
		this.areaOfCoverage = areaOfCoverage;
	}

	/**
	 * @return the location
	 */
	public Point getLocation() {
		return this.location;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLocation(Point point) {
		this.location = point;
	}

	/**
	 * @return the runningServices
	 */
	public Map<Integer, List<AppModule>> getRunningServices() {
		return runningServices;
	}

	/**
	 * @param runningServices the runningServices to set
	 */
	public void setRunningServices(Map<Integer, List<AppModule>> runningServices) {
		this.runningServices = runningServices;
	}
	
	/**
	 * Remove the running services for a single node from the table.
	 * @param deviceId
	 */
	public void removeRunningServices(int deviceId){
		runningServices.remove(deviceId);
	}
	
	/**
	 * @param linksMap
	 */
	public void setLinksMap(Map<Integer, Link> linksMap){
		this.linksMap = linksMap;
	}
	
	/**
	 * @return the current links map
	 */
	public Map<Integer, Link> getLinksMap(){
		return linksMap; 
	}
	
	/**
	 * Add a link to the map and update all associate tables.
	 * It determines the type of object at the other end of the link (PuddleHead or FogNode) and proceeds accordingly.
	 * It only does processing if the other PuddleHead is at a lower level. All FogNodes are processed.   
	 * @param linkId
	 * @param theLink
	 */
	//TODO have Chandler proof this
	public void addLinkToMap(int linkId, Link theLink){
		linksMap.put(linkId, theLink);
		int otherEnd = theLink.getOtherEndpoint(getId());
		SimEntity ent = CloudSim.getEntity(otherEnd);
		String type = ent.getClass().getName(); 
		if(type.contains("PuddleHead")){
			PuddleHead otherPuddleHead = (PuddleHead) CloudSim.getEntity(otherEnd); 
			int otherLevel = otherPuddleHead.getLevel();
			if(level > otherLevel){
				newChildPuddleHead(otherEnd);
			}
		}
		else if(type.contains("FogNode")){
			addNodetoPuddleHead(otherEnd);
		}
	}
	
	/**
	 * Removes a single link from the map.
	 * @param linkId
	 */
	public void removeLinkFromMap(int linkId){
		linksMap.remove(linkId);
	}

	/**
	 * @return the runningApplications
	 */
	public List<Application> getRunningApplications() {
		return runningApplications;
	}

	/**
	 * @param runningApplications the runningApplications to set
	 */
	public void setRunningApplications(List<Application> runningApplications) {
		this.runningApplications = runningApplications;
	}
	

}
