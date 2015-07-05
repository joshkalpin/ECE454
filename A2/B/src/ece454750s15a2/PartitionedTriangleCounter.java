package ece454750s15a2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PartitionedTriangleCounter implements Runnable {
    private Set<Integer>[] graph;
    private GraphIndexer indexer;
    private static Set<BetterTriangle> results = Collections.newSetFromMap(new ConcurrentHashMap<BetterTriangle, Boolean>());

    public PartitionedTriangleCounter(Set<Integer>[] graph, GraphIndexer indexer) {
        this.graph = graph;
        this.indexer = indexer;
    }

    public void run() {
        try {
            for(int i = indexer.getNext(); i < graph.length; i = indexer.getNext()) {
                List<Integer> adjacencyList = new ArrayList<Integer>(graph[i]);

                for (int node = 0; node < adjacencyList.size() - 1; node++) {
                    if (i > adjacencyList.get(node)) {
                        continue;
                    }

                    for (int secondNode = node + 1; secondNode < adjacencyList.size(); secondNode++) {
                        if (graph[adjacencyList.get(node)].contains(adjacencyList.get(secondNode))) {
                            BetterTriangle t = new BetterTriangle(i, adjacencyList.get(node), adjacencyList.get(secondNode));
                            results.add(t);
                        }
                    }
                }
            }
        } catch (GraphIndexer.FinishedException e) {
        }
    }

    public static Set<BetterTriangle> getResults() {
        return results;
    }
}
