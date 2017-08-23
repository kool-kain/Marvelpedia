package com.example.dani.marvelpedia;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import layout.MyPageAdapter;

public class SearchActivity extends AppCompatActivity {
    private TabLayout customTab;
    private ViewPager viewPager;
    private MyPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Carga del ViewPager, su adapter y el Tab
        viewPager=(ViewPager)findViewById(R.id.view_pager);
        adapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        customTab = (TabLayout) findViewById(R.id.custom_tablayout);
        customTab.setTabMode(TabLayout.MODE_FIXED);
        customTab.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(customTab));
    }

}
