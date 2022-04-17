package th.ac.kmitl.se;

import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.*;
import static org.mockito.Mockito.*;

@Model(file  = "VendingMachine.json")
public class VendingMachineAdapter extends ExecutionContext {
    public static int delay = 0;

    /* The following method add some delay between each step
    so that we can see the progress in GraphWalker player.
     */
    @AfterElement
    public void afterEachStep() {
        try
        {
            Thread.sleep(delay);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    Operation operation;
    PaymentModule paymentModule;
    DispenserModule dispenserModule;
    PaymentCallback paymentCallback;
    DispenserCollectCallback dispenserCollectCallback;
    DispenserClearCallback dispenserClearCallback;

    public VendingMachineAdapter() {
        paymentModule = mock(PaymentModule.class);
        dispenserModule = mock(DispenserModule.class);
        operation = new Operation(paymentModule, dispenserModule);
    }

    @Edge()
    public void start() {
        System.out.println("start");
        assertTrue(operation.start());
        assertEquals(0, operation.numTumThai);
        assertEquals(0, operation.numTumPoo);
        assertEquals(Operation.State.ORDERING, operation.state);
    }

    @Edge()
    public void addTumThai() {
        System.out.println("addTumThai");
        int numTumThaiBefore = operation.numTumThai;
        assertTrue(operation.addTumThai());
        int numTumThaiAfter = operation.numTumThai;
        int numTumThaiExpected = getAttribute("numTumThai").asInt();
        assertEquals(numTumThaiExpected, numTumThaiAfter);
        if (numTumThaiBefore < 3)
            assertEquals(Operation.State.ORDERING, operation.state);
        else
            assertEquals(Operation.State.ERROR_ORDER, operation.state);
    }

    @Edge()
    public void addTumPoo() {
        System.out.println("addTumPoo");
        int numTumPooBefore = operation.numTumPoo;
        assertTrue(operation.addTumPoo());
        int numTumPooAfter = operation.numTumPoo;
        int numTumPooExpected = getAttribute("numTumPoo").asInt();
        assertEquals(numTumPooExpected, numTumPooAfter);
        if (numTumPooBefore < 3)
            assertEquals(Operation.State.ORDERING, operation.state);
        else
            assertEquals(Operation.State.ERROR_ORDER, operation.state);
    }

    @Edge()
    public void ack() {
        System.out.println("ack");
        assertTrue(operation.ack());
        assertEquals(Operation.State.ORDERING, operation.state);
    }

    @Edge()
    public void cancel() {
        System.out.println("cancel");
        assertTrue(operation.cancel());
        assertEquals(Operation.State.WELCOME, operation.state);
    }

    @Edge()
    public void confirm() {
        System.out.println("confirm");
        assertTrue(operation.confirm());
        assertEquals(Operation.State.PAYING, operation.state);

        int numTumThai = getAttribute("numTumThai").asInt();
        int numTumPoo = getAttribute("numTumPoo").asInt();
        float amount = Operation.PRICE_TUM_THAI*numTumThai + Operation.PRICE_TUM_POO*numTumPoo;
        ArgumentCaptor<PaymentCallback> argCallback = ArgumentCaptor.forClass(PaymentCallback.class);
        verify(paymentModule).pay(eq(amount), argCallback.capture());
        paymentCallback = argCallback.getValue();
        clearInvocations(paymentModule);
    }

    @Edge()
    public void payError() {
        System.out.println("payError");
        paymentCallback.onError();
        assertEquals(Operation.State.ERROR_PAY, operation.state);
    }

    @Edge()
    public void payRetry() {
        System.out.println("payRetry");
        assertTrue(operation.payRetry());
        assertEquals(Operation.State.PAYING, operation.state);

        int numTumThai = getAttribute("numTumThai").asInt();
        int numTumPoo = getAttribute("numTumPoo").asInt();
        float amount = Operation.PRICE_TUM_THAI*numTumThai + Operation.PRICE_TUM_POO*numTumPoo;
        ArgumentCaptor<PaymentCallback> argCallback = ArgumentCaptor.forClass(PaymentCallback.class);
        verify(paymentModule).pay(eq(amount), argCallback.capture());
        paymentCallback = argCallback.getValue();
        clearInvocations(paymentModule);
    }

    @Edge()
    public void paid() {
        System.out.println("paid");
        paymentCallback.onSuccess();
        assertEquals(Operation.State.COLLECTING, operation.state);

        int numTumThai = getAttribute("numTumThai").asInt();
        int numTumPoo = getAttribute("numTumPoo").asInt();
        ArgumentCaptor<DispenserCollectCallback> argCallback = ArgumentCaptor.forClass(DispenserCollectCallback.class);
        verify(dispenserModule).dispense(eq(numTumThai), eq(numTumPoo), argCallback.capture());
        dispenserCollectCallback = argCallback.getValue();
        clearInvocations(dispenserModule);
    }

    @Edge()
    public void collected() {
        System.out.println("collected");
        dispenserCollectCallback.onSuccess();
        assertEquals(Operation.State.WELCOME, operation.state);
    }

    @Edge()
    public void collectError() {
        System.out.println("collectError");
        dispenserCollectCallback.onError();
        assertEquals(Operation.State.ERROR_COLLECT, operation.state);

        ArgumentCaptor<DispenserClearCallback> argCallback = ArgumentCaptor.forClass(DispenserClearCallback.class);
        verify(dispenserModule).clear(argCallback.capture());
        dispenserClearCallback = argCallback.getValue();
        clearInvocations(dispenserModule);
    }

    @Edge()
    public void cleared() {
        System.out.println("cleared");
        dispenserClearCallback.onCleared();
        assertEquals(Operation.State.WELCOME, operation.state);
    }

}
