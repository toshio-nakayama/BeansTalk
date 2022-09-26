package com.gmail.h1990.toshio.beanstalk.talk;

import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.NAME;
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

import com.gmail.h1990.toshio.beanstalk.databinding.FragmentTalkBinding;
import com.gmail.h1990.toshio.beanstalk.model.TalkListModel;
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

public class TalkFragment extends Fragment {
    private TalkListAdapter talkListAdapter;
    private List<TalkListModel> talkListModelList;
    private DatabaseReference databaseReferenceTalk, databaseReferenceUsers;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private ChildEventListener childEventListener;
    private Query query;
    private FragmentTalkBinding binding;

    public TalkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTalkBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        talkListModelList = new ArrayList<>();
        talkListAdapter = new TalkListAdapter(getActivity(), talkListModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.rvTalkList.setLayoutManager(linearLayoutManager);
        binding.rvTalkList.setAdapter(talkListAdapter);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(USERS);
        databaseReferenceTalk = FirebaseDatabase.getInstance().getReference().child(TALK).child(currentUser.getUid());
        query = databaseReferenceTalk.orderByChild(TIME_STAMP);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateList(snapshot, true, snapshot.getKey());
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

    private void updateList(DataSnapshot dataSnapshot, boolean isNew, String userId) {
        String lastMessage, time, unreadCount;
        lastMessage = "";
        time = "";
        unreadCount = "";
        databaseReferenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = null;
                Optional<Object> opt1 =
                        Optional.ofNullable(snapshot.child(NAME).getValue());
                if (opt1.isPresent()) {
                    name = snapshot.child(NAME).getValue().toString();
                } else {
                    name = "";
                }
                String photoName = userId + ".jpg";
                TalkListModel talkListModel = new TalkListModel(userId, name, photoName,
                        unreadCount, lastMessage, time);
                talkListModelList.add(talkListModel);
                talkListAdapter.notifyDataSetChanged();
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