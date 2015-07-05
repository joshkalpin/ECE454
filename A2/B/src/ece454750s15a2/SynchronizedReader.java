package ece454750s15a2;

import java.io.BufferedReader;
import java.io.IOException;

public class SynchronizedReader {

    private BufferedReader in;

    public SynchronizedReader(BufferedReader in) {
        this.in = in;
    }

    public String nextLine() throws IOException {
        String ret = in.readLine();

        if (ret == null || ret.equals("")) {
            System.out.println("Done");
            throw new IOException("Empty input");
        }

        return ret;
    }
}
