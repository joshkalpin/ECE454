import org.apache.thrift.server.*;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.transport.*;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ece454750s15a1.*;

public class BEServer extends Server {

    private DiscoveryInfo info;
    private Logger logger;


    public static int DISCOVERY_TIMEOUT = 10000;

    public DiscoveryInfo getInfo() {
        return info;
    }

    public static void main(String[] args) {
        Server server = new BEServer(args);
        server.start();
    }

    public BEServer(String[] args) {
        super(args);
        info = new DiscoveryInfo(this.getHost(), this.getMPort(), this.getPPort(), true);
        logger = LoggerFactory.getLogger(BEServer.class);
        logger.info("Node created...");
    }

    @Override
    protected void start() {
        try {
            TServerTransport passwordTransport = new TServerSocket(this.getPPort());
            A1Password.Processor passwordProcessor = new A1Password.Processor(new A1PasswordHandler());
            TThreadPoolServer.Args passwordArgs = new TThreadPoolServer.Args(passwordTransport);
            // passwordArgs.maxWorkerThreads(getNCores());
            final TServer passwordServer =
                new TThreadPoolServer(passwordArgs.processor(passwordProcessor));
            System.out.println("Starting BE password service " + this.getPPort() + "...");
            Runnable passwordHandler = new Runnable() {
                public void run() {
                    passwordServer.serve();
                }
            };

            logger.info("Attempting to start password service...");
            new Thread(passwordHandler).start();

            TServerTransport managementTransport = new TServerSocket(this.getMPort());
            A1Management.Processor managementProcessor = new A1Management.Processor(new A1ManagementHandler());
            TThreadPoolServer.Args managementArgs = new TThreadPoolServer.Args(managementTransport);
            // managementArgs.maxWorkerThreads(getNCores());
            final TServer managementServer =
                new TThreadPoolServer(managementArgs.processor(managementProcessor));
            System.out.println("Starting BE management server " + this.getMPort() + "...");
            Runnable managementHandler = new Runnable() {
                public void run() {
                    managementServer.serve();
                }
            };

            logger.info("Attempting to start management service...");
            new Thread(managementHandler).start();

            List<DiscoveryInfo> seeds = getSeeds();
            for (DiscoveryInfo seed : seeds) {
                register(seed.getHost(), seed.getMport());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void register(String host, int mPort) {
        try {
            logger.info("Registering with node " + host + ":" + mPort);
            TSocket transport;
            transport = new TSocket(host, mPort);
            TProtocol protocol = new TBinaryProtocol(transport);
            A1Management.Client client = new A1Management.Client(protocol);
            // set timeout to 10 seconds
            transport.setTimeout(DISCOVERY_TIMEOUT);
            transport.open();

            client.registerNode(getInfo());

            transport.close();
        } catch (Exception e) {
            logger.warn("Failed to register with " + host + ":" + mPort);
            e.printStackTrace();
        }
    }
}
