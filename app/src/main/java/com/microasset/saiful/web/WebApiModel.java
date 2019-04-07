/**
 * @author Mohammad Saiful Alam
 * WebApiModel model
 */

package com.microasset.saiful.web;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.ResponseObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebApiModel extends BaseModel {
    public WebApiModel(Context context) {
        super(context);
    }

    class ApiReq extends StringRequest{

        public ApiReq(int method, String url, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }
    }



    @Override
    public ResponseObject execute() {
        ResponseObject resObj = new ResponseObject();
        return resObj;
    }

    public void loadVideoInfo( final TextView tvTitle, final TextView tvDesc, String keyYouTubeVideo, String API_KEY){
        // volley
        ApiReq stringRequest = new ApiReq(
                Request.Method.GET,
                "https://www.googleapis.com/youtube/v3/videos?id=" + keyYouTubeVideo + "&key=" +
                        API_KEY +
                        "&part=snippet,contentDetails,statistics,status",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("items");
                            JSONObject object = jsonArray.getJSONObject(0);
                            JSONObject snippet = object.getJSONObject("snippet");
                            //TODO NOT SURE WHETHER THIS TV VALID...
                            String title = snippet.getString("title");
                            tvTitle.setText(title);
                            String description = snippet.getString("description");
                            tvDesc.setText(description);
                            Log.d("stuff: ", "" + title);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){};

        // Request (if not using Singleton [RequestHandler]
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
        // Request with RequestHandler (Singleton: if created)
        //RequestHandler.getInstance(MainActivity.activity_main).addToRequestQueue(stringRequest);
    }
}
