package se.citerus.dddsample.domain.shared;

/**
 * Specificaiton interface.
 * <p/>
 * Use {@link se.citerus.dddsample.domain.shared.AbstractSpecification} as base for creating specifications, and
 * only the method {@link #isSatisfiedBy(Object)} must be implemented.
 */
public interface Specification<T> {

  /**
   * Check if {@code t} is satisfied by the specification.
   *
   * @param t Object to test.
   * @return {@code true} if {@code t} satisfies the specification.
   */
  boolean isSatisfiedBy(T t);
}
