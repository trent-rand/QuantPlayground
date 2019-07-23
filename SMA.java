/**
 * Created by Trent Rand on 28/Mar/18.
 */
public class SMA {

    private int size;
    private double total = 0d;
    private int index = 0;
    private double samples[];

    public SMA(int size) {
        this.size = size;
        samples = new double[size];
        for (int i = 0; i < size; i++) samples[i] = 0d;
    }

    public void add(double x) {
        total -= samples[index];
        samples[index] = x;
        total += x;
        if (++index == size) index = 0; // cheaper than modulus
    }

    public double[] getArray() {
        return samples;
    }

    public double getAverage() {
        return total / size;
    }
}
