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

public class ExpenseListFragment extends Fragment
        implements ExpenseAdapter.OnExpenseClickListener {

    private RecyclerView recyclerView;
    private Button btnAddExpense;
    private ArrayList<Expense> expenses;

    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Inflate the fragment view
        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);

        // Check if views are found
        if (recyclerView == null || btnAddExpense == null) {
            android.util.Log.e("ExpenseListFragment", "Critical views not found");
            return view;
        }

        // Set up RecyclerView with a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Load the expenses from the ExpenseStorage
        expenses = ExpenseStorage.loadExpenses(requireContext());
        if (expenses == null) {
            expenses = new ArrayList<>();
        }

        // Set the adapter with this fragment as the listener
        adapter = new ExpenseAdapter(expenses, this);
        recyclerView.setAdapter(adapter);

        // ================= AJOUT DÉPENSE =================
        btnAddExpense.setOnClickListener(v -> {
            // Navigate to AddExpenseFragment when Add Expense button is clicked
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new AddExpenseFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Hide the back button on this list fragment and set title
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.showBack(false);
        mainActivity.setToolbarTitle("Pocket Manager");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list when returning to this fragment
        refreshExpenseList();
    }

    private void refreshExpenseList() {
        if (recyclerView != null) {
            // Reload expenses from storage
            expenses = ExpenseStorage.loadExpenses(requireContext());
            if (expenses == null) {
                expenses = new ArrayList<>();
            }
            
            // Update the adapter with new data
            if (adapter != null) {
                adapter.updateExpenses(expenses);
            } else {
                adapter = new ExpenseAdapter(expenses, this);
                recyclerView.setAdapter(adapter);
            }
        }
    }

    // ================= CLICK SUR UNE DÉPENSE =================
    @Override
    public void onExpenseClick(int position) {
        // When an expense is clicked, navigate to the ViewExpenseFragment
        ViewExpenseFragment fragment = ViewExpenseFragment.newInstance(position);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }
}
