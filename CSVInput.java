import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by TrentRand on 2019-07-19.
 */
public class CSVInput {



    public void fetchCandles(int period) { //This assumes each line is given 1min Date,O,H,L,C,Volume data.

        Candlestick currentCandle = new Candlestick();

        try {

            BufferedReader reader = new BufferedReader(new FileReader("input/inputFile.csv"));
            String line;

            System.out.println("Reset to 0!");
            int i = 0;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (i == 0) {
                    currentCandle.setOpen(Double.parseDouble(values[1]));
                    i++;

                } else if (i == period - 1) {
                    currentCandle.setClose(Double.parseDouble(values[4]));

                    Portfolio main = Portfolio.getInstance();
                    main.getStrategy().increment(currentCandle);

                    //System.out.println("Latest Candle: \nLow: "+currentCandle.low+"\nHigh: "+currentCandle.high+"\nOpen: "+currentCandle.open+"\nClose: "+currentCandle.close);

                    currentCandle = new Candlestick();

                    i = 0;

                } else {
                    double tempHigh = Double.parseDouble(values[2]);
                    double tempLow = Double.parseDouble(values[3]);


                    //Setup Current Candle.
                    if(tempHigh > currentCandle.getHigh()) {
                        currentCandle.high = tempHigh;
                    } else if (tempLow < currentCandle.low) {
                        currentCandle.low = tempLow;
                    }

                    i++;
                }
            }


        } catch (Exception e) {
            System.out.println("Error parsing the latest input line :"+e);
        }
    }







}
