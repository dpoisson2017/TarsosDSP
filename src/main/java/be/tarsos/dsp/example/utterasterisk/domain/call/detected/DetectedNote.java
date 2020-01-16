package be.tarsos.dsp.example.utterasterisk.domain.call.detected;

public class DetectedNote {
    private double pitch;
    private double timestamp;
    private boolean matches = false;

    public DetectedNote(double pitch, double timestamp) {
        this.pitch = pitch;
        this.timestamp = timestamp;
    }

    public double getPitch() {
        return pitch;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public boolean isMatch() { return matches; }

    public void matches() {
        matches = true;
    }

    public void differs() {
        matches = false;
    }
}
