package be.tarsos.dsp.example.utterasterisk.persistence.call;

import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.CallId;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.CallNotFoundException;
import be.tarsos.dsp.example.utterasterisk.domain.call.expected.CallRepository;

import java.util.HashMap;
import java.util.Map;

public class CallInMemoryRepository implements CallRepository {
    private Map<CallId, Call> calls = new HashMap<>();

    @Override
    public void save(Call call) {
        calls.put(call.getId(), call);
    }

    @Override
    public Call findById(CallId id) throws CallNotFoundException {
        Call call =  calls.get(id);
        if(call == null) {
            throw new CallNotFoundException(id);
        }
        return call;
    }
}
