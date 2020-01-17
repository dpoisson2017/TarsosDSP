package be.tarsos.dsp.example.utterasterisk.domain;

public class Timer {
    private long startTime;

    public Timer() {
        reset();
    }

    public long elapsed() {
        long now = System.currentTimeMillis();
        return now - startTime;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
    }
}
