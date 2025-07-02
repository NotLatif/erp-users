package com.example.erp.inventory;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProductRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public Product save(Product product) {
		if (product.getId() == null) {
			em.persist(product);
		} else {
			product = em.merge(product);
		}
		return product;
	}
	
	public List<Product> findAll(int offset, int limit) {
		return em.createQuery("SELECT p FROM Product p", Product.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
	
	public Product findById(Long id) {
		return em.find(Product.class, id);
	}
	
	@Transactional
	public boolean delete(long id) {
		Product product = em.find(Product.class, id);
		
		if (product != null) {
			em.remove(product);
			return true;
		}
		return false;
	}
}
