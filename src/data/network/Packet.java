package data.network;

import java.io.Serializable;
import java.util.UUID;

public class Packet implements Serializable {
    private UUID id;
    private final PacketType type;
    private int target;
    private final String clientId;
    private int value;
    private int[] values = null;
    private int layer = 0;

    public Packet(PacketType type, String clientId) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }



    public PacketType getType() {
        return type;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }
}
