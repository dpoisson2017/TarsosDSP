package be.tarsos.dsp.example.utterasterisk.domain.call.detected;

public class DetectedNote {
    private double pitch;
    private double secondsFromStart;
    private boolean matches = false;

    public DetectedNote(double pitch, double secondsFromStart) {
        this.pitch = pitch;
        this.secondsFromStart = secondsFromStart;
    }

    public double getPitch() {
        return pitch;
    }

    public double getSecondsFromStart() {
        return secondsFromStart;
    }

    public boolean isMatch() { return matches; }

    public void matches() {
        matches = true;
    }

    public void differs() {
        matches = false;
    }
}
