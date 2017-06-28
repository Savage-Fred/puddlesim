package org.fog.network;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.entities.EndDevice;
import org.fog.entities.FogDevice;
import org.fog.entities.FogNode;
import org.fog.utils.Logger;

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
		setSwitches(new ArrayList<Switch>());
		setEndDevices(new ArrayList<EndDevice>());
		this.fogDeviceIDs = new ArrayList<Integer>();
		this.fogNodeIDs = new ArrayList<Integer>();
		this.endDeviceIDs = new ArrayList<Integer>();
		this.switchIDs = new ArrayList<Integer>();
		this.linkIDs = new ArrayList<Integer>();
	}
	
	@Override
	public void addLink(int endpoint1, int endpoint2, double latency, double bandwidth) {
		Link newLink = new Link("link-"+endpoint1+"-"+endpoint2, latency, bandwidth, endpoint1, endpoint2);
		getLinks().add(newLink);
		linkIDs.add(newLink.getId());
		System.out.println("FOG_NODE: " + this.fogNodeIDs.contains(endpoint1));
		if (this.fogNodeIDs.contains(endpoint1)) {
			FogNode device = (FogNode)CloudSim.getEntity(endpoint1);
			device.getLinksMap().put(newLink.getId(), newLink);
			System.out.println("Link: " + device.getId() + " <==>" + newLink.getId());
		} else if (this.fogDeviceIDs.contains(endpoint1)) {
			// TODO: add maps of links to fogDevices
		} else if (this.endDeviceIDs.contains(endpoint1)) {
			// TODO: add maps of links to endDevices 
		} else if (this.switchIDs.contains(endpoint1)) {
			// TODO: add maps of links to switches			
		}
		System.out.println("FOG_NODE: " + this.fogNodeIDs.contains(endpoint2));
		if (this.fogNodeIDs.contains(endpoint2)) {
			FogNode device = (FogNode)CloudSim.getEntity(endpoint2);
			device.getLinksMap().put(newLink.getId(), newLink);
		} else if (this.fogDeviceIDs.contains(endpoint2)) {
			// TODO: add maps of links to fogDevices
		} else if (this.endDeviceIDs.contains(endpoint2)) {
			// TODO: add maps of links to endDevices 
		} else if (this.switchIDs.contains(endpoint2)) {
			// TODO: add maps of links to switches			
		}
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
	}
	
	/**
	 * Add fog node to physical topology
	 * @param dev
	 */
	@Override
	public void addFogNode(FogNode dev) {
		getFogNodes().add(dev);
		// Add device ID to integer list
		fogNodeIDs.add(dev.getId());
		System.out.println("Added FOG_NODE: " + dev.getId());
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
	}
}
