package org.fog.utils;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * This class is meant to add mobility features onto FogDevice, Actuator, and Sensor currently being used in iFogSim. 
 * It's purpose is to allow the movement of fog devices between the areas of influence of clusterhead. This is accomplished
 * with the use of 2D vector that's updated every time the update function is called.   
 * 
 * @author AviRynderman
 * @since NSF REU 2017 - Parallel and Distributed Computing
 */
public class Mobility {
	/**
	 * Turns on or off the debug outputs. 
	 */
	private static final boolean DEBUG = true;
	/**
	 * Holds the current longitude of the device.
	 */
	private double longitude;
	/**
	 * Holds the current latitude of the device.
	 */
	private double latitude;
	/**
	 * x-component of the vector used to denote movement per second of device.
	 */
	private double xVector;
	/**
	 * y-component of the vector used to denote movement per second of device.
	 */
	private double yVector;
	/**
	 *  Boolean that determines the moveability of the device. False means immobile, True means mobile.
	 */
	private boolean isMobile;
	/**
	 *  Keep track of last update time. When updateLocation() is called, this will be compared
	 *  with the current clock value and the total movement adjustment calculated.
	 */ 
	private double counter;
	/**
	 * 
	 * @param latitude Latitude of the device.
	 * @param longitude Longitude of the device.
	 * @param xVector x-component of the movement vector, meters/second.
	 * @param yVector y-component of the movement vector, meters/second.
	 * @param isMobile determines whether or not the device changes location.
	 */
	public Mobility(double latitude, double longitude, double xVector, double yVector, boolean isMobile){
		this.latitude = latitude;
		this.longitude = longitude;
		this.isMobile = isMobile;
		this.xVector = xVector;
		this.yVector = yVector;
		counter = CloudSim.clock();
	}
	
	/**
	 *  Updates the device location.
	 */
	private void updateLocation(){
		boolean logStatus = Log.isDisabled();
		if(DEBUG){
			Log.enable();
		}
		// Get the difference between last time the clock was updated and the current simulation time
		double scalar = CloudSim.clock() - counter;
		// Now update the simulation time stored in the mobility class
		counter = CloudSim.clock();
		// Output for testing
		String str = "---- Scalar = " + scalar;
		if(isMobile){
			this.latitude +=scalar*this.xVector;
			this.longitude += scalar*this.yVector;	
		}
		if(DEBUG){
			Log.printLine(str);
			if(logStatus)
				Log.disable();
		}
	} 
	/**
	 * Calculates the magnitude of the vector.
	 * @return Velocity of the node.
	 */
	public double getVelocity() {
		double velocity;
		velocity = Math.sqrt(this.latitude*this.latitude + this.longitude*this.longitude);
		return velocity;
	}
	/**
	 * Calculates the angle at which the object is moving.
	 * @return The direction of movement as a radian unit. 
	 */
	public double getAngleRad() {
		return Math.atan2(xVector, yVector);
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getxVector() {
		return xVector;
	}
	public void setxVector(double xVector) {
		this.xVector = xVector;
	}
	public double getyVector() {
		return yVector;
	}
	public void setyVector(double yVector) {
		this.yVector = yVector;
	}
	public boolean isMobile() {
		return isMobile;
	}
	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}
}

