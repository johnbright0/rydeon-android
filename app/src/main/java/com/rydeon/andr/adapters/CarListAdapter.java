package com.rydeon.andr.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rydeon.andr.R;
import com.rydeon.andr.helper.CustomGetters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 19/06/2018.
 */

public class CarListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater layoutInflater;
    private CustomGetters cv;
    private List<CustomGetters> data = new ArrayList<>();

    public CarListAdapter(Activity activity, List<CustomGetters> data){
        this.activity = activity;
        this.data = data;

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        View view = convertView;

        if(convertView == null)
            view = layoutInflater.inflate(R.layout.adapter_car_list, null);

        ImageView imgCarImage = view.findViewById(R.id.imgCarImage);
        TextView txtCarModelName = view.findViewById(R.id.txtMakeModelName);
        TextView txtRegNumber = view.findViewById(R.id.txtRegNumber);
        TextView txtYear = view.findViewById(R.id.txtYear);
        TextView txtCarId = view.findViewById(R.id.txtCarId);

        cv = data.get(position);

        txtCarId.setText(cv.getCarId());
        txtCarModelName.setText(cv.getCarMake()+" - "+cv.getCarModel());
        txtRegNumber.setText(cv.getCarRegNumber());
        txtYear.setText(cv.getCarYear());

        return view;
    }
}
