package server;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;


public class manager {
    public final HashMap<String, conn> connections = new HashMap<>();


    public void add(String visitorName, Session session) {
        var connection = new conn(visitorName, session);
        connections.put(visitorName, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcast(String playerName, ServerMessage message) throws IOException {
        var removeList = new ArrayList<conn>();
        for (conn c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.playerName.equals(playerName)) {
                    c.session.getRemote().sendString(new Gson().toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        for (conn c : removeList) {
            connections.remove(c.playerName);
        }
    }
}
