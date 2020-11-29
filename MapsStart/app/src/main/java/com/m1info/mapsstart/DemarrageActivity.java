package com.m1info.mapsstart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

// Classe qui gère la page de lancement de l'application
public class DemarrageActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_demarrage);

            // On crée un Handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // on défini la vue sur laquelle on va à la fin de la vue de lancement
                    Intent intent = new Intent(DemarrageActivity.this, MapsActivity.class);
                    startActivityForResult(intent, 2);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();

                }
                // Délai avant d'arriver sur la vue suivante
            }, 2000);

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }

