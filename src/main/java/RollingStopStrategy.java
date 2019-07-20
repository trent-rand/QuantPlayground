/**
 * Created by TrentRand on 2019-07-19.
 */
public class RollingStopStrategy extends Strategy {

    private double baseStopPercent = -0.5;
    private double stopLossPercent = baseStopPercent;

    public RollingStopStrategy() {


    }



    @Override
    public void increment(Candlestick candle) {
        Portfolio main = Portfolio.getInstance();

        if(main.isPositionActive()) {

            Trade activeTrade = main.activeTrade;

            double percentGain = 0;
            if(activeTrade.size > 0) {
                percentGain = ((candle.high - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;
            } else if (activeTrade.size < 0) {
                percentGain = -((candle.low - activeTrade.EntryPrice) / activeTrade.EntryPrice * 100); // - 0.2;
            }

            if(percentGain < stopLossPercent) {
                System.out.println("Successful Trade!");

                Portfolio.getInstance().exitPosition((Math.abs(activeTrade.size) * stopLossPercent - 0.1), candle.endDate); // -0.1 Takes into account one way commissions. Was paid to enter.

                increment(candle);

            } else if (percentGain > 2.0 && percentGain > stopLossPercent) {
                if(0.7 * percentGain > stopLossPercent) { stopLossPercent = 0.7 * percentGain; }
                System.out.println("Stop loss Increased to: "+stopLossPercent);

            } else if (percentGain > 1.0 && percentGain > stopLossPercent) {
                if(0.5 * percentGain > stopLossPercent) { stopLossPercent = 0.5 * percentGain; }
                System.out.println("Stop loss Increased to: "+stopLossPercent);

            //} else if (percentGain > 0.6 && percentGain > stopLossPercent) {
            //    if(0.2 * percentGain > stopLossPercent) { stopLossPercent = 0.2 * percentGain; }
            //    System.out.println("Stop loss Increased to: "+stopLossPercent);

            }

        } else {

            stopLossPercent = baseStopPercent;

        }



    }




}
