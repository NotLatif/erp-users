package com.example.erp.users;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserRepository {
	
	@PersistenceContext
	private EntityManager em; // gives access to JPA operations

//	IN MEMORY HASHMAP "DB" //
//	private static final Map<Long, User> users = new HashMap<>();
//	private static final AtomicLong idCounter = new AtomicLong();
//	
//	//not thread safe
//	private static final UserRepository INSTANCE = new UserRepository();
//	public static UserRepository getInstance() { return INSTANCE; };
	
	@Transactional
	public User save(User user) { // better to only return the changed id?
//		if (user.getId() == null) {
//			user.setId(idCounter.incrementAndGet());
//		}
//		users.put(user.getId(), user);
//		return user;
		if (user.getId() == null) {
			em.persist(user);
		} else {
			user = em.merge(user);
		}
		return user;
	}
	
	public List<User> findAll(int offset, int limit) {
//		return new ArrayList<>(users.values());
		return em.createQuery("SELECT u FROM User u", User.class)
				.setFirstResult(offset)
				.setMaxResults(limit)
				.getResultList();
	}
	
	public User findById(Long id) {
//		return users.get(id);
		return em.find(User.class, id);
	}
	
	@Transactional
	public boolean delete(Long id) {
//		return users.remove(id) != null;
		User user = em.find(User.class, id);
		if (user != null) {
			em.remove(user);
			return true;
		}
		return false;
	}

}
