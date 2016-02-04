package com.mattandmikeandscott.richpersonleaderboard.network;

import android.content.res.Resources;
import android.util.Log;

import com.mattandmikeandscott.richpersonleaderboard.R;
import com.mattandmikeandscott.richpersonleaderboard.domain.Achievement;
import com.mattandmikeandscott.richpersonleaderboard.domain.Payment;
import com.mattandmikeandscott.richpersonleaderboard.domain.PeopleQueryType;
import com.mattandmikeandscott.richpersonleaderboard.domain.Person;
import com.mattandmikeandscott.richpersonleaderboard.domain.Rank;
import com.mattandmikeandscott.richpersonleaderboard.domain.RankType;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class Repository {
    private Resources resources;
    private String endpoint = "http://richpersonleaderboardserver.azurewebsites.net";

    public Repository(Resources resources) {
        this.resources = resources;
    }

    public ArrayList<Person> getPeople(PeopleQueryType peopleQueryType, Map<String, Integer> parameters) {
        if(peopleQueryType == PeopleQueryType.Persons) {
            endpoint += "/api/persons";
        } else if(peopleQueryType == PeopleQueryType.Myself) {
            endpoint += "/api/GetPersonAndSurroundingPeople";
        } else if(peopleQueryType == PeopleQueryType.Person) {
            endpoint += "/api/person";
        } else {
            throw new IllegalArgumentException("Invalid endpoint for getPeople()!");
        }

        ArrayList<Person> people = new ArrayList<>();

        JSONArray peopleData = doGetRequest(endpoint, paramListToString(parameters));

        try {
            for(int i = 0; i < peopleData.length(); i++) {
                JSONObject jsonObject = peopleData.getJSONObject(i);

                JSONArray jRankings = null;
                JSONArray jAchievements = null;
                JSONArray jPayments = null;
                ArrayList<Rank> rankings = new ArrayList<>();
                ArrayList<Payment> payments = new ArrayList<>();
                ArrayList<Achievement> achievements = new ArrayList<>();

                if(!jsonObject.getString("Rankings").equals("null")) {
                    jRankings = jsonObject.getJSONArray("Rankings");

                    for(int j = 0; j < jRankings.length(); j++) {
                        int rankTypeInt = jRankings.getJSONObject(j).getInt("RankType");
                        RankType rankType = RankType.getRankType(rankTypeInt);

                        rankings.add(new Rank(rankType, jRankings.getJSONObject(j).getInt("Rank"), jRankings.getJSONObject(j).getDouble("Wealth")));
                    }
                }

                if(!jsonObject.getString("Payments").equals("null")) {
                    jPayments = jsonObject.getJSONArray("Payments");

                    for(int j = 0; j < jPayments.length(); j++) {
                        String dateString = jPayments.getJSONObject(j).getString("InsertDate");
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(Long.parseLong(dateString.substring(6, dateString.length() - 4)));
                        payments.add(new Payment(jPayments.getJSONObject(j).getInt("PaymentId"), jPayments.getJSONObject(j).getDouble("Amount"), cal.getTime()));
                    }
                }

                if(!jsonObject.getString("Achievements").equals("null")) {
                    jAchievements = jsonObject.getJSONArray("Achievements");

                    for(int j = 0; j < jAchievements.length(); j++) {
                        achievements.add(new Achievement(jAchievements.getJSONObject(j).getInt("AchievementType"), jAchievements.getJSONObject(j).getString("Name"), jAchievements.getJSONObject(j).getString("Description")));
                    }
                }

                people.add(new Person(jsonObject.getInt("PersonId"), jsonObject.getString("Name"), jsonObject.getInt("Rank"), jsonObject.getDouble("Wealth"), rankings, payments, achievements));
            }
        } catch(JSONException e) {
            Log.e(resources.getString(R.string.app_short_name), "Error parsing JSON data " + e.toString());
        }

        return people;
    }

    private JSONArray doGetRequest(String endpoint, String parameters) {
        JSONArray jArray = new JSONArray();

        try {
            HttpGet httpGet = new HttpGet(endpoint + "?" + parameters);

            jArray = doRequest(httpGet);
        } catch(Exception e) {
            Log.e(resources.getString(R.string.app_short_name), "Error creating post request " + e.toString());
        }

        return jArray;
    }

    private JSONArray doPostRequest(String endpoint, ArrayList<NameValuePair> nameValuePairList) {
        JSONArray jArray = new JSONArray();

        try {
            HttpPost httpPost = new HttpPost(endpoint);
            httpPost.addHeader("User-Agent", "User-Agent");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, "UTF-8"));

            jArray = doRequest(httpPost);
        } catch(Exception e) {
            Log.e(resources.getString(R.string.app_short_name), "Error creating post request " + e.toString());
        }

        return jArray;
    }

    private JSONArray doRequest(HttpRequestBase request) {
        CloseableHttpClient client = HttpClients.createDefault();
        JSONArray jArray = new JSONArray();

        try {
            CloseableHttpResponse response = client.execute(request);

            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();

            String result = "";
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                is.close();

                result = sb.toString();
            } catch(Exception e) {
                Log.e(resources.getString(R.string.app_short_name), "Error converting result "+e.toString());
            }

            try {
                jArray = new JSONArray(result);
            } catch(JSONException e) {
                Log.e(resources.getString(R.string.app_short_name), "Error parsing JSON data " + e.toString());
            }
        } catch(Exception e) {

        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jArray;
    }

    private String paramListToString(Map<String, Integer> parameters) {
        String listAsString = "";

        for(String parameter : parameters.keySet()) {
            listAsString += parameter + "=" + parameters.get(parameter) + "&"; // a trailing & is ok
        }

        return listAsString;
    }
}
