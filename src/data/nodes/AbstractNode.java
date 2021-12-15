package data.nodes;

import data.network.NetworkManager;
import data.network.Packet;
import data.network.PacketFactory;
import serialization.Serializer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractNode implements Runnable{
    protected int[] values;
    protected final int port;
    protected final String id;
    protected final HashMap<String, Integer> nodes;
    protected volatile boolean running;
    private final List<String> backups;

    public AbstractNode(int port, String id, HashMap<String, Integer> nodes, int n_values, List<String> backups) {
        this.port = port;
        this.id = id;
        this.nodes = nodes;
        this.values = new int[n_values];
        this.running = true;
        this.backups = backups;
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

    protected void updateValues(int[] values) {
        if (!Arrays.equals(this.values, values)) {
            this.values = values;
            logVersion();
        }
    }

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

    protected void updateBackups() {
        if (backups != null) {
            for (String backup: backups) {
                Packet packet = PacketFactory.newUpdatePacket(id, this.values);
                int port = nodes.get(backup);
                NetworkManager.sendPacket(packet, port);
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
