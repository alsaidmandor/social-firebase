package com.example.socialapp.ui.timeline.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.model.commentModel;
import com.example.socialapp.model.userModel;
import com.example.socialapp.utils.constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentFragment extends Fragment
{

    private View mainView;

    private RecyclerView commentRecycle ;

    private EditText commentField;
    private Toolbar toolbar;

    private List<commentModel> commentModelList ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_comment, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
        getComment();
    }



    private void initViews()
    {

        commentRecycle = mainView.findViewById(R.id.comment_recycle);
        commentField = mainView.findViewById(R.id.comment_message_body_field);
        toolbar = mainView.findViewById(R.id.comment_toolbar);
        commentModelList = new ArrayList<>();
        FloatingActionButton sendFab = mainView.findViewById(R.id.send_comment_message_fab);

        sendFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String commentText = commentField.getText().toString();

                if (commentText.isEmpty())
                {
                    constants.showToast(requireContext() , "invalid Data");
                    return;
                }

                sendComment(commentText);
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_white_ios_24); // your drawable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

    }

    private void sendComment(String commentText)
    {
        long time = constants.getTime() ;

        String postId = constants.myComment.getPostId() ;
        String name = constants.myData.getName() ;
        String image = constants.myData.getImage() ;
        String myId = constants.getUid(requireActivity());
        String commentId = constants.getDatabaseReference().child("comment").child(postId).child(myId).push().getKey() ;

        commentModel commentModel = new commentModel(commentId ,name ,image ,commentText ,0 ,time);

        assert commentId != null;
        constants.getDatabaseReference().child("comment").child(postId).child(commentId).setValue(commentModel);

        commentField.setText("");
    }

    private void getComment()
    {
        String postId = constants.myComment.getPostId() ;

        constants.getDatabaseReference().child("comment").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                commentModelList.clear();

                for (DataSnapshot d:   dataSnapshot.getChildren() )
                {

                    commentModel model = d.getValue(commentModel.class) ;

                        commentModelList.add(model);

                }

                commentRecycle.setAdapter(new commentAdapter(commentModelList));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class commentAdapter extends RecyclerView.Adapter<commentAdapter.vhComment>
    {

        private List<commentModel> modelList ;

        public commentAdapter(List<commentModel> modelList)
        {
            this.modelList = modelList;
        }

        @NonNull
        @Override
        public vhComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_comment , parent , false);
            return new vhComment(view);
        }

        @Override
        public void onBindViewHolder(@NonNull vhComment holder, int position)
        {

            final commentModel model = modelList.get(position);

            String name = model.getName();
            String image = model.getUserImage();
            String comment = model.getComment();
            String commentId = model.getCommentId();

            holder.commentUserName.setText(name);
            holder.comment.setText(comment);

            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.commentUserImage.setVisibility(View.VISIBLE);
            Picasso
                    .get()
                    .load(image)
                    .into(holder.commentUserImage);



        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        class vhComment extends RecyclerView.ViewHolder
        {
            CircleImageView commentUserImage;
            TextView commentUserName;
            TextView comment;
            LinearLayout linearLayout ;

            vhComment(@NonNull View itemView)
            {
                super(itemView);

                commentUserName = itemView.findViewById(R.id.comment_user_name);
               commentUserImage = itemView.findViewById(R.id.comment_user_image);
                comment = itemView.findViewById(R.id.comment_text);
                linearLayout = itemView.findViewById(R.id.layout_comment_visibility);

            }
        }
    }
}