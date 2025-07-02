package com.example.erp.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "warehouses")
public class Warehouse {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Size(max = 50)
	@Column(nullable=false)
	private String name;

	@NotBlank
	@Size(max = 155)
	@Column(nullable=false)
	private String location;
	
	public Warehouse() {}
	
	public Warehouse(Long id, String name, String location) {
		this.id = id;
		this.name = name;
		this.location = location;
	}
	
	public Long getId() { return this.id; }
	public void setId(Long id) { this.id = id; }
	
	public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }

	public String getLocation() {return this.location; }
	public void setLocation(String location) { this.location = location; }
}
