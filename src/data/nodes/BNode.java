package data.nodes;

import data.network.NetworkManager;
import data.network.Packet;
import data.network.PacketFactory;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BNode extends AbstractNode {
    private final List<String> backups;

    public BNode(int port, String id, HashMap<String, Integer> nodes, int n_values, List<String> backups) {
        super(port, id, nodes, n_values, backups, true);
        this.backups = backups;
    }

    @Override
    void dispatchPacket(Packet packet) {
        int port = nodes.get(packet.getClientId());
        int target = packet.getTarget();
        int[] values = packet.getValues();
        UUID requestId = packet.getId();
        Packet response;

        switch (packet.getType()) {
            case SHUTDOWN -> setRunning(false);

            case READ -> {
                int responseValue = readValue(packet.getTarget());
                response = PacketFactory.newResponsePacket(id, target, responseValue, requestId);
                NetworkManager.sendPacket(response, port);
            }

            case UPDATE -> updateValues(values);
        }
    }

    @Override
    void writeValue(int target, int value) {

    }

    @Override
    int readValue(int target) {
        return values[target];
    }

    @Override
    public void run() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::updateBackups, 0, 10, TimeUnit.SECONDS);
        startServer();
        executor.shutdown();
    }
}
