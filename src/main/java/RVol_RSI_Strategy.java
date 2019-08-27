/*
*
*
*
*
*/

import java.util.ArrayList;

public class RVol_RSI_Strategy extends Strategy {

    private int lookback = 200;
    private double limit = 2.0;

    private double portfolioAllocation = 1.0;

    ArrayList<Candlestick> historicCandles = new ArrayList<Candlestick>();
    ArrayList<Double> volumes = new ArrayList<Double>();


    SMA shortMA;
    SMA longMA;

    ArrayList<Double> macd = new ArrayList<Double>();


    private FinancialFunctions functions = new FinancialFunctions();

    private double RVolSTD = 1;

    private double rollingLeverage = 1.0;


    public RVol_RSI_Strategy(int lB, double lmt, int shortSize, int longSize, double portAlloc) { //Dumbest Constructor Ever.

        this.shortMA = new SMA(shortSize);
        this.longMA = new SMA(longSize);

        this.lookback = lB;
        this.limit = lmt;

        this.portfolioAllocation = portAlloc;

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

            shortMA.add(candle.getClose());
            longMA.add(candle.getClose());

            macd.add(shortMA.getAverage() - longMA.getAverage());

            return;
        }


        //Now we gotta adjust our Arrays to properly reflect our data.
        for (int t = historicCandles.size() - 1; t > 0; t--) {
            historicCandles.set(t, historicCandles.get(t - 1));
            volumes.set(t, volumes.get(t - 1));

            macd.set(t, macd.get(t - 1));


        }

        shortMA.add(candle.getClose());
        longMA.add(candle.getClose());

        historicCandles.set(0, candle);
        volumes.set(0, candle.getVolume());

        macd.set(0, shortMA.getAverage() - longMA.getAverage());

        RVolSTD = functions.standardDeviation(volumes.toArray(new Double[volumes.size()]));

        //System.out.println("Calculated RVol STD: "+RVolSTD);
        //System.out.println("Current Volume: "+volumes.get(0));

        if(volumes.get(0) > limit * RVolSTD && !main.isPositionActive()) {

            double newStop = -(Math.abs(functions.percentDiff(candle.open, candle.close) * 0.72));

            if(newStop < -5) { newStop = -5; }

            System.out.println("Original Stop Set At: "+newStop);
            System.out.println("Current RVol: "+(volumes.get(0)/RVolSTD));
            main.getExitStrategy().setStopLossPercent(newStop);


            //TODO: Remove these lines and incorporate a "Risk Management" Class.
            rollingLeverage = (volumes.get(0)/RVolSTD) - limit;
            rollingLeverage = rollingLeverage + portfolioAllocation;
            System.out.println("Adjusted Rolling Leverage: "+rollingLeverage);

            System.out.println("MACD: "+macd.get(0));
            System.out.println("RVOLSTD Close: "+((volumes.get(0)/RVolSTD) * candle.getClose()/100));

            //Enter Short on great big volume
            if(candle.open > candle.close) {

                if(Math.abs(macd.get(0)) < ((volumes.get(0)/RVolSTD) * candle.getClose()/100)) {
                    main.enterPosition(candle.close, candle.endDate, -rollingLeverage);
                } else {
                    main.enterPosition(candle.close, candle.endDate, rollingLeverage);
                }



                //Enter Long on great big volume
            } else if (candle.open < candle.close) {

                if(Math.abs(macd.get(0)) < ((volumes.get(0)/RVolSTD) * candle.getClose()/100)) {
                    main.enterPosition(candle.close, candle.endDate, rollingLeverage);
                } else {
                    main.enterPosition(candle.close, candle.endDate, -rollingLeverage);
                }


            }

            return;
        }

        //Maybe we have to manage an already open position?
        main.getExitStrategy().increment(candle);

    }


    public double getCurrentPosition() {
        return currentPosition;
    }

    public RVol_RSI_Strategy setCurrentPosition(double currentPosition) {
        this.currentPosition = currentPosition;
        return this;
    }
}
