package th.ac.kmitl.se;

public class Operation {
    public enum State {
        WELCOME, ORDERING, ERROR_ORDER, PAYING, ERROR_PAY, COLLECTING, ERROR_COLLECT
    }
    State state;
    static final float PRICE_TUM_THAI = 100;
    static final float PRICE_TUM_POO = 120;
    int numTumThai, numTumPoo;

    PaymentModule paymentModule;
    DispenserModule dispenserModule;

    public Operation(PaymentModule paymentModule, DispenserModule dispenserModule) {
        state = State.WELCOME;
        this.paymentModule = paymentModule;
        this.dispenserModule = dispenserModule;
    }

    public Boolean start() {
        if (state == State.WELCOME) {
            state = State.ORDERING;
            numTumThai = 0;
            numTumPoo = 0;
            return true;
        }
        return false;
    }

    public Boolean addTumThai() {
        if (state == State.ORDERING) {
            if (numTumThai<3)
                numTumThai++;
            else
                state = State.ERROR_ORDER;
            return true;
        }
        return false;
    }

    public Boolean addTumPoo() {
        if (state == State.ORDERING) {
            if (numTumPoo<3)
                numTumPoo++;
            else
                state = State.ERROR_ORDER;
            return true;
        }
        return false;
    }

    public Boolean ack() {
        if (state == State.ERROR_ORDER) {
            state = State.ORDERING;
            return true;
        }
        return false;
    }

    public Boolean cancel() {
        if (state == State.ORDERING || state == State.ERROR_PAY) {
            state = State.WELCOME;
            return true;
        }
        return false;
    }

    public Boolean confirm() {
        if (state == State.ORDERING && numTumPoo+numTumThai > 0) {
            state = State.PAYING;
            this.pay();
            return true;
        }
        return false;
    }

    public Boolean payRetry() {
        if (state == State.ERROR_PAY) {
            state = State.PAYING;
            this.pay();
            return true;
        }
        return false;
    }

    private void pay() {
        float amount = PRICE_TUM_THAI*numTumThai + PRICE_TUM_POO*numTumPoo;
        paymentModule.pay(amount, new PaymentCallback() {
            public void onSuccess() {
                state = State.COLLECTING;
                Operation.this.dispense();
            }
            public void onError() {
                state = State.ERROR_PAY;
            }
        });
    }

    private void dispense() {
        dispenserModule.dispense(numTumThai, numTumPoo, new DispenserCollectCallback() {
                public void onSuccess() {
                    state = State.WELCOME;
                }
                public void onError() {
                    state = State.ERROR_COLLECT;
                    Operation.this.clear();
                }
        });
    }

    private void clear() {
        dispenserModule.clear(new DispenserClearCallback() {
                public void onCleared() {
                    state = State.WELCOME;
                }
        });
    }

}
