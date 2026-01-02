package com.example.pocketmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExpenseListFragment extends Fragment {

    private ExpenseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expense_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAdd);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpenseAdapter(
                ExpenseRepository.expenses,
                position -> ((MainActivity) requireActivity())
                        .loadFragment(ViewExpenseFragment.newInstance(position))
        );

        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v ->
                ((MainActivity) requireActivity())
                        .loadFragment(new AddExpenseFragment())
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        ((MainActivity) requireActivity()).showBack(false);
    }
}
