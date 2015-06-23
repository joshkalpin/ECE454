package ece454750s15a2;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class TriadReducer implements Callable<Map<Edge, Triad>> {

    List<Edge> edgeList;
    Integer node;

    public TriadReducer(Integer node, List<Edge> edgeList) {
        this.edgeList = edgeList;
        this.node = node;

    }

    public Map<Edge, Triad> call() {
        Map<Edge, Triad> output = new HashMap<Edge, Triad>();
        for (int i = 0; i < edgeList.size() - 1; i++) {
            Edge e1 = edgeList.get(i);
            for (int j = i + 1; j < edgeList.size(); j++) {
                Edge e2 = edgeList.get(j);
                Triad t;
                if (e1.getHigher() > e2.getHigher()) {
                    t = new Triad(node, e2.getHigher(), e1.getHigher());
                } else {
                    t = new Triad(node, e1.getHigher(), e2.getHigher());
                }
                Edge e3 = new Edge(t.getLeft(), t.getRight());
                output.put(e3, t);
            }
        }
        return output;
    }

}