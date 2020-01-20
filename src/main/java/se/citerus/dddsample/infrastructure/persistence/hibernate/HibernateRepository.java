package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Functionality common to all Hibernate repositories.
 */
public abstract class HibernateRepository {

  private SessionFactory sessionFactory;

  public HibernateRepository(final SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  protected Session getSession() {
    return sessionFactory.getCurrentSession();
  }

}
