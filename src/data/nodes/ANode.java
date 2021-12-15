package data.nodes;

import data.network.NetworkManager;
import data.network.Packet;
import data.network.PacketFactory;

import java.util.*;

public class ANode extends AbstractNode{
    private final HashSet<String> peers;
    private final LinkedList<Packet> queue;
    private HashSet<String> acknowledgements;
    private final Thread processorThread;

    public ANode(int port, String id, HashMap<String, Integer> nodes, int n_values, HashSet<String> peers) {
        super(port, id, nodes, n_values);
        this.peers = peers;
        this.processorThread = new Thread(new PacketProcessor());
        this.queue = new LinkedList<>();
    }

    @Override
    public void run() {
        processorThread.start();
        startServer();
        try {
            processorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    void dispatchPacket(Packet packet) {
        switch (packet.getType()) {
            case ACKNOWLEDGE -> acknowledgements.add(packet.getClientId());

            case SHUTDOWN -> setRunning(false);

            default -> queue.add(packet);
        }

        notifyProcessor();
    }

    private class PacketProcessor implements Runnable{

        @Override
        public void run() {
            while(running) {
                if (queue.isEmpty()) {
                    pauseProcessor();
                } else {
                    Packet packet = queue.poll();
                    int port = nodes.get(packet.getClientId());
                    int target = packet.getTarget();
                    int value = packet.getValue();
                    UUID requestId = packet.getId();
                    Packet response;


                    switch (packet.getType()) {
                        case WRITE -> {
                            response = PacketFactory.newWriteReplicatedPacket(id, target, value, requestId);
                            acknowledgements = new HashSet<>();

                            broadcast(response);
                            waitForAcknowledgements();
                            writeValue(target, value);

                            response = PacketFactory.newAcknowledgePacket(id, requestId);
                            NetworkManager.sendPacket(response, port);
                        }

                        case READ -> {
                            int responseValue = readValue(packet.getTarget());
                            response = PacketFactory.newResponsePacket(id, target, responseValue, requestId);
                            NetworkManager.sendPacket(response, port);
                        }

                        case WRITE_REPLICATED -> {
                            writeValue(target, value);
                            response = PacketFactory.newAcknowledgePacket(id, requestId);
                            NetworkManager.sendPacket(response, port);
                        }
                    }
                }

            }
        }

        private void waitForAcknowledgements() {
            while (acknowledgements.size() < peers.size() - 1) {
                pauseProcessor();
            }
        }
    }

    private void pauseProcessor() {
        synchronized (processorThread) {
            try {
                processorThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyProcessor() {
        synchronized (processorThread) {
            processorThread.notify();
        }
    }

    private void broadcast(Packet packet) {
        for (String peerId: peers) {
            if (!Objects.equals(peerId, id)) {
                int port = nodes.get(peerId);
                NetworkManager.sendPacket(packet, port);
            }
        }
    }

    @Override
    void writeValue(int target, int value) {
        this.values[target] = value;
    }

    @Override
    int readValue(int target) {
        return this.values[target];

    }
}
