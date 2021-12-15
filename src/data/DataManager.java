package data;

import data.network.NetworkManager;
import data.network.Packet;
import data.network.PacketFactory;
import data.network.PacketType;
import data.nodes.ANode;
import data.nodes.AbstractNode;
import data.nodes.BNode;
import data.nodes.CNode;

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

    public void startLayers() {
        startLayer2();
        startLayer1();
        startLayer0();
    }

    private void startLayer0() {
        HashSet<String> peers = new HashSet<>();
        Thread thread;
        List<String> backups;

        peers.add("A1");
        peers.add("A2");
        peers.add("A3");

        thread = new Thread(new ANode(nodes.get("A1"), "A1", nodes, n_values, peers, null));
        threads.add(thread);
        thread.start();

        backups = new ArrayList<>();
        backups.add("B1");

        thread = new Thread(new ANode(nodes.get("A2"), "A2", nodes, n_values, peers, backups));
        threads.add(thread);
        thread.start();

        backups = new ArrayList<>();
        backups.add("B2");

        thread = new Thread(new ANode(nodes.get("A3"), "A3", nodes, n_values, peers, backups));
        threads.add(thread);
        thread.start();
    }

    private void startLayer1() {
        List<String> backups = new ArrayList<>();
        backups.add("C1");
        backups.add("C2");

        Thread thread = new Thread(new BNode(nodes.get("B1"), "B1", nodes, n_values, null));
        threads.add(thread);
        thread.start();

        thread = new Thread(new BNode(nodes.get("B2"), "B2", nodes, n_values, backups));
        threads.add(thread);
        thread.start();
    }

    private void startLayer2() {
        Thread thread = new Thread(new CNode(nodes.get("C1"), "C1", nodes, n_values, null));
        threads.add(thread);
        thread.start();

        thread = new Thread(new CNode(nodes.get("C2"), "C2", nodes, n_values, null));
        threads.add(thread);
        thread.start();
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
