/**
 * ECE 454/750: Distributed Computing
 *
 * Code written by Wojciech Golab, University of Waterloo, 2015
 *
 * IMPLEMENT YOUR SOLUTION IN THIS FILE
 *
 */

package ece454750s15a2;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TriangleCountImpl {
    private byte[] input;
    private int numCores;
    private int numVertices;
    public static final int THREADS_PER_CORE = 1;

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
        List<HashSet<Integer>> adjacencyList = getAdjacencyList(input);
        if (numCores == 1) {
            ret = singleThreadedEnumerateTriangles(adjacencyList);
            // ret = naiveEnumerateTriangles(adjacencyList);
        } else {
            ApexMapper mapper = new ApexMapper(1, adjacencyList.get(1));
            List<Edge> mappedResult = mapper.call();
            // System.out.println(mappedResult);
            TriadReducer reducer = new TriadReducer(1, mappedResult);
            List<Triad> triads = reducer.call();
            // System.out.println(triads);
            TriangleReducer tReducer = new TriangleReducer(triads, ret, adjacencyList);
            tReducer.run();
            System.out.println(ret);
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

    private List<Triangle> multiThreadedEnumerateTriangles(List<HashSet<Integer>> graph) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(this.numCores * THREADS_PER_CORE);

        List<Callable<Triad>> mapJobs = new ArrayList<Callable<Triad>>();

        List<Future<Triad>> triadResults = service.invokeAll(mapJobs);
        return null;
    }

    public List<Triangle> singleThreadedEnumerateTriangles(List<HashSet<Integer>> graph) {
        Set<BetterTriangle> triangles = new HashSet<BetterTriangle>();
        for (int i = 0; i < graph.size(); i++) {
            List<Integer> adjacencyList = new ArrayList<Integer>(graph.get(i));
            for (int node = 0; node < adjacencyList.size() - 1; node++) {
                if (i > adjacencyList.get(node)) {
                    continue;
                }
                for (int secondNode = node + 1; secondNode < adjacencyList.size(); secondNode++) {
                    if (graph.get(adjacencyList.get(node)).contains(adjacencyList.get(secondNode))) {
                        BetterTriangle t = new BetterTriangle(i, adjacencyList.get(node), adjacencyList.get(secondNode));
                        triangles.add(t);
                    }
                }
            }
        }
        return convertResults(new ArrayList<BetterTriangle>(triangles));
    }

    public List<Triangle> naiveEnumerateTriangles(List<HashSet<Integer>> graph) {
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

    public List<Triangle> convertResults(List<BetterTriangle> triangles) {
        // Collections.sort(triangles, new TriangleComparator());
        List<Triangle> ret = new ArrayList<Triangle>();
        for (BetterTriangle t : triangles) {
            ret.add(t.toTriangle());
        }
        return ret;
    }


    public List<Set<Integer>> getAdjacencyList(byte[] data) throws IOException {
        InputStream istream = new ByteArrayInputStream(data);
        BufferedReader br = new BufferedReader(new InputStreamReader(istream));
        String strLine = br.readLine();

        if (!strLine.contains("vertices") || !strLine.contains("edges")) {
            System.err.println("Invalid graph file format. Offending line: " + strLine);
            System.exit(-1);
        }

        String parts[] = strLine.split(" ");
        int numVertices = Integer.parseInt(parts[0]);
        int numEdges = Integer.parseInt(parts[2]);
        System.out.println("Found graph with " + numVertices + " vertices and " + numEdges + " edges");
        this.numVertices = numVertices;

        List<Set<Integer>> adjacencyList = new ArrayList<Set<Integer>>(numVertices);

        while ((strLine = br.readLine()) != null && !strLine.equals(""))   {
            Set<Integer> adjSet = new HashSet<Integer>();
            parts = strLine.split(": ");

            int vertex = Integer.parseInt(parts[0]);
            if (parts.length > 1) {
                parts = parts[1].split(" +");
                for (String part : parts) {
                    adjSet.add(Integer.parseInt(part));
                }
            }

            adjacencyList.add(vertex, adjSet);
        }

        br.close();
        return adjacencyList;
    }
}
