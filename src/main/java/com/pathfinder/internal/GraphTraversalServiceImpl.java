package com.pathfinder.internal;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.api.TransitEdge;
import com.pathfinder.api.TransitPath;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class GraphTraversalServiceImpl implements GraphTraversalService {

  private GraphDAO dao;
  private Random random;
  private static final long ONE_MIN_MS = 1000 * 60;

  public GraphTraversalServiceImpl(GraphDAO dao) {
    this.dao = dao;
    this.random = new Random();
  }

  public List<TransitPath> findShortestPath(final String originNode,
                                            final String destinationNode,
                                            final Properties limitations) {
    LocalDateTime date = nextDate(LocalDateTime.now());

    List<String> allVertices = dao.listAllNodes();
    allVertices.remove(originNode);
    allVertices.remove(destinationNode);

    final int candidateCount = getRandomNumberOfCandidates();
    final List<TransitPath> candidates = new ArrayList<TransitPath>(candidateCount);

    for (int i = 0; i < candidateCount; i++) {
      allVertices = getRandomChunkOfNodes(allVertices);
      final List<TransitEdge> transitEdges = new ArrayList<TransitEdge>(allVertices.size() - 1);
      final String firstLegTo = allVertices.get(0);

      LocalDateTime fromDate = nextDate(date);
      LocalDateTime toDate = nextDate(fromDate);
      date = nextDate(toDate);

      transitEdges.add(new TransitEdge(
        dao.getTransitEdge(originNode, firstLegTo),
        originNode, firstLegTo, fromDate, toDate));

      for (int j = 0; j < allVertices.size() - 1; j++) {
        final String curr = allVertices.get(j);
        final String next = allVertices.get(j + 1);
        fromDate = nextDate(date);
        toDate = nextDate(fromDate);
        date = nextDate(toDate);
        transitEdges.add(new TransitEdge(dao.getTransitEdge(curr, next), curr, next, fromDate, toDate));
      }

      final String lastLegFrom = allVertices.get(allVertices.size() - 1);
      fromDate = nextDate(date);
      toDate = nextDate(fromDate);
      transitEdges.add(new TransitEdge(
        dao.getTransitEdge(lastLegFrom, destinationNode),
        lastLegFrom, destinationNode, fromDate, toDate));

      candidates.add(new TransitPath(transitEdges));
    }

    return candidates;
  }

  private LocalDateTime nextDate(LocalDateTime date) {
    return date.plusDays(1).plusMinutes(random.nextInt(1000) - 500);
  }

  private int getRandomNumberOfCandidates() {
    return 3 + random.nextInt(3);
  }

  private List<String> getRandomChunkOfNodes(List<String> allNodes) {
    Collections.shuffle(allNodes);
    final int total = allNodes.size();
    final int chunk = total > 4 ? 1 + new Random().nextInt(5) : total;
    return allNodes.subList(0, chunk);
  }

}
