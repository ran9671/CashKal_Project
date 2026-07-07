package com.example.cashkal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashkal.model.Expense;
import com.example.cashkal.model.Income;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InsightsFragment extends Fragment {

    private LineChart chart;
    private TextView tvBalanceLeft, tvTopLabel, tvTopCategory, tvTopAmount;
    private double monthlyBudget = 0;

    private final int[] categoryColors = {
            Color.parseColor("#385255"),
            Color.parseColor("#E9CE8E"),
            Color.parseColor("#7EA98B"),
            Color.parseColor("#9DC1BF"),
            Color.parseColor("#6F8988")
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_insights, container, false);

        chart = root.findViewById(R.id.line_chart);
        tvBalanceLeft = root.findViewById(R.id.tv_balance_left);
        tvTopLabel = root.findViewById(R.id.tv_top_label);
        tvTopCategory = root.findViewById(R.id.tv_top_category);
        tvTopAmount = root.findViewById(R.id.tv_top_category_amount);

        setupChartAxes();
        loadBudget();

        RadioGroup rgType = root.findViewById(R.id.rg_type);
        rgType.setOnCheckedChangeListener((group, checkedId) ->
                loadData(checkedId == R.id.rb_expenses));

        loadData(true);
        return root;
    }

    private void setupChartAxes() {
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "₪" + (int) value;
            }
        });
        chart.getAxisRight().setEnabled(false);
        Description d = new Description();
        d.setText("");
        chart.setDescription(d);
    }

    private void loadBudget() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    Double b = doc.getDouble("monthlyBudget");
                    monthlyBudget = b != null ? b : 0;
                });
    }

    private long monthStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    private void loadData(boolean isExpenses) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String collection = isExpenses ? "expenses" : "incomes";

        FirebaseFirestore.getInstance()
                .collection("users").document(user.getUid())
                .collection(collection)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<float[]> rows = new ArrayList<>();
                    List<String> cats = new ArrayList<>();
                    long start = monthStart();

                    for (var doc : snapshot) {
                        long date;
                        double amount;
                        String category;
                        if (isExpenses) {
                            Expense e = doc.toObject(Expense.class);
                            date = e.getDate();
                            amount = e.getAmount();
                            category = e.getCategory();
                        } else {
                            Income in = doc.toObject(Income.class);
                            date = in.getDate();
                            amount = in.getAmount();
                            category = in.getCategory();
                        }
                        if (date < start) continue;
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(date);
                        rows.add(new float[]{c.get(Calendar.DAY_OF_MONTH), (float) amount});
                        cats.add(category != null ? category : "אחר");
                    }

                    buildCategoryLines(rows, cats);
                    buildTopCategory(rows, cats, isExpenses);
                    buildBalance(rows, isExpenses);
                });
    }

    private void buildCategoryLines(List<float[]> rows, List<String> cats) {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        Map<String, Map<Integer, Float>> perCatPerDay = new LinkedHashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            String cat = cats.get(i);
            int day = (int) rows.get(i)[0];
            float amt = rows.get(i)[1];
            perCatPerDay.putIfAbsent(cat, new HashMap<>());
            Map<Integer, Float> dayMap = perCatPerDay.get(cat);
            dayMap.put(day, dayMap.getOrDefault(day, 0f) + amt);
        }

        List<ILineDataSet> dataSets = new ArrayList<>();
        int colorIndex = 0;
        for (Map.Entry<String, Map<Integer, Float>> entry : perCatPerDay.entrySet()) {
            Map<Integer, Float> dayMap = entry.getValue();
            List<Entry> points = new ArrayList<>();
            float running = 0f;
            for (int day = 1; day <= today; day++) {
                running += dayMap.getOrDefault(day, 0f);
                points.add(new Entry(day, running));
            }
            LineDataSet set = new LineDataSet(points, entry.getKey());
            int color = categoryColors[colorIndex % categoryColors.length];
            set.setColor(color);
            set.setCircleColor(color);
            set.setLineWidth(2f);
            set.setCircleRadius(2.5f);
            set.setDrawValues(false);
            dataSets.add(set);
            colorIndex++;
        }

        chart.setData(new LineData(dataSets));
        chart.animateX(500);
        chart.invalidate();
    }

    private void buildTopCategory(List<float[]> rows, List<String> cats, boolean isExpenses) {
        Map<String, Float> perCat = new HashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            String cat = cats.get(i);
            perCat.put(cat, perCat.getOrDefault(cat, 0f) + rows.get(i)[1]);
        }
        String best = "-";
        float bestVal = 0f;
        for (Map.Entry<String, Float> e : perCat.entrySet()) {
            if (e.getValue() > bestVal) { bestVal = e.getValue(); best = e.getKey(); }
        }
        tvTopLabel.setText(isExpenses ? "הקטגוריה הגבוהה החודש" : "מקור ההכנסה הגבוה");
        tvTopCategory.setText(best);
        tvTopAmount.setText(String.format(Locale.getDefault(), "₪%.2f", bestVal));
    }

    private void buildBalance(List<float[]> rows, boolean isExpenses) {
        float total = 0f;
        for (float[] r : rows) total += r[1];
        if (isExpenses) {
            tvBalanceLeft.setText(String.format(Locale.getDefault(), "₪%.2f", monthlyBudget - total));
        } else {
            tvBalanceLeft.setText(String.format(Locale.getDefault(), "₪%.2f", total));
        }
    }
}