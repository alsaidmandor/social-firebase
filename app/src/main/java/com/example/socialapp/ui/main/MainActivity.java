package com.example.socialapp.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.socialapp.R;
import com.example.socialapp.ui.home.homeFragment;
import com.example.socialapp.ui.welcome.welcomeFragment;
import com.example.socialapp.utils.constants;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!constants.getUid(this).equals("empty"))
        {
            startFragment(new homeFragment());
        }
        else
        {
            startFragment(new welcomeFragment());
        }

        constants.initFirebase();
    }

    private void startFragment(Fragment Fragment)
    {

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container , Fragment)
                .disallowAddToBackStack()
                .commit();

    }
}