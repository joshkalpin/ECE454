import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;

// generated thrift code
import ece454750s15a1.*;

public class A1PasswordHandler implements A1Password.Iface {

    public A1PasswordHandler() {}

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        return BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
    }

    public boolean checkPassword(String password, String hash) throws ServiceUnavailableException, TException {
        return BCrypt.checkpw(password, hash);
    }

}
