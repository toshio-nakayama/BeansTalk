package com.gmail.h1990.toshio.beanstalk.util;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TIME_STAMP;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.UNREAD_COUNT;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

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
                databaseRootRef.child(TALK).child(talkUserId).child(currentUserId);
        databaseTalkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentCount = "0";
                if (snapshot.child(NodeNames.UNREAD_COUNT).getValue() != null) {
                    currentCount = snapshot.child(UNREAD_COUNT).getValue().toString();
                }
                Map<String, Object> talkMap = new HashMap<>();
                talkMap.put(TIME_STAMP, ServerValue.TIMESTAMP);
                talkMap.put(UNREAD_COUNT, Integer.valueOf(currentCount) + 1);
                Map<String, Object> talkUserMap = new HashMap<>();
                talkUserMap.put(TALK + "/" + talkUserId + "/" + currentUserId, talkMap);
                databaseRootRef.updateChildren(talkUserMap, (error, ref) -> {
                    if (error != null) {
                        Log.e(TAG, error.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });

    }
}
