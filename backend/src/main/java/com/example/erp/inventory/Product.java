package com.example.erp.inventory;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	
	@NotBlank
	@Size(max=100)
	@Column(nullable = false)
	private String name;
	
	@NotBlank
	@Size(max=50)
	@Column(nullable = false, unique = true)
	private String sku;
	
	@NotNull
	private BigDecimal price;
	
	@NotBlank
	private String description;
	
	public Product() {}
	
	public Product(Long id, String name, String sku, BigDecimal price, String desc) {
		this.id = id;
		this.name = name;
		this.sku = sku;
		this.price = price;
		this.description = desc;
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getSku() { return sku; }
	public void setSku(String sku) { this.sku = sku; }
	
	public BigDecimal getPrice() { return price; }
	public void setPrice(BigDecimal price) { this.price = price; }
	
	public String getDescription() { return description; }
	public void setDescription(String desc) { this.description = desc; }
}
