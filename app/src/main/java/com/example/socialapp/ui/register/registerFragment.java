package com.example.socialapp.ui.register;

import android.content.Intent;
import android.net.Uri;
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
import com.example.socialapp.model.userModel;
import com.example.socialapp.ui.login.loginFragment;
import com.example.socialapp.utils.constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class registerFragment extends Fragment
{

    private View mainView ;

    private EditText nameField ;
    private EditText emailField ;
    private EditText passwordField ;
    private EditText confirmField ;
    private EditText mobileField ;
    private EditText addressField ;

    private Button register ;

    private CircleImageView circleImageView;
    private Uri userImage;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_register , null);
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
        nameField = mainView.findViewById(R.id.register_name);
        emailField = mainView.findViewById(R.id.register_email);
        passwordField = mainView.findViewById(R.id.register_password);
        confirmField = mainView.findViewById(R.id.register_confirm_password);
        mobileField = mainView.findViewById(R.id.register_mobile);
        addressField = mainView.findViewById(R.id.register_address);
        circleImageView = mainView.findViewById(R.id.pick_user_image);

        circleImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CropImage.activity()
                        .start(requireContext(), registerFragment.this);
            }
        });

        register = mainView.findViewById(R.id.register_register_btn);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirm = confirmField.getText().toString();
                String mobile = mobileField.getText().toString();
                String address = addressField.getText().toString();

                if (name.isEmpty() || password.isEmpty() || mobile.isEmpty() || address.isEmpty())
                {
                    constants.showToast(requireContext() , "invalid Data");
                    return;
                }

                if( !confirm.equals(password))
                {
                    constants.showToast(requireContext() , "password is not matching");
                    return;
                }

                constants.showProgress();
                registerUser(name ,email , password , confirm , mobile , address);

            }
        });

    }

    private void registerUser(final String name, final String email, String password, final String confirm, final String mobile, final String address)
    {
        constants.getAuth().createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            task.getResult().getUser().sendEmailVerification();
                            String uId = task.getResult().getUser().getUid() ;

                            uploadImage (name , email,mobile , address , uId);

                        }
                        else
                        {
                            constants.dismissProgress();
                            constants.showToast(requireContext() , task.getException().getMessage());
                        }

                    }
                });
    }

    private void uploadImage(final String name, final String email, final String mobile, final String address, final String uId)
    {

       // set file place into storage and file name
        final StorageReference userImageRef = constants.getStorageReference().child("users_images/"+userImage.getLastPathSegment());

        // put file into upload task
        UploadTask uploadTask = userImageRef.putFile(userImage);

        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                return userImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                if (task.isSuccessful())
                {
                    Uri downloadUri = task.getResult();
                    String imageUrl = downloadUri.toString();

                    saveNewUser(name, email, mobile, address, uId, imageUrl);
                }
            }
        });

    }

    private void saveNewUser(String name, String email, String mobile, String address, String uId, String imageUrl)
    {

        userModel  model = new userModel( name , email, mobile ,address  ,imageUrl,uId);

        constants.getDatabaseReference().child("users").child(uId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                constants.dismissProgress();

                if (task.isSuccessful())
                {
                    constants.showToast(requireContext(), "please verify your email then login");
                    constants.replaceFragment(registerFragment.this , new loginFragment() , true);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                userImage = result.getUri();

                Picasso
                        .get()
                        .load(userImage)
                        .into(circleImageView);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                constants.showToast(requireContext() , error.getMessage());
            }
        }
    }
}