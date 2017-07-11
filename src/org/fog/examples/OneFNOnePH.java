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
import org.fog.entities.GlobalBroker;
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
public class OneFNOnePH {
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
			
			GlobalBroker broker = new GlobalBroker("globalbroker");
			
			Application application = createApplication(appId, broker.getId());
			application.setUserId(broker.getId());
			
			// Create Architecture/Topology
			createSimulationArchitecture(broker.getId(), appId, application);
			
			broker.setup(SimulationArchitecture.getInstance().getPuddleHeadIDs(), SimulationArchitecture.getInstance().getFogNodeIDs());
			broker.setFogDeviceIds(getIds(SimulationArchitecture.getInstance().getFogDevices()));
			broker.setSensorIds(getIds(SimulationArchitecture.getInstance().getSensors()));
			broker.setActuatorIds(getIds(SimulationArchitecture.getInstance().getActuators()));
			
			broker.submitApplication(application, 0, 
					new ModulePlacementOnlyCloud(SimulationArchitecture.getInstance().getFogDevices(), 
							SimulationArchitecture.getInstance().getSensors(),
							SimulationArchitecture.getInstance().getActuators(),
							application));
			
			TimeKeeper.getInstance().setSimulationStartTime(Calendar.getInstance().getTimeInMillis());

			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			Log.printLine("VRGame finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}

	// Creates the architecture
	/**
	 * This function interacts with SimulationArchitecture and creates the architecture. As it stands now,
	 * the topology is manually generated. In the future, it'll read in a file from the Voronoi generator
	 * and instantiate items based on the file.
	 * Step 1: Create the devices. (FogDevice, Switch, FogNode...)
	 * Step 2: Add them to the lists. (SimulationArchitecture.getInstance().add__)
	 * Step 3: 
	 * @param userId
	 * @param appId
	 * @param application
	 */
	private static void createSimulationArchitecture(int userId, String appId, Application application) {
		
		EndDevice dev = new EndDevice("DEV");
		int transmissionInterval = 5000;
		Sensor sensor = new Sensor("s-0", "SENSED_DATA", userId, appId, new DeterministicDistribution(transmissionInterval), application); // inter-transmission time of EEG sensor follows a deterministic distribution
		Actuator actuator = new Actuator("a-0", userId, appId, "ACTION", application);
		dev.addSensor(sensor);
		dev.addActuator(actuator);
		
		FogNode fn0 = SimulationArchitecture.createFogNode("FN0", true, 102400, 
									4000, 0.01, 103, 83.25, 10000000,
									1000000, 3.0, 0.05, 0.001, 0.0,
									new Rectangle(10, 10), new Point(1,1), new Vector(0.), 1);
		
		// PuddleHead attempt
		double[] xcor = {0.0, 6, 6, 0};
		double[] ycor = {0.0, 0, 2, 2};
		Polygon areaOfCoverage = null;
		try {
			areaOfCoverage = new Polygon(xcor, ycor);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location = new Point(3, 1);
		PuddleHead ph0 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD0", areaOfCoverage, location, 1);
		
		SimulationArchitecture.getInstance().addEndDevice(dev);
		SimulationArchitecture.getInstance().addFogNode(fn0);
		SimulationArchitecture.getInstance().addPuddleHead(ph0);

		// Now connecting entities with Links
		SimulationArchitecture.getInstance().addLink(dev.getId(), fn0.getId(), 10, 1000);
		SimulationArchitecture.getInstance().addLink(fn0.getId(), ph0.getId(), 10, 1000);
		
		if (SimulationArchitecture.getInstance().validatePuddlesimTopology()) {
			System.out.println("Topology validation successful");
			SimulationArchitecture.getInstance().setUpPuddlesimEntities();
			
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