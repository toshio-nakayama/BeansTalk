package com.gmail.h1990.toshio.beanstalk.profile;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.STATUS_MESSAGE;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.util.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileHomeFragment extends Fragment implements MenuProvider {
    private TextView tvName;
    private TextView tvStatusMessage;

    private ImageView ivProfile;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private Button btLogout;

    private Uri localFileUri;
    OnLogoutBtnClickListener callback;
    private static final String DIALOG_TAG = "message_display_fragment";


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
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tv_name);
        tvStatusMessage = view.findViewById(R.id.tv_status_message);
        ivProfile = view.findViewById(R.id.iv_profile);
        btLogout = view.findViewById(R.id.bt_logout);
        btLogout.setOnClickListener(view1 -> {
            if (callback != null) {
                callback.onLogoutBtnClick();
            }
        });
        GlideUtils.setPhoto(getContext(), currentUser.getPhotoUrl(), ivProfile);
        tvName.setText(currentUser.getDisplayName());
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        setupStatusMessage();
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
        //DEBUG
        tvStatusMessage.setText("test message . test message . test message . test message . test message . ");

        tvStatusMessage.setEms(12);
        tvStatusMessage.setSingleLine(true);
        tvStatusMessage.setSelected(true);
        tvStatusMessage.setEllipsize(TextUtils.TruncateAt.END);
        if (tvStatusMessage.length() >= 0) {
            tvStatusMessage.setOnClickListener(view -> {
                DialogFragment dialogFragment = new MessageDisplayFragment();
                Bundle args = new Bundle();
                String message = tvStatusMessage.getText().toString();
                args.putString(STATUS_MESSAGE, message);
                dialogFragment.setArguments(args);
                dialogFragment.show(getParentFragmentManager(), DIALOG_TAG);
            });
        }
    }
}