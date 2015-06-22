package ece454750s15a2;

import java.io.*;
import java.util.*;

public class BetterTriangle {
    private int x, y, z;

    public BetterTriangle(int x, int y, int z) {
        int[] list = new int[3];
        list[0] = x;
        list[1] = y;
        list[2] = z;
        if (list[1] < list[0]) {
            int temp = list[0];
            list[0] = list[1];
            list[1] = temp;
        }

        if (list[2] < list[1]) {
            int temp = list[1];
            list[1] = list[2];
            list[2] = temp;
        }

        if (list[1] < list[0]) {
            int temp = list[0];
            list[0] = list[1];
            list[1] = temp;
        }

        this.x = list[0];
        this.y = list[1];
        this.z = list[2];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BetterTriangle)) {
            return false;
        }
        BetterTriangle t = (BetterTriangle)obj;
        if (x == t.x() && y == t.y() && z == t.z()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 31;
        int prime = 127;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public Triangle toTriangle() {
        return new Triangle(x, y, z);
    }

    public String toString() {
        return x + " " + y + " " + z;
    }

}