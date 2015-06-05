package ece454750a15a1;

import ece454750s15a1.A1Password;
import ece454750s15a1.ServiceUnavailableException;
import org.apache.thrift.TException;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;


// generated thrift code

public class A1PasswordHandler implements A1Password.Iface {

    private A1ManagementHandler handler;
    private Logger log;

    public A1PasswordHandler(A1ManagementHandler handler, Logger logger) {
        this.handler = handler;
        this.log = logger;
    }

    @Override
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException, TException {
        handler.receiveRequest();
        log.info("Hashing a password");
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
        handler.completeRequest();
        return hashedPassword;
    }

    @Override
    public boolean checkPassword(String password, String hash) throws ServiceUnavailableException, TException {
        handler.receiveRequest();
        log.info("Checking a password");
        boolean result = BCrypt.checkpw(password, hash);
        handler.completeRequest();
        return result;
    }

}
