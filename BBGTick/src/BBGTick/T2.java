/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BBGTick;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bloomberglp.blpapi.CorrelationID;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Session;
import com.bloomberglp.blpapi.SessionOptions;
import com.bloomberglp.blpapi.Subscription;
import com.bloomberglp.blpapi.SubscriptionList;

public class T2 {
    private Session             d_session;
    private SessionOptions      d_sessionOptions;
    private ArrayList<String>   d_securityList;
    private GridWindow          d_gridWindow;

    public class GridWindow {
        private String            d_name;
        private List<String> d_securityList;

        public GridWindow(String name, List<String> securityList) {
            d_name = name;
            d_securityList = securityList;
        }

        public void processSecurityUpdate(Message msg, long row) {
        	String topic = d_securityList.get((int)row);
            System.out.println(d_name + ": row " + row +
            					" got update for " + topic);
        }
    }

    public T2() {
        d_sessionOptions = new SessionOptions();
        d_sessionOptions.setServerHost("localhost");
        d_sessionOptions.setServerPort(8194);

        d_securityList = new ArrayList<String>();
        d_securityList.add("IBM US Equity");
        d_securityList.add("VOD LN Equity");

        d_gridWindow = new GridWindow("SecurityInfo", d_securityList);
    }

    private boolean createSession() throws Exception {
        System.out.println("Connecting to " + d_sessionOptions.getServerHost()
                            + ":" + d_sessionOptions.getServerPort());
        d_session = new Session(d_sessionOptions);
        if (!d_session.start()) {
            System.err.println("Failed to connect!");
            return false;
        }
        if (!d_session.openService("//blp/mktdata")) {
            System.err.println("Failed to open //blp/mktdata");
            return false;
        }
        return true;
    }

    private void run(String[] args) throws Exception {
        if (!createSession()) return;

        SubscriptionList subscriptionList = new SubscriptionList();
        for (int i = 0; i < d_securityList.size(); ++i) {
            subscriptionList.add(new Subscription(d_securityList.get(i),
                              "LAST_PRICE", new CorrelationID(i)));
        }
        d_session.subscribe(subscriptionList);

        while (true) {
            Event event = d_session.nextEvent();
            MessageIterator msgIter = event.messageIterator();
            while (msgIter.hasNext()) {
                Message msg = msgIter.next();
                if (event.eventType() == Event.EventType.SUBSCRIPTION_DATA) {
                    long row = msg.correlationID().value();
                    d_gridWindow.processSecurityUpdate(msg, row);
                }
            }
        }
    }

    public static void main(String[] args) {
    	System.out.println("SubscriptionCorrelationExample");
    	T2 example = new T2();
    	try {
    		example.run(args);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	System.out.println("Press ENTER to quit");
    	try {
    		System.in.read();
    	} catch (IOException e) {
    	}
    }

}
