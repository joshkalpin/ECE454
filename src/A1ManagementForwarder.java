import ece454750s15a1.A1Management;
import ece454750s15a1.DiscoveryInfo;
import ece454750s15a1.PerfCounters;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class A1ManagementForwarder implements A1Management.Iface {

    private List<DiscoveryInfo> seeds;
    private List<DiscoveryInfo> frontEndNodes;
    private List<DiscoveryInfo> backEndNodes;
    private Random rng;
    private boolean isSeed = false;
    private Logger logger;

    public A1ManagementForwarder(List<DiscoveryInfo> seeds) {
        super();
        this.seeds = seeds;
        backEndNodes = new ArrayList<DiscoveryInfo>();
        rng = new Random(System.currentTimeMillis());
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public A1ManagementForwarder(List<DiscoveryInfo> seeds, DiscoveryInfo self) {
        this(seeds);
        frontEndNodes = new ArrayList<DiscoveryInfo>(seeds);
        frontEndNodes.remove(self);
        isSeed = true;
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        return null;
    }

    @Override
    public List<String> getGroupMembers() throws TException {
        return null;
    }

    @Override
    public boolean registerNode(DiscoveryInfo discoveryInfo) throws TException {
        if (!isSeed) {
            return false;
        }

        if (discoveryInfo.isIsBEServer()) {
            backEndNodes.add(discoveryInfo);
        } else {
            frontEndNodes.add(discoveryInfo);
        }

        return true;
    }

    @Override
    public List<DiscoveryInfo> getBackendNodes() throws TException {
        if (isSeed) {
            return backEndNodes;
        }

        return null;
    }

    @Override
    public DiscoveryInfo getRequestNode() throws TException {
        if (isSeed) {
            return backEndNodes.get(rng.nextInt(backEndNodes.size()));
        }

        if (backEndNodes.isEmpty()) {
            updateBackendNodes();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updateBackendNodes();
                }
            }).start();
        }

        return backEndNodes.get(rng.nextInt(backEndNodes.size()));
    }

    private void updateBackendNodes() {
        for (DiscoveryInfo seed : seeds) {
            try {
                logger.info("Updating backend nodes from seed " + seed.getHost() + ":" + seed.getMport());
                TTransport transport = new TSocket(seed.getHost(), seed.getMport());
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Management.Client client = new A1Management.Client(protocol);
                // set timeout to 10 seconds
                // transport.setTimeout(DISCOVERY_TIMEOUT);

                this.backEndNodes = client.getBackendNodes();
                transport.close();
                return;
            } catch (Exception e) {
                logger.warn("Failed to register with " + seed.getHost() + ":" + seed.getMport());
                e.printStackTrace();
            }
        }

        logger.error("Failed to update backend nodes from seeds");
    }
}
