import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by TrentRand on 2019-07-17.
 */
public class Strategy {

    private String name = ""; //Never used, but good to have is a UI is ever built.
    private int granularity = 900;

    private ArrayList<Candlestick> backTestCandles;

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
        }

    }


}
