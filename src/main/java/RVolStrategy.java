/*
*
*
*
*
*/

import java.util.ArrayList;

public class RVolStrategy extends Strategy {

    private int lookback = 200;
    private double limit = 2.0;

    ArrayList<Candlestick> historicCandles = new ArrayList<Candlestick>();
    ArrayList<Double> volumes = new ArrayList<Double>();

    private FinancialFunctions functions = new FinancialFunctions();

    private double RVolSTD = 1;

    private double rollingLeverage = 1.0;



    public RVolStrategy(int lB, double lmt) { //Dumbest Constructor Ever.

        lookback = lB;
        limit = lmt;

    }

    @Override
    public void tradeIncrement(Trade trade) {

        //rollingLeverage = rollingLeverage / 2;

        if(trade.getPercentGainRaw() > 0 && rollingLeverage < 4) {
            rollingLeverage++;
        } else if (trade.getPercentGainRaw() < 0 && rollingLeverage > 1) {
            rollingLeverage = 1;
        } else {
            rollingLeverage = rollingLeverage;
        }

        rollingLeverage = 1;

        //System.out.println("Adjusted Rolling Leverage: "+rollingLeverage);

    }

    @Override
    public void increment(Candlestick candle) {
        Portfolio main = Portfolio.getInstance();

        //We don't have all the data we need yet.
        if(historicCandles.size() < lookback) {
            historicCandles.add(candle);
            volumes.add(candle.getVolume());
            return;
        }

        //Now we gotta adjust our Arrays to properly reflect our data.
        for (int t = historicCandles.size() - 1; t > 0; t--) {
            historicCandles.set(t, historicCandles.get(t - 1));
            volumes.set(t, volumes.get(t - 1));

        }
        historicCandles.set(0, candle);
        volumes.set(0, candle.getVolume());


        RVolSTD = functions.standardDeviation(volumes.toArray(new Double[volumes.size()]));

        //System.out.println("Calculated RVol STD: "+RVolSTD);
        //System.out.println("Current Volume: "+volumes.get(0));

        if(volumes.get(0) > limit * RVolSTD && !main.isPositionActive()) {

            double newStop = -(Math.abs(functions.percentDiff(candle.open, candle.close) * 0.72));
            System.out.println("Original Stop Set At: "+newStop);
            System.out.println("Current RVol: "+(volumes.get(0)/RVolSTD));
            main.getExitStrategy().setStopLossPercent(newStop);


            //TODO: Remove these lines and incorporate a "Risk Management" Class.
            rollingLeverage = (volumes.get(0)/RVolSTD) - 3;
            System.out.println("Adjusted Rolling Leverage: "+rollingLeverage);

            //Enter Short on great big volume
            if(candle.open > candle.close) {
                main.enterPosition(candle.close, candle.endDate, -rollingLeverage);


            //Enter Long on great big volume
            } else if (candle.open < candle.close) {
                main.enterPosition(candle.close, candle.endDate, rollingLeverage);


            }

            return;
        }

        //Maybe we have to manage an already open position?
        main.getExitStrategy().increment(candle);

    }

}
