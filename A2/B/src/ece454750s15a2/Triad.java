package ece454750s15a2;

public class Triad {

    public int apex, leftNeighbour, rightNeighbour;

    public Triad(int apex, int leftNeighbour, int rightNeighbour) {
        this.apex = apex;
        this.leftNeighbour = leftNeighbour;
        assert leftNeighbour < rightNeighbour;
        this.rightNeighbour = rightNeighbour;
    }

    public int getApex() {
        return apex;
    }

    public int getLeft() {
        return leftNeighbour;
    }

    public int getRight() {
        return rightNeighbour;
    }
}