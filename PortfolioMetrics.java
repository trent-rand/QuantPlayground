import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by TrentRand on 2019-07-20.
 */
public class PortfolioMetrics extends FinancialFunctions {

    private double alpha;
    private double beta;
    private double sharpeRatio;
    private double maxDrawdown;

    private double totalPnL;

    private ArrayList<Double> cashBalances = new ArrayList<Double>();

    private double avgWin = 0;
    private double avgLoss = 0;

    private double maxLoss;
    private double maxWin;

    private ArrayList<Trade> winners = new ArrayList<Trade>();
    private ArrayList<Trade> losers = new ArrayList<Trade>();


    public void updateOnTrade(Trade trade, double cash) {
        cashBalances.add(cash);

        //double[] cashArray = cashBalances.toArray();
        updateMaxDrawdown(cashBalances);

        double PnL = trade.getPercentGainAdjusted();
        if (PnL > 0) {

            winners.add(trade);
            updateAvgWin(PnL);
            updateMaxWin(PnL);

        } else if (PnL < 0) {

            losers.add(trade);
            updateAvgLoss(PnL);
            updateMaxLoss(PnL);
        }


    }

    //Updaters for the incrementation//
    public void updateAlpha() {}
    public void updateBeta() {}

    public void updateMaxWin(double PnL) {
        if (PnL > maxWin) {
            maxWin = PnL;
        }
    }

    public void updateMaxLoss(double PnL) {
        if (PnL < maxLoss) {
            maxLoss = PnL;
        }
    }

    public void updateAvgWin(double PnL) {
        double sum = 0;
        for (int i = 0; i < winners.size(); i++) {
            sum = sum + winners.get(i).getPercentGainAdjusted();

        }

        avgWin = sum/winners.size();
    }
    public void updateAvgLoss(double PnL) {
        double sum = 0;
        for (int i = 0; i < losers.size(); i++) {
            sum = sum + losers.get(i).getPercentGainAdjusted();

        }

        avgLoss = sum/losers.size();
    }

    public void updateMaxDrawdown(ArrayList<Double> prices) {
        if (prices.size() <= 1) { return; }

        double maxPrice = prices.get(0);
        double maxDd = 0;

        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) > maxPrice) {
                maxPrice = prices.get(i);
            } else if (prices.get(i) < maxPrice) {
                maxDd = Math.min(maxDd, prices.get(i) / maxPrice - 1);
            }
        }

        setMaxDrawdown(maxDd);
    }



    public String[] metricsToCsv() {
        String[] toReturn = { "alpha: "+alpha, "beta: "+beta,
                "sharpe: "+sharpeRatio, "maxDD: "+maxDrawdown,
                "PnL: "+totalPnL, "cash: "+cashBalances.get(cashBalances.size() - 1),
                "#ofWins: "+winners.size(), "#ofLosses: "+losers.size(),
                "avgWin: "+avgWin, "avgLoss: "+avgLoss,
                "maxWin: "+maxWin, "maxLoss: "+maxLoss,
        };
        return toReturn;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getSharpeRatio() {
        return sharpeRatio;
    }

    public void setSharpeRatio(double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    public double getMaxDrawdown() {
        return maxDrawdown;
    }

    public void setMaxDrawdown(double maxDrawdown) {
        this.maxDrawdown = maxDrawdown;
    }

    public double getTotalPnL() {
        return totalPnL;
    }

    public void setTotalPnL(double totalPnL) {
        this.totalPnL = totalPnL;
    }

    public double getAvgWin() {
        return avgWin;
    }

    public void setAvgWin(double avgWin) {
        this.avgWin = avgWin;
    }

    public double getAvgLoss() {
        return avgLoss;
    }

    public void setAvgLoss(double avgLoss) {
        this.avgLoss = avgLoss;
    }
}
