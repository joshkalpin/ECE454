import ece454750s15a1.A1Management;
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
        self = new DiscoveryInfo(getHost(), getMPort(), getPPort(), false);
        logger = LoggerFactory.getLogger(FEServer.class);
        logger.info("Node created...");
    }

    @Override
    protected void start() {
        try {
            TServerTransport serverTransport = new TServerSocket(this.getMPort());

            A1ManagementForwarder forwarder;
            List <DiscoveryInfo> seeds = getSeeds();
            boolean isSeed = getSeeds().contains(self);
            if (isSeed) {
                logger.info("Server is seed node.");
                forwarder = new A1ManagementForwarder(seeds, self);
            } else {
                logger.info("Server is not seed node");
                forwarder = new A1ManagementForwarder(seeds);
            }

            A1Management.Processor processor = new A1Management.Processor(forwarder);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            final TServer server = new TThreadPoolServer(args.processor(processor));
            logger.info("Starting FE Node on port " + this.getMPort() + "...");

            Runnable manager = new Runnable() {
                public void run() {
                    server.serve();
                }
            };

            if (!isSeed) {
                for (DiscoveryInfo seed : seeds) {
                    register(seed.getHost(), seed.getMport(), logger, self);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
