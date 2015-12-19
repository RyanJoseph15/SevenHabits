package com.minisoftwareandgames.ryan.sevenhabits;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.minisoftwareandgames.ryan.sevenhabits.Objects.QuadrantDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ryan on 12/18/15.
 */
public class Utilities extends Application {

    public static final String TITLES = "Titles";
    public static final String TASKS = "Tasks";
    public static final String SEVENHABITS = "SevenHabits";
    public static String QUAD1 = "Important and Urgent";
    public static String QUAD2 = "Important and Not Urgent";
    public static String QUAD3 = "Not Important and Urgent";
    public static String QUAD4 = "Not Important and Not Urgent";

    public enum QUADRANT {
        ONE, TWO, THREE, FOUR, ALL
    }

    /* ------------------------------------------------------------------------------------ /
     *                                  Title Parsing
     * ----------------------------------------------------------------------------------- */

    public static boolean addElements(SharedPreferences sharedPreferences, ArrayList<String> elements, String tag) {
        if (elements != null) {
            for (String element : elements) {
                addElement(sharedPreferences, element, tag);
            }
            return true;
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonObject.put(tag, jsonArray);
                sharedPreferences.edit().putString(tag, jsonObject.toString()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static boolean addElement(SharedPreferences sharedPreferences, String element, String tag) {
        // convert to arraylist
        ArrayList<String> elements = getElements(sharedPreferences, tag);
        if (elements == null) {
            elements = new ArrayList<String>();
        }
        elements.add(element);
        // convert to json
        String jsonObjectString = Strings2JSON(elements, tag);
        Log.d("CHECK", jsonObjectString);
        // add to preferences
        if (jsonObjectString != null) {
            sharedPreferences.edit().putString(tag, jsonObjectString).apply();
            return true;
        } else return false;
    }

    public static ArrayList<String> getElements(SharedPreferences sharedPreferences, String tag) {
        Log.d("CHECK", "tag: " + tag);
        String JSONTitles = sharedPreferences.getString(tag, null);
        if (JSONTitles != null)
            Log.d("CHECK", "JSONTitles: " + JSONTitles);
        else Log.d("CHECK", "JSONTitles is null");
        return JSONTitles != null? String2Array(JSONTitles, tag) : null;
    }


    public static String getElement(SharedPreferences sharedPreferences, int position, String tag) {
        return getElements(sharedPreferences, tag).get(position);
    }

    /* ------------------------------------------------------------------------------------ /
     *                                   Conversions
     * ----------------------------------------------------------------------------------- */

    public static ArrayList<String> String2Array(String jsonStrings, String tag) {
        ArrayList<String> strings = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStrings);         // gets the overall object
            JSONArray jsonArray = jsonObject.getJSONArray(tag);         // grabs the array
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                strings.add(jsonArray.getString(i));                     // array only strings so we
                // grab it from the array
            }
            return strings;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String Strings2JSON(ArrayList<String> strings, String tag) {
        String result = null;
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = null;
            if (strings != null) jsonArray = new JSONArray(strings);
            else jsonArray = new JSONArray();
            jsonObject.put(tag, jsonArray);
            result = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<String> QuadrantDetails2StringsArray(ArrayList<QuadrantDetail> quadrantDetails) {
        ArrayList<String> details = new ArrayList<String>();
        if (quadrantDetails != null)
            for (QuadrantDetail detail : quadrantDetails) {
                details.add(detail.getDetails());
            }
        return details;
    }

    public static QUADRANT q2Q(int quad) {
        /* int -> Utilities.QUADRANT */
        if (quad == 1) return QUADRANT.ONE;
        else if (quad == 2) return QUADRANT.TWO;
        else if (quad == 3) return QUADRANT.THREE;
        else if (quad == 4) return QUADRANT.FOUR;
        else return QUADRANT.ALL;
    }

    public static String quad2Title(int quad) {
        switch (quad) {
            case 1:
                return QUAD1;
            case 2:
                return QUAD2;
            case 3:
                return QUAD3;
            case 4:
                return QUAD4;
            default:
                return null;
        }
    }

}
