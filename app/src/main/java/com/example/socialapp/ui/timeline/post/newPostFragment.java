package com.example.socialapp.ui.timeline.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.socialapp.R;
import com.example.socialapp.model.postModel;
import com.example.socialapp.model.userModel;
import com.example.socialapp.ui.timeline.timelineFragment;
import com.example.socialapp.utils.constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class newPostFragment extends Fragment {

    private View mainView;

    private  CircleImageView postUserImage;
    private ImageView postImage;
    private  TextView postUserName;
    private  TextView postText;
    private TextView postPickImage;
    private TextView postDeleteImage;
    private Button postBtn;
    private Uri selectedPostImage = null;
    private String name;
    private String image;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_new_post, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
        constants.initProgress(requireContext() ,"please wait..");
        getUserData();
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
                    name = userModel.getName();
                    image = userModel.getImage();

                    postUserName.setText(name);
                    Picasso
                            .get()
                            .load(image)
                            .into(postImage);

                    constants.dismissProgress();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initViews()
    {

        postUserImage = mainView.findViewById(R.id.new_post_user_image);
        postImage = mainView.findViewById(R.id.new_post_image);
        postUserName = mainView.findViewById(R.id.new_post_user_name);
        postPickImage = mainView.findViewById(R.id.new_post_pick_image);
        postDeleteImage = mainView.findViewById(R.id.new_post_delete_image);
        postText = mainView.findViewById(R.id.new_post_text);
        postBtn = mainView.findViewById(R.id.new_post_btn);

        postPickImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CropImage.activity()
                        .start(requireContext(), newPostFragment.this);

                postImage.setVisibility(View.VISIBLE);
                postDeleteImage.setVisibility(View.VISIBLE);
            }
        });

        postDeleteImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postImage.setVisibility(View.GONE);
                postDeleteImage.setVisibility(View.GONE);
                selectedPostImage = null;
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                long time = constants.getTime();
                String text = postText.getText().toString();

                if (text.isEmpty())
                {
                    constants.showToast(requireContext(), "type a post ..");
                    return;
                }

                constants.showProgress();

                if (selectedPostImage == null)
                {
                    savePost(name , image , text ,time,"",0);
                }
                else
                {
                    uploadImage(name , image , text ,time,selectedPostImage,1);
                }
            }
        });

    }

    private void uploadImage(final String name, final String image, final String text, final long time, Uri selectedPostImage, final int type)
    {
        // set file place into storage and file name
        final StorageReference userImageRef = constants.getStorageReference().child("posts_images/"+selectedPostImage.getLastPathSegment());

        // put file into upload task
        UploadTask uploadTask = userImageRef.putFile(selectedPostImage);

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

                    savePost(name, image, text, time, imageUrl, type);
                }
            }
        });
    }

    private void savePost(String name, String image, String text, long time, String selectedImage, int type)
    {
        String postId = constants.getDatabaseReference().child("Posts").push().getKey();
        postModel postModel = new postModel(name ,image ,text ,selectedImage ,time , type ,postId);

        if (postId != null)
        {

            constants.getDatabaseReference().child("Posts").child(postId).setValue(postModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        constants.dismissProgress();
                        requireActivity().onBackPressed();
                    }

                }
            });

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                selectedPostImage = result.getUri();

                Picasso
                        .get()
                        .load(selectedPostImage)
                        .into(postImage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                constants.showToast(requireContext() , error.getMessage());
            }
        }
    }
}
