package com.example.cashkal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cashkal.model.Expense;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// insights screen: spending line for the current month
public class InsightsFragment extends Fragment {

    private LineChart chart;
    private TextView topCat;
    private TextView topAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_insights, container, false);
        chart = root.findViewById(R.id.line_chart);
        topCat = root.findViewById(R.id.tv_top_category);
        topAmount = root.findViewById(R.id.tv_top_category_amount);
        setupChartAxes();
        loadExpenses();
        return root;
    }

    private void setupChartAxes() {
        // X axis days of the month
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

        // Y axis amount in shekels
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "₪" + (int) value;
            }
        });
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);


        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);


        chart.getLegend().setEnabled(true);
    }

    // read this month's expenses from Firestore
    private void loadExpenses() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // start-of-month timestamp
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long monthStart = cal.getTimeInMillis();

        FirebaseFirestore.getInstance()
                .collection("users").document(user.getUid())
                .collection("expenses")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Expense> monthExpenses = new ArrayList<>();
                    for (var doc : snapshot) {
                        Expense e = doc.toObject(Expense.class);
                        if (e.getDate() >= monthStart) monthExpenses.add(e);
                    }
                    buildChart(monthExpenses);
                    buildTopCategory(monthExpenses);
                });
    }

    // cumulative spending per day of the month
    private void buildChart(List<Expense> expenses) {
        // sum per day-of-month
        Map<Integer, Float> perDay = new HashMap<>();
        for (Expense e : expenses) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(e.getDate());
            int day = c.get(Calendar.DAY_OF_MONTH);
            perDay.put(day, perDay.getOrDefault(day, 0f) + (float) e.getAmount());
        }

        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        List<Entry> entries = new ArrayList<>();
        float running = 0f;
        for (int day = 1; day <= today; day++) {
            running += perDay.getOrDefault(day, 0f);
            entries.add(new Entry(day, running));
        }

        LineDataSet set = new LineDataSet(entries, "הוצאות מצטברות");
        set.setColor(Color.parseColor("#385255"));
        set.setCircleColor(Color.parseColor("#385255"));
        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setDrawFilled(true);
        set.setFillColor(Color.parseColor("#9DC1BF"));

        chart.setData(new LineData(set));
        chart.animateX(600);
        chart.invalidate();
    }

    // find the category with the highest total spend
    private void buildTopCategory(List<Expense> expenses) {
        Map<String, Float> perCat = new HashMap<>();
        for (Expense e : expenses) {
            String cat = e.getCategory() != null ? e.getCategory() : "אחר";
            perCat.put(cat, perCat.getOrDefault(cat, 0f) + (float) e.getAmount());
        }

        String bestCat = "-";
        float best = 0f;
        for (Map.Entry<String, Float> entry : perCat.entrySet()) {
            if (entry.getValue() > best) {
                best = entry.getValue();
                bestCat = entry.getKey();
            }
        }

        topCat.setText(bestCat);
        topAmount.setText(String.format(Locale.getDefault(), "₪%.2f", best));
    }
}