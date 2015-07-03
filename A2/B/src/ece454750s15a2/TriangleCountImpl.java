/**
 * ECE 454/750: Distributed Computing
 *
 * Code written by Wojciech Golab, University of Waterloo, 2015
 *
 * IMPLEMENT YOUR SOLUTION IN THIS FILE
 *
 */

package ece454750s15a2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TriangleCountImpl {
    private byte[] input;
    private int numCores;
    public static final double THREADS_PER_CORE = 1.0;

    public TriangleCountImpl(byte[] input, int numCores) {
        this.input = input;
        this.numCores = numCores;
    }

    public List<String> getGroupMembers() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("jkalpin");
        ret.add("jzanutto");
        return ret;
    }

    public List<Triangle> enumerateTriangles() throws IOException {
        List<Triangle> ret = new ArrayList<Triangle>();
        List<Set<Integer>> graph = getAdjacencyList(input);
        if (numCores == 1) {
            ret = singleThreadedEnumerateTriangles(graph);
            // ret = naiveEnumerateTriangles(adjacencyList);
        } else {
            ret = simpleMultiEnumerateTriangles(graph, numCores);
            // more focused solution that splits vertices evenly among cores
            // Three phases:
            // (Phase 1)
            // Map vertices to their respective adjacent vertices (i.e. triads)
            // (Apex vertex,adjacent vertices)
            // (Phase 2)
            // Check for duplicate vertices on overlapping spaces under each main vertex set selected
            // (Phase 3)
            // Trivially calculate triad groupings
        }

        return ret;
    }

    public List<Triangle> singleThreadedEnumerateTriangles(List<Set<Integer>> graph) {

        Set<BetterTriangle> triangles = new HashSet<BetterTriangle>();
        for (int i = 0; i < graph.size(); i++) {
            List<Integer> adjacencyList = new ArrayList<Integer>(graph.get(i));
            // Collections.sort(adjacencyList);
            for (int node = 0; node < adjacencyList.size() - 1; node++) {
                // skip this entire node if we're larger than the largest value
                // if (i > adjacencyList.get(adjacencyList.size() - 1)) {
                //     ++i;
                // } else if (i > adjacencyList.get(node)) {
                if (i > adjacencyList.get(node)) {
                    continue;
                    // int j = 1;
                    // while (i > adjacencyList.get(node) && node < adjacencyList.size() - 1) {
                    //     node = node + j;
                    //     j = j * 2;
                    //     // if we overshoot, restart the gallop
                    //     if (node >= adjacencyList.size() - 1 || i < adjacencyList.get(node)) {
                    //         node = node - (j / 2);
                    //         j = 1;
                    //     }
                    //     if (i > adjacencyList.get(node) && i < adjacencyList.get(node + 1)) {
                    //         break;
                    //     }
                    // }
                }
                // implementing a gallop search on results to find start point
                for (int secondNode = node + 1; secondNode < adjacencyList.size(); secondNode++) {
                    if (graph.get(adjacencyList.get(node)).contains(adjacencyList.get(secondNode))) {
                        BetterTriangle t = new BetterTriangle(i, adjacencyList.get(node), adjacencyList.get(secondNode));
                        triangles.add(t);
                    }
                }
            }
        }
        return convertResults(triangles);
    }

    public List<Triangle> simpleMultiEnumerateTriangles(List<Set<Integer>> graph, int ncores) {
        Set<BetterTriangle> triangles;
        ExecutorService service = Executors.newFixedThreadPool(ncores);
        GraphIndexer indexer = new GraphIndexer(graph.size());
        for (int i = 0; i < ncores; i++) {
            service.submit(new PartitionedTriangleCounter(graph, indexer));
        }

        service.shutdown();

        try {
            service.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.err.println("Task cancelled before finishing calculations. Exiting...");
            System.exit(0);
        }
        triangles = PartitionedTriangleCounter.getResults();
        return convertResults(triangles);
    }

    public List<Triangle> naiveEnumerateTriangles(List<Set<Integer>> graph) {
        // this code is single-threaded and ignores numCores
        List<BetterTriangle> triangles = new ArrayList<BetterTriangle>();
        // naive triangle counting algorithm
        for (int i = 0; i < graph.size(); i++) {
            List<Integer> n1 = new ArrayList<Integer>(graph.get(i));
            for (int j : n1) {
                List<Integer> n2 = new ArrayList<Integer>(graph.get(j));
                for (int k : n2) {
                    List<Integer> n3 = new ArrayList<Integer>(graph.get(k));
                    for (int l : n3) {
                        if (i < j && j < k && l == i) {
                            triangles.add(new BetterTriangle(i, j, k));
                        }
                    }
                }
            }
        }
        return convertResults(triangles);
    }

    public List<Triangle> convertResults(Collection<BetterTriangle> triangles) {
        return new ArrayList<Triangle>(triangles);
    }


    public List<Set<Integer>> getAdjacencyList(byte[] data) throws IOException {
        long startTime = System.currentTimeMillis();
        InputStream istream = new ByteArrayInputStream(data);
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String strLine = br.readLine();


        String[] parsedLine = strLine.split(" ");
        int numVertices = Integer.parseInt(parsedLine[0]);

        List<Set<Integer>> adjacencyList = new ArrayList<Set<Integer>>(numVertices);

        Set<Integer> adjSet;
        while ((strLine = br.readLine()) != null && !strLine.equals("")) {
            String[] parts = strLine.split(": ", 2);
            int vertex = Integer.parseInt(parts[0]);
            adjSet = new HashSet<Integer>();
            int pos = 0, end;
            if (parts.length > 1) {
                while ((end = parts[1].indexOf(' ', pos)) >= 0) {
                    adjSet.add(Integer.valueOf(parts[1].substring(pos, end)));
                    pos = end + 1;
                }
            }

            adjacencyList.add(vertex, adjSet);
        }

        br.close();
        long diffTime = System.currentTimeMillis() - startTime;
        System.out.println("Parsing took " + diffTime + "ms");
        return adjacencyList;
    }
}
