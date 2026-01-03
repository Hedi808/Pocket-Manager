package com.example.pocketmanager;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;

public class ViewExpenseFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_IMAGE = "image";

    private EditText etTitle, etAmount;
    private ImageView imgReceipt;
    private Button btnUpdate, btnDelete;

    private String originalTitle;
    private String imagePath;

    // ✅ newInstance CORRECT
    public static ViewExpenseFragment newInstance(Expense expense) {
        ViewExpenseFragment fragment = new ViewExpenseFragment();
        Bundle args = new Bundle();
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
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);

        loadData();

        btnUpdate.setOnClickListener(v -> updateExpense());
        btnDelete.setOnClickListener(v -> deleteExpense());

        ((MainActivity) requireActivity()).showBack(true);

        return view;
    }

    private void loadData() {
        if (getArguments() == null) return;

        originalTitle = getArguments().getString(ARG_TITLE);
        imagePath = getArguments().getString(ARG_IMAGE);

        etTitle.setText(originalTitle);
        etAmount.setText(String.valueOf(getArguments().getDouble(ARG_AMOUNT)));

        if (imagePath != null && !imagePath.isEmpty()) {
            imgReceipt.setImageURI(Uri.fromFile(new File(imagePath)));
        }
    }

    private void updateExpense() {

        ArrayList<Expense> expenses =
                ExpenseStorage.loadExpenses(requireContext());

        for (Expense e : expenses) {
            if (e.getTitle().equals(originalTitle)) {
                expenses.remove(e);
                break;
            }
        }

        expenses.add(new Expense(
                etTitle.getText().toString(),
                Double.parseDouble(etAmount.getText().toString()),
                imagePath
        ));

        ExpenseStorage.saveExpenses(requireContext(), expenses);

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }

    private void deleteExpense() {

        ArrayList<Expense> expenses =
                ExpenseStorage.loadExpenses(requireContext());

        for (Expense e : expenses) {
            if (e.getTitle().equals(originalTitle)) {
                expenses.remove(e);
                break;
            }
        }

        ExpenseStorage.saveExpenses(requireContext(), expenses);

        requireActivity()
                .getSupportFragmentManager()
                .popBackStack();
    }
}
