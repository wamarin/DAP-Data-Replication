package data;

import data.network.NetworkManager;
import data.network.Packet;
import data.network.PacketFactory;
import data.network.PacketType;
import data.nodes.ANode;
import data.nodes.BNode;
import data.nodes.CNode;
import data.nodes.TNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class DataManager implements Runnable{
    private final HashMap<String, Integer> nodes;
    private final List<Thread> threads;
    private final int n_values;
    private final int port = 6978;
    private boolean running = true;
    private final HashSet<UUID> acknowledgements;

    public DataManager(int n_values) {
        this.nodes = new HashMap<>();
        this.threads = new ArrayList<>();
        this.acknowledgements = new HashSet<>();
        this.n_values = n_values;

        nodes.put("DM", 6978);
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

                if (packet.getType() == PacketType.ACKNOWLEDGE) {
                    acknowledgements.add(packet.getId());
                } else {
                    System.out.println(packet.getType() + " : " + packet.getTarget() + " : " + packet.getValue());
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
        startTransactionLayer();
    }

    private void startTransactionLayer() {
        Thread thread;
        List<List<String>> layers = new ArrayList<>();

        layers.add(Arrays.asList("A1", "A2", "A3"));
        layers.add(Arrays.asList("B1", "B2"));
        layers.add(Arrays.asList("C1", "C2"));

        thread = new Thread(new TNode(nodes.get("T1"), "T1", nodes, layers, 13));
        threads.add(thread);
        thread.start();
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
        Packet packet = PacketFactory.newLayerWritePacket("DM", target, value);
        int port = nodes.get("T1");
        NetworkManager.sendPacket(packet, port);
    }

    public void sendRead(int target, int layer) {
        Packet packet = PacketFactory.newLayerReadPacket("DM", target, layer);
        int port = nodes.get("T1");
        NetworkManager.sendPacket(packet, port);
    }

    public void shutdown() {
        Packet packet = PacketFactory.newShutdownPacket("DM");
        running = false;
        int port = nodes.get("T1");
        NetworkManager.sendPacket(packet, port);

        for (Thread thread: threads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
