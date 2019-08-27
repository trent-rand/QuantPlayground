/*
*
*
*
*
*/

import java.util.ArrayList;

public class MAStrategy extends Strategy {

    SMA shortMA;
    SMA longMA;

    ArrayList<Double> shortHisAvgs = new ArrayList<Double>();
    ArrayList<Double> longHisAvgs = new ArrayList<Double>();

    ArrayList<Double> macd = new ArrayList<Double>();


    int upperLower = 0;

    int rollingLeverage = 1;

    public MAStrategy(int shortSize, int  longSize) {
        shortMA = new SMA(shortSize);
        longMA = new SMA(longSize);

    }

    @Override
    public void tradeIncrement(Trade trade) {

        if(trade.getPercentGainRaw() > 0 && rollingLeverage < 3) {
            rollingLeverage++;
        } else if (trade.getPercentGainRaw() < 0 && rollingLeverage > 1) {
            rollingLeverage--;
        } else {
            //rollingLeverage = rollingLeverage;
        }

        rollingLeverage = 1;

        System.out.println("Adjusted Rolling Leverage: "+rollingLeverage);
    }

    @Override
    public void increment(Candlestick candle) {
        Portfolio main = Portfolio.getInstance();

        shortMA.add(candle.close);
        longMA.add(candle.close);

        shortHisAvgs.add(shortMA.getAverage());
        longHisAvgs.add(longMA.getAverage());

        macd.add(shortMA.getAverage() - longMA.getAverage());

        //System.out.println("Adjusted Leverage: "+rollingLeverage);
        //System.out.println("Long MA: "+longMA.getAverage());
        //System.out.println("Short MA: "+shortMA.getAverage());


        //Checking for Moving Average Crossings
        try {
            if (macd.get(macd.size() - 2) < 0 && macd.get(macd.size() - 1) > 0) {

                if(main.isPositionActive()) {

                    Trade activeTrade = main.activeTrade;

                    double exitPrice = 0.0;
                    double percentGain = 0;

                    percentGain = -((candle.close - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;
                    System.out.println("Short percent Gain: " + percentGain);
                    exitPrice = candle.close;

                    //Portfolio.getInstance().exitPosition(exitPrice, percentGain, candle.getEndDate());
                }

            //Time to open a position!
                System.out.println("Going Short!");
                System.out.println("Entry Price: "+candle.close);
                System.out.println("Open Date: "+candle.endDate);
                Portfolio.getInstance().enterPosition(candle.close, candle.endDate, rollingLeverage); //TODO: Dynamic Position Sizing. Harcoding 100% invested is embarassing.

            } else if (macd.get(macd.size() - 2) > 0 && macd.get(macd.size() - 1) < 0) {

                if(main.isPositionActive()) {

                    Trade activeTrade = main.activeTrade;

                    double exitPrice = 0.0;
                    double percentGain = 0;

                    percentGain = ((candle.close - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;
                    System.out.println("Long percent Gain: " + percentGain);
                    exitPrice = candle.close;

                    //Portfolio.getInstance().exitPosition(exitPrice, percentGain, candle.getEndDate());
                }

            //Time to open a position!
                System.out.println("Going Long!");
                System.out.println("Entry Price: "+candle.close);
                System.out.println("Open Date: "+candle.endDate);
                Portfolio.getInstance().enterPosition(candle.close, candle.endDate, -rollingLeverage);
            }
        } catch (Exception e) {
                System.out.println("Oops! "+e);

                return;
        }

        //Maybe we have to manage an already open position?
        if (main.isPositionActive()) { main.getExitStrategy().increment(candle); }


        //TODO: Use (& test) an SMA as a stop loss.




    }

}
