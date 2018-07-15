package com.rydeon.andr.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rydeon.andr.R;
import com.rydeon.andr.helper.CustomGetters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 14/07/2018.
 */

public class AdapterCarMakeModel extends BaseAdapter {
    Activity activity;
    LayoutInflater inflater;
    CustomGetters cv;
    List<CustomGetters> data = new ArrayList<>();

    public AdapterCarMakeModel(Activity activity, List<CustomGetters> data){
        this.activity = activity;
        this.data = data;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View myView = convertView;
        if(convertView == null)
            myView = inflater.inflate(R.layout.adapter_make_model, null);

        TextView txtCarMake = myView.findViewById(R.id.txtCarMake);
        TextView txtCarMakeId = myView.findViewById(R.id.txtCarMakeId);

        cv = data.get(position);

        txtCarMake.setText(cv.getCarMake());
        txtCarMakeId.setText(cv.getCarMakeId());

        return myView;
    }
}
