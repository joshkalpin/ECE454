import ece454750s15a1.A1Management;
import ece454750s15a1.DiscoveryInfo;
import ece454750s15a1.InvalidNodeException;
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
import java.util.TimerTask;
import java.util.Timer;
import java.util.Vector;

public class A1ManagementForwarder implements A1Management.Iface {

    private List<DiscoveryInfo> seeds;
    private List<DiscoveryInfo> frontEndNodes;
    private List<DiscoveryInfo> backEndNodes;
    private Random rng;
    private boolean isSeed = false;
    private Logger logger;

    private long lastUpdated;
    private static final long GOSSIP_FREQUENCY_MILLIS = 100L;
    private static final long GOSSIP_DELAY_MILLIS = 2000L;
    private TimerTask gossip = new Gossiper();
    private Timer gossipSchedule;

    private class Gossiper extends TimerTask {
        @Override
        public void run() {
            // sanity check
            if (A1ManagementForwarder.this != null) {
                A1ManagementForwarder.this.gossip();
            }
        }
    }

    public A1ManagementForwarder(List<DiscoveryInfo> seeds) {
        super();
        this.seeds = seeds;
        backEndNodes = new Vector<DiscoveryInfo>();
        frontEndNodes = new Vector<DiscoveryInfo>();
        rng = new Random(System.currentTimeMillis());
        logger = LoggerFactory.getLogger(FEServer.class);
        lastUpdated = 0L;

        gossipSchedule = new Timer(true);
        gossipSchedule.scheduleAtFixedRate(gossip, GOSSIP_DELAY_MILLIS, GOSSIP_FREQUENCY_MILLIS);
    }

    public A1ManagementForwarder(List<DiscoveryInfo> seeds, DiscoveryInfo self) {
        this(seeds);
        logger.info("Creating seed node");
        this.seeds.remove(self);
        isSeed = true;
    }

    // TODO: fix me
    @Override
    public PerfCounters getPerfCounters() throws TException {
        return null;
    }

    // TODO: fix me
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
            logger.info("Registered backend node");
            backEndNodes.add(discoveryInfo);
        } else {
            logger.info("Registered frontend node");
            frontEndNodes.add(discoveryInfo);
        }
        lastUpdated = System.currentTimeMillis();

        return true;
    }

    @Override
    public List<DiscoveryInfo> getUpdatedBackendNodeList() throws TException, InvalidNodeException {
        if (isSeed) {
            return backEndNodes;
        }

        throw new InvalidNodeException("This is not a seed node", seeds);
    }

    @Override
    public void inform(List<DiscoveryInfo> frontend, List<DiscoveryInfo> backend, long timestamp) throws TException, InvalidNodeException {
        if (timestamp <= lastUpdated) {
            return;
        }

        lastUpdated = timestamp;
        logger.info("" + lastUpdated + " - Received new information about system. Updating cluster state knowledge.");
        this.frontEndNodes = frontend;
        this.backEndNodes = backend;
    }

    @Override
    public void reportNode(DiscoveryInfo backend, long timestamp) throws TException, InvalidNodeException {
        if (timestamp <= lastUpdated) {
            return;
        }

        lastUpdated = timestamp;

        if (!isSeed) {
            List<DiscoveryInfo> seedCopy = new Vector<DiscoveryInfo>(seeds);
            for (DiscoveryInfo seed : seeds) {
                try {
                    TTransport transport = new TSocket(seed.getHost(), seed.getMport());
                    transport.open();
                    TProtocol protocol = new TBinaryProtocol(transport);
                    A1Management.Client seedClient = new A1Management.Client(protocol);
                    seedClient.reportNode(backend, timestamp);
                    transport.close();
                } catch (Exception e) {
                    seedCopy.remove(seed);
                    logger.warn("Unable to inform seed node " + seed.toString() + " of bad BE node. Seed node may be down.");
                    logger.info("Removing seed node from list of available nodes.");
                }

                if (seedCopy.isEmpty()) {
                    logger.error("List of available seed nodes is empty. System may not work as expected");
                }
            }
            seeds = seedCopy;
        }

        backEndNodes.remove(backend);
    }

    // TODO: core based load balancing strategy
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

    private synchronized void updateBackendNodes() {
        for (DiscoveryInfo seed : seeds) {
            try {
                logger.info("Updating backend nodes from seed " + seed.getHost() + ":" + seed.getMport());
                TTransport transport = new TSocket(seed.getHost(), seed.getMport());
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Management.Client client = new A1Management.Client(protocol);
                // set timeout to 10 seconds
                // transport.setTimeout(DISCOVERY_TIMEOUT);

                this.backEndNodes = client.getUpdatedBackendNodeList();
                transport.close();
                return;
            } catch (Exception e) {
                logger.warn("Failed to register with " + seed.getHost() + ":" + seed.getMport());
                e.printStackTrace();
            }
        }

        logger.error("Failed to update backend nodes from seeds");
    }

    private synchronized void gossip() {
        if (!seeds.isEmpty() && !isSeed) {
            DiscoveryInfo seed = seeds.get(rng.nextInt(seeds.size()));
            try {
                TTransport seedTransport = new TSocket(seed.getHost(), seed.getMport());
                seedTransport.open();
                TProtocol seedProtocol = new TBinaryProtocol(seedTransport);
                A1Management.Client seedClient = new A1Management.Client(seedProtocol);
                seedClient.inform(frontEndNodes, backEndNodes, lastUpdated);
                seedTransport.close();
            } catch (Exception e) {
                // TODO: better error handling
                logger.warn("Gossip protocol failed to connect to seed" + seed.getHost() + ":" + seed.getMport());
                e.printStackTrace();
            }
        }
        if (!frontEndNodes.isEmpty()) {
            DiscoveryInfo friend = frontEndNodes.get(rng.nextInt(frontEndNodes.size()));
            try {
                TTransport friendTransport = new TSocket(friend.getHost(), friend.getMport());
                friendTransport.open();
                TProtocol friendProtocol = new TBinaryProtocol(friendTransport);
                A1Management.Client friendClient = new A1Management.Client(friendProtocol);
                friendClient.inform(frontEndNodes, backEndNodes, lastUpdated);
                friendTransport.close();
            } catch (Exception e) {
                // TODO: better error handling
                logger.warn("Gossip protocol failed to connect to friend node " + friend.getHost() + ":" + friend.getMport());
                e.printStackTrace();
            }
        }
    }
}
