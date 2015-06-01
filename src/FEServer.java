import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.DiscoveryInfo;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FEServer extends Server {

    Logger logger;

    public static void main(String[] args) {
        Server server = new FEServer(args);
        server.start();
    }

    private DiscoveryInfo self;

    public FEServer(String[] args) {
        super(args);
        self = new DiscoveryInfo(getHost(), getMPort(), getPPort(), getNCores(), false);
        logger = LoggerFactory.getLogger(FEServer.class);
        logger.info("Node created...");
    }

    @Override
    protected void start() {
        try {
            logger.info("Starting server");
            TServerTransport managementServerSocket = new TServerSocket(this.getMPort());

            A1ManagementForwarder managementForwarder;
            List <DiscoveryInfo> seeds = getSeeds();
            boolean isSeed = false;
            for (DiscoveryInfo seed : seeds) {
                if (seed.getHost().equals(this.getHost())
                    && seed.getMport() == this.getMPort()) {

                    isSeed = true;
                }
            }
            if (isSeed) {
                logger.info("Server is seed node.");
                managementForwarder = new A1ManagementForwarder(seeds, self);
            } else {
                logger.info("Server is not seed node");
                managementForwarder = new A1ManagementForwarder(seeds);
            }

            logger.info(this.getHost() + ": opening " + this.getMPort() + " for management server...");

            A1Management.Processor managementProcessor = new A1Management.Processor(managementForwarder);
            TThreadPoolServer.Args managementArgs = new TThreadPoolServer.Args(managementServerSocket);
            final TServer managementServer = new TThreadPoolServer(managementArgs.processor(managementProcessor));

            logger.info(this.getHost() + ": opening " + this.getPPort() + " for password forwarder...");

            TServerTransport passwordServerSocket = new TServerSocket(this.getPPort());
            A1PasswordForwarder passwordForwarder = new A1PasswordForwarder(managementForwarder);
            A1Password.Processor passwordProcessor = new A1Password.Processor(passwordForwarder);
            TThreadPoolServer.Args passwordArgs = new TThreadPoolServer.Args(passwordServerSocket);
            final TServer passwordServer = new TThreadPoolServer(passwordArgs.processor(passwordProcessor));

            if (!isSeed) {
                for (DiscoveryInfo seed : seeds) {
                    logger.info("Registering with seed " + seed.getHost() + ":" + seed.getMport());
                    register(seed.getHost(), seed.getMport(), logger, self);
                }
            }

            Runnable passwordService = new Runnable() {
                public void run() {
                    passwordServer.serve();
                }
            };
            logger.info("Starting password forwarder.");
            new Thread(passwordService).start();
            logger.info("Starting management server.");
            managementServer.serve();

        }
        catch (Exception e) {
            logger.error("Exception thrown");
            e.printStackTrace();
        }
    }
}
