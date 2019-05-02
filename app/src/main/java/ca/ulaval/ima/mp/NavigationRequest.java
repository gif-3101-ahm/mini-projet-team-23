package ca.ulaval.ima.mp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NavigationRequest {
    private MyRequest myRequest = new MyRequest();
    JSONArray getHistory() throws ExecutionException, InterruptedException, JSONException {
        JSONObject jsonData = null;

        MyResponse myResponse = myRequest.get("/history/.json?auth=" + AuthentificationRequest.ACCESS_TOKEN, null);

        if (myResponse.getBody() != null) {
            JSONArray jsonArray = new JSONArray();
            JSONArray sortedJsonArray = new JSONArray();

            jsonData = new JSONObject(myResponse.getBody());
            Iterator<String> temp = jsonData.keys();

            while (temp.hasNext()) {
                String key = temp.next();
                JSONObject line = jsonData.getJSONObject(key);
                if (line.getString("users_id").equals(AuthentificationRequest.USER_ID)) {
                    jsonArray.put(line);
                }
            }
            List<JSONObject> jsonValues = new ArrayList<JSONObject>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonValues.add(jsonArray.getJSONObject(i));
            }


            Collections.sort( jsonValues, new Comparator<JSONObject>() {
                //You can change "Name" with "ID" if you want to sort by ID
                private static final String KEY_NAME = "created_at";

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    Double valA = new Double(0);
                    Double valB = new Double(0);

                    try {
                        valA = a.getDouble(KEY_NAME);
                        valB = b.getDouble(KEY_NAME);
                    }
                    catch (JSONException e) {
                    }

                    return -valA.compareTo(valB);
                }
            });

            for (int i = 0; i < jsonArray.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }

            return (sortedJsonArray);
        }
        return null;
    }

    JSONObject postHistory(JSONObject data) throws ExecutionException, InterruptedException, JSONException {
        return new JSONObject(myRequest.post("/history.json?auth=" + AuthentificationRequest.ACCESS_TOKEN, data, "json", null).getBody());
    }
}
