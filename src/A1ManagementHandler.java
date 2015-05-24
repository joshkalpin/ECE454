import org.apache.thrift.TException;
import java.util.*;

// generated thrift code
import ece454750s15a1.*;

public class A1ManagementHandler implements A1Management.Iface {


    public A1ManagementHandler() {}

    public PerfCounters getPerfCounters() throws org.apache.thrift.TException {
        return new PerfCounters();
    }

    public List<String> getGroupMembers() throws org.apache.thrift.TException {
        return new ArrayList<String>();
    }


}
