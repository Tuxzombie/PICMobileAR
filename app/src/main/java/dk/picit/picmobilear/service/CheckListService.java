package dk.picit.picmobilear.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class CheckListService extends AsyncTask<Void, Void, Void> {

    private UserAndContainerNr userAndContainerNr = new UserAndContainerNr();

    public void sendRequest(){
        try {
//            Log.d(TAG, "sendRequest:" + userAndContainerNr.getHtmlString());
            URL url = new URL(userAndContainerNr.getHtmlString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                readStream(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    private void readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (line != null) {
            Log.d(TAG, "readStream: " + line);
            line = reader.readLine();
        }
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

    @Override
    protected Void doInBackground(Void... voids) {

        sendRequest();

        return null;
    }

    private class UserAndContainerNr{
        String username;
        String password;
        String containerNr;

        String getHtmlString(){
         return "http://mobile.picit.dk/html/GetPost?psi=42.udv.webquery.test&NetUserId=" + username + "&SignOnCode=" + password + "&OpCode=echo2&Eqpid=" + containerNr + "&terminal=Q&";
        }
    }
}
