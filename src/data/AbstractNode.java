package data;

import data.network.Packet;
import serialization.Serializer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public abstract class AbstractNode implements Runnable{
    private int[] values;
    private final int port;
    private final String id;
    private final HashMap<String, AbstractNode> nodes;
    private boolean running;

    public AbstractNode(int port, String id, HashMap<String, AbstractNode> nodes, int n_values) {
        this.port = port;
        this.id = id;
        this.nodes = nodes;
        this.values = new int[n_values];
        this.running = true;
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Packet packet;
            Socket client;

            while (running) {
                client = serverSocket.accept();

                packet = readPacket(client);

                dispatchPacket(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Packet readPacket(Socket client) {
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

    private void sendPacket(Packet packet, int port) {
        try(Socket socket = new Socket("localhost", port)) {
            PrintStream outStream = new PrintStream(socket.getOutputStream());
            outStream.println(Serializer.serializePacket(packet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract void dispatchPacket(Packet packet);

    abstract void writeValue(int target, int value);

    abstract int readValue(int target);

    private void logVersion() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("logs/" + id, true))) {
            writer.println(Arrays.hashCode(values) + " : " + Arrays.toString(values));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearLog() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("logs/" + id))) {
            writer.println(id);
            writer.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public String getId() {
        return id;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
