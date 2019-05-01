package ca.ulaval.ima.mp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ProfilRequest {

    private MyRequest myRequest = new MyRequest();

    JSONObject getUserProfile() throws ExecutionException, InterruptedException, JSONException {
        return new JSONObject(myRequest.get("/users/" + AuthentificationRequest.USER_ID + "/.json?auth=" + AuthentificationRequest.ACCESS_TOKEN, null).getBody());
    }
    JSONObject putUserProfile(JSONObject data) throws ExecutionException, InterruptedException, JSONException {
        return new JSONObject(myRequest.put("/users/" + AuthentificationRequest.USER_ID + "/.json?auth=" + AuthentificationRequest.ACCESS_TOKEN, data, "json", null).getBody());
    }
}
