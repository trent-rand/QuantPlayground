import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TrentRand on 2019-07-17.
 */
public class Strategy {

    private String name = ""; //Never used, but good to have is a UI is ever built.
    private int granularity = 3600;

    private double activePosition = 0.0;

    private Trade activeTrade;


    public ArrayList<Candlestick> backTestCandles;






    public void tradeIncrement(Trade trade) {
        System.out.println("Selected Strategy hasn't overidden 'TradeIncrement' method! Nothing is happening!");


    }

    public void increment(Candlestick candle) {
        System.out.println("Selected Strategy hasn't overriden 'Increment' method! Nothing is happening!");

    }


    public void performBacktest(Date start, Date end, String productID) {
        //System.out.println("Performing your backtest...");
        CoinbaseDelegate delegate = Portfolio.getInstance().getDelegate();
        backTestCandles = delegate.backTest(productID, start, end, granularity);

        for(int i = 0; i < backTestCandles.size(); i++) {
            increment(backTestCandles.get(i));
            System.out.println("Latest Close: "+backTestCandles.get(i).getClose()+" : "+backTestCandles.get(i).getEndDate());
        }

    }



    public void setStopLossPercent(double a) {
        System.out.println("Selected Strategy hasn't overidden 'setStopLoss' method! Nothing is happening!");

    }


    public double getActivePosition() {
        return activePosition;
    }

    public Strategy setActivePosition(double activePosition) {
        this.activePosition = activePosition;
        return this;
    }

    public Trade getActiveTrade() {
        return activeTrade;
    }

    public Strategy setActiveTrade(Trade activeTrade) {
        this.activeTrade = activeTrade;
        return this;
    }
}
