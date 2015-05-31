import ece454750s15a1.A1Management;
import ece454750s15a1.A1Password;
import ece454750s15a1.DiscoveryInfo;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class A1PasswordForwarder implements A1Password.Iface {

    private A1Management.Client client;
    private Logger logger;
    private Map<DiscoveryInfo, TTransport> openConnections;

    public A1PasswordForwarder(DiscoveryInfo self) {
        logger = LoggerFactory.getLogger(FEServer.class);
        openConnections = new HashMap<DiscoveryInfo, TTransport>();
        try {
            logger.info("Opening connection with management node " + self.getHost() + ":" + self.getMport());
            TTransport transport = new TSocket(self.getHost(), self.getMport());
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            client = new A1Management.Client(protocol);
        } catch (Exception e) {
            logger.warn("Failed to connect to management node " + self.getHost() + ":" + self.getMport());
            e.printStackTrace();
        }
    }

    @Override
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        DiscoveryInfo backendInfo = client.getRequestNode();
        boolean passwordSet = false;
        String hashedPassword = "";
        // try until it works
        while(true) {
            A1Password.Client backendClient = openClientConnection(backendInfo);
            try {
                hashedPassword = backendClient.hashPassword(password, logRounds);
                openConnections.get(backendInfo).close();
                passwordSet = true;
            } catch (Exception e) {
                logger.warn("Unable to connect to node: " + backendInfo.toString());
                client.reportNode(backendInfo, System.currentTimeMillis());
            }
            if (passwordSet) {
                return hashedPassword;
            }
            if (backendClient == null) {
                return null;
            }
        }
    }

    @Override
    public boolean checkPassword(String password, String hash) throws ServiceUnavailableException, TException {
        DiscoveryInfo backendInfo = client.getRequestNode();
        A1Password.Client backendClient = openClientConnection(backendInfo);
        boolean result = backendClient.checkPassword(password, hash);
        openConnections.get(backendInfo).close();
        return result;
    }

    private A1Password.Client openClientConnection(DiscoveryInfo info) {
        try {
            logger.info("Opening connection with backend node " + info.getHost() + ":" + info.getPport());
            TTransport transport = new TSocket(info.getHost(), info.getPport());
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client backendClient = new A1Password.Client(protocol);
            openConnections.put(info, transport);
            return backendClient;
        } catch (Exception e) {
            logger.warn("Failed to connect to management node " + info.getHost() + ":" + info.getPport());
            e.printStackTrace();
        }

        return null;
    }
}
