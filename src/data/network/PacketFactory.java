package data.network;

public class PacketFactory {

    public static Packet newWritePacket(String id, int target, int value) {
        Packet packet = new Packet(PacketType.WRITE, id);
        packet.setTarget(target);
        packet.setValue(value);
        return packet;
    }

    public static Packet newReadPacket(String id, int target) {
        Packet packet = new Packet(PacketType.READ, id);
        packet.setTarget(target);
        return packet;
    }

    public static Packet newWriteReplicatedPacket(String id, int target, int value) {
        Packet packet = new Packet(PacketType.WRITE_REPLICATED, id);
        packet.setTarget(target);
        packet.setValue(value);
        return packet;
    }

    public static Packet newUpdatePacket(String id, int[] values) {
        Packet packet = new Packet(PacketType.UPDATE, id);
        packet.setValues(values);
        return packet;
    }

    public static Packet newAcknowledgePacket(String id, int target) {
        return new Packet(PacketType.ACKNOWLEDGE, id);
    }

    public static Packet newShutdownPacket(String id) {
        return new Packet(PacketType.SHUTDOWN, id);
    }
}
