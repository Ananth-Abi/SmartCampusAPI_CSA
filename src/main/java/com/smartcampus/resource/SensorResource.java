package com.smartcampus.resource;

import com.smartcampus.dao.DataStore;
import com.smartcampus.exception.DataNotFoundException;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/sensors")
public class SensorResource {

    // GET /api/v1/sensors - Get all sensors (with optional ?type= filter)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.sensors.values());

        // Filter by type if query param provided
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor sensor : allSensors) {
                if (sensor.getType().equalsIgnoreCase(type)) {
                    filtered.add(sensor);
                }
            }
            return filtered;
        }
        return allSensors;
    }

    // GET /api/v1/sensors/{sensorId} - Get specific sensor
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Sensor getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new DataNotFoundException("Sensor with ID '" + sensorId + "' not found.");
        }
        return sensor;
    }

    // POST /api/v1/sensors - Register a new sensor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        // Validate roomId exists
        if (sensor.getRoomId() == null || !DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Cannot register sensor: Room with ID '" + sensor.getRoomId() + "' does not exist in the system."
            );
        }

        if (DataStore.sensors.containsKey(sensor.getId())) {
            throw new IllegalArgumentException("Sensor with ID '" + sensor.getId() + "' already exists.");
        }

        // Save sensor
        DataStore.sensors.put(sensor.getId(), sensor);

        // Link sensor to room
        Room room = DataStore.rooms.get(sensor.getRoomId());
        room.getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // DELETE /api/v1/sensors/{sensorId} - Delete a sensor
    @DELETE
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new DataNotFoundException("Sensor with ID '" + sensorId + "' not found.");
        }

        // Remove sensor from its room
        Room room = DataStore.rooms.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().remove(sensorId);
        }

        // Remove sensor and its readings
        DataStore.sensors.remove(sensorId);
        DataStore.sensorReadings.remove(sensorId);

        return Response.noContent().build();
    }

    // Sub-resource locator for readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
