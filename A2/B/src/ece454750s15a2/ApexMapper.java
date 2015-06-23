package ece454750s15a2;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ApexMapper implements Callable<Map<Integer, List<Edge>>> {

    private Set<Integer> neighbours;
    private int node;

    public ApexMapper(int node, HashSet<Integer> neighbours) {
        this.node = node;
        this.neighbours = neighbours;
    }

    public Map<Integer, List<Edge>> call() {
        Map<Integer, List<Edge>> output = new HashMap<Integer, List<Edge>>();
        for (Integer neighbour : neighbours) {
            if (node < neighbour) {
                if (output.get(node) == null) {
                    output.put(node, new ArrayList<Edge>());
                }
                output.get(node).add(new Edge(node, neighbour));
            }
        }
        return output;
    }
}
