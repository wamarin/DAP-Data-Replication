package operations;

public abstract class Operation {
    private final int target;

    public Operation(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}
