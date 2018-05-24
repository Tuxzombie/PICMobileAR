package dk.picit.picmobilear.service;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import static android.content.ContentValues.TAG;

public class VisionService extends AsyncTask<String, Void, String> {

    private URL url;
    private HttpURLConnection connection;
    private final Context mContext;
    private int responseCode;
    private String msg;

    public VisionService(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... img) {
        msg = "";
        if(img != null) {
            try {
                url = new URL("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyD7ndaAYBc_Um4T45dmweJ7GYrqGnxkYNA");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.addRequestProperty("Accept", "application/json");
                connection.addRequestProperty("Content-Type", "application/json");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(createJSONPOST(img[0]));
                writer.flush();
                writer.close();
                os.close();
                connection.connect();

                responseCode = connection.getResponseCode();
                msg = streamToString(connection.getInputStream());
                Log.d(TAG, "--doInBack--"+msg);

            } catch (java.io.IOException e) {
                return null;
            }
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String s) {
        String ocrResult = "No Internet Connection";
        if(s != null) {
            try {
                JSONObject jo = stringToJSON(s);
                ocrResult = jo.getJSONArray("responses").getJSONObject(0).getJSONArray("textAnnotations").getJSONObject(0).getString("description");
                ocrResult = ParserService.visionToISO6346(ocrResult);
            } catch (JSONException e) {
                ocrResult = "No Text Found";
            }
        }
        Log.d(TAG, "--onPostexc s--"+s);
        Log.d(TAG, "--onPostexc ocrResult--"+ocrResult);
        Intent in = new Intent("OCR");
        in.putExtra("ocrResult", ocrResult);
        mContext.sendBroadcast(in);
    }

    public String createJSONPOST(String img)
    {
        JSONObject outerJ = new JSONObject();
        try {
            JSONArray reqJ = new JSONArray();

            JSONObject reqJinner = new JSONObject();

            JSONObject imgJ = new JSONObject();
            imgJ.put("content", img);
            reqJinner.put("image", imgJ);

            JSONArray featJ = new JSONArray();
            JSONObject typeJ = new JSONObject();
            typeJ.put("type", "TEXT_DETECTION");
            featJ.put(typeJ);
            reqJinner.put("features", featJ);

            reqJ.put(reqJinner);
            outerJ.put("requests", reqJ);

        } catch (JSONException e) {

        }

        return outerJ.toString();
    }

    private String streamToString(InputStream is)
    {
        String result = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e)
        {

        }
        result = sb.toString();
        return result;
    }

    private JSONObject stringToJSON(String s)
    {
        JSONObject result = null;
        try {
            result = new JSONObject(s);
            JSONArray responsesJ = result.getJSONArray("responses");
            JSONArray textJ = result.getJSONArray("textAnnotations");
            for (int i = 0; i < textJ.length(); i++) {
                JSONObject tmpJ = textJ.getJSONObject(i);
            }

        } catch (JSONException e) {

        }


        return result;
    }
}
