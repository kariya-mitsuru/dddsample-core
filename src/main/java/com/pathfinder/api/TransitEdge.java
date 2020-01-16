package com.pathfinder.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.io.Serializable;
import java.util.Date;

/**
 * Represents an edge in a path through a graph,
 * describing the route of a cargo.
 *  
 */
@Getter
@RequiredArgsConstructor
public final class TransitEdge implements Serializable {

  private final String edge;
  private final String fromNode;
  private final String toNode;
  private final Date fromDate;
  private final Date toDate;
}
