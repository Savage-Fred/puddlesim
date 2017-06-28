package org.fog.network;

import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.entities.EndDevice;
import org.fog.entities.FogDevice;
import org.fog.entities.FogNode;

public class SimulationArchitecture extends PhysicalTopology{

	/**
	 * List of fog devices in the physical topology
	 */
	private List<Integer> fogDevices;
	/**
	 * List of fog nodes in the physical topology
	 */
	private List<Integer> fogNodes;
	/**
	 * List of end-devices in the physical topology
	 */
	private List<Integer> endDevices;
	/**
	 * List of switches in the physical topology
	 */
	private List<Integer> switches;
	/**
	 * List of links in the physical topology
	 */
	private List<Integer> links;
	
	public SimulationArchitecture(){
		super();
	}
	
	@Override
	public void addLink(int endpoint1, int endpoint2, double latency, double bandwidth) {
		Link newLink = new Link("link-"+endpoint1+"-"+endpoint2, latency, bandwidth, endpoint1, endpoint2);
		getLinks().add(newLink);
		links.add(newLink.getId());
		if (this.fogNodes.contains(endpoint1)) {
			FogNode device = (FogNode)CloudSim.getEntity(endpoint1);
			device.getLinksMap().put(newLink.getId(), newLink);
		} else if (this.fogDevices.contains(endpoint1)) {
			// TODO: add maps of links to fogDevices
		} else if (this.endDevices.contains(endpoint1)) {
			// TODO: add maps of links to endDevices 
		} else if (this.switches.contains(endpoint1)) {
			// TODO: add maps of links to switches			
		}
		
		if (this.fogNodes.contains(endpoint2)) {
			FogNode device = (FogNode)CloudSim.getEntity(endpoint2);
			device.getLinksMap().put(newLink.getId(), newLink);
		} else if (this.fogDevices.contains(endpoint2)) {
			// TODO: add maps of links to fogDevices
		} else if (this.endDevices.contains(endpoint2)) {
			// TODO: add maps of links to endDevices 
		} else if (this.switches.contains(endpoint2)) {
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
		fogDevices.add(dev.getId());
	}
	
	/**
	 * Add fog node to physical topology
	 * @param dev
	 */
	@Override
	public void addFogNode(FogNode dev) {
		getFogNodes().add(dev);
		// Add device ID to integer list
		fogNodes.add(dev.getId());
	}


	/**
	 * Add end-device to physical topology
	 * @param dev
	 */
	@Override
	public void addEndDevice(EndDevice dev) {
		getEndDevices().add(dev);
		// Add device ID to integer list
		endDevices.add(dev.getId());
	}
	
	/**
	 * Add switch to physical topology
	 * @param dev
	 */
	@Override
	public void addSwitch(Switch sw) {
		getSwitches().add(sw);
		// Add device ID to integer list
		switches.add(sw.getId());
	}
}
