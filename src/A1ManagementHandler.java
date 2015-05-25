import org.apache.thrift.TException;
import java.util.*;

// generated thrift code
import ece454750s15a1.*;

public class A1ManagementHandler implements A1Management.Iface {


    public A1ManagementHandler() {}

    @Override
    public PerfCounters getPerfCounters() throws org.apache.thrift.TException {
        return new PerfCounters();
    }

    @Override
    public List<String> getGroupMembers() throws org.apache.thrift.TException {
        List<String> members = new ArrayList<String>();
        members.add("jzanutto");
        members.add("jkalpin");
        return members;
    }


}
