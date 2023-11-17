package os.lab2.util;

public enum State {
    READY("R"),
    EXECUTING("E"),
    DONE("D️");

    private final String value;

    State(String val) {
        value = val;
    }

    @Override
    public String toString() {
        return value;
    }
}
