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

public class TriangleCount {
    public static void main(String[] args) throws Exception {
	int i = 0;
	int numCores = 0;
	String graphFileName = null;
	String outFileName = null;

	if (args.length == 6) {
	    while (i < args.length) {
		String argName = args[i];
		String argValue = args[i+1];
		if (argName.equals("-ncores")) {
		    numCores = Integer.parseInt(argValue);
		} else if (argName.equals("-if")) {
		    graphFileName = argValue;
		} else if (argName.equals("-of")) {
		    outFileName = argValue;
		} else {
		    System.err.println("Invalid argument: " + argName);
		    System.exit(-1);
		}
		i += 2;
	    }
	}

	if (numCores <= 0 || graphFileName == null || outFileName == null) {
	    System.err.println("Usage: java ece454750s15a2 -ncores num_cores -if input_file -of output_file");
	    System.exit(-1);
	}

	System.out.println("Running on " + numCores + " cores");
	System.out.println("Reading data from " + graphFileName);

	// read graph data from input file to main memory
	File infile = new File(graphFileName);
	InputStream in = new FileInputStream(infile);
	byte[] data = new byte[(int)infile.length()];
	in.read(data);
	in.close();

	// start the clock
	long startTime = System.currentTimeMillis();

	// do the computation
	TriangleCountImpl impl = new TriangleCountImpl(data, numCores);
	List<Triangle> ret = impl.enumerateTriangles();

	// stop the clock
	long endTime = System.currentTimeMillis();

	// write triangles to output file
	FileOutputStream fout = new FileOutputStream(outFileName);
	PrintWriter pw = new PrintWriter(fout);
	for (Triangle t: ret) {
	    pw.println(t);
	}
	pw.close();

	// report the running time of the computation
	long diffTime = endTime - startTime;
	System.out.println("Done in " + diffTime + "ms");
	System.out.println("Group members: " + impl.getGroupMembers());
    }
}
