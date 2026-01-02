package com.example.pocketmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class AddExpenseFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private EditText etTitle, etAmount;
    private ImageView imgReceipt;
    private Bitmap capturedPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        imgReceipt = view.findViewById(R.id.imgReceipt);

        Button btnCamera = view.findViewById(R.id.btnCamera);
        Button btnSave = view.findViewById(R.id.btnSave);

        btnCamera.setOnClickListener(v -> checkCameraPermission());
        btnSave.setOnClickListener(v -> saveExpense());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showBack(true);
    }

    // ================= CAMERA =================

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION
            );
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.getExtras() != null) {

            capturedPhoto = (Bitmap) data.getExtras().get("data");
            imgReceipt.setImageBitmap(capturedPhoto);
            imgReceipt.setVisibility(View.VISIBLE);
        }
    }

    // ================= SAVE =================

    private void saveExpense() {
        String title = etTitle.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();

        if (title.isEmpty() || amount.isEmpty() || capturedPhoto == null) {
            Toast.makeText(getContext(),
                    "Veuillez remplir tous les champs",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ExpenseRepository.expenses.add(
                new Expense(title, amount, capturedPhoto)
        );

        Toast.makeText(getContext(),
                "Dépense enregistrée",
                Toast.LENGTH_SHORT).show();

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }
}
