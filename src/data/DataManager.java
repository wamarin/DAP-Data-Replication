package data;

import data.network.NetworkManager;
import data.network.Packet;
import data.network.PacketFactory;
import data.nodes.ANode;
import data.nodes.AbstractNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DataManager {
    private final HashMap<String, Integer> nodes;
    private final List<Thread> threads;
    private final int n_values;

    public DataManager(int n_values) {
        nodes = new HashMap<>();
        threads = new ArrayList<>();
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
        NetworkManager.sendPacket(packet, nodes.get("A2"));
        NetworkManager.sendPacket(packet, nodes.get("A3"));
    }

    public void shutdown() {
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
