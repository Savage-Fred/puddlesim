/**
 * 
 */
package org.fog.simtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.entities.Actuator;
import org.fog.entities.Sensor;
import org.fog.entities.EndDevice;
import org.fog.entities.FogDevice;
import org.fog.utils.Logger;
import org.fog.network.Link; 
import org.fog.entities.FogNode; 
import org.fog.entities.PuddleHead2; 

/**
 * @author Jessica Knezha
 * @version PuddleSim 1.0
 * @since June 23, 2017
 *
 */
public class Architecture {
	private List<FogDevice> fogDevices; 
	private List<FogNode> fogNodes;
	private List<PuddleHead2> puddleHeads; 
	//private List<EndDevice> endDevices;
	//private List<Switch> switches; 
	private List<Actuator> actuators;
	private List<Sensor> sensors;
	private List<Link> links; 
	
	public Architecture() {
		fogDevices = new ArrayList<FogDevice>();
		fogNodes = new ArrayList<FogNode>(); 
		puddleHeads = new ArrayList<PuddleHead2>(); 
		actuators = new ArrayList<Actuator>();
		sensors = new ArrayList<Sensor>();
		links = new ArrayList<Link>(); 
	}
	
	public void addFogNode(FogNode node){
		
	}
	
}
