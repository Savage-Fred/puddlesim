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

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 22, 2017
 *
 */
public class PuddleHead1 extends FogDevice {
	
	protected List<Integer> puddleDevices; 
	
	protected Map<Integer, FogDeviceCharacteristics> puddleDevicesCharacteristics; 
	

	/**
	 * Constructor for a PuddleHead. 
	 * @param name
	 * @param characteristics
	 * @param vmAllocationPolicy
	 * @param storageList
	 * @param schedulingInterval
	 * @param ratePerMips
	 * @throws Exception
	 */
	public PuddleHead1(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double ratePerMips) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, ratePerMips);
		// TODO Auto-generated constructor stub
		
		setPuddleDevices(new ArrayList<Integer>());
		setPuddleDevicesCharacteristics(new HashMap<Integer, FogDeviceCharacteristics>());
	}

	/**
	 * PuddleHead constructor with the addition to the basic constructor which takes in parameters for uplink and downlink bandwidth 
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
	public PuddleHead1(String name, FogDeviceCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList, double schedulingInterval, double uplinkBandwidth, double downlinkBandwidth,
			double uplinkLatency, double ratePerMips) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval, uplinkBandwidth,
				downlinkBandwidth, uplinkLatency, ratePerMips);
		// TODO Auto-generated constructor stub
		
		setPuddleDevices(new ArrayList<Integer>());
		setPuddleDevicesCharacteristics(new HashMap<Integer, FogDeviceCharacteristics>());
	}

	/**
	 * Gets the list of FogDevices which belong to this PuddleHead
	 * @return the puddleDevices
	 */
	public List<Integer> getPuddleDevices() {
		return puddleDevices;
	}

	/**
	 * Sets the list of FogDevices which belong to this PuddleHead
	 * @param puddleDevices the puddleDevices to set
	 */
	public void setPuddleDevices(List<Integer> puddleDevices) {
		this.puddleDevices = puddleDevices;
	}

	/**
	 * Gets the map of characteristics of the FogDevices in the puddle
	 * @return the puddleDevicesCharacteristics
	 */
	public Map<Integer, FogDeviceCharacteristics> getPuddleDevicesCharacteristics() {
		return puddleDevicesCharacteristics;
	}

	/**
	 * Sets the map of characteristics of the FogDevices in the puddle
	 * @param puddleDevicesCharacteristics the puddleDevicesCharacteristics to set
	 */
	public void setPuddleDevicesCharacteristics(Map<Integer, FogDeviceCharacteristics> puddleDevicesCharacteristics) {
		this.puddleDevicesCharacteristics = puddleDevicesCharacteristics;
	}

}
