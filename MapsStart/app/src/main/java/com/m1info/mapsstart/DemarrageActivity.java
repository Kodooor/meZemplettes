package com.m1info.mapsstart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DemarrageActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_demarrage);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DemarrageActivity.this, MapsActivity.class);
                    startActivityForResult(intent, 2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();

                }
            }, 4000);

        }

        @Override
        public void onBackPressed() {
            //TODO
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }

