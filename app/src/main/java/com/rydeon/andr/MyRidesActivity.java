package com.rydeon.andr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.rydeon.andr.fragment.PendingRidesFragment;
import com.rydeon.andr.fragment.SuccessfulRidesFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 08/06/2018.
 */

public class MyRidesActivity extends AppCompatActivity {
    private TabLayout tableLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);
        getSupportActionBar().setTitle(getString(R.string.my_rides));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tableLayout = (TabLayout) findViewById(R.id.tabs);
        tableLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//
//                if(position == 0){
//                    getSupportActionBar().setTitle(getString(R.string.pending_rides));
//                }
//                if(position == 1){
//                    getSupportActionBar().setTitle( getString(R.string.sucessful));
//                }
//

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //setiing up the viewPager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PendingRidesFragment(), getString(R.string.pending_rides));
        adapter.addFragment(new SuccessfulRidesFragment(), getString(R.string.sucessful));

        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        //add fragment with title to the fragment list
        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
              return fragmentTitleList.get(position);
           // return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
