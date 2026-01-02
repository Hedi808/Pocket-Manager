package com.example.pocketmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    public interface OnExpenseClickListener {
        void onExpenseClick(int position);
    }

    private final List<Expense> expenseList;
    private final OnExpenseClickListener listener;

    public ExpenseAdapter(List<Expense> expenseList, OnExpenseClickListener listener) {
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Expense expense = expenseList.get(position);

        holder.tvTitle.setText(expense.getTitle());
        holder.tvAmount.setText(expense.getAmount() + " DT");
        holder.imgReceipt.setImageBitmap(expense.getPhoto());

        holder.itemView.setOnClickListener(v ->
                listener.onExpenseClick(position)
        );
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAmount;
        ImageView imgReceipt;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvAmount = itemView.findViewById(R.id.tvItemAmount);
            imgReceipt = itemView.findViewById(R.id.imgItem);
        }
    }
}
