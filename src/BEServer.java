import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;

import ece454750s15a1.*;

public class BEServer extends Server {
    public static void main(String[] args) {
        final Server server = new BEServer(args);
        Runnable bgServer = new Runnable() {
            public void run() {
                server.start();
            }
        };

        new Thread(bgServer).start();
    }

    public BEServer(String[] args) {
        super(args);
    }

    protected void start() {
        try {
            TServerTransport serverTransport = new TServerSocket(this.getPPort());
            A1Password.Processor processor = new A1Password.Processor(new A1PasswordHandler());
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
            System.out.println("Starting BE Node on port " + this.getPPort() + "...");

            server.serve();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
