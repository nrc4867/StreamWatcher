package util;

/**
 * Messages the client sends to the server
 */
public interface ClientMessages {
    /**
     * Ask the server if it is alive
     */
    String HeartbeatSend = "HEART";

    /**
     * Tell the server the client is alive
     */
    String HeartbeatRespond = "BEAT";

    /**
     * Tell the server to disconnect the client
     */
    String Disconnect = "DISCONNECT";
}
