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
			title = "ERP Inventory StockEntry API",
			version = "1.0",
			description = "CRUD API for user management"
		)
	)
@Path("/inv/stocks")
@Produces(MediaType.APPLICATION_JSON)
public class StockEntryResource {
	@Inject
	private StockEntryRepository repo;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addStock(@Valid StockEntry stock, @Context UriInfo uriInfo) {
		StockEntry created = repo.save(stock);
		UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(created.getId()));
		return Response.created(builder.build()).entity(created).build();
	}
	
	@GET
	public List<StockEntry> getStock(@QueryParam("offset") @DefaultValue("0") int offset,
									@QueryParam("limit") @DefaultValue("20") int limit) {
		return repo.findAll(offset, limit);
	}
	
	@GET
	@Path("{id}")
	public Response getStockById(@PathParam("id") Long id) {
		StockEntry stock = repo.findById(id);
		
		if (stock == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok(stock).build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateStock(@PathParam("id") Long id, @Valid StockEntry stock) {
		StockEntry existing = repo.findById(id);
		if (existing == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		existing.setProduct(stock.getProduct());
		existing.setQuantity(stock.getQuantity());
		existing.setWarehouse(stock.getWarehouse());
		existing.setUat(stock.getUat());							// TODO Date.now()
		
		repo.save(existing);
		return Response.ok(existing).build();
	}
	
	@DELETE
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteStock(@PathParam("id") Long id) {
		if (!repo.delete(id)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}
	
}
