package com.example.pocketmanager;

import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.util.ArrayList;

public class ViewExpenseFragment extends Fragment {

    private static final String ARG_INDEX = "index";
    private static final String ARG_TITLE = "title";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_IMAGE = "image";

    private EditText etTitle, etAmount;
    private ImageView imgReceipt;
    private Spinner spinnerCurrency;
    private Button btnConvert, btnUpdate, btnDelete;
    private TextView tvConverted;

    private int expenseIndex;
    private String imagePath;

    private boolean isEditing = false;

    // 🔹 Création du fragment avec index
    public static ViewExpenseFragment newInstance(Expense expense, int index) {
        ViewExpenseFragment fragment = new ViewExpenseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INDEX, index);
        args.putString(ARG_TITLE, expense.getTitle());
        args.putDouble(ARG_AMOUNT, expense.getAmount());
        args.putString(ARG_IMAGE, expense.getImagePath());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_expense, container, false);

        etTitle = view.findViewById(R.id.etViewTitle);
        etAmount = view.findViewById(R.id.etViewAmount);
        imgReceipt = view.findViewById(R.id.imgViewReceipt);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
        tvConverted = view.findViewById(R.id.tvConverted);

        setupSpinner();
        loadData();
        lockEditing();

        // 🔹 Conversion
        btnConvert.setOnClickListener(v -> convertAmount());

        // 🔹 Modifier / Enregistrer
        btnUpdate.setOnClickListener(v -> handleUpdate());

        // 🔹 Supprimer
        btnDelete.setOnClickListener(v -> deleteExpense());

        // 🔹 Clic image → plein écran
        imgReceipt.setOnClickListener(v -> {
            if (imagePath != null && !imagePath.isEmpty()) {
                FullImageFragment fragment =
                        FullImageFragment.newInstance(imagePath);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ((MainActivity) requireActivity()).showBack(true);

        return view;
    }

    // 🔒 Lecture seule
    private void lockEditing() {
        etTitle.setEnabled(false);
        etAmount.setEnabled(false);
        btnUpdate.setText("Modifier la dépense");
        isEditing = false;
    }

    // ✏️ Mode édition
    private void unlockEditing() {
        etTitle.setEnabled(true);
        etAmount.setEnabled(true);
        etTitle.requestFocus();
        btnUpdate.setText("Enregistrer les modifications");
        isEditing = true;
    }

    private void handleUpdate() {
        if (!isEditing) {
            unlockEditing();
        } else {
            updateExpense();
        }
    }

    private void setupSpinner() {
        String[] currencies = {"EUR", "USD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
    }

    private void loadData() {
        if (getArguments() == null) return;

        expenseIndex = getArguments().getInt(ARG_INDEX);
        imagePath = getArguments().getString(ARG_IMAGE);

        etTitle.setText(getArguments().getString(ARG_TITLE));
        etAmount.setText(String.valueOf(getArguments().getDouble(ARG_AMOUNT)));

        if (imagePath != null && !imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                imgReceipt.setImageURI(null);
                imgReceipt.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

    private void updateExpense() {

        ArrayList<Expense> expenses =
                ExpenseStorage.loadExpenses(requireContext());

        if (expenseIndex < 0 || expenseIndex >= expenses.size()) return;

        expenses.set(
                expenseIndex,
                new Expense(
                        etTitle.getText().toString(),
                        Double.parseDouble(etAmount.getText().toString()),
                        imagePath
                )
        );

        ExpenseStorage.saveExpenses(requireContext(), expenses);

        Toast.makeText(getContext(), "Dépense modifiée", Toast.LENGTH_SHORT).show();

        lockEditing();
    }

    private void deleteExpense() {

        ArrayList<Expense> expenses =
                ExpenseStorage.loadExpenses(requireContext());

        if (expenseIndex < 0 || expenseIndex >= expenses.size()) return;

        expenses.remove(expenseIndex);

        ExpenseStorage.saveExpenses(requireContext(), expenses);

        Toast.makeText(getContext(), "Dépense supprimée", Toast.LENGTH_SHORT).show();

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    // ✅ CORRECTION MAJEURE ICI
    private void convertAmount() {

        // 🔹 TOUJOURS lire la valeur ACTUELLE du champ
        double amountDT = Double.parseDouble(etAmount.getText().toString());

        String currency = spinnerCurrency.getSelectedItem().toString();
        double rateDtToEur = 1 / 3.3;

        if (currency.equals("EUR")) {
            tvConverted.setText(
                    String.format("Montant converti : %.2f EUR", amountDT * rateDtToEur)
            );
            return;
        }

        new Thread(() -> {
            try {
                double rate = ExchangeRateApi.getRate("EUR", "USD");
                double result = amountDT * rateDtToEur * rate;

                requireActivity().runOnUiThread(() ->
                        tvConverted.setText(
                                String.format("Montant converti : %.2f USD", result)
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
