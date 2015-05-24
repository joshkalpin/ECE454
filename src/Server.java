public class Server {
    /**
    *   host: name of host on which this process will run
    *   pport:port number for password service
    *   mport: port number for management service
    *   ncores: number of cores available to the process
    *   seeds: comma-separated list of host:portpairs corresponding to FE seed nodes
    **/

    public static final int SAFE_PORT_NUM = 18726;

    private String host;
    private int pport;
    private int mport;
    private int ncores;
    private String [] seeds;

    public enum Options {
        HOST    { public String toString() { return "host"; } },
        PPORT   { public String toString() { return "pport"; } },
        MPORT   { public String toString() { return "mport"; } },
        NCORES  { public String toString() { return "ncores"; } },
        SEEDS   { public String toString() { return "seeds"; } },
    }

    public void init(String [] args) {
        for (int i = 0; i < args.length; i++) {
            String opt = args[i];
            if (opt.endsWith(Options.HOST.toString())) {
                host = args[++i];
            } else if (opt.endsWith(Options.PPORT.toString())) {
                pport = Integer.parseInt(args[++i]);
            } else if (opt.endsWith(Options.MPORT.toString())) {
                mport = Integer.parseInt(args[++i]);
            } else if (opt.endsWith(Options.NCORES.toString())) {
                ncores = Integer.parseInt(args[++i]);
            } else if (opt.endsWith(Options.SEEDS.toString())) {
                seeds = args[++i].split(",");
            } else {
                System.out.println("Invalid option " + args[i] + " provided to server. Exiting...");
                System.exit(0);
            }
        }
    }

    public String getHost() {
        return host;
    }

    public int getPPort() {
        return pport;
    }

    public int getMPort() {
        return mport;
    }

    public int getNCores() {
        return ncores;
    }

    public String[] getSeeds() {
        return seeds;
    }
}