package data.network;

import java.io.Serializable;

public class Packet implements Serializable {
    private final PacketType type;
    private final int timestamp;
    private final int clientId;
    private final int port;

    public Packet(PacketType type, int clientId, int timestamp, int port) {
        this.type = type;
        this.clientId = clientId;
        this.timestamp = timestamp;
        this.port = port;
    }

    public int getClientId() {
        return clientId;
    }

    public PacketType getType() {
        return type;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getPort() {
        return port;
    }
}
