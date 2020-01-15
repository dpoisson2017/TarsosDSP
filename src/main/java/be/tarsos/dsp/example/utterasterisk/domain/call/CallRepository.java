package be.tarsos.dsp.example.utterasterisk.domain.call;

public interface CallRepository {
    void save(Call call);
    Call findById(CallId id) throws CallNotFoundException;
}
