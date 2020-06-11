package com.example.socialapp.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialapp.R;
import com.example.socialapp.model.userModel;
import com.example.socialapp.ui.chats.chatsFragment;
import com.example.socialapp.utils.constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class usersFragment extends Fragment {

    private View mainView;

    private RecyclerView usersRecycle ;
    private List<userModel> userModelList ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_users, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        initViews();
        getUsers();
    }

    private void initViews()
    {

        usersRecycle = mainView.findViewById(R.id.user_recycle);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext() ,DividerItemDecoration.VERTICAL);
        usersRecycle.addItemDecoration(decoration);

        userModelList = new ArrayList<>();


    }

    private void getUsers()
    {

        constants.getDatabaseReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userModelList.clear();

                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    userModel model = d.getValue(userModel.class);

                    if (!model.getId().equals(constants.getUid(requireActivity())))
                    {
                        userModelList.add(model);
                    }
                }

                usersRecycle.setAdapter(new userAdapter(userModelList));
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class userAdapter extends RecyclerView.Adapter<userAdapter.vhcomment>
    {

        private List<userModel> modelList ;

        public userAdapter(List<userModel> modelList)
        {
            this.modelList = modelList;
        }

        @NonNull
        @Override
        public vhcomment onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_users , parent , false);
            return new vhcomment(view);
        }

        @Override
        public void onBindViewHolder(@NonNull vhcomment holder, int position)
        {

            final userModel model = modelList.get(position);

            String name = model.getName();
            String address = model.getAddress();
            String userImage = model.getImage();

            holder.postUserName.setText(name);
            holder.address.setText(address);

            Picasso
                    .get()
                    .load(userImage)
                    .into(holder.postUserImage);

            holder.itemView.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    constants.replaceFragment(usersFragment.this , new chatsFragment() , true);
                    constants.myChats = model ;
                }
            });

        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        class vhcomment extends RecyclerView.ViewHolder
        {
            CircleImageView postUserImage;
            TextView postUserName;
            TextView address;


            vhcomment(@NonNull View itemView)
            {
                super(itemView);

                postUserImage = itemView.findViewById(R.id.user_user_image);
                postUserName = itemView.findViewById(R.id.user_user_name);
                address = itemView.findViewById(R.id.user_user_address);

            }
        }
    }
}
