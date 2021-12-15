package data;

import data.network.NetworkManager;
import data.network.Packet;
import data.network.PacketFactory;
import data.network.PacketType;
import data.nodes.ANode;
import data.nodes.AbstractNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DataManager implements Runnable{
    private final HashMap<String, Integer> nodes;
    private final List<Thread> threads;
    private final int n_values;
    private final int port = 6979;
    private boolean running = true;
    private final HashSet<UUID> acknowledgements;

    public DataManager(int n_values) {
        this.nodes = new HashMap<>();
        this.threads = new ArrayList<>();
        this.acknowledgements = new HashSet<>();
        this.n_values = n_values;

        nodes.put("T1", 6979);

        nodes.put("A1", 6980);
        nodes.put("A2", 6981);
        nodes.put("A3", 6982);

        nodes.put("B1", 6983);
        nodes.put("B2", 6984);

        nodes.put("C1", 6985);
        nodes.put("C2", 6986);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Packet packet;
            Socket client;

            while (running) {
                client = serverSocket.accept();

                packet = NetworkManager.readPacket(client);

                System.out.println(packet.getType() + " : " + packet.getTarget() + " : " + packet.getValue());
                if (packet.getType() == PacketType.ACKNOWLEDGE) {
                    acknowledgements.add(packet.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCoreLayer() {
        HashSet<String> peers = new HashSet<>();

        peers.add("A1");
        peers.add("A2");
        peers.add("A3");

        for (String nodeId: peers) {
            Thread thread = new Thread(new ANode(nodes.get(nodeId), nodeId, nodes, n_values, peers));
            threads.add(thread);
            thread.start();
        }
    }

    public void sendWrite(int target, int value) {
        Packet packet = PacketFactory.newWritePacket("T1", target, value);
        NetworkManager.sendPacket(packet, nodes.get("A1"));

        while(!acknowledgements.contains(packet.getId())) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendRead(int target) {
        Packet packet = PacketFactory.newReadPacket("T1", target);
        NetworkManager.sendPacket(packet, nodes.get("A1"));
    }

    public void shutdown() {
        this.running = false;
        Packet packet = PacketFactory.newShutdownPacket("T1");

        for (int port: nodes.values()) {
            NetworkManager.sendPacket(packet, port);
        }

        for (Thread thread: threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
