package com.example.socialapp.ui.chats;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.model.chatModel;
import com.example.socialapp.model.myChats;
import com.example.socialapp.model.userModel;
import com.example.socialapp.ui.users.usersFragment;
import com.example.socialapp.utils.constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatsFragment extends Fragment {

    private View mainView;

    private RecyclerView recyclerView ;
    private EditText chatField;
    private Toolbar toolbar;
    private CircleImageView circleImageView;
    private TextView textView;

    private List<chatModel> chatModels ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_chats, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        constants.initProgress(requireContext() ,"please wait..");
        initViews();
        getMessages();
    }

    private void initViews()
    {
        recyclerView = mainView.findViewById(R.id.chats_recycle);
        chatField = mainView.findViewById(R.id.message_body_field);
        toolbar = mainView.findViewById(R.id.chat_toolbar);
        circleImageView = mainView.findViewById(R.id.chat_image);
        textView = mainView.findViewById(R.id.chat_title);
        FloatingActionButton sendFab = mainView.findViewById(R.id.send_message_fab);

        chatModels = new ArrayList<>();

        sendFab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String messageText = chatField.getText().toString();

                if (messageText.isEmpty())
                {
                    constants.showToast(requireContext() , "invalid Data");
                    return;
                }

                sendMessage(messageText);
            }
        });

        Picasso.get()
                .load(constants.myChats.getImage())
                .into(circleImageView);

        textView.setText(constants.myChats.getName());

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_white_ios_24); // your drawable
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

/*        requireActivity().setActionBar(toolbar);

        requireActivity().getActionBar().setTitle("");
        requireActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        requireActivity().getActionBar().setDisplayShowHomeEnabled(true);
        requireActivity().getActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_baseline_arrow_white_ios_24));
        requireActivity().getActionBar().*/

    }

    private void sendMessage(String messageText)
    {

        long time = constants.getTime();
        String sendId = constants.getUid(requireActivity());
        String receiverId = constants.myChats.getId();
        String keyMessage = constants.getDatabaseReference().child("chats").child(sendId).child(receiverId).push().getKey();

        chatModel chatModelData = new chatModel(sendId ,messageText ,time,1,keyMessage);

        myChats myChats = new myChats(receiverId,constants.myChats.getName() ,constants.myChats.getImage());

        constants.getDatabaseReference().child("chats").child(sendId).child(receiverId).child(keyMessage).setValue(chatModelData);
        constants.getDatabaseReference().child("chats").child(receiverId).child(sendId).child(keyMessage).setValue(chatModelData);

        constants.getDatabaseReference().child("myChats").child(sendId).child(receiverId).child(keyMessage).setValue(myChats);
        constants.getDatabaseReference().child("myChats").child(receiverId).child(sendId).child(keyMessage).setValue(myChats);

        chatField.setText("");
    }

    private void getMessages()
    {
        constants.getDatabaseReference().child("chats").child(constants.getUid(requireActivity()))
                .child(constants.myChats.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                chatModels.clear();

                for (DataSnapshot b:dataSnapshot.getChildren())
                {

                    chatModel  chatModel = b.getValue(chatModel.class);
                    chatModels.add(chatModel);
                }

                if (chatModels.size() !=0)
                {
                    recyclerView.setAdapter(new chatsAdapter(chatModels));
                    constants.dismissProgress();
                }


                constants.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    class chatsAdapter extends RecyclerView.Adapter<chatsAdapter.vhChats>
    {

        private List<chatModel> modelList ;

        public chatsAdapter(List<chatModel> modelList)
        {
            this.modelList = modelList;
        }

        @NonNull
        @Override
        public vhChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_chat_message , parent , false);
            return new vhChats(view);
        }

        @Override
        public void onBindViewHolder(@NonNull vhChats holder, int position)
        {

            chatModel model = modelList.get(position);

            String message = model.getMessage();
            long time = model.getTime() ;
            String id = model.getSenderId() ;

            long now = constants.getTime() ;

            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

            holder.timeField.setText(String.valueOf(ago));
            holder.messageField.setText(message);

            if (id .equals(constants.getUid(requireActivity())))
            {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColorStateList(requireActivity(), R.color.chat));
                holder.messageField.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                holder.timeField.setTextColor(ContextCompat.getColorStateList(requireActivity(), R.color.white));
                holder.linearLayout.setGravity(Gravity.END);
            }

            setSeen(model.getMessageId());
            isSeen(model.getMessageId() , holder.seenField);
        }

        private void isSeen(final String messageId, final ImageView seenField)
        {
            constants.getDatabaseReference().child("seen").child(constants.myChats.getId()).child(constants.getUid(requireActivity())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(messageId))
                    {
                        seenField.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.like)));
                    } else
                    {
                        seenField.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.disLike)));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        private void setSeen(String id)
        {
            constants.getDatabaseReference().child("seen").child(constants.getUid(requireActivity())).child(constants.myChats.getId()).child(id).setValue(true);
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        class vhChats extends RecyclerView.ViewHolder
        {
//            CircleImageView postUserImage;
            TextView messageField;
            TextView timeField;
            ImageView seenField ;
            CardView cardView ;
            LinearLayout linearLayout;


            vhChats(@NonNull View itemView)
            {
                super(itemView);

                messageField = itemView.findViewById(R.id.chat_message_text);
                timeField = itemView.findViewById(R.id.chat_time_text);
                seenField = itemView.findViewById(R.id.chat_seen);
                cardView = itemView.findViewById(R.id.chat_card);
                linearLayout = itemView.findViewById(R.id.chat_linear);
            }
        }
    }


}
