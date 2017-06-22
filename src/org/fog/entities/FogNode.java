/**
 * Title: PuddleSim
 * Description: PuddleSim is an extension to the iFogSim simulator
 */
package org.fog.entities;

import java.util.List;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 22, 2017
 *
 */
public class FogNode extends FogDevice {
	
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

}
