package com.example.erp.inventory;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StockEntryRepository {
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public StockEntry save(StockEntry stock) {
		if (stock.getId() == null) {	
			em.persist(stock);
		} else {
			stock = em.merge(stock);
		}
		return stock;
	}
	
	public List<StockEntry> findAll(int offset, int limit) {
		return em.createQuery("SELECT s FROM StockEntry s", StockEntry.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
	
	public StockEntry findById(Long id) {
		return em.find(StockEntry.class, id);
	}
	
	@Transactional
	public boolean delete(Long id) {
		StockEntry se = em.find(StockEntry.class, id);
		
		if(se != null) {
			em.remove(se);
			return true;
		}
		return false;
	}
	
	@Transactional
	public boolean deleteEntireWarehouse(Long warehouse_id) {
		//TODO
		System.out.println("Called deleteEntireWarehouse but method was not implemented");
		return false;
	}
}
