package ece454750s15a2;

/**
 * Created by jkalpin on 15-07-02.
 */
public class GraphIndexer {

    private int current;
    private int max;

    public GraphIndexer(int max) {
        this.max = max;
    }

    public synchronized int getNext() throws FinishedException {
        if (current == max) {
            throw new FinishedException();
        }

        return current++;
    }

    public class FinishedException extends Exception {

    }

}
