package dk.picit.picmobilear.service;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class CheckListService {

    private UserAndContainerNr userAndContainerNr = new UserAndContainerNr();
    private InputStream in;

    public void sendRequest(){
        HtmlRequest htmlRequest = new HtmlRequest();
        htmlRequest.execute();

    }

    private void readStream(InputStream inputStream) throws IOException {

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(in, null);
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT){
                String name = parser.getName();
                if (XmlPullParser.START_TAG == event || XmlPullParser.END_TAG == event)
                    Log.d(TAG, "readStream: " + name);

                if (XmlPullParser.TEXT == event){
                    Log.d(TAG, "readStream: " + parser.getText());
                }

                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }


//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        String line = reader.readLine();
//        while (line != null) {
//            line = reader.readLine();
//        }
    }

    public void setUsername(String username){
        userAndContainerNr.username = username;
    }

    public void setPassword(String password){
        userAndContainerNr.password = password;
    }

    public void setContainerNr(String containerNr){
        userAndContainerNr.containerNr = containerNr;
    }


    private class UserAndContainerNr{
        String username;
        String password;
        String containerNr;

        String getHtmlString(){
         return "http://mobile.picit.dk/html/GetPost?psi=42.udv.webquery.test&NetUserId=" + username + "&SignOnCode=" + password + "&OpCode=devtest&Eqpid=" + containerNr + "&terminal=Q&";
        }
    }

    private class HtmlRequest extends AsyncTask<Void, Void, Void>  {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
            Log.d(TAG, "sendRequest:" + userAndContainerNr.getHtmlString());
                URL url = new URL(userAndContainerNr.getHtmlString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } finally {
                    urlConnection.disconnect();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
//            HttpURLConnection urlConnection = null;
//            try {
//                URL url = new URL("http://mobile.picit.dk/html/GetPost?");
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setRequestProperty("psi","42.udv.webquery.test");
//                urlConnection.setRequestProperty("NetUserId", "");
//                urlConnection.setRequestProperty("SignOnCode", "");
//                urlConnection.setRequestProperty("OpCode", "devtest");
//                urlConnection.setRequestProperty("eqpid", "IVAN1234567");
//                urlConnection.setRequestProperty("terminal", "Q");
//                urlConnection.setDoOutput(true);
//
//                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
//
//                outputStream.flush();
//                outputStream.close();
//
//                int responseCode = urlConnection.getResponseCode();
//
//                if(responseCode == HttpURLConnection.HTTP_OK){
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    String line = reader.readLine();
//                    while (line != null){
//                        Log.d(TAG, "doInBackground: " + line);
//                        line = reader.readLine();
//                    }
//                    reader.close();
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                readStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
