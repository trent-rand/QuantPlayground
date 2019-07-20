import javax.sound.sampled.Port;
import java.util.ArrayList;

/**
 * Created by TrentRand on 2019-07-17.
 */
public class BBStrategy extends Strategy {

    private int period = 14;
    private double dev = 1.5;

    private ArrayList<Double> upperBand = new ArrayList<Double>();
    private ArrayList<Double> lowerBand = new ArrayList<Double>();
    private ArrayList<Double> TP = new ArrayList<Double>();

    private double stdev = 1;

    private int iteration = 0;

    private FinancialFunctions functions = new FinancialFunctions();

    public BBStrategy() {

        while(TP.size() < period) {
            TP.add(1.0);
            upperBand.add(1.0);
            lowerBand.add(1.0);
        }




    }


    @Override
    public void increment(Candlestick candle) {
        iteration = iteration + 1;
        //System.out.println("Latest Close: "+candle.getClose());
        //System.out.println("Volume: "+candle.getVolume());


        double tPrice = (candle.getHigh() + candle.getLow() + candle.getClose()) / 3;


        for (int t = TP.size() - 1; t > 0; t--) {
            TP.set(t, TP.get(t - 1));
            upperBand.set(t, upperBand.get(t - 1));
            lowerBand.set(t, upperBand.get(t - 1));
        }

        TP.set(0, tPrice);

        stdev = dev * functions.standardDeviation(TP.toArray(new Double[TP.size()]));

        upperBand.set(0, tPrice + stdev);
        lowerBand.set(0, tPrice - stdev);

        //System.out.println("Lower Band: "+lowerBand.get(0));
        //System.out.println("Upper Band: "+upperBand.get(0));

        if(iteration > 30) {

            if(!Portfolio.getInstance().isPositionActive()) {
                //System.out.println("Looking for an entry ...");

                if(candle.getClose() < lowerBand.get(0)) {
                    System.out.println("Long Entry Signal!");

                    Portfolio.getInstance().enterPosition(lowerBand.get(0), candle.endDate, 1);
                }

                if(candle.getClose() > upperBand.get(0)) {
                    System.out.println("Short Entry Signal!");

                    Portfolio.getInstance().enterPosition(upperBand.get(0), candle.endDate, -1);
                }

            } else {

                Trade activeTrade = Portfolio.getInstance().activeTrade;

                System.out.println("Current Entry: "+activeTrade.getEntryPrice());

                double percentGain = 0;
                if(activeTrade.side > 0) {
                    percentGain = (activeTrade.side * (candle.high - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;

                } else if (activeTrade.side < 0) {
                    percentGain = (activeTrade.side * (candle.low - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;

                } else { System.out.println("We don't have a trade on?!"); }
                System.out.println("Unrealized P/L: "+percentGain);


                Portfolio.getInstance().getExitStrategy().increment(candle);

                /*
                if(percentGain > 1.0) {
                    System.out.println("Successful Trade!");

                    Portfolio.getInstance().exitPosition(closePrice, candle.endDate);
                } else if (percentGain < -1.0) {
                    System.out.println("Stop Loos Hit!!");

                    Portfolio.getInstance().exitPosition(closePrice, candle.endDate);
                }*/

            }

        } //iteration > 30

    } //End of Method


} //End of Class
