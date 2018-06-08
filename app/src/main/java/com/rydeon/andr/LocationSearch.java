package com.rydeon.andr;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import spencerstudios.com.bungeelib.Bungee;

/**
 * Created by HP on 08/06/2018.
 */

public class LocationSearch extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_search_fragment);
       // Bungee.slideUp(LocationSearch.this);
        YoYo.with(Techniques.SlideInUp).duration(500).playOn(findViewById(R.id.cardview));
    }
}
