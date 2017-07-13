package org.fog.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.Application;
import org.fog.entities.EndDevice;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.FogNode;
import org.fog.entities.PuddleHead;
import org.fog.entities.Sensor;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.AppModuleScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.Logger;
import org.fog.utils.Point;
import org.fog.utils.Polygon;
import org.fog.utils.Rectangle;
import org.fog.utils.Vector;

/**
 * @author Avi Rynderman
 * @version PuddleSim 1.0
 * @since REU Summer 2017 - Parallel and Distributed Computing
 * 
 * This class extends PhysicalTopology to allow the creation of the new hierarchical architecture.
 * Responsibilities of this class include processing the output of the Voronoi program, and dynamic creation
 * of the simulation architecture.
 * 
 * See SimArchExample1.java for a detailed example on how this class is used. 
 *
 */

public class SimulationArchitecture extends PhysicalTopology{
	private static String LOG_TAG = "SIMUL_ARCH";
	/**
	 * Singleton object that needs to be manipulated in the example script
	 */
	private static SimulationArchitecture instance = null;
	
	public static SimulationArchitecture getInstance() {
		if (instance == null) 
			instance = new SimulationArchitecture();
		return instance;
	}
	
	/**
	 * List of fog devices in the physical topology
	 */
	private List<Integer> fogDeviceIDs;
	/**
	 * List of fog nodes in the physical topology
	 */
	private List<Integer> fogNodeIDs;
	/**
	 * List of puddleHeads in the physical topology
	 */
	private List<Integer> puddleHeadIDs;
	/**
	 * List of end-devices in the physical topology
	 */
	private List<Integer> endDeviceIDs;
	/**
	 * List of switches in the physical topology
	 */
	private List<Integer> switchIDs;
	/**
	 * List of links in the physical topology
	 */
	private List<Integer> linkIDs;
	
	protected SimulationArchitecture(){
		setLinks(new ArrayList<Link>());
		setFogDevices(new ArrayList<FogDevice>());
		setFogNodes(new ArrayList<FogNode>());
		setPuddleHeads(new ArrayList<PuddleHead>());
		setSwitches(new ArrayList<Switch>());
		setEndDevices(new ArrayList<EndDevice>());
		this.fogDeviceIDs = new ArrayList<Integer>();
		this.fogNodeIDs = new ArrayList<Integer>();
		this.puddleHeadIDs = new ArrayList<Integer>();
		this.endDeviceIDs = new ArrayList<Integer>();
		this.switchIDs = new ArrayList<Integer>();
		this.linkIDs = new ArrayList<Integer>();
	}
	
	
	/**
	 * This function creates the architecture.
	 * @param userId
	 * @param appId
	 * @param application
	 */
	public void CreateNewTopology(String fileName, int userId, String appId, Application application) {
		
	}
	
	/**
	 * This function sets up the file for read in the architecture.
	 * @param userId
	 * @param appId
	 * @param application
	 */
	private void ReadVoronoi(String fileName) {
		
	}
	
	/**
	 * This function adds links and link IDs to the link maps in each device. Links
	 * connect 2 devices. The link map is used so that a device knows what links
	 * it's connected to for latency update features.
	 * @param endpoint1 ID of 1st endpoint of link to be created
	 * @param endpoint2 ID of 2nd endpoint of link to be created
	 * @param latency latency of link, same for both directions
	 * @param bandwidth one-directional bandwidth of link. Both directions of communication will receive equal BW equal to this parameter.
	 */
	@Override
	public void addLink(int endpoint1, int endpoint2, double latency, double bandwidth) {
		Link newLink = new Link("link-"+endpoint1+"-"+endpoint2, latency, bandwidth, endpoint1, endpoint2);
		getLinks().add(newLink);
		linkIDs.add(newLink.getId());
		System.out.println("Added Link: " + newLink.getId() +
							". Connects: " + endpoint1 + "<=" + newLink.getId() + "=>" + endpoint2);
		
		if (this.fogNodeIDs.contains(endpoint1)) {
			FogNode device = (FogNode)CloudSim.getEntity(endpoint1);
			device.addLinkToMap(newLink.getId(), newLink);
			System.out.println("Fog Node Link: " + device.getId() + " <=" + newLink.getId());
		} 
		else if (this.puddleHeadIDs.contains(endpoint1)) {
			PuddleHead device = (PuddleHead)CloudSim.getEntity(endpoint1);
			device.addLinkToMap(newLink.getId(), newLink);
			System.out.println("Puddlehead Link: " + device.getId() + " <=" + newLink.getId());
		} 
		else if (this.fogDeviceIDs.contains(endpoint1)) {
			// TODO: add maps of links to fogDevices
		} 
		else if (this.endDeviceIDs.contains(endpoint1)) {
			EndDevice device = (EndDevice)CloudSim.getEntity(endpoint1);
			device.setLinkId(newLink.getId());
			device.setEdgeSwitchId(endpoint2);
			System.out.println("EndDevice Link: " + device.getId() + " <=" + newLink.getId());
		} 
		else if (this.switchIDs.contains(endpoint1)) {
			// TODO: add maps of links to switches			
		}
		
		if (this.fogNodeIDs.contains(endpoint2)) {
			FogNode device = (FogNode)CloudSim.getEntity(endpoint2);
			device.addLinkToMap(newLink.getId(), newLink);
			System.out.println("Fog Node Link: " + device.getId() + " <=" + newLink.getId());
		} 
		else if (this.puddleHeadIDs.contains(endpoint2)) {
			PuddleHead device = (PuddleHead)CloudSim.getEntity(endpoint2);
			device.addLinkToMap(newLink.getId(), newLink);
			System.out.println("Puddlehead Link: " + device.getId() + " <=" + newLink.getId());
		} 
		else if (this.fogDeviceIDs.contains(endpoint2)) {
			// TODO: add maps of links to fogDevices
		} 
		else if (this.endDeviceIDs.contains(endpoint2)) {
			EndDevice device = (EndDevice)CloudSim.getEntity(endpoint2);
			device.setLinkId(newLink.getId());
			device.setEdgeSwitchId(endpoint1);
			System.out.println("EndDevice Link: " + device.getId() + " <=" + newLink.getId()); 
		} 
		else if (this.switchIDs.contains(endpoint2)) {
			// TODO: add maps of links to switches			
		}
	}	

	/**
	 * This function validates the physical topology on start of simulation. 
	 * Note, it only checks that a fog node is connected by one parent puddlehead.
	 * <p>
	 * <b>IF THIS FUNCTION IS USED, DO NOT USE SWITCHES </b>
	 * Switches require their routing tables be set up, and that functionality
	 * exists as part of PhysicalTopology.validateTopology()  
	 * <ul>
	 * <li> No self-loop link should be present.
	 * <li> Each fog device should be connected to network by a unique link.
	 * 
	 * </ul>
	 * @return true if topology is valid
	 */
	public boolean validatePuddlesimTopology() {
		return true;
		// return super.validateTopology();
	}
	
	/** 
	 * Makes the physical topology ready.
	 * <b>IF THIS FUNCTION IS USED, DO NOT USE SWITCHES </b>
	 * Switches require their routing tables be set up, and that functionality
	 * exists as part of PhysicalTopology.setUpEntities()
	 */
	public void setUpPuddlesimEntities() {
		// Harmless function, just informs a fog device what it's link is. This link will be the puddlehead.
		assignLinksToFogDevices();
		// Harmless function, just informs a fog device what it's link is. This link will be the fognode.
		assignLinksToEndDevices();
		// The following fucntions will be taken care of internal to the puddlehead which is our main coordinator 
		// calculateAdjacentEntities();
		// calculateNeighbourSwitches();
		printAdjacentEntities();
		// See above comment
		// calculateRoutingTables();
	}
	
	/**
	 * Add fog device to physical topology
	 * @param dev
	 */
	@Override
	public void addFogDevice(FogDevice dev) {
		getFogDevices().add(dev);
		// Add device ID to integer list
		fogDeviceIDs.add(dev.getId());
		System.out.println("Added Fog Device: " + dev.getId());
	}
	
	/**
	 * Add fog node to physical topology
	 * @param dev
	 */
	@Override
	public void addFogNode(FogNode dev) {
		getFogNodes().add(dev);
		getFogDevices().add((FogDevice)dev);
		// Add device ID to integer list
		fogNodeIDs.add(dev.getId());
		fogDeviceIDs.add(dev.getId());
		System.out.println("Added Fog Node: " + dev.getId());
	}
	
	/**
	 * Add ouddlehead to physical topology
	 * @param dev
	 */
	@Override
	public void addPuddleHead(PuddleHead dev) {
		getPuddleHeads().add(dev);
		// Add device ID to integer list
		puddleHeadIDs.add(dev.getId());
		System.out.println("Added PuddleHead: " + dev.getId());
	}
	
	/**
	 * Add end-device to physical topology
	 * @param dev
	 */
	@Override
	public void addEndDevice(EndDevice dev) {
		getEndDevices().add(dev);
		// Add device ID to integer list
		endDeviceIDs.add(dev.getId());
		System.out.println("Added End Device: " + dev.getId());
	}
	
	/**
	 * Add switch to physical topology
	 * @param dev
	 */
	@Override
	public void addSwitch(Switch sw) {
		getSwitches().add(sw);
		// Add device ID to integer list
		switchIDs.add(sw.getId());
		System.out.println("Added Switch: " + sw.getId());
	}
	
	/**
	 * Creates a vanilla fogNode
	 * @param nodeName name of the device to be used in simulation
	 * @param mips MIPS
	 * @param ram RAM
	 * @param upBw uplink bandwidth
	 * @param downBw downlink bandwidth
	 * @param level hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @param storage
	 * @param level
	 * @return
	 */
	public static FogNode createFogNode(String nodeName, boolean isCloud, long mips,
			int ram, double ratePerMips, double busyPower, double idlePower, long storage,
			int bw, double costProcessing, double costPerMem, double costPerStorage, double costPerBw,
			Rectangle bounds, Point coordinates,  Vector direction, int level) {
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new AppModuleScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(isCloud, 
				arch, os, vmm, host, time_zone, costProcessing, costPerMem,
				costPerStorage, costPerBw);
		
		FogNode fognode = null;
		try {
			/*
			fognode = new FogNode(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, ratePerMips,
					bounds, 0, 0, direction, true);
					*/
			fognode = new FogNode(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, ratePerMips,
					bounds, coordinates, direction, true, level);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fognode;
	}
	
	/**
	 * Creates a vanilla FogDevice
	 * @param nodeName name of the device to be used in simulation
	 * @param mips MIPS
	 * @param ram RAM
	 * @param upBw uplink bandwidth
	 * @param downBw downlink bandwidth
	 * @param level hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @param storage
	 * @return
	 */
	public static FogDevice createFogDevice(String nodeName, boolean isCloud, long mips,
			int ram, double ratePerMips, double busyPower, double idlePower, long storage,
			int bw, double costProcessing, double costPerMem, double costPerStorage, double costPerBw) {
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();

		PowerHost host = new PowerHost(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerOverbooking(bw),
				storage,
				peList,
				new AppModuleScheduler(peList),
				new FogLinearPowerModel(busyPower, idlePower)
			);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(isCloud, 
				arch, os, vmm, host, time_zone, costProcessing, costPerMem,
				costPerStorage, costPerBw);
		
		FogDevice fogDevice = null;
		try {
			fogDevice = new FogDevice(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fogDevice;
	}
	
	/**
	 * Creates a vanilla PuddleHead
	 * @return
	 */
	public static PuddleHead createPuddleHead(String nodeName, Polygon areaOfCoverage, Point location, int level) {

		PuddleHead puddlehead = null;
		try {
			puddlehead = new PuddleHead(nodeName, areaOfCoverage, location, level);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return puddlehead;
	}

	/**
	 * @return the fogDeviceIDs
	 */
	public List<Integer> getFogDeviceIDs() {
		return fogDeviceIDs;
	}

	/**
	 * @param fogDeviceIDs the fogDeviceIDs to set
	 */
	public void setFogDeviceIDs(List<Integer> fogDeviceIDs) {
		this.fogDeviceIDs = fogDeviceIDs;
	}

	/**
	 * @return the fogNodeIDs
	 */
	public List<Integer> getFogNodeIDs() {
		return fogNodeIDs;
	}

	/**
	 * @param fogNodeIDs the fogNodeIDs to set
	 */
	public void setFogNodeIDs(List<Integer> fogNodeIDs) {
		this.fogNodeIDs = fogNodeIDs;
	}

	/**
	 * @return the puddleHeadIDs
	 */
	public List<Integer> getPuddleHeadIDs() {
		return puddleHeadIDs;
	}

	/**
	 * @param puddleHeadIDs the puddleHeadIDs to set
	 */
	public void setPuddleHeadIDs(List<Integer> puddleHeadIDs) {
		this.puddleHeadIDs = puddleHeadIDs;
	}

	/**
	 * @return the endDeviceIDs
	 */
	public List<Integer> getEndDeviceIDs() {
		return endDeviceIDs;
	}

	/**
	 * @param endDeviceIDs the endDeviceIDs to set
	 */
	public void setEndDeviceIDs(List<Integer> endDeviceIDs) {
		this.endDeviceIDs = endDeviceIDs;
	}

	/**
	 * @return the switchIDs
	 */
	public List<Integer> getSwitchIDs() {
		return switchIDs;
	}

	/**
	 * @param switchIDs the switchIDs to set
	 */
	public void setSwitchIDs(List<Integer> switchIDs) {
		this.switchIDs = switchIDs;
	}

	/**
	 * @return the linkIDs	 */
	public List<Integer> getLinkIDs() {
		return linkIDs;
	}

	/**
	 * @param linkIDs the linkIDs to set
	 */
	public void setLinkIDs(List<Integer> linkIDs) {
		this.linkIDs = linkIDs;
	}
	
}
