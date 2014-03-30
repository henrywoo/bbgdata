package BBGTick;
//-sd 2012-11-01T05:30:00 -ed 2012-11-02T20:30:00
//http://stackoverflow.com/questions/8559165/converting-date-from-utc-to-est-in-java

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.bloomberglp.blpapi.Datetime;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Name;
import com.bloomberglp.blpapi.SessionOptions;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.io.PrintWriter;

public class BBGRawTick {
    private static final Name TICK_DATA      = new Name("tickData");
    private static final Name COND_CODE      = new Name("conditionCodes");
    private static final Name EXCH_CODE      = new Name("exchangeCodes");
    private static final Name SIZE           = new Name("size");
    private static final Name TIME           = new Name("time");
    private static final Name TYPE           = new Name("type");
    private static final Name VALUE          = new Name("value");
    private static final Name RESPONSE_ERROR = new Name("responseError");
    private static final Name CATEGORY       = new Name("category");
    private static final Name MESSAGE        = new Name("message");

    private String            d_host;
    private int               d_port;
    //private String            d_security;
    private ArrayList<String> d_secs;
    private ArrayList<String> d_events;
    private String            d_startDateTime;
    private String            d_endDateTime;
    private SimpleDateFormat  d_dateFormat;
    private DecimalFormat     d_decimalFormat;
    private PrintWriter writer;
    private boolean ishk;
    private String d_dir;

    public static void main(String[] args) throws Exception
    {
        BBGRawTick brt = new BBGRawTick();
        brt.getSec();
        brt.run(args);
        //System.out.println("Press ENTER to quit");
        //System.in.read();
    }
    
    private void getSec() throws Exception {
        d_secs = new ArrayList<String>();
        ishk = false;
        try {
            if (ishk) {
                d_dir = "hkse-main2";
                //Scanner s = new Scanner(new File("hkse-main.csv"));
                FileInputStream fstream = new FileInputStream("hkse-main.csv");
                //ArrayList<String> list = new ArrayList<String>();
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                int i = 0;
                while ((line = br.readLine()) != null) {
                    i++;
                    if (i < 2) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    String sec = parts[0];
                    sec = sec.replaceFirst("^0+(?!$)", "");
                    sec = sec.replace("\"", "");
                    String filePathString="F:\\uploaded\\"+sec+".HK.csv";
                    File f = new File(filePathString);
                    if(!f.exists()) {
                        d_secs.add(sec);
                    }
                    //System.out.println(part1);
                }
                in.close();
            } else {
                //d_dir = "nasdaq-china";
                d_dir = "henry";
                //FileInputStream fstream = new FileInputStream("nasdaq-china.csv");
                FileInputStream fstream = new FileInputStream("henry.lst");
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                //Read File Line By Line
                int i = 0;
                while ((line = br.readLine()) != null) {
                    i++;
                    if (i < 2) {
                        continue;
                    }
                    // Print the content on the console
                    //System.out.println (strLine);
                    String[] parts = line.split(",");
                    String sec = parts[0];
                    sec = sec.replaceFirst("^0+(?!$)", "");
                    sec = sec.replace("\"", "");
                    d_secs.add(sec);
                }
                //Close the input stream
                in.close();
            }


        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    private Calendar getPreviousTradingDate(){
        Calendar rightNow = Calendar.getInstance();
        rightNow.roll(Calendar.DAY_OF_MONTH, -1);
        if (rightNow.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            rightNow.roll(Calendar.DAY_OF_MONTH, -2);
        }else if (rightNow.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            rightNow.roll(Calendar.DAY_OF_MONTH, -1);
        }

        rightNow.set(Calendar.HOUR_OF_DAY, 13);
        rightNow.set(Calendar.MINUTE, 30);
        rightNow.set(Calendar.SECOND, 0);

        return rightNow;
    }


    public BBGRawTick()
    {
        d_host = "localhost";//172.29.34.244
        //d_host = "172.29.34.244";
        d_port = 8194;
        //d_security = "ATHM US Equity";//stock
        //d_security = "QZX2 Index";   //futures index
        //d_security = "GBPUSD Curncy";//forex option
        //d_security = "GOOG US 11/17/12 C680 EQUITY";//option
        //d_security = "BITA US 03/22/14 C35 Equity";
        d_secs = new ArrayList<String>();
        //d_secs.add("BITA US Equity");
        d_events = new ArrayList<String>();
        d_dateFormat = new SimpleDateFormat();
        d_dateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        // k	hour in day (1-24)	Number	24
        d_decimalFormat = new DecimalFormat();
        d_decimalFormat.setMaximumFractionDigits(3);
        
        d_startDateTime="2013-03-22T00:00:00";
        d_endDateTime="2014-03-30T00:00:00";
    }

    private void run(String[] args) throws Exception
    {
        if (!parseCommandLine(args)) return;
        SessionOptions sessionOptions = new SessionOptions();
        sessionOptions.setServerHost(d_host);
        sessionOptions.setServerPort(d_port);

        System.out.println("Connecting to " + d_host + ":" + d_port);
        Session session = new Session(sessionOptions);
        if (!session.start()) {
            System.err.println("Failed to start session.");
            return;
        }
        if (!session.openService("//blp/refdata")) {
            System.err.println("Failed to open //blp/refdata");
            return;
        }
        
        //172.29.34.244
        
        //TODO: Read stocks data into d_secs!!!
        
        for (String security : d_secs){
            //writer = new PrintWriter(security+".csv", "UTF-8");//new FileOutputStream(new File("persons.txt"),true)
            writer = new PrintWriter(new FileOutputStream(new File("henry\\"+security+".csv"),true));
            //writer.println("TIME,TYPE,VALUE,SIZE,CC");
            sendIntradayTickRequest(session, security);
            // wait for events from session.
            eventLoop(session);
            writer.close();
        }
        session.stop();
    }

    private void eventLoop(Session session) throws Exception
    {
        boolean done = false;
        while (!done) {
            Event event = session.nextEvent();
            if (event.eventType() == Event.EventType.PARTIAL_RESPONSE) {
                System.out.println("Processing Partial Response");
                processResponseEvent(event);
            }
            else if (event.eventType() == Event.EventType.RESPONSE) {
                System.out.println("Processing Response");
                processResponseEvent(event);
                done = true;
            } else {
                MessageIterator msgIter = event.messageIterator();
                while (msgIter.hasNext()) {
                    Message msg = msgIter.next();
                    System.out.println(msg.asElement());
                    if (event.eventType() == Event.EventType.SESSION_STATUS) {
                        if (msg.messageType().equals("SessionTerminated")) {
                            done = true;
                        }
                    }
                }
            }
        }
    }
    
    private String RMComma(String s, String to){
        return s.replace(",", to);
    }

    private void processMessage(Message msg) throws Exception {
        Element data = msg.getElement(TICK_DATA).getElement(TICK_DATA);
        int numItems = data.numValues();
        System.out.println("TIME\t\t\tTYPE\t\tVALUE\t\tSIZE\tCC");
        System.out.println("----\t\t\t----\t\t-----\t\t----\t--");

        for (int i = 0; i < numItems; ++i) {
            Element item = data.getValueAsElement(i);
            Datetime time = item.getElementAsDate(TIME);
            String type = item.getElementAsString(TYPE);
            double value = item.getElementAsFloat64(VALUE);
            int size = item.getElementAsInt32(SIZE);
            if (value==0 && size==0){
                continue;
            }
            String cc = "";
            String exch = "";
            if (item.hasElement(COND_CODE)) {
                cc = RMComma(item.getElementAsString(COND_CODE),"|");
                // cc - composite close; oc - official local exchange close
                // R6,IS - rule 611 trade, inter market sweep order, OL - odd lot trade
                // T - nasdaq trade reporting facility
                // 6 - market center closing trade
                // CL - closing quotation
            }
            

            String t=d_dateFormat.format(time.calendar().getTime());
            String ty=type;
            String val=RMComma(d_decimalFormat.format(value),"");
            String sz=RMComma(d_decimalFormat.format(size),"");

            writer.println(t+","+ty+","+val+","+sz+","+cc);
            
            if (item.hasElement(EXCH_CODE)) {
                exch = RMComma(item.getElementAsString(EXCH_CODE),"|");
                System.out.println(t+"\t"+ty+"\t"+val+"\t\t"+sz+"\t"+cc+"\t"+exch);
            }
            //System.out.println(t+"\t"+ty+"\t"+val+"\t\t"+sz+"\t"+cc+"\t"+exch);
        }
    }

    private void processResponseEvent(Event event) throws Exception {
        MessageIterator msgIter = event.messageIterator();
        while (msgIter.hasNext()) {
            Message msg = msgIter.next();
            if (msg.hasElement(RESPONSE_ERROR)) {
                printErrorInfo("REQUEST FAILED: ", msg.getElement(RESPONSE_ERROR));
                continue;
            }
            processMessage(msg);
        }
    }

    private void sendIntradayTickRequest(Session session, String sec) throws Exception
    {
        Service refDataService = session.getService("//blp/refdata");
        Request request = refDataService.createRequest("IntradayTickRequest");

        //request.set("security", sec);
        request.set("security", sec+ (ishk?" HK EQUITY":" US EQUITY"));

        // Add fields to request
        Element eventTypes = request.getElement("eventTypes");
        for (String event : d_events) {
            eventTypes.appendValue(event);
        }
        eventTypes.appendValue("BEST_BID");//henry
        eventTypes.appendValue("BEST_ASK");
        //eventTypes.appendValue("BID");
        //eventTypes.appendValue("ASK");

        // All times are in GMT/UTC
        request.set("startDateTime", d_startDateTime);
        request.set("endDateTime", d_endDateTime);
        request.set("includeConditionCodes", true);
        System.out.println("Sending Request: " + request);
        session.sendRequest(request, null);
    }

    private boolean parseCommandLine(String[] args)
    {
        for (int i = 0; i < args.length; ++i) {
            /*if (args[i].equalsIgnoreCase("-s")) {
                d_security = args[i+1];
            }
            else */if (args[i].equalsIgnoreCase("-e")) {
                d_events.add(args[i+1]);
            }
            else if (args[i].equalsIgnoreCase("-sd")) {
                d_startDateTime = args[i+1];
            }
            else if (args[i].equalsIgnoreCase("-ed")) {
                d_endDateTime = args[i+1];
            }
            else if (args[i].equalsIgnoreCase("-ip")) {
                d_host = args[i+1];
            }
            else if (args[i].equalsIgnoreCase("-p")) {
                d_port = Integer.parseInt(args[i+1]);
            }
            else if (args[i].equalsIgnoreCase("-h")) {
                printUsage();
                return false;
            }
        }

        if (d_events.size() == 0) {
            d_events.add("TRADE");
        }

        return true;
    }

    private void printErrorInfo(String leadingStr, Element errorInfo)
    throws Exception
    {
        System.out.println(leadingStr + errorInfo.getElementAsString(CATEGORY) +
                           " (" + errorInfo.getElementAsString(MESSAGE) + ")");
    }

    private void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("  Retrieve intraday rawticks ");
        //System.out.println("    [-s     <security	= IBM US Equity>");
        System.out.println("    [-e     <event		= TRADE>");
        System.out.println("    [-sd    <startDateTime  = 2008-02-11T15:30:00>");
        System.out.println("    [-ed    <endDateTime    = 2008-02-11T15:35:00>");
        System.out.println("    [-cc    <includeConditionCodes = false>");
        System.out.println("    [-ip    <ipAddress	= localhost>");
        System.out.println("    [-p     <tcpPort	= 8194>");
        System.out.println("Notes:");
        System.out.println("1) All times are in GMT.");
        System.out.println("2) Only one security can be specified.");
    }
}
