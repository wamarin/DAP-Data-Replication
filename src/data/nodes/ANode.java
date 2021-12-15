package data.nodes;

import data.network.Packet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ANode extends AbstractNode{
    private HashSet<String> peers;
    private List<Packet> queue;
    private HashSet<String> acknowledgements;

    public ANode(int port, String id, HashMap<String, Integer> nodes, int n_values, HashSet<String> peers) {
        super(port, id, nodes, n_values);
        this.peers = peers;
    }

    @Override
    public void run() {
        startServer();
    }

    @Override
    void dispatchPacket(Packet packet) {
        System.out.println(packet.getType());
        switch (packet.getType()) {
            case WRITE -> {

            }
            case READ -> {

            }
            case ACKNOWLEDGE -> {

            }
            case WRITE_REPLICATED -> {

            }

            case SHUTDOWN -> {
                setRunning(false);
            }
        }
    }

    @Override
    void writeValue(int target, int value) {

    }

    @Override
    int readValue(int target) {
        return this.values[target];

    }
}
