package com.example.pocketmanager;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExpenseStorage {

    private static final String PREF_NAME = "expenses_pref";
    private static final String KEY_EXPENSES = "expenses";

    // SAVE
    public static void saveExpenses(Context context, ArrayList<Expense> expenses) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray array = new JSONArray();

        try {
            for (Expense e : expenses) {
                JSONObject obj = new JSONObject();
                obj.put("title", e.getTitle());
                obj.put("amount", e.getAmount());
                obj.put("image", e.getImagePath());
                array.put(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        editor.putString(KEY_EXPENSES, array.toString());
        editor.apply();
    }

    // LOAD
    public static ArrayList<Expense> loadExpenses(Context context) {
        ArrayList<Expense> list = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_EXPENSES, null);

        if (json == null) return list;

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                list.add(new Expense(
                        obj.getString("title"),
                        obj.getDouble("amount"),
                        obj.getString("image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
