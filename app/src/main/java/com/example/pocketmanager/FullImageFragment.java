package com.example.pocketmanager;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class FullImageFragment extends Fragment {

    private static final String ARG_IMAGE_PATH = "image_path";

    public static FullImageFragment newInstance(String imagePath) {
        FullImageFragment fragment = new FullImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_full_image, container, false);

        ImageView imgFull = view.findViewById(R.id.imgFullScreen);
        ImageButton btnClose = view.findViewById(R.id.btnClose);

        String imagePath = getArguments() != null
                ? getArguments().getString(ARG_IMAGE_PATH)
                : null;

        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                imgFull.setImageURI(Uri.fromFile(imgFile));
            }
        }

        btnClose.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack()
        );

        return view;
    }
}
