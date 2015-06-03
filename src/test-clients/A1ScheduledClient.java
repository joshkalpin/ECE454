import ece454750s15a1.A1Password;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class A1ScheduledClient {

    private boolean complete = false;

    public static void main(String[] args) {
        A1ScheduledClient client = new A1ScheduledClient();
        client.start(args);
    }

    public void start(String[] args) {
        List<String> connections = new ArrayList<String>();
        int iterations = 1;
        int periodInMillis = 100;
        int startDelay = 1000;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-c")) {
                String connection = args[++i];
                connections.add(connection);
            } else if (args[i].equals("-i")) {
                iterations = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-p")) {
                periodInMillis = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-d")) {
                startDelay = Integer.parseInt(args[++i]);
            } else {
                System.err.println("Option not available: " + args[i]);
                System.exit(0);
            }
        }

        Timer t = new Timer(false);
        TimerTask client = new TimedClient(connections, iterations, t);
        t.scheduleAtFixedRate(client, startDelay, periodInMillis);
        // wait like a peasant for timertask to complete
        while(true) {
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
            if (complete) break;
        }
        System.out.println("Client terminated after completing scheduled task execution.");
    }

    public class TimedClient extends TimerTask {
        private List<String> connections;
        private int iterations = 0;
        char[] chars;
        Random rng;
        Timer timer;
        public TimedClient(List<String> conns, int iters, Timer t) {
            this.connections = conns;
            if (iters > 0) {
                this.iterations = iters;
            } else {
                this.iterations = 1;
            }
            rng = new Random(System.currentTimeMillis());
            chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            timer = t;
        }

        public void run() {
            if (iterations == 0) {
                System.out.println("Process finished creating iterations.");
                A1ScheduledClient.this.complete = true;
                timer.cancel();
                timer.purge();
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rng.nextInt(8) + 8; i++) {
                char c = chars[rng.nextInt(chars.length)];
                sb.append(c);
            }
            String passwd = sb.toString();

            String connection = connections.get(rng.nextInt(this.connections.size()));
            String[] info = connection.split(":");
            TTransport transport;
            try {
                System.out.println("Contacting " + connection);
                transport = new TSocket(info[0], Integer.parseInt(info[1]));
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                A1Password.Client cl = new A1Password.Client(protocol);
                short rounds = (short)(10 + rng.nextInt(6));
                System.out.println("password is: " + passwd);
                System.out.println("hashing rounds: " + rounds);
                String hash = cl.hashPassword(passwd, rounds);
                System.out.println(hash + " is the hashed password");
                if (cl.checkPassword(passwd, hash)) {
                    System.out.println(hash + " is the hashed version of " + passwd);
                } else {
                    System.out.println(hash + " is NOT the hashed version of " + passwd);
                }
                transport.close();
            } catch (Exception e) {
                System.err.println("Client failed to connect to server for password hashing");
                e.printStackTrace();
            }
            --iterations;
        }
    }
}