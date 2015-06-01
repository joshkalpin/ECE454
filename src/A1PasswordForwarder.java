import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.DiscoveryInfo;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class A1PasswordForwarder implements A1Password.Iface {

    private A1ManagementForwarder forwarder;
    private Logger logger;
    private Map<DiscoveryInfo, TTransport> openConnections;

    public A1PasswordForwarder(A1ManagementForwarder forwarder) {
        logger = LoggerFactory.getLogger(FEServer.class);
        openConnections = new ConcurrentHashMap<DiscoveryInfo, TTransport>();
        this.forwarder = forwarder;
    }

    @Override
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        forwarder.receiveRequest();
        // try until it works
        while(true) {
            DiscoveryInfo backendInfo = forwarder.getRequestNode();

            if (backendInfo == null) {
                throw new ServiceUnavailableException();
            }

            try {
                A1Password.Client backendClient = openClientConnection(backendInfo);
                String hashedPassword = backendClient.hashPassword(password, logRounds);
                openConnections.remove(backendInfo).close();
                forwarder.completeRequest();
                return hashedPassword;
            } catch (Exception e) {
                logger.warn("Unable to connect to node: " + backendInfo.toString());
                forwarder.reportNode(backendInfo, System.currentTimeMillis());
            }
        }
    }

    @Override
    public boolean checkPassword(String password, String hash) throws ServiceUnavailableException, TException {
        forwarder.receiveRequest();
        while(true) {
            DiscoveryInfo backendInfo = forwarder.getRequestNode();

            if (backendInfo == null) {
                throw new ServiceUnavailableException();
            }

            try {
                A1Password.Client backendClient = openClientConnection(backendInfo);
                boolean result = backendClient.checkPassword(password, hash);
                openConnections.remove(backendInfo).close();
                forwarder.completeRequest();
                return result;
            } catch (Exception e) {
                logger.warn("Unable to connect to node: " + backendInfo.toString());
                forwarder.reportNode(backendInfo, System.currentTimeMillis());
            }
        }
    }

    private A1Password.Client openClientConnection(DiscoveryInfo info) throws TTransportException {
        logger.info("Opening connection with backend node " + info.getHost() + ":" + info.getPport());
        TTransport transport = new TSocket(info.getHost(), info.getPport());
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        A1Password.Client backendClient = new A1Password.Client(protocol);
        openConnections.put(info, transport);
        return backendClient;
    }
}
