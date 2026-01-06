package com.example.pocketmanager;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExpenseStorage {

    private static final String PREF_NAME = "expenses_pref";
    private static final String KEY_EXPENSES = "expenses";

    // =============================
    // AJOUTER
    // =============================
    public static void addExpense(Context context, Expense expense) {
        ArrayList<Expense> list = getExpenses(context);
        list.add(expense);
        saveExpenses(context, list);
    }

    // =============================
    // CHARGER
    // =============================
    public static ArrayList<Expense> loadExpenses(Context context) {
        return getExpenses(context);
    }

    public static ArrayList<Expense> getExpenses(Context context) {
        ArrayList<Expense> list = new ArrayList<>();

        try {
            SharedPreferences prefs =
                    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            String json = prefs.getString(KEY_EXPENSES, "[]");
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                list.add(new Expense(
                        obj.getString("title"),
                        obj.getDouble("amount"),
                        obj.optString("image", null)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // =============================
    // SUPPRIMER
    // =============================
    public static void deleteExpense(Context context, int index) {
        ArrayList<Expense> list = getExpenses(context);
        if (index >= 0 && index < list.size()) {
            list.remove(index);
            saveExpenses(context, list);
        }
    }

    // =============================
    // MODIFIER
    // =============================
    public static void updateExpense(Context context, int index, Expense expense) {
        ArrayList<Expense> list = getExpenses(context);
        if (index >= 0 && index < list.size()) {
            list.set(index, expense);
            saveExpenses(context, list);
        }
    }

    // =============================
    // INTERNE (PRIVÉ)
    // =============================
    private static void saveExpenses(Context context, ArrayList<Expense> list) {
        try {
            JSONArray array = new JSONArray();
            for (Expense e : list) {
                JSONObject obj = new JSONObject();
                obj.put("title", e.getTitle());
                obj.put("amount", e.getAmount());
                obj.put("image", e.getImageBase64());
                array.put(obj);
            }

            SharedPreferences prefs =
                    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

            prefs.edit()
                    .putString(KEY_EXPENSES, array.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
