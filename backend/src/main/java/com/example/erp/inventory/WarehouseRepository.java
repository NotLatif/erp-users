package com.example.erp.inventory;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class WarehouseRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public Warehouse save(Warehouse warehouse) {
		if (warehouse.getId() == null) {
			em.persist(warehouse);
		} else {
			warehouse = em.merge(warehouse);
		}
		
		return warehouse;
	}
	
	public List<Warehouse> findAll(int offset, int limit) {
		return em.createQuery("SELECT w FROM Warehouse w", Warehouse.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
	
	public Warehouse findById(Long id) {
		return em.find(Warehouse.class, id);
	}
	
	@Transactional
	public boolean delete(Long id) {
		Warehouse wh = em.find(Warehouse.class, id);
		
		if (wh != null) {
			em.remove(wh);
			return true;
		}
		return false;
	}
}
