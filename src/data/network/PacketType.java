package data.network;

public enum PacketType {
    WRITE,
    READ,
    RESPONSE,
    WRITE_REPLICATED,
    UPDATE,
    ACKNOWLEDGE,
    SHUTDOWN
}