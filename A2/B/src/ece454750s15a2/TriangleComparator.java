package ece454750s15a2;

import java.io.*;
import java.util.*;

public class TriangleComparator implements Comparator<BetterTriangle> {
    @Override
    public int compare(BetterTriangle t1, BetterTriangle t2) {
        if (t1.equals(t2)) {
            return 0;
        }
        if (t1.x() < t2.x()) {
            return -1;
        } else if (t1.x() > t2.x()) {
            return 1;
        } else {
            if (t1.y() == t2.y()) {
                if (t1.z() < t2.z()) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (t1.y() < t2.y()) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}