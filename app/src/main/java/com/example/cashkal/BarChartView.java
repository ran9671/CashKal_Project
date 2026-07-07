package com.example.cashkal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

// simple custom bar chart: one bar per day, height scaled to the max value
public class BarChartView extends View {

    private final List<Float> values = new ArrayList<>();
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint.setColor(Color.parseColor("#385255")); // primary
        axisPaint.setColor(Color.parseColor("#9DC1BF")); // aqua
        axisPaint.setStrokeWidth(2f);
    }

    // give the chart a new set of daily totals and redraw
    public void setValues(List<Float> newValues) {
        values.clear();
        if (newValues != null) values.addAll(newValues);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();
        float bottom = h - 20f; // leave room for the baseline

        // baseline
        canvas.drawLine(0, bottom, w, bottom, axisPaint);

        if (values.isEmpty()) return;

        // find the largest value to scale bar heights
        float max = 0f;
        for (float v : values) if (v > max) max = v;
        if (max <= 0f) return;

        int n = values.size();
        float slot = (float) w / n;        // width per day
        float barWidth = slot * 0.6f;      // bar takes 60% of its slot
        float gap = (slot - barWidth) / 2f;

        for (int i = 0; i < n; i++) {
            float value = values.get(i);
            float barHeight = (value / max) * (bottom - 10f);
            float left = i * slot + gap;
            float top = bottom - barHeight;
            RectF rect = new RectF(left, top, left + barWidth, bottom);
            canvas.drawRoundRect(rect, 6f, 6f, barPaint);
        }
    }
}