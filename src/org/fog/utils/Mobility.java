package org.fog.utils;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * This class is meant to add mobility features onto FogDevice, Actuator, and Sensor currently being used in iFogSim. 
 * It's purpose is to allow the movement of fog devices between the areas of influence of puddlehead. This is accomplished
 * with the use of 2D vector that's updated every time the update function is called.   
 * 
 * @author Avi Rynderman
 * @since NSF REU 2017 - Parallel and Distributed Computing
 */
public class Mobility {
	/**
	 * Turns on or off the debug outputs. 
	 */
	private static final boolean DEBUG = true;
	/**
	 * Hold the bounds of the device. If the device leaves the rectangle, it will 'bounce' off the sides.
	 */
	Rectangle bounds;
	/**
	 * Holds the movement vector as an x,y value. Velocity is included in these values.
	 */
	private Vector movementVector;
	/**
	 * Holds the current longitude of the device.
	 */
	private double longitude;
	/**
	 * Holds the current latitude of the device.
	 */
	private double latitude;
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
	 * @param latitude of the device.
	 * @param longitude of the device.
	 * @param xVector x-component of the movement vector, meters/second.
	 * @param yVector y-component of the movement vector, meters/second.
	 * @param isMobile determines whether or not the device changes location.
	 */
	public Mobility(Rectangle bounds, double latitude, double longitude, Vector movementVector, boolean isMobile){
		this.bounds = bounds;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isMobile = isMobile;
		this.movementVector = movementVector;
		counter = CloudSim.clock();
	}
	/**
	 *  Updates the device location or wrap around if device leaves boundaries.
	 */
	public void updateLocation(){
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
		// Update the location of the device
		if(isMobile){
			this.latitude +=scalar*this.movementVector.getxComponent();
			this.longitude += scalar*this.movementVector.getyComponent();	
		}
		// If the device has left the bounds, wrap it around
		if (this.latitude > bounds.getWidth()+bounds.getX()) 
			this.latitude = bounds.getX()+latitude % bounds.getWidth();
		else if (this.latitude < bounds.getX())
			latitude += bounds.getX()+bounds.getWidth();
		if (this.longitude > bounds.getY() + bounds.getHeight()) 
			this.latitude = bounds.getY() + latitude % bounds.getHeight();
		else if (this.latitude < bounds.getY())
			latitude += bounds.getY() + bounds.getHeight();
				
		if(DEBUG){
			Log.printLine(str);
			if(logStatus)
				Log.disable();
		}
	} 
	/**
	 * This function changes the old direction of movement and velocity of the device.
	 * @param v new value for movement of device
	 */
	public void updateDirection(Vector v){
		this.movementVector = v;
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
	public boolean isMobile() {
		return isMobile;
	}
	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}
}

