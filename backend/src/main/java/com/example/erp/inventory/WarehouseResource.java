package com.example.erp.inventory;

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

@OpenAPIDefinition(
		info = @Info(
			title = "ERP Inventory Warehouse API",
			version = "1.0",
			description = "CRUD API for user management"
		)
	)
@Path("/inv/warehouses")
@Produces(MediaType.APPLICATION_JSON)
public class WarehouseResource {
	@Inject
	private WarehouseRepository repo;
	
	@Inject
	private StockEntryRepository stockRepo;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addWarehouse(@Valid Warehouse wh, @Context UriInfo uriInfo) {
		Warehouse created = repo.save(wh);
		UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(created.getId()));
		return Response.created(builder.build()).entity(created).build();
	}
	
	@GET
	public List<Warehouse> getWarehouses(@QueryParam("offset") @DefaultValue("0") int offset,
								      @QueryParam("limit") @DefaultValue("20") int limit) {
		return repo.findAll(offset, limit);
	}
	
	@GET
	@Path("{id}")
	public Response getWarehouseById(@PathParam("id") Long id) {
		Warehouse wh = repo.findById(id);
		
		if (wh == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok(wh).build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateWarehouse(@PathParam("id") Long id, @Valid Warehouse wh) {
		Warehouse existing = repo.findById(id);
		if (existing == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		existing.setName(wh.getName());
		existing.setLocation(wh.getLocation());
		
		repo.save(existing);
		return Response.ok(existing).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteWarehouse(@PathParam("id") Long id) {
		if (!repo.delete(id)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		stockRepo.deleteEntireWarehouse(id);
		
		return Response.noContent().build();
	}
}
