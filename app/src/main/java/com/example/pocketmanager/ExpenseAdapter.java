package com.example.pocketmanager;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenses;
    private final OnExpenseClickListener listener;

    // ================= INTERFACE =================
    public interface OnExpenseClickListener {
        void onExpenseClick(int position);
    }

    public ExpenseAdapter(List<Expense> expenses, OnExpenseClickListener listener) {
        this.expenses = expenses;
        this.listener = listener;
    }

    // Method to update the expenses list
    public void updateExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses != null ? newExpenses : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    // ================= VIEW HOLDER =================
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAmount;
        ImageView imgReceipt;

        ViewHolder(@NonNull View itemView, OnExpenseClickListener listener) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            imgReceipt = itemView.findViewById(R.id.imgReceipt);

            // Set default image if ImageView exists
            if (imgReceipt != null) {
                imgReceipt.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onExpenseClick(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);

        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Expense expense = expenses.get(position);
        if (expense != null) {
            if (holder.tvTitle != null) {
                holder.tvTitle.setText(expense.getTitle() != null ? expense.getTitle() : "");
            }
            if (holder.tvAmount != null) {
                holder.tvAmount.setText(expense.getAmount() + " DT");
            }

            // Check if the image base64 string is available and convert to bitmap
            if (holder.imgReceipt != null) {
                if (expense.getImageBase64() != null && !expense.getImageBase64().isEmpty()) {
                    Bitmap bitmap = ImageUtils.base64ToBitmap(expense.getImageBase64());
                    if (bitmap != null) {
                        holder.imgReceipt.setImageBitmap(bitmap);
                    } else {
                        holder.imgReceipt.setImageResource(android.R.drawable.ic_menu_report_image);  // Fallback image
                    }
                } else {
                    holder.imgReceipt.setImageResource(android.R.drawable.ic_menu_report_image);  // Fallback if no image
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }
}
