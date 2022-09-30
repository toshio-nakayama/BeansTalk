package com.gmail.h1990.toshio.beanstalk.talk;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtils;
import com.gmail.h1990.toshio.beanstalk.common.Constants;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.common.NodeNames;
import com.gmail.h1990.toshio.beanstalk.databinding.ActivityTalkBinding;
import com.gmail.h1990.toshio.beanstalk.model.MessageModel;
import com.gmail.h1990.toshio.beanstalk.reaction.ReactionFragment;
import com.gmail.h1990.toshio.beanstalk.reaction.ReactionState;
import com.gmail.h1990.toshio.beanstalk.util.NetworkChecker;
import com.gmail.h1990.toshio.beanstalk.validation.Validation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.mobsandgeeks.saripaar.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TalkActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference rootReference;
    private String currentUserId, talkUserId;
    private Validation validation;
    private MessagesAdapter messagesAdapter;
    private List<MessageModel> messageList;
    private int currentPage = 1;
    private static final int RECORD_PER_PAGE = 30;
    private ChildEventListener childEventListener;
    private String talkUserName;
    private ActivityTalkBinding binding;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtils.setTheme(this);
        binding = ActivityTalkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setupFirebase();
        acceptData();
        setupValidation();
        setupToolBar(talkUserName);

        binding.ivSend.setOnClickListener(this);
        messageList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messageList);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(messagesAdapter);
        loadMessages();
        rootReference.child(NodeNames.TALK).child(currentUserId).child(talkUserId).child(NodeNames.UNREAD_COUNT).setValue(0);
        binding.rvMessages.scrollToPosition(messageList.size());
        binding.srlMessages.setOnRefreshListener(() -> {
            currentPage++;
            loadMessages();
        });

    }

    private void setupValidation() {
        validator = new Validator(this);
        validation = new Validation(validator);
        validator.setValidationListener(validation);
    }

    private void setupFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }

    private void acceptData() {
        if (getIntent().hasExtra(Extras.USER_KEY)) {
            talkUserId = getIntent().getStringExtra(Extras.USER_KEY);
        }
        if (getIntent().hasExtra(Extras.USER_NAME)) {
            talkUserName = getIntent().getStringExtra(Extras.USER_NAME);
        }
    }

    private void setupToolBar(String userName) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(userName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setElevation(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        validator = null;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_send) {
            if (NetworkChecker.connectionAvailable(this)) {
                DatabaseReference userMessagePush =
                        rootReference.child(NodeNames.MESSAGES).child(currentUserId).child(talkUserId).push();
                String pushId = userMessagePush.getKey();
                sendMessage(validation.trimAndNormalize(binding.etMessage.getText().toString()),
                        Constants.MESSAGE_TYPE_TEXT, pushId);
            } else {
                Toast toast = Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    private void sendMessage(String msg, String msgType, String pushId) {
        try {
            if (!msg.equals("")) {
                HashMap<Object, Object> messageMap = new HashMap<>();
                messageMap.put(NodeNames.MESSAGE_ID, pushId);
                messageMap.put(NodeNames.MESSAGE, msg);
                messageMap.put(NodeNames.MESSAGE_TYPE, msgType);
                messageMap.put(NodeNames.MESSAGE_FROM, currentUserId);
                messageMap.put(NodeNames.MESSAGE_TIME, ServerValue.TIMESTAMP);
                messageMap.put(NodeNames.REACTION_STATUS, 0b00000);
                String currentUserReference = NodeNames.MESSAGES + "/" + currentUserId + "/" + talkUserId;
                String talkUserReference = NodeNames.MESSAGES + "/" + talkUserId + "/" + currentUserId;
                HashMap<String, Object> messageUserMap = new HashMap<>();
                messageUserMap.put(currentUserReference + "/" + pushId, messageMap);
                messageUserMap.put(talkUserReference + "/" + pushId, messageMap);
                binding.etMessage.getEditableText().clear();
                rootReference.updateChildren(messageUserMap, (error, ref) -> {
                    if (error != null) {
                        Log.e(Constants.TAG, getString(R.string.failed_to_send_message) + error.getMessage());
                    } else {
                        Log.d(Constants.TAG, getString(R.string.message_sent_successfully));
                        TalkUtils.updateTalkDetails(TalkActivity.this, currentUserId, talkUserId);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, getString(R.string.failed_to_send_message) + e.getMessage());
        }
    }

    private void loadMessages() {
        messageList.clear();
        DatabaseReference databaseReferenceMessages = rootReference.child(NodeNames.MESSAGES).child(currentUserId).child(talkUserId);
        Query messageQuery = databaseReferenceMessages.limitToLast(currentPage = RECORD_PER_PAGE);
        if (childEventListener != null) {
            messageQuery.removeEventListener(childEventListener);
        }
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageModel messageModel = snapshot.getValue(MessageModel.class);
                messageList.add(messageModel);
                messagesAdapter.notifyDataSetChanged();
                binding.rvMessages.scrollToPosition(messageList.size());
                binding.srlMessages.setRefreshing(false);
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
                binding.srlMessages.setRefreshing(false);
            }
        };
        messageQuery.addChildEventListener(childEventListener);
    }

    public void sendReaction(String messageId, ReactionState reactionState) {
        DatabaseReference databaseRefCurrentUser =
                rootReference.child(NodeNames.MESSAGES).child(currentUserId).child(talkUserId).child(messageId);
        databaseRefCurrentUser.get().addOnSuccessListener(dataSnapshot -> {
            MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
            int flagSetValue = reactionState.getFlagSetValue(Objects.requireNonNull(messageModel).getReactionStatus());
            databaseRefCurrentUser.child(NodeNames.REACTION_STATUS).setValue(flagSetValue).addOnSuccessListener(unused -> {
                DatabaseReference databaseRefTalkUser =
                        rootReference.child(NodeNames.MESSAGES).child(talkUserId).child(currentUserId).child(messageId);
                databaseRefTalkUser.get().addOnSuccessListener(dataSnapshot1 -> {
                    MessageModel messageModel1 = dataSnapshot1.getValue(MessageModel.class);
                    int flagSetValue1 =
                            reactionState.getFlagSetValue(Objects.requireNonNull(messageModel1).getReactionStatus());
                    databaseRefTalkUser.child(NodeNames.REACTION_STATUS).setValue(flagSetValue1)
                            .addOnFailureListener(e -> Log.e(Constants.TAG, e.getMessage()))
                            .addOnSuccessListener(unused1 -> {
                                loadMessages();
                            });
                });
            });
        });
    }


    public void generateReactionDialog(String selectedMessageId) {
        DialogFragment dialogFragment = new ReactionFragment();
        Bundle args = new Bundle();
        args.putString(Extras.MESSAGE_ID, selectedMessageId);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), ReactionFragment.DIALOG_TAG);
    }

    @Override
    public void onBackPressed() {
        rootReference.child(NodeNames.TALK).child(currentUserId).child(talkUserId).child(NodeNames.UNREAD_COUNT).setValue(0);
        super.onBackPressed();
    }
}