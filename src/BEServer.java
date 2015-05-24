import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import ece454750s15a1.*;

public class BEServer extends Server {
    public static void main(String[] args) {
        Server server = new BEServer(args);
        server.start();
    }

    public BEServer(String[] args) {
        super(args);
    }

    public void start() {

    }
}
