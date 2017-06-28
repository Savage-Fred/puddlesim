/**
 * 
 */
package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.application.AppModule;
import org.fog.network.Link;
import org.fog.utils.FogEvents;
import org.fog.utils.Polygon;

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 26, 2017
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
	protected double latitude; 
	protected double longitude; 
	
	/**
	 * The integer is the ID of the fog node and the list is of all modules currently running on that node. 
	 */
	protected Map<Integer, List<AppModule>> runningServices; 
	
	protected Map<Integer, Link> linksMap;
	
	
	
	// services running on all puddleDevices
	// remaining resources on all remaining puddleDevices
	// info about children puddleHeads and their puddles 

	/**
	 * Constructor of a PuddleHead, initializes all Lists and Maps
	 * @param name
	 */
	public PuddleHead(String name) {
		super(name);
	
		setPuddleDevices(new ArrayList<Integer>());
		setPuddleDevicesCharacteristics(new HashMap<Integer, FogDeviceCharacteristics>());
		setPuddleBuddies(new ArrayList<Integer>()); 
		setChildrenIds(new ArrayList<Integer>());
		setChildrenPuddles(new HashMap<Integer, List<Integer>>());
		setRunningServices(new HashMap<Integer, List<AppModule>>());
		
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
		// TODO Auto-generated method stub
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
	 * Cares for when a initially joins a puddlehead in the overall network. 
	 * The event must have in the data the nodeId of the node joining. 
	 * @param ev
	 */
	protected void processNodeJoinPuddleHead(SimEvent ev){
		int nodeId = (int) ev.getData(); 
		FogNode node = (FogNode) CloudSim.getEntity(nodeId); 
		
		node.setPuddleHeadId(this.getId());
		
		for(int buddyId : puddleDevices){
			FogNode buddy = (FogNode) CloudSim.getEntity(buddyId);
			buddy.addPuddleBuddy(nodeId);
		}
		
		addPuddleDevice(nodeId);
		addPuddleDeviceCharacteristics(nodeId, node.getDeviceCharactersitics());
		
		
		
	}
	
	/**
	 * Cares for when a node leaves a puddlehead. This occurs when a node relocates to a different puddlehead 
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
		}
		else{
			for(int buddyId : puddleDevices){
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
	 * Cares for when a node is moving between puddleheads. The puddlehead who does the processing is the new puddlehead. 
	 * The puddlehead does maintenance within the node and for its own lists. If the puddlehead has a parent puddlehead, 
	 * it also updates the list of devices within its puddle in its parents list. This function deals with any service migration
	 * and placement that needs to occur. At the end of the function, it sends a node leave puddlehead to the old puddlehead. 
	 * The event should have the data of the node id. 
	 * @param ev
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
		puddleDevices.remove(deviceId);
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
	 * Add a puddlehead to the puddleBuddies.
	 * @param buddyId
	 */
	public void addPuddleBuddy(int buddyId){
		puddleBuddies.add(buddyId);
	}
	
	/**
	 * Remove a single puddle buddy puddlehead. 
	 * @param buddyId
	 */
	public void removePuddleBuddy(int buddyId){
		puddleBuddies.remove(buddyId);
	}
	
	/**
	 * Check if a puddlehead is a puddle buddy.
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
		childrenIds.remove(deviceId);
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
	
	public void addChildPuddle(int childId, List<Integer> childPuddle){
		childrenPuddles.put(childId, childPuddle);
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
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
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
	
	public void removeRunningServices(int deviceId){
		runningServices.remove(deviceId);
	}
	//getter and setter for linksMap
	public void setLinksMap(Map<Integer, Link> linksMap){
		this.linksMap = linksMap;
	}
	
	public Map<Integer, Link> getLinksMap(){
		return linksMap; 
	}

}
