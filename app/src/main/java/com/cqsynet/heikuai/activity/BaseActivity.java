package com.cqsynet.heikuai.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cqsynet.heikuai.util.StatusBarUtil;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarLightMode(this);

        mProgressDialog = new ProgressDialog(this);
    }


}
