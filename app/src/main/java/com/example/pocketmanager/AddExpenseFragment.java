package com.example.pocketmanager;

import android.Manifest;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class AddExpenseFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    private EditText etTitle, etAmount;
    private ImageView imgReceipt;
    private Button btnTakePhoto, btnAnalyzeReceipt, btnSaveExpense;

    private Bitmap receiptBitmap;
    
    // Activity result launcher for camera
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize camera launcher - must be called before onCreateView
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                            Bundle extras = result.getData().getExtras();
                            if (extras != null) {
                                receiptBitmap = (Bitmap) extras.get("data");
                                if (receiptBitmap != null && imgReceipt != null) {
                                    imgReceipt.setImageBitmap(receiptBitmap);
                                }
                            }
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        imgReceipt = view.findViewById(R.id.imgReceipt);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnAnalyzeReceipt = view.findViewById(R.id.btnAnalyzeReceipt);
        btnSaveExpense = view.findViewById(R.id.btnSaveExpense);

        btnTakePhoto.setOnClickListener(v -> openCamera());

        btnAnalyzeReceipt.setOnClickListener(v -> {
            if (receiptBitmap == null) {
                Toast.makeText(requireContext(), "Prenez une photo du reçu", Toast.LENGTH_SHORT).show();
                return;
            }
            analyzeReceiptWithOCR(receiptBitmap);
        });

        btnSaveExpense.setOnClickListener(v -> saveExpense());

        // Show back button and update toolbar title
        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.showBack(true);
            mainActivity.setToolbarTitle("Ajouter une dépense");
        }

        return view;
    }

    // ================= CAMERA =================

    private void openCamera() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        // Check if camera is available
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(requireContext(), "Aucune application caméra disponible", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Permission caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ================= OCR =================

    private void analyzeReceiptWithOCR(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(result -> {
                    String receiptText = result.getText();

                    if (receiptText.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Aucun texte détecté",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Populating the extracted text into the fields (you can further process as needed)
                    extractExpenseDetails(receiptText);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Erreur OCR",
                                Toast.LENGTH_SHORT).show()
                );
    }

    // Extract the title and amount from OCR result (you can implement your own logic here)
    private void extractExpenseDetails(String receiptText) {
        String[] lines = receiptText.split("\n");

        // Assuming the first line is the title and second line is the amount
        if (lines.length > 1) {
            etTitle.setText(lines[0]);
            try {
                double amount = Double.parseDouble(lines[1].replaceAll("[^\\d.]", "")); // extracting amount
                etAmount.setText(String.valueOf(amount));
            } catch (NumberFormatException e) {
                etAmount.setText("Invalid amount");
            }
        } else {
            etTitle.setText(receiptText); // Set all the OCR text if it’s just one line
        }
    }

    // ================= SAVE =================

    private void saveExpense() {
        String title = etTitle.getText().toString();
        String amountStr = etAmount.getText().toString();

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Champs obligatoires",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String imageBase64 = ImageUtils.bitmapToBase64(receiptBitmap);

        Expense expense = new Expense(title, amount, imageBase64);
        ExpenseStorage.addExpense(requireContext(), expense);

        Toast.makeText(requireContext(),
                "Dépense enregistrée",
                Toast.LENGTH_SHORT).show();

        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
