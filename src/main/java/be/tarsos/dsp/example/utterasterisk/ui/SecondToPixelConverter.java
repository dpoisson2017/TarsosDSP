package be.tarsos.dsp.example.utterasterisk.ui;

public class SecondToPixelConverter {
    private UtterAsteriskPanel panel;
    private double lengthInSeconds;
    //private double widthOfOneSecondInPixels;

    public SecondToPixelConverter(UtterAsteriskPanel panel, double lengthInSeconds) {
        this.panel = panel;
        this.lengthInSeconds = lengthInSeconds;
    }

    private double calculateWidthOfOneSecondInPixels(int widthInPixel, double lengthInSeconds) {
        double widthOfOneSecondInPixels = widthInPixel / lengthInSeconds;
        return widthOfOneSecondInPixels;
    }

    public int convert(double seconds) {
        double widthOfOneSecondInPixels = calculateWidthOfOneSecondInPixels(panel.getWidth(), lengthInSeconds);
        return (int) (seconds * widthOfOneSecondInPixels);
    }
}




