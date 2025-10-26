package com.example.myapplication.ui.useragree;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentUseragreeBinding;

public class UserAgreeFragment extends Fragment {

    private FragmentUseragreeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        UserAgreeViewModel userAgreeViewModel =
                new ViewModelProvider(this).get(UserAgreeViewModel.class);

        binding = FragmentUseragreeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textUseragree;
        userAgreeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
