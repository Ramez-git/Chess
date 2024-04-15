package server;

import org.eclipse.jetty.websocket.api.Session;


public class conn {
    public String playerName;
    public Session session;

    public conn(String playerName, Session session) {
        this.playerName = playerName;
        this.session = session;
    }
}
