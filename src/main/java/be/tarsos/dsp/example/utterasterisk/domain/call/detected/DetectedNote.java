package be.tarsos.dsp.example.utterasterisk.domain.call.detected;

public class DetectedNote {
    private double pitch;
    private double timeInMs;
    private boolean matches = false;

    public DetectedNote(double pitch, double timeFromCallStartInMs) {
        this.pitch = pitch;
        this.timeInMs = timeFromCallStartInMs;
    }

    public double getPitch() {
        return pitch;
    }

    public double getTime() {
        return timeInMs;
    }

    public boolean isMatch() { return matches; }

    public void matches() {
        matches = true;
    }

    public void differs() {
        matches = false;
    }
}
