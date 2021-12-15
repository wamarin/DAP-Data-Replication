package data.network;

import serialization.Serializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class NetworkManager {

    public static Packet readPacket(Socket client) {
        String line;
        BufferedReader inStream = null;
        Packet packet = null;

        try {
            inStream = new BufferedReader(new InputStreamReader(client.getInputStream()));

            if ((line = inStream.readLine()) != null) {
                packet = Serializer.deserializePacket(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return packet;
    }

    public static void sendPacket(Packet packet, int port) {
        try(Socket socket = new Socket("localhost", port)) {
            PrintStream outStream = new PrintStream(socket.getOutputStream());
            outStream.println(Serializer.serializePacket(packet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
