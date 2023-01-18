package com.example.card_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        final Button courseButton = (Button) findViewById(R.id.courseButton);
        final Button staticsButton = (Button) findViewById(R.id.statisticButton);
        final Button scheduleButton = (Button) findViewById(R.id.scheduleButton);
        final RelativeLayout notice = (RelativeLayout) findViewById(R.id.notice);

        courseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                courseButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary_dark));
                staticsButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                scheduleButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new CourseFragment());
                fragmentTransaction.commit();
            }
        });


        staticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                courseButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                staticsButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary_dark));
                scheduleButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new StatisticFragment());
                fragmentTransaction.commit();
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notice.setVisibility(View.GONE);
                courseButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                staticsButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary));
                scheduleButton.setBackgroundColor(getResources().getColor(R.color.design_default_color_primary_dark));
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, new ScheduleFragment());
                fragmentTransaction.commit();
            }
        });
    }
}
