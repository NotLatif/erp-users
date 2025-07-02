package com.example.erp.inventory;


import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import jakarta.ws.rs.Path;

@OpenAPIDefinition(
		info = @Info(
			title = "ERP Inventory API",
			version = "1.0",
			description = "CRUD API for inventory management"
		)
	)
@Path("/inv")
public class InventoryResource {

}
