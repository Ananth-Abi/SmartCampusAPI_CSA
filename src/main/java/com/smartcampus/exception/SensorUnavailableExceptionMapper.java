package com.smartcampus.exception;

import com.smartcampus.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// 403 - Sensor is in MAINTENANCE, cannot accept readings
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {
    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ErrorMessage error = new ErrorMessage(
            exception.getMessage(), 403,
            "https://smartcampus.edu/api/docs/errors"
        );
        return Response.status(Response.Status.FORBIDDEN).entity(error).build();
    }
}
