/**
 * Title: PuddleSim
 * Description: PuddleSim is an extension to the iFogSim simulator
 */
package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.network.Link;
import org.fog.utils.FogEvents;
import org.fog.utils.Logger;
import org.fog.utils.Mobility;
import org.fog.utils.Rectangle;
import org.fog.utils.Vector;
import org.fog.utils.Point;
import org.fog.utils.Polygon;

/**
 * @author Jessica Knezha and Avi Rynderman
 * @version PuddleSim 1.0
 * @since June 22, 2017
 * 
 * The FogNode class is an extension of FogDevice with added features for PuddleSim, especially mobility. 
 *
 */
public class FogNode extends FogDevice {
	
	/**
	 * Mobility object for FogNode.
	 */
	private Mobility mobile = null;
	
	/**
	 * Used for debugging purposes. Adds a label onto the output. Note, only used in Logger.debug
	 */
	private static String LOG_TAG = "FOG_NODE";
	
	/**
	 * The delay between location updates.
	 */
	private static double delayBetweenLocationUpdates = 0;
	
	/**
	 * Used to check whether or not the device has started moving.
	 */
	private boolean moving = false;
	
	/**
	 * The id of the PuddleHead that this node belongs to. 
	 */
	protected int puddleHeadId;
	
	/**
	 * List of all the other nodes in this node's puddle.
	 */
	protected List<Integer> puddleBuddies; 
	
	/**
	 * Map of the links by their id with the link object. 
	 */
	protected Map<Integer, Link> linksMap;  
	
	/**
	 * Polygon denoting its area of coverage for connections to IoT devices (sensors and actuators)
	 */
	protected Polygon areaOfCoverage;
	
	/**
	 * Characteristics of this specific node housed in the object FogDeviceCharacteristics.
	 */
	protected FogDeviceCharacteristics myCharacteristics; 
	
	/**
	 * Boolean that says if this node has left the overall network.
	 */
	protected boolean gone = false;
	
	/**
	 * Level that the node is at. 
	 */
	protected int level; 
	
	/**
	 * Constructor for a FogNode
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param ratePerMips
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double ratePerMips) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, ratePerMips);
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
		myCharacteristics = characteristics; 
	}

	/**
	 * FogNode constructor with the addition to the basic constructor which takes in parameters for uplink and downlink bandwidth 
	 * as well as uplink latency  
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param uplinkBandwidth
	 * @param downlinkBandwidth
	 * @param uplinkLatency
	 * @param ratePerMips
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth,
			double uplinkLatency, double ratePerMips) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth,
				downlinkBandwidth, uplinkLatency, ratePerMips);
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
		myCharacteristics = characteristics; 
	}
	
	/**
	 * Constructor for a FogNode with mobility.
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param ratePerMips
	 * @param bounds
	 * @param coordinates
	 * @param movementVector
	 * @param isMobile
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double ratePerMips,
			Rectangle bounds, Point coordinates, Vector movementVector, boolean isMobile, int level) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, movementVector, isMobile);
		this.setMobilityDelay();
	 		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
		this.level = level;
		
		myCharacteristics = characteristics; 		
	}

	/**
	 * FogNode constructor with mobility and with the addition to the basic constructor which takes in parameters for uplink and downlink bandwidth 
	 * as well as uplink latency
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param uplinkBandwidth
	 * @param downlinkBandwidth
	 * @param uplinkLatency
	 * @param ratePerMips
	 * @param bounds
	 * @param coordinates
	 * @param movementVector
	 * @param isMobile  
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth,
			double uplinkLatency, double ratePerMips,
			Rectangle bounds, Point coordinates, Vector movementVector, boolean isMobile, int level) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth,
				downlinkBandwidth, uplinkLatency, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, movementVector, isMobile);
		this.setMobilityDelay();
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
		this.level = level; 
		
		myCharacteristics = characteristics; 	
	}
	
	/**
	 * Constructor for a FogNode with mobility.
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param ratePerMips
	 * @param bounds
	 * @param coordinates
	 * @param scalarVector
	 * @param isMobile
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double ratePerMips,
			Rectangle bounds, Point coordinates, double scalar, boolean isMobile, int level) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, scalar, isMobile);
		this.setMobilityDelay();
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
		this.level = level;
		
		myCharacteristics = characteristics; 
	}

	/**
	 * FogNode constructor with mobility and with the addition to the basic constructor which takes in parameters for uplink and downlink bandwidth 
	 * as well as uplink latency
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param uplinkBandwidth
	 * @param downlinkBandwidth
	 * @param uplinkLatency
	 * @param ratePerMips
	 * @param bounds
	 * @param coordinates
	 * @param scalar
	 * @param isMobile  
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth,
			double uplinkLatency, double ratePerMips,
			Rectangle bounds, Point coordinates, double scalar, boolean isMobile, int level) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth,
				downlinkBandwidth, uplinkLatency, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, scalar, isMobile);
		this.setMobilityDelay();
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
		this.level = level;
		
		myCharacteristics = characteristics; 
	}

	/**
	 * Gets the ID of the puddle head that this FogNode belongs to. 
	 * @return the puddleHeadId
	 */
	public int getPuddleHeadId() {
		return puddleHeadId;
	}

	/**
	 * Sets the ID of the puddle head that this FogNode belongs to. 
	 * @param puddleHeadId the puddleHeadId to set
	 */
	public void setPuddleHeadId(int puddleHeadId) {
		this.puddleHeadId = puddleHeadId;
	}
	
	/**
	 * Choose the mobility step delay
	 */
	private void setMobilityDelay(){
		// Minimum time is 0.1. 10 x gives 1 second per location update.
		FogNode.delayBetweenLocationUpdates = 10*CloudSim.getMinTimeBetweenEvents();
	}
	
	/**
	 * Updates the location and latency continually
	 * Note: it will send the process node move event only if the name of the global broker is "globalbroker"
	 * @param ev (SimEvent)
	 */
	protected void processUpdateLocation(SimEvent ev){
		// If the device is mobile, update the location and send an event to the queue to trigger it again
		if(mobile.isMobile()){
			mobile.updateLocation();
			send(super.getId(), FogNode.delayBetweenLocationUpdates, FogEvents.UPDATE_LOCATION);
			this.linksMap.forEach((k,v) -> {send(v.getId(), FogNode.delayBetweenLocationUpdates, FogEvents.UPDATE_LATENCY);
				Logger.debug("LINKMAP", getName(), "Loop");});
			
			//Send to global broker for processing. 
			int brokerId = CloudSim.getEntityId("globalbroker");
			if(brokerId > 0){
				send(brokerId, CloudSim.getMinTimeBetweenEvents(), FogEvents.PROCESS_NODE_MOVE, getId());
			}
			else{
				Logger.debug(LOG_TAG, "'globalbroker' is not a defined entity");
			}
		}
		Logger.debug(LOG_TAG, getName(), "Completed execution of move");
	}

	@Override
	protected void processOtherEvent(SimEvent ev) {
		// This kickstarts the movement. 
		if(!this.moving){
			this.moving = true;
			processUpdateLocation(ev);
		}			
		
		switch(ev.getTag()){
		case FogEvents.TUPLE_ARRIVAL:
			processTupleArrival(ev);
			break;
		case FogEvents.LAUNCH_MODULE:
			processModuleArrival(ev);
			break;
		case FogEvents.RELEASE_OPERATOR:
			processOperatorRelease(ev);
			break;
		case FogEvents.SENSOR_JOINED:
			processSensorJoining(ev);
			break;
		case FogEvents.SEND_PERIODIC_TUPLE:
			sendPeriodicTuple(ev);
			break;
		case FogEvents.APP_SUBMIT:
			processAppSubmit(ev);
			break;
		case FogEvents.ACTUATOR_JOINED:
			processActuatorJoined(ev);
			break;
		case FogEvents.RESOURCE_MGMT:
			manageResources(ev);
			break;
		case FogEvents.TUPLE_FINISHED:
			processTupleFinished(ev);
			break;
		case FogEvents.UPDATE_LOCATION:
			processUpdateLocation(ev);
			break;
		case FogEvents.NODE_LEAVE:
			processNodeLeave();
		default:
			break;
		}
	}
	
	//TODO finish this function please. 
	/**
	 * Maintenance work for a node when it leaves the overall network. This occurs when it has no puddlehead to connect to.
	 * This function sets gone to true. This is the only place that this should happen. 
	 * It removes itself from all of it's puddleBuddies lists of their buddies. It then sends an event to leave its current puddlehead.
	 * The puddlehead cares for all other maintenance within the network due to the node leaving. 
	 */
	public void processNodeLeave(){
		gone = true; 
		
		for(int i = 0; i < puddleBuddies.size(); i++){
			int buddyId = puddleBuddies.get(i);
			FogNode node = (FogNode) CloudSim.getEntity(buddyId);
			node.removePuddleBuddy(getId());
		}
		
		
		//This stuff might need to be altered to properly handle what happens when a node leaves the system completely. 
		send(puddleHeadId, CloudSim.getMinTimeBetweenEvents(), FogEvents.NODE_LEAVE_PUDDLEHEAD, getId()); 
	}
	
	/**
	 * @return list of puddle buddies
	 */
	public List<Integer> getPuddleBuddies(){
		return puddleBuddies;
	}
	
	/**
	 * @param puddleBuddies
	 */
	public void setPuddleBuddies(List<Integer> puddleBuddies){
		this.puddleBuddies = puddleBuddies;
	}
	 
	/**
	 * Add a single node to the puddle buddies
	 * @param buddyId
	 */
	public void addPuddleBuddy(int buddyId){
		puddleBuddies.add(buddyId);
	}
	
	/**
	 * Remove a single node from the puddle buddies
	 * @param buddyId
	 */
	public void removePuddleBuddy(int buddyId){
		puddleBuddies.remove((Integer)buddyId);
	}
	
	/**
	 * Check if the given node is one of my puddle buddies
	 * @param buddyId
	 * @return
	 */
	public boolean isMyPuddleBuddy(int buddyId){
		return puddleBuddies.contains(buddyId);
	}
	
	/**
	 * @param linksMap
	 */
	public void setLinksMap(Map<Integer, Link> linksMap){
		this.linksMap = linksMap;
	}
	
	/**
	 * @return the map of links connected to this node
	 */
	public Map<Integer, Link> getLinksMap(){
		return linksMap; 
	}
	
	/**
	 * Adds a single link to the map.
	 * @param linkId
	 * @param theLink
	 */
	public void addLinkToMap(int linkId, Link theLink){
		linksMap.put(linkId, theLink);
	}
	
	/**
	 * Removes a single link from the map.
	 * @param linkId
	 */
	public void removeLinkFromMap(int linkId){
		linksMap.remove(linkId);
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
	 * Gets the location of the fog device.
	 * @return locations of the fog device as a Point.
	 */
	public Point getLocation(){
		return this.mobile.getPoint();
	}
	
	/**
	 * Sets the location of the fog device.
	 * @param point 
	 */
	public void setLocation(Point point){
		this.mobile.setPoint(point);
	}
	
	/**
	 * This function changes the old direction of movement and velocity of the device.
	 * @param v new value for movement of device
	 */
	public void updateDirection(Vector v){
		this.mobile.updateDirection(v);
	}
	
	/**
	 * Determines if the device is mobile.
	 * @return a boolean indicating whether or not the device is mobile.
	 */
	public boolean isMobile() {
		return this.mobile.isMobile();
	}
	
	/**
	 * Sets the mobility of the device. If it is mobile, it will move. 
	 * @param isMobile
	 */
	public void setMobility(boolean isMobile) {
		this.mobile.setMobile(isMobile);
	}
	
	/**
	 * @return the node's characteristics
	 */
	public FogDeviceCharacteristics getDeviceCharactersitics(){
		return myCharacteristics; 
	}
	
	/**
	 * @return true if the node has left the network
	 */
	public boolean isGone(){
		return gone; 
	}

	/**
	 * @param gone the gone to set
	 */
	public void setGone(boolean gone) {
		this.gone = gone;
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
}
