import ece454750s15a1.A1Password;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;



public class A1ConcurrentClient {

    Logger logger;

    public static void main(String[] args) {
        A1ConcurrentClient client = new A1ConcurrentClient();
        client.start(args);
    }

    public void start(String[] args) {
        logger = LoggerFactory.getLogger(A1ConcurrentClient.class);
        List<String> connections = new ArrayList<String>();
        int threadPoolSize = 1;
        int rounds = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-c")) {
                String connection = args[++i];
                connections.add(connection);
            } else if (args[i].equals("-s")) {
                threadPoolSize = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-r")) {
                rounds = Integer.parseInt(args[++i]);
            } else {
                logger.warn("Option not available: " + args[i]);
                System.exit(0);
            }
        }
        Random rng = ThreadLocalRandom.current();
        if (rounds == 0) {
            rounds = connections.size() + rng.nextInt(connections.size() * 3);
        }
        logger.info("Number of concurrent tasks to run: " + rounds);
        ExecutorService taskExecutor = Executors.newFixedThreadPool(threadPoolSize);
        while (rounds > 0) {
            String connection = connections.get(rng.nextInt(connections.size()));
            String[] info = connection.split(":");
            int taskId = rounds;
            taskExecutor.execute(new ConcurrentTask(info[0], Integer.parseInt(info[1]), taskId, logger));
            rounds--;
        }
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.info("Force exiting process. Stopping all threads...");
            System.exit(0);
        }
    }

    public class ConcurrentTask implements Runnable {

        private String hostname;
        private int passwordPort;
        private int taskId;
        private Logger logger;

        public ConcurrentTask(String host, int port, int taskId, Logger lg) {
            hostname = host;
            passwordPort = port;
            logger = lg;
            logger.info("Contacting " + host + ":" + port);

        }

        public void run() {
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            Random rng = ThreadLocalRandom.current();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rng.nextInt(8) + 8; i++) {
                char c = chars[rng.nextInt(chars.length)];
                sb.append(c);
            }

            String passwd = sb.toString();
            short rounds = (short)(10 + rng.nextInt(6));

            try {
                TTransport transport = new TSocket(hostname, passwordPort);
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Password.Client cl = new A1Password.Client(protocol);
                logger.info(taskId + ": password is: " + passwd);
                logger.info(taskId + ": hashing rounds: " + rounds);
                String hash = cl.hashPassword(passwd, rounds);
                logger.info(taskId  + ": " + hash + " is the hashed password");
                if (cl.checkPassword(passwd, hash)) {
                    logger.info(taskId + ": " + hash + " is the hashed version of " + passwd);
                } else {
                    logger.info(taskId + ": " + hash + " is NOT the hashed version of " + passwd);
                }
                transport.close();
            } catch (Exception e) {
                logger.warn(taskId + ": Client failed to connect to " + hostname + ":" + passwordPort + " for password hashing");

                e.printStackTrace();
            }
        }
    }
}