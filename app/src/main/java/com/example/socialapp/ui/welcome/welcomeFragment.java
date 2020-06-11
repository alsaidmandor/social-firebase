package com.example.socialapp.ui.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialapp.R;
import com.example.socialapp.ui.login.loginFragment;
import com.example.socialapp.ui.register.registerFragment;
import com.example.socialapp.utils.constants;

public class welcomeFragment extends Fragment
{

    private View mainView ;

    private Button login ;
    private Button register ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_welcome , null);
        return mainView ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        initViews();
    }

    private void initViews()
    {

        register = mainView.findViewById(R.id.wel_register);

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                constants.replaceFragment(welcomeFragment.this , new registerFragment(), false);
            }
        });

        login = mainView.findViewById(R.id.wel_login);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                constants.replaceFragment(welcomeFragment.this , new loginFragment(), false);
            }
        });

    }
}
