package ece454750s15a2;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class PartitionedTriangleCounter implements Runnable {
    private List<Set<Integer>> graph;
    private GraphIndexer indexer;
    private static Set<BetterTriangle> results = Collections.newSetFromMap(new ConcurrentHashMap<BetterTriangle, Boolean>());


    public PartitionedTriangleCounter(List<Set<Integer>> graph, GraphIndexer indexer) {
        this.graph = graph;
        this.indexer = indexer;
    }

    public void run() {
        try {
            for(int i = indexer.getNext(); i < graph.size(); i = indexer.getNext()) {
                List<Integer> adjacencyList = new ArrayList<Integer>(graph.get(i));

                for (int node = 0; node < adjacencyList.size() - 1; node++) {
                    if (i > adjacencyList.get(node)) {
                        continue;
                    }

                    for (int secondNode = node + 1; secondNode < adjacencyList.size(); secondNode++) {
                        if (graph.get(adjacencyList.get(node)).contains(adjacencyList.get(secondNode))) {
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
