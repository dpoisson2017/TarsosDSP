package be.tarsos.dsp.example.utterasterisk.ui;

public class HzToPixelConverter {
    private UtterAsteriskPanel panel;
    private double maximumFrequency;

    public HzToPixelConverter(UtterAsteriskPanel panel, double maximumFrequency) {
        this.panel = panel;
        this.maximumFrequency = maximumFrequency;
    }

    private double hzToPixelConverter(int heightInPixel, double maximumFrequency) {
        double heightOfOneHzInPixels = heightInPixel / maximumFrequency;
        return heightOfOneHzInPixels;
    }

    public int convert(double frequency) {
        double heightOfOneHzInPixels = hzToPixelConverter(panel.getHeight(), maximumFrequency);
        return (int) (frequency * heightOfOneHzInPixels);
    }


}
