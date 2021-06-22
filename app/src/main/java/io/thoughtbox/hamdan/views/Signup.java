package io.thoughtbox.hamdan.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.HashMap;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.fragments.PersonalInfo;

public class Signup extends AppCompatActivity {

    public static HashMap<String, String> userData;
    public static HashMap<String, File> imageData;
    public static HashMap<String, byte[]> signData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userData = new HashMap<>();
        imageData = new HashMap<>();
        signData = new HashMap<String, byte[]>();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, new PersonalInfo()).commit();
    }

}
