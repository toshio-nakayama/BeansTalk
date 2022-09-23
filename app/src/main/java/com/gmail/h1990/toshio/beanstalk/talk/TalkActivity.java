package com.gmail.h1990.toshio.beanstalk.talk;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.MESSAGE_TYPE_TEXT;
import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.PHOTO_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.MESSAGE;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.MESSAGES;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.MESSAGE_FROM;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.MESSAGE_ID;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.MESSAGE_TIME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.MESSAGE_TYPE;
import static com.gmail.h1990.toshio.beanstalk.reaction.ReactionFragment.DIALOG_TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.changecolor.ColorUtil;
import com.gmail.h1990.toshio.beanstalk.model.MessageModel;
import com.gmail.h1990.toshio.beanstalk.reaction.ReactionFragment;
import com.gmail.h1990.toshio.beanstalk.util.ConnectivityCheck;
import com.gmail.h1990.toshio.beanstalk.util.Validation;
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

public class TalkActivity extends AppCompatActivity implements View.OnClickListener, MessagesAdapter.AdapterCallback {

    private EditText etMessage;
    private DatabaseReference rootReference;
    private String currentUserId, talkUserId;
    private Validation validation;
    private RecyclerView rvMessages;
    private SwipeRefreshLayout srlMessages;
    private MessagesAdapter messagesAdapter;
    private List<MessageModel> messageList;
    private int currentPage = 1;
    private static final int RECORD_PER_PAGE = 30;
    private ChildEventListener childEventListener;
    private String talkUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorUtil.setTheme(this);
        setContentView(R.layout.activity_talk);

        ImageView ivSend = findViewById(R.id.iv_send);
        etMessage = findViewById(R.id.et_message);
        rvMessages = findViewById(R.id.rv_messages);
        srlMessages = findViewById(R.id.srl_messages);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        Validator validator = new Validator(this);
        validation = new Validation(validator);
        validator.setValidationListener(validation);
        if (getIntent().hasExtra(USER_KEY)) {
            talkUserId = getIntent().getStringExtra(USER_KEY);
        }
        if (getIntent().hasExtra(USER_NAME)) {
            talkUserName = getIntent().getStringExtra(USER_NAME);
        }
        if (getIntent().hasExtra(PHOTO_NAME)) {
            String talkUserPhotoName = getIntent().getStringExtra(PHOTO_NAME);
        }
        ivSend.setOnClickListener(this);
        messageList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messageList);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messagesAdapter);
        loadMessages();
        rvMessages.scrollToPosition(messageList.size() - 1);
        srlMessages.setOnRefreshListener(() -> {
            currentPage++;
            loadMessages();
        });
        setupToolBar(talkUserName);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(String msg, String msgType, String pushId) {
        try {
            if (!msg.equals("")) {
                HashMap messageMap = new HashMap();
                messageMap.put(MESSAGE_ID, pushId);
                messageMap.put(MESSAGE, msg);
                messageMap.put(MESSAGE_TYPE, msgType);
                messageMap.put(MESSAGE_FROM, currentUserId);
                messageMap.put(MESSAGE_TIME, ServerValue.TIMESTAMP);
                String currentUserReference = MESSAGES + "/" + currentUserId + "/" + talkUserId;
                String talkUserReference = MESSAGES + "/" + talkUserId + "/" + currentUserId;
                HashMap messageUserMap = new HashMap();
                messageUserMap.put(currentUserReference + "/" + pushId, messageMap);
                messageUserMap.put(talkUserReference + "/" + pushId, messageMap);
                etMessage.getEditableText().clear();
                rootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            Log.e(TAG, getString(R.string.failed_to_send_message) + error.getMessage());
                        } else {
                            Log.d(TAG, getString(R.string.message_sent_successfully));
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.failed_to_send_message) + e.getMessage());
        }
    }

    private void loadMessages() {
        messageList.clear();
        DatabaseReference databaseReferenceMessages = rootReference.child(MESSAGES).child(currentUserId).child(talkUserId);
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
                rvMessages.scrollToPosition(messageList.size() - 1);
                srlMessages.setRefreshing(false);

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
                srlMessages.setRefreshing(false);
            }
        };
        messageQuery.addChildEventListener(childEventListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                if (ConnectivityCheck.connectionAvailable(this)) {
                    DatabaseReference userMessagePush =
                            rootReference.child(MESSAGES).child(currentUserId).child(talkUserId).push();
                    String pushId = userMessagePush.getKey();
                    sendMessage(validation.trimAndNormalize(etMessage.getText().toString()),
                            MESSAGE_TYPE_TEXT, pushId);
                } else {
                    Toast toast = Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                break;
        }
    }

    @Override
    public void onMethodCallback() {
        showDialog();
    }

    private void showDialog() {
        DialogFragment dialogFragment = new ReactionFragment();
        dialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
    }
}