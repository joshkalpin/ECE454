import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.DiscoveryInfo;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
            TServerTransport managementTransport = new TServerSocket(this.getMPort());
            A1ManagementHandler managementHandler = new A1ManagementHandler();
            A1Management.Processor managementProcessor = new A1Management.Processor(managementHandler);
            TThreadPoolServer.Args managementArgs = new TThreadPoolServer.Args(managementTransport);
            final TServer managementServer =
                new TThreadPoolServer(managementArgs.processor(managementProcessor));

            logger.info(this.getHost() + ": Starting BE management server " + this.getMPort() + "...");

            logger.info("Attempting to start management service...");

            TServerTransport passwordTransport = new TServerSocket(this.getPPort());
            A1Password.Processor passwordProcessor = new A1Password.Processor(new A1PasswordHandler(managementHandler, logger));
            TThreadPoolServer.Args passwordArgs = new TThreadPoolServer.Args(passwordTransport);
            final TServer passwordServer =
                    new TThreadPoolServer(passwordArgs.processor(passwordProcessor));
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

            managementServer.serve();
        }
        catch (TException e) {
            e.printStackTrace();
        }
    }
}
