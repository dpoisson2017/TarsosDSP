package be.tarsos.dsp.example.utterasterisk.domain.call.expected;

public class CallNotFoundException extends Exception {
    public CallNotFoundException(CallId id) {
        super("Call " + id + " not found");
    }
}
