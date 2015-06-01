import ece454750s15a1.A1Password;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;

// generated thrift code

public class A1PasswordHandler implements A1Password.Iface {

    private A1ManagementHandler handler;

    public A1PasswordHandler(A1ManagementHandler handler) {
        this.handler = handler;
    }

    @Override
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        handler.receiveRequest();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
        handler.completeRequest();
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(String password, String hash) throws ServiceUnavailableException, TException {
        handler.receiveRequest();
        boolean result = BCrypt.checkpw(password, hash);
        handler.completeRequest();
        return result;
    }

}
