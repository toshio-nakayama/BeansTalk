package com.gmail.h1990.toshio.beanstalk.home;

import static com.gmail.h1990.toshio.beanstalk.common.Constants.TAG;
import static com.gmail.h1990.toshio.beanstalk.common.Extras.USER_KEY;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.TALK;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.USERS;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
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
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
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

public class HomeFragment extends Fragment implements MenuProvider {
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceTalk, databaseReferenceUser, databaseReferenceCurrentUser;
    private ValueEventListener valueEventListener;
    private FragmentHomeBinding binding;
    private static final int MENU_POSITION = 0;
    private static final int SUBMENU_POSITION = 0;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebase();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout llFriendList = view.findViewById(R.id.ll_friend_list);
        llFriendList.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.friendFragment);
        });
        setFriendsCount();
        setProfile();
        binding.llProfile.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        });
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReferenceCurrentUser.removeEventListener(valueEventListener);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_home, menu);
        SubMenu subMenu = (SubMenu) menu.getItem(MENU_POSITION).getSubMenu();
        SpannableString s = new SpannableString(getString(R.string.qr_code));
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        subMenu.getItem(SUBMENU_POSITION).setTitle(s);
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

    private void setProfile() {
        binding.tvName.setText(currentUser.getDisplayName());
        databaseReferenceUser.child(currentUser.getUid()).get().addOnSuccessListener(dataSnapshot -> {
            UserModel userModel = dataSnapshot.getValue(UserModel.class);
            binding.tvStatusMessage.setText(userModel.getStatusMessage());
        });
        GlideUtils.setPhoto(getContext(), currentUser.getPhotoUrl(), R.drawable.default_profile,
                binding.ivProfile);
    }

    private void setupFirebase() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        final DatabaseReference databaseRootRef = FirebaseDatabase.getInstance().getReference();
        databaseReferenceUser = databaseRootRef.child(USERS);
        databaseReferenceTalk = databaseRootRef.child(TALK);
        databaseReferenceCurrentUser = databaseRootRef.child(USERS).child(currentUser.getUid());
    }

    private void setFriendsCount() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count = String.valueOf(snapshot.getChildrenCount());
                binding.tvFriendsCount.setText(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReferenceCurrentUser.addValueEventListener(valueEventListener);
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