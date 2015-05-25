import org.apache.thrift.TException;
import java.util.*;

// generated thrift code
import ece454750s15a1.*;

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

    public void receiveRequest() {
        ++numReceived;
    }

    public void completeRequest() {
        ++numCompleted;
    }
}
