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
 */
public class FogNode extends FogDevice {
	
	/**
	 * Mobility object for FogNode.
	 */
	protected Mobility mobile = null;
	/**
	 * Used for debugging purposes. Adds a label onto the output. Note, only used in Logger.debug
	 */
	private static String LOG_TAG = "FOG_NODE";
	/**
	 * Used to check whether or not the device has started moving.
	 */
	private boolean moving = false;
	
	protected int puddleHeadId;
	
	/**
	 * List of all the other devices in this device's puddle 
	 */
	protected List<Integer> puddleBuddies; 
	
	protected Map<Integer, Link> linksMap; 
	
	protected Polygon areaOfCoverage; 
	
	protected FogDeviceCharacteristics myCharacteristics; 

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
		// TODO Auto-generated constructor stub
		
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
		// TODO Auto-generated constructor stub
		
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
			Rectangle bounds, Point coordinates, Vector movementVector, boolean isMobile) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, movementVector, isMobile);
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
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
			Rectangle bounds, Point coordinates, Vector movementVector, boolean isMobile) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth,
				downlinkBandwidth, uplinkLatency, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, movementVector, isMobile);
		
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
	 * @param scalarVector
	 * @param isMobile
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double ratePerMips,
			Rectangle bounds, Point coordinates, double scalar, boolean isMobile) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, scalar, isMobile);
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
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
			Rectangle bounds, Point coordinates, double scalar, boolean isMobile) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth,
				downlinkBandwidth, uplinkLatency, ratePerMips);
		this.mobile = new Mobility(bounds, coordinates, scalar, isMobile);
		
		//puddlebuddies addition 
		setPuddleBuddies(new ArrayList<Integer>());
		
		//initialize map of links
		setLinksMap(new HashMap<Integer, Link>()); 
		
		myCharacteristics = characteristics; 
	}

	/**
	 * Gets the ID of the puddle head that this FogNode belongs to 
	 * @return the puddleHeadId
	 */
	public int getPuddleHeadId() {
		return puddleHeadId;
	}

	/**
	 * Sets the ID of the puddle head that this FogNode belongs to 
	 * @param puddleHeadId the puddleHeadId to set
	 */
	public void setPuddleHeadId(int puddleHeadId) {
		this.puddleHeadId = puddleHeadId;
	}
	/**
	 * Updates the location continually
	 */
	protected void processUpdateLocation(SimEvent ev){
		// If the device is mobile, update the location and send an event to the queue to trigger it again
		if(mobile.isMobile()){
			send(super.getId(), 100*CloudSim.getMinTimeBetweenEvents(), FogEvents.UPDATE_LOCATION);
			mobile.updateLocation();
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
		default:
			break;
		}
	}
	
	//getter and setter for puddleBuddies
	public List<Integer> getPuddleBuddies(){
		return puddleBuddies;
	}
	public void setPuddleBuddies(List<Integer> puddleBuddies){
		this.puddleBuddies = puddleBuddies;
	}
	 
	//add a node to the puddleBuddies
	public void addPuddleBuddy(int buddyId){
		puddleBuddies.add(buddyId);
	}
	//remove a node from puddleBuddies
	public void removePuddleBuddy(int buddyId){
		puddleBuddies.remove(buddyId);
	}
	//check if a node is a puddle buddy 
	public boolean isMyPuddleBuddy(int buddyId){
		return puddleBuddies.contains(buddyId);
	}
	
	//getter and setter for linksMap
	public void setLinksMap(Map<Integer, Link> linksMap){
		this.linksMap = linksMap;
	}
	
	public Map<Integer, Link> getLinksMap(){
		return linksMap; 
	}

	/**
	 * Gets the area of coverage of where this device can connect 
	 * @return the areaOfCoverage
	 */
	public Polygon getAreaOfCoverage() {
		return areaOfCoverage;
	}

	/**
	 * Sets the area of coverage of where this device can connect
	 * @param areaOfCoverage the areaOfCoverage to set
	 */
	public void setAreaOfCoverage(Polygon areaOfCoverage) {
		this.areaOfCoverage = areaOfCoverage;
	}
	
	public FogDeviceCharacteristics getDeviceCharactersitics(){
		return myCharacteristics; 
	}
}
