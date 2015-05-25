import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.mindrot.jbcrypt.BCrypt;

// generated thrift code
import ece454750s15a1.*;

public class A1PasswordHandlerAsync implements A1Password.AsyncIface {

    public A1PasswordHandlerAsync() {}

    @Override
    public void hashPassword(String password, short logRounds, AsyncMethodCallback resultHandler)
    throws ServiceUnavailableException, TException {
        resultHandler.onComplete(BCrypt.hashpw(password, BCrypt.gensalt(logRounds)));
    }

    @Override
    public void checkPassword(String password, String hash, AsyncMethodCallback resultHandler)
    throws ServiceUnavailableException, TException {
        resultHandler.onComplete(BCrypt.checkpw(password, hash));
    }

}
