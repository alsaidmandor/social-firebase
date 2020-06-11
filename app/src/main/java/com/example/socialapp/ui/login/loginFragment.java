package com.example.socialapp.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialapp.R;
import com.example.socialapp.ui.home.homeFragment;
import com.example.socialapp.utils.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class loginFragment extends Fragment
{

    private View mainView ;

    private EditText emailField ;
    private EditText passwordField ;

    private Button login ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_login , null);
        return mainView ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        initViews();
        constants.initProgress(requireContext() , "please wait..");

    }

    private void initViews()
    {
        emailField = mainView.findViewById(R.id.login_email);
        passwordField = mainView.findViewById(R.id.login_password);
        login = mainView.findViewById(R.id.login_login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if (email.isEmpty() || password.isEmpty())
                {
                    constants.showToast(requireContext() , "invalid Data");
                    return;
                }

                constants.showProgress();
                loginToHome(email , password);
            }
        });

    }

    private void loginToHome(String email, String password)
    {
        constants.getAuth().signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        constants.dismissProgress();
                        if (task.isSuccessful())
                        {
                            if (task.getResult().getUser().isEmailVerified())
                            {
                                constants.saveUid(requireActivity(), task.getResult().getUser().getUid());
                                constants.replaceFragment(loginFragment.this, new homeFragment(), false);
                            } else
                            {
                                constants.showToast(requireContext(), "please verify your email");
                            }
                        } else
                        {
                            constants.showToast(requireContext(), task.getException().getMessage());
                        }

                    }
                });
    }
}
