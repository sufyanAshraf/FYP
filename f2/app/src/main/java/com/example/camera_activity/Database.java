package com.example.camera_activity;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Database {

    private static final int TIMEOUT = 10000;
    private Activity context;
    private RequestQueue queue;
    private String domain;
    private static Database instance = null;

    public Database(Activity _context) {
        context = _context;
        queue = Volley.newRequestQueue(context);
        domain = "https://landmark0.000webhostapp.com";
//        domain = "http://192.168.43.3/f3/server_file"; //"http://192.168.56.1/f2/server_file";
    }

    public void clearQueue()
    {
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public static Database getInstance(Activity _context)
    {
        if (instance == null)
            instance = new Database(_context);
        return instance;
    }

    public void initialize_server()   //should be called once in the beginning, initializes the server database if needed
    {
        String url = domain+"/init.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Unable to connect",Toast.LENGTH_LONG).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    public void getData(final String ID,  final Callback<myObject> _callback) //returns data
    {
        String url = domain+"/retrieve_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                myObject result = (new Gson()).fromJson(response, myObject.class);
                _callback.callbackFunctionSuccess(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _callback.callbackFunctionFailure();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameter = new HashMap<String, String>();
                parameter.put("ID", ID);
                return parameter;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    public void getNearLandmark(final String _id,  final String _city, final Callback<ObjectNear> _callback) //returns data
    {
        String url = domain+"/retrieve_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ObjectNear result = (new Gson()).fromJson(response, ObjectNear.class);
                _callback.callbackFunctionSuccess(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _callback.callbackFunctionFailure();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameter = new HashMap<String, String>();
                parameter.put("getNearLandmark", _id);
                parameter.put("City", _city);
                return parameter;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    public void getFoodData(final String _city,  final Callback<foodObject> _callback) //returns  data
    {
        String url = domain+"/retrieve_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                foodObject result = (new Gson()).fromJson(response, foodObject.class);
                _callback.callbackFunctionSuccess(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _callback.callbackFunctionFailure();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameter = new HashMap<String, String>();
                parameter.put("getFood",_city);
                return parameter;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void getFoodImage(final String _foodID, final Callback<Bitmap> _callback)
    {
        String url = domain+"/retrieve_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Bitmap result = null;
                if (!response.equals("NULL"))
                {
                    byte[] b = Base64.decode(response, Base64.DEFAULT);
                    result = BitmapFactory.decodeByteArray(b, 0, b.length);
                }
                _callback.callbackFunctionSuccess(result);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                _callback.callbackFunctionFailure();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameter = new HashMap<String, String>();
                parameter.put("get_food_image", _foodID);
                return parameter;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }


    interface Callback<T>
    {
        void callbackFunctionSuccess(T result);
        void callbackFunctionFailure();
    }

    static class myObject   //object to store the data
    {
        public String Name, Information, Message, City;
        Double Lat_max, Lat_min, Long_min, Long_max;
        public int ID;

        public myObject(int ID, String _Name, String _info, String _Message, String _city, Double _lat_max, Double _lat_min, Double _long_min, Double _long_max) {
            this.ID = ID;
            this.City = _city;
            this.Message = _Message;
            Name =_Name;
            Information = _info;
            Lat_max = _lat_max;
            Lat_min = _lat_min;
            Long_max=_long_max;
            Long_min=_long_min;
        }
    }

    static class ObjectNear
    {
        public String Name, Message;

        public ObjectNear(  String _Name,  String _Message) {
            this.Message = _Message;
            Name =_Name;
        }
    }

    static class foodObject
    {
        public String Name;
        int id;

        public foodObject(String _Name, int id) {
            this.id = id;
            Name = _Name;
        }
    }
}

