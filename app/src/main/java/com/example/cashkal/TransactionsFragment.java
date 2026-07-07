package com.example.cashkal;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashkal.databinding.FragmentTransactionsBinding;
import com.example.cashkal.model.Expense;
import com.example.cashkal.model.Income;
import com.example.cashkal.model.TransactionItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TransactionsFragment extends Fragment {

    private FragmentTransactionsBinding binding;
    private TransactionAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private ListenerRegistration expensesListener;
    private ListenerRegistration incomesListener;

    private final List<TransactionItem> expenseItems = new ArrayList<>();
    private final List<TransactionItem> incomeItems = new ArrayList<>();
    private final Set<String> selectedCategories = new HashSet<>();
    private final List<String> allCategories = new ArrayList<>();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public TransactionsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        adapter = new TransactionAdapter(requireContext());
        binding.lvTransactions.setAdapter(adapter);

        buildCategoryList();
        setupFilters();
        listenToTransactions();
    }

    private void buildCategoryList() {
        LinkedHashSet<String> categories = new LinkedHashSet<>();

        Collections.addAll(categories, getResources().getStringArray(R.array.expense_categories));
        Collections.addAll(categories, getResources().getStringArray(R.array.income_categories));

        allCategories.clear();
        allCategories.addAll(categories);
    }

    private void setupFilters() {
        binding.rgTypeFilter.setOnCheckedChangeListener((group, checkedId) -> applyFiltersAndSort());

        binding.spSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFiltersAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.btnChooseCategories.setOnClickListener(v -> showCategoryDialog());

        updateCategorySummaryText();
    }

    private void listenToTransactions() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(requireContext(), R.string.auth_error_not_logged_in, Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        expensesListener = firestore.collection("users")
                .document(uid)
                .collection("expenses")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(requireContext(), R.string.transactions_load_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    expenseItems.clear();

                    if (querySnapshot != null) {
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Expense expense = document.toObject(Expense.class);

                            if (expense != null) {
                                expenseItems.add(new TransactionItem(
                                        expense.getId(),
                                        TransactionItem.TYPE_EXPENSE,
                                        expense.getAmount(),
                                        expense.getDescription(),
                                        expense.getCategory(),
                                        dateFormat.format(expense.getDate()),
                                        expense.getDate(),
                                        expense.getNote()
                                ));
                            }
                        }
                    }

                    applyFiltersAndSort();
                });

        incomesListener = firestore.collection("users")
                .document(uid)
                .collection("incomes")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(requireContext(), R.string.transactions_load_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    incomeItems.clear();

                    if (querySnapshot != null) {
                        for (com.google.firebase.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Income income = document.toObject(Income.class);

                            if (income != null) {
                                incomeItems.add(new TransactionItem(
                                        income.getId(),
                                        TransactionItem.TYPE_INCOME,
                                        income.getAmount(),
                                        income.getDescription(),
                                        income.getCategory(),
                                        dateFormat.format(income.getDate()),
                                        income.getDate(),
                                        income.getNote()
                                ));
                            }
                        }
                    }

                    applyFiltersAndSort();
                });
    }

    private void applyFiltersAndSort() {
        List<TransactionItem> result = new ArrayList<>();

        boolean showExpenses = binding.rbShowBoth.isChecked() || binding.rbShowExpenses.isChecked();
        boolean showIncome = binding.rbShowBoth.isChecked() || binding.rbShowIncome.isChecked();

        if (showExpenses) {
            result.addAll(expenseItems);
        }

        if (showIncome) {
            result.addAll(incomeItems);
        }

        if (!selectedCategories.isEmpty()) {
            List<TransactionItem> filtered = new ArrayList<>();

            for (TransactionItem item : result) {
                if (selectedCategories.contains(item.getCategory())) {
                    filtered.add(item);
                }
            }

            result = filtered;
        }

        sortTransactions(result);

        adapter.setItems(result);

        boolean isEmpty = result.isEmpty();
        binding.tvEmptyTransactions.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.lvTransactions.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void sortTransactions(List<TransactionItem> result) {
        int sortPosition = binding.spSort.getSelectedItemPosition();

        if (sortPosition == 1) {
            result.sort((a, b) -> Long.compare(a.getDate(), b.getDate()));
        } else if (sortPosition == 2) {
            result.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
        } else if (sortPosition == 3) {
            result.sort((a, b) -> Double.compare(a.getAmount(), b.getAmount()));
        } else {
            result.sort((a, b) -> Long.compare(b.getDate(), a.getDate()));
        }
    }

    private void showCategoryDialog() {
        String[] categoriesArray = allCategories.toArray(new String[0]);
        boolean[] checkedItems = new boolean[categoriesArray.length];

        for (int i = 0; i < categoriesArray.length; i++) {
            checkedItems[i] = selectedCategories.contains(categoriesArray[i]);
        }

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.transactions_choose_categories)
                .setMultiChoiceItems(categoriesArray, checkedItems, (d, which, isChecked) -> {
                    if (isChecked) {
                        selectedCategories.add(categoriesArray[which]);
                    } else {
                        selectedCategories.remove(categoriesArray[which]);
                    }
                })
                .setPositiveButton(R.string.transactions_apply, (d, which) -> {
                    updateCategorySummaryText();
                    applyFiltersAndSort();
                })
                .setNeutralButton(R.string.transactions_all_categories, (d, which) -> {
                    selectedCategories.clear();
                    updateCategorySummaryText();
                    applyFiltersAndSort();
                })
                .create();

        dialog.getListView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        dialog.show();
    }

    private void updateCategorySummaryText() {
        if (selectedCategories.isEmpty()) {
            binding.tvSelectedCategories.setText("");
        } else if (selectedCategories.size() == 1) {
            binding.tvSelectedCategories.setText(selectedCategories.iterator().next());
        } else {
            binding.tvSelectedCategories.setText(
                    getString(R.string.transactions_categories_count, selectedCategories.size())
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (expensesListener != null) {
            expensesListener.remove();
            expensesListener = null;
        }

        if (incomesListener != null) {
            incomesListener.remove();
            incomesListener = null;
        }

        binding = null;
    }
}