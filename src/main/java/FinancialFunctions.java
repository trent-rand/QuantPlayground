/**
 * Created by TrentRand on 2019-07-17.
 */
public class FinancialFunctions {







    public double standardDeviation(Double[] input) { //Calculates a Standard Deviation of the provided Double[].
        double toReturn = 0;

        double sum = 0;
        for(int i = 0; i < input.length; i++) {
            sum = input[i] + sum;
        }

        double mu = sum/input.length;

        double variance = 0;
        for(int i = 0; i < input.length; i++) {
            double x = (input[i] - mu) * (input[i] - mu);
            variance = variance + x;
        }
        variance = variance/input.length;

        toReturn = Math.sqrt(variance);

        return toReturn;
    }

    public double percentDiff(double a, double b) {
        double toReturn = (a - b) / b * 100;

        return toReturn;
    }


}
