package com.gmail.h1990.toshio.beanstalk.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.common.Constants;
import com.gmail.h1990.toshio.beanstalk.common.Extras;
import com.gmail.h1990.toshio.beanstalk.common.NodeNames;
import com.gmail.h1990.toshio.beanstalk.databinding.FragmentProfileHomeBinding;
import com.gmail.h1990.toshio.beanstalk.model.UserModel;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ProfileHomeFragment extends Fragment implements MenuProvider {
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceUser;
    private StorageReference storageRootRef;
    OnLogoutBtnClickListener callback;
    private FragmentProfileHomeBinding binding;


    public ProfileHomeFragment() {
    }

    public interface OnLogoutBtnClickListener {
        public void onLogoutBtnClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLogoutBtnClickListener) {
            callback = (OnLogoutBtnClickListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebase();
    }

    private void setupFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
        storageRootRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupStatusMessage();
        setProfile();
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        binding.ibLogout.setOnClickListener(view1 -> {
            if (callback != null) {
                callback.onLogoutBtnClick();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_settings) {
            Navigation.findNavController(requireView()).navigate(R.id.profileEditFragment);
        } else {
            return false;
        }
        return true;
    }

    private void setupStatusMessage() {
        binding.tvStatusMessage.setEms(12);
        binding.tvStatusMessage.setSingleLine(true);
        binding.tvStatusMessage.setSelected(true);
        binding.tvStatusMessage.setEllipsize(TextUtils.TruncateAt.END);
        binding.tvStatusMessage.setOnClickListener(view -> {
            if (binding.tvStatusMessage.length() > 0) {
                DialogFragment dialogFragment = new MessageDisplayFragment();
                Bundle args = new Bundle();
                String message = binding.tvStatusMessage.getText().toString();
                args.putString(Extras.STATUS_MESSAGE, message);
                dialogFragment.setArguments(args);
                dialogFragment.show(getParentFragmentManager(), MessageDisplayFragment.DIALOG_TAG);
            }
        });
    }

    private void setProfile() {
        databaseReferenceUser.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                GlideUtils.setPhoto(getContext(), currentUser.getPhotoUrl(), R.drawable.default_profile, binding.ivProfile);
                binding.tvName.setText(userModel.getName());
                binding.tvStatusMessage.setText(Objects.requireNonNull(userModel).getStatusMessage());
                setBackground();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setBackground() {
        String photo = currentUser.getUid() + Constants.EXT_JPG;
        StorageReference fileRef =
                storageRootRef.child(Constants.IMAGES_FOLDER).child(NodeNames.BACKGROUND_PHOTO).child(photo);
        fileRef.getDownloadUrl()
                .addOnFailureListener(e -> Log.e(Constants.TAG, getString(R.string.failed_to_download_uri)))
                .addOnSuccessListener(
                        uri -> GlideUtils.setPhoto(getContext(), uri, R.drawable.default_background, binding.ivBackgroundPhoto));
    }
}