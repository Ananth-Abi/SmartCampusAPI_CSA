package com.smartcampus.dao;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    // Map for each model
    public static final Map<String, Room> rooms = new HashMap<>();
    public static final Map<String, Sensor> sensors = new HashMap<>();
    public static final Map<String, List<SensorReading>> sensorReadings = new HashMap<>();

    // Initialize with the sample data
    static {
        // Instantiate sample rooms
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        Room r3 = new Room("HALL-A", "Main Hall", 200);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
        rooms.put(r3.getId(), r3);

        // Instantiate sample sensors
        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "LAB-101");
        Sensor s3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "HALL-A");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);

        // Link sensors to rooms
        r1.getSensorIds().add("TEMP-001");
        r2.getSensorIds().add("CO2-001");
        r3.getSensorIds().add("OCC-001");

        // Sample Readings
        List<SensorReading> readings1 = new ArrayList<>();
        readings1.add(new SensorReading(22.5));
        readings1.add(new SensorReading(23.1));
        sensorReadings.put("TEMP-001", readings1);

        List<SensorReading> readings2 = new ArrayList<>();
        readings2.add(new SensorReading(400.0));
        sensorReadings.put("CO2-001", readings2);
    }
}
