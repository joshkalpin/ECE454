import ece454750s15a1.A1Management;
import ece454750s15a1.DiscoveryInfo;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    private DiscoveryInfo self;

    public FEServer(String[] args) {
        super(args);
        self = new DiscoveryInfo(getHost(), getMPort(), getPPort(), false);
    }

    @Override
    protected void start() {
        try {
            TServerTransport serverTransport = new TServerSocket(this.getMPort());

            A1ManagementForwarder forwarder;
            List <DiscoveryInfo> seeds = getSeeds();
            boolean isSeed = getSeeds().contains(self);
            if (isSeed) {
                forwarder = new A1ManagementForwarder(seeds, self);
            } else {
                forwarder = new A1ManagementForwarder(seeds);
            }

            A1Management.Processor processor = new A1Management.Processor(forwarder);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.maxWorkerThreads(getNCores());
            TServer server = new TThreadPoolServer(args.processor(processor));
            System.out.println("Starting FE Node on port " + this.getMPort() + "...");

            server.serve();

            if (!isSeed) {
                for (DiscoveryInfo seed : seeds) {
                    register(seed.getHost(), seed.getMport(), LoggerFactory.getLogger(FEServer.class), self);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
