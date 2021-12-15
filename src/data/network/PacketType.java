package data.network;

public enum PacketType {
    WRITE,
    READ,
    WRITE_REPLICATED,
    UPDATE,
    ACKNOWLEDGE,
    SHUTDOWN
}