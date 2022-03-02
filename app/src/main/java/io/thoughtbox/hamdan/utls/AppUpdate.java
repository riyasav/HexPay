package io.thoughtbox.hamdan.utls;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import io.thoughtbox.hamdan.R;


public class AppUpdate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_update);

        Button update = findViewById(R.id.update);
        Button cancel = findViewById(R.id.cancel);

        update.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=io.thoughtbox.hamdan"));
            startActivity(intent);
        });


        cancel.setOnClickListener(v -> finish());


    }
}
