package transactions;

import operations.Operation;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private final boolean read_only;
    private int layer;
    private List<Operation> operations;

    public Transaction(boolean read_only) {
        this.read_only = read_only;
        this.layer = 0;
    }

    public boolean isRead_only() {
        return read_only;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}
