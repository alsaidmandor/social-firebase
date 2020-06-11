package com.example.socialapp.ui.timeline;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.model.postModel;
import com.example.socialapp.model.userModel;
import com.example.socialapp.ui.timeline.comment.commentFragment;
import com.example.socialapp.ui.timeline.post.newPostFragment;
import com.example.socialapp.utils.constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class timelineFragment extends Fragment
{

    private View mainView ;

    private RecyclerView postRecycle ;
    private FloatingActionButton addNewPost ;
    private userModel userModel;
    private List<postModel> modelList ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_timeline , null);
        return mainView ;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        constants.initProgress(requireContext() ,"please wait..");
        getUserData();
        initViews();
        getPost();
    }

    private void initViews()
    {

        postRecycle = mainView.findViewById(R.id.recycle_post);
        addNewPost = mainView.findViewById(R.id.open_new_post);

        modelList = new ArrayList<>();

        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constants.replaceFragment(timelineFragment.this , new newPostFragment() , true);
            }
        });

    }

    private void getUserData()
    {
        final String uId = constants.getUid(requireActivity());
        constants.getDatabaseReference().child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                 userModel = dataSnapshot.getValue(userModel.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getPost()
    {
        constants.showProgress();
        constants.getDatabaseReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                modelList.clear();

                for (DataSnapshot d:   dataSnapshot.getChildren() )
                {
                    postModel model = d.getValue(postModel.class) ;
                    modelList.add(model);
                }

                if (modelList.size() !=0)
                {
                    postRecycle.setAdapter(new postAdapter(modelList));

                    constants.dismissProgress();
                }
                constants.dismissProgress();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class postAdapter extends RecyclerView.Adapter<postAdapter.vhPost>
    {

        private List<postModel> modelList ;

        public postAdapter(List<postModel> modelList)
        {
            this.modelList = modelList;
        }

        @NonNull
        @Override
        public vhPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_post , parent , false);
            return new vhPost(view);
        }

        @Override
        public void onBindViewHolder(@NonNull vhPost holder, int position)
        {

            final postModel model = modelList.get(position);


            int type = model.getType();

            if (type == 0)
            {

                String name = model.getName();
                String userImage = model.getUserImage();
                String postText = model.getPostText();
                String postImage = model.getPostImage();
                long time = model.getTime();
                String postId = model.getPostId();

                holder.postImage.setVisibility(View.GONE);

                holder.postUserName.setText(name);
                holder.postText.setText(postText);

               long timeNow = constants.getTime();

                CharSequence ago =
                        DateUtils.getRelativeTimeSpanString(time, timeNow, DateUtils.MINUTE_IN_MILLIS);

                holder.postTime.setText(String.valueOf(ago));

                Picasso.get()
                        .load(userImage)
                        .into(holder.postUserImage);

                isLike(holder.postLike , postId);
                setCount(holder.postLikesCount , postId);


                holder.postComment.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        constants.replaceFragment(timelineFragment.this , new commentFragment() , true);
                        constants.myComment = model ;
                        constants.myData = userModel ;
                    }
                });
            }
            else if (type == 1)
            {

                String name = model.getName();
                String userImage = model.getUserImage();
                String postText = model.getPostText();
                String postImage = model.getPostImage();
                long time = model.getTime();
                String postId = model.getPostId();

                holder.postUserName.setText(name);
                holder.postText.setText(postText);

                long timeNow = constants.getTime();

                CharSequence ago =
                        DateUtils.getRelativeTimeSpanString(time, timeNow, DateUtils.MINUTE_IN_MILLIS);

                holder.postTime.setText(String.valueOf(ago));

                Picasso.get()
                        .load(userImage)
                        .into(holder.postUserImage);

                Picasso.get()
                        .load(postImage)
                        .into(holder.postImage);

                isLike(holder.postLike , postId);
                setCount(holder.postLikesCount , postId);

                holder.postComment.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        constants.replaceFragment(timelineFragment.this , new commentFragment() , true);
                        constants.myComment = model ;
                        constants.myData = userModel ;
                    }
                });

            }


        }

        void isLike(final TextView like , final String postId)
        {
            final String mId = constants.getUid(requireActivity());
            constants.getDatabaseReference().child("likes").child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(mId))
                    {

                        like.setCompoundDrawableTintList(ContextCompat.getColorStateList(requireActivity(), R.color.like));
                        like.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.like));

                        like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                constants.getDatabaseReference().child("likes").child(postId).child(mId).removeValue();
                            }
                        });
                    }
                    else
                    {
                        like.setCompoundDrawableTintList(ContextCompat.getColorStateList(requireActivity(), R.color.disLike));
                        like.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.disLike));

                        like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                constants.getDatabaseReference().child("likes").child(postId).child(mId).setValue(true);
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void setCount(final TextView count , final String postId)
        {
            constants.getDatabaseReference().child("likes").child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    long countLikes = dataSnapshot.getChildrenCount() ;

                    if (countLikes > 0)
                    {
                        count.setVisibility(View.VISIBLE);
                        count.setText(String.valueOf(countLikes));
                    }
                    else
                    {
                        count.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        class vhPost extends RecyclerView.ViewHolder
        {
            CircleImageView postUserImage;
            ImageView postImage;
            TextView postUserName;
            TextView postTime;
            TextView postText;
            TextView postLikesCount;
            TextView postLike;
            TextView postComment;
            TextView postShare;

            vhPost(@NonNull View itemView)
            {
                super(itemView);

                postUserImage = itemView.findViewById(R.id.post_user_image);
                postImage = itemView.findViewById(R.id.post_image);
                postUserName = itemView.findViewById(R.id.post_user_name);
                postTime = itemView.findViewById(R.id.post_time);
                postText = itemView.findViewById(R.id.post_text);
                postLikesCount = itemView.findViewById(R.id.post_likes_count);
                postLike = itemView.findViewById(R.id.post_like);
                postComment = itemView.findViewById(R.id.post_comment);
                postShare = itemView.findViewById(R.id.post_share);
            }
        }
    }
}
