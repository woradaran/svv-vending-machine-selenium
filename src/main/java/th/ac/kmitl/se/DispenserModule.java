package th.ac.kmitl.se;

public interface DispenserModule {
    public Boolean dispense(int numTumThai, int numTumPoo, DispenserCollectCallback callback);
    public Boolean clear(DispenserClearCallback callback);
}
