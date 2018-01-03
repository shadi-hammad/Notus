package com.shadihammad.stormy.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shadihammad.stormy.R;
import com.shadihammad.stormy.adapters.DayAdapter;
import com.shadihammad.stormy.weather.Day;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class DailyForecastActivity extends AppCompatActivity {

    private Day[] mDays;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.dailyRelativeLayout) RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        int backgroundColor = intent.getIntExtra("background_color", 4879360);
        mRelativeLayout.setBackgroundColor(backgroundColor);

        DayAdapter adapter = new DayAdapter(this, mDays);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        alphaAdapter.setDuration(1000);
        mRecyclerView.setAdapter(alphaAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DailyForecastActivity.this,
                        "Tap item for weather summary", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }
}
