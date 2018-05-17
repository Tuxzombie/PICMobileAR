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
        try {
            url = new URL("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyD7ndaAYBc_Um4T45dmweJ7GYrqGnxkYNA");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(createJSONPOST(img[0]));
            writer.flush();
            writer.close();
            os.close();
            connection.connect();

            responseCode = connection.getResponseCode();
            msg = streamToString(connection.getInputStream());

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String s) {
        String ocrResult = "";
        try {
            JSONObject jo = stringToJSON(s);
            ocrResult = jo.getJSONArray("responses").getJSONObject(0).getJSONArray("textAnnotations").getJSONObject(0).getString("description");
        }
        catch (JSONException e)
        {

        }
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
            e.printStackTrace();
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
            e.printStackTrace();
        }


        return result;
    }

    private String getTestImg()
    {
        return "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";
    }
}
