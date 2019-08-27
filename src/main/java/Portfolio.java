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
 * TODO: Date fixing, proper input arguments, GUI When?
 * TODO: True trade management. This is basic backtesting, need to manage orders to exchanges!
 *
 * Big Picture:
 *  Eventually this will be a persistently trading backend and complete Quantitive Protfolio Management System.
 *  Gotta build a REST to feed a web-based front end
 *  Gotta connect it to the proper exchange APIs
 *  Gotta host it on a cloud and not a Pi
 *
 *
 * Really REALLY Big Picture?
 *  Support multiple user accounts to each manage their own account.
 *  Friends & Family first,
 *  ???
 *  Profit.
 *
 */
public class Portfolio {


    //Important bitMEX API Info:
    /*
    ID: 3EDVrr229RkmuxmcyQ6C-7rw
    Secret: esTexk7jO87aUURruNuqyOQ7J6ifn6_O5GSKxi341W6tDGCM
    */



    private static Portfolio ourInstance = new Portfolio();

    public static synchronized Portfolio getInstance() {
        if (ourInstance == null) {
            ourInstance = new Portfolio();
        }
        return ourInstance;
    }

    private Portfolio() { }

    private PortfolioMetrics metrics;

    private CoinbaseDelegate delegate;
    private CoinbaseWebsocket websocket;
    private BitMexDelegate BitmexDelegate;
    private BitMexWebsocket BitmexWebsocket;
    private Strategy strategy;

    private Strategy exitStrategy;

    private CSVOutput output;
    private CSVInput input;

    private double BTCBalance = 10;
    private double ETHBalance = 10;

    private double position = 0;

    private double totalPayDay = 0.0;


    double currentBalance = 10000.00;
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

            String side = "null";
            //if (size > 0) { side = "Buy"; } else { side = "Sell"; }

                //getInstance().BitmexDelegate.placeOrder(side, activeTrade.getExchangeID(), 1, "Market");


            return;
        } else {
            System.out.println("Attempted to re-enter an already open position.");
            System.out.println("Current Size: "+activeTrade.getSize());
            System.out.println("Current Entry: "+activeTrade.getEntryPrice());

            return;
        }
    }

    public void exitPosition(double exitPrice, double RawPnL, Date closeDate) {

        if(position != 0) {
            activeTrade.setExitPrice(exitPrice);
            activeTrade.setCloseDate(closeDate);

            double percentGain = Math.abs(activeTrade.size) * RawPnL - 0.15; // Adjusting PnL to consider leverage and commissions and fees.

            activeTrade.setPercentGainRaw(RawPnL);
            activeTrade.setPercentGainAdjusted(percentGain);

            double profit = ((getInstance().currentBalance * (1 + (percentGain/100))) - getInstance().currentBalance);
            double toWithdraw = ((getInstance().currentBalance * (1 + (percentGain/1000))) - getInstance().currentBalance);

            System.out.println("Trade Completed! \nEntry: "+activeTrade.EntryPrice+"\nExit: "+activeTrade.ExitPrice);
            System.out.println("Realized P/L% : "+RawPnL);
            System.out.println("Open Date: "+activeTrade.getOpenDate());
            System.out.println("Close Date: "+activeTrade.getCloseDate());
            System.out.println("Profit/Loss: $"+profit);

            System.out.println("Amount Withdrawn: "+toWithdraw);

            getInstance().currentBalance = getInstance().currentBalance * (1 + (percentGain/100));

            if (toWithdraw > 0) {
                //getInstance().currentBalance = getInstance().currentBalance - toWithdraw;
                getInstance().totalPayDay = getInstance().totalPayDay + toWithdraw;
            }

            System.out.println("Current Balance: "+currentBalance);
            System.out.println("Total PayDay: "+totalPayDay+"\n");

            metrics.updateOnTrade(activeTrade, currentBalance);

            String[] outputToLog = {""+activeTrade.size, ""+activeTrade.EntryPrice, ""+activeTrade.ExitPrice, ""+percentGain, ""+activeTrade.getOpenDate(), ""+activeTrade.getCloseDate(), ""+currentBalance};
            getInstance().output.addToOutput(1, outputToLog);

            String side = "null";

            //if(position > 0) { side = "Sell"; } else { side = "Buy"; }
            //    getInstance().BitmexDelegate.placeOrder(side, activeTrade.getExchangeID(), 1, "Market");

            strategy.tradeIncrement(activeTrade);

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
        getInstance().metrics = new PortfolioMetrics();

        getInstance().delegate = new CoinbaseDelegate();
        getInstance().websocket = new CoinbaseWebsocket();

        //TODO: getInstance().strategy = new RSIStrategy();
        getInstance().strategy = new RVol_RSI_Strategy(72,4.0, 24,72, 0.5);
        getInstance().strategy = new
        //getInstance().strategy = new BBStrategy();
        getInstance().exitStrategy = new RollingStopStrategy();

        getInstance().output = new CSVOutput();
        getInstance().input = new CSVInput();

        getInstance().startBackTest();
        //getInstance().startForwardTest();
        //getInstance().inputBacktest();


        getInstance().BitmexDelegate = new BitMexDelegate();

        getInstance().output.addToOutput(1, getInstance().metrics.metricsToCsv());
        getInstance().output.SaveSession("RVol4_0x72_THE_BTC-USD_FULL_NUTS");

    }


    private void inputBacktest() {
        input.fetchCandles(15);
    }


    private void startForwardTest() {
        int delay = 15000;   // delay for one minute.
        int period = 36000000;  // repeat every 5 minutes and 2 seconds.
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
        long startValue = 1505000000000L;
        //long startValue = 1504000000000L;

        Date begin = new Date(startValue);
        Date end = new Date((startValue) + (900 * 300 * 1000));

        for(int i = 0; i < 150; i++) {

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
            end = new Date(startValue + (3600 * 200 * 1000));

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

    public CSVOutput getOutput() {
        return output;
    }

    public Portfolio setOutput(CSVOutput output) {
        this.output = output;
        return this;
    }
}
