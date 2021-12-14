package operations;

public class WriteOperation extends Operation {
    private final int value;

    public WriteOperation(int target, int value) {
        super(target);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
