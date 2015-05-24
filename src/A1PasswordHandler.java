import org.apache.thrift.TException;

// generated thrift code
import ece454750s15a1.*;

public class A1PasswordHandler implements A1Password.Iface {

    public A1PasswordHandler() {}

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, org.apache.thrift.TException {
        return "";
    }

    public boolean checkPassword(String password, String hash) throws org.apache.thrift.TException {
        return false;
    }

}
