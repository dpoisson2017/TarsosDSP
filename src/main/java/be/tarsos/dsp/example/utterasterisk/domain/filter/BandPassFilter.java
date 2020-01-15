package be.tarsos.dsp.example.utterasterisk.domain.filter;

public class BandPassFilter implements Filter {
    private double lowerBound;
    private double upperBound;

    public BandPassFilter(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public boolean filter(double frequency) {
        return frequency >= lowerBound && frequency <= upperBound;
    }
}