/**
 * ECE 454/750: Distributed Computing
 *
 * Code written by Wojciech Golab, University of Waterloo, 2015
 *
 * DO NOT MODIFY THIS FILE
 *
 */

package ece454750s15a2;

import java.io.*;
import java.util.*;

public class Triangle {
    private int x, y, z;

    public Triangle(int x, int y, int z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }
    
    public String toString() {
	return x + " " + y + " " + z;
    }
}
