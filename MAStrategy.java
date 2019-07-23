/*
*
*
*
*
*/

public class MAStrategy extends Strategy {

    SMA shortMA;
    SMA longMA;

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

        System.out.println("Adjusted Rolling Leverage: "+rollingLeverage);
    }

    @Override
    public void increment(Candlestick candle) {

        shortMA.add(candle.close);
        longMA.add(candle.close);

        //System.out.println("Adjusted Leverage: "+rollingLeverage);

        //Checking for Moving Average Crossings
        if(upperLower == 0) {
            if(shortMA.getAverage() > longMA.getAverage()) {
                upperLower = 1;
            } else if(shortMA.getAverage() < longMA.getAverage()) {
                upperLower = -1;
            }
        } else if (upperLower < 0 && shortMA.getAverage() > longMA.getAverage()) {
            upperLower = 1;

            //Time to open a position!
            System.out.println("Going LONG!");
            Portfolio.getInstance().enterPosition(candle.close, candle.endDate, rollingLeverage); //TODO: Dynamic Position Sizing. Harcoding 100% invested is embarassing.

        } else if (upperLower > 0 && shortMA.getAverage() < longMA.getAverage()) {
            upperLower = -1;

            //Time to open a position!
            System.out.println("Going SHORT!");
            Portfolio.getInstance().enterPosition(candle.close, candle.endDate, -rollingLeverage);
        }

        //Maybe we have to manage an already open position?
        Portfolio.getInstance().getExitStrategy().increment(candle);


        //TODO: Use (& test) an SMA as a stop loss.




    }

}
