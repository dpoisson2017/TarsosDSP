package be.tarsos.dsp.example.utterasterisk.domain.call.detected;

import be.tarsos.dsp.example.utterasterisk.domain.call.expected.Call;

import java.util.ArrayList;
import java.util.List;

public class DetectedCall {
    private List<DetectedNote> notes = new ArrayList<DetectedNote>();
    private double lengthInSeconds;

    public DetectedCall(Call call) {
        this.lengthInSeconds = call.getLengthInSeconds();
    }

    public void addNote(DetectedNote note) {
        notes.add(note);
    }

    public List<DetectedNote> getNotes() { return notes; }

    public double getLengthInSeconds() { return lengthInSeconds; }

    public void clear() {
        notes.clear();
    }
}
