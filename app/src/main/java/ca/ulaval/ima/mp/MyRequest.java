package ca.ulaval.ima.mp;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyRequest {
    static public String BASE_URL = "https://monapp-f1b0a.firebaseio.com";

    public MyResponse get(String url, JSONObject headers) throws ExecutionException, InterruptedException {
        GetMyRequestAsync getMyRequestAsync = new GetMyRequestAsync();

        getMyRequestAsync.url = BASE_URL + url;

        if (headers == null) {
            headers = new JSONObject();
        }

        return getMyRequestAsync.execute(headers).get();
    }

    public MyResponse post(String url, JSONObject data, String bodyType, JSONObject headers) throws ExecutionException, InterruptedException {
        PostJSONMyRequestAsync postJSONMyRequestAsync = new PostJSONMyRequestAsync();
        PostFormMyRequestAsync postFormMyRequestAsync = new PostFormMyRequestAsync();

        postJSONMyRequestAsync.url = BASE_URL + url;
        postFormMyRequestAsync.url = BASE_URL + url;

        if (headers == null) {
            headers = new JSONObject();
        }


        if (bodyType.equals("json")) {
           return postJSONMyRequestAsync.execute(data, headers).get();
        } else if (bodyType.equals("form")) {
            return postFormMyRequestAsync.execute(data, headers).get();
        }
        return null;
    }

    public class GetMyRequestAsync extends AsyncTask<JSONObject, Integer, MyResponse> {

        private String url = "";

        @Override
        protected MyResponse doInBackground(JSONObject... objects) {
            Request request;
            String bodyString;
            OkHttpClient client = new OkHttpClient();
            Map<String, String> headersMap = new HashMap<>();
            Iterator<String> keys = objects[0].keys();

            while(keys.hasNext()) {
                String key = keys.next();

                try {
                    headersMap.put(key, objects[0].get(key).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Headers headers = Headers.of(headersMap);

            request = new Request.Builder().url(this.url).headers(headers).build();

            if (request == null)
                return null;

            Response response = null;

            try {
                response = client.newCall(request).execute();
                bodyString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return new MyResponse(response.message(), response.code(), response.headers(), bodyString);
        }
    }

    public class PostJSONMyRequestAsync extends AsyncTask<JSONObject, Integer, MyResponse> {
        private String url = "";
        @Override
        protected MyResponse doInBackground(JSONObject... objects) {
            Request request;
            OkHttpClient client = new OkHttpClient();
            Map<String, String> headersMap = new HashMap<>();
            Iterator<String> keys = objects[1].keys();
            String bodyString;

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, objects[0].toString());

            while(keys.hasNext()) {
                String key = keys.next();

                try {
                    headersMap.put(key, objects[1].get(key).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Headers headers = Headers.of(headersMap);

            request = new Request.Builder().url(this.url).headers(headers).post(body).build();

            if (request == null)
                return null;

            Response response = null;

            try {
                response = client.newCall(request).execute();
                bodyString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return new MyResponse(response.message(), response.code(), response.headers(), bodyString);
        }
    }

    public class PostFormMyRequestAsync extends AsyncTask<JSONObject, Integer, MyResponse> {
        private String url = "";
        @Override
        protected MyResponse doInBackground(JSONObject... objects) {
            Request request;
            OkHttpClient client = new OkHttpClient();
            Map<String, String> headersMap = new HashMap<>();
            Iterator<String> keys = objects[0].keys();
            String bodyString;

            FormBody.Builder formBodyBuilder = new FormBody.Builder();

            while(keys.hasNext()) {
                String key = keys.next();

                try {
                    formBodyBuilder.add(key, objects[0].get(key).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            RequestBody body = formBodyBuilder.build();

            keys = objects[1].keys();

            while(keys.hasNext()) {
                String key = keys.next();

                try {
                    headersMap.put(key, objects[1].get(key).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Headers headers = Headers.of(headersMap);

            request = new Request.Builder().url(this.url).headers(headers).post(body).build();

            if (request == null)
                return null;

            Response response = null;

            try {
                response = client.newCall(request).execute();
                bodyString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return new MyResponse(response.message(), response.code(), response.headers(), bodyString);
        }
    }
}
