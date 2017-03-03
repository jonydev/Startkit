package com.thoughtworks.android.startkit.book;

import android.content.Context;
import android.util.Log;

import com.thoughtworks.android.startkit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import static android.util.Xml.Encoding.UTF_8;

public class DataLoader {

    private static final String TAG = "DataLoader";

    public static JSONObject loadJSONData(Context context) {
        JSONObject json = null;

        InputStream in = context.getResources().openRawResource(R.raw.data);

        try {
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            json = new JSONObject(new String(buffer, UTF_8.toString()));
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return json;
    }
}
