package data.nodes;

import data.network.NetworkManager;
import data.network.Packet;
import serialization.Serializer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;

public abstract class AbstractNode implements Runnable{
    protected int[] values;
    protected final int port;
    protected final String id;
    protected final HashMap<String, Integer> nodes;
    protected boolean running;

    public AbstractNode(int port, String id, HashMap<String, Integer> nodes, int n_values) {
        this.port = port;
        this.id = id;
        this.nodes = nodes;
        this.values = new int[n_values];
        this.running = true;
        this.clearLog();
    }

    protected void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Packet packet;
            Socket client;

            while (running) {
                client = serverSocket.accept();

                packet = NetworkManager.readPacket(client);

                dispatchPacket(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract void dispatchPacket(Packet packet);

    abstract void writeValue(int target, int value);

    abstract int readValue(int target);

    protected void logVersion() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("logs/" + id, true))) {
            writer.println(Arrays.hashCode(values) + " : " + Arrays.toString(values));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void clearLog() {
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
