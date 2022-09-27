package com.gmail.h1990.toshio.beanstalk.home;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.gmail.h1990.toshio.beanstalk.databinding.FragmentHomeBinding;
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
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceTalk, databaseReferenceUser;
    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebase();
    }

    private void setupFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(USERS);
        databaseReferenceTalk = FirebaseDatabase.getInstance().getReference().child(TALK);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout llFriendList = view.findViewById(R.id.ll_friend_list);
        llFriendList.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.friendFragment);
        });
        setFriendsCount();
        GlideUtils.setPhoto(getContext(), currentUser.getPhotoUrl(), R.drawable.default_profile,
                binding.ivProfile);
        binding.tvName.setText(currentUser.getDisplayName());
        binding.ivProfile.setOnTouchListener(HomeFragment.this);
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

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
                binding.tvFriendsCount.setText(count);
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
                    validateOnId(userId);
                }
            });

    private void validateOnId(String id) {
        String currentUserId = currentUser.getUid();
        if (!id.equals(currentUserId)) {
            databaseReferenceTalk.child(currentUserId).child(id).get().addOnFailureListener(e -> {
                Log.e(TAG, getString(R.string.failed_to_get_data));
            }).addOnSuccessListener(dataSnapshot -> {
                if (!dataSnapshot.exists()) {
                    databaseReferenceUser.child(id).get().addOnFailureListener(e -> {
                        Log.e(TAG, getString(R.string.failed_to_get_data));
                    }).addOnSuccessListener(dataSnapshot1 -> {
                        if (dataSnapshot1.exists()) {
                            Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                            intent.putExtra(USER_KEY, id);
                            startActivity(intent);
                        }
                    });
                }
            });

        }
    }
}