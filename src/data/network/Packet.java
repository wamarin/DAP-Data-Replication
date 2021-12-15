package data.network;

import java.io.Serializable;

public class Packet implements Serializable {
    private final PacketType type;
    private int target;
    private final String clientId;
    private int value;
    private int[] values = null;

    public Packet(PacketType type, String clientId) {
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
}
