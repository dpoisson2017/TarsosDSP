package be.tarsos.dsp.example.utterasterisk.domain.call.expected;

import java.util.Objects;

public class CallId {
    private String id;

    public CallId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallId)) return false;
        CallId callId = (CallId) o;
        return id.equals(callId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
