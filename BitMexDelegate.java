import org.apache.commons.codec.binary.Hex;
import org.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by TrentRand on 2019-07-17.
 */
public class BitMexDelegate {

    //Actual Key Connection. "Syrena"
    String apiID = "PIdNimaJFHvnJFS7wE-uwn4y";
    String apiSecret = "hTs08aTr1j6H5lps_SiY5WoPswLsAu9hQP11X4D2fXLntxy4";

    //Signature Test Components
    //String apiSecret = "chNOOS4KvNXR_Xq4k4c9qsfoKWvnDecLATCRlcBwyKDYnWgO";
    //String apiID = "LAqUlngMIQkIUjXMUreyu3qn";


    public void placeOrder(String side, String orderID, double Quant, String type) {

        //Setup Request from Input Arguments
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        Date expiryDate = new Date();
        Long millis = expiryDate.getTime() + 12000;
        millis = millis/1000;
        String endAsISO = ""+millis;

        //Signature Test Component
        //endAsISO = "1518064238";

        System.out.println(endAsISO);

        //Setup Base HTTP Request. Catch Errors.
        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

        try {
            HttpPost request = new HttpPost("https://www.bitmex.com/api/v1/order");
            request.addHeader("Content-Type", "application/json");

            String jsonInputString = "{\"symbol\":\"XBTUSD\"," +
                    "\"side\":\""+side+"\"," +
                    "\"orderQty\":"+Quant+"," +
                    "\"clOrdID\":\""+orderID+"\"," +
                    "\"ordType\":\""+type+"\"}";

            //Signature Test Component
            //jsonInputString = "{\"symbol\":\"XBTM15\",\"price\":219.0,\"clOrdID\":\"mm_bitmex_1a/oemUeQ4CAJZgP3fjHsA\",\"orderQty\":98}";

            StringEntity jsonEntity = new StringEntity(jsonInputString);

            String signature = createSignature("POST","/api/v1/order", endAsISO, jsonInputString);

            request.addHeader("api-expires", endAsISO);
            request.addHeader("api-key", apiID);
            request.addHeader("api-signature", signature);

            request.setEntity(jsonEntity);

            //System.out.println("Signature created: "+signature);
            //System.out.println("Json Payload: "+jsonInputString);
            //System.out.println(request.getEntity().toString());
            //System.out.println(request.getAllHeaders().toString());

            HttpResponse response = httpClient.execute(request);

            //"Shoot'er 'cross the bow!"//
            System.out.println("Successfully Set Up Request!");

            System.out.println(response.getStatusLine());
            System.out.println(response.toString());


            //Parse HTTP Response Code//
            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {

                //"Handle Successful Response"//
                case 200:

                    System.out.println("Status 200! We're good to Parse! Output incoming!");

                    //Successful Connection, attempting to parse response!//
                    InputStreamReader inputReader = new InputStreamReader(response.getEntity().getContent());
                    BufferedReader in = new BufferedReader(inputReader);

                    String tempLine;
                    String toParse = "";
                    StringBuffer content = new StringBuffer();

                    while ((tempLine = in.readLine()) != null) {
                        toParse = tempLine;
                    }

                    try {
                        JSONArray jsonInput = new JSONArray(toParse);
                        System.out.println("Line to parse: "+jsonInput);


                    } catch(Exception e) {
                        System.out.println("Error parsing input!");
                        System.err.println(e);
                    }

                    in.close();

                    System.out.println("Returning Complete Candlestick Array!");



                //"Strike Colors!//
                default:
                    System.out.println("HTTP Response Unexpected:");
                    System.out.println(statusCode);

                    InputStreamReader inptReader = new InputStreamReader(response.getEntity().getContent());
                    BufferedReader inn = new BufferedReader(inptReader);

                    String tmpLine;
                    String tParse = "";

                    while ((tempLine = inn.readLine()) != null) {
                        tParse = tempLine;
                    }

                    System.out.println("Parsing Line: "+tParse);

            }


        } catch (Exception e) {
            System.out.print("Error setting up and executing HTTP Request! "+e);
        }


    }






    @SuppressWarnings("Since15")
    public String createSignature(String verb, String path, String expiry, String payload) {
        String data = verb+path+expiry+payload;

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            System.out.println("Serializing Following Signature: "+data);

            return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Definitely screwed up hashing our signature.");
        }
        String toReturn = "Nothing";
        return toReturn;
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

        System.out.println(startAsISO);
        System.out.println(endAsISO);
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
            System.out.println("Successfully Set Up Request!");
            System.out.println(con.getURL());
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

                            tempCandle.setEndDate(new Date(Integer.parseInt(candle.get(0).toString())));
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

                    System.out.println("Returning Complete Candlestick Array!");

                    //Collections.reverse(toReturn);
                    System.out.println(toReturn);

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