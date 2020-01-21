package be.tarsos.dsp.example.utterasterisk.domain.fft;

import java.util.Objects;

public class FftResult implements Comparable<FftResult> {
    private int frequencyInHz;
    private double amplitude;

    public FftResult(int frequencyInHz, double amplitude) {
        this.frequencyInHz = frequencyInHz;
        this.amplitude = amplitude;
    }

    public int getFrequencyInHz() {
        return frequencyInHz;
    }

    public double getAmplitude() {
        return amplitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FftResult fftResult = (FftResult) o;
        return Double.compare(fftResult.amplitude, amplitude) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(amplitude);
    }

    @Override
    public int compareTo(FftResult that) {
        if (amplitude > that.amplitude) {
            return -1;
        } else if (amplitude < that.amplitude) {
            return 1;
        } else {
            return 0;
        }
    }
}