package data.nodes;

import data.network.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.*;

public class TNode extends AbstractNode{
    private final LinkedList<Packet> queue;
    private HashSet<UUID> acknowledgements;
    private HashMap<UUID, Integer> responses;
    private List<List<String>> layers;
    private final Thread processorThread;
    private final Random random;

    public TNode(int port, String id, HashMap<String, Integer> nodes, List<List<String>> layers, int seed) {
        super(port, id, nodes, 0, null, false);
        this.processorThread = new Thread(new PacketProcessor());
        this.queue = new LinkedList<>();
        this.acknowledgements = new HashSet<>();
        this.responses = new HashMap<>();
        this.layers = layers;
        this.random = new Random(seed);

        try {
            webSocketClient = new WebSocketClient(new URI( "ws://localhost:8080/status/" + id )) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {

                }

                @Override
                public void onMessage(String s) {
                    System.out.println(s);
                    try {
                        Packet packet = new PacketDecoder().decode(s);
                        queue.add(packet);
                        notifyProcessor();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    System.out.println("T1 closed!");
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            };

            webSocketClient.connect();
            System.out.println("Node " + id + " connected to WebSocket server!");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            case ACKNOWLEDGE -> acknowledgements.add(packet.getId());

            case RESPONSE -> responses.put(packet.getId(), packet.getValue());

            case SHUTDOWN -> shutdown();

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

                    if (Objects.equals(packet.getClientId(), "WebApp")) {
                        if (packet.getType() == PacketType.WRITE)
                            packet = PacketFactory.newWritePacket("T1", packet.getTarget(), packet.getValue(), UUID.randomUUID());
                        if (packet.getType() == PacketType.SHUTDOWN)
                            packet = PacketFactory.newShutdownPacket("T1");
                    }

                    int port = nodes.get(packet.getClientId());
                    int target = packet.getTarget();
                    int layer = packet.getLayer();
                    int value = packet.getValue();
                    UUID requestId = packet.getId();
                    Packet response;

                    switch (packet.getType()) {
                        case WRITE -> {
                            routeWriteOperation(target, value, requestId);
                            waitForAcknowledgement(packet.getId());
                            response = PacketFactory.newAcknowledgePacket(id, requestId);
                            NetworkManager.sendPacket(response, port);
                        }

                        case READ -> {
                            routeReadOperation(target, layer, requestId);
                            waitForResponse(requestId);
                            response = PacketFactory.newResponsePacket(id, target, responses.get(requestId), requestId);
                            NetworkManager.sendPacket(response, port);
                        }

                        case SHUTDOWN -> shutdown();
                    }
                }

            }
        }

        private void waitForAcknowledgement(UUID requestId) {
            while (!acknowledgements.contains(requestId)) {
                pauseProcessor();
            }
        }

        private void waitForResponse(UUID requestId) {
            while (!responses.containsKey(requestId)) {
                pauseProcessor();
            }
        }

        private void routeWriteOperation(int target, int value, UUID requestId) {
            Packet packet = PacketFactory.newWritePacket(id, target, value, requestId);

            List<String> layer0 = layers.get(0);
            String nodeId = layer0.get(random.nextInt(layer0.size()));
            System.out.println("Routed write operation to node " + nodeId);
            int port = nodes.get(nodeId);

            NetworkManager.sendPacket(packet, port);
        }

        private void routeReadOperation(int target, int layerId, UUID requestId) {
            Packet packet = PacketFactory.newReadPacket(id, target, requestId);

            List<String> layer = layers.get(layerId);
            String nodeId = layer.get(random.nextInt(layer.size()));
            System.out.println("Routed read operation to node " + nodeId);
            int port = nodes.get(nodeId);

            NetworkManager.sendPacket(packet, port);
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
        for (int p: nodes.values()) {
            if (!Objects.equals(p, port)) {
                NetworkManager.sendPacket(packet, p);
            }
        }
    }

    @Override
    void writeValue(int target, int value) {

    }

    @Override
    int readValue(int target) {
        return 0;
    }

    private void shutdown() {
        this.running = false;
        Packet packet = PacketFactory.newShutdownPacket(id);

        broadcast(packet);
        packet = PacketFactory.newAcknowledgePacket(id, null);
        NetworkManager.sendPacket(packet, nodes.get(id));
    }
}
