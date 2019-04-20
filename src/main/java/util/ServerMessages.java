package util;

/**
 * Messages the server sends to the client
 */
public interface ServerMessages {
    /**
     * Tell the client that a cheese is on the screen
     */
    String Cheese = "CHEESE";

    /**
     * Tell the client that a rad cheese is on the screen
     */
    String Rad = "RADCHEESE";

    /**
     * Ask the client if it is alive
     */
    String HeartbeatSend = "HEART";

    /**
     * Tell the client the server is alive
     */
    String HeatbeatRespond = "BEAT";

    /**
     * Tell the client to close the connection, disconnect
     */
    String Close = "CLOSE";
}
