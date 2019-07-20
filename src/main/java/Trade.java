import java.util.Date;

/**
 * Created by Trent Rand on 10/Feb/18.
 */
public class Trade {

    double EntryPrice = 0.00;
    double ExitPrice = 0.00;

    double size;

    Date CloseDate;
    Date OpenDate;

    public Date getOpenDate() {
        return OpenDate;
    }

    public void setOpenDate(Date openDate) {
        OpenDate = openDate;
    }

    public Date getCloseDate() {
        return CloseDate;
    }

    public void setCloseDate(Date closeDate) {
        CloseDate = closeDate;
    }

    public double getEntryPrice() {
        return EntryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        EntryPrice = entryPrice;
    }

    public double getExitPrice() {
        return ExitPrice;
    }

    public void setExitPrice(double exitPrice) {
        ExitPrice = exitPrice;
    }
}
