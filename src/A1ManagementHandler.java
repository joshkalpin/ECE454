import ece454750s15a1.A1Management;
import ece454750s15a1.DiscoveryInfo;
import ece454750s15a1.PerfCounters;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;

// generated thrift code

public class A1ManagementHandler implements A1Management.Iface {

    private int numReceived = 0;
    private int numCompleted = 0;
    private long birthTime = 0;

    public A1ManagementHandler() {
        birthTime = System.currentTimeMillis();
    }

    @Override
    public PerfCounters getPerfCounters() throws TException {
        return new PerfCounters((int)System.currentTimeMillis() - (int)birthTime, numReceived, numCompleted);
    }

    @Override
    public List<String> getGroupMembers() throws TException {
        List<String> members = new ArrayList<String>();
        members.add("jzanutto");
        members.add("jkalpin");
        return members;
    }

    @Override
    public boolean registerNode(DiscoveryInfo discoveryInfo) throws TException {
        return false;
    }

    @Override
    public List<DiscoveryInfo> getBackendNodes() throws TException {
        return null;
    }

    @Override
    public DiscoveryInfo getRequestNode() throws TException {
        return null;
    }

    public void receiveRequest() {
        ++numReceived;
    }

    public void completeRequest() {
        ++numCompleted;
    }
}
