package be.tarsos.dsp.example.utterasterisk.domain.call.expected;

public class Note {
    private double pitch;
    private double duration;

    public Note(double pitch, double duration) {
        this.pitch = pitch;
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public double getPitch() {
        return pitch;
    }
}
