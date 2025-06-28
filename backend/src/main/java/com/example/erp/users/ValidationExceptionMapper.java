package com.example.erp.users;

import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	
	@Override
	public Response toResponse(ConstraintViolationException exception) {
		String errors = exception.getConstraintViolations()
				.stream()
				.map(v -> v.getPropertyPath() + " " + v.getMessage())
				.collect(Collectors.joining("; "));
		
		return Response.status(Response.Status.BAD_REQUEST)
				.entity("{\"error\":\"" + errors + "\"}")
				.type(MediaType.APPLICATION_JSON)
				.build();
	}

}
