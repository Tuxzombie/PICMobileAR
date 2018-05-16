package dk.picit.picmobilear.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class VisionService {

    private URL url;
    private HttpURLConnection connection;

    public VisionService(String img) {
//        try {
//            url = new URL("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyD7ndaAYBc_Um4T45dmweJ7GYrqGnxkYNA");
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.addRequestProperty("Accept", "application/json");
//            connection.addRequestProperty("Content-Type", "application/json");
//            connection.setDoInput(true);
//            connection.setDoOutput(true);

            Log.d("Ser her!", createJSON(img));

//            OutputStream os = connection.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(createJSON(img));
//            writer.flush();
//            writer.close();
//            os.close();
//            connection.connect();
//
//            connection.getResponseCode();
//            connection.getResponseMessage();

//        } catch (java.io.IOException e) {
//            e.printStackTrace();
//        }

    }

    public String createJSON(String img)
    {
        JSONObject outerJ = new JSONObject();
        try {
            JSONArray reqJ = new JSONArray("requests");

            JSONObject imgJ = new JSONObject("image");
            imgJ.put("content", img);
            reqJ.put(imgJ);

            JSONArray featJ = new JSONArray("features");
            JSONObject typeJ = new JSONObject();
            typeJ.put("type", "TEXT_DETECTION");
            featJ.put(typeJ);

            outerJ.put("aoColumnDefs", (Object)reqJ);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return outerJ.toString();
    }
}
