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
    // number of cores
    4: i32 ncores,
    // the type of server
    5: bool isBEServer
}

exception InvalidNodeException {
    1: string msg,
    2: list<DiscoveryInfo> seeds
}

service A1Management {
    PerfCounters getPerfCounters(),
    list<string> getGroupMembers(),
    // TODO: additional interface content if necessary
    bool registerNode(1:DiscoveryInfo discoveryInfo) throws (1:InvalidNodeException e),
    list<DiscoveryInfo> getUpdatedBackendNodeList() throws (1:InvalidNodeException e),
    DiscoveryInfo getRequestNode() throws (1:InvalidNodeException e),
    // used to share information about other nodes
    void inform(1:list<DiscoveryInfo> frontend, 2:list<DiscoveryInfo> backend, 3:i64 timestamp) throws (1:InvalidNodeException e)
    // let management know password found a bad node
    void reportNode(1:DiscoveryInfo backendNode, 2:i64 timestamp) throws (1:InvalidNodeException e)
}