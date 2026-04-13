package com.smartcampus.resource;

import com.smartcampus.dao.DataStore;
import com.smartcampus.exception.DataNotFoundException;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings - Get all readings for a sensor
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        // Verify sensor exists
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new DataNotFoundException("Sensor with ID '" + sensorId + "' not found.");
        }
        List<SensorReading> readings = DataStore.sensorReadings.get(sensorId);
        if (readings == null) {
            return new ArrayList<>();
        }
        return readings;
    }

    // POST /api/v1/sensors/{sensorId}/readings - Add a new reading
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        // Verify sensor exists
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new DataNotFoundException("Sensor with ID '" + sensorId + "' not found.");
        }

        // Block if sensor is in MAINTENANCE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is currently under MAINTENANCE and cannot accept new readings."
            );
        }

        // Generate ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Save reading
        DataStore.sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        // Update sensor's currentValue (side effect)
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
