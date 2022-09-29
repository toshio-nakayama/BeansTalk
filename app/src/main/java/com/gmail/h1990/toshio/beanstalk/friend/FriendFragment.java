package com.gmail.h1990.toshio.beanstalk.friend;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.EXT_JPG;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.STATUS_MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TIME_STAMP;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.databinding.FragmentFriendBinding;
import com.gmail.h1990.toshio.beanstalk.model.FriendListModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendFragment extends Fragment {
    RecyclerView rvFriendList;
    private FriendListAdapter friendListAdapter;
    private List<FriendListModel> friendModelList;
    private DatabaseReference databaseReferenceUsers;
    private ChildEventListener childEventListener;
    private Query query;
    private FragmentFriendBinding binding;

    public FriendFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFriendList = view.findViewById(R.id.rv_friend);
        friendModelList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(getActivity(), friendModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvFriendList.setLayoutManager(linearLayoutManager);
        rvFriendList.setAdapter(friendListAdapter);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(USERS);
        DatabaseReference databaseReferenceTalk = FirebaseDatabase.getInstance().getReference().child(TALK).child(currentUser.getUid());
        query = databaseReferenceTalk.orderByChild(TIME_STAMP);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot, snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addChildEventListener(childEventListener);
    }

    private void updateList(DataSnapshot dataSnapshot, String userId) {
        databaseReferenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = "";
                Optional<Object> opt =
                        Optional.ofNullable(snapshot.child(NAME).getValue());
                if (opt.isPresent()) {
                    name = snapshot.child(NAME).getValue().toString();
                }
                String statusMessage = "";
                Optional<Object> opt1 =
                        Optional.ofNullable(snapshot.child(STATUS_MESSAGE).getValue());
                if (opt1.isPresent()) {
                    statusMessage = snapshot.child(STATUS_MESSAGE).getValue().toString();
                }
                String photoName = userId + EXT_JPG;
                FriendListModel friendListModel = new FriendListModel(userId, name,
                        statusMessage, photoName);
                friendModelList.add(friendListModel);
                friendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        query.removeEventListener(childEventListener);
    }
}