import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

import ece454750s15a1.*;

public class FEServer extends Server {
    public static void main(String[] args) {
        final Server server = new FEServer(args);
        Runnable bgServer = new Runnable() {
            public void run() {
                server.start();
            }
        };

        new Thread(bgServer).start();
    }

    public FEServer(String[] args) {
        super(args);
    }

    protected void start() {
        try {
            TServerTransport serverTransport = new TServerSocket(this.getMPort());
            A1Management.Processor processor = new A1Management.Processor(new A1ManagementHandler());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.println("Starting FE Node on port " + this.getMPort() + "...");

            server.serve();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
