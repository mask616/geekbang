package org.geektimes.work.project.repository;

import org.geektimes.work.project.domain.User;
import org.geektimes.work.project.jpa.DelegatingEntityManager;

import javax.annotation.Resource;
import javax.persistence.EntityTransaction;

public class UserRepository {

	@Resource(name = "bean/EntityManager")
	private DelegatingEntityManager entityManager;

	public boolean save(User user) {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			entityManager.persist(user);
			entityTransaction.commit();
			return true;
		} catch (Exception ex) {
			entityTransaction.rollback();
		}

		return false;

	}
}
