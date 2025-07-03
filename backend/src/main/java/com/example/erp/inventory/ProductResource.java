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
			title = "ERP Inventory Product API",
			version = "1.0",
			description = "CRUD API for products management"
		)
	)
@Path("/inv/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
	@Inject
	private ProductRepository repo;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addProduct(@Valid Product product, @Context UriInfo uriInfo) {
		Product created = repo.save(product);
		UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(created.getId()));
		return Response.created(builder.build()).entity(created).build();
	}
	
	@GET
	public List<Product> getProducts(@QueryParam("offset") @DefaultValue("0") int offset,
									@QueryParam("limit") @DefaultValue("20") int limit) {
		return repo.findAll(offset, limit);
	}
	
	@GET
	@Path("{id}")
	public Response getProductById(@PathParam("id") Long id) {
		Product product = repo.findById(id);
		
		if (product == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok(product).build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProduct(@PathParam("id") Long id, @Valid Product product) {
		
		Product existing = repo.findById(id);
		if (existing == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		existing.setName(product.getName());
		existing.setDescription(product.getDescription());
		existing.setPrice(product.getPrice());
		existing.setSku(product.getSku());
		
		repo.save(existing);
		return Response.ok(existing).build();
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteProduct(@PathParam("id") Long id) {
		if (!repo.delete(id)) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.noContent().build();
	}
}
