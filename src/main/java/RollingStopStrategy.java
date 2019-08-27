/**
 * Created by TrentRand on 2019-07-19.it’s easier to trade vega and theta than delta.
 */
public class RollingStopStrategy extends Strategy {

    private double baseStopPercent = -0.6;
    private double stopLossPercent = baseStopPercent;

    @Override
    public void increment(Candlestick candle) {
        Portfolio main = Portfolio.getInstance();

        if(main.isPositionActive()) {

            Trade activeTrade = main.activeTrade;

            double exitPrice = 0.0;

            double percentGain = 0;
            double consPercentGain = 0;

            //TODO: This is bad practice.
            // TODO: Can't assume stop will fill immediately.
            // TODO: Also percentGain is the most liberal estimate. Should backtest with most conservative.
            if(activeTrade.size > 0) {
                percentGain = ((candle.high - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;
                consPercentGain = ((candle.low - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100);

                //System.out.println("Long percent Gain: "+percentGain);
                //System.out.println("Conservative Percent Gain: "+consPercentGain);

                exitPrice = candle.close;

            } else if (activeTrade.size < 0) {
                percentGain = -((candle.low - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;
                consPercentGain = -((candle.high - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100);


                //System.out.println("Short percent Gain: "+percentGain);
                //System.out.println("Conservative Percent Gain: "+consPercentGain);

                exitPrice = candle.close;

            }
            if(consPercentGain < stopLossPercent) {
                //System.out.println("Successful Trade!");

                //Time to exit our position!
                //exitPrice = activeTrade.EntryPrice * (1 + (stopLossPercent * 0.01));
                Portfolio.getInstance().exitPosition(exitPrice, stopLossPercent, candle.getEndDate());

                //Reset the stoploss for the next trade.
                stopLossPercent = baseStopPercent;

            } else if (percentGain > 5.0 && percentGain > stopLossPercent) {
                if(0.8 * percentGain > stopLossPercent) {
                    stopLossPercent = 0.8 * percentGain;
                }
                System.out.println("Stop loss Increased x3: "+stopLossPercent);
                System.out.println("Total Possible Gains: "+percentGain);

            } else if (percentGain > 3.0 && percentGain > stopLossPercent) {
                if (0.6 * percentGain > stopLossPercent) {
                    stopLossPercent = 0.6 * percentGain;
                }
                System.out.println("Stop loss Increased x2: "+stopLossPercent);
                System.out.println("Total Possible Gains: "+percentGain);

            } else if(percentGain > -stopLossPercent) {
                //Portfolio.getInstance().exitPosition(exitPrice, 0.5, candle.getEndDate());
                stopLossPercent = 0.5 * percentGain;
                System.out.println("Stop loss Increased x1: "+stopLossPercent);
                System.out.println("Total Possible Gains: "+percentGain);


            } /*else if(percentGain > 0.5 && percentGain > stopLossPercent) {
                //Portfolio.getInstance().exitPosition(exitPrice, 0.5, candle.getEndDate());
                stopLossPercent = 0.5 * percentGain;
                System.out.println("Stop loss Increased x1: "+stopLossPercent);
                System.out.println("Total Possible Gains: "+percentGain);


            } */
            //TODO: There's a logrithmic function that would probably do better than this triple step conditional.
            //TODO: Control systems as trailing stop losses. C'mon, it's like you didn't even go to school.
            // else if (percentGain > 0.6 && percentGain > stopLossPercent) {
            //    if(0.2 * percentGain > stopLossPercent) { stopLossPercent = 0.3; }
            //    System.out.println("Stop loss Increased to: "+stopLossPercent);

            //}

        } else {

            stopLossPercent = baseStopPercent;

        }



    }

    @Override
    public void setStopLossPercent(double stop) {
        stopLossPercent = stop;
    }




}
