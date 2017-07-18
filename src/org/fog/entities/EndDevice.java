package org.fog.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.fog.network.Link;
import org.fog.utils.Config;
import org.fog.utils.FogEvents;
import org.fog.utils.GeoLocation;
import org.fog.utils.Logger;
import org.fog.utils.Mobility;
import org.fog.utils.Point;
import org.fog.utils.Rectangle;
import org.fog.utils.Vector;

public class EndDevice extends SimEntity {

	private List<Sensor> sensors;
	private List<Actuator> actuators;
	
	// Essentially the other other end of the link when used in puddlesim.
	private int edgeSwitchId;
	private int linkId;

	/**
	 * Used for debugging purposes. Adds a label onto the output. Note, only used in Logger.debug
	 */
	private static String LOG_TAG = "END_DEVICE";
	
	/**
	 * Used to check whether or not the device has started moving.
	 */
	private boolean moving = false;
	/**
	 * Mobility object for FogNode.
	 */
	private Mobility mobile = null;
	
	private GeoLocation geoLocation;
	
	// Puddlesim addition:
	public EndDevice(String name, Rectangle bounds, Point coordinates, Vector vector, boolean isMobile) {
		super(name);
		this.mobile = new Mobility(bounds, coordinates, vector, isMobile);
		setSensors(new ArrayList<Sensor>());
		setActuators(new ArrayList<Actuator>());
	}
	
	public EndDevice(String name){
		super(name);
		this.mobile = new Mobility(new Rectangle(10,10), new Point(0,0), new Vector(0.1), false); 
		setSensors(new ArrayList<Sensor>());
		setActuators(new ArrayList<Actuator>());
	}

	/**
	 * Updates the location and latency continually
	 * Note: it will send the process node move event only if the name of the global broker is "globalbroker"
	 * @param ev (SimEvent)
	 */
	protected void processUpdateLocation(SimEvent ev){
		// If the device is mobile, update the location and send an event to the queue to trigger it again
		if(mobile.isMobile()){
			mobile.updateLocation();
			updateDeviceLocations();
			
			Logger.debug(LOG_TAG, "End device moved");
			
			send(super.getId(), Config.LOCATION_UPDATE_INTERVAL, FogEvents.UPDATE_LOCATION);
			
			//Send to global broker for processing. 
			int brokerId = CloudSim.getEntityId("globalbroker");
			if(brokerId > 0){
				send(brokerId, CloudSim.getMinTimeBetweenEvents(), FogEvents.PROCESS_END_DEVICE_MOVE, getId());
			}
			else{
				//Logger.debug(LOG_TAG, "'globalbroker' is not a defined entity");
			}
		}
		//Logger.debug(LOG_TAG, getName(), "Completed execution of move");
	}
	
	private void updateDeviceLocations() {
		// Set the location of the sensors and actuators
		for(Actuator a : this.actuators){
			a.setLocation(mobile.getPoint());
		}
		for(Sensor s : this.sensors){
			s.setLocation(mobile.getPoint());
		}
	}

	protected void sendTuple(Tuple tuple, int dstDeviceId, int dstVmId) {
		tuple.setVmId(dstVmId);
		tuple.setSourceDeviceId(getId());
		tuple.setDestinationDeviceId(dstDeviceId);
		send(getLinkId(), CloudSim.getMinTimeBetweenEvents(), FogEvents.TUPLE_ARRIVAL, tuple);
	}
	
	protected void sendTuple(Tuple tuple, int dstDeviceId) {
		send(dstDeviceId, CloudSim.getMinTimeBetweenEvents(), FogEvents.TUPLE_ARRIVAL, tuple);
	}
	
	public void addSensor(Sensor sensor) {
		getSensors().add(sensor);
		sensor.setGatewayDeviceId(getEdgeSwitchId());
		sensor.setEndDeviceId(getId());
		sensor.setDevice(this);
	}
	
	public void addActuator(Actuator actuator) {
		getActuators().add(actuator);
		actuator.setGatewayDeviceId(getEdgeSwitchId());
		actuator.setEndDeviceId(getId());
	}
	
	public void addPuddlesimSensor(Sensor sensor) {
		sensor.setLocation(this.mobile.getPoint());
		getSensors().add(sensor);
		sensor.setGatewayDeviceId(getEdgeSwitchId());
		sensor.setEndDeviceId(getId());
		sensor.setDevice(this);
	}
	
	public void addPuddlesimActuator(Actuator actuator) {
		actuator.setLocation(this.mobile.getPoint());
		getActuators().add(actuator);
		actuator.setGatewayDeviceId(getEdgeSwitchId());
		actuator.setEndDeviceId(getId());
	}
	
	@Override
	public void startEntity() {

	}

	@Override
	public void processEvent(SimEvent ev) {
		// This kickstarts the movement. 
		if(!this.moving){
			this.moving = true;
			processUpdateLocation(ev);
		}
		
		int tag = ev.getTag();
		
		switch(tag) {
		case FogEvents.TUPLE_ARRIVAL:
			Logger.debug(LOG_TAG, "Tuple arrived.");
			processTupleArrival(ev);
			break;
		case FogEvents.UPDATE_LOCATION:
			processUpdateLocation(ev);
			break;
		}

	}

	private void processTupleArrival(SimEvent ev) {
		Tuple tuple = (Tuple) ev.getData();
		int destId = tuple.getDestinationDeviceId();
		for (Actuator a : getActuators()) {
			if (destId == a.getId()) {
				sendTuple(tuple, destId);
			}
		}
	}

	@Override
	public void shutdownEntity() {

	}

	/**
	 * Gets the location of the fog device.
	 * @return locations of the fog device as a Point.
	 */
	public Point getLocation(){
		return this.mobile.getPoint();
	}
	
	/**
	 * Sets the location of the fog device.
	 * @param point 
	 */
	public void setLocation(Point point){
		this.mobile.setPoint(point);
	}
	
	/**
	 * This function changes the old direction of movement and velocity of the device.
	 * @param v new value for movement of device
	 */
	public void updateDirection(Vector v){
		this.mobile.updateDirection(v);
	}
	
	/**
	 * Determines if the device is mobile.
	 * @return a boolean indicating whether or not the device is mobile.
	 */
	public boolean isMobile() {
		return this.mobile.isMobile();
	}
	
	/**
	 * Sets the mobility of the device. If it is mobile, it will move. 
	 * @param isMobile
	 */
	public void setMobility(boolean isMobile) {
		this.mobile.setMobile(isMobile);
	}

	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}
	public List<Actuator> getActuators() {
		return actuators;
	}
	public void setActuators(List<Actuator> actuators) {
		this.actuators = actuators;
	}
	public int getEdgeSwitchId() {
		return edgeSwitchId;
	}
	public void setEdgeSwitchId(int edgeSwitchId) {
		this.edgeSwitchId = edgeSwitchId;
		for(Actuator actuator : actuators){
			actuator.setGatewayDeviceId(edgeSwitchId);
		}
	}
	public int getLinkId() {
		return linkId;
	}
	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}
	public GeoLocation getGeoLocation() {
		return geoLocation;
	}
	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}
}
