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
    // management port
    2: i32 mport,
    // password port
    3: i32 pport,
    // the type of server
    4: bool isBEServer
}

exception InvalidNodeException {
    1: string msg,
    2: list<DiscoveryInfo> seeds
}

service A1Management {
    PerfCounters getPerfCounters(),
    list<string> getGroupMembers(),
    bool registerNode(1:DiscoveryInfo discoveryInfo) throws (1:InvalidNodeException e),
    list<DiscoveryInfo> getUpdatedBackendNodeList() throws (1:InvalidNodeException e),
    DiscoveryInfo getRequestNode() throws (1:InvalidNodeException e)
    // TODO: additional interface content if necessary
}