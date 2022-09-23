package com.gmail.h1990.toshio.beanstalk.home;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.addfriend.AddFriendActivity;
import com.gmail.h1990.toshio.beanstalk.profile.ProfileActivity;
import com.gmail.h1990.toshio.beanstalk.qrcode.QRScanActivity;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class HomeFragment extends Fragment implements View.OnTouchListener, MenuProvider {
    private TextView tvFriendsCount, tvName;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceTalk;
    private ImageView ivProfile;

    public HomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceTalk = FirebaseDatabase.getInstance().getReference().child(TALK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tv_name);
        tvFriendsCount = view.findViewById(R.id.tv_friends_count);
        ivProfile = view.findViewById(R.id.iv_profile);
        LinearLayout llFriendList = view.findViewById(R.id.ll_friend_list);
        llFriendList.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.friendFragment);
        });
        setFriendsCount();
        GlideUtils.setPhoto(getContext(), currentUser.getPhotoUrl(), R.drawable.default_profile, ivProfile);
        tvName.setText(currentUser.getDisplayName());
        ivProfile.setOnTouchListener(HomeFragment.this);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    private void setFriendsCount() {
        databaseReferenceTalk.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count = String.valueOf(snapshot.getChildrenCount());
                tvFriendsCount.setText(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void launchQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(false);
        options.setBeepEnabled(false);
        options.setCaptureActivity(QRScanActivity.class);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String userId = result.getContents();
                    Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                    intent.putExtra(USER_KEY, userId);
                    startActivity(intent);
                }
            });


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_home, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_qr_scan) {
            launchQRScanner();
        } else {
            return false;
        }
        return true;
    }
}