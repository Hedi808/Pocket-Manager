package com.example.pocketmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseListFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnAddExpense;
    private ArrayList<Expense> expenses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expenses = ExpenseStorage.loadExpenses(requireContext());

        ExpenseAdapter adapter = new ExpenseAdapter(
                expenses,
                expense -> {
                    int index = expenses.indexOf(expense);

                    ViewExpenseFragment fragment =
                            ViewExpenseFragment.newInstance(expense, index);

                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
        );

        recyclerView.setAdapter(adapter);

        btnAddExpense.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new AddExpenseFragment())
                    .addToBackStack(null)
                    .commit();
        });

        ((MainActivity) requireActivity()).showBack(false);

        return view;
    }
}
