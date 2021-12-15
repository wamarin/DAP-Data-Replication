package data.network;

import java.util.UUID;

public class PacketFactory {

    public static Packet newLayerWritePacket(String id, int target, int value) {
        Packet packet = new Packet(PacketType.WRITE, id);
        packet.setTarget(target);
        packet.setValue(value);
        return packet;
    }

    public static Packet newWritePacket(String id, int target, int value, UUID requestId) {
        Packet packet = new Packet(PacketType.WRITE, id);
        packet.setTarget(target);
        packet.setValue(value);
        packet.setId(requestId);
        return packet;
    }

    public static Packet newLayerReadPacket(String id, int target, int layer) {
        Packet packet = new Packet(PacketType.READ, id);
        packet.setTarget(target);
        packet.setLayer(layer);
        return packet;
    }

    public static Packet newReadPacket(String id, int target, UUID requestId) {
        Packet packet = new Packet(PacketType.READ, id);
        packet.setTarget(target);
        packet.setId(requestId);
        return packet;
    }

    public static Packet newResponsePacket(String id, int target, int value, UUID requestId) {
        Packet packet = new Packet(PacketType.RESPONSE, id);
        packet.setId(requestId);
        packet.setTarget(target);
        packet.setValue(value);
        return packet;
    }

    public static Packet newWriteReplicatedPacket(String id, int target, int value, UUID requestId) {
        Packet packet = new Packet(PacketType.WRITE_REPLICATED, id);
        packet.setId(requestId);
        packet.setTarget(target);
        packet.setValue(value);
        return packet;
    }

    public static Packet newUpdatePacket(String id, int[] values) {
        Packet packet = new Packet(PacketType.UPDATE, id);
        packet.setValues(values);
        return packet;
    }

    public static Packet newAcknowledgePacket(String id, UUID requestId) {
        Packet packet = new Packet(PacketType.ACKNOWLEDGE, id);
        packet.setId(requestId);
        return packet;
    }

    public static Packet newShutdownPacket(String id) {
        return new Packet(PacketType.SHUTDOWN, id);
    }
}
