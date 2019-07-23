import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by TrentRand on 2019-07-17.
 *
 * Simple Class to structure the Open,High,Low,Close,Volume,Date data that is often used in financial modelling.
 * Use Class "Tick" for tick-based trading systems.
 *
 *
 *
 */
public class Candlestick {

        //TURN ME INTO A BUILDER! PLEASE! FOR THE LOVE OF *****!//

        String source = "source";

        Date startDate;
        Date endDate;

        public Date getStartDate() {
                return startDate;
        }

        public void setStartDate(Date startDate) {
                this.startDate = startDate;
        }

        public int getTime() {
                return time;
        }

        public void setTime(int time) {
                this.time = time;
        }

        public double getLow() {
                return low;
        }

        public void setLow(double low) {
                this.low = low;
        }

        public double getHigh() {
                return high;
        }

        public void setHigh(double high) {
                this.high = high;
        }

        public double getOpen() {
                return open;
        }

        public void setOpen(double open) {
                this.open = open;
        }

        public double getClose() {
                return close;
        }

        public void setClose(double close) {
                this.close = close;
        }

        public double getVolume() {
                return volume;
        }

        public void setVolume(double volume) {
                this.volume = volume;
        }

        int time = 300;
        double low = 1000000;
        double high = 1.00;
        double open = 1.00;
        double close = 1.20;
        double volume = 100000;

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }



}
