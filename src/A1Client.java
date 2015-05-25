import org.apache.thrift.server.*;
import org.apache.thrift.transport.*;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.*;

import ece454750s15a1.*;


public class A1Client extends Server {

    public static void main(String [] args) {
        Server client = new A1Client(args);
        client.start();

    }

    public A1Client(String[] args) {
        super(args);
    }

    protected void start() {
        TTransport transport;
        try {
            transport = new TSocket(getHost(), getPPort());
            TProtocol protocol = new TBinaryProtocol(transport);
            A1Password.Client client = new A1Password.Client(protocol);

            transport.open();

            String passwd = "hunter2";
            short rounds = 16;
            System.out.println("password is: " + passwd);
            String hash = client.hashPassword(passwd, rounds);
            System.out.println(hash + " is the hashed password");
            if (client.checkPassword(passwd, hash)) {
                System.out.println(hash + " is the hashed version of " + passwd);
            } else {
                System.out.println(hash + " is NOT the hashed version of " + passwd);
            }

            transport.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}