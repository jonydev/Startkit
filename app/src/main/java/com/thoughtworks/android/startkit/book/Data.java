package com.thoughtworks.android.startkit.book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private static final String TAG = "Data";

    private static final String COUNT = "count";
    private static final String START = "start";
    private static final String TOTAL = "total";
    private static final String BOOKS = "books";

    private final JSONObject mJSONObject;

    public Data(JSONObject jsonObject) {
        this.mJSONObject = jsonObject;
    }

    public int getCount() {
        return mJSONObject.optInt(COUNT);
    }

    public int getStart() {
        return mJSONObject.optInt(START);
    }

    public int getTotal() {
        return mJSONObject.optInt(TOTAL);
    }

    public List<Book> getBookArray() {
        JSONArray array = mJSONObject.optJSONArray(BOOKS);
        List<Book> books = new ArrayList<>(array.length());

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = (JSONObject) array.opt(i);
            books.add(new Book(object.optString("title"), object.optJSONObject("images").optString("large"), object.optJSONArray("author").toString(), object.optString("publisher"), object.optString("pubdate"), object.optString("summary"), object.optJSONObject("rating").optDouble("average")));
        }

        return books;
    }

    public static Data from(JSONObject jsonObject) {
        return new Data(jsonObject);
    }

    @Override
    public String toString() {
        return mJSONObject.toString();
    }
}
