package com.example.myapplication.ui.softversion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentSoftversionBinding;

public class SoftVersionFragment extends Fragment {

    private FragmentSoftversionBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        SoftVersionViewModel softVersionViewModel =
                new ViewModelProvider(this).get(SoftVersionViewModel.class);

        binding = FragmentSoftversionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSoftversion;
        softVersionViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
