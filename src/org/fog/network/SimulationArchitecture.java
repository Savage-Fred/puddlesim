package org.fog.network;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.fog.utils.CSVReader;
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
	
	/**
	 * Map of all the PuddleHeads in the network by level. The key is the level and the list is all the PuddleHeads at that level.
	 */
	protected Map<Integer, List<PuddleHead>> puddleHeadsByLevel; 
	
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
		this.puddleHeadsByLevel = new HashMap<Integer, List<PuddleHead>>();
	}
	
	
	/**
	 * This function creates the architecture.
	 * @param userId
	 * @param appId
	 * @param application
	 */
	public void createNewTopology(String fileName, int userId, String appId, Application application) {
		readPuddleHeadNodeCSV(fileName);
		linkNodestoPuddleHeads();
	}
	
	/**
	 * Function to be called to structure the architecture. 
	 * @param puddleHeadFile
	 * @param nodeFile
	 * @param userId
	 * @param appId
	 * @param application
	 */
	public void createNewTopology(String puddleHeadFile, String nodeFile, int userId, String appId, Application application) {
		readPuddleHeadCSV(puddleHeadFile);
		readFogNodeCSV(nodeFile);
		linkNodestoPuddleHeads();
	}
	
	/**
	 * This function adds all PuddleHeads and Nodes based on the information
	 * passed in from a CSV file. 
	 * 
	 * File is assumed to be a list where each line is type, dx, dy, level, xcoordinate, ycoordinate. 
	 * Any subsequent line with the same information for the first four values adds the new xcoordinate
	 * and ycoordinate to the polygon which is the area of coverage for the PuddleHead. If there are 
	 * multiple lines for the same node, the additional lines are ignored. 
	 * 
	 * @param fileName of the CSV file with the Voronoi information
	 */
	private void readPuddleHeadNodeCSV(String fileName) {
	   BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(fileName));
            ArrayList<Double> areaPointsX = new ArrayList<Double>();
            ArrayList<Double> areaPointsY = new ArrayList<Double>();
            double lastX = 0.0;
            double lastY = 0.0;
            boolean first = true;
            line = br.readLine();
            while(line !=null) {
                String[] row = line.split(csvSplitBy);
                
                //TODO: make sure this is for the right kind of file
                //if type, dx, dy, level, areaX, areaY
                Integer type = Integer.parseInt(row[0]);
                Double xcoord = Double.parseDouble(row[1]);
                Double ycoord = Double.parseDouble(row[2]);
                Integer level = Integer.parseInt(row[3]);
                Double areaX = Double.parseDouble(row[4]);
                Double areaY = Double.parseDouble(row[5]);
                
                //if level, dx, dy, areaX, areaY
//                Integer type = 80;
//                Integer level = Integer.parseInt(row[0]);
//                Double xcoord = Double.parseDouble(row[1]);
//                Double ycoord = Double.parseDouble(row[2]);
//                Double areaX = Double.parseDouble(row[3]);
//                Double areaY = Double.parseDouble(row[4]);
          
                
                //TODO: remove this, it isn't needed, just here for running tests
                if(type != 80){
                	if(!(lastX == xcoord && lastY == ycoord)){
		            	Point location = new Point(xcoord, ycoord);
		                int numNodes = getInstance().getFogNodeIDs().size();
		                String name = "FN" + numNodes;
		                FogNode node = createFogNode(name, false, 102400, 
								4000, 0.01, 103, 83.25, 10000000,
								1000000, 3.0, 0.05, 0.001, 0.0,
								new Rectangle(20, 20), location, new Vector(0.1), level);
		                getInstance().addFogNode(node);
		                lastX = xcoord;
		                lastY = ycoord; 
                	}
                }
                else if(first){
                	areaPointsX.add(areaX);
                	areaPointsY.add(areaY);
                	lastX = xcoord;
                	lastY = ycoord;
                	first = false;
                }
                else if(lastX == xcoord && lastY == ycoord){
                	areaPointsX.add(areaX);
                	areaPointsY.add(areaY);
                }
                else if(!(lastX == xcoord && lastY == ycoord)){
                	int numPoints = areaPointsX.size();
                	Double[] xDouble = areaPointsX.toArray(new Double[numPoints]);
                	double[] xIn = new double[numPoints];
                	for(int i = 0; i < numPoints; i++){
                		xIn[i] = xDouble[i];
                	}
                	Double[] yDouble = areaPointsY.toArray(new Double[numPoints]);
                	double[] yIn = new double[numPoints];
                	for(int i = 0; i < numPoints; i++){
                		yIn[i] = yDouble[i];
                	}
                	Polygon areaOfCoverage = new Polygon(xIn, yIn);
                	int numOfPuddleHead = getInstance().getPuddleHeadIDs().size() + 1;
                	String name = "PH" + numOfPuddleHead; 
                	Point point = new Point(lastX, lastY);
                	PuddleHead newPH = createPuddleHead(name, areaOfCoverage, point, level);
                	getInstance().addPuddleHead(newPH);
                	areaPointsX = new ArrayList<Double>();
                	areaPointsY = new ArrayList<Double>();
                	lastX = xcoord;
                	lastY = ycoord;
                	areaPointsX.add(areaX);
                	areaPointsY.add(areaY);
                }
                line = br.readLine();
                if(line == null){
                	int numPoints = areaPointsX.size();
                	Double[] xDouble = areaPointsX.toArray(new Double[numPoints]);
                	double[] xIn = new double[numPoints];
                	for(int i = 0; i < numPoints; i++){
                		xIn[i] = xDouble[i];
                	}
                	Double[] yDouble = areaPointsY.toArray(new Double[numPoints]);
                	double[] yIn = new double[numPoints];
                	for(int i = 0; i < numPoints; i++){
                		yIn[i] = yDouble[i];
                	}
                	Polygon areaOfCoverage = new Polygon(xIn, yIn);
                	int numOfPuddleHead = getInstance().getPuddleHeadIDs().size() + 1;
                	String name = "PH" + numOfPuddleHead; 
                	Point point = new Point(lastX, lastY);
                	PuddleHead newPH = createPuddleHead(name, areaOfCoverage, point, level);
                	getInstance().addPuddleHead(newPH);
                	areaPointsX = new ArrayList<Double>();
                	areaPointsY = new ArrayList<Double>();
                	lastX = xcoord;
                	lastY = ycoord;
                	areaPointsX.add(areaX);
                	areaPointsY.add(areaY);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
	
	/**
	 * This function adds all PuddleHeads based on the information
	 * passed in from a CSV file. 
	 * 
	 * File is assumed to be a list where each line is level, xcoordinate, ycoordinate, areaX point, areaY point. 
	 * Any subsequent line with the same information for the first four values adds the new xcoordinate
	 * and ycoordinate to the polygon which is the area of coverage for the PuddleHead. 
	 * 
	 * @param fileName of the CSV file with the PuddleHead Voronoi information
	 */
	private void readPuddleHeadCSV(String fileName) {
		   BufferedReader br = null;
	        String line = "";
	        String csvSplitBy = ",";

	        try {
	            br = new BufferedReader(new FileReader(fileName));
	            ArrayList<Double> areaPointsX = new ArrayList<Double>();
	            ArrayList<Double> areaPointsY = new ArrayList<Double>();
	            double lastX = 0.0;
	            double lastY = 0.0;
	            boolean first = true;
	            line = br.readLine();
	            while(line !=null) {
	                String[] row = line.split(csvSplitBy);
	                
	                //if level, dx, dy, areaX, areaY
	                Integer level = Integer.parseInt(row[0]);
	                Double xcoord = Double.parseDouble(row[1]);
	                Double ycoord = Double.parseDouble(row[2]);
	                Double areaX = Double.parseDouble(row[3]);
	                Double areaY = Double.parseDouble(row[4]);

	               if(first){
	                	areaPointsX.add(areaX);
	                	areaPointsY.add(areaY);
	                	lastX = xcoord;
	                	lastY = ycoord;
	                	first = false;
	                }
	                else if(lastX == xcoord && lastY == ycoord){
	                	areaPointsX.add(areaX);
	                	areaPointsY.add(areaY);
	                }
	                else if(!(lastX == xcoord && lastY == ycoord)){
	                	int numPoints = areaPointsX.size();
	                	Double[] xDouble = areaPointsX.toArray(new Double[numPoints]);
	                	double[] xIn = new double[numPoints];
	                	for(int i = 0; i < numPoints; i++){
	                		xIn[i] = xDouble[i];
	                	}
	                	Double[] yDouble = areaPointsY.toArray(new Double[numPoints]);
	                	double[] yIn = new double[numPoints];
	                	for(int i = 0; i < numPoints; i++){
	                		yIn[i] = yDouble[i];
	                	}
	                	Polygon areaOfCoverage = new Polygon(xIn, yIn);
	                	int numOfPuddleHead = getInstance().getPuddleHeadIDs().size() + 1;
	                	String name = "PH" + numOfPuddleHead; 
	                	Point point = new Point(lastX, lastY);
	                	PuddleHead newPH = createPuddleHead(name, areaOfCoverage, point, level);
	                	getInstance().addPuddleHead(newPH);
	                	areaPointsX = new ArrayList<Double>();
	                	areaPointsY = new ArrayList<Double>();
	                	lastX = xcoord;
	                	lastY = ycoord;
	                	areaPointsX.add(areaX);
	                	areaPointsY.add(areaY);
	                }
	                line = br.readLine();
	                if(line == null){
	                	int numPoints = areaPointsX.size();
	                	Double[] xDouble = areaPointsX.toArray(new Double[numPoints]);
	                	double[] xIn = new double[numPoints];
	                	for(int i = 0; i < numPoints; i++){
	                		xIn[i] = xDouble[i];
	                	}
	                	Double[] yDouble = areaPointsY.toArray(new Double[numPoints]);
	                	double[] yIn = new double[numPoints];
	                	for(int i = 0; i < numPoints; i++){
	                		yIn[i] = yDouble[i];
	                	}
	                	Polygon areaOfCoverage = new Polygon(xIn, yIn);
	                	int numOfPuddleHead = getInstance().getPuddleHeadIDs().size() + 1;
	                	String name = "PH" + numOfPuddleHead; 
	                	Point point = new Point(lastX, lastY);
	                	PuddleHead newPH = createPuddleHead(name, areaOfCoverage, point, level);
	                	getInstance().addPuddleHead(newPH);
	                	areaPointsX = new ArrayList<Double>();
	                	areaPointsY = new ArrayList<Double>();
	                	lastX = xcoord;
	                	lastY = ycoord;
	                	areaPointsX.add(areaX);
	                	areaPointsY.add(areaY);
	                }
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (NumberFormatException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
		}
	
	//TODO: make this not a hardcoded function
	/**
	 * This function adds all FogNodes based on the information passed in from a CSV file.
	 * 
	 * File is assumed to be a list where each line is level, dx, dy. 
	 * Nodes are given different values for their other characteristics based on their level.
	 * 
	 * NOTE: THIS FUNCTION IS HARDCODED FOR THE NUMBERS DESIGNATED PER LEVEL OF FOG NODE
	 * 
	 * @param fileName CSV file containing FogNode information
	 */
	private void readFogNodeCSV(String fileName) {
		   BufferedReader br = null;
	        String line = "";
	        String csvSplitBy = ",";

	        try {
	            br = new BufferedReader(new FileReader(fileName));
	            while((line = br.readLine()) !=null) {
	                String[] row = line.split(csvSplitBy);
	                Double xcoord = Double.parseDouble(row[1]);
	                Double ycoord = Double.parseDouble(row[2]);
	                Integer level = Integer.parseInt(row[0]);
	                Point location = new Point(xcoord, ycoord);
	                int numNodes = getInstance().getFogNodes().size();
	                String name = "FN" + numNodes;

	                FogNode node = createFogNode(name, false, 102400, 
							4000, 0.01, 103, 83.25, 10000000,
							1000000, 3.0, 0.05, 0.001, 0.0,
							new Rectangle(1001, 1001), location, new Vector(1), level);
	                getInstance().addFogNode(node);
	                
	                //TODO: NEED INFO FOR EACH LEVEL. 
	                //mips, ram, ratePerMips, busyPower, idlePower, storage, bw, costProcessing, costPerMem, costPerStorage, costPerBw, bounds, (coordinates-Point), (level)
	                switch(level){
	                case 1: 
	                	
//	                	FogNode newNode1 = createFogNode(name, false,  );
//	                	addFogNode(newNode1);
	                	break;
	                case 2:
	                	
//	                	FogNode newNode2 = createFogNode(name, false, );
//	                	addFogNode(newNode2);
	                	break;
	                case 3:
	                	
//	                	FogNode newNode3 = createFogNode(name, false, );
//	                	addFogNode(newNode3);
	                	break;
	                case 4:
	                	
//	                	FogNode newNode4 = createFogNode(name, false, );
//	                	addFogNode(newNode4);
	                	break;
	                default:
	                	break;
	                }
	             
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (NumberFormatException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	}
	
	private void linkNodestoPuddleHeads(){
		double latency = 2.0;
		double bandwidth = 1000.0;
		for(FogNode node : getInstance().getFogNodes()){
			PuddleHead puddlehead = findNodeNewPuddleHead(node);
			if(puddlehead != null){
				getInstance().addLink(node.getId(), puddlehead.getId(), latency, bandwidth);
			}
		}
	}
	
	/**
	 * finds which PuddleHead's area of coverage a node is in. If one is not found it returns null
	 * @param node
	 * @return puddlehead
	 */
	private PuddleHead findNodeNewPuddleHead(FogNode node){
		Point nodePoint = node.getLocation();
		
		List<PuddleHead> viablePuddleHeads = getInstance().getPuddleHeadsByLevel().get(node.getLevel());
		
		if(viablePuddleHeads != null){
			for(PuddleHead puddlehead : viablePuddleHeads){
				Polygon area = puddlehead.getAreaOfCoverage(); 
				if(area.contains(nodePoint)){
					return puddlehead; 
				}
			}
		}
		return null; 
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
			// TODO: add maps of links to endDevices 
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
			// TODO: add maps of links to endDevices 
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
		System.out.println("Added Fog Node: " + dev.getId() + " Point: " + dev.getLocation() + " My level: " + dev.getLevel());
	}
	
	/**
	 * Add ouddlehead to physical topology
	 * @param dev
	 */
	@Override
	public void addPuddleHead(PuddleHead dev) {
		getPuddleHeads().add(dev);
		addPuddleHeadByLevel(dev, dev.getLevel());
		// Add device ID to integer list
		puddleHeadIDs.add(dev.getId());
		System.out.println("Added PuddleHead: " + dev.getId() + " Point: " + dev.getLocation() + " " + dev.getAreaOfCoverage());
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
	/**
	 * @return the puddleHeadsByLevel
	 */
	public Map<Integer, List<PuddleHead>> getPuddleHeadsByLevel() {
		return puddleHeadsByLevel;
	}

	/**
	 * @param puddleHeadsByLevel the puddleHeadsByLevel to set
	 */
	public void setPuddleHeadsByLevel(Map<Integer, List<PuddleHead>> puddleHeadsByLevel) {
		this.puddleHeadsByLevel = puddleHeadsByLevel;
	}
	
	/**
	 * Adds a single PuddleHead into the by level map. If there are no PuddleHeads at that level, a new list is created. 
	 * @param puddleHeadId
	 * @param level
	 */
	public void addPuddleHeadByLevel(PuddleHead dev, int level){
		List<PuddleHead> levelList = puddleHeadsByLevel.get(level); 
		if(levelList == null){
			ArrayList<PuddleHead> newLevelList = new ArrayList<PuddleHead>();
			newLevelList.add(dev); 
			puddleHeadsByLevel.put(level, newLevelList);
		}
		else{
			levelList.add(dev);
			puddleHeadsByLevel.put(level, levelList); 
		}
	}
}
