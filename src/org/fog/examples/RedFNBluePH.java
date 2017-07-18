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
public class RedFNBluePH {

	public static void main(String[] args) {

		Log.printLine("Starting SimArch...");
		Logger.ENABLED = false;
		Logger.enableTag("FOG_DEVICE");
		Logger.enableTag("FOG_NODE");
		Logger.enableTag("SWITCH");
		Logger.enableTag("LINK");
		Logger.enableTag("END_DEVICE");
		Logger.enableTag("ACTUATOR");
		Logger.enableTag("SENSOR");
		//Logger.enableTag("GLOBAL_BROKER");
		
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
			
			broker.setup(SimulationArchitecture.getInstance().getPuddleHeadIDs(), 
					SimulationArchitecture.getInstance().getFogNodeIDs(), 
					SimulationArchitecture.getInstance().getLinkIDs(), 
					SimulationArchitecture.getInstance().getEndDeviceIDs());
			
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

		///////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////     FOG NODES    /////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////
		FogNode fn0 = SimulationArchitecture.createFogNode("FN0", true, 102400, 
									4000, 0.01, 103, 83.25, 10000000,
									1000000, 3.0, 0.05, 0.001, 0.0,
									new Rectangle(15, 15), new Point(1,1), new Vector(0.1), 1);
		
		FogNode fn1 = SimulationArchitecture.createFogNode("FN1", false, 102400, 
									4000, 0.01, 103, 83.25, 10000000,
									1000000, 3.0, 0.05, 0.001, 0.0,
									new Rectangle(15, 15), new Point(1,2), new Vector(0.1), 1);
		
		FogNode fn2 = SimulationArchitecture.createFogNode("FN2", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(5,1), new Vector(0.1), 1);

		FogNode fn3 = SimulationArchitecture.createFogNode("FN3", false, 102400, 
						4000, 0.01, 103, 83.25, 10000000,
						1000000, 3.0, 0.05, 0.001, 0.0,
						new Rectangle(15, 15), new Point(8,6), new Vector(0.1), 1);
		
		FogNode fn4 = SimulationArchitecture.createFogNode("FN4", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(10,10), new Vector(0.1), 1);
		
		FogNode fn5 = SimulationArchitecture.createFogNode("FN5", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(2,11), new Vector(0.1), 1);
			
		FogNode fn6 = SimulationArchitecture.createFogNode("FN6", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(3,10), new Vector(0.1), 1);

		FogNode fn7 = SimulationArchitecture.createFogNode("FN7", false, 102400, 
						4000, 0.01, 103, 83.25, 10000000,
						1000000, 3.0, 0.05, 0.001, 0.0,
						new Rectangle(15, 15), new Point(1,7), new Vector(0.1), 1);
		
		 FogNode fn8 = SimulationArchitecture.createFogNode("FN8", false, 102400, 
						4000, 0.01, 103, 83.25, 10000000,
						1000000, 3.0, 0.05, 0.001, 0.0,
						new Rectangle(15, 15), new Point(6,11), new Vector(0.1), 1);
		
		FogNode fn9 = SimulationArchitecture.createFogNode("FN9", false, 102400, 
						4000, 0.01, 103, 83.25, 10000000,
						1000000, 3.0, 0.05, 0.001, 0.0,
						new Rectangle(15, 15), new Point(6,10), new Vector(0.1), 1);
		
		FogNode fn10 = SimulationArchitecture.createFogNode("FN10", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(7,7), new Vector(0.1), 1);

		FogNode fn11 = SimulationArchitecture.createFogNode("FN11", false, 102400, 
						4000, 0.01, 103, 83.25, 10000000,
						1000000, 3.0, 0.05, 0.001, 0.0,
						new Rectangle(15, 15), new Point(4,5), new Vector(0.1), 2);
		
		FogNode fn12 = SimulationArchitecture.createFogNode("FN12", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(13,11), new Vector(0.1), 2);
		
		FogNode fn13 = SimulationArchitecture.createFogNode("FN13", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(1,13), new Vector(0.1), 2);
			
		FogNode fn14 = SimulationArchitecture.createFogNode("FN14", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(4,1), new Vector(0.1), 2);
		
		FogNode fn15 = SimulationArchitecture.createFogNode("FN15", false, 102400, 
				4000, 0.01, 103, 83.25, 10000000,
				1000000, 3.0, 0.05, 0.001, 0.0,
				new Rectangle(15, 15), new Point(11,8), new Vector(0.1), 1); 
		
		///////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////    PUDDLEHEADS   /////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////////////////////
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
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		double[] xcor1 = {6, 9, 9, 7, 6};
		double[] ycor1 = {4, 4, 7, 8, 7};
		Polygon areaOfCoverage1 = null;
		try {
			areaOfCoverage1 = new Polygon(xcor1, ycor1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location1 = new Point(7, 5);
		PuddleHead ph1 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD1", areaOfCoverage1, location1, 1);
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		double[] xcor2 = {3, 5, 3, 1};
		double[] ycor2 = {9, 11, 13, 11};
		Polygon areaOfCoverage2 = null;
		try {
			areaOfCoverage2 = new Polygon(xcor2, ycor2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location2 = new Point(3, 11);
		PuddleHead ph2 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD2", areaOfCoverage2, location2, 1);
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		double[] xcor3 = {5, 7, 7, 5};
		double[] ycor3 = {9, 9, 13, 13};
		Polygon areaOfCoverage3 = null;
		try {
			areaOfCoverage3 = new Polygon(xcor3, ycor3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location3 = new Point(6, 12);
		PuddleHead ph3 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD3", areaOfCoverage3, location3, 1);
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		double[] xcor4 = {10, 11, 12, 11, 11, 8};
		double[] ycor4 = {7, 7, 8, 9, 12, 12};
		Polygon areaOfCoverage4 = null;
		try {
			areaOfCoverage4 = new Polygon(xcor4, ycor4);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location4 = new Point(10, 11);
		PuddleHead ph4 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD4", areaOfCoverage4, location4, 1);
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		double[] xcor5 = {0.0, 7, 7, 0};
		double[] ycor5 = {0.0, 0, 13, 13};
		Polygon areaOfCoverage5 = null;
		try {
			areaOfCoverage5 = new Polygon(xcor5, ycor5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location5 = new Point(3, 7);
		PuddleHead ph5 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD5", areaOfCoverage5, location5, 2);
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		double[] xcor6 = {8, 14, 14, 8};
		double[] ycor6 = {3, 3, 13, 13};
		Polygon areaOfCoverage6 = null;
		try {
			areaOfCoverage6 = new Polygon(xcor6, ycor6);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location6 = new Point(12, 5);
		PuddleHead ph6 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD6", areaOfCoverage6, location6, 2);
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		double[] xcor7 = {0, 2, 2, 0};
		double[] ycor7 = {5, 5, 8, 8};
		Polygon areaOfCoverage7 = null;
		try {
			areaOfCoverage7 = new Polygon(xcor7, ycor7);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Point location7 = new Point(12, 5);
		PuddleHead ph7 = SimulationArchitecture.createPuddleHead("PUDDLEHEAD7", areaOfCoverage7, location7, 1);
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		EndDevice dev = new EndDevice("DEV", new Rectangle(10, 10), new Point(1,2), new Vector(0.23), false);
		int transmissionInterval = 5000;
		Sensor sensor = new Sensor("s-0", "SENSED_DATA", userId, appId, new DeterministicDistribution(transmissionInterval), application); // inter-transmission time of EEG sensor follows a deterministic distribution
		Actuator actuator = new Actuator("a-0", userId, appId, "ACTION", application);
		dev.addSensor(sensor);
		dev.addActuator(actuator);
		
		SimulationArchitecture.getInstance().addFogNode(fn0);
		SimulationArchitecture.getInstance().addFogNode(fn1);
		SimulationArchitecture.getInstance().addFogNode(fn2);
		SimulationArchitecture.getInstance().addFogNode(fn3);
		SimulationArchitecture.getInstance().addFogNode(fn4);
		SimulationArchitecture.getInstance().addFogNode(fn5);
		SimulationArchitecture.getInstance().addFogNode(fn6);
		SimulationArchitecture.getInstance().addFogNode(fn7);
		SimulationArchitecture.getInstance().addFogNode(fn8);
		SimulationArchitecture.getInstance().addFogNode(fn9);
		SimulationArchitecture.getInstance().addFogNode(fn10);
		SimulationArchitecture.getInstance().addFogNode(fn11);
		SimulationArchitecture.getInstance().addFogNode(fn12);
		SimulationArchitecture.getInstance().addFogNode(fn13);
		SimulationArchitecture.getInstance().addFogNode(fn14);
		SimulationArchitecture.getInstance().addFogNode(fn15);
		
		SimulationArchitecture.getInstance().addPuddleHead(ph0);
		SimulationArchitecture.getInstance().addPuddleHead(ph1);
		SimulationArchitecture.getInstance().addPuddleHead(ph2);
		SimulationArchitecture.getInstance().addPuddleHead(ph3);
		SimulationArchitecture.getInstance().addPuddleHead(ph4);
		SimulationArchitecture.getInstance().addPuddleHead(ph5);
		SimulationArchitecture.getInstance().addPuddleHead(ph6);
		SimulationArchitecture.getInstance().addPuddleHead(ph7);
		
		SimulationArchitecture.getInstance().addEndDevice(dev);

		// Now connecting entities with Links
		SimulationArchitecture.getInstance().addLink(dev.getId(), fn0.getId(), 2, 1000);
		
		SimulationArchitecture.getInstance().addLink(ph0.getId(), fn0.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph0.getId(), fn1.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph0.getId(), fn2.getId(), 2, 1000);
		
		SimulationArchitecture.getInstance().addLink(ph1.getId(), fn3.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph1.getId(), fn10.getId(), 2, 1000);
		
		SimulationArchitecture.getInstance().addLink(ph2.getId(), fn5.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph2.getId(), fn6.getId(), 2, 1000);

		SimulationArchitecture.getInstance().addLink(ph3.getId(), fn8.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph3.getId(), fn9.getId(), 2, 1000);

		SimulationArchitecture.getInstance().addLink(ph4.getId(), fn4.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph4.getId(), fn15.getId(), 2, 1000);

		SimulationArchitecture.getInstance().addLink(ph5.getId(), fn11.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph5.getId(), fn14.getId(), 2, 1000);
		SimulationArchitecture.getInstance().addLink(ph5.getId(), fn13.getId(), 2, 1000);

		SimulationArchitecture.getInstance().addLink(ph6.getId(), fn12.getId(), 2, 1000);
		
		SimulationArchitecture.getInstance().addLink(ph7.getId(), fn7.getId(), 2, 1000);



		
		// TODO: Create stuff so that these functions work
		if (SimulationArchitecture.getInstance().validatePuddlesimTopology()) {
			System.out.println("Topology validation successful");
			SimulationArchitecture.getInstance().setUpPuddlesimEntities();
			
		} else {
			System.out.println("Topology validation Unsuccessful");
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