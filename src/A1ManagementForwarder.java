import ece454750s15a1.A1Management;
import ece454750s15a1.DiscoveryInfo;
import ece454750s15a1.PerfCounters;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

public class A1ManagementForwarder implements A1Management.Iface {

    private List<DiscoveryInfo> seeds;
    private List<DiscoveryInfo> frontEndNodes;
    private List<DiscoveryInfo> backEndNodes;
    private boolean isSeed = false;

    public A1ManagementForwarder(List<DiscoveryInfo> seeds) {
        super();
        this.seeds = seeds;
        backEndNodes = new ArrayList<DiscoveryInfo>();

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
}
