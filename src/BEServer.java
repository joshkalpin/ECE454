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
                register(seed.getHost(), seed.getMport(), logger, getInfo());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
