import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by TrentRand on 2019-07-17.
 */
public class CoinbaseDelegate {

    //testnet.BitMEX.com;


    public void enterTrade (int type, double size) {
        //Type: 0 - market, 1 - Limit, 2 - Stop Market
        //Defaults to Market base don size.
        switch (type) {

            case 0:


                break;
            case 1:


                break;
            case 2:


                break;
            default:


                break;
        }


    }

    public void exitTrade (Trade trade) {

    }

    public void cancelTrade () {

    }

    public void flatten () {

    }


    public ArrayList<Candlestick> backTest(String productId, Date startDate, Date endDate, int granularity) {
        ArrayList<Candlestick> toReturn = new ArrayList<Candlestick>();

        //Setup Request from Input Arguments
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String startAsISO = df.format(startDate);
        String endAsISO = df.format(endDate);

        //System.out.println("Setting Up Request...");

        //TODO: This is wild. Gotta handle Dates better. I don't think this ever gets called anymore.
        //System.out.println(startAsISO);
        //System.out.println(endAsISO);
         if(!(endAsISO.substring(endAsISO.length() - 3)).matches(":00")) {
             endAsISO = endAsISO.substring(0, endAsISO.length() - 3) + "00";
             System.out.println("Had to fix the date: "+endAsISO);
         }

        //Setup Base HTTP Request. Catch Errors.
        try {
            URL url = new URL("https://api.gdax.com/products/" + productId + "/candles?start="+startAsISO+"&end="+endAsISO+"&granularity="+granularity); //Might need to add a slash at the end of this one, bruh.
            //System.out.println(url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);

            //Setup HTTP Headers//
            con.setRequestProperty("Content-Type", "application/json");
            String contentType = con.getHeaderField("Content-Type");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);


            //"Shoot'er 'cross the bow!"//
            //System.out.println("Successfully Set Up Request!");
            System.out.println(con.getURL()+"\n");

            con.connect();

            //Parse HTTP Response Code//
            int statusCode = con.getResponseCode();
            switch (statusCode) {

                //"Handle Successful Response"//
                case 200:

                    //System.out.println("Status 200! We're good to Parse! Output incoming!");

                    //Successful Connection, attempting to parse response!//
                    InputStreamReader inputReader = new InputStreamReader(con.getInputStream());
                    BufferedReader in = new BufferedReader(inputReader);

                    String tempLine;
                    String toParse = "";
                    StringBuffer content = new StringBuffer();

                    while ((tempLine = in.readLine()) != null) {
                        toParse = tempLine;
                    }

                    try {
                        JSONArray jsonInput = new JSONArray(toParse);
                        //System.out.println("Line to parse: "+jsonInput);

                        for(int j = 0; j < jsonInput.length(); j++) {
                            JSONArray candle = jsonInput.getJSONArray(j);

                            //System.out.println("Candle: "+candle);

                            Candlestick tempCandle = new Candlestick();

                            //Dates are clearly whack upon compliation.//
                            //So now I gotta do this, I should just use python.//
                            Long dateLong = Long.decode(candle.get(0).toString());
                            dateLong = dateLong * 1000;
                            Date tempDate = new Date(dateLong);

                            //System.out.println("Parsed End Date: "+tempDate);

                            tempCandle.setEndDate(tempDate);
                            tempCandle.low = Double.parseDouble(candle.get(1).toString());
                            tempCandle.high = Double.parseDouble(candle.get(2).toString());
                            tempCandle.open = Double.parseDouble(candle.get(3).toString());
                            tempCandle.close = Double.parseDouble(candle.get(4).toString());
                            tempCandle.volume = Double.parseDouble(candle.get(5).toString());

                            toReturn.add(tempCandle);
                        }

                    } catch(Exception e) {
                        System.out.println("Error parsing input!");
                        System.err.println(e);
                    }

                    in.close();

                    //System.out.println("Returning Complete Candlestick Array!");

                    Collections.reverse(toReturn);
                    //System.out.println(toReturn);

                    for(int i = 0; i < toReturn.size(); i++) {
                        Portfolio.getInstance().getOutput().addCandle(toReturn.get(i));
                    }

                    return toReturn;

                //"Strike Colors!//
                default:
                    System.out.println("HTTP Response Unexpected:");
                    System.out.println(statusCode);
                    System.out.println(con.getResponseMessage());


                    return toReturn;
            }


        } catch (Exception e) {
            System.out.print("Error setting up and executing HTTP Request. Prices are not real time!");
        }

        return toReturn;
    }
}
