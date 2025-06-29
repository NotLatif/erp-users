package com.example.erp.users;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

// API STUFF

@OpenAPIDefinition(
	info = @Info(
		title = "ERP Users API",
		version = "1.0",
		description = "CRUD API for user management"
	)
)
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	@Inject
	private UserRepository repo;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addUser(@Valid User user, @Context UriInfo uriInfo) {
		User created = repo.save(user);
		UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(created.getId()));
		return Response.created(builder.build()).entity(created).build();
	}
	
	@GET
	public List<User> getUsers(@QueryParam("offset") @DefaultValue("0") int offset,
	                          @QueryParam("limit") @DefaultValue("20") int limit) {
	    return repo.findAll(offset, limit);
	}

    @GET
    @Path("{id}")
    public Response getUserById(@PathParam("id") Long id) {
        User user = repo.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }
    
    @PUT
    @Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, @Valid User user) {
        User existing = repo.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existing.setUsername(user.getUsername());
        existing.setEmail(user.getEmail());
        repo.save(existing);
        return Response.ok(existing).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
    	
        if (!repo.delete(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}