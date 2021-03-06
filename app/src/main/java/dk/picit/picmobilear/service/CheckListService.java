package dk.picit.picmobilear.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CheckListService {

    private UserAndContainerNr userAndContainerNr = new UserAndContainerNr();
    private List<String> service = new ArrayList<>();
    private Map<String, String> information = new HashMap<>();
    private Context context;

    public CheckListService(Context context) {
        this.context = context;
    }

    /**
     * send request to server
     */
    public void sendRequest() {
        HtmlRequest htmlRequest = new HtmlRequest();
        htmlRequest.execute(userAndContainerNr.getHtmlString());
    }

    /**
     * find services in string, and add them to a list.
     * @param serviceString string with services
     */
    private void saveServiceString(String serviceString) {
        String[] strings = serviceString.split("\\|");
        for (int i = 0; i < strings.length; i++) {
            String[] service = strings[i].split(":");
            this.service.add(service[1]);
        }
    }

    /**
     * Find and save the information about the container, and the check list.
     *
     * @param inputStream
     * @throws IOException
     */
    private void readStream(InputStream inputStream) throws IOException {

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inputStream, null);
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                // UpdateMessage contains information about the container
                if (XmlPullParser.START_TAG == event && name.equals("UpdateMessage")) {
                    event = parser.next();
                    name = parser.getName();
                    while (XmlPullParser.END_TAG != event || !name.equals("UpdateMessage")) {
                        Log.d(TAG, "readStream: " + name);
                        // if service, save as service
                        if (XmlPullParser.START_TAG == event && name.equals("Services")) {
                            event = parser.next();
                            if (XmlPullParser.END_TAG != event) {
                                saveServiceString(parser.getText());
                            }
                            // if not service, save as information about container
                        } else if (XmlPullParser.START_TAG == event) {
                            event = parser.next();
                            if (XmlPullParser.END_TAG != event) {
                                information.put(name, parser.getText());
                            }
                        }
                        event = parser.next();
                        name = parser.getName();
                    }
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
    }

    public void setUsername(String username) {
        userAndContainerNr.username = username;
    }

    public void setPassword(String password) {
        userAndContainerNr.password = password;
    }

    public void setContainerNr(String containerNr) {
        userAndContainerNr.containerNr = containerNr;
    }

    public Map<String, String> getInformation() {
        return information;
    }

    public List<String> getService() {
        return service;
    }

    /**
     * holds user information and container number, for the http connection
     */
    private class UserAndContainerNr {
        String username;
        String password;
        String containerNr;

        /**
         *
         * @return String for the http connection
         */
        String getHtmlString() {
            return "http://mobile.picit.dk/html/GetPost?psi=42.udv.webquery.test&NetUserId=" +
                   username + "&SignOnCode=" + password + "&OpCode=devtest&Eqpid=" + containerNr +
                   "&terminal=Q&";
        }
    }

    private class HtmlRequest extends AsyncTask<String, Void, InputStream> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service.clear();
            information.clear();
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                Log.d(TAG, "sendRequest:" + strings[0]);
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                try {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (java.io.IOException e) {
                Toast toast = Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG);
                toast.show();
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            try {
                readStream(inputStream);
                Intent in = new Intent("CheckListReady");
                context.sendBroadcast(in);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
