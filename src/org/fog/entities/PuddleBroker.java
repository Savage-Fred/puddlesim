package org.fog.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.placement.ModulePlacement;
import org.fog.placement.ModulePlacementPolicy;
import org.fog.utils.AppModuleAddress;
import org.fog.utils.Config;
import org.fog.utils.FogEvents;
import org.fog.utils.FogUtils;
import org.fog.utils.Logger;
import org.fog.utils.TimeKeeper;


/**
 * @class       PuddleBroker 
 * @since       June 29, 2017
 * @author      William McCarty
 * NSF REU Summer 2017 
 *
 * <h1>Puddle Broker Class </h1>
 * <p>A puddle broker adds functionality to a puddlehead by allowing it to move applications and their modules to neighboring puddleheads. 
 */ 
public class PuddleBroker extends FogBroker {
    ////////////////////////////////////////
    /////////////// FIELDS 
    ////////////////////////////////////////
    private static final String LOG_TAG = "PUDDLE_BROKER";

    protected List<Integer> fogDeviceIds;
    protected List<Integer> sensorIds;
    protected List<Integer> actuatorIds;
    
    protected Map<Integer, FogDeviceCharacteristics> fogDeviceCharacteristics;
    protected Map<Integer, SensorCharacteristics> sensorCharacteristics;
    protected Map<Integer, ActuatorCharacteristics> actuatorCharacteristics;

    private Map<String, Application> applications;
    private Map<String, Double> appLaunchDelays;
    private Map<String, ModulePlacementPolicy> appModulePlacementPolicy;
}





















