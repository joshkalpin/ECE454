import ece454750s15a1.A1Password;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class A1Client extends Server {

    public static void main(String [] args) {
        Server client = new A1Client(args);
        client.start();

    }

    public A1Client(String[] args) {
        super(args);
    }

    @Override
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