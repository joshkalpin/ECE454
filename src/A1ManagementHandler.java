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
    public PerfCounters getPerfCounters() throws org.apache.thrift.TException {
        return new PerfCounters((int)System.currentTimeMillis() - (int)birthTime, numReceived, numCompleted);
    }

    @Override
    public List<String> getGroupMembers() throws org.apache.thrift.TException {
        List<String> members = new ArrayList<String>();
        members.add("jzanutto");
        members.add("jkalpin");
        return members;
    }

    public void receiveRequest() {
        ++numReceived;
    }

    public void completeRequest() {
        ++numCompleted;
    }
}
