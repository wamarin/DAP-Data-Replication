package serialization;

import com.google.gson.Gson;
import data.network.Packet;

public class Serializer {
    private static final Gson gson = new Gson();

    public static String serializePacket(Packet packet) {
        return gson.toJson(packet);
    }

    public static Packet deserializePacket(String packetString) {
        return gson.fromJson(packetString, Packet.class);
    }
}
