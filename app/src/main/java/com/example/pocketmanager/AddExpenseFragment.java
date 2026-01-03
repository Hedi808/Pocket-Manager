package com.example.pocketmanager;

import android.app.Activity;
import android.content.Intent;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AddExpenseFragment extends Fragment {

    private EditText etTitle, etAmount;
    private ImageView imgReceipt;
    private Button btnSave, btnPhoto;

    private Bitmap receiptBitmap;
    private String imagePath = "";

    private ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                receiptBitmap = (Bitmap) extras.get("data");
                                imgReceipt.setImageBitmap(receiptBitmap);

                                // 🔥 Sauvegarde réelle dans le stockage interne
                                imagePath = saveImage(receiptBitmap);
                            }
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        imgReceipt = view.findViewById(R.id.imgReceipt);
        btnSave = view.findViewById(R.id.btnSave);
        btnPhoto = view.findViewById(R.id.btnPhoto);

        btnPhoto.setOnClickListener(v -> openCamera());
        btnSave.setOnClickListener(v -> saveExpense());

        ((MainActivity) requireActivity()).showBack(true);

        return view;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(getContext(), "Caméra non disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveExpense() {

        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        ArrayList<Expense> expenses =
                ExpenseStorage.loadExpenses(requireContext());

        expenses.add(new Expense(title, amount, imagePath));

        ExpenseStorage.saveExpenses(requireContext(), expenses);

        Toast.makeText(getContext(), "Dépense enregistrée", Toast.LENGTH_SHORT).show();

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    // ✅ Sauvegarde persistante de l'image
    private String saveImage(Bitmap bitmap) {
        try {
            File file = new File(
                    requireContext().getFilesDir(),
                    "receipt_" + System.currentTimeMillis() + ".png"
            );

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
