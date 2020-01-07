package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.List;

@Repository
public class LocationRepositoryHibernate extends HibernateRepository implements LocationRepository {

  public LocationRepositoryHibernate(final SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Location find(final UnLocode unLocode) {
    return getSession().
      createQuery("from Location where unLocode = ?0", Location.class).
      setParameter(0, unLocode).
      uniqueResult();
  }

  public List<Location> findAll() {
    return getSession().createQuery("from Location", Location.class).list();
  }

}
