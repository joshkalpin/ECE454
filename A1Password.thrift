namespace java ece454750s15a1

exception ServiceUnavailableException {
    1: string msg
}

service A1Password {
    string hashPassword (1:string password, 2:i16 logRounds) throws (1: ServiceUnavailableException e),
    bool checkPassword (1:string password, 2:string hash)
}