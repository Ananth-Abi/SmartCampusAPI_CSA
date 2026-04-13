package com.smartcampus.exception;

import com.smartcampus.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

// 404 - Resource Not Found
@Provider
public class DataNotFoundExceptionMapper implements ExceptionMapper<DataNotFoundException> {
    @Override
    public Response toResponse(DataNotFoundException exception) {
        ErrorMessage error = new ErrorMessage(
            exception.getMessage(), 404,
            "https://smartcampus.edu/api/docs/errors"
        );
        return Response.status(Status.NOT_FOUND).entity(error).build();
    }
}
