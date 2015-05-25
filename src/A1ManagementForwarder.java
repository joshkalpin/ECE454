import ece454750s15a1.A1Management;
import ece454750s15a1.PerfCounters;
import org.apache.thrift.TException;

import java.util.List;

public class A1ManagementForwarder implements A1Management.Iface {
    @Override
    public PerfCounters getPerfCounters() throws TException {
        return null;
    }

    @Override
    public List<String> getGroupMembers() throws TException {
        return null;
    }
}
