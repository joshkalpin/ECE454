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
        Server server = new BEServer(args);
        server.start();
    }

    public BEServer(String[] args) {
        super(args);
    }

    @Override
    protected void start() {
        try {
            TServerTransport passwordTransport = new TServerSocket(this.getPPort());
            A1Password.Processor passwordProcessor = new A1Password.Processor(new A1PasswordHandler());
            TThreadPoolServer.Args passwordArgs = new TThreadPoolServer.Args(passwordTransport);
            passwordArgs.maxWorkerThreads(getNCores());
            final TServer passwordServer =
                new TThreadPoolServer(passwordArgs.processor(passwordProcessor));
            System.out.println("Starting BE password service " + this.getPPort() + "...");

            Runnable passwordHandler = new Runnable() {
                public void run() {
                    passwordServer.serve();
                }
            };

            new Thread(passwordHandler).start();

            TServerTransport managementTransport = new TServerSocket(this.getMPort());
            A1Management.Processor managementProcessor = new A1Management.Processor(new A1ManagementHandler());
            TThreadPoolServer.Args managementArgs = new TThreadPoolServer.Args(managementArgs);
            managementArgs.maxWorkerThreads(getNCores());
            final TServer managementServer =
                new TThreadPoolServer(managementArgs.processor(managementProcessor));
            System.out.println("Starting BE management server " + this.getMPort() + "...");

            Runnable managementHandler = new Runnable() {
                public void run() {
                    managementServer.serve();
                }
            };

            new Thread(managementHandler).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
