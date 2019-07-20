import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by TrentRand on 2019-07-17.
 *
 * This project is intended to provide a simple playground to develop and test quantitive investment strategies on Cryptocurrencies.
 * The intention is to hot swap many stratgies so that many things may be tested.
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class Portfolio {

    private static Portfolio ourInstance = new Portfolio();

    public static synchronized Portfolio getInstance() {
        if (ourInstance == null) {
            ourInstance = new Portfolio();
        }
        return ourInstance;
    }

    private Portfolio() { }

    private CoinbaseDelegate delegate;
    private CoinbaseWebsocket websocket;
    private Strategy strategy;

    private Strategy exitStrategy;

    private CSVOutput output;
    private CSVInput input;

    private double BTCBalance = 10;
    private double ETHBalance = 10;

    private double position = 0;



    double currentBalance = 1000.00;
    Trade activeTrade;
    boolean positionActive = false;

    public void enterPosition(double entryPrice, Date openDate, double size) {
        if(position == 0) {
            activeTrade = new Trade();
            activeTrade.setEntryPrice(entryPrice);
            activeTrade.setOpenDate(openDate);
            activeTrade.size = size;
            positionActive = true;
            position = size;
            return;
        } else {
            System.out.println("Attempted to re-enter an already open position.");

            return;
        }
    }

    public void exitPosition(double exitPrice, Date closeDate) {

        if(position != 0) {
            activeTrade.setExitPrice(exitPrice);
            activeTrade.setCloseDate(closeDate);

            double percentGain = (position * (activeTrade.ExitPrice - activeTrade.EntryPrice)/activeTrade.EntryPrice * 100); // - 0.2;

            percentGain = exitPrice;

            System.out.println("Trade Completed! \nEntry: "+activeTrade.EntryPrice+"\nExit: "+activeTrade.ExitPrice+"\n");
            System.out.println("Realized P/L% : "+percentGain);
            System.out.println("Closed Date: "+activeTrade.getCloseDate());

            getInstance().currentBalance = getInstance().currentBalance * (1 + (percentGain/100));

            System.out.print("Current Balance: "+currentBalance);

            String[] outputToLog = {""+activeTrade.size, ""+activeTrade.EntryPrice, ""+activeTrade.ExitPrice, ""+percentGain, ""+activeTrade.getOpenDate(), ""+activeTrade.getCloseDate(), ""+currentBalance};
            getInstance().output.addToOutput(1, outputToLog);

            positionActive = false;
            position = 0;
            return;
        } else {
            System.out.println("Attempted to exit a non-active position.");
            return;
        }
    }

    public boolean isPositionActive() {
        return positionActive;
    }







    public static void main(String[] args) {
        getInstance().delegate = new CoinbaseDelegate();
        getInstance().websocket = new CoinbaseWebsocket();

        getInstance().strategy = new MAStrategy(50,200);
        // getInstance().strategy = new BBStrategy();
        getInstance().exitStrategy = new RollingStopStrategy();

        getInstance().output = new CSVOutput();
        getInstance().input = new CSVInput();

        getInstance().startBackTest();
        //getInstance().startForwardTest();
        //getInstance().inputBacktest();

        getInstance().output.SaveSession("_Today_Date_Test_Long_Only");

    }


    private void inputBacktest() {
        input.fetchCandles(15);
    }


    private void startForwardTest() {
        int delay = 15000;   // delay for one minute.
        int period = 3600000;  // repeat every 5 minutes and 2 seconds.
        Timer timer = new Timer();


        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(new StandardWebSocketClient(), websocket, "wss://ws-feed.pro.coinbase.com");
        connectionManager.start();


        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                System.out.println("Saving our session!");
                getInstance().output.SaveSession(""+Calendar.DATE);

            }
        }, delay, period);

    }

    private void startBackTest() {
        long startValue = 1505000100000L;
        Date begin = new Date(startValue);
        Date end = new Date((startValue) + (900 * 300 * 1000));

        for(int i = 0; i < 200; i++) {

            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
                System.err.println("We fucked up.");
                return;
            }

            begin = new Date(startValue);
            end = new Date(startValue + (900 * 300 * 1000));

            getInstance().getStrategy().performBacktest(begin, end, "BTC-USD");

            startValue = end.getTime();

        }
    }




    /* Getters & Setters - BEGIN */

    public double getBTCBalance() {
        return BTCBalance;
    }

    public void setBTCBalance(double BTCBalance) {
        this.BTCBalance = BTCBalance;
    }

    public double getETHBalance() {
        return ETHBalance;
    }

    public void setETHBalance(double ETHBalance) {
        this.ETHBalance = ETHBalance;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public CoinbaseDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(CoinbaseDelegate delegate) {
        this.delegate = delegate;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public Strategy getExitStrategy() {
        return exitStrategy;
    }

    public void setExitStrategy(Strategy exitStrategy) {
        this.exitStrategy = exitStrategy;
    }
}
