import org.json.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by TrentRand on 2019-07-18.
 */
public class CoinbaseWebsocket extends TextWebSocketHandler {

    Candlestick currentCandle = new Candlestick();
    Date CurrentMinute = Calendar.getInstance().getTime();


    public void logTrade(String tradeSource) {

        try {
            //JSONArray tradeContainer = new JSONArray(tradeSource);
            //JSONObject tradeObject  = new JSONObject(tradeContainer.getJSONObject(0));
            JSONObject tradeObject  = new JSONObject(tradeSource);

            //System.out.print(tradeObject.toString());

            String priceStr = tradeObject.getString("price");
            double price = Double.parseDouble(priceStr);

            //try {
            String dateStr = tradeObject.getString("time");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date time = df.parse(dateStr);

            Date now = new Date();

            //System.out.print("Date Source: "+dateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(CurrentMinute);
            calendar.add(Calendar.SECOND, (60 * 60)); //Hourly Candles

            Date endOfCandle = calendar.getTime();


            currentCandle.close = price;

            //System.out.println("End Of Candle: "+endOfCandle);


            if (now.compareTo(endOfCandle) > 0) {
                Portfolio main = Portfolio.getInstance();
                main.getStrategy().increment(currentCandle);

                System.out.println("Latest Candle: \nLow: "+currentCandle.low+"\nHigh: "+currentCandle.high+"\nOpen: "+currentCandle.open+"\nClose: "+currentCandle.close);

                currentCandle = new Candlestick();
                currentCandle.open = price;

                CurrentMinute = now;
            }
            //} catch (Exception t) { System.out.print("Error parsing Date!"); }
            //Setup Current Candle.
            if(price > currentCandle.high) {
                currentCandle.high = price;
            } else if (price < currentCandle.low) {
                currentCandle.low = price;
            }

        } catch (Exception e) {
            System.out.println("Error Handling Latest Data!");
            System.out.println("Error: "+e);
            System.out.println(tradeSource);

        }

    }

    //WebSocket Overrides.
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        //System.out.println("Message Received: " + message.getPayload());

        logTrade(message.getPayload());

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String payload = "{\"type\": \"subscribe\",\"channels\":[{\"name\": \"ticker\",\"product_ids\": [\"BTC-USD\"]}]}";
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("Transport Error: "+ exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        System.out.println("Connection Closed [" + status.getReason() + "]");
    }


}
