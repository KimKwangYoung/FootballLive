package com.example.footballlive;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.footballlive.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FootballLiveApplication extends Application {
    private static final String TAG = "Application";

    static RequestQueue requestQueue;

    public static final String MEMBER_NOTI_CHANNEL_ID = "notiMember"; //멤버 관련 알림 채널 id
    public static final String MATCH_NOTI_CHANNEL_ID = "notiMatch"; //매치 관련 알림 채널 id
    public static final String APP_NOTI_CHANNEL_ID = "notiApp"; //앱 공지사항 알림 채널 id

    public static final String MEMBER_NOTI_CHANNEL_NAME = "회원관련알림"; //멤버 관련 알림 채널이름
    public static final String MATCH_NOTI_CHANNEL_NAME = "매치관련알림"; //매치 관련 알림 채널이름
    public static final String APP_NOTI_CHANNEL_NAME = "앱공지사항"; //앱 공지사항 알림 채널 이름
    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel memberChannel;
            memberChannel = new NotificationChannel(MEMBER_NOTI_CHANNEL_ID, MEMBER_NOTI_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(memberChannel);

            NotificationChannel matchChannel;
            matchChannel = new NotificationChannel(MATCH_NOTI_CHANNEL_ID, MATCH_NOTI_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(matchChannel);

            NotificationChannel appChannel;
            appChannel = new NotificationChannel(APP_NOTI_CHANNEL_ID,APP_NOTI_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(appChannel);
        }

    }

    public void sendMessage(Context context, String title, String contents, ArrayList<String> tokens, String channelID){
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("priority", "high");

            JSONObject dataObj = new JSONObject();

            dataObj.put("contents", contents);
            dataObj.put("title", title);
            dataObj.put("channelId", channelID);
            requestData.put("data", dataObj);
            JSONArray idArray = new JSONArray();

            int i = 0;
            for(String s : tokens){
                idArray.put(i, s);
                i += 1;
                Log.e(TAG, s + " is user token");
            }

            requestData.put("registration_ids", idArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendData(requestData, new SendResponseListener() {

            @Override
            public void onRequestCompleted() {
                Log.e(TAG,"onRequestCompleted() 호출됨.");
            }

            @Override
            public void onRequestStarted() {
                Log.e(TAG,"onRequestStarted() 호출됨.");
            }

            @Override
            public void onRequestWithError(VolleyError error) {
                Log.e(TAG, "onRequestWithError() 호출됨.");
            }
        });
    }
    public interface SendResponseListener {
        void onRequestStarted();
        void onRequestCompleted();
        void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(

                Request.Method.POST, "https://fcm.googleapis.com/fcm/send", requestData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onRequestCompleted();
                Log.e(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String key = "key="+BaseActivity.fcmSeverKey;
                headers.put("Authorization", key);
                Log.e(TAG, "fcm server key : " + key);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        requestQueue.add(request);
    }
}
