package com.smartcampus.resource;

import com.smartcampus.dao.DataStore;
import com.smartcampus.exception.DataNotFoundException;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class RoomResource {

    // GET /api/v1/rooms - Get all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }

    // GET /api/v1/rooms/{roomId} - Get specific room
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new DataNotFoundException("Room with ID '" + roomId + "' not found.");
        }
        return room;
    }

    // POST /api/v1/rooms - Create a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            throw new IllegalArgumentException("Room ID cannot be empty.");
        }
        if (DataStore.rooms.containsKey(room.getId())) {
            throw new IllegalArgumentException("Room with ID '" + room.getId() + "' already exists.");
        }
        DataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} - Delete a room (blocked if sensors exist)
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new DataNotFoundException("Room with ID '" + roomId + "' not found.");
        }
        // Business Logic: Cannot delete room if it still has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Cannot delete room '" + roomId + "'. It still has " +
                room.getSensorIds().size() + " active sensor(s) assigned to it."
            );
        }
        DataStore.rooms.remove(roomId);
        return Response.noContent().build();
    }
}
