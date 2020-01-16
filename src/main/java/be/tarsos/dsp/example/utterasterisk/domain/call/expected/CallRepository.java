package be.tarsos.dsp.example.utterasterisk.domain.call.expected;

public interface CallRepository {
    void save(Call call);
    Call findById(CallId id) throws CallNotFoundException;
}
