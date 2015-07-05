package ece454750s15a2;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Created by jkalpin on 15-07-05.
 */
public class LineParser implements Runnable {

    private SynchronizedReader reader;
    private static Set<Integer>[] adjacencyList;

    public LineParser(SynchronizedReader reader) {
        this.reader = reader;
    }

    public static void init(int numVertices) {
        adjacencyList = new Set[numVertices];
    }

    public static Set<Integer>[] getResults() {
        return adjacencyList;
    }

    @Override
    public void run() {
        try {
            while(true) {
                String line = reader.nextLine();
                String[] parts = line.split(": ", 2);
                int vertex = Integer.parseInt(parts[0]);
                Set<Integer> adjSet = new HashSet<Integer>();
                int pos = 0, end;
                if (parts.length > 1) {
                    while ((end = parts[1].indexOf(' ', pos)) >= 0) {
                        adjSet.add(Integer.valueOf(parts[1].substring(pos, end)));
                        pos = end + 1;
                    }
                }

                adjacencyList[vertex] = adjSet;
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
