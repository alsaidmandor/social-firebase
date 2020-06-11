package com.example.socialapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialapp.R;
import com.example.socialapp.model.userModel;
import com.example.socialapp.utils.constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileFragment extends Fragment {

    private View mainView;

    private CircleImageView userImage;
    private TextView userName;
    private  TextView emailField;
    private TextView mobileField;
    private TextView addressField;

    private LinearLayout layoutEmail ;
    private LinearLayout layoutMobile ;
    private LinearLayout layoutAddress ;
    private LinearLayout layoutName ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_profile, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        constants.initProgress(requireContext() ,"please wait..");
        initViews();
        getUserData();
    }

    private void initViews() {
        userImage = mainView.findViewById(R.id.profile_user_image);
        userName = mainView.findViewById(R.id.profile_name);
        mobileField = mainView.findViewById(R.id.profile_mobile);
        addressField = mainView.findViewById(R.id.profile_address);
        emailField = mainView.findViewById(R.id.profile_email);

        layoutName = mainView.findViewById(R.id.layout_name);
        layoutEmail = mainView.findViewById(R.id.layout_email);
        layoutMobile = mainView.findViewById(R.id.layout_mobile);
        layoutAddress = mainView.findViewById(R.id.layout_address);
    }

    private void getUserData()
    {
        constants.showProgress();
        final String uId = constants.getUid(requireActivity());
        constants.getDatabaseReference().child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                userModel userModel = dataSnapshot.getValue(userModel.class);

                if (userModel != null)
                {
                  String name = userModel.getName();
                    String image = userModel.getImage();
                    String email = userModel.getEmail();
                    String mobile = userModel.getMobile();
                    String address = userModel.getAddress();

                    Picasso
                            .get()
                            .load(image)
                            .into(userImage);
                    userImage.setVisibility(View.VISIBLE);

                    layoutName.setVisibility(View.VISIBLE);
                    userName.setText(name);

                    layoutEmail.setVisibility(View.VISIBLE);
                    emailField.setText(email);

                    layoutMobile.setVisibility(View.VISIBLE);
                    mobileField.setText(mobile);

                    layoutAddress.setVisibility(View.VISIBLE);
                    addressField.setText(address);

                    constants.dismissProgress();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
