/**
 * Title: PuddleSim
 * Description: PuddleSim is an extension to the iFogSim simulator
 */
package org.fog.entities;

import java.util.List;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.utils.FogEvents;
import org.fog.utils.Mobility;
import org.fog.utils.Rectangle;
import org.fog.utils.Vector;

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 22, 2017
 *
 */
public class FogNode extends FogDevice {
	
	/**
	 * Mobility object for FogNode.
	 */
	protected Mobility mobile = null;
	
	
	protected int puddleHeadId; 

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
	 * @param latitude
	 * @param longitude
	 * @param movementVector
	 * @param isMobile
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double ratePerMips,
			Rectangle bounds, double latitude, double longitude, Vector movementVector, boolean isMobile) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, ratePerMips);
		this.mobile = new Mobility(bounds, latitude, longitude, movementVector, isMobile);
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
	 * @param latitude
	 * @param longitude
	 * @param movementVector
	 * @param isMobile  
	 * @throws Exception
	 */
	public FogNode(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth,
			double uplinkLatency, double ratePerMips,
			Rectangle bounds, double latitude, double longitude, Vector movementVector, boolean isMobile) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth,
				downlinkBandwidth, uplinkLatency, ratePerMips);
		this.mobile = new Mobility(bounds, latitude, longitude, movementVector, isMobile);
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
			send(getLinkId(), CloudSim.getMinTimeBetweenEvents(), FogEvents.UPDATE_LOCATION);
			mobile.updateLocation();
		}
	}

	@Override
	protected void processOtherEvent(SimEvent ev) {
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
}
