package org.fog.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.entities.Actuator;
import org.fog.entities.EndDevice;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.FogNode;
import org.fog.entities.PuddleHead;
import org.fog.entities.Sensor;
import org.fog.entities.Tuple;
import org.fog.network.EdgeSwitch;
import org.fog.network.PhysicalTopology;
import org.fog.network.SimulationArchitecture;
import org.fog.network.Switch;
import org.fog.placement.ModulePlacementOnlyCloud;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.AppModuleScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.Logger;
import org.fog.utils.Point;
import org.fog.utils.Polygon;
import org.fog.utils.Rectangle;
import org.fog.utils.TimeKeeper;
import org.fog.utils.Vector;
import org.fog.utils.distribution.DeterministicDistribution;

/**
 * Class to implement the following topology.
 *  SW2----FD1	|	    MODULE
 *  |			|	      /\
 *  SW1			|	     /  \
 *  |			|	    S    A
 *  SW0----FD0	|	
 *  |			|	
 * DEV			|	
 * /\			|	
 * S A			|	
 * @author Harshit Gupta & Avi Rynderman
 *
 */
public class SimArchExample1 {
	static List<FogDevice> fogDevices = new ArrayList<FogDevice>();
	static List<FogNode> fogNodes = new ArrayList<FogNode>();
	static List<PuddleHead> puddleHeads = new ArrayList<PuddleHead>();
	static List<Sensor> sensors = new ArrayList<Sensor>();
	static List<Actuator> actuators = new ArrayList<Actuator>();
	
	public static void main(String[] args) {

		Log.printLine("Starting SimArch...");
		Logger.ENABLED = false;
		Logger.enableTag("FOG_DEVICE");
		Logger.enableTag("FOG_NODE");
		Logger.enableTag("SWITCH");
		Logger.enableTag("LINK");
		
		try {
			Log.disable();
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			String appId = "simple_app"; // identifier of the application
			
			FogBroker broker = new FogBroker("broker");
			
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			
			createSimulationArchitecture(broker.getId(), appId, application);
			
			broker.setFogDeviceIds(getIds(fogDevices));
			broker.setSensorIds(getIds(sensors));
			broker.setActuatorIds(getIds(actuators));
			
			broker.submitApplication(application, 0, 
					new ModulePlacementOnlyCloud(fogDevices, sensors, actuators, application));
			
			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			Log.printLine("VRGame finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	private static void createSimulationArchitecture(int userId, String appId, Application application) {
		FogDevice fd1 = SimulationArchitecture.createFogDevice("FD1", true, 102400, 
																4000, 0.01, 103, 83.25, 10000000,
																1000000, 3.0, 0.05, 0.001, 0.0);
		FogDevice fd0 = SimulationArchitecture.createFogDevice("FD0", false, 102400, 
																4000, 0.01, 103, 83.25, 10000000,
																1000000, 3.0, 0.05, 0.001, 0.0);
		Switch sw0 = new EdgeSwitch("SW0");
		Switch sw1 = new Switch("SW1");
		Switch sw2 = new Switch("SW2");
		EndDevice dev = new EndDevice("DEV");
		
		FogNode fd2 = SimulationArchitecture.createFogNode("FD2", false, 102400, 
									4000, 0.01, 103, 83.25, 10000000,
									1000000, 3.0, 0.05, 0.001, 0.0,
									new Rectangle(1000, 1000), new Vector(1,1));
		
		// PuddleHead attempt
		double[] xcor = {0.0, 0, 100, 100};
		double[] ycor = {0.0, 100, 0, 100};
		Polygon areaOfCoverage = null;
		try {
			areaOfCoverage = new Polygon(xcor, ycor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location = new Point(10, 10);
		PuddleHead ph0 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD", areaOfCoverage, location, 1);
		
		int transmissionInterval = 5000;
		Sensor sensor = new Sensor("s-0", "SENSED_DATA", userId, appId, new DeterministicDistribution(transmissionInterval), application); // inter-transmission time of EEG sensor follows a deterministic distribution
		sensors.add(sensor);
		Actuator actuator = new Actuator("a-0", userId, appId, "ACTION", application);
		actuators.add(actuator);
		dev.addSensor(sensor);
		dev.addActuator(actuator);
		
		SimulationArchitecture.getInstance().addFogDevice(fd0);
		SimulationArchitecture.getInstance().addFogDevice(fd1);
		SimulationArchitecture.getInstance().addFogNode(fd2);
		SimulationArchitecture.getInstance().addPuddleHead(ph0);
		SimulationArchitecture.getInstance().addSwitch(sw0);
		SimulationArchitecture.getInstance().addSwitch(sw1);
		SimulationArchitecture.getInstance().addSwitch(sw2);
		SimulationArchitecture.getInstance().addEndDevice(dev);
		fogDevices.add(fd0);
		fogDevices.add(fd1);
		fogDevices.add(fd2);
		
		// Now connecting entities with Links
		SimulationArchitecture.getInstance().addLink(dev.getId(), sw0.getId(), 10, 1000);
		SimulationArchitecture.getInstance().addLink(sw0.getId(), sw1.getId(), 15, 1000);
		SimulationArchitecture.getInstance().addLink(sw0.getId(), fd0.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(sw1.getId(), sw2.getId(), 20, 1000);
		SimulationArchitecture.getInstance().addLink(sw2.getId(), fd1.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(sw2.getId(), fd2.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph0.getId(), fd2.getId(), 2, 1000);
		
		if (SimulationArchitecture.getInstance().validateTopology()) {
			System.out.println("Topology validation successful");
			SimulationArchitecture.getInstance().setUpEntities();
			
		} else {
			System.out.println("Topology validation UNsuccessful");
			System.exit(1);
		}
		
	}

	public static List<Integer> getIds(List<? extends SimEntity> entities) {
		List<Integer> ids = new ArrayList<Integer>();
		for (SimEntity entity : entities) {
			ids.add(entity.getId());
		}
		return ids;
	}
	
	/**
	 * Creates a vanilla fog device
	 * @param nodeName name of the device to be used in simulation
	 * @param mips MIPS
	 * @param ram RAM
	 * @param upBw uplink bandwidth
	 * @param downBw downlink bandwidth
	 * @param level hierarchy level of the device
	 * @param ratePerMips cost rate per MIPS used
	 * @param busyPower
	 * @param idlePower
	 * @return
	 */
	private static FogDevice createFogDevice(String nodeName, boolean isCloud, long mips,
			int ram, double ratePerMips, double busyPower, double idlePower) {
		
		List<Pe> peList = new ArrayList<Pe>();

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips))); // need to store Pe id and MIPS Rating

		int hostId = FogUtils.generateEntityId();
		long storage = 10000000; // host storage
		int bw = 1000000;

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
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(isCloud, 
				arch, os, vmm, host, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		FogDevice fogdevice = null;
		try {
			// TODO Check about scheduling interval
			fogdevice = new FogDevice(nodeName, characteristics, 
					new AppModuleAllocationPolicy(hostList), storageList, 10, ratePerMips);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fogdevice;
	}
	
	/**
	 * Function to create the EEG Tractor Beam game application in the DDF model. 
	 * @param appId unique identifier of the application
	 * @param userId identifier of the user of the application
	 * @return
	 */
	private static Application createApplication(String appId, int userId){
		
		Application application = Application.createApplication(appId, userId); // creates an empty application model (empty directed graph)
		
		/*
		 * Adding modules (vertices) to the application model (directed graph)
		 */
		application.addAppModule("MODULE", 1000, 100);
		
		/*
		 * Connecting the application modules (vertices) in the application model (directed graph) with edges
		 */
		application.addAppEdge("SENSED_DATA", "MODULE", 30000, 10*1024, "SENSED_DATA", Tuple.UP, AppEdge.SENSOR);
		application.addAppEdge("MODULE", "ACTION", 1000, 1*1024, "ACTION", Tuple.DOWN, AppEdge.ACTUATOR);  // adding edge from Client module to Display (actuator) carrying tuples of type SELF_STATE_UPDATE
		
		/*
		 * Defining the input-output relationships (represented by selectivity) of the application modules. 
		 */
		application.addTupleMapping("MODULE", "SENSED_DATA", "ACTION", new FractionalSelectivity(1.0)); 
		
		final AppLoop loop1 = new AppLoop(new ArrayList<String>(){{add("SENSED_DATA");add("MODULE");add("ACTION");}});
		System.out.println("LOOP ID at creation = "+loop1.getLoopId());
		List<AppLoop> loops = new ArrayList<AppLoop>(){{add(loop1);}};
		application.setLoops(loops);
		
		return application;
	}
	
	
}