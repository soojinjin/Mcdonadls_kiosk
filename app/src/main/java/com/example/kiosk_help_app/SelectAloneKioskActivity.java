package com.example.kiosk_help_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.kiosk_help_app.alone.fastfood.AloneFastfoodSelectStorePackageActivity;

public class
SelectAloneKioskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_alone_kiosk);

        ImageButton ff_btn = (ImageButton) findViewById(R.id.AloneFF_btn);

        ff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AloneFastfoodSelectStorePackageActivity.class);
                startActivity(intent);
            }
        });
    }
}