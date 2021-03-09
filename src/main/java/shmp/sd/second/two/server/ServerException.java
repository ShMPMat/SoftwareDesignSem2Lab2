package shmp.sd.second.two.server;


public class ServerException extends Exception {
    public ServerException(String message) {
        super("{'errorMessage': '" + message + "'}");
    }
}
