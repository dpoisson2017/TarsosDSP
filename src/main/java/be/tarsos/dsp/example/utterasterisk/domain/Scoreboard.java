package be.tarsos.dsp.example.utterasterisk.domain;

public class Scoreboard {
    private int score = 0;

    public void addMatch() { score++; }

    public int getScore() { return score; }

    public void reset() { score = 0; }
}
