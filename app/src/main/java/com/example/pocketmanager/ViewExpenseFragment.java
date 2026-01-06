package com.example.pocketmanager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewExpenseFragment extends Fragment {

    private static final String ARG_INDEX = "index";

    private int expenseIndex;
    private Expense expense;

    private EditText etTitle, etAmount;
    private ImageView imgReceipt;
    private Button btnUpdate, btnDelete, btnConvert;
    private Spinner spinnerCurrency;
    private TextView tvConverted;
    
    private ExecutorService executorService;
    private Handler mainHandler;

    public static ViewExpenseFragment newInstance(int index) {
        ViewExpenseFragment fragment = new ViewExpenseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_expense, container, false);

        etTitle = view.findViewById(R.id.etViewTitle);
        etAmount = view.findViewById(R.id.etViewAmount);
        imgReceipt = view.findViewById(R.id.imgViewReceipt);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnConvert = view.findViewById(R.id.btnConvert);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency);
        tvConverted = view.findViewById(R.id.tvConverted);
        
        // Initialize executor and handler for background tasks
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        if (getArguments() != null) {
            expenseIndex = getArguments().getInt(ARG_INDEX);
            ArrayList<Expense> expenses = ExpenseStorage.loadExpenses(requireContext());
            if (expenseIndex >= 0 && expenseIndex < expenses.size()) {
                expense = expenses.get(expenseIndex);
            }
        }

        // Check if expense is null to prevent crash
        if (expense == null) {
            Toast.makeText(requireContext(), "Dépense introuvable", Toast.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
            return view;
        }

        // Show back button in toolbar and update title
        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.showBack(true);
            mainActivity.setToolbarTitle("Détails de la dépense");
        }

        // ================= DISPLAY =================
        etTitle.setText(expense.getTitle());
        etAmount.setText(String.valueOf(expense.getAmount()));
        
        // Make sure EditTexts are enabled and editable
        etTitle.setEnabled(true);
        etTitle.setFocusable(true);
        etTitle.setFocusableInTouchMode(true);
        etAmount.setEnabled(true);
        etAmount.setFocusable(true);
        etAmount.setFocusableInTouchMode(true);

        // Decoding the Base64 string and setting the image
        if (expense.getImageBase64() != null && !expense.getImageBase64().isEmpty()) {
            Bitmap bitmap = base64ToBitmap(expense.getImageBase64());
            if (bitmap != null) {
                imgReceipt.setImageBitmap(bitmap);
            }
        }

        // ================= CURRENCY CONVERSION SETUP =================
        setupCurrencyConversion();

        // ================= UPDATE =================
        btnUpdate.setOnClickListener(v -> {

            String newTitle = etTitle.getText().toString().trim();
            String newAmountStr = etAmount.getText().toString().trim();

            if (newTitle.isEmpty() || newAmountStr.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Champs obligatoires",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double newAmount = Double.parseDouble(newAmountStr);
                
                if (newAmount < 0) {
                    Toast.makeText(requireContext(),
                            "Le montant doit être positif",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Expense updatedExpense = new Expense(
                        newTitle,
                        newAmount,
                        expense.getImageBase64() != null ? expense.getImageBase64() : "" // Keep the same image Base64 string
                );

                ExpenseStorage.updateExpense(requireContext(), expenseIndex, updatedExpense);

                Toast.makeText(requireContext(),
                        "Dépense modifiée",
                        Toast.LENGTH_SHORT).show();

                // Go back to refresh the list
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(),
                        "Montant invalide",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(),
                        "Erreur lors de la modification",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        // ================= DELETE =================
        btnDelete.setOnClickListener(v -> {

            ExpenseStorage.deleteExpense(requireContext(), expenseIndex);

            Toast.makeText(requireContext(),
                    "Dépense supprimée",
                    Toast.LENGTH_SHORT).show();

            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return view;
    }

    // ================= CURRENCY CONVERSION =================
    
    private void setupCurrencyConversion() {
        // Setup spinner with currency options
        String[] currencies = {"USD", "EUR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
        
        // Initialize converted amount text
        tvConverted.setText("Montant converti : -");
        
        // Convert button click listener
        btnConvert.setOnClickListener(v -> convertCurrency());
    }
    
    private void convertCurrency() {
        String amountStr = etAmount.getText().toString().trim();
        
        if (amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez entrer un montant", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountStr);
            String selectedCurrency = (String) spinnerCurrency.getSelectedItem();
            
            if (selectedCurrency == null) {
                Toast.makeText(requireContext(), "Veuillez sélectionner une devise", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Disable button during conversion
            btnConvert.setEnabled(false);
            btnConvert.setText("Conversion...");
            tvConverted.setText("Conversion en cours...");
            
            // Perform conversion on background thread
            executorService.execute(() -> {
                try {
                    // TND to selected currency
                    double rate = ExchangeRateApi.getRate("TND", selectedCurrency);
                    double convertedAmount = amount * rate;
                    
                    // Update UI on main thread
                    mainHandler.post(() -> {
                        String result = String.format("%.2f %s", convertedAmount, selectedCurrency);
                        tvConverted.setText("Montant converti : " + result);
                        btnConvert.setEnabled(true);
                        btnConvert.setText("💱 Convertir");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // Update UI on main thread with error
                    final String errorMessage = e.getMessage() != null ? e.getMessage() : "Erreur inconnue";
                    mainHandler.post(() -> {
                        tvConverted.setText("Erreur de conversion");
                        String userMessage = "Erreur lors de la conversion";
                        if (errorMessage.contains("404") || errorMessage.contains("not found")) {
                            userMessage = "Service de conversion non disponible. Vérifiez votre connexion internet.";
                        } else if (errorMessage.contains("timeout") || errorMessage.contains("Timeout")) {
                            userMessage = "Délai d'attente dépassé. Réessayez plus tard.";
                        } else if (errorMessage.contains("HTTP error")) {
                            userMessage = "Erreur de connexion au serveur de conversion.";
                        }
                        Toast.makeText(requireContext(), userMessage, Toast.LENGTH_LONG).show();
                        btnConvert.setEnabled(true);
                        btnConvert.setText("💱 Convertir");
                    });
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Montant invalide", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Shutdown executor when fragment is destroyed
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // ================= CONVERT BASE64 TO BITMAP =================
    private Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedString = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
            return android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
