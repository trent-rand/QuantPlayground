import java.io.FileWriter;

/**
 * Created by Trent Rand on 09/Feb/18.
 */
public class CSVOutput {

    String sessionLog = "null";
    String tradeLog = "null";

    public void addToOutput(int LogType, String[] data) {
        switch (LogType) {

            case 0:
                for (int l = 0; l < data.length; l++) {
                    tradeLog = tradeLog + "," + data[l];
                }
                tradeLog = tradeLog + "\n";
                break;
            default:
                for (int l = 0; l < data.length; l++) {
                    sessionLog = sessionLog + "," + data[l];
                }
                sessionLog = sessionLog + "\n";
                break;
        }
    }

    public void SaveSession(String date) {
        try {
            String filename = "Session"+date+".csv";
            FileWriter writer = new FileWriter(filename);
            writer.append(sessionLog.subSequence(0, sessionLog.length()));

            sessionLog = "null";

            writer.flush();
            writer.close();
        } catch(Exception e) {
            System.out.print("Error saving the output file!\n");
        }
    }





}
