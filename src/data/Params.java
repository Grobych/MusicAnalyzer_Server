package data;

/**
 * Created by Alex on 03.05.2017.
 */
public class Params {
    private double[] RMS;
    private double[] MFCC;
    private double averageRMS;
    private double averageDeltaRMS;
    private double maxDeltaRMS;

    public Params(double RMS[], double MFCC[]){
        setRMS(RMS);
        setMFCC(MFCC);
    }
    public Params(double RMS[], double MFCC[], double averageRMS, double averageDeltaRMS, double maxDeltaRMS){
        setRMS(RMS);
        setMFCC(MFCC);
        setAverageRMS(averageRMS);
        setAverageDeltaRMS(averageDeltaRMS);
        setMaxDeltaRMS(maxDeltaRMS);
    }

    public void setRMS(double[] RMS) {
        this.RMS = RMS;
    }
    public double[] getRMS() {
        return RMS;
    }

    public void setMFCC(double[] MFCC) {
        this.MFCC = MFCC;
    }
    public double[] getMFCC() {
        return MFCC;
    }

    public void setAverageRMS(double averageRMS) {
        this.averageRMS = averageRMS;
    }

    public double getAverageRMS() {
        return averageRMS;
    }

    public void setAverageDeltaRMS(double averageDeltaRMS) {
        this.averageDeltaRMS = averageDeltaRMS;
    }

    public double getAverageDeltaRMS() {
        return averageDeltaRMS;
    }

    public void setMaxDeltaRMS(double maxDeltaRMS) {
        this.maxDeltaRMS = maxDeltaRMS;
    }

    public double getMaxDeltaRMS() {
        return maxDeltaRMS;
    }
}
