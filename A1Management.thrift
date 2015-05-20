namespace java ece454750s15a1

struct PerfCounters {
    // number of seconds since service startup
    1: i32 numSecondsUp,
    // total number ofrequests received by service handler
    2: i32 numRequestsReceived,
    // total number ofrequests completed by service handler
    3: i32 numRequestsCompleted
}

service A1Management {
    PerfCounters getPerfCounters(),
    list<string> getGroupMembers()
    // TODO: additional interface content if necessary
}