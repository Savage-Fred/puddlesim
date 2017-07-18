package org.fog.utils;

public class Config {

	public static final double RESOURCE_MGMT_INTERVAL = 100;
	public static final int SPEED_OF_LIGHT = 299792458;		/* Speed of Light in m/s */
	public static int MAX_SIMULATION_TIME = 1000000;
	public static int RESOURCE_MANAGE_INTERVAL = 100;
	public static String FOG_DEVICE_ARCH = "x86";
	public static String FOG_DEVICE_OS = "Linux";
	public static String FOG_DEVICE_VMM = "Xen";
	public static double DEFAULT_FLOW_CAPACITY = 10.0;
	public static double FOG_DEVICE_TIMEZONE = 10.0;
	public static double FOG_DEVICE_COST = 3.0;
	public static double FOG_DEVICE_COST_PER_MEMORY = 0.05;
	public static double FOG_DEVICE_COST_PER_STORAGE = 0.001;
	public static double FOG_DEVICE_COST_PER_BW = 0.0;
	public static double LOCATION_UPDATE_INTERVAL = 10; /* Interval between location updates. */
}
