/**
 * 
 */
package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.application.AppModule;
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
	
	
	
	
	
	// services running on all puddleDevices
	// remaining resources on all remaining puddleDevices
	// info about children puddleHeads and their puddles 

	/**
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
		case FogEvents.NODE_JOIN:
			processNodeJoin(ev); 
			break;
		case FogEvents.NODE_LEAVE:
			processNodeLeave(ev);
			break;
		case FogEvents.PLACE_SERVICES:
			processPlaceServices(ev);
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
	protected void processNodeJoin(SimEvent ev){
		FogNode node = (FogNode) ev.getData(); 
		
		node.setPuddleHeadId(this.getId());
		addPuddleDevice(node.getId());
		addPuddleDevicesCharacteristics(node.getId(), node.getDeviceCharactersitics());
		
	}
	
	
	protected void processNodeLeave(SimEvent ev){
		FogNode node = (FogNode) ev.getData();
		
		removePuddleDevice(node.getId()); 
		removePuddleDevicesCharacteristics(node.getId());
		removeRunningServices(node.getId());
	}
	
	protected void processPlaceServices(SimEvent ev){
		//TODO make this work 
		//update the running services table 
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
	public void addPuddleDevicesCharacteristics(int deviceId, FogDeviceCharacteristics characteristics) {
		puddleDevicesCharacteristics.put(deviceId, characteristics);
	}
	
	/**
	 * Remove a single node's characteristics. 
	 * @param deviceId
	 */
	public void removePuddleDevicesCharacteristics(int deviceId){
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

}
