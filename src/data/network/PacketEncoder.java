package data.network;

import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class PacketEncoder implements Encoder.Text<Packet> {

    private static final Gson gson = new Gson();

    @Override
    public String encode(Packet packet) throws EncodeException {
        return gson.toJson(packet);
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