package se.citerus.dddsample.domain.shared;

public class AlwaysTrueSpec implements Specification<Object> {
  public boolean isSatisfiedBy(Object o) {
    return true;
  }
}
