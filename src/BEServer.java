import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.DiscoveryInfo;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BEServer extends Server {

    private DiscoveryInfo info;
    private Logger logger;

    public DiscoveryInfo getInfo() {
        return info;
    }

    public static void main(String[] args) {
        Server server = new BEServer(args);
        server.start();
    }

    public BEServer(String[] args) {
        super(args);
        info = new DiscoveryInfo(this.getHost(), this.getMPort(), this.getPPort(), getNCores(), true);
        logger = LoggerFactory.getLogger(BEServer.class);
        logger.info("Node created...");
    }

    @Override
    protected void start() {
        try {
            TNonblockingServerTransport managementTransport = new TNonblockingServerSocket(this.getMPort());
            A1ManagementHandler managementHandler = new A1ManagementHandler();
            A1Management.Processor managementProcessor = new A1Management.Processor(managementHandler);
            TNonblockingServer.Args managementArgs = new TNonblockingServer.Args(managementTransport);
            final TServer managementServer = new TNonblockingServer(
                    managementArgs.processor(managementProcessor).protocolFactory(new TCompactProtocol.Factory())
            );

            logger.info(this.getHost() + ": Starting BE management server " + this.getMPort() + "...");

            logger.info("Attempting to start management service...");

            TNonblockingServerTransport passwordTransport = new TNonblockingServerSocket(this.getPPort());
            A1Password.Processor passwordProcessor = new A1Password.Processor(new A1PasswordHandler(managementHandler, logger));
            THsHaServer.Args passwordArgs = new THsHaServer.Args(passwordTransport);
            final TServer passwordServer = new THsHaServer(
                    passwordArgs.processor(passwordProcessor).protocolFactory(new TCompactProtocol.Factory())
            );
            logger.info(this.getHost() + ": Starting BE password service " + this.getPPort() + "...");

            Runnable passwordHandler = new Runnable() {
                public void run() {
                    passwordServer.serve();
                }
            };

            logger.info("Attempting to start password service...");

            List<DiscoveryInfo> seeds = getSeeds();
            ExecutorService executor = Executors.newFixedThreadPool(seeds.size());
            executor.submit(passwordHandler);

            for (final DiscoveryInfo seed : seeds) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        register(seed.getHost(), seed.getMport(), logger, getInfo());
                    }
                };

                executor.submit(runnable);
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);

            managementServer.serve();
        } catch (TException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            logger.error("executor termination interrupted");
            e.printStackTrace();
        }
    }
}
