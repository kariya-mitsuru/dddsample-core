package se.citerus.dddsample.domain.shared;

public class AlwaysFalseSpec implements Specification<Object> {
  public boolean isSatisfiedBy(Object o) {
    return false;
  }
}
