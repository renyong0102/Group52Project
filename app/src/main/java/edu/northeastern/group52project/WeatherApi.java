package edu.northeastern.group52project;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApi {
    private String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=37.41&longitude=-121.89&daily=weathercode,temperature_2m_max,temperature_2m_min,windgusts_10m_max&timezone=auto";
    private String jsonRes = null;
    private JSONObject jsonObj = null;
    private int dayLength;

    public WeatherApi(double lat, double lon) {
        apiUrl = String.format(apiUrl, lat, lon);
    }

    public int getDayLength() {
        return dayLength;
    }

    /**
     * @return A string contains the API url.
     */
    public String getUrl() {
        return apiUrl;
    }

    /**
     * This method converts the input raw String-formatted JSON response to a
     * JSON object and saves it within the class. The method also calculates
     * the length of the forecast days.
     *
     * @param res A raw String-format JSON response.
     */
    public void setJsonResAndObject(String res) {
        jsonRes = res;
        try {
            jsonObj = new JSONObject(res);
            dayLength = jsonObj.getJSONObject("daily")
                    .getJSONArray("time").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns a raw String-format JSON response.
     *
     * @return A Raw JSON String.
     */
    public String getJsonRes() {
        return jsonRes;
    }

    /**
     * This method returns the temperature unit of the json response.
     *
     * @return Temperature unit, either °C or °F.
     */
    public String getTemperatureUnit() {
        String result = null;
        try {
            result = jsonObj.getJSONObject("daily_units")
                    .getString("temperature_2m_max");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * This method returns a list of dates in String format.
     *
     * @return A list of dates in String format.
     */
    public String[] getListOfDates() {
        String[] result = null;

        try {
            JSONArray dates = jsonObj.getJSONObject("daily")
                    .getJSONArray("time");
            result = new String[dayLength];
            for (int i = 0; i < dayLength; i++)
                result[i] = dates.getString(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * This method returns a list of Weathercode.
     *
     * @return A list of Weathercode in integer.
     */
    public int[] getListOfWeathercode() {
        int[] result = null;

        try {
            JSONArray weathercodes = jsonObj.getJSONObject("daily")
                    .getJSONArray("weathercode");
            result = new int[dayLength];
            for (int i = 0; i < dayLength; i++)
                result[i] = weathercodes.getInt(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * This method returns a list of daily maximum temperatures.
     *
     * @return A list of daily maximum temperatures in integer.
     */
    public int[] getListOfMaxTemp() {
        int[] result = null;

        try {
            JSONArray maxTemps = jsonObj.getJSONObject("daily")
                    .getJSONArray("temperature_2m_max");
            result = new int[dayLength];
            for (int i = 0; i < dayLength; i++)
                result[i] = (int)Math.round(maxTemps.getDouble(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * This method returns a list of daily minimum temperatures.
     *
     * @return A list of daily minimum temperatures in integer.
     */
    public int[] getListOfMinTemp() {
        int[] result = null;

        try {
            JSONArray minTemps = jsonObj.getJSONObject("daily")
                    .getJSONArray("temperature_2m_min");
            result = new int[dayLength];
            for (int i = 0; i < dayLength; i++)
                result[i] = (int)Math.round(minTemps.getDouble(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int[] getListOfGusts() {
        int[] result = null;

        try {
            JSONArray minTemps = jsonObj.getJSONObject("daily")
                    .getJSONArray("windgusts_10m_max");
            result = new int[dayLength];
            for (int i = 0; i < dayLength; i++)
                result[i] = (int)Math.round(minTemps.getDouble(i));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
