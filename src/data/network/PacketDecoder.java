package data.network;

import com.google.gson.Gson;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class PacketDecoder implements Decoder.Text<Packet> {

    private static final Gson gson = new Gson();

    @Override
    public Packet decode(String s) throws DecodeException {
        return gson.fromJson(s, Packet.class);
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}