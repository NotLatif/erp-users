package com.example.erp.orders;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderRepository {
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public Order save(Order order) {
		if (order.getId() == null) {
			em.persist(order);
		} else {
			order = em.merge(order);
		}
		return order;
	}
	
	public List<Order> findAll(int offset, int limit) {
	    return em.createQuery(
	        "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.cart LEFT JOIN FETCH o.cart.product", Order.class)
	        .setFirstResult(offset)
	        .setMaxResults(limit)
	        .getResultList();
	}
	
	public Order findById(Long id) {
	    List<Order> results = em.createQuery(
	        "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.cart LEFT JOIN FETCH o.cart.product WHERE o.id = :id", Order.class)
	        .setParameter("id", id)
	        .getResultList();
	    return results.isEmpty() ? null : results.get(0);
	}
	
	@Transactional
	public boolean delete(Long id) {
		Order ord = em.find(Order.class, id);
		
		if (ord != null) {
			em.remove(ord);
			return true;
		}
		return false;
	}
}
