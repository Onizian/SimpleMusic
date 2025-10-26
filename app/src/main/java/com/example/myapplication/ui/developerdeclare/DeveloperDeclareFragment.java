package com.example.myapplication.ui.developerdeclare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.databinding.FragmentDeveloperdeclareBinding;

public class DeveloperDeclareFragment extends Fragment {

    private FragmentDeveloperdeclareBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DeveloperDeclareViewModel developerDeclareViewModel =
                new ViewModelProvider(this).get(DeveloperDeclareViewModel.class);

        binding = FragmentDeveloperdeclareBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDeveloperdeclare;
        developerDeclareViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
