package ca.ulaval.ima.mp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;


class AuthentificationRequest {
    private MyRequest myRequest = new MyRequest();
    private String API_KEY = "AIzaSyDWcvrvuALLrdOssn_Z6ATEJ6N0wg6Naps";

    static String ACCESS_TOKEN;
    static String USER_ID;
    private static String REFRESH_TOKEN;

    boolean refreshToken() throws ExecutionException, InterruptedException, JSONException {

        MyRequest.BASE_URL = "";

        MyResponse response = myRequest.post("https://securetoken.googleapis.com/v1/token?key=" + API_KEY, new JSONObject()
                .put("grant_type", "refresh_token")
                .put("refresh_token", AuthentificationRequest.REFRESH_TOKEN),
                "form", null);

        MyRequest.BASE_URL = "https://monapp-f1b0a.firebaseio.com";

        JSONObject jsonResponse = new JSONObject(response.getBody());

        if (response.getCode() == 200) {
            AuthentificationRequest.ACCESS_TOKEN = jsonResponse.get("idToken").toString();
            AuthentificationRequest.REFRESH_TOKEN = jsonResponse.get("refreshToken").toString();
        }
        return response.getCode() == 200;
    }

    boolean postSignIn(JSONObject data) throws ExecutionException, InterruptedException, JSONException {

        MyRequest.BASE_URL = "";

        MyResponse response = myRequest.post("https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=" + API_KEY, data, "json", null);

        MyRequest.BASE_URL = "https://monapp-f1b0a.firebaseio.com";

        JSONObject jsonResponse = new JSONObject(response.getBody());

        if (response.getCode() == 200) {
            AuthentificationRequest.ACCESS_TOKEN = jsonResponse.get("idToken").toString();
            AuthentificationRequest.REFRESH_TOKEN = jsonResponse.get("refreshToken").toString();

            Log.d("response", ACCESS_TOKEN);

            jsonResponse = new JSONObject(myRequest.get("/users/.json?auth=" + AuthentificationRequest.ACCESS_TOKEN, null).getBody());
            Iterator<String> temp = jsonResponse.keys();

            while (temp.hasNext()) {
                String key = temp.next();
                JSONObject line = jsonResponse.getJSONObject(key);
                if (data.getString("email").equals(line.getString("email"))) {
                    AuthentificationRequest.USER_ID = key;
                }

            }

        }

        return response.getCode() == 200;
    }

    boolean postSignUp(JSONObject data) throws ExecutionException, InterruptedException, JSONException {
        MyRequest.BASE_URL = "";

        MyResponse response = myRequest.post("https://www.googleapis.com/identitytoolkit/v3/relyingparty/signupNewUser?key=" + API_KEY, data, "json", null);

        MyRequest.BASE_URL = "https://monapp-f1b0a.firebaseio.com";

        JSONObject jsonResponse = new JSONObject(response.getBody());

        if (response.getCode() == 200) {
            AuthentificationRequest.ACCESS_TOKEN = jsonResponse.get("idToken").toString();
            AuthentificationRequest.REFRESH_TOKEN = jsonResponse.get("refreshToken").toString();

            response = myRequest.post("/users/.json?auth=" + AuthentificationRequest.ACCESS_TOKEN, new JSONObject()
                    .put("email", data.get("email"))
                    .put("firstname", data.get("firstname"))
                    .put("lastname", data.get("lastname")),
                    "json", null);
        }

        return response.getCode() == 200;
    }
}
