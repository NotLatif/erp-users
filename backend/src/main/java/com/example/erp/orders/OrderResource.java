package com.example.erp.orders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import com.example.erp.inventory.Product;
import com.example.erp.inventory.ProductRepository;
import com.example.erp.orders.dto.OrderRequest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@OpenAPIDefinition(
		info = @Info(
			title = "ERP Orders API",
			version = "1.0",
			description = "CRUD API for order management"
		)
	)
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {
	@Inject OrderRepository repo;
	
	@Inject ProductRepository productRepo;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrder(@Valid OrderRequest req, @Context UriInfo uriInfo) {
        // Build Order
        Order order = new Order();
        order.setOrderNo(req.orderNo);
        order.setCustomer(req.customer);
        order.setStatus(Order.Status.valueOf(req.status.toUpperCase()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Build OrderItems
        List<OrderItem> items = new ArrayList<>();
        for (OrderRequest.CartItem ci : req.cart) {
            Product product = productRepo.findById(ci.productId);
            if (product == null) {
                throw new WebApplicationException("Product not found: " + ci.productId, 400);
            }
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(ci.quantity);
            items.add(item);
        }
        order.setCart(items);

        repo.save(order);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(order.getId().toString());
        return Response.created(builder.build()).entity(order).build();
    }
	
	@GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        Order order = repo.findById(id);
        if (order == null) {
            throw new NotFoundException("Order not found with id: " + id);
        }
        return Response.ok(order).build();
    }

    @GET
    public List<Order> getOrders(@QueryParam("offset") @DefaultValue("0") int offset,
                              @QueryParam("limit") @DefaultValue("20") int limit) {
        return repo.findAll(offset, limit);
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOrder(@PathParam("id") Long id, @Valid OrderRequest req) {
        Order existing = repo.findById(id);
        if (existing == null) {
            throw new NotFoundException("Order not found with id: " + id);
        }

        // Update fields
        existing.setOrderNo(req.orderNo);
        existing.setCustomer(req.customer);
        existing.setStatus(Order.Status.valueOf(req.status.toUpperCase()));
        existing.setUpdatedAt(LocalDateTime.now());

        // Remove existing items (orphanRemoval=true takes care of DB)
        existing.getCart().clear();

        // Add new items
        List<OrderItem> items = new ArrayList<>();
        for (OrderRequest.CartItem ci : req.cart) {
            Product product = productRepo.findById(ci.productId);
            if (product == null) {
                throw new WebApplicationException("Product not found: " + ci.productId, 400);
            }
            OrderItem item = new OrderItem();
            item.setOrder(existing);
            item.setProduct(product);
            item.setQuantity(ci.quantity);
            items.add(item);
        }
        existing.setCart(items);

        repo.save(existing);

        return Response.ok(existing).build();
    }
    
    
    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        boolean deleted = repo.delete(id);
        if (!deleted) {
            throw new NotFoundException("Order not found with id: " + id);
        }
        return Response.noContent().build();
    }
    
}
