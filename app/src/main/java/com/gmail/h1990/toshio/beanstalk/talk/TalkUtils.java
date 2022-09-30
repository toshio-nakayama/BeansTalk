package com.gmail.h1990.toshio.beanstalk.talk;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gmail.h1990.toshio.beanstalk.common.Constants;
import com.gmail.h1990.toshio.beanstalk.common.NodeNames;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TalkUtils {
    public static void updateTalkDetails(Context context, String currentUserId, String talkUserId) {
        final DatabaseReference databaseRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseTalkRef =
                databaseRootRef.child(NodeNames.TALK).child(talkUserId).child(currentUserId);
        databaseTalkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentCount = "0";
                if (snapshot.child(NodeNames.UNREAD_COUNT).getValue() != null) {
                    currentCount = snapshot.child(NodeNames.UNREAD_COUNT).getValue().toString();
                }
                Map<String, Object> talkMap = new HashMap<>();
                talkMap.put(NodeNames.TIME_STAMP, ServerValue.TIMESTAMP);
                talkMap.put(NodeNames.UNREAD_COUNT, Integer.valueOf(currentCount) + 1);
                Map<String, Object> talkUserMap = new HashMap<>();
                talkUserMap.put(NodeNames.TALK + "/" + talkUserId + "/" + currentUserId, talkMap);
                databaseRootRef.updateChildren(talkUserMap, (error, ref) -> {
                    if (error != null) {
                        Log.e(Constants.TAG, error.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(Constants.TAG, error.getMessage());
            }
        });

    }
}
