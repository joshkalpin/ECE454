import ece454750s15a1.A1Management;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;

public class A1PerfCounterClient {

    private List<String> feConnections;
    private List<String> beConnections;

    public List<String> getFEConnections() {
        return feConnections;
    }

    public List<String> getBEConnections() {
        return beConnections;
    }

    public static void main(String [] args) {
        A1PerfCounterClient service = new A1PerfCounterClient(args);
        System.out.println("Frontend Node Perf Counters:");
        for (String connection : service.getFEConnections()) {
            String[] info = connection.split(":");
            service.outputPerfCounters(info);
        }

        System.out.println("Backend Node Perf Counters:");
        for (String connection : service.getBEConnections()) {
            String[] info = connection.split(":");
            service.outputPerfCounters(info);
        }

    }

    public A1PerfCounterClient(String[] args) {
        feConnections = new ArrayList<String>();
        beConnections = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fe")) {
                feConnections.add(args[++i]);
            } else if (args[i].equals("-be")) {
                beConnections.add(args[++i]);
            } else {
                System.err.println("Option not available: " + args[i]);
                System.exit(0);
            }
        }
    }

    private void outputPerfCounters(String[] info) {
        TTransport transport;
        try {
            transport = new TSocket(info[0], Integer.parseInt(info[1]));
            System.out.println("Host: " + info[0] + " Port: " + info[1]);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            A1Management.Client client = new A1Management.Client(protocol);

            System.out.println(client.getPerfCounters());
            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}