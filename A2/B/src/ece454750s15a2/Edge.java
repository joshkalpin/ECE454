package ece454750s15a2;

import java.io.*;
import java.util.*;

public class Edge {
    private int lower;
    private int higher;

    public Edge(int lower, int higher) {
        this.lower = lower;
        this.higher = higher;
    }

    public int getLower() {
        return lower;
    }

    public int getHigher() {
        return higher;
    }

    public boolean equals(Edge e2) {
        if (this.getLower() == e2.getLower() && this.getHigher() == e2.getHigher()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = 37;
        int prime = 131;
        result = prime * result + getLower();
        result = prime * result + getHigher();
        return result;
    }
}