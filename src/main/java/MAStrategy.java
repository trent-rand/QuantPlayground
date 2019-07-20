/*
*
*
*
*
*/


import javax.sound.sampled.Port;

public class MAStrategy extends Strategy {


    SMA shortMA;
    SMA longMA;

    int upperLower = 0;

    public MAStrategy(int shortSize, int  longSize) {
        shortMA = new SMA(shortSize);
        longMA = new SMA(longSize);

    }

    @Override
    public void increment(Candlestick candle) {

        shortMA.add(candle.close);
        longMA.add(candle.close);

        //Checking for Moving Average Crossings
        if(upperLower == 0) {
            if(shortMA.getAverage() > longMA.getAverage()) {
                upperLower = 1;
            } else if(shortMA.getAverage() < longMA.getAverage()) {
                upperLower = -1;
            }
        } else if (upperLower < 0 && shortMA.getAverage() > longMA.getAverage()) {
            upperLower = 1;

            Portfolio.getInstance().enterPosition(candle.close, candle.endDate, 3.0);

        } else if (upperLower > 0 && shortMA.getAverage() < longMA.getAverage()) {
            upperLower = -1;

            Portfolio.getInstance().enterPosition(candle.close, candle.endDate, 3.0);
        }

        //
        Portfolio.getInstance().getExitStrategy().increment(candle);


    }







}
