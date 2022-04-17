package th.ac.kmitl.se;

public interface PaymentModule {
    public Boolean pay(float amount, PaymentCallback callback);
}
