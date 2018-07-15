package com.rydeon.andr.dataloaders;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.rydeon.andr.adapters.AdapterCarMakeModel;
import com.rydeon.andr.app.AppConfig;
import com.rydeon.andr.helper.CustomGetters;
import com.rydeon.andr.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 14/07/2018.
 */

public class CarMakeModelLoader {

    AdapterCarMakeModel adapterCarMakeModel;
    ProgressBar progressBar;
    ListView listView;
    Activity activity;
    SessionManager sm;
    CustomGetters cv;

    public CarMakeModelLoader(Activity activity, ProgressBar progressBar, ListView listView){
        this.activity = activity;
        this.progressBar = progressBar;
        this.listView = listView;

        sm = new SessionManager(activity);



    }


    public void loadCarMake(){
        final List<CustomGetters> data = new ArrayList<>();


        adapterCarMakeModel = new AdapterCarMakeModel(activity, data);
        listView.setAdapter(adapterCarMakeModel);
        progressBar.setVisibility(View.VISIBLE);

        Ion.with(activity).load("GET", AppConfig.MAKE)
                .setHeader("x-auth-token", sm.getToken())
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {
                progressBar.setVisibility(View.GONE);

                if(e == null){
                    Log.d("RESPONSE", result.getResult());

                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());

                        JSONArray resultArray = jsonObject.getJSONArray("result");

                        for(int i =0; i < resultArray.length(); i++){

                            JSONObject makeObj = resultArray.getJSONObject(i);
                            String car_make = makeObj.getString("make");
                            String make_id = makeObj.getString("id");

                            cv = new CustomGetters();
                            cv.setCarMake(car_make);
                            cv.setCarMakeId(make_id);

                            data.add(cv);

                        }
                        adapterCarMakeModel.notifyDataSetChanged();

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
                else {
                    e.printStackTrace();
                }

            }
        });

    }//end of load car make


    public void loadCarModel(String modelId){

        progressBar.setVisibility(View.VISIBLE);
        final List<CustomGetters> data = new ArrayList<>();


        adapterCarMakeModel = new AdapterCarMakeModel(activity, data);
        listView.setAdapter(adapterCarMakeModel);

        Ion.with(activity).load("GET", AppConfig.MODEL+"?makeId="+modelId)
                .setHeader("x-auth-token", sm.getToken())
                .asString().withResponse().setCallback(new FutureCallback<Response<String>>() {
            @Override
            public void onCompleted(Exception e, Response<String> result) {

                progressBar.setVisibility(View.GONE);

                if(e == null){
                    Log.d("RESPONSE", result.getResult());

                    try {
                        JSONObject jsonObject = new JSONObject(result.getResult());

                        JSONArray resultArray = jsonObject.getJSONArray("result");

                        for(int i =0; i < resultArray.length(); i++){

                            JSONObject makeObj = resultArray.getJSONObject(i);
                            String car_model = makeObj.getString("modelName");
                            String model_id = makeObj.getString("id");

                            cv = new CustomGetters();
                            cv.setCarMake(car_model);
                            cv.setCarMakeId(model_id);

                            data.add(cv);

                        }
                        adapterCarMakeModel.notifyDataSetChanged();

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
                else {
                    e.printStackTrace();
                }

            }
        });

    }

}
