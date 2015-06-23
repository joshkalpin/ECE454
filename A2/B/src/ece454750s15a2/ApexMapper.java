package ece454750s15a2;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ApexMapper implements Callable<List<Edge>> {

    private Set<Integer> neighbours;
    private int node;

    public ApexMapper(int node, Set<Integer> neighbours) {
        this.node = node;
        this.neighbours = neighbours;
    }

    public List<Edge> call() {
        List<Edge> output = new ArrayList<Edge>();
        for (Integer neighbour : neighbours) {
            if (node < neighbour) {
                output.add(new Edge(node, neighbour));
            }
        }
        return output;
    }
}
