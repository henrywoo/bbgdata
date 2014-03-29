package BBGTick;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.bloomberglp.blpapi.Datetime;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Name;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;
import java.util.Calendar;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class IntraDayBar {

    private static final Name BAR_DATA = new Name("barData");
    private static final Name BAR_TICK_DATA = new Name("barTickData");
    private static final Name OPEN = new Name("open");
    private static final Name HIGH = new Name("high");
    private static final Name LOW = new Name("low");
    private static final Name CLOSE = new Name("close");
    private static final Name VOLUME = new Name("volume");
    private static final Name NUM_EVENTS = new Name("numEvents");
    private static final Name TIME = new Name("time");
    private static final Name RESPONSE_ERROR = new Name("responseError");
    private static final Name CATEGORY = new Name("category");
    private static final Name MESSAGE = new Name("message");
    private static final Name VALUE = new Name("value");

    private String d_host;
    private int d_port;
    private String d_security;
    private String d_eventType;
    private int d_barInterval;
    private boolean d_gapFillInitialBar;
    private String d_startDateTime;
    private String d_endDateTime;
    private SimpleDateFormat d_dateFormat;
    private DecimalFormat d_decimalFormat;
    private ArrayList<String> d_secs;
    private PrintWriter writer;

    private boolean ishk;

    private String d_dir;

    public static void main(String[] args) throws Exception {
        System.out.println("Intraday Bars Example");
        IntraDayBar example = new IntraDayBar();
        example.getSec();
        example.run(args);

        System.out.println("Press ENTER to quit");
        System.in.read();
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

    private void sendIntradayBarRequest(Session session, String sec) throws Exception {
        Service refDataService = session.getService("//blp/refdata");
        Request request = refDataService.createRequest(
                "IntradayBarRequest");

        // only one security/eventType per request
        request.set("security", sec+ (ishk?" HK EQUITY":" US EQUITY"));
        request.set("eventType", d_eventType);
        request.set("interval", d_barInterval);

        d_startDateTime="2012-02-15T00:00:00";
        d_endDateTime="2014-04-16T00:00:00";

        /*if (d_startDateTime == null || d_endDateTime == null) {
            Calendar calendar = getPreviousTradingDate();
            Datetime prevTradeDateTime = new Datetime(calendar);

            // set the end date for next day
            calendar.roll(Calendar.DAY_OF_MONTH, +1);
            Datetime endDateTime = new Datetime(calendar);

            request.set("startDateTime", prevTradeDateTime);
            request.set("endDateTime", endDateTime);
        } else {
            request.set("startDateTime", d_startDateTime);
            request.set("endDateTime", d_endDateTime);
        }*/
        request.set("startDateTime", d_startDateTime);
        request.set("endDateTime", d_endDateTime);

        if (d_gapFillInitialBar) {
            request.set("gapFillInitialBar", d_gapFillInitialBar);
        }

        System.out.println("Sending Request: " + request);
        session.sendRequest(request, null);
    }

    private Calendar getPreviousTradingDate() {
        Calendar prevDate = Calendar.getInstance();
        prevDate.roll(Calendar.DAY_OF_MONTH, -1);
        if (prevDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            prevDate.roll(Calendar.DAY_OF_MONTH, -2);
        } else if (prevDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            prevDate.roll(Calendar.DAY_OF_MONTH, -1);
        }
        prevDate.set(Calendar.HOUR_OF_DAY, 13);
        prevDate.set(Calendar.MINUTE, 30);
        prevDate.set(Calendar.SECOND, 0);
        return prevDate;
    }

    public IntraDayBar() {
        d_host = "localhost";
        d_port = 8194;
        d_barInterval = 1;
        //d_security = "IBM US Equity";
        d_eventType = "TRADE";
        d_gapFillInitialBar = false;

        d_dateFormat = new SimpleDateFormat();
        d_dateFormat.applyPattern("yyyyMMdd k:mm");
        d_decimalFormat = new DecimalFormat();
        d_decimalFormat.setMaximumFractionDigits(3);
    }

    private void run(String[] args) throws Exception {
        if (!parseCommandLine(args)) {
            return;
        }

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

        for (String security : d_secs) {
            writer = new PrintWriter(d_dir+"/"+security + ".csv", "UTF-8");
            writer.println("Datetime,Open,High,Low,Close,Value,NumEvents,Volume");
            sendIntradayBarRequest(session, security);
            System.out.println(security);
            eventLoop(session);
            writer.close();
            System.out.println(security + "");
            //writer = new PrintWriter(d_dir+"/"+"done." + security + ".csv", "UTF-8");
            //writer.close();
        }

        session.stop();
    }

    private void eventLoop(Session session) throws Exception {
        boolean done = false;
        while (!done) {
            Event event = session.nextEvent();
            if (event.eventType() == Event.EventType.PARTIAL_RESPONSE) {
                System.out.println("Processing Partial Response");
                processResponseEvent(event, session);
            } else if (event.eventType() == Event.EventType.RESPONSE) {
                System.out.println("Processing Response");
                processResponseEvent(event, session);
                done = true;
            } else {
                MessageIterator msgIter = event.messageIterator();
                while (msgIter.hasNext()) {
                    Message msg = msgIter.next();
                    System.out.println(msg);
                    if (event.eventType() == Event.EventType.SESSION_STATUS) {
                        if (msg.messageType().equals("SessionTerminated")) {
                            done = true;
                        }
                    }
                }
            }
        }
    }

    private void processMessage(Message msg) throws Exception {
        Element data = msg.getElement(BAR_DATA).getElement(BAR_TICK_DATA);
        int numBars = data.numValues();
        /*System.out.println("Response contains " + numBars + " bars");
        System.out.println("Datetime\t\tOpen\t\tHigh\t\tLow\t\tClose\t\tNumEvents\tVolume");*/

        for (int i = 0; i < numBars; ++i) {
            Element bar = data.getValueAsElement(i);
            Datetime time = bar.getElementAsDate(TIME);
            double open = bar.getElementAsFloat64(OPEN);
            double high = bar.getElementAsFloat64(HIGH);
            double low = bar.getElementAsFloat64(LOW);
            double close = bar.getElementAsFloat64(CLOSE);
            int numEvents = bar.getElementAsInt32(NUM_EVENTS);
            long volume = bar.getElementAsInt64(VOLUME);
            double value = bar.getElementAsFloat64(VALUE);

            String t = d_dateFormat.format(time.calendar().getTime());
            String _open = d_decimalFormat.format(open);
            _open=_open.replace(",", "");
            String _high = d_decimalFormat.format(high);
            _high=_high.replace(",", "");
            String _low = d_decimalFormat.format(low);
            _low=_low.replace(",", "");
            String _close = d_decimalFormat.format(close);
            _close=_close.replace(",", "");
            String _numEv = d_decimalFormat.format(numEvents);
            _numEv=_numEv.replace(",", "");
            String vol = d_decimalFormat.format(volume);
            vol=vol.replace(",", "");
            String _value = d_decimalFormat.format(value);
            _value=_value.replace(",", "");
            

            writer.println(t + "," + _open + "," + _high + "," + _low + "," + _close + "," + _value + "," + _numEv + "," + vol);

        /*System.out.println(d_dateFormat.format(time.calendar().getTime()) + "\t" +
        d_decimalFormat.format(open) + "\t\t" +
        d_decimalFormat.format(high) + "\t\t" +
        d_decimalFormat.format(low) + "\t\t" +
        d_decimalFormat.format(close) + "\t\t" +
        d_decimalFormat.format(numEvents) + "\t\t" +
        d_decimalFormat.format(volume));*/
        }
    }

    private void processResponseEvent(Event event, Session session) throws Exception {
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

    

    private boolean parseCommandLine(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("-s")) {
                d_security = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-e")) {
                d_eventType = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-ip")) {
                d_host = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-p")) {
                d_port = Integer.parseInt(args[i + 1]);
            } else if (args[i].equalsIgnoreCase("-b")) {
                d_barInterval = Integer.parseInt(args[i + 1]);
            } else if (args[i].equalsIgnoreCase("-g")) {
                d_gapFillInitialBar = true;
            } else if (args[i].equalsIgnoreCase("-sd")) {
                d_startDateTime = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-ed")) {
                d_endDateTime = args[i + 1];
            } else if (args[i].equalsIgnoreCase("-h")) {
                printUsage();
                return false;
            }
        }

        return true;
    }

    private void printErrorInfo(String leadingStr, Element errorInfo)
            throws Exception {
        System.out.println(leadingStr + errorInfo.getElementAsString(CATEGORY) +
                " (" + errorInfo.getElementAsString(MESSAGE) + ")");
    }

    private void printUsage() {
        System.out.println("Usage:");
        System.out.println("  Retrieve intraday bars");
        System.out.println("    [-s     <security   = IBM US Equity>");
        System.out.println("    [-e     <event      = TRADE>");
        System.out.println("    [-b     <barInterval= 60>");
        System.out.println("    [-sd    <startDateTime  = 2008-08-11T13:30:00>");
        System.out.println("    [-ed    <endDateTime    = 2008-08-12T13:30:00>");
        System.out.println("    [-g     <gapFillInitialBar = false>");
        System.out.println("    [-ip    <ipAddress  = localhost>");
        System.out.println("    [-p     <tcpPort    = 8194>");
        System.out.println("1) All times are in GMT.");
        System.out.println("2) Only one security can be specified.");
        System.out.println("3) Only one event can be specified.");
    }
}
