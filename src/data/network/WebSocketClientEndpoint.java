package data.network;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.websocket.DecodeException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class WebSocketClientEndpoint extends WebSocketClient {
    public WebSocketClientEndpoint(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onMessage( String message ) {

    }

    @Override
    public void onOpen( ServerHandshake handshake ) {

    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {

    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
    }
}
