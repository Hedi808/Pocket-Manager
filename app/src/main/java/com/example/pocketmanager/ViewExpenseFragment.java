package com.example.pocketmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ViewExpenseFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private int position;
    private Expense expense;

    public static ViewExpenseFragment newInstance(int position) {
        ViewExpenseFragment fragment = new ViewExpenseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_expense, container, false);

        position = getArguments().getInt(ARG_POSITION);
        expense = ExpenseRepository.expenses.get(position);

        EditText etTitle = view.findViewById(R.id.etViewTitle);
        EditText etAmount = view.findViewById(R.id.etViewAmount);
        ImageView img = view.findViewById(R.id.imgViewReceipt);

        Button btnUpdate = view.findViewById(R.id.btnUpdate);
        Button btnDelete = view.findViewById(R.id.btnDelete);

        etTitle.setText(expense.getTitle());
        etAmount.setText(expense.getAmount());
        img.setImageBitmap(expense.getPhoto());

        btnUpdate.setOnClickListener(v -> {
            expense.setTitle(etTitle.getText().toString());
            expense.setAmount(etAmount.getText().toString());

            Toast.makeText(getContext(),
                    "Dépense modifiée",
                    Toast.LENGTH_SHORT).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        btnDelete.setOnClickListener(v -> {
            ExpenseRepository.expenses.remove(position);

            Toast.makeText(getContext(),
                    "Dépense supprimée",
                    Toast.LENGTH_SHORT).show();

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showBack(true);
    }
}
