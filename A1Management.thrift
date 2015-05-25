namespace java ece454750s15a1

struct PerfCounters {
    // number of seconds since service startup
    1: i32 numSecondsUp,
    // total number ofrequests received by service handler
    2: i32 numRequestsReceived,
    // total number ofrequests completed by service handler
    3: i32 numRequestsCompleted
}

struct DiscoveryInfo {
    // the host name for service discovery
    1: string host,
    // the port for the node
    2: i32 port
}

service A1Management {
    PerfCounters getPerfCounters(),
    list<string> getGroupMembers(),
    bool registerNode(DiscoveryInfo discoveryInfo)
    // TODO: additional interface content if necessary
}