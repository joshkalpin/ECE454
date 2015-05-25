import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import java.util.*;

// generated thrift code
import ece454750s15a1.*;

public class A1ManagementHandlerAsync implements A1Management.AsyncIface {


    public A1ManagementHandlerAsync() {}

    @Override
    public void getPerfCounters(AsyncMethodCallback resultHandler) throws TException {
        resultHandler.onComplete(new PerfCounters());
    }

    @Override
    public void getGroupMembers(AsyncMethodCallback resultHandler) throws TException {
        List<String> members = new ArrayList<String>();
        members.add("jzanutto");
        members.add("jkalpin");

        resultHandler.onComplete(members);
    }

    @Override
    public void registerNode(DiscoveryInfo discoveryInfo, AsyncMethodCallback resultHandler) throws TException {

    }


}
