package com.cqsynet.swifi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cqsynet.swifi.util.StatusBarUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarLightMode(this);
    }


}
