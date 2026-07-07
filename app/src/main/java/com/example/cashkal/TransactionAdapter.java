package com.example.cashkal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.cashkal.databinding.ItemTransactionBinding;
import com.example.cashkal.model.TransactionItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends BaseAdapter {

    private final Context context;
    private final List<TransactionItem> items = new ArrayList<>();

    public TransactionAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<TransactionItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public TransactionItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemTransactionBinding binding;

        if (convertView == null) {
            binding = ItemTransactionBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            );
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemTransactionBinding) convertView.getTag();
        }

        TransactionItem item = getItem(position);

        String title = item.getDescription();
        if (title == null || title.trim().isEmpty()) {
            title = item.getCategory();
        }

        binding.tvTransactionTitle.setText(title);
        binding.tvTransactionCategory.setText(item.getCategory());
        binding.tvTransactionDate.setText(item.getDateText());

        if (item.isExpense()) {
            binding.tvTransactionType.setText(R.string.transactions_expense);
            binding.tvTransactionAmount.setText(
                    String.format(Locale.getDefault(), "-₪%.2f", item.getAmount())
            );
            binding.tvTransactionAmount.setTextColor(context.getColor(R.color.gold));
        } else {
            binding.tvTransactionType.setText(R.string.transactions_income);
            binding.tvTransactionAmount.setText(
                    String.format(Locale.getDefault(), "+₪%.2f", item.getAmount())
            );
            binding.tvTransactionAmount.setTextColor(context.getColor(R.color.growth_green));
        }

        return convertView;
    }
}